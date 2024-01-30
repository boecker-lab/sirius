/*
 *  This file is part of the SIRIUS Software for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer, Marvin Meusel and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schiller University.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License along with SIRIUS.  If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
 */

package de.unijena.bioinf.projectspace.summaries.mztab;

import de.isas.mztab2.io.MZTabParameter;
import de.isas.mztab2.io.SiriusMZTabParameter;
import de.isas.mztab2.io.SiriusWorkspaceMzTabNonValidatingWriter;
import de.isas.mztab2.io.SiriusWorkspaceMzTabValidatingWriter;
import de.isas.mztab2.model.*;
import de.unijena.bioinf.ChemistryBase.algorithm.scoring.FormulaScore;
import de.unijena.bioinf.ChemistryBase.algorithm.scoring.SScored;
import de.unijena.bioinf.ChemistryBase.algorithm.scoring.Scored;
import de.unijena.bioinf.ChemistryBase.chem.RetentionTime;
import de.unijena.bioinf.ChemistryBase.ms.AdditionalFields;
import de.unijena.bioinf.ChemistryBase.ms.Ms2Experiment;
import de.unijena.bioinf.ChemistryBase.ms.Spectrum;
import de.unijena.bioinf.ChemistryBase.ms.ft.FTree;
import de.unijena.bioinf.ChemistryBase.ms.ft.TreeStatistics;
import de.unijena.bioinf.ChemistryBase.ms.utils.AnnotatedSpectrum;
import de.unijena.bioinf.ChemistryBase.ms.utils.SpectrumWithAdditionalFields;
import de.unijena.bioinf.GibbsSampling.ZodiacScore;
import de.unijena.bioinf.chemdb.CompoundCandidate;
import de.unijena.bioinf.chemdb.DataSource;
import de.unijena.bioinf.fingerid.ConfidenceScore;
import de.unijena.bioinf.fingerid.blast.FBCandidates;
import de.unijena.bioinf.fingerid.blast.TopCSIScore;
import de.unijena.bioinf.ms.annotations.DataAnnotation;
import de.unijena.bioinf.projectspace.FormulaScoring;
import de.unijena.bioinf.projectspace.ProjectWriter;
import de.unijena.bioinf.projectspace.Summarizer;
import de.unijena.bioinf.projectspace.fingerid.FingerIdLocations;
import de.unijena.bioinf.projectspace.CompoundContainer;
import de.unijena.bioinf.projectspace.FormulaResult;
import de.unijena.bioinf.projectspace.summaries.SummaryLocations;
import de.unijena.bioinf.sirius.scores.IsotopeScore;
import de.unijena.bioinf.sirius.scores.SiriusScore;
import de.unijena.bioinf.sirius.scores.TreeScore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab2.model.MZTabConstants;
import uk.ac.ebi.pride.jmztab2.model.MZTabUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static de.unijena.bioinf.projectspace.SiriusLocations.SPECTRA;
import static de.unijena.bioinf.projectspace.SiriusLocations.TREES;
import static de.unijena.bioinf.projectspace.summaries.mztab.JenaMSAdditionalKeys.*;

public class MztabMExporter implements Summarizer {
    private int smlID = 0;
    private int smfID = 0;
    private int smeID = 0;
    private final MzTab mztab;

    private boolean fingerID = false;

    private final Map<String, MsRun> pathToRun = new HashMap<>();


    public MztabMExporter() {
        mztab = new MzTab();
        mztab.setMetadata(
                buildMTDBlock()
        );

        UUID id = UUID.randomUUID();
        setID("SIRIUS-" + id.toString());
        setTitle("SIRIUS Analysis Report: " + id.toString());
    }

    public MztabMExporter(String title, String id) {
        this();
        setTitle(title);
        setID(id);
    }


    public static void write(final Writer writer, final MzTab mztab) throws IOException {
        write(writer, mztab, false);
    }

    public static void write(final Writer writer, final MzTab mztab, final boolean validate) throws IOException {
        if (validate)
            new SiriusWorkspaceMzTabValidatingWriter().write(writer, mztab);
        else
            new SiriusWorkspaceMzTabNonValidatingWriter().write(writer, mztab);
    }

    public void write(final Writer writer) throws IOException {
        write(writer, false);
    }

    public void write(final Writer writer, final boolean validate) throws IOException {
        write(writer, mztab, validate);
    }

    @Override
    public synchronized void addWriteCompoundSummary(ProjectWriter writer, @NotNull CompoundContainer c, List<? extends SScored<FormulaResult, ? extends FormulaScore>> results) throws IOException {
        final @NotNull Ms2Experiment exp = c.getAnnotationOrThrow(Ms2Experiment.class);
        if (results != null && !results.isEmpty()) {
            FormulaResult bestHitSource = results.get(0).getCandidate();
            final FormulaScoring bestHitScores = bestHitSource.getAnnotationOrThrow(FormulaScoring.class);

            // check if fingerid results are available
            Scored<CompoundCandidate> bestHit = null;
            int bestHitSourceRank = 1;
            if (bestHitScores.hasAnnotation(TopCSIScore.class)) {
                bestHitSource = results.stream()
                        .filter(s -> s.getCandidate().getAnnotationOrThrow(FormulaScoring.class).hasAnnotation(TopCSIScore.class))
                        .map(s -> new SScored<>(s.getCandidate(), s.getCandidate().getAnnotationOrThrow(FormulaScoring.class).getAnnotationOrThrow(TopCSIScore.class)))
                        .sorted().findFirst().map(SScored::getCandidate).orElseThrow();

                final FormulaResult finalBestHitSource = bestHitSource;
                bestHit = finalBestHitSource.getAnnotation(FBCandidates.class).map(can -> can.getResults().get(0)).orElseGet(
                        () -> {
                            LoggerFactory.getLogger(getClass()).warn("Best CSI score found but no corresponding hit! " + finalBestHitSource.getId().toString());
                            return null;
                        }
                );

                //rerank by structure hit
                results.stream().map(SScored::getCandidate).collect(Collectors.toList()).indexOf(bestHitSource);
            }

            final SmallMoleculeSummary smlItem = buildSMLItem(exp, bestHitSource, bestHit);
            mztab.addSmallMoleculeSummaryItem(smlItem);

            final SmallMoleculeFeature smfItem = buildSMFItem(exp, bestHitSource, smlItem);
            mztab.addSmallMoleculeFeatureItem(smfItem);

            final List<SpectraRef> spectraRefs = extractReferencesAndRuns(exp);

            final SmallMoleculeEvidence smeSiriusItem = buildSiriusFormulaIDSMEItem(exp, bestHitSource, bestHitSourceRank, smfItem);
            smeSiriusItem.setSpectraRef(spectraRefs);
            mztab.addSmallMoleculeEvidenceItem(smeSiriusItem);


            if (bestHit != null) {
                final SmallMoleculeEvidence smeFingerIDItem = buildFingerIDSMEItem(exp, bestHitSource, bestHit, smfItem);
                smeFingerIDItem.setSpectraRef(spectraRefs);
                mztab.addSmallMoleculeEvidenceItem(smeFingerIDItem);
                smlItem.setReliability("2");

                smlItem.setBestIdConfidenceMeasure(SiriusMZTabParameter.CSI_FINGERID_CONFIDENCE_SCORE);
                smlItem.setBestIdConfidenceValue(smeFingerIDItem.getIdConfidenceMeasure().get(0));


                List<String> ids = bestHit.getCandidate().getLinks().stream()
                        .filter(dbLink -> dbLink.name.equals(DataSource.PUBCHEM.realName)).map(dbLink -> dbLink.id).collect(Collectors.toList());

                smlItem.setDatabaseIdentifier(
                        ids.stream().map(dbLink -> "CID:" + dbLink)
                                .collect(Collectors.toList())
                );

                smlItem.setUri(
                        ids.stream().map(DataSource.PUBCHEM::getLink)
                                .collect(Collectors.toList())
                );

                if (!fingerID) {
                    fingerID = true;
                    mztab.getMetadata().addSoftwareItem(new Software().id(2)
                            .parameter(SiriusMZTabParameter.SOFTWARE_FINGER_ID)
                    );

                    mztab.getMetadata().addIdConfidenceMeasureItem(SiriusMZTabParameter.CSI_FINGERID_CONFIDENCE_SCORE);
                }
            } else {
                smlItem.setReliability("4");
            }
            //todo add zodiac spectral library hits at some time
//        final SmallMoleculeEvidence smeSpectralHitItem = buildSpectralLibSMEItem(bestHitSource, bestHit, smfItem);
//        mztab.addSmallMoleculeEvidenceItem(smeSpectralHitItem);
            mztab.getMetadata().setMsRun(new ArrayList<>(pathToRun.values()));
        }
    }

    @Override
    public synchronized void writeProjectSpaceSummary(ProjectWriter writer) throws IOException {
        writer.textFile(SummaryLocations.MZTAB_SUMMARY, this::write);
    }

    @Override
    public List<Class<? extends DataAnnotation>> requiredFormulaResultAnnotations() {
        return Arrays.asList(FormulaScoring.class, FTree.class, FBCandidates.class);
    }



    private SmallMoleculeEvidence buildSiriusSMEItem(@NotNull final Ms2Experiment er, @NotNull final FormulaResult bestHitSource, @NotNull final SmallMoleculeFeature smfItem) {
        SmallMoleculeEvidence smeItem = buildSMEItem(smfItem);

        smeItem.setMsLevel(MZTabParameter.newInstance(MZTabParameter.MS_LEVEL).value("2"));
        smeItem.setCharge(bestHitSource.getId().getIonType().getCharge());
        smeItem.setAdductIon(bestHitSource.getId().getIonType().toString());
        smeItem.setChemicalFormula(bestHitSource.getId().getMolecularFormula().toString());
        smeItem.setTheoreticalMassToCharge(bestHitSource.getId().getIonType().addIonAndAdduct(bestHitSource.getId().getMolecularFormula().getMass()));
        smeItem.setExpMassToCharge(er.getIonMass());

        return smeItem;
    }

    private SmallMoleculeEvidence buildSiriusFormulaIDSMEItem(@NotNull final Ms2Experiment er, @NotNull final FormulaResult bestHitSource, final int bestHitSourceRank, @NotNull final SmallMoleculeFeature smfItem) {
        SmallMoleculeEvidence smeItem = buildSiriusSMEItem(er, bestHitSource, smfItem);

        smeItem.setIdentificationMethod(SiriusMZTabParameter.SOFTWARE_SIRIUS);
        smeItem.setRank(bestHitSourceRank);
        smeItem.setEvidenceInputId(makeMassIdentifier(er, bestHitSource));

        @NotNull final FormulaScoring scoring = bestHitSource.getAnnotationOrThrow(FormulaScoring.class);
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_SCORE, scoring.getAnnotation(SiriusScore.class).map(String::valueOf).orElse(FormulaScore.NA())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_ISOTOPE_SCORE, scoring.getAnnotation(IsotopeScore.class).map(String::valueOf).orElse(FormulaScore.NA())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_TREE_SCORE, scoring.getAnnotation(TreeScore.class).map(String::valueOf).orElse(FormulaScore.NA())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.ZODIAC_SCORE, scoring.getAnnotation(ZodiacScore.class).map(String::valueOf).orElse(FormulaScore.NA())));

        @NotNull final FTree tree = bestHitSource.getAnnotationOrThrow(FTree.class);
        @NotNull final TreeStatistics treeStats = tree.getAnnotationOrThrow(TreeStatistics.class);
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_EXPL_INTENSITY_OF_TOTAL_INTENSITY, String.valueOf(treeStats.getExplainedIntensity())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_EXPL_INTENSITY_OF_EXPLAINABLE_INTENSITY, String.valueOf(treeStats.getExplainedIntensityOfExplainablePeaks())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_NUM_EXPL_PEAKS_RATIO, String.valueOf(treeStats.getRatioOfExplainedPeaks())));

        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_ANNOTATED_SPECTRA_LOCATION, SPECTRA.relFilePath(bestHitSource.getId())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_TREE_LOCATION, TREES.relFilePath(bestHitSource.getId())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.SIRIUS_SUMMARY_LOCATION, bestHitSource.getId().getParentId().getDirectoryName() + "/" + SummaryLocations.FORMULA_CANDIDATES));

        return smeItem;
    }

    private SmallMoleculeEvidence buildFingerIDSMEItem(@NotNull final Ms2Experiment er, @NotNull final FormulaResult bestHitSource, @NotNull final Scored<? extends CompoundCandidate> bestHit, @NotNull final SmallMoleculeFeature smfItem) {
        SmallMoleculeEvidence smeItem = buildSiriusSMEItem(er, bestHitSource, smfItem);
        smeItem.setIdentificationMethod(SiriusMZTabParameter.SOFTWARE_FINGER_ID);
        smeItem.setRank(1); //todo make exported result user definable in gui
        smeItem.setEvidenceInputId(makeFormulaIdentifier(er, bestHitSource));

        smeItem.setChemicalName(bestHit.getCandidate().getName());
        smeItem.setInchi(bestHit.getCandidate().getInchi().in2D);
        smeItem.setSmiles(bestHit.getCandidate().getSmiles());

        @NotNull final FormulaScoring scoring = bestHitSource.getAnnotationOrThrow(FormulaScoring.class);
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.FINGERID_SCORE, scoring.getAnnotation(TopCSIScore.class).map(String::valueOf).orElse(FormulaScore.NA())));
//        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.FINGERID_TANIMOTO_SIMILARITY, bestHit.getCandidate().));

        final ConfidenceScore c = scoring.getAnnotationOr(ConfidenceScore.class, FormulaScore::NA);
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.FINGERID_CONFIDENCE, c.toString()));
        smeItem.addIdConfidenceMeasureItem(c.score());

        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.FINGERID_FINGERPRINT_LOCATION, FingerIdLocations.FINGERPRINTS.relFilePath(bestHitSource.getId())));
        smeItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.FINGERID_CANDIDATE_LOCATION, FingerIdLocations.FINGERBLAST.relFilePath(bestHitSource.getId())));


        return smeItem;
    }

    private SmallMoleculeEvidence buildSpectralLibSMEItem(@NotNull Ms2Experiment er, final FormulaResult bestHitSource, final Scored<CompoundCandidate> bestHit, final SmallMoleculeFeature smfItem) {
        SmallMoleculeEvidence smeItem = buildSMEItem(smfItem);
        //todo implement if available through zodiac?
        return smeItem;
    }

    private SmallMoleculeEvidence buildSMEItem(final SmallMoleculeFeature smfItem) {
        SmallMoleculeEvidence smeItem = new SmallMoleculeEvidence();
        smeItem.setSmeId(++smeID);
        smfItem.addSmeIdRefsItem(smeItem.getSmeId());
//        smeItem.setEvidenceInputId(); //todo maybe we can use this for openms mapping stuff
        return smeItem;
    }

    private SmallMoleculeFeature buildSMFItem(@NotNull Ms2Experiment er, @NotNull final FormulaResult bestHitSource, @NotNull final SmallMoleculeSummary smlItem) {
        final SmallMoleculeFeature smfItem = new SmallMoleculeFeature();
        smfItem.setSmfId(++smfID);
        smfItem.smeIdRefAmbiguityCode(2); //todo 3 is needed if we also want to add multiple candidates
        smlItem.addSmfIdRefsItem(smfItem.getSmfId());

        smfItem.setAdductIon(bestHitSource.getId().getIonType().toString());
        smfItem.setCharge(bestHitSource.getId().getIonType().getCharge());
        smfItem.setExpMassToCharge(er.getIonMass());

        //add retention time if available
        er.getAnnotation(RetentionTime.class).ifPresent(rt -> {
            smfItem.setRetentionTimeInSeconds(rt.getRetentionTimeInSeconds());
            if (rt.isInterval()){
                if (!Double.isNaN(rt.getStartTime()))
                    smfItem.setRetentionTimeInSecondsStart(rt.getStartTime());
                if (!Double.isNaN(rt.getEndTime()))
                    smfItem.setRetentionTimeInSecondsEnd(rt.getEndTime());
            }
        });

        return smfItem;
    }

    public void setTitle(String title) {
        mztab.getMetadata().setTitle(title);

    }

    public void setID(String ID) {
        mztab.getMetadata().setMzTabID(ID); //todo add workspace file parameterName here
    }

    private SmallMoleculeSummary buildSMLItem(@NotNull Ms2Experiment er, @NotNull FormulaResult bestHitSource, @Nullable Scored<? extends CompoundCandidate> bestHit) {
        final SmallMoleculeSummary smlItem = new SmallMoleculeSummary();
        smlItem.setSmlId(++smlID);
        smlItem.adductIons(Collections.singletonList(bestHitSource.getId().getIonType().toString()));
        smlItem.addChemicalFormulaItem(bestHitSource.getId().getMolecularFormula().toString());
        smlItem.addTheoreticalNeutralMassItem(bestHitSource.getId().getMolecularFormula().getMass());

        if (bestHit != null) {
            smlItem.addChemicalNameItem(bestHit.getCandidate().getName());
            smlItem.addInchiItem(bestHit.getCandidate().getInchi().in2D);
            smlItem.addSmilesItem(bestHit.getCandidate().getSmiles());
        }

        er.getAnnotation(AdditionalFields.class).
                ifPresent(fields -> {
                    if (fields.containsKey(FEATURE_ID)) {
                        smlItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.OPENMS_FEATURE_ID, fields.get(FEATURE_ID)));
                    }
                    if (fields.containsKey(CONSENSUS_ID)) {
                        smlItem.addOptItem(SiriusMZTabParameter.newOptColumn(SiriusMZTabParameter.OPENMS_CONSENSUS_ID, fields.get(CONSENSUS_ID)));
                    }
                });

        return smlItem;
    }

    private static Metadata buildMTDBlock() {
        Metadata mtd = new Metadata();
        mtd.mzTabVersion(MZTabConstants.VERSION_MZTAB_M); //this is the format not the library version
        mtd.addCvItem(SiriusMZTabParameter.DEFAULT_CV);
        mtd.setSmallMoleculeIdentificationReliability(SiriusMZTabParameter.SMALL_MOLECULE_IDENTIFICATION_RELIABILITY);


        mtd.addSoftwareItem(new Software().id(1)
                .parameter(SiriusMZTabParameter.SOFTWARE_SIRIUS)
        );

        mtd.addDatabaseItem(SiriusMZTabParameter.NO_DATABASE.id(1));
//        mtd.addDatabaseItem(SiriusMZTabParameter.DE_NOVO);
        mtd.addDatabaseItem(SiriusMZTabParameter.PUBCHEM.id(2));


        return mtd;
    }

    public List<SpectraRef> extractReferencesAndRuns(@NotNull Ms2Experiment exp) {
        List<Spectrum<?>> specs = new ArrayList<>(exp.getMs2Spectra().size() + exp.getMs1Spectra().size() + 1);
        specs.add(exp.getMergedMs1Spectrum());
        specs.addAll(exp.getMs1Spectra());
        specs.addAll(exp.getMs2Spectra());

        final AdditionalFields global = exp.getAnnotation(AdditionalFields.class).orElse(new AdditionalFields());

        final String globalSource = global.getOrDefault(SOURCE_FILE, Optional.ofNullable(exp.getSourceString()).orElse(null));

        return specs.stream().map((it) -> {
            if (it instanceof AnnotatedSpectrum)
                return (AdditionalFields) ((AnnotatedSpectrum) it).getAnnotationOrNull(AdditionalFields.class);
            else if (it instanceof SpectrumWithAdditionalFields<?>)
                return ((SpectrumWithAdditionalFields<?>) it).additionalFields();

            return null;
        }).filter(Objects::nonNull).map((it) -> {
            SpectraRef ref = new SpectraRef();
            String specref = it.get(SPECTRUM_ID);
            Integer runID = null;
//            String scanNumber =  it.get(SCAN_NUMBER);
//            if (scanNumber != null)


            if (specref == null)
                return null;

            if (specref.startsWith("ms_run[") && specref.contains("]:")) { //todo pattern matcher
                final String[] s = specref.split(":", 2);
                specref = s[1];
                try {
                    runID = Integer.parseInt(s[0].substring(s[0].indexOf('[') + 1, s[0].indexOf(']')));
                } catch (NumberFormatException e) {
                    runID = null;
                }
            }
            ref.setReference(specref);

            String source = it.getOrDefault(SOURCE_FILE, globalSource);
            if (source == null)
                return null;

            MsRun run = pathToRun.get(source);
            if (run == null) {
                run = new MsRun()
                        .id(runID != null ? runID : pathToRun.size() + 1)
                        .location(source);
                pathToRun.put(source, run);
            }

            if (run.getFormat() == null && it.containsKey(SOURCE_FILE_FORMAT))
                run.setFormat(MZTabUtils.parseParam(it.get(SOURCE_FILE_FORMAT)));
            if (run.getIdFormat() == null && it.containsKey(SPECTRUM_ID_FORMAT))
                run.setIdFormat(MZTabUtils.parseParam(it.get(SPECTRUM_ID_FORMAT)));

            final Parameter polarity = SiriusMZTabParameter.getScanPolarity(exp.getPrecursorIonType());
            if (polarity != null && (run.getScanPolarity() == null || !run.getScanPolarity().contains(polarity)))
                run.addScanPolarityItem(polarity);

            ref.setMsRun(run);


            return ref;
        }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    protected static String makeFormulaIdentifier(Ms2Experiment ex, FormulaResult result) {
        return result.getId().getParentId().getDirectoryName() + ":" + result.getId().getMolecularFormula() + ":" + result.getId().getIonType().toString().replaceAll("[\\[\\] _]", "");
    }

    protected String makeMassIdentifier(Ms2Experiment ex, FormulaResult result) {
        try {
            return result.getId().getParentId().getDirectoryName() + ":" + ex.getIonMass() + ":" + result.getId().getIonType().withoutAdduct().toString().replaceAll("[\\[\\] _]", "");
        } catch (Exception e) {
//            System.out.println("Instance was not written?????????? -> " + ex.getName());
            throw e;
        }
    }
}

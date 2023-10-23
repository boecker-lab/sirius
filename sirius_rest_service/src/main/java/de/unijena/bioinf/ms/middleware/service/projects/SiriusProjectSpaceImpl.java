/*
 *
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schiller University.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with SIRIUS. If not, see <https://www.gnu.org/licenses/lgpl-3.0.txt>
 */

package de.unijena.bioinf.ms.middleware.service.projects;

import de.unijena.bioinf.ChemistryBase.algorithm.scoring.FormulaScore;
import de.unijena.bioinf.ChemistryBase.algorithm.scoring.SScored;
import de.unijena.bioinf.ChemistryBase.algorithm.scoring.Scored;
import de.unijena.bioinf.ChemistryBase.fp.Fingerprint;
import de.unijena.bioinf.ChemistryBase.ms.*;
import de.unijena.bioinf.ChemistryBase.ms.ft.FTree;
import de.unijena.bioinf.ChemistryBase.ms.lcms.CoelutingTraceSet;
import de.unijena.bioinf.ChemistryBase.ms.lcms.LCMSPeakInformation;
import de.unijena.bioinf.canopus.CanopusResult;
import de.unijena.bioinf.chemdb.CompoundCandidate;
import de.unijena.bioinf.fingerid.FingerprintResult;
import de.unijena.bioinf.fingerid.blast.FBCandidateFingerprints;
import de.unijena.bioinf.fingerid.blast.FBCandidates;
import de.unijena.bioinf.fingerid.blast.TopCSIScore;
import de.unijena.bioinf.lcms.LCMSCompoundSummary;
import de.unijena.bioinf.ms.annotations.DataAnnotation;
import de.unijena.bioinf.ms.frontend.core.ApplicationCore;
import de.unijena.bioinf.ms.middleware.controller.AlignedFeaturesController;
import de.unijena.bioinf.ms.middleware.model.features.AlignedFeature;
import de.unijena.bioinf.ms.middleware.model.features.LCMSFeatureQuality;
import de.unijena.bioinf.ms.middleware.model.features.MsData;
import de.unijena.bioinf.ms.middleware.model.features.annotations.*;
import de.unijena.bioinf.ms.middleware.model.spectra.AnnotatedSpectrum;
import de.unijena.bioinf.projectspace.*;
import de.unijena.bioinf.projectspace.fingerid.FBCandidateNumber;
import de.unijena.bioinf.sirius.Sirius;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SiriusProjectSpaceImpl implements Project {


    @NotNull
    private final ProjectSpaceManager<?> projectSpaceManager;

    public SiriusProjectSpaceImpl(@NotNull ProjectSpaceManager<?> projectSpaceManager) {
        this.projectSpaceManager = projectSpaceManager;
    }

    public @NotNull ProjectSpaceManager<?> getProjectSpaceManager() {
        return projectSpaceManager;
    }

    @Override
    public Page<AlignedFeature> findAlignedFeatures(Pageable pageable, EnumSet<AlignedFeature.OptFields> optFields) {
        LoggerFactory.getLogger(AlignedFeaturesController.class).info("Started collecting aligned features...");
        final List<AlignedFeature> alignedFeatures = projectSpaceManager.projectSpace().stream()
                .skip(pageable.getOffset()).limit(pageable.getPageSize())
                .map(ccid -> asCompoundId(ccid, optFields))
                .toList();
        LoggerFactory.getLogger(AlignedFeaturesController.class).info("Finished parsing aligned features...");

        return new PageImpl<>(alignedFeatures, pageable, projectSpaceManager.size());
    }

    @Override
    public AlignedFeature findAlignedFeaturesById(String alignFeatureId, EnumSet<AlignedFeature.OptFields> optFields) {
        final CompoundContainerId ccid = parseCID(alignFeatureId);
        return asCompoundId(ccid, optFields);
    }

    @Override
    public void deleteAlignedFeaturesById(String alignFeatureId) {
        CompoundContainerId compound = projectSpaceManager.projectSpace().findCompound(alignFeatureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "AlignedFeature with id '" + alignFeatureId + "' does not exist."));
        try {
            projectSpaceManager.projectSpace().deleteCompound(compound);
        } catch (IOException e) {
            log.error("Error when deleting feature with Id " + alignFeatureId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when deleting feature with Id " + alignFeatureId);
        }
    }

    @Override
    public Page<FormulaCandidate> findFormulaCandidatesByFeatureId(String alignFeatureId, Pageable pageable, EnumSet<FormulaCandidate.OptFields> optFields) {
        LoggerFactory.getLogger(getClass()).info("Started collecting formulas...");
        Class<? extends DataAnnotation>[] annotations = resolveFormulaCandidateAnnotations(optFields);
        Instance instance = loadInstance(alignFeatureId);
        Stream<FormulaResult> paged;
        int size;
        {
            List<? extends SScored<FormulaResult, ? extends FormulaScore>> tmpSource = instance.loadFormulaResults();
            paged = tmpSource.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(SScored::getCandidate);
            size = tmpSource.size();
        }

        return new PageImpl<>(
                paged.peek(fr -> instance.loadFormulaResult(fr.getId(), annotations).ifPresent(fr::setAnnotationsFrom))
                        .map(res -> makeFormulaCandidate(instance, res, optFields)).toList(), pageable, size);
    }

    @Override
    public FormulaCandidate findFormulaCandidateByFeatureIdAndId(String formulaId, String alignFeatureId, EnumSet<FormulaCandidate.OptFields> optFields) {
        Class<? extends DataAnnotation>[] annotations = resolveFormulaCandidateAnnotations(optFields);
        Instance instance = loadInstance(alignFeatureId);
        return instance.loadFormulaResult(parseFID(instance, formulaId), annotations)
                .map(res -> makeFormulaCandidate(instance, res, optFields)).orElse(null);
    }

    @Override

    public Page<StructureCandidate> findStructureCandidatesByFeatureIdAndFormulaId(String formulaId, String alignFeatureId, Pageable pageable, EnumSet<StructureCandidate.OptFields> optFields) {
        long topK = pageable.getOffset() + pageable.getPageSize();
        List<Class<? extends DataAnnotation>> para = (optFields.contains(StructureCandidate.OptFields.fingerprint)
                ? List.of(FormulaScoring.class, FBCandidates.class, FBCandidateFingerprints.class)
                : List.of(FormulaScoring.class, FBCandidates.class));

        Instance instance = loadInstance(alignFeatureId);
        FormulaResultId fidObj = parseFID(instance, formulaId);
        fidObj.setAnnotation(FBCandidateNumber.class, topK <= 0 ? FBCandidateNumber.ALL : new FBCandidateNumber((int) topK));
        FormulaResult fr = instance.loadFormulaResult(fidObj, (Class<? extends DataAnnotation>[]) para.toArray(Class[]::new)).orElseThrow();
        return fr.getAnnotation(FBCandidates.class).map(FBCandidates::getResults).map(l -> {
                    List<StructureCandidate> candidates = new ArrayList();

                    Iterator<Scored<CompoundCandidate>> it =
                            l.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).iterator();

                    if (optFields.contains(StructureCandidate.OptFields.fingerprint)) {
                        Iterator<Fingerprint> fps = fr.getAnnotationOrThrow(FBCandidateFingerprints.class).getFingerprints()
                                .stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).iterator();

                        if (it.hasNext())//tophit
                            candidates.add(StructureCandidate.of(it.next(), fps.next(),
                                    fr.getAnnotationOrNull(FormulaScoring.class), optFields));

                        while (it.hasNext())
                            candidates.add(StructureCandidate.of(it.next(), fps.next(),
                                    null, optFields));
                    } else {
                        if (it.hasNext())//tophit
                            candidates.add(StructureCandidate.of(it.next(), null,
                                    fr.getAnnotationOrNull(FormulaScoring.class), optFields));

                        while (it.hasNext())
                            candidates.add(StructureCandidate.of(it.next(), null,
                                    null, optFields));
                    }
                    return candidates;
                }).map(it -> (Page<StructureCandidate>) new PageImpl<>(it))
                .orElse(Page.empty(pageable)); //todo number of candidates for page.
    }

    @Override
    public StructureCandidate findTopStructureCandidatesByFeatureId(String alignFeatureId, EnumSet<StructureCandidate.OptFields> optFields) {
        boolean fingerprint = optFields.contains(StructureCandidate.OptFields.fingerprint);
        List<Class<? extends DataAnnotation>> para = (fingerprint ? List.of(FormulaScoring.class, FBCandidates.class, FBCandidateFingerprints.class) : List.of(FormulaScoring.class, FBCandidates.class));
        Instance instance = loadInstance(alignFeatureId);

        return instance.loadTopFormulaResult(List.of(TopCSIScore.class)).flatMap(fr -> {
            fr.getId().setAnnotation(FBCandidateNumber.class, new FBCandidateNumber(1));
            return instance.loadFormulaResult(fr.getId(), (Class<? extends DataAnnotation>[]) para.toArray(Class[]::new))
                    .flatMap(fr2 -> fr2.getAnnotation(FBCandidates.class).map(FBCandidates::getResults)
                            .filter(l -> !l.isEmpty()).map(r -> r.get(0))
                            .map(sc -> StructureCandidate.of(sc,
                                    fr2.getAnnotation(FBCandidateFingerprints.class)
                                            .map(FBCandidateFingerprints::getFingerprints)
                                            .map(fps -> fps.isEmpty() ? null : fps.get(0))
                                            .orElse(null),
                                    fr.getAnnotationOrThrow(FormulaScoring.class), optFields))
                    );
        }).orElseThrow();
    }

    private AlignedFeature asCompoundId(CompoundContainerId cid, EnumSet<AlignedFeature.OptFields> optFields) {
        final AlignedFeature alignedFeature = AlignedFeature.of(cid);
        if (!optFields.isEmpty()) {
            Instance instance = projectSpaceManager.getInstanceFromCompound(cid);
            if (optFields.contains(AlignedFeature.OptFields.topAnnotations))
                alignedFeature.setTopAnnotations(asCompoundSummary(instance));
            if (optFields.contains(AlignedFeature.OptFields.msData))
                alignedFeature.setMsData(asCompoundMsData(instance));
            if (optFields.contains(AlignedFeature.OptFields.lcmsFeatureQuality))
                alignedFeature.setLcmsFeatureQuality(asCompoundLCMSFeatureQuality(instance));
            if (optFields.contains(AlignedFeature.OptFields.qualityFlags))
                alignedFeature.setQualityFlags(asCompoundQualityData(instance));
        }
        return alignedFeature;
    }


    protected Instance loadInstance(String cid) {
        return projectSpaceManager.getInstanceFromCompound(parseCID(cid));
    }

    protected CompoundContainerId parseCID(String cid) {
        return projectSpaceManager.projectSpace().findCompound(cid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Compound with ID '" + cid + "' in project with name '" + projectSpaceManager.projectSpace().getLocation() + "'"));
    }

    protected FormulaResultId parseFID(String cid, String fid) {
        return parseFID(loadInstance(cid), fid);
    }

    protected FormulaResultId parseFID(Instance instance, String fid) {
        return instance.loadCompoundContainer().findResult(fid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FormulaResult with FID '" + fid + "' not found!"));

    }

    private static FormulaCandidate makeFormulaCandidate(Instance inst, FormulaResult res, EnumSet<FormulaCandidate.OptFields> optFields) {
        FormulaCandidate candidate = optFields.contains(FormulaCandidate.OptFields.statistics)
                ? FormulaCandidate.of(res)
                : FormulaCandidate.of(res.getId(), res.getAnnotationOrThrow(FormulaScoring.class));

        if (optFields.contains(FormulaCandidate.OptFields.fragmentationTree))
            res.getAnnotation(FTree.class).map(FragmentationTree::fromFtree).ifPresent(candidate::setFragmentationTree);
        if (optFields.contains(FormulaCandidate.OptFields.simulatedIsotopePattern))
            asSimulatedIsotopePattern(inst, res).ifPresent(candidate::setSimulatedIsotopePattern);
        if (optFields.contains(FormulaCandidate.OptFields.predictedFingerprint))
            res.getAnnotation(FingerprintResult.class).map(fpResult -> fpResult.fingerprint.toProbabilityArray())
                    .ifPresent(candidate::setPredictedFingerprint);
        if (optFields.contains(FormulaCandidate.OptFields.canopusPredictions))
            res.getAnnotation(CanopusResult.class).map(CanopusPrediction::of).ifPresent(candidate::setCanopusPrediction);
        if (optFields.contains(FormulaCandidate.OptFields.compoundClasses))
            res.getAnnotation(CanopusResult.class).map(CompoundClasses::of).ifPresent(candidate::setCompoundClasses);
        return candidate;
    }

    private static Class<? extends DataAnnotation>[] resolveFormulaCandidateAnnotations(EnumSet<FormulaCandidate.OptFields> optFields) {
        List<Class<? extends DataAnnotation>> classes = new ArrayList<>();
        classes.add(FormulaScoring.class);
        if (Stream.of(
                        FormulaCandidate.OptFields.statistics,
                        FormulaCandidate.OptFields.fragmentationTree,
                        FormulaCandidate.OptFields.simulatedIsotopePattern)
                .anyMatch(optFields::contains))
            classes.add(FTree.class);

        if (optFields.contains(FormulaCandidate.OptFields.predictedFingerprint))
            classes.add(FingerprintResult.class);

        if (Stream.of(FormulaCandidate.OptFields.compoundClasses, FormulaCandidate.OptFields.canopusPredictions)
                .anyMatch(optFields::contains))
            classes.add(CanopusResult.class);

        return (Class<? extends DataAnnotation>[]) classes.toArray();
    }

    private static Optional<AnnotatedSpectrum> asSimulatedIsotopePattern(Instance instance, FormulaResult fResult) {
        Sirius sirius = ApplicationCore.SIRIUS_PROVIDER.sirius(instance.loadCompoundContainer(ProjectSpaceConfig.class).getAnnotationOrThrow(ProjectSpaceConfig.class).config.getConfigValue("AlgorithmProfile"));
        return Optional.of(fResult)
                .map(FormulaResult::getId)
                .map(id -> sirius.simulateIsotopePattern(id.getMolecularFormula(), id.getIonType().getIonization()))
                .map(AnnotatedSpectrum::new);
    }

    private static Annotations asCompoundSummary(Instance inst) {
        return inst.loadTopFormulaResult(List.of(TopCSIScore.class)).map(de.unijena.bioinf.projectspace.FormulaResult::getId).flatMap(frid -> {
            frid.setAnnotation(FBCandidateNumber.class, new FBCandidateNumber(1));
            return inst.loadFormulaResult(frid, FormulaScoring.class, FTree.class, FBCandidates.class, CanopusResult.class)
                    .map(topHit -> {
                        final Annotations cSum = new Annotations();
//
                        //add formula summary
                        cSum.setFormulaAnnotation(FormulaCandidate.of(topHit));

                        // fingerid result
                        topHit.getAnnotation(FBCandidates.class).map(FBCandidates::getResults)
                                .filter(l -> !l.isEmpty()).map(r -> r.get(0)).map(s ->
                                        StructureCandidate.of(s, topHit.getAnnotationOrThrow(FormulaScoring.class),
                                                EnumSet.of(StructureCandidate.OptFields.dbLinks, StructureCandidate.OptFields.pubmedIds, StructureCandidate.OptFields.refSpectraLinks)))
                                .ifPresent(cSum::setStructureAnnotation);

                        topHit.getAnnotation(CanopusResult.class).map(CompoundClasses::of).
                                ifPresent(cSum::setCompoundClassAnnotation);
                        return cSum;

                    });
        }).orElseGet(Annotations::new);
    }

    private static MsData asCompoundMsData(Instance instance) {
        return instance.loadCompoundContainer(Ms2Experiment.class)
                .getAnnotation(Ms2Experiment.class).map(exp -> new MsData(
                        opt(exp.getMergedMs1Spectrum(), s -> {
                            AnnotatedSpectrum t = new AnnotatedSpectrum((Spectrum<Peak>) s);
                            t.setMsLevel(1);
                            return t;
                        }).orElse(null),
                        null,
                        exp.getMs1Spectra().stream().map(x -> {
                            AnnotatedSpectrum t = new AnnotatedSpectrum(x);
                            t.setMsLevel(1);
                            return t;
                        }).collect(Collectors.toList()),
                        exp.getMs2Spectra().stream().map(x -> {
                            AnnotatedSpectrum t = new AnnotatedSpectrum(x);
                            t.setCollisionEnergy(new CollisionEnergy(x.getCollisionEnergy()));
                            t.setMsLevel(2);
                            return t;
                        }).collect(Collectors.toList()))).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Feature with ID '" + instance + "' has no input Data!"));
    }

    private static EnumSet<CompoundQuality.CompoundQualityFlag> asCompoundQualityData(Instance instance) {
        return instance.loadCompoundContainer(Ms2Experiment.class)
                .getAnnotation(Ms2Experiment.class)
                .flatMap(exp -> exp.getAnnotation(CompoundQuality.class))
                .map(CompoundQuality::getFlags)
                .orElse(EnumSet.of(CompoundQuality.CompoundQualityFlag.UNKNOWN));
    }

    private static LCMSFeatureQuality asCompoundLCMSFeatureQuality(Instance instance) {
        final LCMSPeakInformation peakInformation = instance.loadCompoundContainer(LCMSPeakInformation.class).getAnnotation(LCMSPeakInformation.class, LCMSPeakInformation::empty);
        Ms2Experiment experiment = instance.getExperiment();
        Optional<CoelutingTraceSet> traceSet = peakInformation.getTracesFor(0);
        if (traceSet.isPresent()) {
            final LCMSCompoundSummary summary = new LCMSCompoundSummary(traceSet.get(), traceSet.get().getIonTrace(), experiment);
            return new LCMSFeatureQuality(summary);
        } else {
            //todo is this allowed???
            return null;
        }
    }

    private static  <S, T> Optional<T> opt(S input, Function<S, T> convert) {
        return Optional.ofNullable(input).map(convert);
    }
}
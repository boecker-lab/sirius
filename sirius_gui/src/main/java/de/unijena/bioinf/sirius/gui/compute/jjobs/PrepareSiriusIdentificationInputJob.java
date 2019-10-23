package de.unijena.bioinf.sirius.gui.compute.jjobs;

import de.unijena.bioinf.ChemistryBase.chem.Element;
import de.unijena.bioinf.ChemistryBase.chem.FormulaConstraints;
import de.unijena.bioinf.ChemistryBase.chem.PeriodicTable;
import de.unijena.bioinf.ChemistryBase.chem.PrecursorIonType;
import de.unijena.bioinf.ChemistryBase.ms.*;
import de.unijena.bioinf.ChemistryBase.properties.PropertyManager;
import de.unijena.bioinf.FragmentationTreeConstruction.model.Whiteset;
import de.unijena.bioinf.IsotopePatternAnalysis.prediction.ElementPredictor;
import de.unijena.bioinf.fingerid.db.SearchableDatabase;
import de.unijena.bioinf.jjobs.BasicJJob;
import de.unijena.bioinf.jjobs.SwingJJobContainer;
import de.unijena.bioinf.sirius.IsotopePatternHandling;
import de.unijena.bioinf.sirius.Sirius;
import de.unijena.bioinf.sirius.gui.compute.FormulaWhiteListJob;
import de.unijena.bioinf.sirius.gui.compute.SearchProfilePanel;
import de.unijena.bioinf.sirius.gui.structure.ExperimentContainer;
import de.unijena.bioinf.sirius.logging.TextAreaJJobContainer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PrepareSiriusIdentificationInputJob extends BasicJJob<MutableMs2Experiment> implements GuiObservableJJob<MutableMs2Experiment> {
    final Sirius sirius;
    final String name;
    final MutableMs2Experiment exp;
    final FormulaConstraints constraints;
    final List<Element> elementsToAutoDetect;
    final PossibleIonModes possibleIonModes;
    final PossibleAdducts possibleAdducts;
    final Deviation massDev;
    final boolean onlyOrganic;
    final SearchableDatabase searchableDatabase;
    final SearchProfilePanel.Instruments instrument; // TODO: just a workaround
    final IsotopePatternHandling isoHandling;

    public PrepareSiriusIdentificationInputJob(ExperimentContainer ec, SearchProfilePanel.Instruments instrument, double ppm, boolean onlyOrganic, SearchableDatabase db, final FormulaConstraints constraints, final List<Element> elementsToAutoDetect, PossibleIonModes possibleIonModes, PossibleAdducts possibleAdducts, IsotopePatternHandling isoHandling) {
        super(JobType.CPU);
        this.sirius = Jobs.getSiriusByProfile(instrument.profile);
        this.name = ec.getGUIName();
        this.exp = ec.getMs2Experiment();
        this.constraints = constraints;
        this.elementsToAutoDetect = elementsToAutoDetect;
        this.possibleIonModes = possibleIonModes;
        this.possibleAdducts = possibleAdducts;
        this.massDev = new Deviation(ppm);
        this.onlyOrganic = onlyOrganic;
        this.searchableDatabase = db;
        this.instrument = instrument;
        this.isoHandling = isoHandling;
    }

    @Override
    protected MutableMs2Experiment compute() throws Exception {
        //check what we have to compute
        if (!elementsToAutoDetect.isEmpty() && !exp.getMs1Spectra().isEmpty()) {
            FormulaConstraints autoConstraints = sirius.predictElementsFromMs1(exp);
            if (autoConstraints != null) {
                ElementPredictor predictor = sirius.getElementPrediction();
                for (Element element : elementsToAutoDetect) {
                    if (predictor.isPredictable(element)) {
                        constraints.setUpperbound(element, autoConstraints.getUpperbound(element));
                    }
                }
            }
        }

        setAnnotations();
        updateProgress(0, 100, 100);
        return exp;
    }


    private void setAnnotations() throws Exception {
        PrecursorIonType i = exp.getPrecursorIonType();
        List<PrecursorIonType> ions;
        exp.setAnnotation(MsInstrumentation.class, instrument.instrument);
        if (i.isUnknownPositive()) {
            exp.setAnnotation(PossibleIonModes.class, PossibleIonModes.reduceTo(possibleIonModes, PeriodicTable.getInstance().getPositiveIonizationsAsString()));
            ions = exp.getAnnotation(PossibleIonModes.class).getIonModesAsPrecursorIonType();
        } else if (i.isUnknownNegative()) {
            exp.setAnnotation(PossibleIonModes.class, PossibleIonModes.reduceTo(possibleIonModes, PeriodicTable.getInstance().getNegativeIonizationsAsString()));
            ions = exp.getAnnotation(PossibleIonModes.class).getIonModesAsPrecursorIonType();
        } else if (i.isIonizationUnknown()) {
            exp.setAnnotation(PossibleIonModes.class, possibleIonModes);
            ions = exp.getAnnotation(PossibleIonModes.class).getIonModesAsPrecursorIonType();
        } else {
            ions = Collections.singletonList(i);
        }

        if (ions.size() != 1 || ions.get(0).getAdduct() == null) {
            Set<PrecursorIonType> allPossible = PeriodicTable.getInstance().adductsByIonisation(ions);
            exp.setAnnotation(PossibleAdducts.class, PossibleAdducts.intersection(possibleAdducts, allPossible));
        }

        exp.setAnnotation(PossibleIonModes.class, possibleIonModes); //todo autodetect
        exp.setAnnotation(PossibleAdducts.class, possibleAdducts);

        sirius.setTimeout(exp, -1, Integer.valueOf(PropertyManager.PROPERTIES.getProperty("de.unijena.bioinf.sirius.treebuilder.timeout", "0")));
        sirius.setFormulaConstraints(exp, constraints);
        sirius.setAllowedMassDeviation(exp, massDev);
        sirius.enableRecalibration(exp, true); //todo maybe as gui option
        sirius.setIsotopeMode(exp, isoHandling);

        if (exp.getMolecularFormula() == null) {
            if (searchableDatabase != null) {
                FormulaWhiteListJob wlj = new FormulaWhiteListJob(massDev, onlyOrganic, searchableDatabase, exp);
                wlj.call();
            }
        } else {
            exp.setAnnotation(Whiteset.class, new Whiteset(Collections.singleton(exp.getMolecularFormula())));
        }
    }


    @Override
    public SwingJJobContainer<MutableMs2Experiment> asSwingJob() {
        return new TextAreaJJobContainer<>(this, name, "Preparing Molecular Formula Identification");
    }
}
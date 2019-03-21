/*
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2015 Kai Dührkop
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with SIRIUS.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unijena.bioinf.ms.cli;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import de.unijena.bioinf.ChemistryBase.SimpleRectangularIsolationWindow;
import de.unijena.bioinf.ChemistryBase.chem.*;
import de.unijena.bioinf.ChemistryBase.jobs.SiriusJobs;
import de.unijena.bioinf.ChemistryBase.ms.*;
import de.unijena.bioinf.ChemistryBase.ms.inputValidators.Warning;
import de.unijena.bioinf.ChemistryBase.ms.utils.SimpleSpectrum;
import de.unijena.bioinf.ChemistryBase.ms.utils.Spectrums;
import de.unijena.bioinf.ChemistryBase.properties.PropertyManager;
import de.unijena.bioinf.ChemistryBase.sirius.projectspace.Index;
import de.unijena.bioinf.FragmentationTreeConstruction.computation.FragmentationPatternAnalysis;
import de.unijena.bioinf.FragmentationTreeConstruction.computation.tree.TreeBuilder;
import de.unijena.bioinf.FragmentationTreeConstruction.computation.tree.TreeBuilderFactory;
import de.unijena.bioinf.IsotopePatternAnalysis.IsotopePattern;
import de.unijena.bioinf.IsotopePatternAnalysis.IsotopePatternAnalysis;
import de.unijena.bioinf.IsotopePatternAnalysis.prediction.DNNRegressionPredictor;
import de.unijena.bioinf.IsotopePatternAnalysis.prediction.ElementPredictor;
import de.unijena.bioinf.babelms.GenericParser;
import de.unijena.bioinf.babelms.MsExperimentParser;
import de.unijena.bioinf.babelms.SpectralParser;
import de.unijena.bioinf.jjobs.BufferedJJobSubmitter;
import de.unijena.bioinf.jjobs.JobManager;
import de.unijena.bioinf.jjobs.exceptions.TimeoutException;
import de.unijena.bioinf.sirius.IdentificationResult;
import de.unijena.bioinf.sirius.Sirius;
import de.unijena.bioinf.sirius.core.ApplicationCore;
import de.unijena.bioinf.sirius.projectspace.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class CLI<Options extends SiriusOptions> extends ApplicationCore {
    protected Sirius sirius;
    protected final boolean shellMode;
    protected ShellProgress progress;

    protected FilenameFormatter filenameFormatter;
    protected ProjectWriter projectWriter;
    protected boolean shellOutputSurpressed = false;

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(CLI.class);


    public static void main(String[] args) {
        try {
            SiriusWorkspaceReader w = new SiriusWorkspaceReader(new File("/home/kaidu/data/datasets/irina_dataset/final.sirius"));

            ProjectReader reader = new DirectoryReader(w);
            while (reader.hasNext()) {
                ExperimentResult exp = reader.next();
                if (exp.getExperiment() == null)
                    System.out.println(exp.getExperimentName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(String s) {
        if (!shellOutputSurpressed) System.out.print(s);
    }

    public void println(String s) {
        if (!shellOutputSurpressed) System.out.println(s);
    }

    protected void printf(String msg, Object... args) {
        if (!shellOutputSurpressed)
            System.out.printf(Locale.US, msg, args);
    }

    public CLI() {
        this.shellMode = System.console() != null;
        this.progress = new ShellProgress(System.out, shellMode);
    }

    Options options;
    List<String> inputs, formulas;
    PrecursorIonType[] ionTypes;
    PrecursorIonType[] ionTypesWithoutAdducts;


    public void compute() {
        final long time = System.currentTimeMillis();
        try {
            //set oprtions todo: i would like to do this in the cli parser but how with jewelcli?
            int initBuffer = options.getMinInstanceBuffer() != null ? options.getMinInstanceBuffer() : PropertyManager.getNumberOfCores() * 2;
            int maxBuffer = options.getMaxInstanceBuffer() != null ? options.getMaxInstanceBuffer() : initBuffer * 2;

            if (initBuffer <= 0) {
                initBuffer = Integer.MAX_VALUE; //no buffering, submit all jobs at once
                maxBuffer = 0;
            }

            CLIJobSubmitter submitter = newSubmitter(handleInput(options));
            submitter.start(initBuffer, maxBuffer);
        } catch (IOException e) {
            logger.error("Error while handling the input data", e);
        } finally {
            if (projectWriter != null) try {
                projectWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            progress.info("Computation time: " + (double) (System.currentTimeMillis() - time) / 1000d + "s");
        }
    }

    //override to add more jobs
    protected void handleJobs(BufferedJJobSubmitter<Instance>.JobContainer jc) throws IOException {
        Sirius.SiriusIdentificationJob j = jc.getJob(Sirius.SiriusIdentificationJob.class);
        progress.info("Sirius results for: '" + jc.sourceInstance.file.getName() + "', " + jc.sourceInstance.experiment.getName());
        if (j != null)
            handleSiriusResults(jc, j); //handle results
        else
            logger.error("Could not load results for " + jc.sourceInstance.file.getName());
    }

    private void setPrecursorIonTypes(MutableMs2Experiment exp, PossibleAdducts pa, PossibleIonModes.GuessingMode guessingMode, boolean preferProtonation) {
        exp.setAnnotation(PossibleAdducts.class, pa);
        PossibleIonModes im = exp.getAnnotation(PossibleIonModes.class, new PossibleIonModes());
        im.setGuessFromMs1(guessingMode);


        if (preferProtonation){
            if (guessingMode.isEnabled()) im.enableGuessFromMs1WithCommonIonModes(exp.getPrecursorIonType().getCharge());
            final Set<Ionization> ionModes = new HashSet<>(pa.getIonModes());
            for (Ionization ion : ionModes) {
                im.add(ion, 0.02);
            }
            if (exp.getPrecursorIonType().getCharge()>0){
                im.add(PrecursorIonType.getPrecursorIonType("[M+H]+").getIonization(), 1d);
            } else {
                im.add(PrecursorIonType.getPrecursorIonType("[M-H]-").getIonization(), 1d);

            }
        } else {
            final Set<Ionization> ionModes = new HashSet<>(pa.getIonModes());
            for (Ionization ion : ionModes) {
                im.add(ion, 1d);
            }
        }

        exp.setAnnotation(PossibleIonModes.class, im);
    }

    protected Sirius.SiriusIdentificationJob makeSiriusJob(final Instance i) {
        Sirius.SiriusIdentificationJob job = null;
        sirius.setTimeout(i.experiment, options.getInstanceTimeout(), options.getTreeTimeout());
        final List<String> whitelist = formulas;

        final Set<MolecularFormula> whiteset = getFormulaWhiteset(i, whitelist);

        PossibleIonModes.GuessingMode enabledGuessingMode = options.isTrustGuessIonFromMS1()? PossibleIonModes.GuessingMode.SELECT : PossibleIonModes.GuessingMode.ADD_IONS;

        if (options.isAutoCharge()) { //TODO: add optiosn.getIon into this case
            if (i.experiment.getPrecursorIonType().isIonizationUnknown()) {
                i.experiment.setAnnotation(PossibleAdducts.class, null);
                setPrecursorIonTypes(i.experiment, new PossibleAdducts(Iterables.toArray(PeriodicTable.getInstance().getKnownLikelyPrecursorIonizations(i.experiment.getPrecursorIonType().getCharge()), PrecursorIonType.class)), enabledGuessingMode, true);
            } else {
                setPrecursorIonTypes(i.experiment, new PossibleAdducts(i.experiment.getPrecursorIonType()), PossibleIonModes.GuessingMode.DISABLED, false);
            }
            //todo whats with options.getIon().size() == 1 ?
        } else if (options.getIon() != null && options.getIon().size() > 1) {
            if (i.experiment.getPrecursorIonType().isIonizationUnknown()) {
                final List<PrecursorIonType> ionTypes = new ArrayList<>();
                for (String ion : options.getIon()) ionTypes.add(PrecursorIonType.getPrecursorIonType(ion));
                setPrecursorIonTypes(i.experiment, new PossibleAdducts(ionTypes), enabledGuessingMode, false);
            } else {
                setPrecursorIonTypes(i.experiment, new PossibleAdducts(i.experiment.getPrecursorIonType()), PossibleIonModes.GuessingMode.DISABLED, false);
            }
        } else {
            if (i.experiment.getPrecursorIonType().isIonizationUnknown()) {
                if (i.experiment.getPrecursorIonType().getCharge()==0)
                setPrecursorIonTypes(i.experiment, new PossibleAdducts(i.experiment.getPrecursorIonType().getCharge() > 0 ? PrecursorIonType.getPrecursorIonType("[M+H]+") : PrecursorIonType.getPrecursorIonType("[M-H]-")), enabledGuessingMode, true); // TODO: ins MS1 gucken
            } else {
                setPrecursorIonTypes(i.experiment, new PossibleAdducts(i.experiment.getPrecursorIonType()), PossibleIonModes.GuessingMode.DISABLED, false);
            }
        }

        if (options.isMostIntenseMs2()) {
            onlyKeepMostIntenseMS2(i.experiment);
        }

        sirius.enableRecalibration(i.experiment, !options.isNotRecalibrating());
        sirius.setIsotopeMode(i.experiment, options.getIsotopes());
        if (whiteset != null) sirius.setFormulaSearchList(i.experiment, whiteset);

        job = (sirius.makeIdentificationJob(i.experiment, getNumberOfCandidates()));
        return job;
    }

    /*
    remove all but the most intense ms2
     */
    protected void onlyKeepMostIntenseMS2(MutableMs2Experiment experiment) {
        if (experiment.getMs2Spectra().size() == 0) return;
        double precursorMass = experiment.getIonMass();
        int mostIntensiveIdx = -1;
        double maxIntensity = -1d;
        int pos = -1;
        if (experiment.getMs1Spectra().size() == experiment.getMs2Spectra().size()) {
            //one ms1 corresponds to one ms2. we take ms2 with most intense ms1 precursor peak
            for (Spectrum<Peak> spectrum : experiment.getMs1Spectra()) {
                ++pos;
                Deviation dev = new Deviation(100);
                int idx = Spectrums.mostIntensivePeakWithin(spectrum, precursorMass, dev);
                if (idx < 0) continue;
                double intensity = spectrum.getIntensityAt(idx);
                if (intensity > maxIntensity) {
                    maxIntensity = intensity;
                    mostIntensiveIdx = pos;
                }
            }
        }
        if (mostIntensiveIdx < 0) {
            //take ms2 with highest summed intensity
            pos = -1;
            for (Spectrum<Peak> spectrum : experiment.getMs2Spectra()) {
                ++pos;
                final int n = spectrum.size();
                double sumIntensity = 0d;
                for (int i = 0; i < n; ++i) {
                    sumIntensity += spectrum.getIntensityAt(i);
                }
                if (sumIntensity > maxIntensity) {
                    maxIntensity = sumIntensity;
                    mostIntensiveIdx = pos;
                }
            }
        }

        List<SimpleSpectrum> ms1List = new ArrayList<>();
        List<MutableMs2Spectrum> ms2List = new ArrayList<>();
        if (experiment.getMs1Spectra().size() == experiment.getMs2Spectra().size()) {
            ms1List.add(experiment.getMs1Spectra().get(mostIntensiveIdx));
        } else {
            ms1List.addAll(experiment.getMs1Spectra());
        }
        ms2List.add(experiment.getMs2Spectra().get(mostIntensiveIdx));
        experiment.setMs1Spectra(ms1List);
        experiment.setMs2Spectra(ms2List);
    }

    protected void handleSiriusResults(BufferedJJobSubmitter<Instance>.JobContainer jc, Sirius.SiriusIdentificationJob siriusJob) throws IOException {
        if (siriusJob != null) {
            try {
                final List<IdentificationResult> results = siriusJob.takeResult();
                if (!results.isEmpty()) {
                    int rank = 1;
                    int n = Math.max(1, (int) Math.ceil(Math.log10(results.size())));
                    for (IdentificationResult result : results) {
                        final IsotopePattern pat = result.getRawTree().getAnnotationOrNull(IsotopePattern.class);
                        final int isoPeaks = pat == null ? 0 : pat.getPattern().size();
                        printf("%" + n + "d.) %s\t%s\tscore: %.2f\ttree: %+.2f\tiso: %.2f\tpeaks: %d\texplained intensity: %.2f %%\tisotope peaks: %d\n", rank++, result.getMolecularFormula().toString(), String.valueOf(result.getResolvedTree().getAnnotationOrNull(PrecursorIonType.class)), result.getScore(), result.getTreeScore(), result.getIsotopeScore(), result.getResolvedTree().numberOfVertices(), sirius.getMs2Analyzer().getIntensityRatioOfExplainedPeaks(result.getResolvedTree()) * 100, isoPeaks);
                    }

                    //
                    // TODO: Dirty Hack
                    //
                    if (projectWriter != null) {
                        projectWriter.writeExperiment(createExperimentResult(jc, siriusJob, results));
                    }
                } else {
                    logger.warn("Cannot find valid tree that supports the data. You can try to increase the allowed mass deviation with parameter --ppm-max");
                    if (projectWriter != null) {
                        projectWriter.writeExperiment(new ExperimentResult(siriusJob.getExperiment(), null, "NORESULTS"));
                    }
                }
            } catch (TimeoutException e) {
                println("Ignore " + siriusJob.getExperiment().getName() + " due to timeout!");
                projectWriter.writeExperiment(new ExperimentResult(siriusJob.getExperiment(), null, "TIMEOUT"));
            } catch (RuntimeException e) {
                e.printStackTrace();
                println("Error during computation of " + siriusJob.getExperiment().getName() + ": " + e.getMessage());
                logger.debug("Error during computation of " + siriusJob.getExperiment().getName(), e);
                projectWriter.writeExperiment(new ExperimentResult(siriusJob.getExperiment(), null, "ERROR"));
            }
        } else {
            logger.debug("Null job occurred!");
        }


    }

    protected ExperimentResult createExperimentResult(BufferedJJobSubmitter<Instance>.JobContainer jc, Sirius.SiriusIdentificationJob siriusJob, List<IdentificationResult> results) {
        return new ExperimentResult(siriusJob.getExperiment(), results);
    }

    protected Set<MolecularFormula> getFormulaWhiteset(Instance i, List<String> whitelist) {
        final Set<MolecularFormula> whiteset = new HashSet<MolecularFormula>();
        if (whitelist == null && (options.getNumberOfCandidates() == null) && i.experiment.getMolecularFormula() != null) {
            whiteset.add(i.experiment.getMolecularFormula());
        } else if (whitelist != null) for (String s : whitelist) whiteset.add(MolecularFormula.parse(s));
        return whiteset.isEmpty() ? null : whiteset;
    }

    private Integer getNumberOfCandidates() {
        return options.getNumberOfCandidates() != null ? options.getNumberOfCandidates() : 5;
    }

    protected void cite() {
        println("Please cite the following paper when using our method:");
        println(ApplicationCore.CITATION);
    }

    protected void parseArgsAndInit(String[] args, Class<Options> optionsClass) {
        parseArgs(args, optionsClass);
        setup();
        validate();
    }

    protected void validate() {

    }


    public void parseArgs(String[] args, Class<Options> optionsClass) {
        if (args.length == 0) {
            println(CliFactory.createCli(optionsClass).getHelpMessage());
            System.exit(0);
        }
        try {
            args = fixBuggyJewelCliLibrary(args);
            this.options = CliFactory.createCli(optionsClass).parseArguments(args);
            if (options.isCite()) {
                cite();
                System.exit(0);
            }
        } catch (HelpRequestedException e) {
            println(e.getMessage());
            println("");
            cite();
            System.exit(0);
        }
        if (options.isVersion()) {
            cite();
            System.exit(0);
        }
        handleOutputOptions(options);
    }

    private String[] fixBuggyJewelCliLibrary(String[] args) {
        final List<String> argsCopy = new ArrayList<>();
        final List<String> ionModeStrings = new ArrayList<>();
        boolean ionIn = false;
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.equals("--ion") || arg.equals("-i")) {
                if (!ionIn) {
                    ionModeStrings.add(arg);
                    ionIn = true;
                }
                final Pattern ionPattern = Pattern.compile("^\\s*\\[?\\s*M\\s*[+-\\]]");
                // if a list parameter is last parameter, we have to distinguish it from the rest parameter
                for (i = i + 1; i < args.length; ++i) {
                    arg = args[i];
                    if (ionPattern.matcher(arg).find()) {
                        ionModeStrings.add(arg);
                    } else {
                        break;
                    }
                }
            }
            argsCopy.add(arg);
        }
        if (ionModeStrings.size() > 0) ionModeStrings.add("--placeholder");
        ionModeStrings.addAll(argsCopy);
        return ionModeStrings.toArray(new String[ionModeStrings.size()]);
    }

    protected void handleOutputOptions(Options options) {
        int numberOfWrittenExperiments = 0;
        if (options.getNumOfCores() > 0) {
            PropertyManager.PROPERTIES.setProperty("de.unijena.bioinf.sirius.cpu.cores", String.valueOf(options.getNumOfCores()));
        }

        if (options.isQuiet() || "-".equals(options.getSirius())) {
            this.shellOutputSurpressed = true;
            disableShellLogging();
        }
        if ("-".equals(options.getOutput())) {
            logger.error("Cannot write output files and folders into standard output stream. Please use --sirius t get a zip file of SIRIUS output into the standard output stream");
            System.exit(1);
        }

        if (options.getNamingConvention()!=null){
            String formatString = options.getNamingConvention();
            try {
                filenameFormatter = new StandardMSFilenameFormatter(formatString);
            } catch (ParseException e) {
                logger.error("Cannot parse naming convention:\n" + e.getMessage(), e);
                System.exit(1);
            }
        } else {
            //default
            filenameFormatter = new StandardMSFilenameFormatter();
        }


        final List<ProjectWriter> writers = new ArrayList<>();

        if (options.getOutput() != null) {
            final ProjectWriter pw;
            if (new File(options.getOutput()).exists()) {
                try {
                    checkForValidProjectDirectory(options.getOutput());
                    pw = new ProjectSpaceMerger(this, options.getOutput(), false);
                    writers.add(pw);
                    numberOfWrittenExperiments = Math.max(numberOfWrittenExperiments,((ProjectSpaceMerger)pw).getNumberOfWrittenExperiments());
                } catch (IOException e) {

                    logger.error("Cannot merge project " + options.getOutput() + ". Maybe the specified directory is not a valid SIRIUS workspace. You can still specify a new not existing filename to create a new workspace.\n" + e.getMessage(), e);
                    System.exit(1);
                    return;
                }
            } else {
                try {
                    pw = getDirectoryOutputWriter(options.getOutput(), getWorkspaceWritingEnvironmentForDirectoryOutput(options.getOutput()), filenameFormatter);
                    writers.add(pw);
                } catch (IOException e) {
                    logger.error("Cannot write into " + options.getOutput() + ":\n" + e.getMessage(), e);
                    System.exit(1);
                }
            }
        }
        if (options.getSirius() != null) {
            final ProjectWriter pw;
            if (options.getSirius().equals("-")) {
                pw = getSiriusOutputWriter(options.getSirius(), getWorkspaceWritingEnvironmentForSirius(options.getSirius()), filenameFormatter);
                shellOutputSurpressed = true;
            } else if (new File(options.getSirius()).exists()) {
                try {
                    pw = new ProjectSpaceMerger(this, options.getSirius(), true);
                    numberOfWrittenExperiments = Math.max(numberOfWrittenExperiments,((ProjectSpaceMerger)pw).getNumberOfWrittenExperiments());
                } catch (IOException e) {
                    System.err.println("Cannot merge " + options.getSirius() + ". The specified file might be no valid SIRIUS workspace. You can still specify a new not existing filename to create a new workspace.");
                    System.exit(1);
                    return;
                }
            } else {
                pw = getSiriusOutputWriter(options.getSirius(), getWorkspaceWritingEnvironmentForSirius(options.getSirius()), filenameFormatter);
            }

            writers.add(pw);
        }

        if (writers.size() > 1) {
            this.projectWriter = new MultipleProjectWriter(writers.toArray(new ProjectWriter[writers.size()]));
        } else if (writers.size() > 0) {
            this.projectWriter = writers.get(0);
        } else {
            this.projectWriter = new ProjectWriter() {
                @Override
                public void writeExperiment(ExperimentResult result) throws IOException {
                    // dummy stub
                }

                @Override
                public void close() throws IOException {
                    // dummy stub
                }
            };
        }
        this.instanceIdOffset = numberOfWrittenExperiments;
    }

    protected int instanceIdOffset;

    private void checkForValidProjectDirectory(String output) throws IOException {
        final File f = new File(output);
        if (!f.exists()) return;
        if (!f.isDirectory()) throw new IOException("Expect a directory name. But " + output + " is an existing file.");
        final Pattern pat = Pattern.compile("Sirius", Pattern.CASE_INSENSITIVE);
        boolean empty = true;
        for (File g : f.listFiles()) {
            empty = false;
            if (g.getName().equalsIgnoreCase("version.txt")) {
                for (String line : Files.readLines(g, Charset.forName("UTF-8"))) {
                    if (pat.matcher(line).find()) return;
                }
            }
        }
        if (!empty)
            throw new IOException("Given directory is not a valid SIRIUS workspace. Please specify an empty directory or existing SIRIUS workspace!");
    }

    private void disableShellLogging() {
        Handler ch = null;
        for (Handler h : Logger.getGlobal().getHandlers()) {
            if (h instanceof ConsoleHandler) {
                ch = h;
                break;
            }
        }
        if (ch != null) {
            Logger.getGlobal().removeHandler(ch);
        }
    }

    // TODO: implement merge?
    protected DirectoryWriter.WritingEnvironment getWorkspaceWritingEnvironmentForSirius(String value) {
        try {
            if (value.equals("-")) {
                return new SiriusWorkspaceWriter(System.out);
            } else {
                return new SiriusWorkspaceWriter(new FileOutputStream(new File(value)));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot write into " + value + ". The given file name might already exists.");
            System.exit(1);
            return null;
        }
    }

    protected DirectoryWriter.WritingEnvironment getWorkspaceWritingEnvironmentForDirectoryOutput(String value) throws IOException {
        final File root = new File(value);
        if (root.exists()) {
            System.err.println("Cannot create directory " + root.getName() + ". File already exist.");
            System.exit(1);
            return null;
        }
        root.mkdirs();
        return new SiriusFileWriter(root);
    }

    protected ProjectWriter getSiriusOutputWriter(String sirius, DirectoryWriter.WritingEnvironment env, FilenameFormatter filenameFormatter) {
        return new DirectoryWriter(env, ApplicationCore.VERSION_STRING, filenameFormatter);
    }

    protected ProjectWriter getDirectoryOutputWriter(String sirius, DirectoryWriter.WritingEnvironment env, FilenameFormatter filenameFormatter) {
        return new DirectoryWriter(env, ApplicationCore.VERSION_STRING, filenameFormatter);
    }

    protected DirectoryReader.ReadingEnvironment getWorkspaceReadingEnvironmentForSirius(String value) {
        try {
            return new SiriusWorkspaceReader(new File(value));
        } catch (IOException e) {
            System.err.println("Cannot read " + value + ":\n" + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    protected DirectoryReader.ReadingEnvironment getWorkspaceReadingEnvironmentForDirectoryOutput(String value) {
        final File root = new File(value);
        return new SiriusFileReader(root);
    }

    protected ProjectReader getSiriusOutputReader(String sirius, DirectoryReader.ReadingEnvironment env) {
        return new DirectoryReader(env);
    }

    protected ProjectReader getDirectoryOutputReader(String sirius, DirectoryReader.ReadingEnvironment env) {
        return new DirectoryReader(env);
    }

    public void setup() {
        try {
            this.sirius = new Sirius(options.getProfile());
            Sirius.USE_FAST_MODE = !options.isDisableFastMode();
//            if (options.isDisableFastMode()) LoggerFactory.getLogger(CLI.class).info("Use experimental fast mode. Results might differ from default mode.");
            final FragmentationPatternAnalysis ms2 = sirius.getMs2Analyzer();
            final IsotopePatternAnalysis ms1 = sirius.getMs1Analyzer();
            final MutableMeasurementProfile ms1Prof = new MutableMeasurementProfile(ms1.getDefaultProfile());
            final MutableMeasurementProfile ms2Prof = new MutableMeasurementProfile(ms2.getDefaultProfile());
            final String outerClassName = getClass().getName();
            ms2.setValidatorWarning(new Warning() {
                @Override
                public void warn(String message) {
                    Logger.getLogger(outerClassName).warning(message);
                }
            });
//            if (options.getElements() == null) {
//                // autodetect and use default set
//                ms1Prof.setFormulaConstraints(getDefaultElementSet(options));
//                ms2Prof.setFormulaConstraints(getDefaultElementSet(options));
//            } else {
//                ms2Prof.setFormulaConstraints(options.getElements());
//                ms1Prof.setFormulaConstraints(options.getElements());
//            }

            if (options.getMedianNoise() != null) {
                ms2Prof.setMedianNoiseIntensity(options.getMedianNoise());
            }
            if (options.getPPMMax() != null) {
                ms2Prof.setAllowedMassDeviation(new Deviation(options.getPPMMax()));
                ms1Prof.setAllowedMassDeviation(new Deviation(options.getPPMMax()));
            }
            if (options.getPPMMaxMs2() != null) {
                ms2Prof.setAllowedMassDeviation(new Deviation(options.getPPMMax()));
            }
            final TreeBuilder builder = sirius.getMs2Analyzer().getTreeBuilder();
            if (builder == null) {
                String noILPSolver = "Could not load a valid ILP solver (TreeBuilder) " + Arrays.toString(TreeBuilderFactory.getBuilderPriorities()) + ". Please read the installation instructions.";
                logger.error(noILPSolver);
                System.exit(1);
            }
            logger.info("Compute trees using " + builder);


            sirius.getMs2Analyzer().setDefaultProfile(ms2Prof);
            sirius.getMs1Analyzer().setDefaultProfile(ms1Prof);

            if (options.isEnableSiliconDetection()) {
                ElementPredictor elementPredictor = sirius.getElementPrediction();
                if (elementPredictor instanceof DNNRegressionPredictor) {
                    ((DNNRegressionPredictor) elementPredictor).enableSilicon();
                }
            }

            /*
            if (options.getPossibleIonizations() != null) {
                List<String> ionList = options.getPossibleIonizations();
                if (ionList.size() == 1) {
                    ionList = Arrays.asList(ionList.get(0).split(","));
                }
                if (ionList.size() == 1) {
                    logger.error("Cannot guess ionization when only one ionization/adduct is provided");
                }
                ionTypes = new PrecursorIonType[ionList.size()];
                Set<PrecursorIonType> set = new HashSet<>();
                for (int i = 0; i < ionTypes.length; i++) {
                    String ion = ionList.get(i);
                    ionTypes[i] = PrecursorIonType.getPrecursorIonType(ion);
                    set.add(ionTypes[i].withoutAdduct());
                }
                ionTypesWithoutAdducts = set.toArray(new PrecursorIonType[0]);

            }
            */
        } catch (IOException e) {
            logger.error("Cannot load profile '" + options.getProfile() + "':\n", e);
            System.exit(1);
        }
    }

    protected Instance setupInstance(Instance inst) {
        final MutableMs2Experiment exp = inst.experiment instanceof MutableMs2Experiment ? (MutableMs2Experiment) inst.experiment : new MutableMs2Experiment(inst.experiment);
        if (exp.getPrecursorIonType() == null || exp.getPrecursorIonType().isIonizationUnknown())
            exp.setPrecursorIonType(getIonFromOptions(options, exp.getPrecursorIonType() == null ? 0 : exp.getPrecursorIonType().getCharge()));
        if (formulas != null && formulas.size() == 1) exp.setMolecularFormula(MolecularFormula.parse(formulas.get(0)));
        if (options.getParentMz() != null) exp.setIonMass(options.getParentMz());
        if (options.getIsolationWindowWidth()!=null) {
            final double width = options.getIsolationWindowWidth();
            final double shift = options.getIsolationWindowShift();
            final double right = width/2d+shift;
            final double left = -width/2d+shift;
            SimpleRectangularIsolationWindow isolationWindow = new SimpleRectangularIsolationWindow(left, right);
            exp.setAnnotation(IsolationWindow.class, isolationWindow);
        }

        return new Instance(exp, inst.file, inst.index);
    }

    public Iterator<Instance> handleInput(final SiriusOptions options) throws IOException {
        final ArrayDeque<Instance> instances = new ArrayDeque<Instance>();

        inputs = options.getInput() == null ? new ArrayList<String>() : options.getInput();
        formulas = options.getFormula();
        // WARNING: if you run sirius --formula C6H12O6 C2H2 bla.ms
        // JewelCli will put the bla.ms into the formulas list
        // however, we can simply check if the entries in the formula list
        // are filenames and, if so, move them back into the input list
        if (formulas != null) {
            formulas = new ArrayList<>(formulas);
            final ListIterator<String> iter = formulas.listIterator(formulas.size());
            while (iter.hasPrevious()) {
                final String s = iter.previous();
                if (new File(s).exists()) {
                    inputs.add(s);
                    iter.remove();
                } else {
                    break;
                }
            }
            if (options.getIon()!=null){
                for (String ionString : options.getIon()) {
                    for (String formula : formulas) {
                        if (formula.equals(ionString)){
                            throw new IllegalArgumentException(String.format("The formula '%s' is also provided as an ionization. The CLI seemed to incorrectly parsed the parameters. " +
                                    "Please specify formulas as last parameter (but before the list of input files).", formula));
                        }
                    }
                }
            }


        }

        final MsExperimentParser parser = new MsExperimentParser();
        // two different input modes:
        // general information that should be used if this fields are missing in the file
        final Double defaultParentMass = options.getParentMz();
        final FormulaConstraints constraints = options.getElements() == null ? null/*getDefaultElementSet(options, ion)*/ : options.getElements();
        // direct input: --ms1 and --ms2 command line options are given
        if (options.getMs2() != null && !options.getMs2().isEmpty()) {
            if (options.getIon()==null){
                throw new IllegalArgumentException("Please specify ionization via --ion when using --ms2 option.");
            }
            final MutableMeasurementProfile profile = new MutableMeasurementProfile();
            profile.setFormulaConstraints(constraints);
            final MutableMs2Experiment exp = new MutableMs2Experiment();
            exp.setSource(options.getMs2().get(0));
            final PrecursorIonType ionType = getIonFromOptions(options, 0);
            if (ionType.isIonizationUnknown() && options.getParentMz()==null && formulas.size()==1){
                throw new IllegalArgumentException("Please specify a single ionization via --ion. Precursor mass cannot be computed from molecular formula only.");
            } else if (ionType.isIonizationUnknown() && options.getParentMz()==null) {
                throw new IllegalArgumentException("Please specify ionization and precursor mass when using --ms2 option.");
            }
            exp.setPrecursorIonType(ionType);
            exp.setMs2Spectra(new ArrayList<MutableMs2Spectrum>());
            for (File f : foreachIn(options.getMs2())) {
                final Iterator<Ms2Spectrum<Peak>> spiter = SpectralParser.getParserFor(f).parseSpectra(f);
                while (spiter.hasNext()) {
                    final Ms2Spectrum<Peak> spec = spiter.next();
                    if (spec.getIonization() == null || spec.getPrecursorMz() == 0 || spec.getMsLevel() == 0) {
                        final MutableMs2Spectrum ms;
                        if (spec instanceof MutableMs2Spectrum) ms = (MutableMs2Spectrum) spec;
                        else ms = new MutableMs2Spectrum(spec);
                        if (ms.getIonization() == null) ms.setIonization(ionType.getIonization());
                        if (ms.getMsLevel() == 0) ms.setMsLevel(2);
                        if (ms.getPrecursorMz() == 0) {
                            if (defaultParentMass == null) {
                                if (exp.getMs2Spectra().size() > 0) {
                                    ms.setPrecursorMz(exp.getMs2Spectra().get(0).getPrecursorMz());
                                } else {
                                    final MolecularFormula formula;
                                    if (exp.getMolecularFormula() != null) formula = exp.getMolecularFormula();
                                    else if (formulas != null && formulas.size() == 1)
                                        formula = MolecularFormula.parse(formulas.get(0));
                                    else formula = null;
                                    if (formula != null) {
                                        ms.setPrecursorMz(ms.getIonization().addToMass(formula.getMass()));
                                    } else ms.setPrecursorMz(0);
                                }
                            } else {
                                ms.setPrecursorMz(defaultParentMass);
                            }
                        }
                    }
                    exp.getMs2Spectra().add(new MutableMs2Spectrum(spec));
                }
            }
            if (exp.getMs2Spectra().size() <= 0)
                throw new IllegalArgumentException("SIRIUS expect at least one MS/MS spectrum. Please add a MS/MS spectrum via --ms2 option");

            if (options.getMs2() != null && options.getMs1() != null && !options.getMs1().isEmpty()) {
                exp.setMs1Spectra(new ArrayList<SimpleSpectrum>());
                for (File f : options.getMs1()) {
                    final Iterator<Ms2Spectrum<Peak>> spiter = SpectralParser.getParserFor(f).parseSpectra(f);
                    while (spiter.hasNext()) {
                        exp.getMs1Spectra().add(new SimpleSpectrum(spiter.next()));
                    }
                }
                exp.setMergedMs1Spectrum(exp.getMs1Spectra().get(0));
            }

            final double expPrecursor;
            if (options.getParentMz() != null) {
                expPrecursor = options.getParentMz();
            } else if (exp.getMolecularFormula() != null) {
                expPrecursor = exp.getPrecursorIonType().neutralMassToPrecursorMass(exp.getMolecularFormula().getMass());
            } else {
                double prec = 0d;
                for (int k = 0; k < exp.getMs2Spectra().size(); ++k) {
                    final double pmz = exp.getMs2Spectra().get(k).getPrecursorMz();
                    if (pmz != 0 && Math.abs(pmz - exp.getMs2Spectra().get(0).getPrecursorMz()) > 1e-3) {
                        throw new IllegalArgumentException("The given MS/MS spectra have different precursor mass and cannot belong to the same compound");
                    } else if (pmz != 0) prec = pmz;
                }
                if (prec == 0) {
                    if (exp.getMs1Spectra().size() > 0) {
                        final SimpleSpectrum patterns = sirius.getMs1Analyzer().extractPattern(exp, exp.getMergedMs1Spectrum().getMzAt(0));
                        if (patterns.size() < exp.getMergedMs1Spectrum().size()) {
                            throw new IllegalArgumentException("SIRIUS cannot infer the parentmass of the measured compound from MS1 spectrum. Please provide it via the -z option.");
                        }
                        expPrecursor = patterns.getMzAt(0);
                    } else
                        throw new IllegalArgumentException("SIRIUS expects the parentmass of the measured compound as parameter. Please provide it via the -z option.");
                } else expPrecursor = prec;
            }


            exp.setIonMass(expPrecursor);
            if (exp.getName() == null) {
                exp.setName("unknown");
            }
            if (constraints != null) {
                sirius.setFormulaConstraints(exp, constraints);
            }
            if (options.isDisableElementDetection()) {
                sirius.enableAutomaticElementDetection(exp, false);
            }
            instances.add(new Instance(exp, options.getMs2().get(0), ++instanceIdOffset));
        } else if (options.getMs1() != null && !options.getMs1().isEmpty()) {
            throw new IllegalArgumentException("SIRIUS expect at least one MS/MS spectrum. Please add a MS/MS spectrum via --ms2 option");
        }
        // batch input: files containing ms1 and ms2 data are given
        if (!inputs.isEmpty()) {
            final Iterator<File> fileIter;
            final ArrayList<File> infiles = new ArrayList<File>();
            Collections.sort(inputs);
            for (String f : inputs) {
                final File g = new File(f);
                if (g.isDirectory()) {
                    File[] ins = g.listFiles(pathname -> pathname.isFile());
                    if (ins != null) {
                        Arrays.sort(ins, Comparator.comparing(File::getName));
                        infiles.addAll(Arrays.asList(ins));
                    }
                } else {
                    infiles.add(g);
                }
            }
            fileIter = infiles.iterator();
            return new Iterator<Instance>() {
                File currentFile;
                int index = instanceIdOffset;
                Iterator<Ms2Experiment> experimentIterator = fetchNext();

                @Override
                public boolean hasNext() {
                    return !instances.isEmpty();
                }

                @Override
                public Instance next() {
                    fetchNext();
                    Instance c = instances.poll();
                    return setupInstance(c);
                }

                private Iterator<Ms2Experiment> fetchNext() {
                    start:
                    while (true) {
                        if (experimentIterator == null || !experimentIterator.hasNext()) {
                            if (fileIter.hasNext()) {
                                currentFile = fileIter.next();
                                try {
                                    GenericParser<Ms2Experiment> p = parser.getParser(currentFile);
                                    if (p == null) {
                                        logger.error("Unknown file format: '" + currentFile + "'");
                                    } else experimentIterator = p.parseFromFileIterator(currentFile);
                                } catch (IOException e) {
                                    logger.error("Cannot parse file '" + currentFile + "':\n", e);
                                }
                            } else return null;
                        } else {
                            MutableMs2Experiment experiment = sirius.makeMutable(experimentIterator.next());
                            if (options.getMaxMz() != null) {
                                //skip high-mass compounds
                                if (experiment.getIonMass() > options.getMaxMz()) continue start;
                            }
                            if (constraints != null) {
                                sirius.setFormulaConstraints(experiment, constraints);
                            }
                            if (options.isDisableElementDetection()) {
                                sirius.enableAutomaticElementDetection(experiment, false);
                            }

                            Index expIndex = experiment.getAnnotation(Index.class);
                            int currentIndex;
                            if (instanceIdOffset==0 && expIndex!=null && expIndex.index>=0){
                                //if no workspaces are merged and parser provides real index, use if
                                currentIndex = expIndex.index;
                            } else {
                                //normal fallback
                                currentIndex = ++index;
                            }
                            instances.add(new Instance(experiment, currentFile, currentIndex));
                            return experimentIterator;
                        }
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            return instances.iterator();
        }
    }

    private List<File> foreachIn(List<File> ms2) {
        final List<File> queue = new ArrayList<File>();
        for (File f : ms2) {
            if (f.isDirectory()) {
                for (File g : f.listFiles())
                    if (!g.isDirectory())
                        queue.add(g);
            } else queue.add(f);
        }
        return queue;
    }


    private static final Pattern CHARGE_PATTERN = Pattern.compile("(\\d+)[+-]?");
    private static final Pattern CHARGE_PATTERN2 = Pattern.compile("[+-]?(\\d+)");

    protected static PrecursorIonType getIonFromOptions(SiriusOptions opt, int charge) {
        List<String> ionStr = opt.getIon();
        if (ionStr == null) {
            return PrecursorIonType.unknown(charge);
        } else if (ionStr.size() == 1) {
            final PrecursorIonType ionType = PeriodicTable.getInstance().ionByName(opt.getIon().get(0));
            if (ionType.isIonizationUnknown() && !opt.isAutoCharge()) {
                if (ionType.getCharge() > 0) return PrecursorIonType.getPrecursorIonType("[M+H]+");
                else return PrecursorIonType.getPrecursorIonType("[M-H]-");
            } else return ionType;
        } else {
            final List<PrecursorIonType> ionTypes = new ArrayList<>();
            for (String ion : ionStr) ionTypes.add(PrecursorIonType.getPrecursorIonType(ion));
            int ch = ionTypes.get(0).getCharge();
            for (PrecursorIonType pi : ionTypes)
                if (pi.getCharge() != ch)
                    throw new IllegalArgumentException("SIRIUS does not support different charge states for the same compound");
            return PrecursorIonType.unknown(ch);
        }
    }

    private final static FormulaConstraints DEFAULT_ELEMENTS = new FormulaConstraints("CHNOP[5]S");

    public FormulaConstraints getDefaultElementSet(SiriusOptions opts) {
        final FormulaConstraints cf = (opts.getElements() != null) ? opts.getElements() : DEFAULT_ELEMENTS;
        return cf;
    }

    protected static String fileNameWithoutExtension(File file) {
        final String name = file.getName();
        final int i = name.lastIndexOf('.');
        if (i >= 0) return name.substring(0, i);
        else return name;
    }


    private <T> boolean arrayContains(T[] array, T object) {
        return this.arrayFind(array, object) >= 0;
    }

    private <T> int arrayFind(T[] array, T object) {
        for (int i = 0; i < array.length; ++i) {
            Object t = array[i];
            if (t.equals(object)) {
                return i;
            }
        }

        return -1;
    }


    private int countMatches(String string, String sub) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = string.indexOf(sub, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += sub.length();
            }
        }
        return count;
    }


    protected class CLIJobSubmitter extends BufferedJJobSubmitter<Instance> {

        public CLIJobSubmitter(Iterator<Instance> instances) {
            super(instances);
        }

        @Override
        protected void submitJobs(final JobContainer watcher) {
            submitJob(makeSiriusJob(watcher.sourceInstance), watcher);
        }

        @Override
        protected void handleResults(JobContainer watcher) {
            try {
                handleJobs(watcher);
            } catch (IOException e) {
                logger.error("Error processing instance: " + watcher.sourceInstance.file.getName());
            }
        }

        @Override
        protected JobManager jobManager() {
            return SiriusJobs.getGlobalJobManager();
        }
    }

    protected CLIJobSubmitter newSubmitter(Iterator<Instance> instanceIterator) {
        return new CLIJobSubmitter(instanceIterator);
    }
}

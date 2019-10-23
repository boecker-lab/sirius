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

import com.lexicalscope.jewel.cli.Option;
import com.lexicalscope.jewel.cli.Unparsed;
import de.unijena.bioinf.ChemistryBase.chem.FormulaConstraints;
import de.unijena.bioinf.sirius.IsotopePatternHandling;

import java.io.File;
import java.util.List;

public interface SiriusOptions {

    //////////////////
    // OUTPUT OPTIONS
    //////////////////

    @Option(shortName = "q", description = "surpress shell output")
    boolean isQuiet();

    @Option(shortName = "o", description = "output directory.", defaultToNull = true)
    String getOutput();


    @Option(longName = {"sirius", "workspace"}, shortName = "w", description = "store workspace into given file, such that it can be loaded by SIRIUS GUI afterwards", defaultToNull = true)
    String getSirius();

    /////////////////////////////////////////////////
    // run Zodiac

    @Option(longName = {"zodiac"}, description = "run zodiac on a given sirius workspace.", hidden = true)
    boolean isZodiac();

    /////////////////////////////////////////////////

    @Option
    boolean isVersion();

    @Option(shortName = "s", longName = "isotope", defaultValue = "score", description = "how to handle isotope pattern data. Use 'score' to use them for ranking (default) or 'filter' if you just want to remove candidates with bad isotope pattern. With 'both' you can use isotopes for filtering and scoring. Use 'omit' to ignore isotope pattern.")
    IsotopePatternHandling getIsotopes();

    @Option(shortName = "c", longName = "candidates", description = "Number of candidates in the output", defaultToNull = true)
    Integer getNumberOfCandidates();

    @Option(shortName = "f", longName = {"formula", "formulas"}, description = "specify the neutral molecular formula of the measured compound to compute its tree or a list of candidate formulas the method should discriminate. Omit this option if you want to consider all possible molecular formulas", defaultToNull = true)
    List<String> getFormula();

    @Option(longName = "no-recalibration")
    boolean isNotRecalibrating();

    @Option(longName = "ppm-max", description = "allowed ppm for decomposing masses", defaultToNull = true)
    Double getPPMMax();

    @Option(longName = "ppm-max-ms2", description = "allowed ppm for decomposing masses in MS2. If not specified, the same value as for the MS1", defaultToNull = true)
    Double getPPMMaxMs2();

    @Option(longName = "noise", description = "median intensity of noise peaks", defaultToNull = true)
    Double getMedianNoise();

    @Option(shortName = "Z", longName = "auto-charge", description = "Use this option if the adduct type and/or ion mode of your compounds is unknown and you do not want to assume [M+H]+/[M-H]- as default. With the option enabled, SIRIUS will also search for other adduct types (e.g. [M+NH3+H]+ or even other ion modes (e.g. [M+Na]+) if no ion mode is specified.")
    boolean isAutoCharge();

    @Option(shortName = "h", longName = "help", helpRequest = true)
    boolean isHelp();

    @Option
    boolean isCite();

    /*
        @Option(shortName = "o", description = "target directory/filename for the output", defaultToNull = true)
        public File getOutput();

        @Option(shortName = "O", description = "file format of the output. Available are 'dot', 'json' and 'sirius'. 'sirius' is file format that can be read by the Sirius user interface.", defaultToNull = true)
        public String getFormat();

        @Option(shortName = "a", longName = "annotate", description = "if set, a csv file is  created additional to the trees. It contains all annotated peaks together with their explanation ")
        public boolean isAnnotating();

        @Option(longName = "no-html", description = "only for DOT/graphviz output: Do not use html for node labels")
        public boolean isNoHTML();

        @Option(longName = "iontree", description = "Print molecular formulas and node labels with the ion formula instead of the neutral formula")
        public boolean isIonTree();
    */
    @Option(shortName = "p", description = "name of the configuration profile. Some of the default profiles are: 'qtof', 'orbitrap', 'fticr'.", defaultValue = "default")
    String getProfile();

    @Option(longName = "disable-fast-mode", hidden = true)
    public boolean isDisableFastMode();

    @Option(shortName = "1", longName = "ms1", description = "MS1 spectrum file name", minimum = 0, defaultToNull = true)
    List<File> getMs1();

    @Option(shortName = "2", longName = "ms2", description = "MS2 spectra file names", minimum = 0, defaultToNull = true)
    List<File> getMs2();

    @Option(shortName = "z", longName = {"parentmass", "precursor", "mz"}, description = "the mass of the parent ion", defaultToNull = true)
    Double getParentMz();

    @Option(shortName = "i", longName = "ion", description = "the ionization/adduct of the MS/MS data. Example: [M+H]+, [M-H]-, [M+Cl]-, [M+Na]+, [M]+. You can also provide a comma separated list of adducts.", defaultToNull = true)
    List<String> getIon();

    @Option(longName = "tree-timeout", description = "Time out in seconds per fragmentation tree computations. 0 for an infinite amount of time. Default: 0", defaultValue = "0")
    int getTreeTimeout();

    @Option(longName = "compound-timeout", description = "Maximal computation time in seconds for a single compound. 0 for an infinite amount of time. Default: 0", defaultValue = "0")
    int getInstanceTimeout();

    @Unparsed
    List<String> getInput();


    @Option(shortName = "e", longName = "elements", description = "The allowed elements. Write CHNOPSCl to allow the elements C, H, N, O, P, S and Cl. Add numbers in brackets to restrict the minimal and maximal allowed occurence of these elements: CHNOP[5]S[8]Cl[1-2]. When one number is given then it is interpreted as upperbound.", defaultToNull = true)
    FormulaConstraints getElements();
    /*
    @Option(shortName = "f", longName = {"guession"}, description = "specifies a list of possible ionizations/adducts, e.g. '[M]+,[M+H]+,[M+Na]+,[M+K]+'.", defaultToNull = true)
    List<String> getPossibleIonizations();
    */

    @Option(longName = "maxmz", description = "Just consider compounds with a precursor mz lower or equal this maximum mz. All other compounds in the input file are ignored.", defaultToNull = true)
    Double getMaxMz();

    @Option(longName = "mostintense-ms2", description = "Only use the fragmentation spectrum with the most intense precursor peak (for each compound).")
    boolean isMostIntenseMs2();

    @Option(longName = "trust-ion-prediction", description = "By default we use MS1 information to select additional ionizations ([M+Na]+,[M+K]+,[M+Cl]-,[M+Br]-) for considerations. With this parameter we trust the MS1 prediction and only consider these found ionizations.")
    boolean isTrustGuessIonFromMS1();


    //naming
    @Option(longName = "naming-convention", description = "Specify a format for compounds' output directorys. Default %index_%filename_%compoundname",  defaultToNull = true)
    String getNamingConvention();



    //technical stuff
    @Option(
            longName = {"processors", "cores"},
            description = "Number of cpu cores to use. If not specified Sirius uses all available cores.",
            defaultValue = "0"
    )
    int getNumOfCores();

    @Option(longName = "max-compound-buffer", description = "Maxmimal number of compounds that will be buffered in Memory. A larger buffer ensures that there are enough compounds available to use all cores efficiently during computation. A smaller buffer saves Memory. For Infinite buffer size set it to 0. Default: 2 * --initial_intance_buffer", defaultToNull = true)
    Integer getMaxInstanceBuffer();

    @Option(longName = "initial-compound-buffer", description = "Number of compounds that will be loaded initially into the Memory. A larger buffer ensures that there are enough compounds available to use all cores efficiently during computation. A smaller buffer saves Memory. To load all compounds immediately set it to 0. Default: 2 * --cores", defaultToNull = true)
    Integer getMinInstanceBuffer();

    ///// some hidden parameters

    @Option(longName = "disable-element-detection", hidden = true)
    public boolean isDisableElementDetection();

    @Option(longName = "enable-silicon-detection", hidden = true)
    public boolean isEnableSiliconDetection();

    @Option(
            longName = {"isolation-window-width"},
            description = "width of the isolation window to measure MS2",
            defaultToNull = true,
            hidden = true
    )
    Double getIsolationWindowWidth();

    @Option(
            longName = {"isolation-window-shift"},
            description = "The shift applied to the isolation window to measure MS2 in relation to the precursormass",
            defaultValue = "0",
            hidden = true
    )
    double getIsolationWindowShift();

    @Option(
            longName = {"assess-data-quality"},
            description = "produce stats on quality of spectra and estimate isolation window. Needs to read all data at once.",
            hidden = true
    )
    boolean isAssessDataQuality();



    /**
     * Just to artificially separate list parameters from rest parameters -_-
     */
    @Option(hidden = true)
    public boolean isPlaceholder();

}

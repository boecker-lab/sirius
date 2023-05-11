/*
 *
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schilller University.
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

package de.unijena.bioinf.ms.frontend.subtools.fingerprinter;

import de.unijena.bioinf.ms.frontend.subtools.Provide;
import de.unijena.bioinf.ms.frontend.subtools.RootOptions;
import de.unijena.bioinf.ms.frontend.subtools.StandaloneTool;
import de.unijena.bioinf.ms.properties.ParameterConfig;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "fingerprinter", aliases = {"FP"}, description = "Compute SIRIUS compatible fingerprints from PubChem standardized SMILES in csv format.",  versionProvider = Provide.Versions.class, mixinStandardHelpOptions = true, showDefaultValues = true)
public class FingerprinterOptions implements StandaloneTool<FingerprinterWorkflow> {

    @CommandLine.Option(names = {"--output", "-o"}, description = "Specify output csv file.", required = true)
    private Path outputPath;

    @Override
    public FingerprinterWorkflow makeWorkflow(RootOptions<?, ?, ?, ?> rootOptions, ParameterConfig config) {
        return new FingerprinterWorkflow(rootOptions, outputPath);
    }
}
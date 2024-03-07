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

package de.unijena.bioinf.projectspace;

import de.unijena.bioinf.ChemistryBase.ms.Ms2Experiment;
import de.unijena.bioinf.babelms.ms.CsvParser;
import de.unijena.bioinf.jjobs.JobProgressMerger;
import de.unijena.bioinf.ms.frontend.subtools.InputFilesOptions;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CsvMS2ExpIterator implements InstIterProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CsvMS2ExpIterator.class);

    private final Iterator<InputFilesOptions.CsvInput> basIter;
    private final CsvParser parer = new CsvParser();
    private final Predicate<Ms2Experiment> filter;
    private final JobProgressMerger prog;

    private Ms2Experiment next = null;

    public CsvMS2ExpIterator(List<InputFilesOptions.CsvInput> basIter, Predicate<Ms2Experiment> filter, @Nullable JobProgressMerger prog) {
        this(basIter.iterator(), filter, prog);
    }

    public CsvMS2ExpIterator(Iterator<InputFilesOptions.CsvInput> basIter, Predicate<Ms2Experiment> filter, @Nullable JobProgressMerger prog) {
        this.basIter = basIter;
        this.filter = filter;
        this.prog = prog;
    }

    @Override
    public boolean hasNext() {
        if (next != null)
            return true;

        if (basIter.hasNext()) {
            final InputFilesOptions.CsvInput csvInput = basIter.next();
            final Ms2Experiment exp = parer.parseSpectra(csvInput.ms1, csvInput.ms2, csvInput.parentMz, csvInput.ionType, csvInput.formula);
            prog.increaseProgress(csvInput, csvInput.ms2.size(), "Read " + exp.getName() + "..."); //todo count spectra
            if (!filter.test(exp)) {
                LOG.info("Skipping instance (CSV)" + csvInput.ms2.stream().map(File::getAbsolutePath).collect(Collectors.joining(",")) + " because it does not match the Filter criterion.");
                return hasNext();
            } else {
                next = exp;
                return true;
            }
        }
        return false;
    }

    @Override
    public Ms2Experiment next() {
        try {
            if (!hasNext())
                return null;
            return next;
        } finally {
            next = null;
        }
    }
}

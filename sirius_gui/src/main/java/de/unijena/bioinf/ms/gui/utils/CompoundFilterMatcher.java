package de.unijena.bioinf.ms.gui.utils;/*
 *
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2021 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman and Sebastian Böcker,
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
 *  You should have received a copy of the GNU General Public License along with SIRIUS. If not, see <https://www.gnu.org/licenses/lgpl-3.0.txt>
 */

import ca.odell.glazedlists.matchers.Matcher;
import de.unijena.bioinf.ChemistryBase.chem.RetentionTime;
import de.unijena.bioinf.ChemistryBase.ms.lcms.CoelutingTraceSet;
import de.unijena.bioinf.ChemistryBase.ms.lcms.LCMSPeakInformation;
import de.unijena.bioinf.lcms.LCMSCompoundSummary;
import de.unijena.bioinf.projectspace.CompoundContainer;
import de.unijena.bioinf.projectspace.InstanceBean;

import java.util.Optional;

public class CompoundFilterMatcher implements Matcher<InstanceBean> {
    final CompoundFilterModel filterModel;

    public CompoundFilterMatcher(CompoundFilterModel filterModel) {
        this.filterModel = filterModel;
    }

    @Override
    public boolean matches(InstanceBean item) {
        double mz = item.getIonMass();
        double rt = item.getID().getRt().map(RetentionTime::getRetentionTimeInSeconds).orElse(Double.NaN);
        if ((mz < filterModel.getCurrentMinMz()) ||
                (filterModel.isMaxMzFilterActive() && mz > filterModel.getCurrentMaxMz())) {
            return false;
        }
        if (!Double.isNaN(rt)) {
            if ((rt < filterModel.getCurrentMinRt()) ||
                    (filterModel.isMaxRtFilterActive() && rt > filterModel.getCurrentMaxRt())) {
                return false;
            }
        }
        if (filterModel.isPeakShapeFilterEnabled()) {
            return filterByPeakShape(item, filterModel);
        }
        return true;

    }

    private boolean filterByPeakShape(InstanceBean item, CompoundFilterModel filterModel) {
        final CompoundContainer compoundContainer = item.loadCompoundContainer(LCMSPeakInformation.class);
        final Optional<LCMSPeakInformation> annotation = compoundContainer.getAnnotation(LCMSPeakInformation.class);
        if (annotation.isEmpty()) return false;
        final LCMSPeakInformation lcmsPeakInformation = annotation.get();
        for (int k=0; k < lcmsPeakInformation.length(); ++k) {
            final Optional<CoelutingTraceSet> tracesFor = lcmsPeakInformation.getTracesFor(k);
            if (tracesFor.isPresent()) {
                final CoelutingTraceSet coelutingTraceSet = tracesFor.get();
                LCMSCompoundSummary sum = new LCMSCompoundSummary(coelutingTraceSet,coelutingTraceSet.getIonTrace(), item.getExperiment());
                if (filterModel.getPeakShapeQuality(sum.peakQuality)) {
                    return true;
                }
            }
        }
        return false;
    }
}

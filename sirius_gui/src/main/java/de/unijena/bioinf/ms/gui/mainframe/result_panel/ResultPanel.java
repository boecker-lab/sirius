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

package de.unijena.bioinf.ms.gui.mainframe.result_panel;

import de.unijena.bioinf.ms.gui.SiriusGui;
import de.unijena.bioinf.ms.gui.canopus.compound_classes.CompoundClassBean;
import de.unijena.bioinf.ms.gui.canopus.compound_classes.CompoundClassList;
import de.unijena.bioinf.ms.gui.fingerid.StructureList;
import de.unijena.bioinf.ms.gui.fingerid.fingerprints.FingerprintTable;
import de.unijena.bioinf.ms.gui.mainframe.instance_panel.CompoundList;
import de.unijena.bioinf.ms.gui.mainframe.result_panel.tabs.*;
import de.unijena.bioinf.ms.gui.molecular_formular.FormulaList;
import de.unijena.bioinf.ms.gui.molecular_formular.FormulaListHeaderPanel;
import de.unijena.bioinf.ms.nightsky.sdk.model.CanopusPrediction;
import de.unijena.bioinf.ms.properties.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ResultPanel extends JTabbedPane {

    protected static final Logger logger = LoggerFactory.getLogger(ResultPanel.class);

    public final SpectralMatchingPanel spectralMatchingPanel;

    public final FormulaOverviewPanel formulasTab;
    public final TreeVisualizationPanel treeTab;
    public final SpectraVisualizationPanel spectrumTab;

//    public final LCMSViewerPanel lcmsTab;

    public final CandidateListDetailViewPanel structuresTab;
    public final EpimetheusPanel structureAnnoTab;
    public final FingerprintPanel fpTab;
    public final CompoundClassPanel canopusTab;

    public ResultPanel(final StructureList structureList, final FormulaList siriusResultElements, final CompoundList compoundList, SiriusGui gui) {
        super();
        this.setToolTipText("Results");

        if (PropertyManager.getBoolean("de.unijena.bioinf.sirius.spectralAlignment.show", false)) {
            spectralMatchingPanel = new SpectralMatchingPanel(compoundList);
        } else {
            spectralMatchingPanel = null;
        }

        formulasTab = new FormulaOverviewPanel(siriusResultElements);
        treeTab = new TreeVisualizationPanel();
        spectrumTab = new SpectraVisualizationPanel();

//        this.lcmsTab = new LCMSViewerPanel(siriusResultElements); //todo LCMS: reactivate if LCMS Data structures are done!

        structureAnnoTab = new EpimetheusPanel(structureList);
        structuresTab = new CandidateListDetailViewPanel(this, compoundList, structureList, gui);
        FingerprintPanel fpTabTmp;
        try {
            fpTabTmp = new FingerprintPanel(new FingerprintTable(siriusResultElements, gui));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            fpTabTmp = null;
        }

        fpTab = fpTabTmp;
        canopusTab = new CompoundClassPanel(
                new CompoundClassList(siriusResultElements, sre ->
                        sre.getCanopusPrediction()
                                .stream().map(CanopusPrediction::getClassyFireClasses).filter(Objects::nonNull)
                                .flatMap(List::stream).map(CompoundClassBean::new).toList()), siriusResultElements
        );


//        addTab("LC-MS", null, lcmsTab, lcmsTab.getDescription());

        addTab("Formulas", null, formulasTab, formulasTab.getDescription());
        addTab("Spectra", null, new FormulaListHeaderPanel(siriusResultElements, spectrumTab), spectrumTab.getDescription());
        addTab("Trees", null, new FormulaListHeaderPanel(siriusResultElements, treeTab), treeTab.getDescription());

        addTab("Predicted Fingerprint", null, new FormulaListHeaderPanel(siriusResultElements, fpTab), fpTab.getDescription());

        addTab("Structures", null, new FormulaListHeaderPanel(siriusResultElements, structuresTab), structuresTab.getDescription());
        addTab("Substructure Annotation", null, structureAnnoTab, structureAnnoTab.getDescription());

        addTab("Compound Classes", null, new FormulaListHeaderPanel(siriusResultElements, canopusTab), canopusTab.getDescription());

        if (spectralMatchingPanel != null) {
            addTab("Reference Spectra", null, spectralMatchingPanel, spectralMatchingPanel.getDescription());
        }

        setSelectedIndex(1);
    }
}

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

package de.unijena.bioinf.ms.gui.utils;

import de.unijena.bioinf.ChemistryBase.chem.MolecularFormula;
import de.unijena.bioinf.ChemistryBase.chem.PrecursorIonType;
import de.unijena.bioinf.ChemistryBase.ms.Ms2Experiment;
import de.unijena.bioinf.projectspace.InstanceBean;
import de.unijena.bioinf.projectspace.MsExperiments2;

import javax.swing.*;

//todo property change support so that other views can listen to changes input data before applying them
public class ExperimentEditPanel extends JPanel {
    public final PrecursorSelector precursorSelection;
    public final PrecursorIonTypeSelector ionTypeCB;
    public final JTextField nameTF;
    public final JTextField formulaTF;

    public ExperimentEditPanel(boolean enablePrecursorSelection) {
        RelativeLayout rl = new RelativeLayout(RelativeLayout.X_AXIS, 15);
        rl.setAlignment(RelativeLayout.LEADING);
        setLayout(rl);

        nameTF = new JTextField(12);
        add(new TextHeaderBoxPanel("Name", nameTF));


        if (enablePrecursorSelection) {
            precursorSelection = new PrecursorSelector();
            add(new TextHeaderBoxPanel(PrecursorSelector.name, precursorSelection));
        }else {
            precursorSelection = null;
        }

        ionTypeCB = new PrecursorIonTypeSelector();
        add(new TextHeaderBoxPanel(PrecursorIonTypeSelector.name, ionTypeCB));

        formulaTF = new JTextField(12);
        add(new TextHeaderBoxPanel("Molecular Formula", formulaTF));
    }

    public double getSelectedIonMass() {
        return precursorSelection == null ? Double.NaN :  precursorSelection.getSelectedIonMass();
    }

    public boolean setSelectedIonMass(double ionMass) {
        if (precursorSelection == null)
            return false;
        return precursorSelection.setSelectedItem(ionMass);
    }

    public String getExperiementName() {
        return nameTF.getText();
    }

    public PrecursorIonType getSelectedAdduct() {
       return ionTypeCB.getSelectedAdductOrDefault();
    }

    public void setData(InstanceBean ec) {
        nameTF.setText(ec.getName());
        ionTypeCB.setSelectedItem(ec.getIonType().toString());
        setMolecularFomula(ec.getExperiment());
        if (precursorSelection != null){
            precursorSelection.setData(ec.getMs1Spectra(), ec.getMs2Spectra(), ec.getIonMass());
        }
    }

    public void setMolecularFomula(Ms2Experiment ec) {
        String formulaString = MsExperiments2.extractMolecularFormulaString(ec);
        formulaTF.setText(formulaString);
    }

    public boolean validateFormula() {
        String formulaString = formulaTF.getText();
        if (formulaString == null || formulaString.isEmpty())
            return true;
        MolecularFormula mf = MolecularFormula.parseOrNull(formulaString);

        return mf != null && !mf.equals(MolecularFormula.emptyFormula());
    }

    public MolecularFormula getMolecularFormula() {
        String formulaString = formulaTF.getText();
        if (formulaString == null || formulaString.isEmpty())
            return null;
        return MolecularFormula.parseOrNull(formulaString);
    }
}

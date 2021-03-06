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

package de.unijena.bioinf.ms.gui.actions;
/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com)
 * as part of the sirius_frontend
 * 29.01.17.
 */

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.gui.configs.Icons;
import de.unijena.bioinf.ms.gui.mainframe.instance_panel.ExperimentListChangeListener;
import de.unijena.bioinf.projectspace.InstanceBean;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static de.unijena.bioinf.ms.gui.mainframe.MainFrame.MF;

/**
 * @author Markus Fleischauer (markus.fleischauer@gmail.com)
 */
public class CancelComputeAction extends AbstractAction {
    public CancelComputeAction() {
        super("Cancel Computation");
        putValue(Action.SMALL_ICON, Icons.CANCEL_16);
        putValue(Action.LARGE_ICON_KEY, Icons.CANCEL_32);
        putValue(Action.SHORT_DESCRIPTION, "Cancel the running Computation(s) of the selected compound(s)");

        setEnabled(!MF.getCompoundListSelectionModel().isSelectionEmpty() && MF.getCompoundListSelectionModel().getSelected().get(0).isComputing());

        MF.getCompoundList().addChangeListener(new ExperimentListChangeListener() {
            @Override
            public void listChanged(ListEvent<InstanceBean> event, DefaultEventSelectionModel<InstanceBean> selection) {
                if (!selection.isSelectionEmpty()) {
                    for (InstanceBean ec : selection.getSelected()) {
                        if (ec != null && ec.isComputing()) {
                            setEnabled(true);
                            return;
                        }
                    }
                }
                setEnabled(false);
            }

            @Override
            public void listSelectionChanged(DefaultEventSelectionModel<InstanceBean> selection) {

            }
        });

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("NOT Implemented");
//        Jobs.cancel(MF.getCompoundListSelectionModel().getSelected());
    }


}

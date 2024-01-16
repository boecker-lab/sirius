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

package de.unijena.bioinf.ms.gui.molecular_formular;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import de.unijena.bioinf.ms.gui.table.*;
import de.unijena.bioinf.projectspace.FormulaResultBean;
import de.unijena.bioinf.projectspace.InstanceBean;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Markus Fleischauer
 */
public class FormulaListDetailView extends ActionListDetailView<FormulaResultBean, InstanceBean, FormulaList> {
    private final ActionTable<FormulaResultBean> table;
    private final ConnectedSelection<FormulaResultBean> selectionConnection; //this object synchronizes selection models and is not obsolete

    private SortedList<FormulaResultBean> sortedSource;
    private final SiriusResultTableFormat tableFormat;

    public FormulaListDetailView(final FormulaList source) {
        super(source);
        //todo dirty hack until search field bug is fixed
        getNorth().remove(searchField);
        tableFormat = new SiriusResultTableFormat(source.getBestFunc());

        table = new ActionTable<>(filteredSource, sortedSource, tableFormat);

        table.setSelectionModel(filteredSelectionModel);
        filteredSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //sync selections models
        selectionConnection = new ConnectedSelection<>(source.getElementListSelectionModel(), filteredSelectionModel, source.getElementList(), sortedSource);

        table.setDefaultRenderer(Object.class, new SiriusResultTableCellRenderer(tableFormat.highlightColumnIndex()));
        //todo re-enable threshold marker
//        table.getColumnModel().getColumn(3).setCellRenderer(new FingerIDScoreBarRenderer(tableFormat.highlightColumnIndex(), source.zodiacScoreStats, true));
        table.getColumnModel().getColumn(4).setCellRenderer(new ListStatBarTableCellRenderer<>(tableFormat.highlightColumnIndex(), source.zodiacScoreStats, true));
//        table.getColumnModel().getColumn(4).setCellRenderer(new FingerIDScoreBarRenderer(tableFormat.highlightColumnIndex(), source.siriusScoreStats, true));
        table.getColumnModel().getColumn(5).setCellRenderer(new ScoreListStatBarTableCellRenderer(tableFormat.highlightColumnIndex(), source.siriusScoreStats, true));
        table.getColumnModel().getColumn(6).setCellRenderer(new ListStatBarTableCellRenderer<>(tableFormat.highlightColumnIndex(), source.isotopeScoreStats, false));
        table.getColumnModel().getColumn(7).setCellRenderer(new ListStatBarTableCellRenderer<>(tableFormat.highlightColumnIndex(), source.treeScoreStats, false));
        table.getColumnModel().getColumn(8).setCellRenderer(new ListStatBarTableCellRenderer<>(tableFormat.highlightColumnIndex(), source.explainedPeaks, false, true, new DecimalFormat("#0")));
        table.getColumnModel().getColumn(9).setCellRenderer(new BarTableCellRenderer(tableFormat.highlightColumnIndex(), 0, 1, true));

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //todo insert compute CSI option by opening batch compute
                /*if (e.getClickCount() == 2) {
                    // Double-click detected
                    int index = table.rowAtPoint(e.getPoint());
                    table.setRowSelectionInterval(index, index);
                    SiriusActions.COMPUTE_CSI_LOCAL.getInstance().actionPerformed(new ActionEvent(table, 112, SiriusActions.COMPUTE_CSI_LOCAL.name()));
                }*/
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        //decorate this guy
        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
//        table.getInputMap().put(enterKey, SiriusActions.COMPUTE_CSI_LOCAL.name());
//        table.getActionMap().put(SiriusActions.COMPUTE_CSI_LOCAL.name(), SiriusActions.COMPUTE_CSI_LOCAL.getInstance());

        this.add(
                new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER
        );
    }

    @Override
    protected JToolBar getToolBar() {
        return null;
    }

    @Override
    protected EventList<MatcherEditor<FormulaResultBean>> getSearchFieldMatchers() {
        return GlazedLists.eventListOf(new StringMatcherEditor<>(tableFormat, searchField.textField));
    }

    @Override
    protected FilterList<FormulaResultBean> configureFiltering(EventList<FormulaResultBean> source) {
        sortedSource = new SortedList<>(source);
        return super.configureFiltering(sortedSource);
    }

    private class ConnectedSelection<T> {
        final DefaultEventSelectionModel<T> model1;
        final DefaultEventSelectionModel<T> model2;

        final EventList<T> model1List;
        final EventList<T> model2List;

        final Map<DefaultEventSelectionModel, ListSelectionListener> modelTListener = new HashMap<>();

        public ConnectedSelection(DefaultEventSelectionModel<T> model1, DefaultEventSelectionModel<T> model2, EventList<T> model1List, EventList<T> model2List) {
            this.model1 = model1;
            this.model2 = model2;

            this.model1List = model1List;
            this.model2List = model2List;
            addListeners();
        }

        public void addListeners() {
            modelTListener.put(model1, createAndAddListener(model1, model2, model2List));
            modelTListener.put(model2, createAndAddListener(model2, model1, model1List));
        }

        public void removeListeners() {
            model1.removeListSelectionListener(modelTListener.get(model1));
            model2.removeListSelectionListener(modelTListener.get(model2));
            modelTListener.clear();
        }

        private ListSelectionListener createAndAddListener(final DefaultEventSelectionModel<T> notifier, final DefaultEventSelectionModel<T> listener, final EventList<T> listenerList) {
            ListSelectionListener l = e -> {
                if (notifier.isSelectionEmpty()) {
                    if (!listener.isSelectionEmpty())
                        listener.clearSelection();
                    return;
                } else {
                    EventList<T> s1 = notifier.getSelected();
                    T s = s1.get(0);
                    if (!listener.isSelectionEmpty()) {
                        EventList<T> s2 = listener.getSelected();
                        if ((s1.size() == 1 || s2.size() == 1) && (s == s2.get(0))) {
                            return;
                        }
                    }

                    listener.removeListSelectionListener(modelTListener.get(listener));
                    int i = listenerList.indexOf(s);
                    listener.setSelectionInterval(i, i);
                    listener.addListSelectionListener(modelTListener.get(listener));
                }
            };
            notifier.addListSelectionListener(l);
            return l;
        }
    }
}

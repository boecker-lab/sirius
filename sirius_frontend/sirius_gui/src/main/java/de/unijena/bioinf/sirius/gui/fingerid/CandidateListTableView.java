package de.unijena.bioinf.sirius.gui.fingerid;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import de.unijena.bioinf.sirius.gui.structure.ExperimentContainer;
import de.unijena.bioinf.sirius.gui.structure.SiriusResultElement;
import de.unijena.bioinf.sirius.gui.table.*;
import de.unijena.bioinf.sirius.gui.utils.SearchTextField;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by fleisch on 15.05.17.
 */
public class CandidateListTableView extends CandidateListView implements ActiveElementChangedListener<SiriusResultElement, ExperimentContainer> {

    private final ActionTable<CompoundCandidate> table;
    private SortedList<CompoundCandidate> sortedSource;

    public CandidateListTableView(final CandidateList list) {
        super(list);

        final DefaultEventSelectionModel<CompoundCandidate> model = new DefaultEventSelectionModel<>(sortedSource);

        final CandidateTableFormat tf = new CandidateTableFormat();
        this.table = new ActionTable<>(filteredSource, sortedSource, tf);
        TableComparatorChooser.install(table, sortedSource, AbstractTableComparatorChooser.SINGLE_COLUMN);

        table.setSelectionModel(model);
        table.setDefaultRenderer(Object.class, new SiriusResultTableCellRenderer(tf.highlightColumn()));

//        table.getColumnModel().getColumn(2).setCellRenderer(new BarTableCellRenderer(true, true, source.scoreStats));
//        table.getColumnModel().getColumn(3).setCellRenderer(new BarTableCellRenderer(false, false, source.isotopeScoreStats));
//        table.getColumnModel().getColumn(4).setCellRenderer(new BarTableCellRenderer(false, false, source.treeScoreStats));

        /*table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click detected
                    int index = table.rowAtPoint(e.getPoint());
                    table.setRowSelectionInterval(index, index);
                    SiriusActions.COMPUTE_CSI_LOCAL.getInstance().actionPerformed(new ActionEvent(table, 112, SiriusActions.COMPUTE_CSI_LOCAL.name()));
                }
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
        });*/

        // todo decoration

        this.add(
                new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER
        );


    }

    @Override
    protected FilterList<CompoundCandidate> configureFiltering(EventList<CompoundCandidate> source) {
        sortedSource = new SortedList<>(source);
        return super.configureFiltering(sortedSource);
    }

    @Override
    public void resultsChanged(ExperimentContainer experiment, SiriusResultElement sre, List<SiriusResultElement> resultElements, ListSelectionModel selections) {
        //todo do something
    }
}

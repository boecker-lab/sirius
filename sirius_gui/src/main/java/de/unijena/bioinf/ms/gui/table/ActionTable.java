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

package de.unijena.bioinf.ms.gui.table;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import de.unijena.bioinf.ms.frontend.core.SiriusPCS;

import javax.swing.*;

/**
 * @author Markus Fleischauer
 */
public class ActionTable<T extends SiriusPCS> extends JTable {
    public final TableComparatorChooser<T> comparatorChooser;


    public ActionTable(SortedList<T> sorted, TableFormat<T> format, EventList<MatcherEditor<T>> matcher) {
        this(new FilterList<T>(sorted, new CompositeMatcherEditor<>(matcher)), format);
    }

    public ActionTable(FilterList<T> filtered, TableFormat<T> format) {
        this(filtered, new SortedList<>(filtered), format);
    }

    public ActionTable(FilterList<T> filtered, SortedList<T> sorted, TableFormat<T> format) {
        setModel(new DefaultEventTableModel<>(filtered, format));
        comparatorChooser = TableComparatorChooser.install(this, sorted, AbstractTableComparatorChooser.SINGLE_COLUMN);
    }
}

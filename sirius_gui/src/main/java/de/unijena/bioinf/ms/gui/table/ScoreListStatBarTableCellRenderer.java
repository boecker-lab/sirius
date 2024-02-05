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

import de.unijena.bioinf.ms.gui.molecular_formular.FormulaScoreListStats;

import javax.swing.*;
import java.text.NumberFormat;


public class ScoreListStatBarTableCellRenderer extends ListStatBarTableCellRenderer<FormulaScoreListStats> {
//    private Function<FormulaScoreListStats, Double> thresholder = (stats) -> Math.max(stats.getMax(), 0) - Math.max(5, stats.getMax() * 0.25);

    public ScoreListStatBarTableCellRenderer(FormulaScoreListStats stats) {
        super(stats);
    }

    public ScoreListStatBarTableCellRenderer(FormulaScoreListStats stats, boolean percentage) {
        super(stats, percentage);
    }

    public ScoreListStatBarTableCellRenderer(int highlightColumn, FormulaScoreListStats stats, boolean percentage) {
        super(highlightColumn, stats, percentage);
    }

    public ScoreListStatBarTableCellRenderer(int highlightColumn, FormulaScoreListStats stats, boolean percentage, boolean printMaxValue, NumberFormat lableFormat) {
        super(highlightColumn, stats, percentage, printMaxValue, lableFormat);
    }

   /* @Override
    protected double getThresh(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        System.out.println("Reimplement threshold separator line computation"); //todo threshold
        double v = Math.exp(thresholder.apply(stats) - stats.getMax());
        System.out.println("Thresh:" + v);
        return v;
    }
*/
    @Override
    protected double getMax(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        return stats.getExpMaxScore();
    }

    @Override
    protected double getMin(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        return stats.getExpMinScore();
    }

    @Override
    protected double getSum(JTable table, boolean isSelected, boolean hasFocus, int row, int column) {
        return stats.getExpScoreSum();
    }

    @Override
    protected Double getValue(Object value) {
        return Math.exp(super.getValue(value) - stats.getMax());
    }
}

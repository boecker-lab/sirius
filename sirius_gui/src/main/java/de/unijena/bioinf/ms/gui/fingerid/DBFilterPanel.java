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

package de.unijena.bioinf.ms.gui.fingerid;

import de.unijena.bioinf.chemdb.DataSource;
import de.unijena.bioinf.chemdb.custom.CustomDataSources;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.gui.table.ActiveElementChangedListener;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.gui.utils.WrapLayout;
import de.unijena.bioinf.projectspace.InstanceBean;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class DBFilterPanel extends JPanel implements ActiveElementChangedListener<FingerprintCandidateBean, InstanceBean>, CustomDataSources.DataSourceChangeListener {
    public final static Set<String> BLACK_LIST = Set.of(/*DataSource.ADDITIONAL.realName,*/ DataSource.ALL.realName, DataSource.ALL_BUT_INSILICO.realName,
            DataSource.PUBCHEMANNOTATIONBIO.realName, DataSource.PUBCHEMANNOTATIONDRUG.realName, DataSource.PUBCHEMANNOTATIONFOOD.realName, DataSource.PUBCHEMANNOTATIONSAFETYANDTOXIC.realName,
            DataSource.SUPERNATURAL.realName
    );

    private final Queue<FilterChangeListener> listeners = new ConcurrentLinkedQueue<>();

    protected long bitSet;
    protected List<JCheckBox> checkboxes;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);


    public DBFilterPanel(StructureList sourceList) {
        setLayout(new WrapLayout(FlowLayout.LEFT, 5, 1));
        setBorder(BorderFactory.createEmptyBorder(0, 0, GuiUtils.SMALL_GAP, 0));
        this.checkboxes = new ArrayList<>(CustomDataSources.size());
        for (CustomDataSources.Source source : CustomDataSources.sources()) {
            if (!BLACK_LIST.contains(source.name()))
                checkboxes.add(new JCheckBox(source.name()));
        }
        addBoxes();
        CustomDataSources.addListener(this);
        sourceList.addActiveResultChangedListener(this);
    }

    public void addFilterChangeListener(FilterChangeListener listener) {
        listeners.add(listener);
    }

    public void fireFilterChangeEvent() {
        listeners.forEach(l -> l.fireFilterChanged(bitSet));
    }

    protected void addBoxes() {
        checkboxes.sort(Comparator.comparing(o -> o.getText().toUpperCase()));

        this.bitSet = 0L;
        for (final JCheckBox box : checkboxes) {
            if (box.isSelected())
                this.bitSet |= CustomDataSources.getSourceFromName(box.getText()).flag();
            add(box);
            box.addChangeListener(e -> {
                if (!isRefreshing.get()) {
                    if (box.isSelected())
                        bitSet |= CustomDataSources.getSourceFromName(box.getText()).flag();
                    else
                        bitSet &= ~CustomDataSources.getSourceFromName(box.getText()).flag();
                    fireFilterChangeEvent();
                }
            });
        }
    }

    protected void reset() {
        isRefreshing.set(true);
        bitSet = 0;
        try {
            for (JCheckBox checkbox : checkboxes) {
                checkbox.setSelected(false);
            }
        } finally {
            fireFilterChangeEvent();
            isRefreshing.set(false);
        }
    }

    public boolean toggle() {
        setVisible(!isVisible());
        return isVisible();
    }

    @Override
    public void resultsChanged(InstanceBean datas, FingerprintCandidateBean sre, List<FingerprintCandidateBean> resultElements, ListSelectionModel selections) {
        reset();
    }

    @Override
    public void fireDataSourceChanged(Collection<String> changes) {
        Jobs.runEDTLater(() -> {
            HashSet<String> changed = new HashSet<>(changes);
            isRefreshing.set(true);
            boolean c = false;
            Iterator<JCheckBox> it = checkboxes.iterator();

            while (it.hasNext()) {
                JCheckBox checkbox = it.next();
                if (changed.remove(checkbox.getText())) {
                    it.remove();
                    c = true;
                }
            }

            for (String name : changed) {
                checkboxes.add(new JCheckBox(name));
                c = true;
            }

            if (c) {
                removeAll();
                addBoxes();
                revalidate();
                repaint();
                fireFilterChangeEvent();
            }


            isRefreshing.set(false);
        });
    }

    public interface FilterChangeListener extends EventListener {
        void fireFilterChanged(long filterSet);
    }
}

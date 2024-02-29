/*
 *  Copyright (C) 2011 Naveed Quadri
 *  naveedmurtuza@gmail.com
 *  www.naveedmurtuza.blogspot.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package de.unijena.bioinf.ms.gui.utils.jCheckboxList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.function.BiFunction;

/**
 * An implementation of JCheckboxList, a JList with checkboxes
 *
 * @author Naveed Quadri
 */
public class JCheckBoxList<E> extends JList<CheckBoxListItem<E>> {


    private final MouseAdapter mouseAdapter;
    private final BiFunction<E, E, Boolean> equals;

    public JCheckBoxList(java.util.List<E> listElements, @NotNull BiFunction<E, E, Boolean> equals) {
        this(equals);
        DefaultListModel<CheckBoxListItem<E>> m = (DefaultListModel<CheckBoxListItem<E>>) getModel();
        for (E listElement : listElements) {
            m.addElement(new CheckBoxListItem<>(listElement, false));
        }
    }

    public JCheckBoxList() {
        this(Object::equals);
    }

    public JCheckBoxList(@NotNull BiFunction<E, E, Boolean> equals) {
        super();
        this.equals = equals;
        setModel(new DefaultListModel<>());
        setCellRenderer(new CheckboxCellRenderer());

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                Rectangle bounds = getCellBounds(index, index);
                if (index != -1) {
                    JCheckBox checkbox = getModel().getElementAt(index);
                    if (checkbox != null && checkbox.isEnabled()) {
                        //check if the click is on checkbox (including the label)
                        boolean inCheckbox = getComponentOrientation().isLeftToRight() ? e.getX() < bounds.x + checkbox.getPreferredSize().getWidth() : e.getX() > bounds.x + checkbox.getPreferredSize().getWidth();
                        //change the state of the checkbox on double click or if the click is on checkbox (including the label)
                        if (e.getClickCount() >= 2 || inCheckbox) {
                            checkbox.setSelected(!checkbox.isSelected());
                            fireSelectionValueChanged(index, index, inCheckbox);
                        }
                        repaint();
                    }
                }
            }
        };
        addMouseListener(mouseAdapter);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Gets Checked Items
     *
     * @return Checked Items
     */
    public ArrayList<E> getCheckedItems() {
        ArrayList<E> list = new ArrayList<>();
        Enumeration<CheckBoxListItem<E>> dlm = ((DefaultListModel<CheckBoxListItem<E>>) getModel()).elements();

        while (dlm.hasMoreElements()) {
            CheckBoxListItem<E> checkboxListItem = dlm.nextElement();
            if (checkboxListItem.isSelected()) {
                list.add(checkboxListItem.getValue());
            }
        }

        return list;
    }

    /**
     * Check all Items
     */
    private void setAllItemsChecked(boolean checked) {
        Enumeration<CheckBoxListItem<E>> dlm = ((DefaultListModel<CheckBoxListItem<E>>) getModel()).elements();

        while (dlm.hasMoreElements()) {
            CheckBoxListItem<E> checkboxListItem = dlm.nextElement();
            checkboxListItem.setSelected(checked);
        }
    }

    public void refresh() {
        fireSelectionValueChanged(getSelectionModel().getMinSelectionIndex(), getSelectionModel().getMaxSelectionIndex(), false);
        revalidate();
        repaint();
    }

    public void checkAll() {
        setAllItemsChecked(true);
        refresh();
    }

    public void uncheckAll() {
        setAllItemsChecked(false);
        refresh();
    }

    private void setItemChecked(E item, boolean checked) {
        CheckBoxListItem<E> checkboxListItem = getCheckboxListItem(item);
        if (checkboxListItem != null) {
            checkboxListItem.setSelected(checked);
        }
    }

    @Nullable
    private CheckBoxListItem<E> getCheckboxListItem(E item) {
        Enumeration<CheckBoxListItem<E>> dlm = ((DefaultListModel<CheckBoxListItem<E>>) getModel()).elements();

        while (dlm.hasMoreElements()) {
            CheckBoxListItem<E> checkboxListItem = dlm.nextElement();
            if (equals.apply(checkboxListItem.getValue(), item)) {
                return checkboxListItem;
            }
        }
        return null;
    }

    public void setItemEnabled(E item, boolean enabled) {
        CheckBoxListItem<E> checkboxListItem = getCheckboxListItem(item);
        if (checkboxListItem != null) {
            checkboxListItem.setEnabled(enabled);
        }
        revalidate();
        repaint();
    }

    public void setItemToolTip(E item, String tooltip) {
        CheckBoxListItem<E> checkboxListItem = getCheckboxListItem(item);
        if (checkboxListItem != null) {
            checkboxListItem.setToolTipText(tooltip);
        }
    }

    public void check(E item) {
        setItemChecked(item, true);
        refresh();
    }

    public void uncheck(E item) {
        setItemChecked(item, false);
        refresh();
    }

    public void checkAll(Collection<E> items) {
        for (E item : items) {
            setItemChecked(item, true);
        }
        refresh();
    }

    public void uncheckAll(Collection<E> items) {
        for (E item : items) {
            setItemChecked(item, false);
        }
        refresh();
    }

    public void replaceElements(Iterable<E> nuElements) {
        DefaultListModel<CheckBoxListItem<E>> m = (DefaultListModel<CheckBoxListItem<E>>) getModel();
        m.removeAllElements();
        for (E element : nuElements) {
            m.addElement(CheckBoxListItem.getNew(element));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        final Enumeration<CheckBoxListItem<E>> dlm = ((DefaultListModel<CheckBoxListItem<E>>) getModel()).elements();

        while (dlm.hasMoreElements()) {
            dlm.nextElement().setEnabled(enabled);
        }

        setMouseListenerEnabled(enabled);
    }


    private void setMouseListenerEnabled(boolean enabled) {
        if (enabled) {
            if (!Arrays.asList(getMouseListeners()).contains(mouseAdapter))
                addMouseListener(mouseAdapter);
        } else {
            removeMouseListener(mouseAdapter);
        }
    }
}

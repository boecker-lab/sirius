/*
 *
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer and Sebastian Böcker,
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
 *  You should have received a copy of the GNU Lesser General Public License along with SIRIUS. If not, see <https://www.gnu.org/licenses/lgpl-3.0.txt>
 */

package de.unijena.bioinf.ms.gui.mainframe;

import de.unijena.bioinf.ms.gui.net.ConnectionMonitor;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.nightsky.sdk.model.ConnectionCheck;
import de.unijena.bioinf.ms.nightsky.sdk.model.Subscription;
import de.unijena.bioinf.ms.nightsky.sdk.model.SubscriptionConsumables;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

public class WebServiceInfoPanel extends JToolBar implements PropertyChangeListener {
    private static final String INF = Character.toString('\u221E');
    private final JLabel license;
    private final JLabel consumedCompounds;
    //    private final JLabel connected = new JLabel("Connected: ?");
    private final JLabel pendingJobs;

    public WebServiceInfoPanel(ConnectionMonitor monitor) {
        super("Web service info");
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setPreferredSize(new Dimension(getPreferredSize().width, 16));
        setFloatable(false);

        license = new JLabel("License: ?");
        license.setToolTipText("Web service license information.");
        consumedCompounds = new JLabel("Compounds: 'UNLIMITED'");
        consumedCompounds.setToolTipText(GuiUtils.formatToolTip("Consumed compounds in billing period. (If subscription is compound based)"));
        pendingJobs = new JLabel("Jobs: ?");
        pendingJobs.setToolTipText("Number of pending jobs on web server.");

        add(license);
        add(Box.createGlue());
        add(consumedCompounds);
        add(Box.createGlue());
        add(pendingJobs);
        monitor.addConnectionUpdateListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final ConnectionMonitor.ConnectionUpdateEvent cevt = (ConnectionMonitor.ConnectionUpdateEvent) evt;
        ConnectionCheck check = cevt.getConnectionCheck();

        if (check.getLicenseInfo().getSubscription() != null) {
            @Nullable Subscription sub = check.getLicenseInfo().getSubscription();
            license.setText("<html>License: <b>" + sub.getSubscriberName() + "</b>" + (check.getLicenseInfo().getSubscription() == null ? "" : " (" + check.getLicenseInfo().getSubscription().getName() + ")</html>"));
            if (sub.isCountQueries()) {
                final boolean hasLimit = sub.getInstanceLimit() != null && sub.getInstanceLimit() > 0;
                String max = hasLimit ? String.valueOf(sub.getInstanceLimit()) : INF;
                final int consumed = Optional.ofNullable(check.getLicenseInfo().getConsumables())
                        .map(SubscriptionConsumables::getCountedCompounds).orElse(-1);

                final String current = consumed < 0 ? "N/A" : String.valueOf(consumed);
                consumedCompounds.setText("<html>Compounds: <b>" + current + "/" + max + "</b> (per " + (hasLimit ? "Year" : "Month") + ")</html>");
            } else {
                consumedCompounds.setText("<html>Compounds: <b>UNLIMITED</b></html>");
            }
        } else {
            license.setText("License: '?'");
            consumedCompounds.setText("Compounds: '?'");
        }

        if (check.getWorkerInfo() != null) {
            pendingJobs.setText("<html>Jobs: <b>" + check.getWorkerInfo().getPendingJobs() + "</b></html>");
        } else {
            pendingJobs.setText("Jobs: ?");
        }

        revalidate();
        repaint();
    }
}

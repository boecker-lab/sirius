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

package de.unijena.bioinf.ms.gui.canopus.compound_classes;

import de.unijena.bioinf.ChemistryBase.fp.*;
import de.unijena.bioinf.canopus.CanopusResult;
import de.unijena.bioinf.jjobs.JJob;
import de.unijena.bioinf.jjobs.TinyBackgroundJJob;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.gui.configs.Colors;
import de.unijena.bioinf.ms.gui.configs.Fonts;
import de.unijena.bioinf.ms.gui.molecular_formular.FormulaList;
import de.unijena.bioinf.ms.gui.table.ActiveElementChangedListener;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.gui.utils.TextHeaderBoxPanel;
import de.unijena.bioinf.projectspace.FormulaResultBean;
import de.unijena.bioinf.projectspace.InstanceBean;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.jdesktop.swingx.WrapLayout;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CompoundClassDetailView extends JPanel implements ActiveElementChangedListener<FormulaResultBean, InstanceBean>  {

    protected static String[] typeNames = new String[]{
            "Kingdom", "Superclass", "Class", "Subclass"
    };

    private final JScrollPane containerSP;
    protected ClassyfireProperty mainClass;
    protected ClassyfireProperty[] lineage;
    protected NPCFingerprintVersion.NPCProperty[] npcClasses = new NPCFingerprintVersion.NPCProperty[0];
    protected final TObjectDoubleHashMap<NPCFingerprintVersion.NPCProperty> npcProbabilities = new TObjectDoubleHashMap<>();
    protected List<ClassyfireProperty> secondaryClasses;
    protected ProbabilityFingerprint canopusFp, npcFp;

    protected JPanel mainClassPanel, descriptionPanel, alternativeClassPanels, npcPanel;

    protected JPanel container;

    public CompoundClassDetailView(FormulaList siriusResultElements) {
        super();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setAlignmentX(0f);
        mainClassPanel = new JPanel();
        mainClassPanel.setAlignmentX(0f);
        mainClassPanel.setLayout(new WrapLayout(WrapLayout.LEFT));

        descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.setAlignmentX(0f);

        alternativeClassPanels = new JPanel();
        alternativeClassPanels.setLayout(new WrapLayout(WrapLayout.LEFT));
        alternativeClassPanels.setAlignmentX(0f);

        npcPanel = new JPanel();
        npcPanel.setLayout(new WrapLayout(WrapLayout.LEFT));
        npcPanel.setAlignmentX(0f);

        container = new JPanel();
        container.setAlignmentX(0);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        containerSP = new JScrollPane(container, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(containerSP);

        siriusResultElements.addActiveResultChangedListener(this);

    }

    public void updateContainer() {
        container.removeAll();
        if (mainClass!=null) {
            // add lineage
            mainClassPanel.removeAll();
            for (int k=0; k < lineage.length; ++k) {
                int level = lineage.length - k;
                String tpname = (level <= typeNames.length) ? typeNames[level - 1] : ("Level " + level);
                mainClassPanel.add(new ClassifClass(lineage[k], tpname, k == 0, prob(lineage[k]) ));
                if (k < lineage.length - 1) {
                    final JLabel comp = new JLabel("\u27be");
                    comp.setFont(Fonts.FONT_BOLD.deriveFont(18f));
                    mainClassPanel.add(comp);
                }
            }


            {
                alternativeClassPanels.removeAll();
                // alternative classes
                for (int k = 0; k < secondaryClasses.size(); ++k) {
                    alternativeClassPanels.add(new ClassifClass(secondaryClasses.get(k), "alternative", false, prob(secondaryClasses.get(k))));
                }
            }

            // npc panel
            npcPanel.removeAll();
            if (npcClasses.length>0) {
                for (NPCFingerprintVersion.NPCProperty p : npcClasses) {
                    npcPanel.add(new NPClass(p,npcProbabilities.get(p)));
                }
            }

            {
                int width = Math.max(Math.max(mainClassPanel.getPreferredSize().width, alternativeClassPanels.getPreferredSize().width), npcPanel.getPreferredSize().width);
                descriptionPanel.removeAll();
                final JLabel comp = new JLabel();
                comp.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

                descriptionPanel.add(comp);
                comp.setText(GuiUtils.formatToolTip(width - 2, description()));
                descriptionPanel.setMaximumSize(new Dimension(width, descriptionPanel.getMaximumSize().height));
            }

            container.add(new TextHeaderBoxPanel("Main Classes", mainClassPanel));
            container.add(new TextHeaderBoxPanel("Description", descriptionPanel));
            container.add(new TextHeaderBoxPanel("Alternative Classes", alternativeClassPanels));
            //if (npcClasses.length>0)
                container.add(new TextHeaderBoxPanel("Natural Product Classes", npcPanel));


            revalidate();
            repaint();
            if (getParent() instanceof JSplitPane)
                ((JSplitPane)getParent()).setDividerLocation(getPreferredSize().height);
        }
    }

    private double prob(ClassyfireProperty p) {
        return canopusFp==null ? 0d : canopusFp.getProbability(getVersion(canopusFp, ClassyFireFingerprintVersion.class).getIndexOfMolecularProperty(p));
    }
    private double prob(NPCFingerprintVersion.NPCProperty p) {
        return npcFp==null ? 0d : npcFp.getProbability(p.npcIndex);
    }

    private <T extends FingerprintVersion> T getVersion(ProbabilityFingerprint fp, Class<T> vklass) {
        FingerprintVersion v = fp.getFingerprintVersion();
        if (v instanceof MaskedFingerprintVersion) v= ((MaskedFingerprintVersion) v).getMaskedFingerprintVersion();
        return (T)v;
    }

    private String description() {
        if (mainClass==null) return "This compound is not classified yet.";
        String m = mainClass.getDescription();
        return "This compound belongs to the class " + mainClass.getName() + ", which describes " + Character.toLowerCase(m.charAt(0)) + m.substring(1,m.length());
    }

    public void setPrediction(CanopusResult canopusResult) {
        final ProbabilityFingerprint classyfireFingerprint = canopusResult.getCanopusFingerprint();
        this.canopusFp = classyfireFingerprint;
        FingerprintVersion version = classyfireFingerprint.getFingerprintVersion();
        if (version instanceof MaskedFingerprintVersion) version = ((MaskedFingerprintVersion) version).getMaskedFingerprintVersion();
        if (!(version instanceof ClassyFireFingerprintVersion)) {
            LoggerFactory.getLogger(CompoundClassDetailView.class).error("Classyfire fingerprint has wrong version: " + version);
            clear();
            return;
        }
        ClassyFireFingerprintVersion classif = (ClassyFireFingerprintVersion)version;
        ClassyfireProperty main = classif.getPrimaryClass(classyfireFingerprint);
        Set<ClassyfireProperty> alternatives = new HashSet<>(Arrays.asList(classif.getPredictedLeafs(classyfireFingerprint, 0.5)));
        alternatives.remove(main);
        ClassyfireProperty[] anc = main.getAncestors();
        ClassyfireProperty[] lin = new ClassyfireProperty[anc.length];
        int k=0;
        lin[0] = main;
        // ignore chemical entities
        for (int j=0; j < anc.length-1; ++j) {
            lin[++k] = anc[j];
        }
        this.lineage = lin;
        this.mainClass = main;
        this.secondaryClasses = new ArrayList<>(alternatives);
        secondaryClasses.sort(Comparator.comparingInt(x->-x.getPriority()));

        // NPC classes
        final Optional<ProbabilityFingerprint> npcResult = canopusResult.getNpcFingerprint();
        final List<NPCFingerprintVersion.NPCProperty> predicted = new ArrayList<>();
        npcProbabilities.clear();
        if (npcResult.isPresent()) {
            this.npcFp=npcResult.get();
            for (FPIter x : npcResult.get().presentFingerprints()) {
                predicted.add((NPCFingerprintVersion.NPCProperty) x.getMolecularProperty());
                npcProbabilities.put((NPCFingerprintVersion.NPCProperty)x.getMolecularProperty(), x.getProbability());
            }
        } else this.npcFp = null;
        this.npcClasses = predicted.toArray(NPCFingerprintVersion.NPCProperty[]::new);
        Arrays.sort(npcClasses, (u,v)->v.npcIndex-u.npcIndex);
        updateContainer();
    }

    public void clear() {
        this.lineage = null;
        this.mainClass = null;
        this.secondaryClasses = null;
        updateContainer();
    }


    private JJob<Boolean> backgroundLoader = null;
    private final Lock backgroundLoaderLock = new ReentrantLock();

    @Override
    public void resultsChanged(InstanceBean experiment, FormulaResultBean sre, List<FormulaResultBean> resultElements, ListSelectionModel selections) {
        try {
            backgroundLoaderLock.lock();
            final JJob<Boolean> old = backgroundLoader;
            backgroundLoader = Jobs.runInBackground(new TinyBackgroundJJob<>() {
                @Override
                protected Boolean compute() throws Exception {
                    if (old != null && !old.isFinished()) {
                        old.cancel(false);
                        old.getResult(); //await cancellation so that nothing strange can happen.
                    }
                    Jobs.runEDTAndWait(() -> clear());
                    checkForInterruption();
                    if (sre != null) {
                        final Optional<CanopusResult> cs = sre.getCanopusResult();
                        checkForInterruption();
                        if (cs.isPresent())
                            Jobs.runEDTAndWait(() -> setPrediction(cs.get()));
                    }
                    return true;
                }
            });
        } finally {
            backgroundLoaderLock.unlock();
        }
    }

    protected static class ClassifClass extends JPanel implements MouseListener {

        protected final ClassyfireProperty klass;
        protected final String type;

        protected TextLayout typeName, className;
        protected Rectangle classBox, typeBox;

        protected Font typeFont, classFont;

        protected static final int PADDING = 4, SAFETY_DISTANCE = 4, GAP_TOP=4;
        protected boolean main;

        public ClassifClass(ClassyfireProperty klass, String type, boolean main, double probability) {
            super();
            setToolTipText("<html><p>Probability: <b>" + (int)(Math.round(probability*100)) + " %</b></p><p>" + klass.getDescription() + "</p>");
            this.main = main;
            this.klass = klass;
            this.type = type;
            typeFont = Fonts.FONT_BOLD.deriveFont(10f);
            typeName = new TextLayout(type, typeFont, new FontRenderContext(null, false, false));
            classFont = Fonts.FONT_BOLD.deriveFont(13f);
            className = new TextLayout(klass.getName(), classFont, new FontRenderContext(null, false, false));
            setOpaque(false);
            classBox = className.getPixelBounds(null,0,0);
            typeBox = typeName.getPixelBounds(null,0,0);
            setPreferredSize(new Dimension(Math.max(classBox.width,typeBox.width)+2*PADDING+SAFETY_DISTANCE, classBox.height+typeBox.height + 2*PADDING + SAFETY_DISTANCE+GAP_TOP));
            setMinimumSize(new Dimension(Math.max(classBox.width,typeBox.width)+2*PADDING+SAFETY_DISTANCE, classBox.height+typeBox.height + 2*PADDING + SAFETY_DISTANCE+GAP_TOP));
            addMouseListener(this);

        }

        @Override
        public void paintComponent(Graphics g_) {
            final Graphics2D g = (Graphics2D)g_;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            final Color color = main ? Colors.CLASSIFIER_MAIN : Colors.CLASSIFIER_OTHER;
            g.setColor(color);
            final int boxwidth = Math.max(classBox.width,typeBox.width)+2*PADDING;
            final int boxheight = classBox.height+2*PADDING+GAP_TOP;
            g.fillRoundRect(0, typeBox.height, boxwidth, boxheight, 4, 4);
            g.setColor(Colors.FOREGROUND);
            g.drawRoundRect(0, typeBox.height, boxwidth, boxheight, 4, 4);
            g.setColor(color);
            final int gap = (boxwidth-typeBox.width)/2;
            g.drawRect(gap, typeBox.height,typeBox.width,Math.min(typeBox.height,classBox.height));
            g.setFont(classFont);
            g.setColor(Colors.FOREGROUND);
            g.drawString(klass.getName(), PADDING, classBox.height+typeBox.height+PADDING+GAP_TOP);

            g.setFont(typeFont);
            g.setColor(Colors.FOREGROUND);
            g.drawString(type, gap, typeBox.height+GAP_TOP);

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                Desktop.getDesktop().browse(URI.create(String.format("http://classyfire.wishartlab.com/tax_nodes/C%07d",klass.getChemOntId())));
            } catch (IOException ex) {
                LoggerFactory.getLogger(CompoundClassDetailView.class).error("Failed to open webbrowser");
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
    }


    protected static class NPClass extends JPanel {

        protected final NPCFingerprintVersion.NPCProperty property;

        protected TextLayout typeName, className;
        protected Rectangle classBox, typeBox;

        protected Font typeFont, classFont;

        protected static final int PADDING = 4, SAFETY_DISTANCE = 4, GAP_TOP=4;
        protected boolean main;

        public NPClass(NPCFingerprintVersion.NPCProperty klass, double probability) {
            super();
            setToolTipText("<html><p>Probability: <b>" + (int)(Math.round(probability*100)) + " %</b></p></html>");
            this.main = main;
            this.property = klass;
            typeFont = Fonts.FONT_BOLD.deriveFont(10f);
            typeName = new TextLayout(klass.level.name, typeFont, new FontRenderContext(null, false, false));
            classFont = Fonts.FONT_BOLD.deriveFont(13f);
            className = new TextLayout(property.name, classFont, new FontRenderContext(null, false, false));
            setOpaque(false);
            classBox = className.getPixelBounds(null,0,0);
            typeBox = typeName.getPixelBounds(null,0,0);
            setPreferredSize(new Dimension(Math.max(classBox.width,typeBox.width)+2*PADDING+SAFETY_DISTANCE, classBox.height+typeBox.height + 2*PADDING + SAFETY_DISTANCE+GAP_TOP));
            setMinimumSize(new Dimension(Math.max(classBox.width,typeBox.width)+2*PADDING+SAFETY_DISTANCE, classBox.height+typeBox.height + 2*PADDING + SAFETY_DISTANCE+GAP_TOP));

        }

        @Override
        public void paintComponent(Graphics g_) {
            final Graphics2D g = (Graphics2D)g_;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            final Color color = main ? Colors.CLASSIFIER_MAIN : Colors.CLASSIFIER_OTHER;
            g.setColor(color);
            final int boxwidth = Math.max(classBox.width,typeBox.width)+2*PADDING;
            final int boxheight = classBox.height+2*PADDING+GAP_TOP;
            g.fillRoundRect(0, typeBox.height, boxwidth, boxheight, 4, 4);
            g.setColor(Colors.FOREGROUND);
            g.drawRoundRect(0, typeBox.height, boxwidth, boxheight, 4, 4);
            g.setColor(color);
            final int gap = (boxwidth-typeBox.width)/2;
            g.drawRect(gap, typeBox.height,typeBox.width,Math.min(typeBox.height,classBox.height));
            g.setFont(classFont);
            g.setColor(Colors.FOREGROUND);
            g.drawString(property.name, PADDING, classBox.height+typeBox.height+PADDING+GAP_TOP);

            g.setFont(typeFont);
            g.setColor(Colors.FOREGROUND);
            g.drawString(property.level.name, gap, typeBox.height+GAP_TOP);

        }
    }
}

package de.unijena.bioinf.ms.gui.compute;

import de.unijena.bioinf.ChemistryBase.chem.ChemicalAlphabet;
import de.unijena.bioinf.ChemistryBase.chem.Element;
import de.unijena.bioinf.ChemistryBase.chem.FormulaConstraints;
import de.unijena.bioinf.ChemistryBase.chem.utils.UnknownElementException;
import de.unijena.bioinf.ChemistryBase.ms.MutableMs2Experiment;
import de.unijena.bioinf.ChemistryBase.ms.ft.model.FormulaSettings;
import de.unijena.bioinf.ChemistryBase.utils.DescriptiveOptions;
import de.unijena.bioinf.chemdb.annotations.FormulaSearchDB;
import de.unijena.bioinf.chemdb.annotations.StructureSearchDB;
import de.unijena.bioinf.ms.frontend.core.ApplicationCore;
import de.unijena.bioinf.ms.gui.SiriusGui;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.gui.dialogs.ElementSelectionDialog;
import de.unijena.bioinf.ms.gui.dialogs.ExceptionDialog;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.gui.utils.RelativeLayout;
import de.unijena.bioinf.ms.gui.utils.TextHeaderBoxPanel;
import de.unijena.bioinf.ms.gui.utils.TwoColumnPanel;
import de.unijena.bioinf.ms.gui.utils.jCheckboxList.JCheckboxListPanel;
import de.unijena.bioinf.ms.nightsky.sdk.model.MsData;
import de.unijena.bioinf.ms.nightsky.sdk.model.SearchableDatabase;
import de.unijena.bioinf.ms.properties.PropertyManager;
import de.unijena.bioinf.projectspace.InstanceBean;
import de.unijena.bioinf.sirius.Ms1Preprocessor;
import de.unijena.bioinf.sirius.ProcessedInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class FormulaSearchStrategy extends ConfigPanel {
    public enum Strategy implements DescriptiveOptions {
        DEFAULT("De novo + bottom up (recommended)", "Perform both a bottom up search and de novo molecular formula generation."),
        BOTTOM_UP("Bottom up", "Generate molecular formula candidates using bottom up search: if a fragement + precursor loss have candidates in the formula database, these are combined to a precursor formula candidate."),
        DE_NOVO("De novo", "Generate molecular formula candidates de novo."),
        DATABASE("Database search", "Retrieve molecular formula candidates from a database.");

        private final String description;
        private final String displayName;

        Strategy(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum ElementAlphabetStrategy implements DescriptiveOptions {
        DE_NOVO_ONLY("De novo", "Use set of elements for de novo generation only."),
        BOTH("De novo + bottom up", "Use set of elements for de novo generation and filter of bottom up search.");

        private final String description;

        private final String displayName;

        ElementAlphabetStrategy(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    protected Strategy strategy;

    protected final Dialog owner;
    protected final SiriusGui gui;
    protected final List<InstanceBean> ecs;
    protected final boolean isMs2;
    protected final boolean isBatchDialog;

    protected  JCheckboxListPanel<SearchableDatabase> searchDBList;

    /**
     * Map of strategy-specific UI components for showing/hiding when changing the strategy
     */
    private final Map<Strategy, List<Component>> strategyComponents;

    public FormulaSearchStrategy(SiriusGui gui, Dialog owner, List<InstanceBean> ecs, boolean isMs2, boolean isBatchDialog, ParameterBinding parameterBindings) {
        super(parameterBindings);
        this.owner = owner;
        this.gui = gui;
        this.ecs = ecs;
        this.isMs2 = isMs2;
        this.isBatchDialog = isBatchDialog;

        strategyComponents = new HashMap<>();
        strategyComponents.put(Strategy.DEFAULT, new ArrayList<>());
        strategyComponents.put(Strategy.BOTTOM_UP, new ArrayList<>());
        strategyComponents.put(Strategy.DE_NOVO, new ArrayList<>());
        strategyComponents.put(Strategy.DATABASE, new ArrayList<>());

        createPanel();
    }

    public JCheckboxListPanel<SearchableDatabase> getSearchDBList() {
        return searchDBList;
    }

    private void createPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final JPanel formulaSearchStrategySelection = new JPanel();
        formulaSearchStrategySelection.setLayout(new BoxLayout(formulaSearchStrategySelection, BoxLayout.PAGE_AXIS));
        formulaSearchStrategySelection.setBorder(BorderFactory.createEmptyBorder(0, GuiUtils.LARGE_GAP, 0, 0));
        JComboBox<Strategy> strategyBox =  GuiUtils.makeParameterComboBoxFromDescriptiveValues(Strategy.values());
        formulaSearchStrategySelection.add(new TextHeaderBoxPanel("Molecular formula generation", strategyBox));

        add(formulaSearchStrategySelection);
        add(Box.createRigidArea(new Dimension(0, GuiUtils.MEDIUM_GAP)));

        JPanel strategyCardContainer = new JPanel();
        strategyCardContainer.setBorder(BorderFactory.createEmptyBorder(0, GuiUtils.LARGE_GAP, 0, 0));
        strategyCardContainer.setLayout(new BoxLayout(strategyCardContainer, BoxLayout.LINE_AXIS));

        strategy = (Strategy) strategyBox.getSelectedItem();

        JPanel defaultStrategyParameters = createDefaultStrategyParameters();
        JPanel databaseStrategyParameters = createDatabaseStrategyParameters();

        strategyComponents.get(Strategy.DEFAULT).add(defaultStrategyParameters);
        strategyComponents.get(Strategy.DATABASE).add(databaseStrategyParameters);

        strategyCardContainer.add(defaultStrategyParameters);
        strategyCardContainer.add(databaseStrategyParameters);

        strategyCardContainer.add(createElementFilterPanel());

        add(strategyCardContainer);

        hideAllStrategySpecific();
        showStrategySpecific(strategy, true);

        strategyBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            showStrategySpecific(strategy, false);
            strategy = (Strategy) e.getItem();
            showStrategySpecific(strategy, true);
        });
    }

    private void showStrategySpecific(Strategy s, boolean show) {
        strategyComponents.get(s).forEach(c -> c.setVisible(show));
    }

    private void hideAllStrategySpecific() {
        strategyComponents.forEach((s, lst) -> lst.forEach(c -> c.setVisible(false)));
    }

    private JPanel createDefaultStrategyParameters() {
        JPanel parameterPanel = applyDefaultLayout(new JPanel());
        ((RelativeLayout) parameterPanel.getLayout()).setBorderGap(0);
        parameterPanel.setBorder(BorderFactory.createEmptyBorder(0, GuiUtils.LARGE_GAP, 0, 0));

        final TwoColumnPanel options = new TwoColumnPanel();

        JSpinner denovoUpTo = makeIntParameterSpinner("FormulaSearchSettings.performDeNovoBelowMz", 0, Integer.MAX_VALUE, 5);  // binding is overwritten
        options.addNamed("Perform de novo below m/z", denovoUpTo);

        parameterBindings.put("FormulaSearchSettings.performBottomUpAboveMz", () -> switch (strategy) {
            case DEFAULT, BOTTOM_UP -> "0";
            case DE_NOVO, DATABASE -> String.valueOf(Double.POSITIVE_INFINITY);
        });

        parameterBindings.put("FormulaSearchSettings.performDeNovoBelowMz", () -> switch (strategy) {
            case DEFAULT -> denovoUpTo.getValue().toString();
            case BOTTOM_UP, DATABASE -> "0";
            case DE_NOVO -> String.valueOf(Double.POSITIVE_INFINITY);
        });

        parameterPanel.add(new TextHeaderBoxPanel("General", options));

        return parameterPanel;
    }

    private JPanel createDatabaseStrategyParameters() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.PAGE_AXIS));

        initDatabasePanel();
        searchDBList.setBorder(BorderFactory.createEmptyBorder(0, GuiUtils.LARGE_GAP, 0, 0));

        card.add(searchDBList);
        return card;
    }

    private void initDatabasePanel() {
        searchDBList = new JCheckboxListPanel<>(DBSelectionList.fromSearchableDatabases(gui.getSiriusClient()), "Use DB formulas only");
        GuiUtils.assignParameterToolTip(searchDBList.checkBoxList, "FormulaSearchDB");

        PropertyManager.DEFAULTS.createInstanceWithDefaults(StructureSearchDB.class).searchDBs
                .forEach(s -> searchDBList.checkBoxList.check(gui.getSiriusClient().databases().getDatabase(s.name(), false)));

        parameterBindings.put("FormulaSearchDB", () -> strategy == Strategy.DATABASE ? String.join(",", getFormulaSearchDBStrings()) : ",");
        PropertyManager.DEFAULTS.createInstanceWithDefaults(FormulaSearchDB.class).searchDBs
                .forEach(s -> searchDBList.checkBoxList.check(gui.getSiriusClient().databases().getDatabase(s.name(), false)));
    }

    private JPanel createElementFilterPanel() {
        Set<Element> autoDetectableElements = ApplicationCore.SIRIUS_PROVIDER.sirius().getMs1Preprocessor().getSetOfPredictableElements();
        final FormulaSettings formulaSettings = PropertyManager.DEFAULTS.createInstanceWithDefaults(FormulaSettings.class);

        final TwoColumnPanel filterFields = new TwoColumnPanel();

        JLabel constraintsLabel = new JLabel("Allowed elements");
        JTextField enforcedTextBox = makeParameterTextField("FormulaSettings.enforced", formulaSettings.getEnforcedAlphabet().toString(), 20);

        JLabel autodetectLabel = new JLabel("Autodetect");
        final JTextField detectableTextBox = isBatchDialog ? makeParameterTextField("FormulaSettings.detectable", 20) : null;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton buttonEdit = new JButton("…");  // Ellipsis symbol instead of ... because 1-char buttons don't get side insets
        buttonEdit.setToolTipText("Customize allowed elements and their quantities");
        buttonPanel.add(buttonEdit);
        if (!isBatchDialog) {
            JButton buttonAutodetect = new JButton("Detect");
            buttonAutodetect.setToolTipText("Auto detectable element are: " + join(autoDetectableElements));
            buttonAutodetect.addActionListener(e -> detectElements(autoDetectableElements, enforcedTextBox));
            buttonPanel.add(buttonAutodetect);
        }

        addDefaultStrategyElementFilterSettings(filterFields);

        List<Component> filterComponents = new ArrayList<>(List.of(constraintsLabel, enforcedTextBox, buttonPanel));
        if (isBatchDialog) {
            filterComponents.addAll(List.of(autodetectLabel, detectableTextBox));
        }
        int columnWidth = enforcedTextBox.getPreferredSize().width;
        int sidePanelWidth = buttonPanel.getPreferredSize().width;
        addElementFilterEnabledCheckboxForStrategy(filterFields, filterComponents, Strategy.BOTTOM_UP, columnWidth, sidePanelWidth);
        addElementFilterEnabledCheckboxForStrategy(filterFields, filterComponents, Strategy.DATABASE, columnWidth, sidePanelWidth);

        int constraintsGridY = filterFields.both.gridy;
        filterFields.add(constraintsLabel, enforcedTextBox);
        if (isBatchDialog) {
            filterFields.add(autodetectLabel, detectableTextBox);
        }


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = constraintsGridY;
        c.gridheight = isBatchDialog ? 2 : 1;
        filterFields.add(buttonPanel, c);

        buttonEdit.addActionListener(e -> {
            FormulaConstraints currentConstraints = FormulaConstraints.fromString(enforcedTextBox.getText());
            Set<Element> currentAuto = null;
            if (isBatchDialog) {
                try {
                    currentAuto = ChemicalAlphabet.fromString(detectableTextBox.getText()).toSet();
                } catch (UnknownElementException ex) {
                    currentAuto = autoDetectableElements;
                }
            }
            ElementSelectionDialog dialog = new ElementSelectionDialog(owner, "Filter Elements", isBatchDialog ? autoDetectableElements : null, currentAuto, currentConstraints);
            if (dialog.isSuccess()) {
                enforcedTextBox.setText(dialog.getConstraints().toString());
                if (isBatchDialog) {
                    detectableTextBox.setText(join(dialog.getAutoDetect()));
                }
            }
        });

        JPanel elementFilterPanel = applyDefaultLayout(new JPanel());
        elementFilterPanel.add(new TextHeaderBoxPanel("Element Filter", filterFields));

        return elementFilterPanel;
    }

    private void addDefaultStrategyElementFilterSettings(TwoColumnPanel filterFields) {
        JComboBox<ElementAlphabetStrategy> elementAlphabetStrategySelector = new JComboBox<>(); //todo NewWorflow: implement this feature in sirius-libs
        List<ElementAlphabetStrategy> settingsElements = List.copyOf(EnumSet.allOf(ElementAlphabetStrategy.class));
        settingsElements.forEach(elementAlphabetStrategySelector::addItem);
        elementAlphabetStrategySelector.setSelectedItem(ElementAlphabetStrategy.DE_NOVO_ONLY);
        parameterBindings.put("FormulaSearchSettings.applyFormulaConstraintsToBottomUp", () -> Boolean.toString(elementAlphabetStrategySelector.getSelectedItem() == ElementAlphabetStrategy.BOTH));

        JLabel label = new JLabel("Apply element filter to");
        filterFields.add(label, elementAlphabetStrategySelector);

        strategyComponents.get(Strategy.DEFAULT).add(label);
        strategyComponents.get(Strategy.DEFAULT).add(elementAlphabetStrategySelector);
    }

    private void addElementFilterEnabledCheckboxForStrategy(TwoColumnPanel filterFields, List<Component> filterComponents, Strategy s, int columnWidth, int sidePanelWidth) {
        JCheckBox useElementFilter = new JCheckBox() { //todo NewWorkflow: implement this feature. This makes the organics filter obsolete. Maybe dont use the checkbox but always select the organics. Make new Element panel popup
            @Override
            public void setVisible(boolean flag) {
                super.setVisible(flag);
                if (flag) {
                    filterComponents.forEach(c -> c.setVisible(this.isSelected()));
                } else {
                    filterComponents.forEach(c -> c.setVisible(true));
                }
            }
        };

        parameterBindings.put("FormulaSearchSettings.applyFormulaConstraintsToDatabaseCandidates", () -> Boolean.toString(useElementFilter.isSelected()));

        JLabel label = new JLabel("Enable element filter");

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        checkBoxPanel.add(useElementFilter);

        checkBoxPanel.setPreferredSize(new Dimension(columnWidth, checkBoxPanel.getPreferredSize().height));  // Prevent resizing on checking/unchecking

        int constraintsGridY = filterFields.both.gridy;
        filterFields.add(label, checkBoxPanel);
        useElementFilter.addActionListener(e -> filterComponents.forEach(c -> c.setVisible(useElementFilter.isSelected())));

        JPanel invisiblePanel = new JPanel();  // Prevent resizing on checking/unchecking
        invisiblePanel.setPreferredSize(new Dimension(sidePanelWidth, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = constraintsGridY;
        filterFields.add(invisiblePanel, c);

        strategyComponents.get(s).add(label);
        strategyComponents.get(s).add(checkBoxPanel);
        strategyComponents.get(s).add(useElementFilter);
        strategyComponents.get(s).add(invisiblePanel);
    }

    private String join(Collection<?> objects) {
        return objects.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    protected void detectElements(Set<Element> autoDetectable, JTextField formulaConstraintsTextBox) {
        //todo nightsky: do we want todo that in the frontend?
        String notWorkingMessage = "Element detection requires MS1 spectrum with isotope pattern.";
        FormulaConstraints[] currentConstraints = new FormulaConstraints[]{FormulaConstraints.fromString(formulaConstraintsTextBox.getText())};
        InstanceBean ec = ecs.get(0);
        MsData msData = ec.getMsData();
        if (!msData.getMs1Spectra().isEmpty() || msData.getMergedMs1() != null) {
            Jobs.runInBackgroundAndLoad(owner, "Detecting Elements...", () -> {
                final Ms1Preprocessor pp = ApplicationCore.SIRIUS_PROVIDER.sirius().getMs1Preprocessor();
                ProcessedInput pi = pp.preprocess(new MutableMs2Experiment(ec.asMs2Experiment(), false));

                pi.getAnnotation(FormulaConstraints.class).
                        ifPresentOrElse(c -> {
                                    for (Element element : autoDetectable) {
                                        if (currentConstraints[0].hasElement(element)) {
                                            currentConstraints[0].setBound(element, c.getLowerbound(element), c.getUpperbound(element));
                                        } else {
                                            currentConstraints[0] = currentConstraints[0].getExtendedConstraints(element);
                                            currentConstraints[0].setBound(element, 0, 0);
                                        }
                                    }
                                    formulaConstraintsTextBox.setText(currentConstraints[0].toString());
                                },
                                () -> new ExceptionDialog(owner, notWorkingMessage)
                        );
            }).getResult();
        } else {
            new ExceptionDialog(owner, notWorkingMessage);
        }
    }

    public List<SearchableDatabase> getFormulaSearchDBs() {
        return searchDBList.checkBoxList.getCheckedItems();
    }

    public List<String> getFormulaSearchDBStrings() {
        return getFormulaSearchDBs().stream().map(SearchableDatabase::getDatabaseId).collect(Collectors.toList());
    }

    public Strategy getSelectedStrategy(){
        return strategy;
    }
}

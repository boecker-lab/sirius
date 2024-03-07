package de.unijena.bioinf.ms.gui.fingerid.custom_db;

import de.unijena.bioinf.ChemistryBase.utils.FileUtils;
import de.unijena.bioinf.ms.frontend.core.SiriusProperties;
import de.unijena.bioinf.ms.frontend.io.FileChooserPanel;
import de.unijena.bioinf.ms.frontend.subtools.custom_db.CustomDBOptions;
import de.unijena.bioinf.ms.gui.SiriusGui;
import de.unijena.bioinf.ms.gui.compute.SubToolConfigPanel;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.gui.configs.Buttons;
import de.unijena.bioinf.ms.gui.dialogs.input.DragAndDrop;
import de.unijena.bioinf.ms.gui.net.ConnectionMonitor;
import de.unijena.bioinf.ms.gui.utils.*;
import de.unijena.bioinf.ms.nightsky.sdk.model.ConnectionCheck;
import de.unijena.bioinf.ms.nightsky.sdk.model.SearchableDatabase;
import de.unijena.bioinf.ms.properties.PropertyManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static de.unijena.bioinf.chemdb.custom.CustomDatabases.NOSQL_SUFFIX;
import static de.unijena.bioinf.ms.gui.net.ConnectionChecks.isConnected;
import static de.unijena.bioinf.ms.gui.net.ConnectionChecks.isLoggedIn;


public class DatabaseImportConfigPanel extends SubToolConfigPanel<CustomDBOptions> {

    private PlaceholderTextField dbDisplayNameField;
    private PlaceholderTextField dbNameField;
    private FileChooserPanel dbLocationField;
    private DefaultListModel<File> fileListModel;
    public JButton importButton;

    private boolean validDbDisplayName;
    private boolean validDbName;
    private boolean validDbDirectory;

    private boolean loggedIn = false;

    private final JLabel loginErrorLabel = new JLabel();

    private final SiriusGui gui;

    public DatabaseImportConfigPanel(@NotNull SiriusGui gui, @Nullable SearchableDatabase db) {
        super(CustomDBOptions.class);
        this.gui = gui;
        setLayout(new BorderLayout());

        add(createParametersPanel(db), BorderLayout.NORTH);
        add(createCompoundsBox(), BorderLayout.CENTER);
        add(createImportButton(), BorderLayout.SOUTH);


        gui.getConnectionMonitor().addConnectionStateListener(evt -> {
            ConnectionCheck check = ((ConnectionMonitor.ConnectionStateEvent) evt).getConnectionCheck();
            setLoggedIn(check);
        });
        Jobs.runInBackground(() -> setLoggedIn(gui.getConnectionMonitor().checkConnection()));
    }

    private synchronized void setLoggedIn(ConnectionCheck check) {
        loggedIn = isConnected(check) && isLoggedIn(check);
        loginErrorLabel.setVisible(!loggedIn);
        refreshImportButton();
    }


    private boolean checkName(String name){
        final String n = !name.endsWith(NOSQL_SUFFIX) ? (name + NOSQL_SUFFIX) : name;
        return gui.applySiriusClient((c,pid) -> c.databases().getDatabaseWithResponseSpec(n,false)
                .bodyToMono(SearchableDatabase.class).onErrorComplete().blockOptional().isPresent());
    }

    private JPanel createParametersPanel(@Nullable SearchableDatabase db) {
        final TwoColumnPanel smalls = new TwoColumnPanel();

        dbDisplayNameField = new PlaceholderTextField("");
        smalls.addNamed("Name", dbDisplayNameField, GuiUtils.formatToolTip("Displayable name of the custom database. " +
                "This is the preferred name to be shown in the GUI. Maximum Length: 15 characters. " +
                "If not given the filename will be used."));
        parameterBindings.put("displayName", dbDisplayNameField::getText);
        dbNameField = new PlaceholderTextField("");
        smalls.addNamed("Filename", dbNameField, GuiUtils.formatToolTip("Filename and unique identifier of the new custom database, should end in " + NOSQL_SUFFIX));
        parameterBindings.put("name", dbNameField::getText);

        String dbDirectory = db != null ? Path.of(db.getLocation()).getParent().toString()
                : PropertyManager.getProperty(SiriusProperties.DEFAULT_SAVE_DIR_PATH, null, "");

        dbLocationField = new FileChooserPanel(dbDirectory, JFileChooser.DIRECTORIES_ONLY);
        smalls.addNamed("Location", dbLocationField, "The directory where the custom database file will be stored.");
        parameterBindings.put("location", this::getDbFilePath);
        validDbDirectory = !dbDirectory.isEmpty();

        if (db != null) {
            dbDisplayNameField.setText(db.getDisplayName());
            dbDisplayNameField.setEnabled(false);
            dbNameField.setText(db.getDatabaseId());
            dbNameField.setEnabled(false);
            dbLocationField.setEnabled(false);
            validDbName = true;
            validDbDisplayName = true;
        } else {
            dbDisplayNameField.setPlaceholder("My Database");
            dbNameField.setPlaceholder("my_database" + NOSQL_SUFFIX);
        }

        dbDisplayNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String name = dbDisplayNameField.getText();

                if (dbNameField.getText() == null || dbNameField.getText().isBlank()) {
                    if (!name.isBlank()) {
                        name = FileUtils.sanitizeFilename(name).toLowerCase();
                        if (!name.endsWith(NOSQL_SUFFIX))
                            name = name + NOSQL_SUFFIX;
                        dbNameField.setText(name);
                    }
                }
            }
        });

        dbNameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String name = dbNameField.getText();

                if (name.isBlank()) {
                    String displayName = dbDisplayNameField.getText();
                    if (!displayName.isBlank()) {
                        name = FileUtils.sanitizeFilename(displayName).toLowerCase();
                        if (!name.endsWith(NOSQL_SUFFIX))
                            name = name + NOSQL_SUFFIX;
                        dbNameField.setText(name);
                    }
                } else if (!name.endsWith(NOSQL_SUFFIX)) {
                    dbNameField.setText(name + NOSQL_SUFFIX);
                }
            }
        });

        dbDisplayNameField.setInputVerifier(new ErrorReportingDocumentListener(dbDisplayNameField) {
            @Override
            public String getErrorMessage(JComponent input) {
                String name = ((JTextField) input).getText();
                String error = null;
                if (name != null) {
                    if (name.length() > 15) {
                        error = "DB Display name must have not more than 15 characters.";
                    } else if (!name.isEmpty() && name.isBlank()) {
                        error = "DB Display name must not contain only white spaces.";
                    }
                }
                if (validDbDisplayName != (error == null)) {
                    validDbDisplayName = error == null;
                    refreshImportButton();
                }
                return error;
            }
        });

        dbNameField.setInputVerifier(new ErrorReportingDocumentListener(dbNameField) {
            @Override
            public String getErrorMessage(JComponent input) {
                String name = ((JTextField) input).getText();
                String error = null;
                if (name == null || name.isBlank()) {
                    error = "DB name missing";
                } else if (checkName(name)) {
                    error = "This name is already in use";
                }
                if (validDbName != (error == null)) {
                    validDbName = error == null;
                    refreshImportButton();
                }
                return error;
            }
        });

        dbLocationField.field.setInputVerifier(new ErrorReportingDocumentListener(dbLocationField.field) {
            @Override
            public String getErrorMessage(JComponent input) {
                String text = ((JTextField) input).getText();
                String error = null;
                if (text == null || text.isBlank()) {
                    error = "DB location not set";
                } else if (!new File(text).isDirectory()) {
                    error = "DB location is not a valid directory";
                }
                if (validDbDirectory != (error == null)) {
                    validDbDirectory = error == null;
                    refreshImportButton();
                }
                return error;
            }
        });

        final String buf = "buffer";
        JSpinner bufferSize = makeGenericOptionSpinner(buf,
                getOptionDefaultByName(buf).map(Integer::parseInt).orElse(1),
                1, Integer.MAX_VALUE, 1,
                (v) -> String.valueOf(v.getNumber().intValue()));
        smalls.addNamed("Buffer Size", bufferSize);

        return smalls;
    }

    private Box createCompoundsBox() {
        final Box box = Box.createVerticalBox();

        String inputParameter = "input";
        fileListModel = new DefaultListModel<>();
        JList<File> fileList = new JListDropImage<>(fileListModel);
        getOptionDescriptionByName(inputParameter).ifPresent(description -> box.setToolTipText(GuiUtils.formatToolTip(300, description)));
        parameterBindings.put(inputParameter, this::getFiles);

        final JScrollPane pane = new JScrollPane(fileList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        box.add(pane);

        ToolbarButton addFiles = Buttons.getAddButton16("Add files");
        ToolbarButton removeFiles = Buttons.getRemoveButton16("Remove files");
        removeFiles.setEnabled(false);

        Box fileListButtons = Box.createHorizontalBox();
        fileListButtons.add(Box.createHorizontalGlue());
        fileListButtons.add(removeFiles);
        fileListButtons.add(addFiles);

        box.add(fileListButtons);

        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Drop or add compound and/or spectra files")
        );
        box.setBorder(border);


        JFileChooser importFileChooser = new JFileChooser();
        importFileChooser.setMultiSelectionEnabled(true);

        addFiles.addActionListener(e -> {
            if (importFileChooser.showOpenDialog(DatabaseImportConfigPanel.this) == JFileChooser.APPROVE_OPTION) {
                File[] files = importFileChooser.getSelectedFiles();

                for (File f : files) {
                    fileListModel.addElement(f);
                }
                refreshImportButton();
            }
        });

        DropTarget dropTarget = new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                fileListModel.addAll(DragAndDrop.getFileListFromDrop(gui.getMainFrame(), evt));
                refreshImportButton();
            }
        };
        setDropTarget(dropTarget);

        fileList.addListSelectionListener(e -> removeFiles.setEnabled(!fileList.isSelectionEmpty()));

        Action removeSelectedFiles = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedIndices = fileList.getSelectedIndices();
                for (int i = selectedIndices.length - 1; i >= 0; i--) {
                    fileListModel.removeElementAt(selectedIndices[i]);
                }
                refreshImportButton();
            }
        };

        removeFiles.addActionListener(removeSelectedFiles);

        String removeFilesActionName = "removeSelectedFiles";
        fileList.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), removeFilesActionName);
        fileList.getActionMap().put(removeFilesActionName, removeSelectedFiles);

        return box;
    }

    private JPanel createImportButton() {
        JPanel panel = new JPanel(new BorderLayout());

        loginErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginErrorLabel.setText("<html><p style=\"background-color:#ffafaf; color:black\"><b>LOGIN ERROR:</b> Please login with a verified user account to import compounds!</p></html>");
        loginErrorLabel.setVisible(false);

        importButton = new JButton("Import structures and spectra");
        importButton.setEnabled(false);

        panel.add(loginErrorLabel, BorderLayout.CENTER);
        panel.add(importButton, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshImportButton() {
        importButton.setEnabled(validDbDirectory && validDbName && !fileListModel.isEmpty() && loggedIn);
    }

    public String getDbFilePath() {
        return Path.of(dbLocationField.getFilePath(), dbNameField.getText()).toString();
    }

    /**
     * @return comma-separated list of absolute paths to files to import
     */
    public String getFiles() {
        return Arrays.stream(fileListModel.toArray()).map(f -> ((File) f).getAbsolutePath()).distinct().collect(Collectors.joining(","));
    }
}

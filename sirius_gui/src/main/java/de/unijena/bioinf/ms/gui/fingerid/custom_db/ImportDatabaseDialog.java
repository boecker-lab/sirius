package de.unijena.bioinf.ms.gui.fingerid.custom_db;

import de.unijena.bioinf.ChemistryBase.utils.FileUtils;
import de.unijena.bioinf.chemdb.custom.CustomDatabase;
import de.unijena.bioinf.ms.frontend.subtools.InputFilesOptions;
import de.unijena.bioinf.ms.gui.compute.jjobs.Jobs;
import de.unijena.bioinf.ms.gui.dialogs.StacktraceDialog;
import de.unijena.bioinf.ms.gui.dialogs.input.DragAndDrop;
import de.unijena.bioinf.ms.gui.utils.GuiUtils;
import de.unijena.bioinf.ms.gui.utils.JTextAreaDropImage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static de.unijena.bioinf.ms.gui.mainframe.MainFrame.MF;

class ImportDatabaseDialog extends JDialog {
    private final DatabaseDialog databaseDialog;
    protected JButton importButton;
    protected DatabaseImportConfigPanel configPanel;

    public ImportDatabaseDialog(DatabaseDialog databaseDialog) {
        this(databaseDialog, null);
    }

    public ImportDatabaseDialog(DatabaseDialog databaseDialog, @Nullable CustomDatabase db) {
        super(databaseDialog, db != null ? "Import into " + db.name() : "Create custom database", true);
        this.databaseDialog = databaseDialog;

        setPreferredSize(new Dimension(640, 480));
        setLayout(new BorderLayout());

        final Box box = Box.createVerticalBox();
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JLabel label = new JLabel(
                "<html>Please insert the compounds of your custom database as SMILES here (one compound per line). It is also possible to drag and drop files with SMILES and reference spectrum files into this text field.");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(label);
        final JTextArea textArea = new JTextAreaDropImage();
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JScrollPane pane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(pane);
        box.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Import compounds"));

        importButton = new JButton("Create/Open database and import compounds");
        importButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

        configPanel = new DatabaseImportConfigPanel(db);
        importButton.setEnabled(db != null && !configPanel.dbLocationField.getFilePath().isBlank());
        configPanel.dbLocationField.field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextChanged();
            }

            public void onTextChanged() {
                if (configPanel.dbLocationField.getFilePath() == null) return;
                importButton.setEnabled(!configPanel.dbLocationField.getFilePath().isEmpty() && configPanel.dbLocationField.getFilePath().replaceAll("\\s", "").equals(configPanel.dbLocationField.getFilePath()) && databaseDialog.customDatabases.stream().noneMatch(k -> k.name().equalsIgnoreCase(configPanel.dbLocationField.getFilePath())));
            }
        });

        add(configPanel, BorderLayout.NORTH);
        add(box, BorderLayout.CENTER);
        add(importButton, BorderLayout.SOUTH);

        importButton.addActionListener(e -> {
            dispose();
            String t = textArea.getText();
            runImportJob(null,
                    t != null && !t.isBlank()
                            ? Arrays.asList(t.split("\n"))
                            : null
            );
        });

        final DropTarget dropTarget = new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                dispose();
                String t = textArea.getText();
                runImportJob(
                        DragAndDrop.getFileListFromDrop(evt).stream().map(File::toPath).collect(Collectors.toList()),
                        t != null && !t.isBlank()
                                ? Arrays.asList(t.split("\n"))
                                : null
                );
            }
        };

        GuiUtils.closeOnEscape(this);
        setDropTarget(dropTarget);
        textArea.setDropTarget(dropTarget);
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);

    }

    protected void runImportJob(@Nullable java.util.List<Path> sources, @Nullable java.util.List<String> stringSources) {
        if (sources == null)
            sources = new ArrayList<>();
        try {
            if (stringSources != null && !stringSources.isEmpty()) {
                sources.add(Jobs.runInBackgroundAndLoad(this, "Processing string input Data...", () -> {
                    Path f = FileUtils.newTempFile("custom-db-import", ".csv");
                    try {
                        Files.write(f, stringSources);
                        return f;
                    } catch (IOException ioException) {
                        throw new IOException("Could not write input data to temp location '" + f + "'.", ioException);
                    }
                }).awaitResult());
            }

            List<String> command = new ArrayList<>();
            command.add(configPanel.toolCommand());
            command.addAll(configPanel.asParameterList());

            InputFilesOptions input = new InputFilesOptions();
            input.msInput = new InputFilesOptions.MsInput();
            input.msInput.setInputPath(sources);

            Jobs.runCommandAndLoad(command, null,
                            input, this,
                            "Importing into '" + configPanel.dbLocationField.getFilePath() + "'...",
                            false)
                    .awaitResult();

            databaseDialog.whenCustomDbIsAdded(configPanel.getDbFilePath());
        } catch (ExecutionException ex) {
            LoggerFactory.getLogger(getClass()).error("Error during Custom DB import.", ex);

            if (ex.getCause() != null)
                new StacktraceDialog(this, ex.getCause().getMessage(), ex.getCause());
            else
                new StacktraceDialog(this, "Unexpected error when importing custom DB!", ex);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Fatal Error during Custom DB import.", e);
            new StacktraceDialog(MF, "Fatal Error during Custom DB import.", e);
        }
    }
}

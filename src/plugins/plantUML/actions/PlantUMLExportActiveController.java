package plugins.plantUML.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import plugins.plantUML.export.DiagramExportPipeline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;

public class PlantUMLExportActiveController implements VPActionController {

    @Override
    public void performAction(VPAction action) {
        ViewManager viewManager = ApplicationManager.instance().getViewManager();

        viewManager.showDialog(new ExportDialogHandler());
    }

    @Override
    public void update(VPAction action) {
    }

    private static class ExportDialogHandler implements IDialogHandler {
        private JPanel mainPanel;
        private JTextField outputFolderField;
        private IDialog dialog;

        public ExportDialogHandler() {
        }

        @Override
        public Component getComponent() {
            // Initialize the main panel
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

            JLabel descriptionLabel = new JLabel("Choose an output folder:");
            topPanel.add(descriptionLabel);
            topPanel.add(Box.createVerticalStrut(10)); // Add spacing

            // Add output folder chooser line
            JPanel folderChooserPanel = new JPanel(new BorderLayout());
            folderChooserPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
            JLabel folderLabel = new JLabel("Output Folder:");
            outputFolderField = new JTextField("C:\\Users\\30695\\Documents" ); //TODO: remove its debug
            JButton browseButton = new JButton("Browse");

            browseButton.addActionListener((ActionEvent e) -> {
                // Open a folder chooser dialog
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(mainPanel);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    outputFolderField.setText(selectedFolder.getAbsolutePath());
                }
            });

            folderChooserPanel.add(folderLabel, BorderLayout.WEST);
            folderChooserPanel.add(outputFolderField, BorderLayout.CENTER);
            folderChooserPanel.add(browseButton, BorderLayout.EAST);

            topPanel.add(folderChooserPanel);

            mainPanel.add(topPanel, BorderLayout.NORTH);

            return mainPanel;
        }

        @Override
        public void prepare(IDialog dialog) {
            this.dialog = dialog;
            dialog.setTitle("Export Active Diagram as PlantUML");
            dialog.setModal(true);
            dialog.setSize(500, 210);

        }

        @Override
        public void shown() {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

            JButton exportButton = new JButton("Export");
            JButton closeButton = new JButton("Close");

            exportButton.addActionListener((ActionEvent e) -> {
                IDiagramUIModel diagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();



                File outputFolder = new File(outputFolderField.getText());
                if (!outputFolder.exists() || !outputFolder.isDirectory()) {
                    ApplicationManager.instance().getViewManager()
                            .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(),  "Please select a valid output folder.");
                    return;
                }

                DiagramExportPipeline pipeline = new DiagramExportPipeline(outputFolder);

                if (pipeline.exportDiagramList(Collections.singletonList(diagram))) {
                    ApplicationManager.instance().getViewManager()
                            .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Export complete."); // TODO this is shown even when error
                }
                dialog.close();
            });

            closeButton.addActionListener((ActionEvent e) -> {
                dialog.close();
            });

            buttonPanel.add(exportButton);
            buttonPanel.add(closeButton);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }


        @Override
        public boolean canClosed() {
            return true;
        }

    }
}

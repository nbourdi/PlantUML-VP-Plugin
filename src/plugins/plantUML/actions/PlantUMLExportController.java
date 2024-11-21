package plugins.plantUML.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import plugins.plantUML.export.ClassDiagramExporter;
import plugins.plantUML.export.DiagramExportPipeline;
import plugins.plantUML.export.UseCaseDiagramExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlantUMLExportController implements VPActionController {

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
        private List<JCheckBox> allCheckboxes;
        private IDialog dialog;

        public ExportDialogHandler() {
        }

        @Override
        public Component getComponent() {
            // Initialize the main panel
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            allCheckboxes = new ArrayList<>();

            // Create the top panel for description and output folder chooser
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

            // Add a description label
            JLabel descriptionLabel = new JLabel("Please select the diagrams to export and choose an output folder:");
            topPanel.add(descriptionLabel);
            topPanel.add(Box.createVerticalStrut(10)); // Add spacing

            // Add output folder chooser line
            JPanel folderChooserPanel = new JPanel(new BorderLayout());
            folderChooserPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
            JLabel folderLabel = new JLabel("Output Folder:");
            outputFolderField = new JTextField();
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

            // Add the top panel to the main panel
            mainPanel.add(topPanel, BorderLayout.NORTH);

            // Group diagrams by type
            Map<String, List<IDiagramUIModel>> groupedDiagrams = groupDiagramsByType();

            // Create the content panel for grouped diagrams
         // Create the content panel for grouped diagrams
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            contentPanel.setBackground(Color.WHITE); // Ensure the background is white

            for (Map.Entry<String, List<IDiagramUIModel>> entry : groupedDiagrams.entrySet()) {
                // Add a category label
                JLabel categoryLabel = new JLabel(entry.getKey());
                // categoryLabel.setFont(new Font(Font.BOLD));
                categoryLabel.setOpaque(true); // Set opaque to respect background
                categoryLabel.setBackground(Color.WHITE); // Ensure the label background is also white
                contentPanel.add(categoryLabel);

                // Add checkboxes for diagrams in this category
                JPanel checkBoxPanel = new JPanel();
                checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
                checkBoxPanel.setBackground(Color.WHITE); // Ensure the panel is white
                checkBoxPanel.setBorder(new EmptyBorder(5, 15, 15, 15)); // Add padding

                for (IDiagramUIModel diagram : entry.getValue()) {
                    JCheckBox checkBox = new JCheckBox(diagram.getName());
                    checkBox.setActionCommand(diagram.getId());
                    checkBox.setBackground(Color.WHITE); // Ensure checkboxes have a white background
                    allCheckboxes.add(checkBox);
                    checkBoxPanel.add(checkBox);
                }

                contentPanel.add(checkBoxPanel);

                // Add a white border between categories (no visible grey area)
                contentPanel.add(Box.createVerticalStrut(10)); // Acts as a spacer
            }

            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            scrollPane.getViewport().setBackground(Color.WHITE); // Ensure the scroll pane viewport is white

            mainPanel.add(scrollPane, BorderLayout.CENTER);

            return mainPanel;
        }

        @Override
        public void prepare(IDialog dialog) {
        	this.dialog = dialog;
            dialog.setTitle("Select Diagrams to Export");
            dialog.setModal(true);
            dialog.setSize(500, 400);
        }

        @Override
        public void shown() {
            // Add buttons at the bottom-right corner
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Add spacing from the top

            JButton exportButton = new JButton("Export");
            JButton closeButton = new JButton("Close");

            exportButton.addActionListener((ActionEvent e) -> {
                // Collect selected diagrams
                List<IDiagramUIModel> selectedDiagrams = new ArrayList<>();
                for (JCheckBox checkBox : allCheckboxes) {
                    if (checkBox.isSelected()) {
                        // Find the corresponding IDiagramUIModel instance
                        ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
                        IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
                        for (IDiagramUIModel diagram : allDiagrams) {
                            if (diagram.getId().equals(checkBox.getActionCommand())) {
                                selectedDiagrams.add(diagram);
                                break;
                            }
                        }
                    }
                }

                // Perform the extraction logic for each selected diagram
                File outputFolder = new File(outputFolderField.getText());
                if (!outputFolder.exists() || !outputFolder.isDirectory()) {
                    ApplicationManager.instance().getViewManager()
                    .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(),  "Please select a valid output folder.");
                    return;
                }

                DiagramExportPipeline pipeline = new DiagramExportPipeline(outputFolder);

                for (IDiagramUIModel activeDiagram : selectedDiagrams) {
                    try {
                        pipeline.export(activeDiagram);
                    } catch (IOException ex) {
                        ApplicationManager.instance().getViewManager()
                        .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Error processing diagram: " + activeDiagram.getName() + "\n" + ex.getMessage());
                    } catch (UnsupportedOperationException ex) {
                        ApplicationManager.instance().getViewManager()
                        .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), ex.getMessage());
                    }
                }

                ApplicationManager.instance().getViewManager()
                    .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Export complete."); // TODO this is shown even when error
                dialog.close();
            });

            closeButton.addActionListener((ActionEvent e) -> {
                // Close the dialog
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

        private Map<String, List<IDiagramUIModel>> groupDiagramsByType() {
            ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
            IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();

            Map<String, List<IDiagramUIModel>> grouped = new TreeMap<>();
            for (IDiagramUIModel diagram : allDiagrams) {
                String type = diagram.getType(); // Get the diagram type
                grouped.computeIfAbsent(type, k -> new ArrayList<>()).add(diagram);
            }
            return grouped;
        }
    }
}

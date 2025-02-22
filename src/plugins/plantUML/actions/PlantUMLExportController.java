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
import plugins.plantUML.models.SemanticsData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

            JLabel descriptionLabel = new JLabel("Please select the diagrams to export and choose an output folder:");
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

            Map<String, List<IDiagramUIModel>> groupedDiagrams = groupDiagramsByType();

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(new EmptyBorder(15, 10, 10, 10));
            contentPanel.setBackground(Color.WHITE);

            Map<JCheckBox, List<JCheckBox>> categoryCheckBoxMap = new HashMap<>(); // Store category-to-diagram checkboxes mapping


            for (Map.Entry<String, List<IDiagramUIModel>> entry : groupedDiagrams.entrySet()) {
                JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5)); // Less space
                categoryPanel.setBackground(Color.WHITE);

                JCheckBox categoryCheckBox = new JCheckBox(entry.getKey());
                categoryCheckBox.setBackground(Color.WHITE);
                categoryCheckBox.setFont(new Font(categoryCheckBox.getFont().getName(), Font.BOLD, 12));
                categoryPanel.add(categoryCheckBox);

                contentPanel.add(categoryPanel);

                JPanel checkBoxPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Less indentation
                checkBoxPanel.setBackground(Color.WHITE);
                checkBoxPanel.setBorder(new EmptyBorder(0, 25, 0, 0)); // Reduced left padding

                List<JCheckBox> diagramCheckBoxes = new ArrayList<>();

                for (IDiagramUIModel diagram : entry.getValue()) {
                    JCheckBox checkBox = new JCheckBox(diagram.getName());
                    checkBox.setActionCommand(diagram.getId());
                    checkBox.setBackground(Color.WHITE);
                    allCheckboxes.add(checkBox);
                    diagramCheckBoxes.add(checkBox);
                    checkBoxPanel.add(checkBox);

                    // Sync individual checkboxes with category
                    checkBox.addActionListener(e -> {
                        if (!checkBox.isSelected()) {
                            categoryCheckBox.setSelected(false);
                        } else if (diagramCheckBoxes.stream().allMatch(JCheckBox::isSelected)) {
                            categoryCheckBox.setSelected(true);
                        }
                    });
                }

                contentPanel.add(checkBoxPanel);
                contentPanel.add(Box.createVerticalStrut(3)); // Reduce vertical space

                categoryCheckBoxMap.put(categoryCheckBox, diagramCheckBoxes);

                // Sync category checkbox with all diagrams
                categoryCheckBox.addActionListener(e -> {
                    boolean isSelected = categoryCheckBox.isSelected();
                    for (JCheckBox checkBox : diagramCheckBoxes) {
                        checkBox.setSelected(isSelected);
                    }
                });
            }



            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setPreferredSize(new Dimension(700, 500));
            scrollPane.getViewport().setBackground(Color.WHITE);

            scrollPane.getVerticalScrollBar().setUnitIncrement(16);


            mainPanel.add(scrollPane, BorderLayout.CENTER);

            return mainPanel;
        }

        @Override
        public void prepare(IDialog dialog) {
        	this.dialog = dialog;
            dialog.setTitle("Select Diagrams to Export");
            dialog.setModal(true);
            dialog.setSize(600, 600);
        }

        @Override
        public void shown() {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

            JButton exportButton = new JButton("Export");
            JButton closeButton = new JButton("Close");

            exportButton.addActionListener((ActionEvent e) -> {
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

                File outputFolder = new File(outputFolderField.getText());
                if (!outputFolder.exists() || !outputFolder.isDirectory()) {
                    ApplicationManager.instance().getViewManager()
                    .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(),  "Please select a valid output folder.");
                    return;
                }

                DiagramExportPipeline pipeline = new DiagramExportPipeline(outputFolder);

                if (pipeline.exportDiagramList(selectedDiagrams)) {
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

        private Map<String, List<IDiagramUIModel>> groupDiagramsByType() {
            ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
            IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();

            // Use HashSet for Java 8 compatibility
            Set<String> allowedTypes = new HashSet<>(Arrays.asList(
                    "ClassDiagram", "ComponentDiagram", "DeploymentDiagram",
                    "InteractionDiagram", "ActivityDiagram", "UseCaseDiagram", "StateDiagram"
            ));

            Map<String, List<IDiagramUIModel>> grouped = new TreeMap<>();

            for (IDiagramUIModel diagram : allDiagrams) {
                String type = diagram.getType(); // Get the diagram type

                if (allowedTypes.contains(type)) { // Filter only allowed types
                    String formattedType;
                    if (type == "InteractionDiagram") formattedType = "Sequence Diagram";
                    else formattedType = type.replaceAll("([a-z])([A-Z])", "$1 $2"); // Add spaces between words
                    grouped.computeIfAbsent(formattedType, k -> new ArrayList<>()).add(diagram);
                }
            }
            return grouped;
        }

    }
}

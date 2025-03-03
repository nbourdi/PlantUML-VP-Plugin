package plugins.plantUML.actions;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import plugins.plantUML.imports.importers.DiagramImportPipeline;

public class PlantUMLImportController implements VPActionController {

    public void performAction(VPAction action) {
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        Component parentFrame = viewManager.getRootFrame();

        JRadioButton singleDiagramButton = new JRadioButton("Import Single Diagram (without semantics)");
        JRadioButton multipleDiagramsButton = new JRadioButton("Import Multiple Diagrams from Folder (with or without semantics)");

        ButtonGroup group = new ButtonGroup();
        group.add(singleDiagramButton);
        group.add(multipleDiagramsButton);

        singleDiagramButton.setSelected(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        panel.add(singleDiagramButton);
        panel.add(Box.createVerticalStrut(5)); 
        panel.add(multipleDiagramsButton);

        int choice = JOptionPane.showConfirmDialog(
            parentFrame,
            panel,
            "Select Import Option",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        if (singleDiagramButton.isSelected()) {
            // Handle single diagram import
            JFileChooser fileChooser = viewManager.createJFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    return "*.txt, *.puml, *.plantuml";
                }

                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || 
                           file.getName().toLowerCase().endsWith(".txt") || 
                           file.getName().toLowerCase().endsWith(".puml") ||
                            file.getName().toLowerCase().endsWith(".plantuml");
                }
            });

            int userSelection = fileChooser.showOpenDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                DiagramImportPipeline pipeline = new DiagramImportPipeline();
                pipeline.importFromSource(file);
            }
        } else if (multipleDiagramsButton.isSelected()) {
            // Handle multiple diagrams import
            JFileChooser folderChooser = viewManager.createJFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int userSelection = folderChooser.showOpenDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File folder = folderChooser.getSelectedFile();
                File[] files = folder.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".txt") || name.toLowerCase().endsWith(".puml") || name.toLowerCase().endsWith(".plantuml")
                );

                if (files != null && files.length > 0) {
                    DiagramImportPipeline pipeline = new DiagramImportPipeline(); // No single file required for batch import
                    pipeline.importMultipleFiles(Arrays.asList(files));
                } else {
                    JOptionPane.showMessageDialog(
                        parentFrame,
                        "No valid diagram files found in the selected folder.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        }
    }

    public void update(VPAction action) {
    }
}

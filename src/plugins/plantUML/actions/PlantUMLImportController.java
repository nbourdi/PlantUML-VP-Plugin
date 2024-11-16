package plugins.plantUML.actions;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import net.sourceforge.plantuml.syntax.SyntaxResult;
import plugins.plantUML.parser.PlantUMLParser;

public class PlantUMLImportController implements VPActionController {

    public void performAction(VPAction action) {
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        Component parentFrame = viewManager.getRootFrame();

        // Set up file chooser
        JFileChooser fileChooser = viewManager.createJFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "*.txt, *.puml";
            }

            public boolean accept(File file) {
                return file.isDirectory() || 
                       file.getName().toLowerCase().endsWith(".txt") || 
                       file.getName().toLowerCase().endsWith(".puml");
            }
        });

        int userSelection = fileChooser.showOpenDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            PlantUMLParser parser = new PlantUMLParser(file);

            try {
                // Parse the file
                SyntaxResult result = parser.parse();

                if (result.isError()) {
                    // If there are syntax errors, display them
                    StringBuilder errorMessage = new StringBuilder("Syntax errors detected:\n");
                    for (String error : result.getErrors()) {
                        errorMessage.append(" - ").append(error).append("\n");
                    }
                    viewManager.showMessageDialog(parentFrame, errorMessage.toString());
                } else {
                    // If parsing is successful
                    viewManager.showMessageDialog(parentFrame, 
                        "Syntax is valid.\nDiagram Type: " + result.getUmlDiagramType() +
                        "\nDescription: " + result.getDescription());
                }
            } catch (IOException e) {
                // Handle file reading exceptions
                viewManager.showMessageDialog(parentFrame, 
                    "Error reading file: " + e.getMessage());
            }
        }
    }

    public void update(VPAction action) {
        // No additional logic needed for updating the action
    }
}

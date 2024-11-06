package plugins.plantUML.actions;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class PlantUMLImportController implements VPActionController {

    public void performAction(VPAction action) {
        // Get the view manager and the parent component for the modal dialog
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        Component parentFrame = viewManager.getRootFrame();
        
        // Popup a file chooser to select the .txt or .puml file to import
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
        
        // Show open dialog and get the selected file
        int userSelection = fileChooser.showOpenDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            StringBuilder fileContent = new StringBuilder();

            // Read the file content
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }
                
                // Display success message with file path
                viewManager.showMessageDialog(parentFrame, "File imported from: " + file.getAbsolutePath());
                
                // Process or store fileContent.toString() as needed for the import functionality
                
            } catch (IOException e) {
                // Show error message if file reading fails
                viewManager.showMessageDialog(parentFrame, "Error reading file: " + e.getMessage());
            }
        }
    }

    public void update(VPAction action) {
    }
}

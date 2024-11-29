package plugins.plantUML.actions;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.factory.IModelElementFactory;

import net.sourceforge.plantuml.syntax.SyntaxResult;
import plugins.plantUML.imports.importers.DiagramImportPipeline;
import plugins.plantUML.parser.PlantUMLParser;

public class PlantUMLImportController implements VPActionController {

    public void performAction(VPAction action) {
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        Component parentFrame = viewManager.getRootFrame();

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
            DiagramImportPipeline pipeline = new DiagramImportPipeline(file);
            try {
				String source = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				pipeline.importFromSource(source);	
		
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            
//            PlantUMLParser parser = new PlantUMLParser(file);
////
//            try {
//                // Parse the file
//                SyntaxResult result = parser.parse();
//
//                if (result.isError()) {
//                    // If there are syntax errors, display them
//                    StringBuilder errorMessage = new StringBuilder("Syntax errors detected:\n");
//                    for (String error : result.getErrors()) {
//                        errorMessage.append(" - ").append(error).append("\n");
//                    }
//                    viewManager.showMessageDialog(parentFrame, errorMessage.toString());
//                } else {
//                    // If parsing is successful
//                    viewManager.showMessageDialog(parentFrame, 
//                        "Syntax is valid.\nDiagram Type: " + result.getUmlDiagramType() +
//                        "\nDescription: " + result.getDescription());
//                }
//            } catch (IOException e) {
//                // Handle file reading exceptions
//                viewManager.showMessageDialog(parentFrame, 
//                    "Error reading file: " + e.getMessage());
//            }

        }
//    	 DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
//
//         // Create a new Class Diagram
//         IClassDiagramUIModel classDiagram = (IClassDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
//         classDiagram.setName("Sample Class Diagram");
//
//         // Create a Class element
//         IClass class1 = IModelElementFactory.instance().createClass();
//         class1.setName("SampleClass");
//         class1.setVisibility("public");
//
//         // Add the class to the diagram
//         diagramManager.openDiagram(classDiagram);
//         // diagramManager.createDiagramElement(classDiagram, class1);
//
//         // Create another Class element
//         IClass class2 = IModelElementFactory.instance().createClass();
//         class2.setName("AnotherClass");
//         class2.setVisibility("public");
//
//         // Add the second class to the diagram
//         IClassUIModel class2Shape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, class2);
//         IClassUIModel class1Shape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, class1);
//
//         
//         
//         
//         // Connect the two classes with an Association
//         IAssociation association = IModelElementFactory.instance().createAssociation();
//         association.setFrom(class1);
//         association.setTo(class2);
//         
//         diagramManager.createConnector(classDiagram, association, class1Shape, class2Shape, null);
//         // Save and update the project
//         
//         diagramManager.layout(classDiagram, DiagramManager.LAYOUT_ORTHOGONAL);
//         ApplicationManager.instance().getProjectManager().saveProject();
    	
     
    }

    public void update(VPAction action) {
        // No additional logic needed for updating the action
    }
}


package plugins.plantUML.actions;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;

import plugins.plantUML.export.ClassDiagramExporter;
import plugins.plantUML.export.UseCaseDiagramExporter;

public class PlantUMLExportController implements VPActionController {

    public void performAction(VPAction action) {
        // Get the view manager and the parent component for the modal dialog
//        ViewManager viewManager = ApplicationManager.instance().getViewManager();
//        Component parentFrame = viewManager.getRootFrame();
//        
//        // Popup a file chooser for choosing the output file
//        JFileChooser fileChooser = viewManager.createJFileChooser();
//        fileChooser.setFileFilter(new FileFilter() {
//
//            public String getDescription() {
//                return "*.txt, *.puml";
//            }
//
//            public boolean accept(File file) {
//                return file.isDirectory() || 
//                       file.getName().toLowerCase().endsWith(".txt") || 
//                       file.getName().toLowerCase().endsWith(".puml");
//            }
//
//        });
//        
//        // Show save dialog and get the selected file
//        int userSelection = fileChooser.showSaveDialog(parentFrame);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File file = fileChooser.getSelectedFile();
//            String fileName = file.getName().toLowerCase();
//
//            // Ensure the file has either .txt or .puml extension
//            if (!fileName.endsWith(".txt") && !fileName.endsWith(".puml")) {
//                // Default to .txt if no extension is provided
//                file = new File(file.getAbsolutePath() + ".txt");
//            }
//
//            if (file != null && !file.isDirectory()) {
//                String messageContent = "Test message";  // Fixed test message
//
//                // Write the test message to the file
//                try (FileWriter writer = new FileWriter(file)) {
//                    writer.write(messageContent);
//                    viewManager.showMessageDialog(parentFrame, "Diagram exported to " + file.getAbsolutePath());
//                } catch (IOException e) {
//                    viewManager.showMessageDialog(parentFrame, "Error writing to file: " + e.getMessage());
//                }
//            }
//        }
        
        IDiagramUIModel activeDiagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
        String diagramType = activeDiagram.getType();
        
        switch(diagramType) {
        	case "ClassDiagram":	
        		ClassDiagramExporter cde = new ClassDiagramExporter();
        		cde.extract(activeDiagram);
        		break;
        	case "UseCaseDiagram":
        		UseCaseDiagramExporter ucde = new UseCaseDiagramExporter();
        		ucde.extract(activeDiagram);
        		break;
        	default:
        		ApplicationManager.instance().getViewManager().showMessage("TYPE " + diagramType + " not yet implemented");
              IDiagramElement[] allElements = activeDiagram.toDiagramElementArray(); 
        		
        		for (IDiagramElement diagramElement : allElements){
        			ApplicationManager.instance().getViewManager().showMessage("NAME:");
        			ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getName());
        			ApplicationManager.instance().getViewManager().showMessage("DESCRIPTION:");
        			ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getDescription()); // getDocumentation is deprecated
        			ApplicationManager.instance().getViewManager().showMessage("TYPE:");
        			ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getModelType());
        		}
        		IDiagramElement[] connectors = activeDiagram.toDiagramElementArray();
        		for (IDiagramElement connector : connectors) {
        			IModelElement connectorModel = connector.getModelElement();
        		    if (connectorModel instanceof IRelationship) {
        		    	
        		    	IRelationship relationship = (IRelationship) connectorModel;
        		    	String id = relationship.getId();
        		    	IModelElement source = relationship.getFrom();
                        IModelElement target = relationship.getTo();
        
                        String sourceAlias = source.getName();
                        String targetAlias = target.getName();
        		    	
        		        IConnectorUIModel connection = (IConnectorUIModel) connector;
        		        ApplicationManager.instance().getViewManager().showMessage("connector of type " + connector.getModelElement().getModelType() + " from: " + sourceAlias + " to " + targetAlias);
        				ApplicationManager.instance().getViewManager().showMessage(connector.getModelElement().getModelType());
        		        
        		    }
        		}
        }
        		


    }

    public void update(VPAction action) {
        // No update behavior needed for this action
    }
}

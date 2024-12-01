package plugins.plantUML.export;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;

import plugins.plantUML.export.writers.ClassUMLWriter;
import plugins.plantUML.export.writers.PlantJSONWriter;
import plugins.plantUML.export.writers.UseCaseWriter;
import plugins.plantUML.models.SemanticsData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiagramExportPipeline {
		
	private final File outputFolder;

    public DiagramExportPipeline(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * Executes the export pipeline for a given diagram.
     */
    
    private List<SemanticsData> projectSemanticsDatas = new ArrayList<SemanticsData>();
    
    public void export(IDiagramUIModel diagram) throws IOException {
        String diagramType = diagram.getType();
        String diagramTitle = diagram.getName();
        File outputFile = createOutputFile(diagramTitle, "uml");
        // File jsonFile = createOutputFile(diagramTitle, "json");

        try {
            switch (diagramType) {
                case "ClassDiagram":
                    ClassDiagramExporter cde = new ClassDiagramExporter(diagram);
                    cde.extract();
                    ClassUMLWriter classWriter = new ClassUMLWriter(
                            cde.getExportedClasses(), 
                            cde.getRelationshipDatas(), 
                            cde.getExportedPackages(), 
                            cde.getExportedNary(), 
                            cde.getNotes()
                        );
                        classWriter.writeToFile(outputFile);
                        
                        if (cde.getExportedSemantics() != null && !cde.getExportedSemantics().isEmpty()) {
                        	
                        	projectSemanticsDatas.addAll(cde.getExportedSemantics());
//                        	File jsonFile = createOutputFile(diagramTitle, "json");
//                        	PlantJSONWriter.writeToFile(jsonFile, cde.getExportedSemantics());
                        }
                        	
                        break;

                case "UseCaseDiagram":
                    UseCaseDiagramExporter ucde = new UseCaseDiagramExporter(diagram);
                    ucde.extract();
                    UseCaseWriter useCaseWriter = new UseCaseWriter(
                    		ucde.getExportedUseCases(), 
                    		ucde.getExportedRelationships(),
                    		ucde.getExportedPackages(),
                    		ucde.getExportedActors(), 
                    		ucde.getNotes()
                    );
                    useCaseWriter.writeToFile(outputFile);
                    
                    break;

                default:
                    ApplicationManager.instance().getViewManager()
                        .showMessage("TYPE " + diagramType + " not yet implemented");
                    break;
            }
        } catch (IOException ex) {
            ApplicationManager.instance().getViewManager()
            .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(),  "Error processing diagram: " + diagram.getName() + "\n" + ex.getMessage());
        }
    }
    
    public List<SemanticsData> exportPartialSemantics(DiagramExporter diagramExporter) {
    	if (diagramExporter.getExportedSemantics() != null && !diagramExporter.getExportedSemantics().isEmpty()) {
    		return diagramExporter.getExportedSemantics(); 
        }
    	return null;
    }
    
    private File createOutputFile(String title, String contentType) throws IOException {
        StringBuilder fileName = new StringBuilder();
        fileName.append(title.replaceAll("[^a-zA-Z0-9._-]", "_"));
        if (contentType.equals("json")) fileName.append("_semantics");
        fileName.append(".txt");
        File outputFile = new File(outputFolder, fileName.toString());
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
        }
        return outputFile;
    }

	public void exportDiagramList(List<IDiagramUIModel> selectedDiagrams) {
		for (IDiagramUIModel activeDiagram : selectedDiagrams) {
            try {
                export(activeDiagram);
            } catch (IOException ex) {
                ApplicationManager.instance().getViewManager()
                .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Error processing diagram: " + activeDiagram.getName() + "\n" + ex.getMessage());
            } catch (UnsupportedOperationException ex) {
                ApplicationManager.instance().getViewManager()
                .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), ex.getMessage());
            }
        }
		
		File jsonFile;
		try {
			jsonFile = createOutputFile("project_semantics", "json");
			PlantJSONWriter.writeToFile(jsonFile, projectSemanticsDatas);
		} catch (IOException e) {
			ApplicationManager.instance().getViewManager()
            .showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Error writing to json semantics file");
			e.printStackTrace();
		}
    	
		
	}
}
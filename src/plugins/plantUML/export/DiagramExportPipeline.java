package plugins.plantUML.export;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.plantUML.writer.ClassUMLWriter;
import plugins.plantUML.writer.PlantJSONWriter;
import plugins.plantUML.writer.UseCaseWriter;
import java.io.File;
import java.io.IOException;

public class DiagramExportPipeline {
		
	private final File outputFolder;

    public DiagramExportPipeline(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * Executes the export pipeline for a given diagram.
     */
    public void export(IDiagramUIModel diagram) throws IOException {
        String diagramType = diagram.getType();
        File outputFile = createOutputFile(diagram);

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
                        PlantJSONWriter.writeToFile(outputFile, cde.exportedSemantics);
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
    
    
    private File createOutputFile(IDiagramUIModel diagram) throws IOException {
        String fileName = diagram.getName().replaceAll("[^a-zA-Z0-9._-]", "_") + ".txt";
        File outputFile = new File(outputFolder, fileName);
        if (!outputFile.exists() && !outputFile.createNewFile()) {
            throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
        }
        return outputFile;
    }
}
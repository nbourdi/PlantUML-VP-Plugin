package plugins.plantUML.imports.importers;

import java.io.File;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import plugins.plantUML.imports.creators.ClassDiagramCreator;

public class DiagramImportPipeline {
	
	private final File inputFile;

    public DiagramImportPipeline(File inputFile) {
        this.inputFile = inputFile;
        
    }
    
    public void importFromSource(String source) {
    	// 1. syntax check
    	// 2. detect type and route with switch statement
    	
    	// temp just for class diagrams
    	
    	SourceStringReader reader = new SourceStringReader(source);
        ClassDiagram classDiagram = (ClassDiagram) reader.getBlocks().get(0).getDiagram();
    	
        ClassDiagramImporter importer = new ClassDiagramImporter(classDiagram);
        importer.extract();
        
        // will need big constructor to call with params
        ClassDiagramCreator creator = new ClassDiagramCreator(
        		importer.getClassDatas()
        		);
        creator.createDiagram(); 
    }
}

package plugins.plantUML.imports.importers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.vp.plugin.ApplicationManager;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.syntax.SyntaxChecker;
import net.sourceforge.plantuml.syntax.SyntaxResult;
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
        List<String> lines;
        boolean isCorrectSyntax;
		try {
			lines = Files.readAllLines(inputFile.toPath());
			SyntaxResult result = SyntaxChecker.checkSyntax(lines);
			if (result.isError()) {
                // If there are syntax errors, display them
                StringBuilder errorMessage = new StringBuilder("Syntax errors detected:\n");
                for (String error : result.getErrors()) {
                    errorMessage.append(" - ").append(error + "\nAt line: ").append(result.getLineLocation().getPosition());
                }
                ApplicationManager.instance().getViewManager().showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), 
                        errorMessage.toString());
                return;
			} else isCorrectSyntax = true; // TODO unnecessary 
		} catch (IOException e) {
			ApplicationManager.instance().getViewManager().showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), 
                    "Error reading file from system: " + e.getMessage());
		}
        
        
        
        ClassDiagram classDiagram = (ClassDiagram) reader.getBlocks().get(0).getDiagram();
        ClassDiagramImporter importer = new ClassDiagramImporter(classDiagram);
        importer.extract();
        
        // will need big constructor to call with params
        ClassDiagramCreator creator = new ClassDiagramCreator(
        		importer.getClassDatas(),
        		importer.getPackageDatas(),
        		importer.getNaryDatas()
        		);
        creator.createDiagram(); 
    }
}

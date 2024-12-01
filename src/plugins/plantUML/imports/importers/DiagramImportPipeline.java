package plugins.plantUML.imports.importers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vp.plugin.ApplicationManager;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.jsondiagram.JsonDiagram;
import net.sourceforge.plantuml.syntax.SyntaxChecker;
import net.sourceforge.plantuml.syntax.SyntaxResult;
import plugins.plantUML.imports.creators.ClassDiagramCreator;
import plugins.plantUML.models.SemanticsData;

public class DiagramImportPipeline {

  //  private final File inputFile;
    
    // Hash map to contain the mapping of entities to semanticsData for lookup when creating 
    // String key is of format ownerName|ownerType to uniquely map to an element
    Map<String, SemanticsData> semanticsMap = new HashMap<>();

//    public DiagramImportPipeline(File inputFile) {
//        this.inputFile = inputFile;
//    }

    public void importMultipleFiles(List<File> files) {
        List<File> jsonFiles = new ArrayList<>();
        List<File> otherFiles = new ArrayList<>();

        for (File file : files) {
            try {
                String source = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                SourceStringReader reader = new SourceStringReader(source);
                if (reader.getBlocks().get(0).getDiagram() instanceof JsonDiagram) {
                    jsonFiles.add(file);
                } else {
                    otherFiles.add(file);
                }
            } catch (IOException e) {
                ApplicationManager.instance().getViewManager().showMessageDialog(
                        ApplicationManager.instance().getViewManager().getRootFrame(),
                        "Error reading file: " + file.getName() + "\n" + e.getMessage()
                );
            } catch (Exception e) {
                ApplicationManager.instance().getViewManager().showMessageDialog(
                        ApplicationManager.instance().getViewManager().getRootFrame(),
                        "Error processing file: " + file.getName() + "\n" + e.getMessage()
                );
            }
        }
        
        if (jsonFiles.size() > 1) {
        	ApplicationManager.instance().getViewManager().showMessageDialog(
                    ApplicationManager.instance().getViewManager().getRootFrame(),
                    "Error: Cannot process more than one json diagram file.");
        	return;
        }
        
        if (jsonFiles.size() != 0) {
        	File jsonFile = jsonFiles.get(0);

	        // Import JSON semantics file FIRST
	        try {
	            String source = new String(Files.readAllBytes(jsonFiles.get(0).toPath()), StandardCharsets.UTF_8);
	            importSemantics(source);
	        } catch (IOException e) {
	            ApplicationManager.instance().getViewManager().showMessageDialog(
	                    ApplicationManager.instance().getViewManager().getRootFrame(),
	                    "Error importing JSON diagram: " + jsonFile.getName() + "\n" + e.getMessage());
	            
	        }
        }

        // Import other diagrams
        for (File otherFile : otherFiles) {
        	importFromSource(otherFile);
//            try {
//                String source = new String(Files.readAllBytes(otherFile.toPath()), StandardCharsets.UTF_8);
//                importFromSource(source);
//            } catch (IOException e) {
//                ApplicationManager.instance().getViewManager().showMessageDialog(
//                        ApplicationManager.instance().getViewManager().getRootFrame(),
//                        "Error importing diagram: " + otherFile.getName() + "\n" + e.getMessage()
//                );
//            }
        }
    }
    private void importSemantics(String source) {
    	
        String jsonContent = extractJsonContent(source);

        if (jsonContent == null) {
        	ApplicationManager.instance().getViewManager().showMessageDialog(
                    ApplicationManager.instance().getViewManager().getRootFrame(), "No valid JSON content found in the source.");
            return;
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parsing  JSON source string into a list of SemanticsData 
            List<SemanticsData> semanticsDataList = objectMapper.readValue(source, new TypeReference<List<SemanticsData>>() {});

            for (SemanticsData semanticsData : semanticsDataList) {
                String key = semanticsData.getOwnerName() + "|" + semanticsData.getOwnerType();
                semanticsMap.put(key, semanticsData);
            }
        } catch (IOException e) {
        	ApplicationManager.instance().getViewManager().showMessageDialog(
                    ApplicationManager.instance().getViewManager().getRootFrame(), "Error parsing SemanticsData JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String extractJsonContent(String source) {
        // regex to match json between @startuml and @enduml
        Pattern pattern = Pattern.compile("@startuml(.*?)@enduml", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(source);

        if (matcher.find()) {
            String content = matcher.group(1).trim();
            if (content.startsWith("{") || content.startsWith("[")) {
                return content;
            }
        }
        return null;
    }
    
	public void importFromSource(File sourceFile) {
        try {
            List<String> lines = Files.readAllLines(sourceFile.toPath());
            SyntaxResult result = SyntaxChecker.checkSyntax(lines);

            if (result.isError()) {
                // If there are syntax errors, display them
                StringBuilder errorMessage = new StringBuilder("Syntax errors detected:\n");
                for (String error : result.getErrors()) {
                    errorMessage.append(" - ").append(error)
                            .append("\nAt line: ").append(result.getLineLocation().getPosition());
                }
                ApplicationManager.instance().getViewManager().showMessageDialog(
                        ApplicationManager.instance().getViewManager().getRootFrame(),
                        errorMessage.toString()
                );
                return;
            }

            String source = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
            SourceStringReader reader = new SourceStringReader(source);
            ClassDiagram classDiagram = (ClassDiagram) reader.getBlocks().get(0).getDiagram();
            
            String diagramTitle = sourceFile.getName();
            ClassDiagramImporter importer = new ClassDiagramImporter(classDiagram, semanticsMap);
            importer.extract();

            ClassDiagramCreator creator = new ClassDiagramCreator(
            		diagramTitle,
                    importer.getClassDatas(),
                    importer.getPackageDatas(),
                    importer.getNaryDatas(),
                    importer.getRelationshipDatas(),
                    importer.getNoteDatas()
            );
            creator.createDiagram();
        } catch (IOException e) {
            ApplicationManager.instance().getViewManager().showMessageDialog(
                    ApplicationManager.instance().getViewManager().getRootFrame(),
                    "Error reading source: " + e.getMessage()
            );
        }
    }
}

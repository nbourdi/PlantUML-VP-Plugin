package plugins.plantUML.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;

import plugins.plantUML.export.models.AttributeData;
import plugins.plantUML.export.models.ClassData;
import plugins.plantUML.export.models.OperationData;
import plugins.plantUML.export.models.PackageData;
import plugins.plantUML.export.models.RelationshipData;

public class PlantUMLWriter {
    
	private List<PackageData> packages;
    private List<ClassData> classes;
    private List<RelationshipData> relationships;

    public PlantUMLWriter(List<ClassData> classes, List<RelationshipData> relationships, List<PackageData> packages) {
    	this.packages = packages;
        this.classes = classes;
        this.relationships = relationships;
    }

    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");
        
        for (PackageData packageData : packages) {
        	if(!packageData.isSubpackage())
        		plantUMLContent.append(writePackage(packageData));
        }

        for (ClassData classData : classes) {
        	if(!classData.isInPackage())  
        		plantUMLContent.append(writeClass(classData));
        }

        for (RelationshipData relationship : relationships) {
            plantUMLContent.append(writeRelationship(relationship));
        }

        plantUMLContent.append("@enduml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(plantUMLContent.toString());
        }
    }
    
    private String writePackage(PackageData packageData) {
    	StringBuilder packageString = new StringBuilder();
    	String name = formatName(packageData.getPackageName());
    	
    	packageString.append("package " ).append(name).append(" {\n");
    	
    	for (ClassData classData : packageData.getClasses()) {
    		packageString.append(writeClass(classData));
    		
    	}
    	
    	for (PackageData subPackage : packageData.getSubPackages()) {
    		ApplicationManager.instance().getViewManager().showMessage("subpackages..." );
    		packageString.append(writePackage(subPackage));
    		
    	}
    	
    	packageString.append("}\n");
		return packageString.toString();
    	
    }

    private String writeClass(ClassData classData) {
    	StringBuilder classString = new StringBuilder();
    	String name = formatName(classData.getName());

    	// class declarations
    	
    	// equivalents
    	Map<String, String> keywordStereotypes = new HashMap<>();
    	keywordStereotypes.put("interface", "interface");
    	keywordStereotypes.put("enumeration", "enum");  
    	keywordStereotypes.put("enum", "enum");
    	keywordStereotypes.put("metaclass", "metaclass");
    	keywordStereotypes.put("struct", "struct");
    	keywordStereotypes.put("entity", "entity");

    	// Check if there's exactly one stereotype and if it matches a keyword
    	if (classData.getStereotypes().size() == 1) {
    	    String stereotype = classData.getStereotypes().get(0).toLowerCase();  
    	    if (keywordStereotypes.containsKey(stereotype)) {
    	        classString.append(keywordStereotypes.get(stereotype)).append(" ").append(name);
    	    } else {
    	        // if not puml keyword
    	        classString.append("class ").append(name).append(" <<").append(stereotype).append(">>");
    	    }
    	} else {
    	    // Default to "class" with any stereotypes listed
    	    classString.append("class ").append(name);
    	    if (!classData.getStereotypes().isEmpty()) {
    	        String stereotypesString = classData.getStereotypes().stream()
    	            .map(stereotype -> "<<" + stereotype + ">>")
    	            .collect(Collectors.joining(", "));
    	        classString.append(" ").append(stereotypesString);
    	    }
    	}

    	classString.append(" {\n");



        // Attributes
        for (AttributeData attribute : classData.getAttributes()) {
        	String visibilityChar = writeVisibility(attribute.getVisibility());
        	
        	classString.append("  ").append(visibilityChar);
        	if (attribute.isStatic()) classString.append("{static} ");
        	classString.append(attribute.getName());
        	
        	if (attribute.getType() != null) {
        		classString.append(": ").append(attribute.getType());
        	}
        	
            classString.append("\n");
        }

        // Add operations
        for (OperationData operation : classData.getOperations()) {
            String visibilityChar = writeVisibility(operation.getVisibility());

            classString.append("  ").append(visibilityChar);
            
            if (operation.isAbstract()) classString.append("{abstract} ");
            
        	if (operation.isStatic()) classString.append("{static} ");
        	
            classString.append(operation.getName()).append("(");

            // Add parameters
            List<OperationData.Parameter> parameters = operation.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                OperationData.Parameter param = parameters.get(i);

                // Append name
                classString.append(param.getName());

                // Append type if available
                if (param.getType() != null && !param.getType().isEmpty()) {
                    classString.append(": ").append(param.getType());
                }

                // Append default value if available
                if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
                    classString.append(" = ").append(param.getDefaultValue());
                }

                // Add comma between parameters, but not after the last one
                if (i < parameters.size() - 1) {
                    classString.append(", ");
                }
            }
            
            classString.append(")");

            // Append return type if available
            if (operation.getReturnType() != null && !operation.getReturnType().isEmpty()) {
                classString.append(": ").append(operation.getReturnType());
            }

            classString.append("\n");
        }

        classString.append("}\n");
        return classString.toString();
    }
    
    private String writeVisibility(String visibility) {
    	String visibilityCharacter = "";
    	switch (visibility) {
    		case "private": 
    			visibilityCharacter = "-";
    			break;
    		case "protected": 
    			visibilityCharacter = "#";
    			break;
    		case 
	    		"package": visibilityCharacter = "~";
	    		break;
    		case "public": 
    			visibilityCharacter = "+";
    			break;    	
    	}
    	return visibilityCharacter;
    }

    private String formatName(String name) {
		/*
		 * Spaces and other non-letter characters are not 
		 * supported as names for plantUML, hence we need to format
		 * using "as" syntax
		 * e.g. class "name with space" as nameWithSpace {}
		 */
	    if (!name.matches("[a-zA-Z0-9]+")) {
//	        // Remove non-letter characters and capitalize each word to form an identifier
//	        String formattedName = name.replaceAll("[^a-zA-Z0-9]+", " "); // Replace non-letters with space
//	        StringBuilder camelCaseName = new StringBuilder();
//	        for (String word : formattedName.split(" ")) {
//	            if (!word.isEmpty()) {
//	                camelCaseName.append(Character.toUpperCase(word.charAt(0)))
//	                             .append(word.substring(1).toLowerCase());
//	            }
//	        }
//	        return "\"" + name + "\" as " + camelCaseName.toString();
	        return "\"" + name + "\"";
	    }
	    return name;
    }


	private String writeRelationship(RelationshipData relationship) {
        // Map relationship types to PlantUML syntax (e.g., association, inheritance)
        String symbol = "--";  // Default association
        if (relationship.getType().equals("generalization")) {
            symbol = "<|--";
        } else if (relationship.getType().equals("aggregation")) {
            symbol = "o--";
        } else if (relationship.getType().equals("composition")) {
            symbol = "*--";
        }

        return relationship.getSource() + " " + symbol + " " + relationship.getTarget() + "\n";
    }
}

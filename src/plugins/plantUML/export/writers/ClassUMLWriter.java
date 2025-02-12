package plugins.plantUML.export.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;

public class ClassUMLWriter extends PlantUMLWriter {
    
	private List<PackageData> packages;
    private List<ClassData> classes;
    private List<RelationshipData> relationships;
    private List<NaryData> naries;

    public ClassUMLWriter(List<ClassData> classes, List<RelationshipData> relationships, List<PackageData> packages, List<NaryData> naries, List<NoteData> notes) {
    	super(notes);
    	this.packages = packages;
        this.classes = classes;
        this.relationships = relationships;
        this.setNaries(naries);
    }

    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");
        
        for (PackageData packageData : packages) {
        	if(!packageData.isSubpackage())
        		plantUMLContent.append(writePackage(packageData, ""));
        }

        for (ClassData classData : classes) {
        	if(!classData.isInPackage())  
        		plantUMLContent.append(writeClass(classData, ""));
        }
        
        for (NaryData naryData : naries) {
        	if (!naryData.isInPackage())
        		plantUMLContent.append(writeNary(naryData, ""));
        }
        
        plantUMLContent.append(writeNotes());

        for (RelationshipData relationship : relationships) {
            plantUMLContent.append(writeRelationship(relationship));
        }
        
        plantUMLContent.append("@enduml");
		try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
			writer.write(plantUMLContent.toString());
		}
    }
    
    private String writeNary(NaryData naryData, String indent) {
		
	    StringBuilder naryString = new StringBuilder();
	    String alias = naryData.getAlias();
	    String name = naryData.getName();
	    
	    naryString.append(indent).append("diamond ")
                .append("\"").append(name).append("\"").append(" as ").append(alias).append("\n");
	    
	    return naryString.toString();
	}
	

	private String writePackage(PackageData packageData, String indent) {
    	StringBuilder packageString = new StringBuilder();
    	String name = formatName(packageData.getName());
    	
    	packageString.append(indent).append("package " ).append(name).append(" {\n");
    	
    	for (ClassData classData : packageData.getClasses()) {
    		packageString.append(writeClass(classData, indent + "\t"));
    		
    	}
    	
    	for (NaryData naryData : packageData.getNaries()) {
    		packageString.append(writeNary(naryData, indent + "\t"));
    	}
    	
    	for (PackageData subPackage : packageData.getSubPackages()) {
    		packageString.append(writePackage(subPackage, indent + "\t"));
    		
    	}
    	
    	packageString.append(indent).append("}\n");
		return packageString.toString();
    	
    }

    private String writeClass(ClassData classData, String indent) {
    	StringBuilder classString = new StringBuilder();
    	String name = formatName(classData.getName());
    	String aliasDeclaration = formatAlias(classData.getName()).equals(classData.getName()) ? "" : (" as " + formatAlias(classData.getName()));
    	
    	// equivalents mapping
    	Map<String, String> keywordStereotypes = new HashMap<>();
    	keywordStereotypes.put("interface", "interface");
    	keywordStereotypes.put("enumeration", "enum");  
    	keywordStereotypes.put("enum", "enum");
    	keywordStereotypes.put("metaclass", "metaclass");
    	keywordStereotypes.put("struct", "struct");
    	keywordStereotypes.put("entity", "entity");
    	
    	classString.append(indent);
    	classString.append(writeVisibility(classData.getVisibility()));
    	
    	if (classData.isAbstract()) classString.append("abstract ");
    	if (classData.getStereotypes().size() == 1 && !classData.isAbstract()) {
    	    String stereotype = classData.getStereotypes().get(0).toLowerCase();  
    	    if (keywordStereotypes.containsKey(stereotype)) {
    	        classString.append(keywordStereotypes.get(stereotype)).append(" ").append(name).append(aliasDeclaration);
    	    } else {
    	        // if not puml keyword
    	        classString.append("class ").append(name).append(aliasDeclaration).append(" <<").append(stereotype).append(">>");
    	    }
    	} else {
    	    // Default to "class" with any stereotypes listed
    	    classString.append("class ").append(name).append(aliasDeclaration);
    	    if (!classData.getStereotypes().isEmpty()) {
    	        String stereotypesString = classData.getStereotypes().stream()
    	            .map(stereotype -> "<<" + stereotype + ">>")
    	            .collect(Collectors.joining(", "));
    	        classString.append(" ").append(stereotypesString);
    	    }
    	}

    	classString.append(" {\n");

        // Attributes
		writeAttributesAndOperations(classData.getAttributes(), classData.getOperations(), indent, classString);

		classString.append(indent).append("}\n");
        return classString.toString();
    }






	private String writeRelationship(RelationshipData relationship) {

		return relationship.toExportFormat();
    }

	public List<NaryData> getNaries() {
		return naries;
	}

	public void setNaries(List<NaryData> naries) {
		this.naries = naries;
	}
}

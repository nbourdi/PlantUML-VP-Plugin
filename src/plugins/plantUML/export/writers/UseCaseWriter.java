package plugins.plantUML.export.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;

import plugins.plantUML.models.ActorData;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.UseCaseData;

public class UseCaseWriter extends PlantUMLWriter {
    
	private List<PackageData> packages;
    private List<ActorData> actors;
    private List<RelationshipData> relationships;
    private List<UseCaseData> usecases;

    public UseCaseWriter(List<UseCaseData> usecases, List<RelationshipData> relationships, List<PackageData> packages, List<ActorData> actors, List<NoteData> notes) {
    	super(notes);
    	this.packages = packages;
    	this.usecases = usecases;
    	this.actors = actors;
        this.relationships = relationships;
    }

    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");
        
        for (PackageData packageData : packages) {
        	if(!packageData.isSubpackage())
        		plantUMLContent.append(writePackage(packageData, ""));
        }

        for (ActorData actorData : actors) {
        	if(!actorData.isInPackage())  
        		plantUMLContent.append(writeActor(actorData, ""));
        }

        for (UseCaseData usecaseData : usecases) {
        	if(!usecaseData.isInPackage())  
        		plantUMLContent.append(writeUseCase(usecaseData, ""));
        }
        
        plantUMLContent.append(writeNotes());

        for (RelationshipData relationship : relationships) {
            plantUMLContent.append(writeRelationship(relationship));
        }
        
        plantUMLContent.append("@enduml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(plantUMLContent.toString());
        }
    }
    
    

	private String writePackage(PackageData packageData, String indent) {
    	StringBuilder packageString = new StringBuilder();
    	String name = formatName(packageData.getName());
    	String definition = packageData.isRectangle() ? "rectangle " : "package " ;
    	packageString.append(indent).append(definition).append(name).append(" {\n");
    	
    	for (ActorData actorData : packageData.getActors()) {
    		packageString.append(writeActor(actorData, indent + "\t"));
    	}
    	for (UseCaseData useCaseData : packageData.getUseCases()) {
    		packageString.append(writeUseCase(useCaseData, indent + "\t"));
    	}
    	
    	for (PackageData subPackage : packageData.getSubPackages()) {
    		packageString.append(writePackage(subPackage, indent + "\t"));
    		
    	}
    	
    	packageString.append(indent).append("}\n");
		return packageString.toString();
    }


	private String writeUseCase(UseCaseData useCaseData, String indent) {
	    StringBuilder usecaseString = new StringBuilder();
	    String name = useCaseData.getName();
	    String business = useCaseData.isBusiness() ? "/" : "";
	    usecaseString.append(indent).append("usecase").append(business)
	                 .append(" (").append(name).append(")");
	    
	    if (!useCaseData.getStereotypes().isEmpty()) {
	        String stereotypesString = useCaseData.getStereotypes().stream()
	            .filter(stereotype -> !"UseCase".equals(stereotype)) // Exclude "UseCase", VP auto applies it to every use case for some reason
	            .map(stereotype -> "<<" + stereotype + ">>")
	            .collect(Collectors.joining(", "));
	        if (!stereotypesString.isEmpty()) { 
	            usecaseString.append(" ").append(stereotypesString);
	        }
	    }
	    
	    usecaseString.append("\n");
	    return usecaseString.toString();
	}


	private String writeActor(ActorData actorData, String indent) {
		StringBuilder actorString = new StringBuilder();
		String name = actorData.getName();
		String business = actorData.isBusiness() ? "/" : "";
		actorString.append(indent).append("actor ").append(business)
					.append(" :" + name + ":");
		if (!actorData.getStereotypes().isEmpty()) {
	        String stereotypesString = actorData.getStereotypes().stream()
	            .map(stereotype -> "<<" + stereotype + ">>")
	            .collect(Collectors.joining(", "));
	        actorString.append(" ").append(stereotypesString);
	    }
		actorString.append("\n");
		return actorString.toString();
	}

	private String writeRelationship(RelationshipData relationship) {
		return relationship.toExportFormat();
    }

}

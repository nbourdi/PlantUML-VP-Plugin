package plugins.plantUML.export.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.ComponentData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.ComponentData.PortData;

public class ComponentUMLWriter extends PlantUMLWriter {

	private List<ComponentData> components;
	private List<ClassData> interfaces;
	private List<PackageData> packages;
	private List<RelationshipData> relationships;

	public ComponentUMLWriter(List<NoteData> notes, List<ComponentData> components, List<ClassData> interfaces, List<PackageData> packages, List<RelationshipData> relationships) {
		super(notes);
		this.components = components;
		this.interfaces = interfaces;
		this.packages = packages;
		this.relationships = relationships;
	}

	@Override
	public void writeToFile(File file) throws IOException {
		StringBuilder plantUMLContent = new StringBuilder("@startuml\n");



		for (ComponentData componentData : components) {
			if(!componentData.isInPackage() && !componentData.isResident())  
				plantUMLContent.append(writeComponent(componentData, ""));
		}

		for (ClassData interfaceData : interfaces) {
			if(!interfaceData.isInPackage())  
				plantUMLContent.append(writeInterface(interfaceData, ""));
		}

		for (PackageData packageData : packages) {
			if(!packageData.isSubpackage())
				plantUMLContent.append(writePackage(packageData, ""));
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
	
	private String writeRelationship(RelationshipData relationship) {

		return relationship.toExportFormat();
    }
	private String writePackage(PackageData packageData, String indent) {
		StringBuilder packageString = new StringBuilder();
		String name = formatName(packageData.getName());

		packageString.append(indent).append("package " ).append(name).append(" {\n");

		for (ClassData interfaceData : packageData.getClasses()) {
			packageString.append(writeInterface(interfaceData, indent + "\t"));
		}

		for (ComponentData componentData : packageData.getComponents()) {
			packageString.append(writeComponent(componentData, indent + "\t"));
		}

		for (PackageData subPackage : packageData.getSubPackages()) {
			packageString.append(writePackage(subPackage, indent + "\t"));
		}

		packageString.append(indent + "}\n");
		return packageString.toString();
	}

	private String writeInterface(ClassData interfaceData, String indent) {
		StringBuilder interfaceString = new StringBuilder();
		String name = formatName(interfaceData.getName());
		interfaceString.append(indent);
		interfaceString.append("() ").append(name); // as?
		if (!interfaceData.getStereotypes().isEmpty()) {
			String stereotypesString = interfaceData.getStereotypes().stream()
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			interfaceString.append(" ").append(stereotypesString);
		}
		interfaceString.append("\n");
		return interfaceString.toString();
	}

	private String writeComponent(ComponentData componentData, String indent) {
		StringBuilder componentString = new StringBuilder();
		String name = formatName(componentData.getName()) ;


		componentString.append(indent).append("component ");

		componentString.append("[").append(name).append("]");	// TODO: "as" may be needed
		if (!componentData.getStereotypes().isEmpty()) {
			String stereotypesString = componentData.getStereotypes().stream()
					.filter(stereotype -> !"component".equals(stereotype)) // Exclude "UseCase", VP auto applies it to every use case for some reason
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			if (!stereotypesString.isEmpty()) { 
				componentString.append(" ").append(stereotypesString);
			}
		}

		// resident components & ports
		List<ComponentData> residents = componentData.getResidents();
		List<PortData> ports = componentData.getPorts();

		componentString.append(" {\n");

		if (residents != null && !residents.isEmpty()) {
			for (ComponentData resident : residents) {
				componentString.append(writeComponent(resident, indent + "\t"));
			}
		}
		
		if (ports != null && !ports.isEmpty()) {
			for (PortData port : ports) {
				
				String alias = port.getAlias();
				String portName = port.getName();
				
				componentString.append(indent + "\t");
				componentString.append("port ")
					.append("\"").append(portName).append("\"")
	    		    .append(" as " + alias + "\n");
			}
		}
		
		componentString.append(indent + "}");
		componentString.append("\n");
		return componentString.toString();
	}


}

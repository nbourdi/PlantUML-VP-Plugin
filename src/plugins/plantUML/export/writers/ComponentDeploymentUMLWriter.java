package plugins.plantUML.export.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import plugins.plantUML.models.*;
import plugins.plantUML.models.ComponentData.PortData;

public class ComponentDeploymentUMLWriter extends PlantUMLWriter {

	private List<ComponentData> components;
	private List<ClassData> interfaces;
	private List<PackageData> packages;
	private List<RelationshipData> relationships;
	private List<ArtifactData> artifacts;

	public ComponentDeploymentUMLWriter(List<NoteData> notes, List<ComponentData> components, List<ClassData> interfaces, List<ArtifactData> artifacts, List<PackageData> packages, List<RelationshipData> relationships) {
		super(notes);
		this.components = components;
		this.interfaces = interfaces;
		this.artifacts = artifacts;
		this.packages = packages;
		this.relationships = relationships;
	}

	@Override
	public void writeToFile(File file) throws IOException {
		StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

		// Allowmixing parameter to allow for class-type interfaces
		plantUMLContent.append("allowmixing\n");

		for (ComponentData componentData : components) {
			if(!componentData.isInPackage() && !componentData.isResident())  
				plantUMLContent.append(writeComponent(componentData, ""));
		}

		for (ClassData interfaceData : interfaces) {
			if(!interfaceData.isInPackage())  
				plantUMLContent.append(writeInterface(interfaceData, ""));
		}

		for (ArtifactData artifactData : artifacts) {
			if(!artifactData.isInPackage() && !artifactData.isInNode())
				plantUMLContent.append(writeArtifact(artifactData, ""));
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

	private String writeArtifact(ArtifactData artifactData, String indent) {
		StringBuilder artifactString = new StringBuilder();
		String name = formatName(artifactData.getName());

		artifactString.append(indent).append("artifact ").append(name).append(" as ").append(formatAlias(artifactData.getName())).append("\n");
		return artifactString.toString();
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

		for (ArtifactData artifactData : packageData.getArtifacts()) {
			packageString.append(writeArtifact(artifactData, indent + "\t"));
		}
		for (ComponentData componentData : packageData.getComponents()) {
			packageString.append(writeComponent(componentData, indent + "\t"));
		}

		for (PackageData subPackage : packageData.getSubPackages()) {
			packageString.append(writePackage(subPackage, indent + "\t"));
		}

		packageString.append(indent).append("}\n");
		return packageString.toString();
	}

	private String writeInterface(ClassData interfaceData, String indent) {
		StringBuilder interfaceString = new StringBuilder();
		String name = formatName(interfaceData.getName());
		String aliasDeclaration = formatAlias(interfaceData.getName()).equals(interfaceData.getName()) ? "" : (" as " + formatAlias(interfaceData.getName()));

		interfaceString.append(indent);
		boolean hasAttrAndOps = (!interfaceData.getAttributes().isEmpty() || !interfaceData.getOperations().isEmpty());
		List<String> filteredStereotypes;
		if (hasAttrAndOps) {
			interfaceString.append("class ");
			filteredStereotypes = interfaceData.getStereotypes();
		} else {
			interfaceString.append("() ");
			filteredStereotypes = interfaceData.getStereotypes().stream()
					.filter(stereotype -> !"interface".equalsIgnoreCase(stereotype)) // Skip "interface"
					.collect(Collectors.toList());
		}

		interfaceString.append(name).append(aliasDeclaration);

		if (!filteredStereotypes.isEmpty()) {
			String stereotypesString = filteredStereotypes.stream()
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			interfaceString.append(" ").append(stereotypesString);
		}
		if (hasAttrAndOps) {
			interfaceString.append(" {\n");
			writeAttributesAndOperations(interfaceData.getAttributes(), interfaceData.getOperations(), indent, interfaceString);
			interfaceString.append(indent).append("}\n");
		}
		interfaceString.append("\n");
		return interfaceString.toString();
	}


	private String writeComponent(ComponentData componentData, String indent) {
		StringBuilder componentString = new StringBuilder();
		String name = formatName(componentData.getName()) ;
		String aliasDeclaration = formatAlias(componentData.getName()).equals(componentData.getName()) ? "" : (" as " + formatAlias(componentData.getName()));

		List<AttributeData> attributeData = componentData.getAttributes();
		List<OperationData> operationData = componentData.getOperations();
		boolean hasOpsAndAttrs = !attributeData.isEmpty() || !operationData.isEmpty();
		if (hasOpsAndAttrs) {
			// we will need to create a package to contain the component and the interface it will implement
			componentString.append("package ").append(name).append(" {\n");
		}
		componentString.append(indent);

		if (componentData.isNodeComponent()) {
			componentString.append("node ");
		} else {
			componentString.append("component ");
		}
		componentString.append(name).append(aliasDeclaration);

		if (!componentData.getStereotypes().isEmpty()) {
			String stereotypesString = componentData.getStereotypes().stream()
					.filter(stereotype -> !"component".equals(stereotype)) // Exclude "component", VP auto applies it to every use case for some reason
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			if (!stereotypesString.isEmpty()) { 
				componentString.append(" ").append(stereotypesString);
			}
		}
		// resident components & ports
		List<ComponentData> residents = componentData.getResidents();
		List<PortData> ports = componentData.getPorts();
		List<ArtifactData> artifacts = componentData.getArtifacts();

		componentString.append(" {\n");

		if (residents != null && !residents.isEmpty()) {
			for (ComponentData resident : residents) {
				componentString.append(writeComponent(resident, indent + "\t"));
			}
		}

		if (artifacts != null && !artifacts.isEmpty()) {
			for (ArtifactData artifact : artifacts) {
				componentString.append(writeArtifact(artifact, indent + "\t"));
			}
		}
		
		if (ports != null && !ports.isEmpty()) {
			for (PortData port : ports) {
				
				String alias = port.getAlias();
				String portName = port.getName();
				
				componentString.append(indent).append("\t");
				componentString.append("port ")
                        .append("\"").append(portName).append("\"").append(" as ").append(alias).append("\n");
			}
		}
		
		componentString.append(indent).append("}");
		componentString.append("\n");

		if (hasOpsAndAttrs) {
			componentString.append(writeImplInterface(componentData, indent));
			componentString.append("}\n");
		}
		return componentString.toString();
	}

	private String writeImplInterface(ComponentData componentData, String indent) {
		String name = formatName("I" + componentData.getName());
		StringBuilder interfaceString = new StringBuilder();
		String aliasDeclaration = formatAlias(componentData.getName()).equals(componentData.getName()) ? "" : (" as I" + formatAlias(componentData.getName()));

		interfaceString.append(indent).append("class ").append(name).append(aliasDeclaration).append(" <<interface>>");
		interfaceString.append(" {\n");

		writeAttributesAndOperations(componentData.getAttributes(), componentData.getOperations(), indent, interfaceString);
		interfaceString.append(indent).append("}\n");

		interfaceString.append(indent).append(formatAlias(componentData.getName())).append(" ..|> ").append("I" + formatAlias(componentData.getName()));
		interfaceString.append("\n");
		return interfaceString.toString();
	}
}

package plugins.plantUML.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IComponent;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IPort;
import com.vp.plugin.model.IRelationship;

import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.ComponentData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.ComponentData.PortData;

public class ComponentDiagramExporter extends DiagramExporter {
	
	private IDiagramUIModel diagram;
	
	List<ComponentData> exportedComponents = new ArrayList<>();
	List<ClassData> exportedInterfaces = new ArrayList<ClassData>();
	List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
	
	List<NoteData> exportedNotes = new ArrayList<>();

	List<PackageData> exportedPackages = new ArrayList<PackageData>();
	List<PortData> allExportedPorts = new ArrayList<>();  

	public ComponentDiagramExporter(IDiagramUIModel diagram) throws IOException {
		this.diagram = diagram;
	}
	
	@Override
	public void extract() {
		
		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement != null) {

				if (modelElement instanceof IComponent) {
					if (!(modelElement.getParent() instanceof IPackage) && !(modelElement.getParent() instanceof IComponent))
						extractComponent((IComponent) modelElement, null, null);
				} else if (modelElement instanceof IClass) { // interfaces are classes with stereotype "Interface"
					if (!(modelElement.getParent() instanceof IPackage))
						extractInterface((IClass) modelElement, null);
				
				} else if (modelElement instanceof IPackage) {
					extractPackage((IPackage) modelElement);
				} else if (modelElement instanceof INOTE) {
					this.extractNote((INOTE) modelElement);
				} else if (modelElement instanceof IRelationship) {
					extractRelationship((IRelationship) modelElement);
				} else if (modelElement instanceof IPort) {
					
					//  just to not  show the message
				} else {
					ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element "
							+ modelElement.getName() +" is of unsupported type and will not be processed ... ");
				}
			} else {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
			}
		}


		exportedNotes = getNotes(); // from base diagram exporter

	}
	
	private String getPortAliasById(String portID) {
		for (PortData portData : allExportedPorts) {
			if (portData.getId().equals(portID)) {
				return portData.getAlias();
			}
		}
		return null;
	}
	
	private void extractRelationship(IRelationship relationship) {
		IModelElement source = (IModelElement) relationship.getFrom();
		IModelElement target = (IModelElement) relationship.getTo();
		ApplicationManager.instance().getViewManager().showMessage("rel type? " + relationship.getModelType());
		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof IPort) {
			sourceName = getPortAliasById(((IPort) source).getId());
		} else if (source instanceof INOTE) {
			sourceName = getNoteAliasById(((INOTE) source).getId());
		} 
		if (target instanceof IPort) {
			targetName = getPortAliasById(((IPort) target).getId());
		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(((INOTE) target).getId());
		} 

		if (relationship instanceof IAssociation) {
			IAssociation association = (IAssociation) relationship;

			IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
			IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();
			String fromEndMultiplicity = "";
			String toEndMultiplicity = "" ;
			
			if (fromEnd.getMultiplicity() != null) {
				 fromEndMultiplicity = fromEnd.getMultiplicity().equals("Unspecified") ? "" : fromEnd.getMultiplicity();
			}
			if (toEnd.getMultiplicity() != null) {
				 toEndMultiplicity = toEnd.getMultiplicity().equals("Unspecified") ? "" : toEnd.getMultiplicity();
			}
			AssociationData associationData = new AssociationData(sourceName, targetName, relationship.getModelType(),
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity,
					// fromEnd.getNavigable() == 0,
					toEnd.getNavigable() == 0, fromEnd.getAggregationKind());
			relationshipDatas.add(associationData);
			return;
		}
		

		ApplicationManager.instance().getViewManager()
				.showMessage("Relationship from: " + sourceName + " to: " + targetName);
		ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());

		if (sourceName == null || targetName == null) {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
		}
		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(),
				relationship.getName());
		relationshipDatas.add(relationshipData);
	}

	private void extractPackage(IPackage packageModel) {
		
		if (!(packageModel.getParent() instanceof IPackage)) {
			PackageData packageData = new PackageData(packageModel.getName(), packageModel.getDescription(), false);
			IModelElement[] childElements = packageModel.toChildArray();
			for (IModelElement childElement : childElements) {
				if (childElement instanceof IClass) {
					extractInterface((IClass) childElement, packageData);
				} else if (childElement instanceof IComponent) {
					extractComponent((IComponent) childElement, packageData, null); // idk
				} else if (childElement instanceof IPackage) {
					PackageData parent = packageData;
					extractPackagedPackage((IPackage) childElement, parent);

				}
			}
			addSemanticsIfExist(packageModel, packageData);
			exportedPackages.add(packageData);
		}
		
	}

	private void extractPackagedPackage(IPackage packageModel, PackageData parent) {
		PackageData packageData = new PackageData(packageModel.getName(), packageModel.getDescription(), true);
		IModelElement[] childElements = packageModel.toChildArray();
		for (IModelElement childElement : childElements) {
			if (childElement instanceof IClass) {
				extractInterface((IClass) childElement, packageData);
			} else if (childElement instanceof IComponent) {
				extractComponent((IComponent) childElement, packageData, null);
			} else if (childElement instanceof IPackage) {
				extractPackagedPackage((IPackage) childElement, packageData);

			}
		}
		parent.getSubPackages().add(packageData);
		exportedPackages.add(packageData);
		
	}

	private void extractInterface(IClass interfaceModel, PackageData packageData) {
		boolean isInPackage = (interfaceModel.getParent() instanceof IPackage);
		
		ClassData interfaceData = new ClassData(interfaceModel.getName(), interfaceModel.getDescription(), isInPackage);
		interfaceData.setStereotypes(extractStereotypes(interfaceModel));
		addSemanticsIfExist(interfaceModel, interfaceData);
		
		exportedInterfaces.add(interfaceData);
		if (packageData != null)
			packageData.getClasses().add(interfaceData);
	}
	
	

	private void extractComponent(IComponent componentModel, PackageData packageData, ComponentData parentComponentData) {
		
		boolean isInPackage = (componentModel.getParent() instanceof IPackage);
		boolean isResident = (componentModel.getParent() instanceof IComponent);
		
		ComponentData componentData = new ComponentData(componentModel.getName(), componentModel.getDescription(), isInPackage);
		componentData.setResident(isResident);
		
		componentData.setStereotypes(extractStereotypes(componentModel));
		
		Iterator componentIterator = componentModel.componentIterator();
		while (componentIterator.hasNext()) {
			IComponent residentComponentModel = (IComponent) componentIterator.next();
			extractComponent(residentComponentModel, null, componentData);  // idk
		}
		
		Iterator portIterator =  componentModel.portIterator();
		while (portIterator.hasNext()) {
			IPort portModel = (IPort) portIterator.next();
			PortData portData = new PortData(portModel.getName());
			portData.setId(portModel.getId());
			
			componentData.getPorts().add(portData);
			allExportedPorts.add(portData);
		}
		
		addSemanticsIfExist(componentModel, componentData);
		
		exportedComponents.add(componentData);
		if (packageData != null)
			packageData.getComponents().add(componentData);
		if (parentComponentData != null)
			parentComponentData.getResidents().add(componentData);
	}
	
	
	public List<ComponentData> getExportedComponents() {
		return exportedComponents;
	}
	
	public List<ClassData> getExportedInterfaces() {
		return exportedInterfaces;
	}
	
	public List<PackageData> getExportedPackages() {
		return exportedPackages;
	}
	public List<RelationshipData> getRelationshipDatas() {
		return relationshipDatas;
	}
}

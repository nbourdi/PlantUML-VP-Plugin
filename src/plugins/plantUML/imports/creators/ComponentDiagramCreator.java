package plugins.plantUML.imports.creators;

import java.util.ArrayList;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IComponentDiagramUIModel;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.connector.IAnchorUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IContainmentUIModel;
import com.vp.plugin.diagram.connector.IDependencyUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.connector.IRealizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IComponentUIModel;
import com.vp.plugin.diagram.shape.IInterfaceClassUIModel;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.diagram.shape.IPortUIModel;
import com.vp.plugin.diagram.shape.IStructuredInterfaceUIModel;
import com.vp.plugin.model.IAnchor;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IComponent;
import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IInterface;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IPort;
import com.vp.plugin.model.IRealization;
import com.vp.plugin.model.factory.IModelElementFactory;

import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.ComponentData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.ComponentData.PortData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import v.acx.fo;
import v.chx.cor;

public class ComponentDiagramCreator extends DiagramCreator {

	List<ComponentData> componentDatas = new ArrayList<ComponentData>();
	List<ClassData> interfaceDatas = new ArrayList<ClassData>();
	List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
	List<NoteData> noteDatas = new ArrayList<NoteData>();
	List<PackageData> packageDatas = new ArrayList<PackageData>();
	
	
	IComponentDiagramUIModel componentDiagram = (IComponentDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_COMPONENT_DIAGRAM);
	
	public ComponentDiagramCreator(String diagramTitle, List<ComponentData> componentDatas, List<ClassData> interfaceDatas, List<PackageData> packageDatas, List<RelationshipData> relationshipDatas, List<NoteData> noteDatas) {
		super(diagramTitle);
		this.componentDatas = componentDatas;
		this.interfaceDatas = interfaceDatas;
		this.packageDatas = packageDatas;
		this.relationshipDatas = relationshipDatas;		
		this.noteDatas = noteDatas;
		diagram = componentDiagram;
	}

	@Override
	public void createDiagram() {
		
		componentDiagram.setName(getDiagramTitle());
		
		for (ComponentData componentData : componentDatas) {
			IComponent componentModel = createComponent(componentData);
		}
		
		for (ClassData interfaceData : interfaceDatas) {
			createInterface(interfaceData);
		}
		
		for (PackageData packageData : packageDatas) {
			IPackage packageModel = createPackage(packageData);
		}
		
		for (NoteData noteData : noteDatas) { // TODO:  clean up + possibly buggy? 
			INOTE noteModel = createNote(noteData);
			
		}
		
		for (RelationshipData relationshipData : relationshipDatas) {
			createRelationship(relationshipData);			
		}
		
		diagramManager.layout(componentDiagram, DiagramManager.LAYOUT_AUTO);
	    ApplicationManager.instance().getProjectManager().saveProject();
	    ApplicationManager.instance().getDiagramManager().openDiagram(componentDiagram);
	}

	private void createRelationship(RelationshipData relationshipData) {
		String fromID = relationshipData.getSourceID();
		String toID = relationshipData.getTargetID();
		IModelElement fromModelElement = elementMap.get(fromID);
		IModelElement toModelElement = elementMap.get(toID);
		if (fromModelElement == null || toModelElement == null) {
			ApplicationManager.instance().getViewManager()
	        .showMessage("Warning: a relationship was skipped because one of its ends was not a previously imported modelElement");
			return;
		}
		
		if (relationshipData instanceof AssociationData) {
			IAssociation association = IModelElementFactory.instance().createAssociation();
			association.setFrom(fromModelElement);
			association.setTo(toModelElement);
			
			if (relationshipData.getType() == "Aggregation") {
				IAssociationEnd aggregationFromEnd = (IAssociationEnd) association.getFromEnd();
				aggregationFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_AGGREGATION);
			}
			else if (relationshipData.getType() == "Composition") {
				IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
				compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
			}
			// TODO : decide if ignore navigables..
			if (((AssociationData) relationshipData).isToEndNavigable()) {
				IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();
				// toEnd.setNavigable();
			}
			
			IAssociationEnd associationFromEnd = (IAssociationEnd) association.getFromEnd();
			associationFromEnd.setMultiplicity(((AssociationData) relationshipData).getFromEndMultiplicity());
			IAssociationEnd associationToEnd = (IAssociationEnd) association.getToEnd();
			associationToEnd.setMultiplicity(((AssociationData) relationshipData).getToEndMultiplicity());
			IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(componentDiagram, association, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
			
			
			if (relationshipData.getName() != "NULL") // label
				association.setName(relationshipData.getName());
		}
		
		else {			
			switch (relationshipData.getType()) {
			case "Generalization":
				IGeneralization generalization = IModelElementFactory.instance().createGeneralization();					
				generalization.setFrom(fromModelElement);
				generalization.setTo(toModelElement);
				IGeneralizationUIModel generalizationConnector = (IGeneralizationUIModel) diagramManager.createConnector(componentDiagram, generalization, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
				if (relationshipData.getName() != "NULL") // label
					generalization.setName(relationshipData.getName());
				break;

			case "Realization":
				IRealization realization = IModelElementFactory.instance().createRealization();
				realization.setFrom(fromModelElement);
				realization.setTo(toModelElement);
				IRealizationUIModel realizationConnector = (IRealizationUIModel) diagramManager.createConnector(componentDiagram, realization, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
				if (relationshipData.getName() != "NULL") // label
					realization.setName(relationshipData.getName());
				break;
				
			case "Dependency":
				IDependency dependency = IModelElementFactory.instance().createDependency();
				dependency.setFrom(fromModelElement);
				dependency.setTo(toModelElement);
				IDependencyUIModel dependencyConnector = (IDependencyUIModel) diagramManager.createConnector(componentDiagram, dependency, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
				if (relationshipData.getName() != "NULL") // label
					dependency.setName(relationshipData.getName());
				break;
			case "Anchor": // be ware of constraint on notes.
				IAnchor anchor = IModelElementFactory.instance().createAnchor();
				anchor.setFrom(fromModelElement);
				anchor.setTo(toModelElement);
				IAnchorUIModel anchorConnector = (IAnchorUIModel) diagramManager.createConnector(componentDiagram, anchor, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
				if (relationshipData.getName() != "NULL") // label
					anchor.setName(relationshipData.getName());
				break;
				
			case "Containment":
				// Containment is only a UI model, not a model element
				IContainmentUIModel containmentConnector = (IContainmentUIModel) diagramManager.createConnector(componentDiagram, IClassDiagramUIModel.SHAPETYPE_CONTAINMENT, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
			default:
				ApplicationManager.instance().getViewManager()
		        .showMessage("Warning: unsupported type " + relationshipData.getType() + " of relationship was skipped");
				break;
			}			
		}
	}

	private IPackage createPackage(PackageData packageData) {
		IPackage packageModel = IModelElementFactory.instance().createPackage();
		elementMap.put(packageData.getUid(), packageModel);
		
		checkAndSettleNameConflict(packageData.getName(), "Package");
		
		packageModel.setName(packageData.getName());		
		IPackageUIModel packageShape = (IPackageUIModel) diagramManager.createDiagramElement(componentDiagram, packageModel);
		shapeMap.put(packageModel, packageShape);
		
		for (ClassData packagedInterfaceData : packageData.getClasses()) {
			IClass packagedInterfaceModel = createInterface(packagedInterfaceData);
			packageModel.addChild(packagedInterfaceModel);
			packageShape.addChild((IShapeUIModel) shapeMap.get(packagedInterfaceModel));
		}
		
		for (ComponentData packagedComponentData : packageData.getComponents()) {
			IComponent packagedComponentModel = createComponent(packagedComponentData);
			packageModel.addChild(packagedComponentModel);
			packageShape.addChild((IShapeUIModel) shapeMap.get(packagedComponentModel));
		}
		
		for (PackageData subPackageData : packageData.getSubPackages()) {
			IPackage subPackageModel = createPackage(subPackageData);
			packageModel.addChild(subPackageModel);
			packageShape.addChild((IShapeUIModel) shapeMap.get(subPackageModel));			
		}

		putInSemanticsMap(packageModel, packageData);
		return packageModel; 
	}

	private IClass createInterface(ClassData interfaceData) {
		IClass interfaceModel = IModelElementFactory.instance().createClass();
		String entityId = interfaceData.getUid();
		elementMap.put(entityId, interfaceModel);
		
		checkAndSettleNameConflict(interfaceData.getName(), "Class");
		
		interfaceModel.setName(interfaceData.getName());
		
		interfaceModel.addStereotype("Interface");
		for (String stereotype : interfaceData.getStereotypes()) {
			interfaceModel.addStereotype(stereotype);
		}
		
		putInSemanticsMap(interfaceModel, interfaceData);
		
		IStructuredInterfaceUIModel interfaceShape = (IStructuredInterfaceUIModel) diagramManager.createDiagramElement(componentDiagram, IShapeTypeConstants.SHAPE_TYPE_STRUCTURED_INTERFACE);
		
		interfaceShape.setModelElement(interfaceModel);
		
		// despite the VP tutorial, the shape created gets aux view status (while being the only view). fix by setting it master.
		interfaceShape.toBeMasterView();
		
		interfaceShape.setRequestResetCaption(true);
		shapeMap.put(interfaceModel, interfaceShape);
		
		return interfaceModel;
	}

	private IComponent createComponent(ComponentData componentData) {
		IComponent componentModel = IModelElementFactory.instance().createComponent();
		String entityId = componentData.getUid();
		elementMap.put(entityId, componentModel);
		
		checkAndSettleNameConflict(componentData.getName(), "Component");
		
		componentModel.setName(componentData.getName());
		IComponentUIModel componentShape = (IComponentUIModel) diagramManager.createDiagramElement(componentDiagram, componentModel);
		shapeMap.put(componentModel, componentShape);
		
		for (String stereotype : componentData.getStereotypes()) {
			componentModel.addStereotype(stereotype);
		}
		
		for (PortData port : componentData.getPorts()) {
			IPort portModel = IModelElementFactory.instance().createPort();
			elementMap.put(port.getUid(), portModel);
			portModel.setName(port.getName());
			IPortUIModel portShape = (IPortUIModel) diagramManager.createDiagramElement(componentDiagram, portModel);
			shapeMap.put(portModel, portShape);
			componentModel.addPort(portModel);
			componentShape.addChild(portShape);
		}
		
		for (ComponentData residentComponent : componentData.getResidents()) {
			IComponent residentModel = createComponent(residentComponent);
			componentModel.addComponent(residentModel);
			componentShape.addChild((IShapeUIModel) shapeMap.get(residentModel));
		}
		
		for (ClassData residentInterface : componentData.getInterfaces()) {
			IClass residentModel = createInterface(residentInterface);
			componentModel.addChild(residentModel);
			componentShape.addChild((IShapeUIModel) shapeMap.get(residentModel));
		}
		
		for (PackageData packageData : componentData.getPackages()) {
			IPackage packageModel = createPackage(packageData);
			componentModel.addChild(packageModel);
			componentShape.addChild((IShapeUIModel) shapeMap.get(packageModel));
		}
		
		putInSemanticsMap(componentModel, componentData);
		
		componentShape.fitSize();
		return componentModel;
	}
}

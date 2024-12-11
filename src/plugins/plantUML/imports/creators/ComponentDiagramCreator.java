package plugins.plantUML.imports.creators;

import java.util.ArrayList;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IComponentDiagramUIModel;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IComponentUIModel;
import com.vp.plugin.diagram.shape.IInterfaceClassUIModel;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.diagram.shape.IPortUIModel;
import com.vp.plugin.diagram.shape.IStructuredInterfaceUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IComponent;
import com.vp.plugin.model.IInterface;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IPort;
import com.vp.plugin.model.factory.IModelElementFactory;

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
//		
//		for (RelationshipData relationshipData : relationshipDatas) {
//			createRelationship(relationshipData);			
//		}
		
		diagramManager.layout(componentDiagram, DiagramManager.LAYOUT_AUTO);
	    ApplicationManager.instance().getProjectManager().saveProject();
	    ApplicationManager.instance().getDiagramManager().openDiagram(componentDiagram);
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
		// TODO package residents
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

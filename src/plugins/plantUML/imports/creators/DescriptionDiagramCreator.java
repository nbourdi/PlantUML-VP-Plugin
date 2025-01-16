package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.*;
import com.vp.plugin.diagram.connector.*;
import com.vp.plugin.diagram.shape.*;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.*;
import plugins.plantUML.models.ComponentData.PortData;

import java.util.*;
import java.util.function.Consumer;

public class DescriptionDiagramCreator extends DiagramCreator {

    private static final Map<String, String> DIAGRAM_TYPE_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("usecase", DiagramManager.DIAGRAM_TYPE_USE_CASE_DIAGRAM);
        map.put("component", DiagramManager.DIAGRAM_TYPE_COMPONENT_DIAGRAM);
        map.put("deployment", DiagramManager.DIAGRAM_TYPE_DEPLOYMENT);
        DIAGRAM_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    public DescriptionDiagramCreator(String diagramTitle, String specificDiagramType) {
        super(diagramTitle);
        String diagramType = DIAGRAM_TYPE_MAP.getOrDefault(specificDiagramType, DiagramManager.DIAGRAM_TYPE_COMPONENT_DIAGRAM);
        diagram = diagramManager.createDiagram(diagramType);
    }

    public void createDiagram(List<ComponentData> componentDatas, List<ClassData> interfaceDatas, List<PackageData> packageDatas, List<RelationshipData> relationshipDatas, List<ActorData> actorDatas, List<UseCaseData> useCaseDatas, List<ArtifactData> artifactDatas, List<NoteData> noteDatas) {

        diagram.setName(getDiagramTitle());

        for (ComponentData componentData : componentDatas) {
            if (componentData.isNodeComponent()) createNode(componentData);
           else createComponent(componentData);
        }
        interfaceDatas.forEach(this::createInterface);
        artifactDatas.forEach(this::createArtifact);
        packageDatas.forEach(this::createPackage);
        noteDatas.forEach(this::createNote);
        actorDatas.forEach(this::createActor);
        useCaseDatas.forEach(this::createUseCase);
        for (RelationshipData relationshipData : relationshipDatas) {
            ApplicationManager.instance().getViewManager().showMessage("Trying to create relationship");
            createRelationship(relationshipData);
        }

        diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getDiagramManager().openDiagram(diagram);
    }

    private INode createNode(ComponentData nodeData) {
        INode nodeModel = IModelElementFactory.instance().createNode();
        String entityId = nodeData.getUid();
        elementMap.put(entityId, nodeModel);

        checkAndSettleNameConflict(nodeData.getName(), "Node");

        nodeModel.setName(nodeData.getName());
        INodeUIModel nodeShape = (INodeUIModel) diagramManager.createDiagramElement(diagram, nodeModel);
        shapeMap.put(nodeModel, nodeShape);

        for (String stereotype : nodeData.getStereotypes()) {
            nodeModel.addStereotype(stereotype);
        }

        for (PortData port : nodeData.getPorts()) {
            IPort portModel = IModelElementFactory.instance().createPort();
            elementMap.put(port.getUid(), portModel);
            portModel.setName(port.getName());
            IPortUIModel portShape = (IPortUIModel) diagramManager.createDiagramElement(diagram, portModel);
            shapeMap.put(portModel, portShape);
            nodeModel.addPort(portModel);
            nodeShape.addChild(portShape);
        }

        for (ComponentData residentComponent : nodeData.getResidents()) {
            if (residentComponent.isNodeComponent()) {
                INode residentModel = createNode(residentComponent);
                nodeModel.addNode(residentModel);
                nodeShape.addChild((IShapeUIModel) shapeMap.get(residentModel));
            } else {
                IComponent residentModel = createComponent(residentComponent);
                nodeModel.addComponent(residentModel);
                nodeShape.addChild((IShapeUIModel) shapeMap.get(residentModel));
            }
        }

        for (ClassData residentInterface : nodeData.getInterfaces()) {
            IClass residentModel = createInterface(residentInterface);
            nodeModel.addChild(residentModel);
            nodeShape.addChild((IShapeUIModel) shapeMap.get(residentModel));
        }

        for (PackageData packageData : nodeData.getPackages()) {
            IHasChildrenBaseModelElement packageModel = createPackage(packageData);
            nodeModel.addChild(packageModel);
            nodeShape.addChild((IShapeUIModel) shapeMap.get(packageModel));
        }

        for (ArtifactData artifactData : nodeData.getArtifacts()) {
            IArtifact artifactModel = createArtifact(artifactData);
            nodeModel.addChild(artifactModel);
            nodeShape.addChild((IShapeUIModel) shapeMap.get(artifactModel));
        }

        putInSemanticsMap(nodeModel, nodeData);
        nodeShape.fitSize();
        return nodeModel;
    }

    private IArtifact createArtifact(ArtifactData artifactData) {
        IArtifact artifactModel = IModelElementFactory.instance().createArtifact();
        elementMap.put(artifactData.getUid(), artifactModel);
        checkAndSettleNameConflict(artifactData.getName(), "Artifact");
        artifactModel.setName(artifactData.getName());
        IArtifactUIModel artifactShape = (IArtifactUIModel) diagramManager.createDiagramElement(diagram, artifactModel);
        shapeMap.put(artifactModel, artifactShape);
        putInSemanticsMap(artifactModel, artifactData);
        artifactShape.fitSize();
        return artifactModel;
    }

    private IUseCase createUseCase(UseCaseData useCaseData) {
        IUseCase useCaseModel = IModelElementFactory.instance().createUseCase();
        elementMap.put(useCaseData.getUid(), useCaseModel);
        checkAndSettleNameConflict(useCaseData.getName(), "UseCase");
        useCaseModel.setName(useCaseData.getName());
        if (useCaseData.isBusiness()) useCaseModel.setBusinessModel(true);
        IUseCaseUIModel useCaseShape = (IUseCaseUIModel) diagramManager.createDiagramElement(diagram, useCaseModel);
        shapeMap.put(useCaseModel, useCaseShape);
        putInSemanticsMap(useCaseModel, useCaseData);
        return useCaseModel;
    }

    private IActor createActor(ActorData actorData) {
        IActor actorModel = IModelElementFactory.instance().createActor();
        if (actorData.isBusiness()) actorModel.setBusinessModel(true);
        elementMap.put(actorData.getUid(), actorModel);
        checkAndSettleNameConflict(actorData.getName(), "Actor");
        actorModel.setName(actorData.getName());
        IActorUIModel actorShape = (IActorUIModel) diagramManager.createDiagramElement(diagram, actorModel);
        actorShape.resetCaption();
        shapeMap.put(actorModel, actorShape);
        putInSemanticsMap(actorModel, actorData);

        return actorModel;
    }

    private IHasChildrenBaseModelElement createPackage(PackageData packageData) {

        IHasChildrenBaseModelElement packageOrSystem;
        IShapeUIModel packageShape;
        if (packageData.isRectangle()) {
            packageOrSystem = IModelElementFactory.instance().createSystem();
            elementMap.put(packageData.getUid(), packageOrSystem);
            checkAndSettleNameConflict(packageData.getName(), "System");

            packageOrSystem.setName(packageData.getName());
            packageShape = (ISystemUIModel) diagramManager.createDiagramElement(diagram, packageOrSystem);
            shapeMap.put(packageOrSystem, packageShape);
        } else {
            packageOrSystem = IModelElementFactory.instance().createPackage();
            elementMap.put(packageData.getUid(), packageOrSystem);
            checkAndSettleNameConflict(packageData.getName(), "Package");

            packageOrSystem.setName(packageData.getName());
            packageShape = (IPackageUIModel) diagramManager.createDiagramElement(diagram, packageOrSystem);
            shapeMap.put(packageOrSystem, packageShape);
        }

        for (ClassData packagedInterfaceData : packageData.getClasses()) {
            IClass packagedInterfaceModel = createInterface(packagedInterfaceData);
            packageOrSystem.addChild(packagedInterfaceModel);
            packageShape.addChild((IShapeUIModel) shapeMap.get(packagedInterfaceModel));
        }
        fillInPackage(packageData, packageOrSystem, packageShape);
        putInSemanticsMap(packageOrSystem, packageData);
        return packageOrSystem;
    }

    private void fillInPackage(PackageData packageData, IHasChildrenBaseModelElement packageOrSystem, IShapeUIModel packageShape) {
        for (ComponentData packagedComponentData : packageData.getComponents()) {
            IHasChildrenBaseModelElement residentModel;
            if (packagedComponentData.isNodeComponent()) {
                residentModel = createNode(packagedComponentData);
            } else {
                residentModel = createComponent(packagedComponentData);
            }
            packageOrSystem.addChild(residentModel);
            packageShape.addChild((IShapeUIModel) shapeMap.get(residentModel));
        }

        for (PackageData subPackageData : packageData.getSubPackages()) {
            IModelElement subPackageModel = createPackage(subPackageData);
            packageOrSystem.addChild(subPackageModel);
            packageShape.addChild((IShapeUIModel) shapeMap.get(subPackageModel));
        }

        for (ActorData actorData : packageData.getActors()) {
            IActor packagedActorModel = createActor(actorData);
            packageOrSystem.addChild(packagedActorModel);
            packageShape.addChild((IShapeUIModel) shapeMap.get(packagedActorModel));
        }

        for (UseCaseData useCaseData : packageData.getUseCases()) {
            IUseCase packagedUseCase = createUseCase(useCaseData);
            packageOrSystem.addChild(packagedUseCase);
            packageShape.addChild((IShapeUIModel) shapeMap.get(packagedUseCase));
        }

        for (ArtifactData artifactData : packageData.getArtifacts()) {
            IArtifact packagedArtifact = createArtifact(artifactData);
            packageOrSystem.addChild(packagedArtifact);
            packageShape.addChild((IShapeUIModel) shapeMap.get(packagedArtifact));
        }
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

        for (AttributeData attributeData : interfaceData.getAttributes()) {
            IAttribute attributeModel = IModelElementFactory.instance().createAttribute();
            attributeModel.setName(attributeData.getName());
            attributeModel.setType(attributeData.getType());
            attributeModel.setInitialValue(attributeData.getInitialValue());
            attributeModel.setVisibility(attributeData.getVisibility());
            if (attributeData.isStatic()) {
                attributeModel.setScope("classifier");
            }
            interfaceModel.addAttribute(attributeModel);
        }

        for (OperationData operationData : interfaceData.getOperations()) {
            IOperation operationModel = IModelElementFactory.instance().createOperation();
            operationModel.setAbstract(operationData.isAbstract());
            operationModel.setName(operationData.getName());
            operationModel.setReturnType(operationData.getReturnType());

            for (OperationData.Parameter parameter : operationData.getParameters()) {
                IParameter paramModel = IModelElementFactory.instance().createParameter();
                paramModel.setName(parameter.getName());
                paramModel.setType(parameter.getType());
                paramModel.setDefaultValue(parameter.getDefaultValue());
                operationModel.addParameter(paramModel);
            }
            interfaceModel.addOperation(operationModel);
        }
        putInSemanticsMap(interfaceModel, interfaceData);
        IStructuredInterfaceUIModel interfaceShape = (IStructuredInterfaceUIModel) diagramManager.createDiagramElement(diagram, IShapeTypeConstants.SHAPE_TYPE_STRUCTURED_INTERFACE);
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
        IComponentUIModel componentShape = (IComponentUIModel) diagramManager.createDiagramElement(diagram, componentModel);
        shapeMap.put(componentModel, componentShape);

        for (String stereotype : componentData.getStereotypes()) {
            componentModel.addStereotype(stereotype);
        }

        for (PortData port : componentData.getPorts()) {
            IPort portModel = IModelElementFactory.instance().createPort();
            elementMap.put(port.getUid(), portModel);
            portModel.setName(port.getName());
            IPortUIModel portShape = (IPortUIModel) diagramManager.createDiagramElement(diagram, portModel);
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
            IModelElement packageModel = createPackage(packageData);
            componentModel.addChild(packageModel);
            componentShape.addChild((IShapeUIModel) shapeMap.get(packageModel));
        }

        putInSemanticsMap(componentModel, componentData);

        componentShape.fitSize();
        return componentModel;
    }
}
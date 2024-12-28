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

import java.util.ArrayList;
import java.util.List;

public class DescriptionDiagramCreator extends DiagramCreator {

    List<ComponentData> componentDatas = new ArrayList<ComponentData>();
    List<ClassData> interfaceDatas = new ArrayList<ClassData>();
    List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
    List<ActorData> actorDatas = new ArrayList<>();
    List<UseCaseData> useCaseDatas = new ArrayList<>();
    List<NoteData> noteDatas = new ArrayList<NoteData>();
    List<PackageData> packageDatas = new ArrayList<PackageData>();

    String specificDiagramType;

    // IComponentDiagramUIModel componentDiagram = (IComponentDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_COMPONENT_DIAGRAM);

    public DescriptionDiagramCreator(String diagramTitle, String specificDiagramType, List<ComponentData> componentDatas, List<ClassData> interfaceDatas, List<PackageData> packageDatas, List<RelationshipData> relationshipDatas, List<ActorData> actorDatas, List<UseCaseData> useCaseData, List<NoteData> noteDatas) {
        super(diagramTitle);
        this.componentDatas = componentDatas;
        this.interfaceDatas = interfaceDatas;
        this.packageDatas = packageDatas;
        this.relationshipDatas = relationshipDatas;
        this.noteDatas = noteDatas;
        this.specificDiagramType = specificDiagramType;
        this.actorDatas = actorDatas;
        this.useCaseDatas = useCaseData;
        switch (specificDiagramType) {
            case "usecase":
                IUseCaseDiagramUIModel usecaseDiagram = (IUseCaseDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_USE_CASE_DIAGRAM);
                diagram = usecaseDiagram;
                break;
            case "component":
                IComponentDiagramUIModel componentDiagram = (IComponentDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_COMPONENT_DIAGRAM);
                diagram = componentDiagram;
                break;
            case "deployment":
                IDeploymentDiagramUIModel deploymentDiagram = (IDeploymentDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_DEPLOYMENT);
                diagram = deploymentDiagram;
                break;
        }
    }

    @Override
    public void createDiagram() {

        diagram.setName(getDiagramTitle());

        // component diagram
        for (ComponentData componentData : componentDatas) {
            IComponent componentModel = createComponent(componentData);
        }

        for (ClassData interfaceData : interfaceDatas) {
            createInterface(interfaceData);
        }

        for (PackageData packageData : packageDatas) {
            createPackage(packageData);
        }

        for (NoteData noteData : noteDatas) { // TODO:  clean up + possibly buggy?
            INOTE noteModel = createNote(noteData);
        }

        for (ActorData actorData : actorDatas) {
            IActor actorModel = createActor(actorData);
        }

        for (UseCaseData useCaseData : useCaseDatas) {
            createUseCase(useCaseData);
        }

        for (RelationshipData relationshipData : relationshipDatas) {
            ApplicationManager.instance().getViewManager().showMessage("Trying to create relationship");
            createRelationship(relationshipData);
        }

        diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getProjectManager().saveProject();
        ApplicationManager.instance().getDiagramManager().openDiagram(diagram);
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
        elementMap.put(actorData.getUid(), actorModel);
        checkAndSettleNameConflict(actorData.getName(), "Actor");
        actorModel.setName(actorData.getName());
        IActorUIModel actorShape = (IActorUIModel) diagramManager.createDiagramElement(diagram, actorModel);
        actorShape.resetCaption();
        shapeMap.put(actorModel, actorShape);
        putInSemanticsMap(actorModel, actorData);

        return actorModel;
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
            IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, association, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);


            if (relationshipData.getName() != "NULL") // label
                association.setName(relationshipData.getName());
        }

        else {
            switch (relationshipData.getType()) {
                case "Generalization":
                    IGeneralization generalization = IModelElementFactory.instance().createGeneralization();
                    generalization.setFrom(fromModelElement);
                    generalization.setTo(toModelElement);
                    IGeneralizationUIModel generalizationConnector = (IGeneralizationUIModel) diagramManager.createConnector(diagram, generalization, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
                    if (relationshipData.getName() != "NULL") // label
                        generalization.setName(relationshipData.getName());
                    break;

                case "Realization":
                    IRealization realization = IModelElementFactory.instance().createRealization();
                    realization.setFrom(fromModelElement);
                    realization.setTo(toModelElement);
                    IRealizationUIModel realizationConnector = (IRealizationUIModel) diagramManager.createConnector(diagram, realization, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
                    if (relationshipData.getName() != "NULL") // label
                        realization.setName(relationshipData.getName());
                    break;

                case "Dependency":
                    IDependency dependency = IModelElementFactory.instance().createDependency();
                    dependency.setFrom(fromModelElement);
                    dependency.setTo(toModelElement);
                    IDependencyUIModel dependencyConnector = (IDependencyUIModel) diagramManager.createConnector(diagram, dependency, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
                    if (relationshipData.getName() != "NULL") // label
                        dependency.setName(relationshipData.getName());
                    break;
                case "Anchor": // be ware of constraint on notes.
                    IAnchor anchor = IModelElementFactory.instance().createAnchor();
                    anchor.setFrom(fromModelElement);
                    anchor.setTo(toModelElement);
                    IAnchorUIModel anchorConnector = (IAnchorUIModel) diagramManager.createConnector(diagram, anchor, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
                    if (relationshipData.getName() != "NULL") // label
                        anchor.setName(relationshipData.getName());
                    break;

                case "Containment":
                    // Containment is only a UI model, not a model element
                    IContainmentUIModel containmentConnector = (IContainmentUIModel) diagramManager.createConnector(diagram, IClassDiagramUIModel.SHAPETYPE_CONTAINMENT, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
                default:
                    ApplicationManager.instance().getViewManager()
                            .showMessage("Warning: unsupported type " + relationshipData.getType() + " of relationship was skipped");
                    break;
            }
        }
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
        // IPackage packageModel = IModelElementFactory.instance().createPackage();



        for (ClassData packagedInterfaceData : packageData.getClasses()) {
            IClass packagedInterfaceModel = createInterface(packagedInterfaceData);
            packageOrSystem.addChild(packagedInterfaceModel);
            packageShape.addChild((IShapeUIModel) shapeMap.get(packagedInterfaceModel));
        }

        for (ComponentData packagedComponentData : packageData.getComponents()) {
            IComponent packagedComponentModel = createComponent(packagedComponentData);
            packageOrSystem.addChild(packagedComponentModel);
            packageShape.addChild((IShapeUIModel) shapeMap.get(packagedComponentModel));
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

        putInSemanticsMap(packageOrSystem, packageData);
        return packageOrSystem;
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
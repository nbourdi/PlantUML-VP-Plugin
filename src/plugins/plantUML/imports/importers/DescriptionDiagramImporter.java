package plugins.plantUML.imports.importers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vp.plugin.ApplicationManager;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.style.SName;
import org.pushingpixels.flamingo.api.common.AbstractFileViewPanel;
import plugins.plantUML.models.*;
import plugins.plantUML.models.ComponentData.PortData;

public class DescriptionDiagramImporter extends DiagramImporter {

    private AbstractEntityDiagram descriptionDiagram;

    private List<PackageData> packageDatas = new ArrayList<PackageData>();
    private List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
    private List<NoteData> noteDatas = new ArrayList<NoteData>();
    private List<ClassData> interfaceDatas = new ArrayList<ClassData>();
    private List<ComponentData> componentDatas = new ArrayList<ComponentData>();
    private List<ArtifactData> artifactDatas = new ArrayList<>();

    private List<ActorData> actorDatas = new ArrayList<>();
    private List<UseCaseData> useCaseDatas = new ArrayList<>();


    public DescriptionDiagramImporter(AbstractEntityDiagram descriptionDiagram, Map<String, SemanticsData> semanticsMap) {
        super(semanticsMap);
        this.descriptionDiagram = descriptionDiagram;
    }

    public void extract() {

        for (Entity groupEntity : descriptionDiagram.groups()) {
            if (groupEntity.getParentContainer().isRoot()) {
                extractGroup(groupEntity, componentDatas, packageDatas);
            }
        }

        for (Entity entity : descriptionDiagram.leafs()) {
            if (entity.getParentContainer().isRoot()) {
                extractLeaf(entity, componentDatas, interfaceDatas, actorDatas, useCaseDatas, artifactDatas, packageDatas);
            }
        }

        for (Link link : descriptionDiagram.getLinks()) {
            if (link.isInvis()) continue;
            RelationshipData relationship = extractRelationship(link);
            if(relationship != null) {
                relationshipDatas.add(relationship);
            }
        }
    }

    private RelationshipData extractRelationship(Link link) {
        String sourceID;
        String targetID;

        String relationshipType = "";
        String decor1 = link.getType().getDecor1().toString();
        String decor2 = link.getType().getDecor2().toString();

        boolean isDecorated1 = (decor1 != "NONE" && decor1 != "NOT_NAVIGABLE");
        boolean isDecorated2 = (decor2 != "NONE" && decor2 != "NOT_NAVIGABLE");
        String decor = (isDecorated1 ? decor1 : decor2);
        boolean isNotNavigable = (decor1 == "NOT_NAVIGABLE" || decor2 == "NOT_NAVIGABLE");

        // DESIGN CONSTRAINT : double-ended relationships do not exist in VP.
        if (isDecorated1 && isDecorated2) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: an unsupported type of relationship with TWO ends was found and not imported.");
            return null;
        }
        String lineStyle = link.getType().getStyle().toString();

        boolean isReverse = (lineStyle.contains("NORMAL")); // meaning the "from" side has the decoration
        boolean isAssoc = false;

        String fromEndMultiplicity = link.getLinkArg().getQuantifier1();
        String toEndMultiplicity = link.getLinkArg().getQuantifier2();
        String fromEndAggregation = "";
        if (lineStyle.contains("NORMAL")) {
            // association, containment, composition, agregation are solid line links

            if (decor == "COMPOSITION") {
                relationshipType = "Composition";
                fromEndAggregation = "composite";
                isAssoc = true;
            } else if (decor == "AGREGATION") {
                relationshipType = "Aggregation";
                fromEndAggregation = "shared";
                isAssoc = true;

            } else if (!isDecorated1 && !isDecorated2) {
                relationshipType = "Simple";
                isAssoc = true;

            } else if (decor == "EXTENDS") { // TODO : arrow with solid line isnt in VP.. but means extend in puml
                relationshipType = "Generalization";
            } else if (decor == "CROWFOOT") {
                relationshipType = "Containment";
            }

        } else {
            // DASHED
            switch (decor) {
                case "EXTENDS": // |>
                    isReverse = true;
                    relationshipType = "Realization";
                    break;
                case "ARROW": // > , abstraction and all stereotypes + dependency stereotypes all look the same...
                    relationshipType = "Dependency";
                    break;
                case "NONE": // ".." note anchor or assoc class
                    relationshipType = "Anchor";
                    break;
                default:
                    ApplicationManager.instance().getViewManager()
                            .showMessage("Warning: an unsupported type of relationship was found and not imported.");
                    break;
            }
        }

        if (isReverse) {
            sourceID = link.getEntity1().getUid();
            targetID = link.getEntity2().getUid();
        } else {
            sourceID = link.getEntity2().getUid();
            targetID = link.getEntity1().getUid();
        }

        if(relationshipType.isEmpty()) return null; // TODO: temp fix.

        if (isAssoc) {
            AssociationData associationData = new AssociationData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()) , fromEndMultiplicity, toEndMultiplicity, !isNotNavigable, fromEndAggregation);
            associationData.setSourceID(sourceID);
            associationData.setTargetID(targetID);

            return associationData;
        }
        else {
            RelationshipData relationshipData = new RelationshipData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()));
            relationshipData.setSourceID(sourceID);
            relationshipData.setTargetID(targetID);
            return relationshipData;
        }
    }

    private void extractLeaf(Entity entity, List<ComponentData> components, List<ClassData> interfaces, List<ActorData> actors, List<UseCaseData> usecases, List<ArtifactData> artifacts, List<PackageData> packages) {

        // notes and use cases have null usymbols
        if (entity.getLeafType() == LeafType.NOTE) {
            noteDatas.add(extractNote(entity));
            return;
        }
        else if (entity.getLeafType() == LeafType.USECASE || entity.getLeafType() == LeafType.USECASE_BUSINESS) {
            usecases.add(extractUseCase(entity));
            return;
        }
        // ideally leafTypes would be more specific than they are for component, workaround by getting SNames from USymbols

        SName sName = entity.getUSymbol().getSName();
        ApplicationManager.instance().getViewManager().showMessage("extracting leaf " + sName.toString() +" " + entity.getDisplay().toString());
        switch (sName) {
            // TODO: packages, nodes.. when empty are considered leafs
            case component:
                components.add(extractComponent(entity));
                break;

            case interface_:
                interfaces.add(extractInterface(entity));
                break;
            case actor:
            case business:
                actors.add(extractActor(entity));
                break;
            case usecase:
                usecases.add(extractUseCase(entity));
                break;
            case artifact:
                artifacts.add(extractArtifact(entity));
                break;
            case node:
                components.add(extractNode(entity));
                break;
            case package_:
                packages.add(extractPackage(entity));
            default:
                break;
        }
    }

    private ArtifactData extractArtifact(Entity entity) {
        String name = removeBrackets(entity.getDisplay().toString());
        ArtifactData artifactData = new ArtifactData(name, false, false);
        String key = name + "|Artifact";
        boolean hasSemantics = getSemanticsMap().containsKey(key);
        if (hasSemantics) artifactData.setSemantics(getSemanticsMap().get(key));

        artifactData.setUid(entity.getUid());

        return artifactData;
    }

    private UseCaseData extractUseCase(Entity entity) {
        String name = removeBrackets(entity.getDisplay().toString());

        UseCaseData useCaseData = new UseCaseData(name);
        String key = name + "|UseCase";
        if (entity.getLeafType() == LeafType.USECASE_BUSINESS) useCaseData.setBusiness(true);
        boolean hasSemantics = getSemanticsMap().containsKey(key);

        if (hasSemantics) useCaseData.setSemantics(getSemanticsMap().get(key));
        List<String> stereotypes = extractStereotypes(entity, useCaseData);
        for (String stereotype : stereotypes) {
            useCaseData.addStereotype(stereotype);
        }
        useCaseData.setUid(entity.getUid());
        return useCaseData;
    }

    private ActorData extractActor(Entity entity) {
        String name = removeBrackets(entity.getDisplay().toString());

        ActorData actorData = new ActorData(name);

        String key = name + "|Actor";
        if (entity.getUSymbol().getSName() == SName.business) {
            actorData.setBusiness(true);
            ApplicationManager.instance().getViewManager().showMessage("========== BUSINESS ACTOR");
        }

        boolean hasSemantics = getSemanticsMap().containsKey(key);

        if (hasSemantics) actorData.setSemantics(getSemanticsMap().get(key));
        List<String> stereotypes = extractStereotypes(entity, actorData);
        for (String stereotype : stereotypes) {
            actorData.addStereotype(stereotype);
        }
        actorData.setUid(entity.getUid());
        return actorData;
    }

    private ClassData extractInterface(Entity entity) {

        String name = removeBrackets(entity.getDisplay().toString());

        ClassData interfaceData = new ClassData(name, false);
        String key = name + "|Class";

        boolean hasSemantics = getSemanticsMap().containsKey(key);

        if (hasSemantics) interfaceData.setSemantics(getSemanticsMap().get(key));
        List<String> stereotypes = extractStereotypes(entity, interfaceData);

        for (String stereotype : stereotypes) {
            interfaceData.addStereotype(stereotype);
        }

        interfaceData.setUid(entity.getUid());
        return interfaceData;
    }

    private ComponentData extractComponent(Entity entity) {
        String name = removeBrackets(entity.getDisplay().toString());

        ComponentData componentData = new ComponentData(name, false);
        String key = name + "|Component";

        boolean hasSemantics = getSemanticsMap().containsKey(key);
        if (hasSemantics) componentData.setSemantics(getSemanticsMap().get(key));

        List<String> stereotypes = extractStereotypes(entity, componentData);

        for (String stereotype : stereotypes) {
            componentData.addStereotype(stereotype);
        }

        componentData.setUid(entity.getUid());

        for (Entity residentGroup : entity.groups()) {
            extractGroup(residentGroup, componentData.getResidents(), componentData.getPackages());
        }

        for (Entity residentLeaf : entity.leafs()) {
            // Ports need special handling since their getUSymbols return null for unknown reason
            if (residentLeaf.getLeafType() == LeafType.PORTIN || residentLeaf.getLeafType() == LeafType.PORTOUT) {
                String portName = removeBrackets(residentLeaf.getDisplay().toString());
                PortData portData = new PortData(portName);
                portData.setUid(residentLeaf.getUid());
                componentData.getPorts().add(portData);
            }

            else extractLeaf(residentLeaf, componentData.getResidents(), componentData.getInterfaces(), null, null, componentData.getArtifacts(), null); // TODO : actors and use cases obvi shouldnt be here. sitched with null which might cause havoc
        }
        return componentData;
    }


    private void extractGroup(Entity groupEntity, List<ComponentData> components, List<PackageData> packages) {

        /*
        * When dealing with an unwanted ClassDiagram classification where components are present,
        * "class" packages have GroupType PACKAGE and null USymbols.
        * Components are also PACKAGE GroupTypes in ClassDiagram but with not-null USymbols.
        * Therefore this check is done to support the "miss"-classified component diagrams...
         */

        if (groupEntity.getUSymbol() == null) {
            if (groupEntity.getGroupType() == GroupType.PACKAGE) {
                PackageData packageData = extractPackage(groupEntity);
                packages.add(packageData);
                return;
            }
            ApplicationManager.instance().getViewManager().showMessage("Warning: USymbol is null for a group...");
            return;
        }
        SName sName = groupEntity.getUSymbol().getSName();

        switch (sName) {
            case component:
                ComponentData componentWithResidents = extractComponent(groupEntity);
                components.add(componentWithResidents);
                break;
            case package_:
                PackageData packageData = extractPackage(groupEntity);
                packages.add(packageData);
                break;
            case rectangle:
                PackageData rectanglePackageData = extractPackage(groupEntity);
                rectanglePackageData.setRectangle(true);
                packages.add(rectanglePackageData);
                break;
            case node:
                ComponentData nodeData = extractNode(groupEntity);
                nodeData.setNodeComponent(true);
                components.add(nodeData);
                break;
            default:
                break;
        }

    }

    private ComponentData extractNode(Entity groupEntity) {
        String name = removeBrackets(groupEntity.getDisplay().toString());

        ComponentData nodeData = new ComponentData(name, false);
        nodeData.setNodeComponent(true);
        String key = name + "|Node";

        boolean hasSemantics = getSemanticsMap().containsKey(key);
        if (hasSemantics) nodeData.setSemantics(getSemanticsMap().get(key));

        List<String> stereotypes = extractStereotypes(groupEntity, nodeData);

        for (String stereotype : stereotypes) {
            nodeData.addStereotype(stereotype);
        }
        nodeData.setUid(groupEntity.getUid());
        for (Entity residentGroup : groupEntity.groups()) {
            extractGroup(residentGroup, nodeData.getResidents(), nodeData.getPackages());
        }

        for (Entity residentLeaf : groupEntity.leafs()) {
            // Ports need special handling since their getUSymbols return null for unknown reason
            if (residentLeaf.getLeafType() == LeafType.PORTIN || residentLeaf.getLeafType() == LeafType.PORTOUT) {
                String portName = removeBrackets(residentLeaf.getDisplay().toString());
                PortData portData = new PortData(portName);
                portData.setUid(residentLeaf.getUid());
                nodeData.getPorts().add(portData);
            }
            else extractLeaf(residentLeaf, nodeData.getResidents(), nodeData.getInterfaces(), null, null, nodeData.getArtifacts(), null); // TODO : actors and use cases obvi shouldnt be here. sitched with null which might cause havoc
        }
        return nodeData;
    }

    private PackageData extractPackage(Entity groupEntity) {

        String name = removeBrackets(groupEntity.getDisplay().toString());
        PackageData packageData = new PackageData(groupEntity.getName(), false);
        packageData.setUid(groupEntity.getUid());

        for (Entity packagedLeaf : groupEntity.leafs()) {
            extractLeaf(packagedLeaf, packageData.getComponents(), packageData.getClasses(), packageData.getActors(), packageData.getUseCases(), packageData.getArtifacts(), packageData.getSubPackages());
        }

        for (Entity subgroupEntity : groupEntity.groups()) {
            extractGroup(subgroupEntity, packageData.getComponents(), packageData.getSubPackages());
        }

        String key = name + "|Package";

        boolean hasSemantics = getSemanticsMap().containsKey(key);

        if (hasSemantics) packageData.setSemantics(getSemanticsMap().get(key));
        return packageData;
    }

    public List<ComponentData> getComponentDatas() {
        return componentDatas;
    }

    public List<ClassData> getInterfaceDatas() {
        return interfaceDatas;
    }

    public List<RelationshipData> getRelationshipDatas() {
        return relationshipDatas;
    }

    public List<PackageData> getPackageDatas() {
        return packageDatas;
    }

    public List<NoteData> getNoteDatas() {
        return noteDatas;
    }

    public List<ActorData> getActorDatas() {
        return actorDatas;
    }

    public List<UseCaseData> getUseCaseDatas() {
        return useCaseDatas;
    }

    public List<ArtifactData> getArtifactDatas() {
        return artifactDatas;
    }
}

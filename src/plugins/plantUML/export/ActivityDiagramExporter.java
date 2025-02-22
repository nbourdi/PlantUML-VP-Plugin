package plugins.plantUML.export;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.*;

import java.util.*;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public class ActivityDiagramExporter extends DiagramExporter {

    private final IDiagramUIModel diagram;
    private FlowNode rootNode;
    private List<IModelElement> visited = new ArrayList<>();
    private Map<IModelElement, FlowNode> joinMap = new HashMap<>();
    List<NoteData> exportedNotes = new ArrayList<>();

    public ActivityDiagramExporter(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void extract() {
        IModelElement chosenInitialModelElement = findInitialNode();
        if (chosenInitialModelElement == null) {
            throw new UnfitForExportException("No valid initial element found.");
        }

        rootNode = traverseAndBuild(chosenInitialModelElement, visited);


        if (rootNode == null) {
            throw new UnfitForExportException("Failed to construct activity diagram structure.");
        }

        logFlow(rootNode, "");
    }

    private FlowNode traverseAndBuild(IModelElement currentElement, List<IModelElement> visited) {
        if (visited.contains(currentElement)) {
            if (currentElement.getModelType() == SHAPE_TYPE_JOIN_NODE || currentElement.getModelType() == SHAPE_TYPE_MERGE_NODE) {
                return joinMap.get(currentElement);
            }
            throw new UnfitForExportException("Looping or repeated flows detected during export.");
        }
        visited.add(currentElement);

        switch (currentElement.getModelType()) {
            case SHAPE_TYPE_ACTIVITY_ACTION:
            case SHAPE_TYPE_INITIAL_NODE:
            case SHAPE_TYPE_ACTIVITY_FINAL_NODE:
            case SHAPE_TYPE_FLOW_FINAL_NODE:
                ActionData action = getActionData(currentElement, visited);
                return action;

            case SHAPE_TYPE_DECISION_NODE:
                return extractDecision(currentElement);

            case SHAPE_TYPE_FORK_NODE:
                return extractFork(currentElement);

            case SHAPE_TYPE_JOIN_NODE:
                if (!checkSingleOutgoingEdge(currentElement)) {
                    throw new UnfitForExportException("Error: a join can't have more than one outgoing edge.");
                }
                JoinFlowNode join = extractJoin(currentElement);
                joinMap.put(currentElement, join);
                IRelationship[] outgoingRelationships2 = currentElement.toFromRelationshipArray();
                if (outgoingRelationships2.length > 0) {
                    IModelElement targetElement = outgoingRelationships2[0].getTo();

                    FlowNode nextNode = traverseAndBuild(targetElement, visited);
                    if (nextNode != null) {
                        join.setNextNode(nextNode);
                    }
                }
                return join;
            case SHAPE_TYPE_MERGE_NODE:
                if (!checkSingleOutgoingEdge(currentElement)) {
                    throw new UnfitForExportException("Error: a merge node can't have more than one outgoing edge.");
                }
                JoinFlowNode merge = extractJoin(currentElement);
                joinMap.put(currentElement, merge);
                IRelationship[] outgoingRelationships3 = currentElement.toFromRelationshipArray();
                if (outgoingRelationships3.length > 0) {
                    IModelElement targetElement = outgoingRelationships3[0].getTo();

                    FlowNode nextNode = traverseAndBuild(targetElement, visited);
                    if (nextNode != null) {
                        merge.setNextNode(nextNode);
                    }
                }
                return merge;
            case SHAPE_TYPE_ACTIVITY_SWIMLANE2:
                throw new UnfitForExportException("ERROR: Transition to Swimlane?");
            default:
                throw new UnfitForExportException("ERROR: can't export type " + currentElement.getModelType() + ", no PlantUML equivalent.");
        }
    }

    private ActionData getActionData(IModelElement currentElement, List<IModelElement> visited) {
        if (!checkSingleOutgoingEdge(currentElement)) {
            throw new UnfitForExportException("Error: an action can't have more than one outgoing edge.");
        }
        ActionData action = extractAction(currentElement);
        IRelationship[] outgoingRelationships = currentElement.toFromRelationshipArray();
        if (outgoingRelationships.length > 0) {
            IModelElement targetElement = outgoingRelationships[0].getTo();
            FlowNode nextNode = traverseAndBuild(targetElement, visited);
            action.setNextLabel(outgoingRelationships[0].getName());
            if (nextNode != null) {
                action.setNextNode(nextNode);
            }
        }
        return action;
    }

    private JoinFlowNode extractJoin(IModelElement joinModel) {
        JoinFlowNode joinOrMergeNode = new JoinFlowNode(joinModel.getName());
        joinOrMergeNode.setMerge(joinModel instanceof IMergeNode);

        return joinOrMergeNode;
    }

    private ActionData extractAction(IModelElement actionModel) {
        ActionData actionData = new ActionData(actionModel.getName());
        actionData.setInitial(actionModel instanceof IInitialNode);
        actionData.setFinal(actionModel instanceof IActivityFinalNode);
        actionData.setFinalFlow(actionModel instanceof IFlowFinalNode);

        IModelElement[] partition =
                actionModel.getReferencingModels(
                        IModelElementFactory.MODEL_TYPE_ACTIVITY_PARTITION, // <------ owner’s modelType (Partition’s modelType)
                        IActivityPartition.PROP_CONTAINED_ELEMENTS // <----- owner’s propertyName (Partition’s PROP_CONTAINED_ELEMENTS)
                );

        // could be in multiple due to other activity diagrams, but safe enough to assume it's just in this one, hence [0]
        if (partition.length > 0) {
            actionData.setSwimlane(partition[0].getName());
        }

        return actionData;
    }

    private SplitFlowNode extractDecision(IModelElement decisionModel) {
        SplitFlowNode decisionNode = new SplitFlowNode(decisionModel.getName(), "decision");

        IModelElement[] partition =
                decisionModel.getReferencingModels(
                        IModelElementFactory.MODEL_TYPE_ACTIVITY_PARTITION, // <------ owner’s modelType (Partition’s modelType)
                        IActivityPartition.PROP_CONTAINED_ELEMENTS // <----- owner’s propertyName (Partition’s PROP_CONTAINED_ELEMENTS)
                );

        // could be in multiple due to other activity diagrams, but safe enough to assume it's just in this one, hence [0]
        if (partition.length > 0) {
            decisionNode.setSwimlane(partition[0].getName());
        }

        IRelationship[] outgoingRelationships = decisionModel.toFromRelationshipArray();
        for (IRelationship relationship : outgoingRelationships) {
            IModelElement targetElement = relationship.getTo();
            FlowNode branch = traverseAndBuild(targetElement, visited);
            Objects.requireNonNull(branch).setPrevLabelBranch(relationship.getName());
            decisionNode.addBranch(branch);
        }
        return decisionNode;
    }

    private FlowNode extractFork(IModelElement forkModel) {
        SplitFlowNode fork = new SplitFlowNode(forkModel.getName(), "fork");
        IRelationship[] outgoingRelationships = forkModel.toFromRelationshipArray();
        for (IRelationship relationship : outgoingRelationships) {
            IModelElement targetElement = relationship.getTo();
            FlowNode branch = traverseAndBuild(targetElement, visited);
            if (branch != null) {
                fork.addBranch(branch);
            }
        }
        return fork;
    }

    private IModelElement findInitialNode() {
        IDiagramElement[] initialNodeDiagramElements = diagram.toDiagramElementArray(SHAPE_TYPE_INITIAL_NODE);
        for (IDiagramElement initialNodeDiagramElement : initialNodeDiagramElements) {
            IModelElement initialNodeModelElement = initialNodeDiagramElement.getModelElement();
            //if (isRootLevel(initialNodeModelElement)) {
                return initialNodeModelElement;
            // }
        }
        IDiagramElement[] allElements = diagram.toDiagramElementArray();
        for (IDiagramElement diagramElement : allElements) {
            if (diagramElement.getShapeType() != SHAPE_TYPE_ACTIVITY_ACTION ||
                    diagramElement.getShapeType() != SHAPE_TYPE_FORK_NODE  ||
                    diagramElement.getShapeType() != SHAPE_TYPE_INITIAL_NODE) continue;
            IModelElement modelElement = diagramElement.getModelElement();
            if (modelElement == null) continue;
            if (modelElement.toRelationshipCount() == 0) {
                return modelElement;
            }
        }
        return null;
    }

    private void logFlow(FlowNode node, String indent) {
        if (node == null) {
            return;
        }

        if (node instanceof ActionData) {
            ActionData action = (ActionData) node;
            System.out.println(indent + "Action: " + action.getName() +
                    (action.isInitial() ? " (Initial)" : "") +
                    (action.isFinal() ? " (Final)" : ""));

            if (action.getNextNode() != null) {
                System.out.println(indent + "  -> Next:");
                logFlow(action.getNextNode(), indent + "    ");
            }
        }
        else if (node instanceof SplitFlowNode) {
            SplitFlowNode decision = (SplitFlowNode) node;
            System.out.println(indent + "Decision Node: " + decision.getName());

            for (int i = 0; i < decision.getBranches().size(); i++) {
                System.out.println(indent + "  Branch " + (i + 1) + ":");
                logFlow(decision.getBranches().get(i), indent + "    ");
            }
        } else if (node instanceof JoinFlowNode) {

            JoinFlowNode join = (JoinFlowNode) node;
            System.out.println(indent + "Join Node: " + join.getName());

            logFlow(join.getNextNode(), indent + "\t");

        }
    }

    private boolean checkSingleOutgoingEdge(IModelElement modelElement) {
        int outgoingCount = modelElement.fromRelationshipCount();
        return outgoingCount <= 1;
    }

    public FlowNode getRootNode() {
        return this.rootNode;
    }

}

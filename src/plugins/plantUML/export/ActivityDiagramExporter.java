package plugins.plantUML.export;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import plugins.plantUML.models.ActionData;
import plugins.plantUML.models.SplitFlowNode;
import plugins.plantUML.models.FlowNode;

import java.util.ArrayList;
import java.util.List;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public class ActivityDiagramExporter extends DiagramExporter {

    private final IDiagramUIModel diagram;
    private FlowNode rootNode;

    public ActivityDiagramExporter(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void extract() {
        IModelElement chosenInitialModelElement = findInitialNode();
        if (chosenInitialModelElement == null) {
            ApplicationManager.instance().getViewManager().showMessage("No valid initial element found.");
            return;
        }

        rootNode = traverseAndBuild(chosenInitialModelElement, new ArrayList<>());
        if (rootNode == null) {
            ApplicationManager.instance().getViewManager().showMessage("Failed to construct activity diagram structure.");
            return;
        }

        System.out.println("=============Constructed Activity Flow:");
        logFlow(rootNode, "");
        ApplicationManager.instance().getViewManager().showMessage("Export completed successfully.");
    }

    private FlowNode traverseAndBuild(IModelElement currentElement, List<IModelElement> visited) {
        if (visited.contains(currentElement)) {
            return null;
        }
        visited.add(currentElement);

        switch (currentElement.getModelType()) {
            case SHAPE_TYPE_ACTIVITY_ACTION:
            case SHAPE_TYPE_INITIAL_NODE:
            case SHAPE_TYPE_ACTIVITY_FINAL_NODE:
                if (!checkSingleOutgoingEdge(currentElement)) {
                    ApplicationManager.instance().getViewManager().showMessage("Error: an action can't have more than one outgoing edge.");
                    return null;
                }
                ActionData action = extractAction(currentElement);
                IRelationship[] outgoingRelationships = currentElement.toFromRelationshipArray();
                if (outgoingRelationships.length > 0) {
                    IModelElement targetElement = outgoingRelationships[0].getTo();
                    FlowNode nextNode = traverseAndBuild(targetElement, visited);
                    if (nextNode != null) {
                        action.setNextNode(nextNode);
                    }
                }
                return action;

            case SHAPE_TYPE_DECISION_NODE:
                return extractDecision(currentElement);

            case SHAPE_TYPE_FORK_NODE:
            case SHAPE_TYPE_JOIN_NODE:
            case SHAPE_TYPE_MERGE_NODE:
                return extractSplit(currentElement);

            default:
                ApplicationManager.instance().getViewManager().showMessage("ERROR: can't export type " + currentElement.getModelType() + ", no PlantUML equivalent.");
                return null;
        }
    }

    private ActionData extractAction(IModelElement actionModel) {
        ActionData actionData = new ActionData(actionModel.getName());
        actionData.setInitial(actionModel instanceof IInitialNode);
        actionData.setFinal(actionModel instanceof IActivityFinalNode);
        return actionData;
    }

    private SplitFlowNode extractDecision(IModelElement decisionModel) {
        SplitFlowNode decisionNode = new SplitFlowNode(decisionModel.getName(), "decision");
        IRelationship[] outgoingRelationships = decisionModel.toFromRelationshipArray();
        for (IRelationship relationship : outgoingRelationships) {
            IModelElement targetElement = relationship.getTo();
            FlowNode branch = traverseAndBuild(targetElement, new ArrayList<>());
            if (branch != null) {
                decisionNode.addBranch(branch);
            }
        }
        return decisionNode;
    }

    private FlowNode extractSplit(IModelElement forkOrJoinModel) {
        // TODO merge
        String type = (forkOrJoinModel instanceof IForkNode) ? "fork" : "join";
        SplitFlowNode forkOrJoinNode = new SplitFlowNode(forkOrJoinModel.getName(), type);
        IRelationship[] outgoingRelationships = forkOrJoinModel.toFromRelationshipArray();
        for (IRelationship relationship : outgoingRelationships) {
            IModelElement targetElement = relationship.getTo();
            FlowNode branch = traverseAndBuild(targetElement, new ArrayList<>());
            if (branch != null) {
                forkOrJoinNode.addBranch(branch);
            }
        }
        return forkOrJoinNode;
    }

    private IModelElement findInitialNode() {
        IDiagramElement[] initialNodeDiagramElements = diagram.toDiagramElementArray(SHAPE_TYPE_INITIAL_NODE);
        for (IDiagramElement initialNodeDiagramElement : initialNodeDiagramElements) {
            IModelElement initialNodeModelElement = initialNodeDiagramElement.getModelElement();
            if (isRootLevel(initialNodeModelElement)) {
                return initialNodeModelElement;
            }
        }
        IDiagramElement[] allElements = diagram.toDiagramElementArray();
        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();
            if (modelElement.toRelationshipCount() == 0) {
                return modelElement;
            }
        }
        return null;
    }

// TODO delete
    private void logFlow(FlowNode node, String indent) {
        if (node == null) {
            return;
        }

        // Handle ActionData
        if (node instanceof ActionData) {
            ActionData action = (ActionData) node;
            System.out.println(indent + "Action: " + action.getName() +
                    (action.isInitial() ? " (Initial)" : "") +
                    (action.isFinal() ? " (Final)" : ""));

            // Recursively log the next node
            if (action.getNextNode() != null) {
                System.out.println(indent + "  -> Next:");
                logFlow(action.getNextNode(), indent + "    ");
            }
        }
        // Handle DecisionNode
        else if (node instanceof SplitFlowNode) {
            SplitFlowNode decision = (SplitFlowNode) node;
            System.out.println(indent + "Decision Node: " + decision.getName());

            // Recursively log each branch
            for (int i = 0; i < decision.getBranches().size(); i++) {
                System.out.println(indent + "  Branch " + (i + 1) + ":");
                logFlow(decision.getBranches().get(i), indent + "    ");
            }
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

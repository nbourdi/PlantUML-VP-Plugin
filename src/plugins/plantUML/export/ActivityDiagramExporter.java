package plugins.plantUML.export;


import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import com.vp.vpserver.shared.admin.AdStringDTO;
import org.hibernate.mapping.Join;
import plugins.plantUML.models.ActionData;
import plugins.plantUML.models.JoinFlowNode;
import plugins.plantUML.models.SplitFlowNode;
import plugins.plantUML.models.FlowNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public class ActivityDiagramExporter extends DiagramExporter {

    private final IDiagramUIModel diagram;
    private FlowNode rootNode;
    private List<IModelElement> visited = new ArrayList<>();
    private Map<IModelElement, FlowNode> joinMap = new HashMap<>();

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

        System.out.println("=============Constructed Activity Flow:");
        logFlow(rootNode, "");
    }

    private FlowNode traverseAndBuild(IModelElement currentElement, List<IModelElement> visited) {
        if (visited.contains(currentElement)) {
            if (currentElement.getModelType() == SHAPE_TYPE_JOIN_NODE) {
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
                if (!checkSingleOutgoingEdge(currentElement)) {
                    throw new UnfitForExportException("Error: an action can't have more than one outgoing edge.");
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
                return extractFork(currentElement);

            case SHAPE_TYPE_JOIN_NODE:
                if (!checkSingleOutgoingEdge(currentElement)) {
                    throw new UnfitForExportException("Error: a join can't have more than one outgoing edge.");
                }
                JoinFlowNode join = extractJoin(currentElement);
                joinMap.put(currentElement, join);
                IRelationship[] outgoingRelationships2 = currentElement.toFromRelationshipArray();
                if (outgoingRelationships2.length > 0) {
                    ApplicationManager.instance().getViewManager().showMessage("1 outgoing relation");
                    IModelElement targetElement = outgoingRelationships2[0].getTo();
                    FlowNode nextNode = traverseAndBuild(targetElement, visited);
                    if (nextNode != null) {
                        ApplicationManager.instance().getViewManager().showMessage("next node isnt null.");
                        join.setNextNode(nextNode);
                    }
                }
                return join;

            default:
                throw new UnfitForExportException("ERROR: can't export type " + currentElement.getModelType() + ", no PlantUML equivalent.");
        }
    }

    private JoinFlowNode extractJoin(IModelElement joinModel) {
        JoinFlowNode forkOrJoinNode = new JoinFlowNode(joinModel.getName());

        return forkOrJoinNode;
    }

    private ActionData extractAction(IModelElement actionModel) {
        ActionData actionData = new ActionData(actionModel.getName());
        actionData.setInitial(actionModel instanceof IInitialNode);
        actionData.setFinal(actionModel instanceof IActivityFinalNode || actionModel instanceof IFlowFinalNode);
        return actionData;
    }

    private SplitFlowNode extractDecision(IModelElement decisionModel) {
        SplitFlowNode decisionNode = new SplitFlowNode(decisionModel.getName(), "decision");
        IRelationship[] outgoingRelationships = decisionModel.toFromRelationshipArray();
        for (IRelationship relationship : outgoingRelationships) {
            IModelElement targetElement = relationship.getTo();
            FlowNode branch = traverseAndBuild(targetElement, visited);
            if (branch != null) {
                decisionNode.addBranch(branch);
            }
        }
        return decisionNode;
    }

    private FlowNode extractFork(IModelElement forkModel) {
        // TODO merge
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

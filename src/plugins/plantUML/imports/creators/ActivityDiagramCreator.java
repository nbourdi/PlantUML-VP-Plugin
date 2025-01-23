package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IActivityDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.connector.IControlFlowUIModel;
import com.vp.plugin.diagram.shape.*;
import com.vp.plugin.model.IActivityAction;
import com.vp.plugin.model.IControlFlow;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.ActionData;
import plugins.plantUML.models.FlowNode;
import plugins.plantUML.models.SplitBranch;
import plugins.plantUML.models.SplitFlowNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityDiagramCreator extends DiagramCreator{

    private IActivityDiagramUIModel activityDiagram = (IActivityDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_ACTIVITY_DIAGRAM);
    private Map<IModelElement, IShapeUIModel> shapeMap = new HashMap<>();
    private IModelElement lastRootNode;
//    private IModelElement currentRootNode;

    public ActivityDiagramCreator(String diagramTitle) {
        super(diagramTitle);
        diagram = activityDiagram;
    }

    public void createDiagram(List<FlowNode> nodeList) {

        activityDiagram.setName(getDiagramTitle());

        IModelElement previousRootNode = createFlowNode(nodeList.get(0), null);
        //lastRootNode = previousRootNode;
//        currentRootNode = previous;

        for (int i = 1; i < nodeList.size(); i++) {
            FlowNode flowNode = nodeList.get(i);
            IModelElement current = createFlowNode(flowNode, previousRootNode);
          //  createControlFlow(previousRootNode, current);
            previousRootNode = current;
//            previousRootNode = lastRootNode;
//            lastRootNode = current;
//            current = lastRootNode;
        }

        diagramManager.layout(activityDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getDiagramManager().openDiagram(activityDiagram);
    }

    private void createControlFlow(IModelElement previous, IModelElement current) {
        if (previous == null || current == null) return;
        IControlFlow controlFlow = IModelElementFactory.instance().createControlFlow();
        controlFlow.setFrom(previous);
        controlFlow.setTo(current);
        diagramManager.createConnector(activityDiagram, controlFlow, shapeMap.get(previous), shapeMap.get(current), null);
    }

    private IModelElement createFlowNode(FlowNode flowNode, IModelElement previousUpNode) {

        IShapeUIModel shape;
        IModelElement element = null;
        if (flowNode instanceof ActionData) {
            if (((ActionData) flowNode).isInitial()) {
                element  = IModelElementFactory.instance().createInitialNode();
                shape = (IInitialNodeUIModel) diagramManager.createDiagramElement(activityDiagram, element);
            } else if (((ActionData) flowNode).isFinal()) {
                element = IModelElementFactory.instance().createActivityFinalNode();
                shape = (IActivityFinalUIModel) diagramManager.createDiagramElement(activityDiagram, element);
            } else if (((ActionData) flowNode).isFinalFlow()) {
                element = IModelElementFactory.instance().createFlowFinalNode();
                shape = (IFlowFinalUIModel) diagramManager.createDiagramElement(activityDiagram, element);
            } else {
                element = IModelElementFactory.instance().createActivityAction();
                shape = (IActivityActionUIModel) diagramManager.createDiagramElement(activityDiagram, element);
            }
            element.setName(((ActionData) flowNode).getName());
            shapeMap.put(element, shape);
            createControlFlow(previousUpNode, element);
            // TODO get swimlanes, see if created already and if not create the swimlane and partition (if swimlane not exist yet)
        } else if (flowNode instanceof SplitFlowNode) {
            if (((SplitFlowNode) flowNode).getType() == "decision") {
                element = IModelElementFactory.instance().createDecisionNode();
                shape = (IDecisionNodeUIModel) diagramManager.createDiagramElement(activityDiagram, element);
                shapeMap.put(element, shape);

                createControlFlow(previousUpNode, element);

                for (SplitBranch branch : ((SplitFlowNode) flowNode).getSplitBranches()) {
                    List<FlowNode> nodes = branch.getNodeList();
                    IModelElement previous = createFlowNode(nodes.get(0), previousUpNode);
                    // previous needs to connect to decision
                    createControlFlow(previous, element);

                    for (int i = 1; i < nodes.size(); i++) {
                        FlowNode node = nodes.get(i);
                        IModelElement current = createFlowNode(node, previous);
                        createControlFlow(current, element);
                        previous = current;
                    }
                }
            } else if (((SplitFlowNode) flowNode).getType() == "fork") {
                element = IModelElementFactory.instance().createForkNode();
                shape = (IForkNodeUIModel) diagramManager.createDiagramElement(activityDiagram, element);
                shapeMap.put(element, shape);

                createControlFlow(previousUpNode, element);

                List<IModelElement> lastInBranch = new ArrayList<>();

                for (SplitBranch branch : ((SplitFlowNode) flowNode).getSplitBranches()) {
                    List<FlowNode> nodes = branch.getNodeList();
                    IModelElement previous = createFlowNode(nodes.get(0), element);
//                    createControlFlow(element, previous);

                    for (int i = 1; i < nodes.size(); i++) {
                        FlowNode node = nodes.get(i);
                        IModelElement current = createFlowNode(node, previous);
                        // createControlFlow(previous, current);
                        previous = current;
                    }
                    // we reached the end of the branch, save the last element to link with the join
                    lastInBranch.add(previous);
                }

                IModelElement joinElement = IModelElementFactory.instance().createJoinNode();
                IJoinNodeUIModel joinShape = (IJoinNodeUIModel) diagramManager.createDiagramElement(activityDiagram, joinElement);
                shapeMap.put(joinElement, joinShape);
                for (IModelElement lastInBranchItem : lastInBranch)
                    createControlFlow(lastInBranchItem, joinElement);
                //lastRootNode = joinElement;
                return joinElement;
            }
        }
        return element;
    }
}

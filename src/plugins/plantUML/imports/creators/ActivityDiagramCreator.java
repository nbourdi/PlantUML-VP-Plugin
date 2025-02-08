package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IActivityDiagramUIModel;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.shape.*;
import com.vp.plugin.model.IActivityPartition;
import com.vp.plugin.model.IActivitySwimlane2;
import com.vp.plugin.model.IControlFlow;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.ActionData;
import plugins.plantUML.models.FlowNode;
import plugins.plantUML.models.SplitBranch;
import plugins.plantUML.models.SplitFlowNode;

import java.util.*;

public class ActivityDiagramCreator extends DiagramCreator{

    private IActivityDiagramUIModel activityDiagram = (IActivityDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_ACTIVITY_DIAGRAM);
    private Map<IModelElement, IShapeUIModel> shapeMap = new HashMap<>();
    private Set<String> createdSwimlanes = new HashSet<>();
    private Map<String, IActivityPartition> partitionMap = new HashMap<>();
    IActivitySwimlane2 parentSwimlane = null;
    IActivitySwimlane2NewUIModel parentSwimlaneShape = null;

    public ActivityDiagramCreator(String diagramTitle) {
        super(diagramTitle);
        diagram = activityDiagram;
    }

    public void createDiagram(List<FlowNode> nodeList) {

        activityDiagram.setName(getDiagramTitle());

        IModelElement previousRootNode = createFlowNode(nodeList.get(0), null);


        for (int i = 1; i < nodeList.size(); i++) {
            FlowNode flowNode = nodeList.get(i);
            IModelElement current = createFlowNode(flowNode, previousRootNode);
            previousRootNode = current;
        }

        diagramManager.layout(activityDiagram, DiagramManager.LAYOUT_HIERARCHIC);
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
            
            String swimlane = ((ActionData) flowNode).getSwimlane();
            if (!swimlane.isEmpty()) {
                IActivityPartition partition = createSwimlaneIfNotExist(swimlane);
                parentSwimlane.addChild(element);
                partition.addContainedElement(element);
            }

        } else if (flowNode instanceof SplitFlowNode) {
            if (((SplitFlowNode) flowNode).getType() == "decision") {
                element = IModelElementFactory.instance().createDecisionNode();
                element.setName(((SplitFlowNode) flowNode).getName());
                shape = (IDecisionNodeUIModel) diagramManager.createDiagramElement(activityDiagram, element);
                shapeMap.put(element, shape);
                shape.resetCaption();

                createControlFlow(previousUpNode, element);

                for (SplitBranch branch : ((SplitFlowNode) flowNode).getSplitBranches()) {
                    List<FlowNode> nodes = branch.getNodeList();
                    IModelElement previous = createFlowNode(nodes.get(0), element);

                    for (int i = 1; i < nodes.size(); i++) {
                        FlowNode node = nodes.get(i);
                        previous = createFlowNode(node, previous);
                    }
                }
            } else if (((SplitFlowNode) flowNode).getType() == "fork") {
                element = IModelElementFactory.instance().createForkNode();
                element.setName(((SplitFlowNode) flowNode).getName());
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

                IModelElement joinElement;
                IShapeUIModel joinShape;

                if (((SplitFlowNode) flowNode).isMergeStyleJoin()) { // "end merge" type
                    joinElement = IModelElementFactory.instance().createMergeNode();
                    joinShape = (IMergeNodeUIModel) diagramManager.createDiagramElement(activityDiagram, joinElement);
                }
                else { // "end fork" type
                    joinElement = IModelElementFactory.instance().createJoinNode();
                    joinShape = (IJoinNodeUIModel) diagramManager.createDiagramElement(activityDiagram, joinElement);
                }
                shapeMap.put(joinElement, joinShape);

                for (IModelElement lastInBranchItem : lastInBranch)
                    createControlFlow(lastInBranchItem, joinElement);
                return joinElement;
            }
        }
        return element;
    }

    private IActivityPartition createSwimlaneIfNotExist(String swimlane) {
        if (createdSwimlanes.contains(swimlane)) {
            return partitionMap.get(swimlane);
        }

        // isn't created
        if (parentSwimlane == null) {
            parentSwimlane = IModelElementFactory.instance().createActivitySwimlane2();
            parentSwimlaneShape = (IActivitySwimlane2NewUIModel) diagramManager.createDiagramElement(activityDiagram, parentSwimlane);
            parentSwimlaneShape.setRequestResetCaption(true);
        }
        IActivityPartition partition = IModelElementFactory.instance().createActivityPartition();
        partition.setName(swimlane);

        parentSwimlane.addVerticalPartition(partition);
        parentSwimlane.addChild(partition);

        IActivityPartitionHeaderUIModel headerUIModel = (IActivityPartitionHeaderUIModel) diagramManager.createDiagramElement(activityDiagram, partition);
        headerUIModel.setHorizontal(false);
        headerUIModel.setSwimlane(parentSwimlaneShape);
        parentSwimlaneShape.addChild(headerUIModel);
        headerUIModel.setRequestResetCaption(true);


        String[] ids = parentSwimlaneShape.getVerticalPartitionIds() == null ? new String[]{} : parentSwimlaneShape.getVerticalPartitionIds();
        String[] updatedIds = new String[ids.length + 1];
        System.arraycopy(ids, 0, updatedIds, 0, ids.length);
        updatedIds[ids.length] = partition.getId();
        parentSwimlaneShape.setVerticalPartitionIds(updatedIds);


        IActivitySwimlane2CompartmentUIModel cellPartition = (IActivitySwimlane2CompartmentUIModel) diagramManager.createDiagramElement(diagram, IShapeTypeConstants.SHAPE_TYPE_ACTIVITY_SWIMLANE2_COMPARTMENT);
        cellPartition.setVerticalPartitionId(headerUIModel.getId());
        parentSwimlaneShape.addChild(cellPartition);

        String[] cids = parentSwimlaneShape.getCompartmentIds() == null ? new String[]{} : parentSwimlaneShape.getCompartmentIds();
        String[] updatedcIds = new String[cids.length + 1];
        System.arraycopy(cids, 0, updatedcIds, 0, cids.length);
        updatedcIds[cids.length] = cellPartition.getId();
        parentSwimlaneShape.setCompartmentIds(updatedcIds);

        partitionMap.put(swimlane, partition);
        createdSwimlanes.add(swimlane);

        return partition;
    }
}

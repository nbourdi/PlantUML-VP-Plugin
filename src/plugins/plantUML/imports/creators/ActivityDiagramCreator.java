package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IActivityDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.connector.IControlFlowUIModel;
import com.vp.plugin.diagram.shape.IActivityActionUIModel;
import com.vp.plugin.diagram.shape.IActivityFinalUIModel;
import com.vp.plugin.diagram.shape.IFlowFinalUIModel;
import com.vp.plugin.diagram.shape.IInitialNodeUIModel;
import com.vp.plugin.model.IActivityAction;
import com.vp.plugin.model.IControlFlow;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.ActionData;
import plugins.plantUML.models.FlowNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityDiagramCreator extends DiagramCreator{

    IActivityDiagramUIModel activityDiagram = (IActivityDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_ACTIVITY_DIAGRAM);
    Map<IModelElement, IShapeUIModel> shapeMap = new HashMap<>();

    public ActivityDiagramCreator(String diagramTitle) {
        super(diagramTitle);
        diagram = activityDiagram;
    }

    public void createDiagram(List<FlowNode> nodeList) {

        activityDiagram.setName(getDiagramTitle());

        IModelElement previous = createFlowNode(nodeList.get(0));

        for (int i = 1; i < nodeList.size(); i++) {
            FlowNode flowNode = nodeList.get(i);
            IModelElement current = createFlowNode(flowNode);
            createControlFlow(previous, current);
            previous = current;
        }

        diagramManager.layout(activityDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getDiagramManager().openDiagram(activityDiagram);
    }

    private void createControlFlow(IModelElement previous, IModelElement current) {
        IControlFlow controlFlow = IModelElementFactory.instance().createControlFlow();
        controlFlow.setFrom(previous);
        controlFlow.setTo(current);
        IControlFlowUIModel controlFlowUIModel = (IControlFlowUIModel) diagramManager.createConnector(activityDiagram, controlFlow, shapeMap.get(previous), shapeMap.get(current), null);
    }

    private IModelElement createFlowNode(FlowNode flowNode) {

        IShapeUIModel shape = null;
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
            // TODO get swimlanes, see if created already and if not create the swimlane and partition (if swimlane not exist yet)
        }

        shapeMap.put(element, shape);
        return element;
    }
}

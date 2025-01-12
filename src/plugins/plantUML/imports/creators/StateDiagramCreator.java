package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.IStateDiagramUIModel;
import com.vp.plugin.diagram.shape.*;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.ForkJoin;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.StateChoice;
import plugins.plantUML.models.StateData;

import java.util.List;

public class StateDiagramCreator extends DiagramCreator {

    IStateDiagramUIModel stateDiagram = (IStateDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_STATE_DIAGRAM);
    public StateDiagramCreator(String diagramTitle) {
        super(diagramTitle);
        diagram = stateDiagram;
    }

    public void createDiagram (List<StateData> stateDatas, List<StateChoice> stateChoices, List<ForkJoin> forkJoins, List<RelationshipData> transitions) {
        stateDiagram.setName(getDiagramTitle());

        stateDatas.forEach(this::createState);
        stateChoices.forEach(this::createChoice);
        transitions.forEach(this::createRelationship);

        diagramManager.layout(stateDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getProjectManager().saveProject();
        ApplicationManager.instance().getDiagramManager().openDiagram(stateDiagram);

    }


    private void createChoice(StateChoice stateChoice) {
        IChoice choiceModel = IModelElementFactory.instance().createChoice();
        String entityId = stateChoice.getUid();
        elementMap.put(entityId, choiceModel);

        //no name conflict.

        choiceModel.setName(stateChoice.getName());

        IChoiceUIModel choiceShape = (IChoiceUIModel) diagramManager.createDiagramElement(stateDiagram, choiceModel);
        shapeMap.put(choiceModel, choiceShape);
        choiceShape.fitSize();
    }

    private void createState(StateData stateData) {

        IModelElement stateModel = null;
        IShapeUIModel stateShape = null;
        if (stateData.isStart()) {
            stateModel = IModelElementFactory.instance().createInitialPseudoState();
            stateShape = (IInitialPseudoStateUIModel) diagramManager.createDiagramElement(stateDiagram, stateModel);
        } else if (stateData.isEnd()) {
            stateModel = IModelElementFactory.instance().createFinalState2();
            stateShape = (IFinalState2UIModel) diagramManager.createDiagramElement(stateDiagram, stateModel);
        } else {
            stateModel = IModelElementFactory.instance().createState2();
            putInSemanticsMap((IHasChildrenBaseModelElement) stateModel, stateData);
            stateShape = (IState2UIModel) diagramManager.createDiagramElement(stateDiagram, stateModel);

        }
        String entityId = stateData.getUid();
        elementMap.put(entityId, stateModel);

        // no name conflict.
        stateModel.setName(stateData.getName());


        shapeMap.put(stateModel, stateShape);
        stateShape.fitSize();
    }

}

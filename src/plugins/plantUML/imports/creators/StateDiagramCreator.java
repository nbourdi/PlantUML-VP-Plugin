package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.IStateDiagramUIModel;
import com.vp.plugin.diagram.shape.*;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.*;

import java.util.List;

public class StateDiagramCreator extends DiagramCreator {

    IStateDiagramUIModel stateDiagram = (IStateDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_STATE_DIAGRAM);
    public StateDiagramCreator(String diagramTitle) {
        super(diagramTitle);
        diagram = stateDiagram;
    }

    public void createDiagram (List<StateData> stateDatas, List<StateChoice> stateChoices, List<ForkJoin> forkJoins, List<RelationshipData> transitions, List<History> histories, List<NoteData> noteDatas) {
        stateDiagram.setName(getDiagramTitle());

        stateDatas.forEach(this::createState);
        stateChoices.forEach(this::createChoice);
        forkJoins.forEach(this::createForkJoin);
        histories.forEach(this::createHistory);
        noteDatas.forEach(this::createNote);
        transitions.forEach(this::createRelationship);

        diagramManager.layout(stateDiagram, DiagramManager.LAYOUT_AUTO);
        diagramManager.layout(stateDiagram, DiagramManager.LAYOUT_HIERARCHIC);
        ApplicationManager.instance().getDiagramManager().openDiagram(stateDiagram);
    }

    private void createHistory(History history) {
        IModelElement historyModel;
        IShapeUIModel historyShape;
        if (history.isDeep()) {
            historyModel = IModelElementFactory.instance().createDeepHistory();
            historyShape = (IDeepHistoryUIModel) diagramManager.createDiagramElement(stateDiagram, historyModel);
        } else {
            historyModel = IModelElementFactory.instance().createShallowHistory();
            historyShape = (IShallowHistoryUIModel) diagramManager.createDiagramElement(stateDiagram, historyModel);
        }
        historyModel.setName(history.getName());
        elementMap.put(history.getUid(), historyModel);
        shapeMap.put(historyModel, historyShape);
    }

    private void createForkJoin(ForkJoin forkJoin) {
        IModelElement forkOrJoin;
        IShapeUIModel shape;
        if (forkJoin.isFork()) {
            forkOrJoin = IModelElementFactory.instance().createForkNode();
            shape = (IForkNodeUIModel) diagramManager.createDiagramElement(stateDiagram, forkOrJoin);
        } else {
            forkOrJoin = IModelElementFactory.instance().createJoinNode();
            shape = (IJoinNodeUIModel) diagramManager.createDiagramElement(stateDiagram, forkOrJoin);
        }
        elementMap.put(forkJoin.getUid(), forkOrJoin);
        shapeMap.put(forkOrJoin, shape);
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

            if (!stateData.getRegions().isEmpty()) {
                for (StateData.StateRegion stateRegion : stateData.getRegions()) {
                    IRegion region1 = ((IState2) stateModel).createRegion();
                    IRegionUIModel region1UIModel = (IRegionUIModel) diagramManager.createDiagramElement(stateDiagram, region1);
                    stateShape.addChild(region1UIModel);
                    for (StateData subState : stateRegion.getSubStates()) {
                        createState(subState);
                        region1.addChild(elementMap.get(subState.getUid()));
                        region1UIModel.addChild((IShapeUIModel) shapeMap.get(elementMap.get(subState.getUid())));
                    }
                    for (History history : stateRegion.getHistories()) {
                        createHistory(history);
                        region1.addChild(elementMap.get(history.getUid()));
                        region1UIModel.addChild((IShapeUIModel) shapeMap.get(elementMap.get(history.getUid())));
                    }
                    region1UIModel.fitSize();
                }
            }
        }
        String entityId = stateData.getUid();
        elementMap.put(entityId, stateModel);

        // no name conflict.
        stateModel.setName(stateData.getName());


        shapeMap.put(stateModel, stateShape);
        stateShape.fitSize();

    }

}

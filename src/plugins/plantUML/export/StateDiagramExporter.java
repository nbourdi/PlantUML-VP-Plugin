package plugins.plantUML.export;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import org.apache.commons.lang.ObjectUtils;
import plugins.plantUML.models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StateDiagramExporter extends DiagramExporter {

    private IDiagramUIModel diagram;
    List<NoteData> exportedNotes = new ArrayList<>();
    List<StateData> stateDatas = new ArrayList<>();
    List<History> histories = new ArrayList<>();
    List<RelationshipData> transitions = new ArrayList<>();
    List<StateChoice> choices = new ArrayList<>();
    List<ForkJoin> forkJoins = new ArrayList<>();


    public StateDiagramExporter(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void extract() {
        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        List<IRelationship> deferredRelationships = new ArrayList<>();

        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement == null) {
                ApplicationManager.instance().getViewManager()
                        .showMessage("Warning: modelElement is null for a diagram element.");
                continue;
            }

            // Add to exported elements list
            allExportedElements.add(modelElement);

            if (modelElement instanceof IState2) {
                if (isRootLevel(modelElement)) {
                    extractState((IState2) modelElement, null);
                }
            } else if (modelElement instanceof IInitialPseudoState) {
                if (isRootLevel(modelElement)) {
                    extractInitFin(modelElement, null, true);
                }
            } else if (modelElement instanceof IFinalState2) {
                if (isRootLevel(modelElement)) {
                    extractInitFin(modelElement, null, false);
                }
            } else if (modelElement instanceof IChoice) {
                if (isRootLevel(modelElement)) {
                    extractChoice((IChoice) modelElement, null);
                }
            } else if (modelElement instanceof IShallowHistory || modelElement instanceof IDeepHistory) {
                if (isRootLevel(modelElement)) {
                    extractHistory(modelElement, null);
                }
            }
            else if (modelElement instanceof  IForkNode || modelElement instanceof IJoinNode) {
                extractForkJoin(modelElement);
            } else if (modelElement instanceof INOTE) {
                extractNote((INOTE) modelElement);
            } else if (modelElement instanceof IRelationship) {
                deferredRelationships.add((IRelationship) modelElement); // Defer relationships
            } else {
                allExportedElements.remove(modelElement);
                ApplicationManager.instance().getViewManager()
                        .showMessage("Warning: diagram element " + modelElement.getName()
                                + " is of unsupported type and will not be processed ... ");
            }
        }

        for (IRelationship relationship : deferredRelationships) {
            extractTransition(relationship);
        }

        exportedNotes = getNotes();
    }

    private void extractHistory(IModelElement modelElement, StateData.StateRegion regionData) {
        History history = new History(modelElement.getName());
        history.setDeep(modelElement instanceof IDeepHistory);
        history.setId(modelElement.getId());
        history.setInState(modelElement.getParent() instanceof IRegion);
        if (regionData != null) {
            regionData.getSubStates().add(history);
        }
        histories.add(history);
    }

    private void extractForkJoin(IModelElement modelElement) {
        String id = modelElement.getId();

        ForkJoin forkJoin = new ForkJoin(modelElement.getName());
        forkJoin.setFork(modelElement instanceof IForkNode);

        forkJoin.setId(id);
        forkJoins.add(forkJoin);
    }

    private void extractInitFin(IModelElement initElement, StateData.StateRegion parentRegion, boolean isStart) {
        boolean isInState = (initElement.getParent() instanceof IRegion);

        StateData stateData = new StateData(initElement.getName());
        stateData.setDescription(initElement.getDescription());
        stateData.setInState(isInState);
        stateData.setStart(isStart);
        stateData.setEnd(!isStart);
        stateData.setId(initElement.getId());

        if (parentRegion != null) {
            parentRegion.getSubStates().add(stateData);
        }
        stateDatas.add(stateData);
    }

    private void extractTransition(IRelationship relationship) {
        IModelElement source = relationship.getFrom();
        IModelElement target = relationship.getTo();
        String sourceName;
        String targetName;
        try {
            sourceName = source.getName();
            targetName = target.getName();
        }
        catch (NullPointerException e) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's elements were null possibly due to a previously imported illegal relationship (e.g. an Anchor between classes)");
            return;
        }

        if (source instanceof IState2 || source instanceof IInitialPseudoState || source instanceof IFinalState2 || source instanceof IChoice) {
            sourceName = getStateAliasById(source.getId());
        } else if (source instanceof INOTE) {
            sourceName = getNoteAliasById(source.getId());
        }
        if (target instanceof IState2 || target instanceof IInitialPseudoState || target instanceof IFinalState2 || target instanceof IChoice) {
            targetName = getStateAliasById(target.getId());
        } else if (target instanceof INOTE) {
            targetName = getNoteAliasById(target.getId());
        }

        ApplicationManager.instance().getViewManager()
                .showMessage("Relationship from: " + sourceName + " to: " + targetName);
        ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());

        if (sourceName == null || targetName == null) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
        }

        if (Objects.equals(relationship.getModelType(), "Anchor")) return;
        RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(), relationship.getName());
        transitions.add(relationshipData);
    }

    private String getStateAliasById(String id) {
        for (StateData stateData : stateDatas) {
            if (stateData.getId().equals(id)) {
                return stateData.getAlias();
            }
        }
        for (StateChoice stateChoice :choices) {
            if (stateChoice.getId().equals(id)) {
                return stateChoice.getAlias();
            }
        }
        return null;
    }

    private void extractChoice(IChoice choiceModel, StateData.StateRegion parentRegion) {
        boolean isInState = (choiceModel.getParent() instanceof IRegion);

        String id = choiceModel.getId();

        StateChoice stateChoice = new StateChoice(choiceModel.getName());
        stateChoice.setDescription(choiceModel.getDescription());
        stateChoice.setInState(isInState);
        stateChoice.setId(id);

        choices.add(stateChoice);

    }

    private void extractState(IState2 stateModel, StateData.StateRegion parentRegion) {

        boolean isInState = (stateModel.getParent() instanceof IRegion);

        String id = stateModel.getId();

        StateData stateData = new StateData(stateModel.getName());
        stateData.setDescription(stateModel.getDescription());
        stateData.setInState(isInState);
        stateData.setId(id);

        addSemanticsIfExist(stateModel, stateData);

        Iterator regionIter = stateModel.regionIterator();
        while (regionIter.hasNext()) {
            IRegion regionModel = (IRegion) regionIter.next();
            StateData.StateRegion regionData  = new StateData.StateRegion();
            stateData.getRegions().add(regionData);

            Iterator stateIter = regionModel.state2Iterator();
            while (stateIter.hasNext()) {
                IState2 subStateModel = (IState2) stateIter.next();
                extractState(subStateModel, regionData);
            }

            Iterator initIter = regionModel.initialPseudoStateIterator();
            while (initIter.hasNext()) {
                IInitialPseudoState subInit = (IInitialPseudoState) initIter.next();
                extractInitFin(subInit, regionData, true);
            }

            Iterator finIter = regionModel.finalState2Iterator();
            while (finIter.hasNext()) {
                IFinalState2 subFin = (IFinalState2) finIter.next();
                extractInitFin(subFin, regionData, false);
            }

            Iterator choiceIter = regionModel.choiceIterator();
            while (choiceIter.hasNext()) {
                IChoice subChoice = (IChoice) choiceIter.next();
                extractChoice(subChoice, regionData);
            }

            Iterator histIter = regionModel.deepHistoryIterator();
            while (histIter.hasNext()) {
                IDeepHistory hist = (IDeepHistory) histIter.next();
                extractHistory(hist, regionData);
            }

            Iterator histIter2 = regionModel.deepHistoryIterator();
            while (histIter2.hasNext()) {
                IDeepHistory hist = (IDeepHistory) histIter2.next();
                extractHistory(hist, regionData);
            }
        }

        if (parentRegion != null) {
            parentRegion.getSubStates().add(stateData);
        }
        stateDatas.add(stateData);
    }


    public List<History> getHistories() {
        return histories;
    }

    public List<StateData> getStateDatas() {
        return stateDatas;
    }

    public List<ForkJoin> getForkJoins() {
        return forkJoins;
    }

    public List<RelationshipData> getTransitions() {
        return transitions;
    }

    public List<StateChoice> getChoices() {
        return choices;
    }

}


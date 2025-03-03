package plugins.plantUML.export;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import plugins.plantUML.models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;

public class StateDiagramExporter extends DiagramExporter {

    private final IDiagramUIModel diagram;
    List<NoteData> exportedNotes = new ArrayList<>();
    List<StateData> stateDatas = new ArrayList<>();
    List<History> histories = new ArrayList<>();
    List<RelationshipData> transitions = new ArrayList<>();
    List<StateChoice> choices = new ArrayList<>();
    List<ForkJoin> forkJoins = new ArrayList<>();

    // call at the very end to fix insane aliases
    private void prettifyAliases(List<? extends StateData> stateDatas, String type) {
            // iterate through them and change the aliases
        int alias_counter = 0;
        for (StateData stateData : stateDatas) {
            stateData.setAlias(type + "_" + alias_counter);
            alias_counter++;
        }
    }


    public StateDiagramExporter(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }

    @Override
    public void extract() {
        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        List<IRelationship> deferredRelationships = new ArrayList<>();

        IDiagramElement[] stateDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_REGION);

        for (IDiagramElement stateElement : stateDiagramElems) {
            String packageModelId = stateElement.getModelElement().getId();
            packageModelIds.add(packageModelId);
        }



        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement == null) {
                ApplicationManager.instance().getViewManager()
                        .showMessage("Warning: modelElement is null for a diagram element.");
                addWarning("ModelElement is null for a diagram element.");
                continue;
            }

            // Add to exported elements list
            allExportedElements.add(modelElement);

            if (modelElement instanceof IState2) {
               if (isRootLevelInDiagram(modelElement)) {
                   extractState((IState2) modelElement, null);
               }
            } else if (modelElement instanceof IInitialPseudoState) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractInitFin(modelElement, null, true);
                }
            } else if (modelElement instanceof IFinalState2) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractInitFin(modelElement, null, false);
                }
            } else if (modelElement instanceof IChoice) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractChoice((IChoice) modelElement, null);
                }
            } else if (modelElement instanceof IShallowHistory || modelElement instanceof IDeepHistory) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractHistory(modelElement, null);
                }
            } else if (modelElement instanceof  IFork || modelElement instanceof IJoin) {
                if (isRootLevelInDiagram(modelElement)) {
                    extractForkJoin(modelElement, null);
                }
            } else if (modelElement instanceof INOTE) {
                extractNote((INOTE) modelElement);
            } else if (modelElement instanceof IRelationship) {
                deferredRelationships.add((IRelationship) modelElement); // Defer relationships
            } else {
                allExportedElements.remove(modelElement);
                if (!(modelElement instanceof IRegion)) {
                    ApplicationManager.instance().getViewManager()
                            .showMessage("Warning: diagram element " + modelElement.getName()
                                    + " is of unsupported type " + modelElement.getModelType() + " and will not be processed ... ");
                    addWarning("Diagram element " + modelElement.getName()
                            + " is of unsupported type " + modelElement.getModelType() + " and was not processed. ");
                }
            }
        }

        prettifyAliases(stateDatas, "state");
        prettifyAliases(choices, "choice");
        prettifyAliases(forkJoins, "forkjoin");
        prettifyAliases(histories, "history");

        for (IRelationship relationship : deferredRelationships) {
            extractTransition(relationship);
        }
        filterAndExportTransitions();
        exportedNotes = getNotes();
    }

    private void extractHistory(IModelElement modelElement, StateData.StateRegion regionData) {
        History history = new History(modelElement.getName());
        history.setDeep(modelElement instanceof IDeepHistory);
        history.setId(modelElement.getId());
        history.setInState(modelElement.getParent() instanceof IRegion || regionData != null);
        if (regionData != null) {
            regionData.getSubStates().add(history);
        }
        histories.add(history);
    }

    private void extractForkJoin(IModelElement modelElement, StateData.StateRegion regionData) {
        String id = modelElement.getId();

        ForkJoin forkJoin = new ForkJoin(modelElement.getName());
        forkJoin.setFork(modelElement instanceof IFork);
        forkJoin.setInState(modelElement.getParent() instanceof IRegion || regionData != null);
        forkJoin.setId(id);

        if (regionData != null) {
            regionData.getSubStates().add(forkJoin);
        }
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
            addWarning("One of the relationship's elements were null possibly due to a previously imported illegal relationship (e.g. an Anchor between classes)");
            return;
        }

        sourceName = getAliasByType(source, sourceName);
        targetName = getAliasByType(target, targetName);

//        ApplicationManager.instance().getViewManager()
//                .showMessage("Relationship from: " + sourceName + " to: " + targetName);
//        ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());

        if (sourceName == null || targetName == null) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");

            addWarning("One of the relationship's elements were null possibly due to a previously imported illegal relationship (e.g. an Anchor between classes)");
            return;
        }

        if (Objects.equals(relationship.getModelType(), "Anchor")) return;
        RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(), relationship.getName());

        StateData sourceState = findStateById(source.getId());
        StateData targetState = findStateById(target.getId());
        for (StateData state : stateDatas) {
            for (StateData.StateRegion region : state.getRegions()) {
                if (region.getSubStates().contains(sourceState) && region.getSubStates().contains(targetState)) {
                    region.getRegTransitions().add(relationshipData);
                    return;
                }
            }
        }

        transitions.add(relationshipData);

    }
    private String getAliasByType(IModelElement element, String original) {
        if (element instanceof IState2 || element instanceof IInitialPseudoState ||
                element instanceof IFinalState2 || element instanceof IChoice ||
                element instanceof IDeepHistory || element instanceof IShallowHistory ||
                element instanceof IFork || element instanceof IJoin) {
            return getStateAliasById(element.getId());
        } else if (element instanceof INOTE) {
            return getNoteAliasById(element.getId());
        }
        return original;
    }

    private StateData findStateById(String id) {
        for (StateData stateData : stateDatas) {
            if (stateData.getId().equals(id)) {
                return stateData;
            }
        }
        return null;
    }

    private String getStateAliasById(String id) {
        for (StateData stateData : stateDatas) {
            if (stateData.getId().equals(id)) {
                return stateData.getAlias();
            }
        }

        for (StateChoice stateChoice : choices) {
            if (stateChoice.getId().equals(id)) {
                return stateChoice.getAlias();
            }
        }

        for (StateData hist : histories) {
            if (hist.getId().equals(id)) {
                return hist.getAlias();
            }
        }

        for (ForkJoin forkJoin : forkJoins) {
            if (forkJoin.getId().equals(id)) {
                return forkJoin.getAlias();
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

            Iterator histIter2 = regionModel.shallowHistoryIterator();
            while (histIter2.hasNext()) {
                IDeepHistory hist = (IDeepHistory) histIter2.next();
                extractHistory(hist, regionData);
            }

            Iterator forkIter = regionModel.forkIterator();
            while (forkIter.hasNext()) {
                IFork fork = (IFork) forkIter.next();
                extractForkJoin(fork, regionData);
            }

            Iterator joinIter = regionModel.joinIterator();
            while (joinIter.hasNext()) {
                IJoin join = (IJoin) joinIter.next();
                extractForkJoin(join, regionData);
            }

        }

        if (parentRegion != null) {
            parentRegion.getSubStates().add(stateData);
        }
        stateDatas.add(stateData);
    }

    private void filterAndExportTransitions() {
        List<RelationshipData> validTransitions = new ArrayList<>();

        for (RelationshipData transition : transitions) {
            StateData sourceState = findStateByAlias(transition.getSource());
            StateData targetState = findStateByAlias(transition.getTarget());

            if (sourceState == null || targetState == null) {
                validTransitions.add(transition);
                continue;
            }

            // Get regions for both states
            StateData.StateRegion sourceRegion = findRegionContainingState(sourceState);
            StateData.StateRegion targetRegion = findRegionContainingState(targetState);

            if (sourceRegion == targetRegion) {
                // Valid if both are in the same region
                validTransitions.add(transition);
            } else if (sourceRegion == null || targetRegion == null) {
                // Valid if one state is not in a region and the other belongs to a single-region state
                StateData containingState = findStateContainingRegion(sourceRegion != null ? sourceRegion : targetRegion);
                if (containingState == null || containingState.getRegions().size() == 1) {
                    validTransitions.add(transition);
                } else {
                    outputLostTransitionWarning(transition, "One state is not in a region, but the other belongs to a multi-region state.");
                }
            } else {
                // Check if both regions belong to a single-region state
                StateData sourceContainingState = findStateContainingRegion(sourceRegion);
                StateData targetContainingState = findStateContainingRegion(targetRegion);

                if (sourceContainingState != null && sourceContainingState == targetContainingState
                        && sourceContainingState.getRegions().size() == 1) {
                    validTransitions.add(transition);
                } else {
                    outputLostTransitionWarning(transition, "States are in different regions of a multi-region state.");
                }
            }
        }

        // Replace original transitions with valid ones
        transitions = validTransitions;
    }

    private void outputLostTransitionWarning(RelationshipData transition, String reason) {
        ApplicationManager.instance().getViewManager()
                .showMessage("Warning: Transition '" + transition.getName() + "' lost during export because it's illegal in PlantUML. Reason: " + reason);
        addWarning("Transition '" + transition.getName() + "' lost during export because it's illegal in PlantUML. Reason: " + reason);
    }


    private StateData.StateRegion findRegionContainingState(StateData state) {
        for (StateData containerState : stateDatas) {
            for (StateData.StateRegion region : containerState.getRegions()) {
                if (region.getSubStates().contains(state)) {
                    return region;
                }
            }
        }
        return null;
    }

    private StateData findStateContainingRegion(StateData.StateRegion region) {
        for (StateData state : stateDatas) {
            if (state.getRegions().contains(region)) {
                return state;
            }
        }
        return null;
    }

    private StateData findStateByAlias(String alias) {
        for (StateData stateData : stateDatas) {
            if (stateData.getAlias().equals(alias)) {
                return stateData;
            }
        }
        return null;
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


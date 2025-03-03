package plugins.plantUML.imports.importers;

import com.vp.plugin.ApplicationManager;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.statediagram.StateDiagram;
import plugins.plantUML.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StateDiagramImporter extends DiagramImporter {

    private StateDiagram stateDiagram;
    private List<StateData> stateDatas = new ArrayList<>();
    private List<RelationshipData> relationshipDatas = new ArrayList<>();
    private List<ForkJoin> forkJoins = new ArrayList<>();
    private List<StateChoice> stateChoices = new ArrayList<>();
    private List<History> histories = new ArrayList<>();
    private List<NoteData> noteDatas = new ArrayList<>();


    public StateDiagramImporter(StateDiagram stateDiagram, Map<String, SemanticsData> semanticsMap) {
        super(semanticsMap);
        this.stateDiagram = stateDiagram;
    }

    @Override
    public void extract() {

        for (Entity groupEntity : stateDiagram.groups()) {
            if (groupEntity.getParentContainer().isRoot()) {
                extractGroup(groupEntity, stateDatas, null);
            }
        }

        for (Entity entity : stateDiagram.leafs()) {

            if (entity.getParentContainer().isRoot()) {
                extractLeaf(entity, stateDatas, histories);
            }
        }

        for (Link link : stateDiagram.getLinks()) {
            RelationshipData relationship = extractTransition(link);
            relationshipDatas.add(relationship);
        }
    }

    private void extractGroup(Entity groupEntity, List<StateData> states, StateData state) {
        GroupType groupType = groupEntity.getGroupType();
        if (groupType == GroupType.STATE) {

            List<StateData> subStates = new ArrayList<>();
            List<History> historyList = new ArrayList<>();

            StringBuilder name = new StringBuilder(groupEntity.getName());
            name.append(" \n").append(removeBrackets(groupEntity.getBodier().getRawBody().toString()));

            StateData stateData = new StateData(name.toString());
            stateData.setUid(groupEntity.getUid());
            stateData.setEnd(false);
            stateData.setStart(false);

            for (Entity subState : groupEntity.leafs()) {
                extractLeaf(subState, subStates, historyList);
            }

            for (Entity subGroup : groupEntity.groups()) {
                extractGroup(subGroup, subStates, stateData);
            }

            StateData.StateRegion region = new StateData.StateRegion();
            region.setSubStates(subStates);
            region.setHistories(historyList);
            stateData.getRegions().add(region);

            String key = name + "|State";
            boolean hasSemantics = getSemanticsMap().containsKey(key);

            if (hasSemantics) stateData.setSemantics(getSemanticsMap().get(key));
            states.add(stateData);
        } else if (groupType == GroupType.CONCURRENT_STATE) {
            // we are inside a state, a subgroup of it.
            // new region
            List<StateData> subStates = new ArrayList<>();
            List<History> historyList = new ArrayList<>();

            StateData.StateRegion region = new StateData.StateRegion();

            for (Entity subState : groupEntity.leafs()) {
                extractLeaf(subState, subStates, historyList);
            }

            for (Entity subGroup : groupEntity.groups()) {
                extractGroup(subGroup, subStates, state);
            }

            region.setSubStates(subStates);
            state.getRegions().add(region);
        }
    }

    private RelationshipData extractTransition(Link link) {
        String sourceID = link.getEntity1().getUid();
        String targetID = link.getEntity2().getUid();
        String relationshipType = "Transition";
        if (link.getEntity1().getLeafType() == LeafType.NOTE || link.getEntity2().getLeafType() == LeafType.NOTE) relationshipType = "Anchor";
        RelationshipData relationshipData = new RelationshipData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()));
        relationshipData.setSourceID(sourceID);
        relationshipData.setTargetID(targetID);
        return relationshipData;
    }

    private void extractLeaf(Entity entity, List<StateData> states, List<History> historyList) {
        LeafType leafType = entity.getLeafType();


        switch (leafType) {
            case STATE:
                states.add(extractState(entity, false, false));
                break;
            case CIRCLE_START:
                states.add(extractState(entity, true, false));
                break;
            case CIRCLE_END:
                states.add(extractState(entity, false, true));
                break;
            case STATE_FORK_JOIN:
                forkJoins.add(extractForkJoin(entity));
                break;
            case DEEP_HISTORY:
                historyList.add(extractHistory(entity, true));
                break;
            case PSEUDO_STATE:
                historyList.add(extractHistory(entity, false));
                break;
            case STATE_CHOICE:
                stateChoices.add(extractChoice(entity));
                break;
            case NOTE:
                noteDatas.add(extractNote(entity));
                break;
            default:
                ApplicationManager.instance().getViewManager().showMessage("Warning: A leaf was not imported due to unsupported LeafType: " + leafType);
                addWarning("A leaf was not imported due to unsupported LeafType: " + leafType);


        }

    }

    private History extractHistory(Entity entity, boolean deep) {
        StringBuilder name = new StringBuilder(entity.getName());
        name.append(" \n").append(removeBrackets(entity.getBodier().getRawBody().toString()));

        History history = new History(name.toString());
        history.setUid(entity.getUid());
        history.setDeep(deep);
        return history;
    }

    private StateChoice extractChoice(Entity entity) {
        String name = removeBrackets(entity.getDisplay().toString());
        StateChoice stateChoice = new StateChoice(name);
        stateChoice.setUid(entity.getUid());
        return stateChoice;
    }

    private ForkJoin extractForkJoin(Entity entity) {
        ForkJoin forkJoin = new ForkJoin(entity.getName());
        forkJoin.setUid(entity.getUid());
        List<String> stereo = extractStereotypes(entity, forkJoin);
        if (stereo.contains("fork")) forkJoin.setFork(true);
        return forkJoin;
    }

    private StateData extractState(Entity entity, boolean isStart, boolean isEnd) {

        // String name = removeBrackets(entity.getDisplay().toString());
        StringBuilder name = new StringBuilder(entity.getName());
        name.append(" \n").append(removeBrackets(entity.getBodier().getRawBody().toString()));

        StateData stateData = new StateData(name.toString());
        stateData.setUid(entity.getUid());
        stateData.setEnd(isEnd);
        stateData.setStart(isStart);

        String key = name + "|State";
        boolean hasSemantics = getSemanticsMap().containsKey(key);

        if (hasSemantics) stateData.setSemantics(getSemanticsMap().get(key));
        return stateData;
    }

    public List<ForkJoin> getForkJoins() {
        return forkJoins;
    }

    public List<StateData> getStateDatas() {
        return stateDatas;
    }

    public List<StateChoice> getStateChoices() {
        return stateChoices;
    }

    public List<RelationshipData> getRelationshipDatas() {
        return relationshipDatas;
    }

    public List<History> getHistories() {
        return histories;
    }

    public List<NoteData> getNoteDatas() {
        return noteDatas;
    }
}

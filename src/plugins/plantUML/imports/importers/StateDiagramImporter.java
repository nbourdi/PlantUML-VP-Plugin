package plugins.plantUML.imports.importers;

import cb.petal.Diagram;
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


    public StateDiagramImporter(StateDiagram stateDiagram, Map<String, SemanticsData> semanticsMap) {
        super(semanticsMap);
        this.stateDiagram = stateDiagram;
    }

    @Override
    public void extract() {

        for (Entity groupEntity : stateDiagram.groups()) {
            if (groupEntity.getParentContainer().isRoot()) {
                extractGroup(groupEntity);
            }
        }

        for (Entity entity : stateDiagram.leafs()) {

            if (entity.getParentContainer().isRoot()) {
                extractLeaf(entity);
            }
        }

        for (Link link : stateDiagram.getLinks()) {
            RelationshipData relationship = extractTransition(link);
            relationshipDatas.add(relationship);
        }
    }

    private void extractGroup(Entity groupEntity) {
        GroupType groupType = groupEntity.getGroupType();
        if (groupType == GroupType.STATE) {

            for (Entity subState : groupEntity.leafs()) {
                extractLeaf(subState);
            }

            for (Entity subGroup : groupEntity.groups()) {
                extractGroup(subGroup);
            }
            StringBuilder name = new StringBuilder(groupEntity.getName());
            name.append(" \n").append(removeBrackets(groupEntity.getBodier().getRawBody().toString()));

            StateData stateData = new StateData(name.toString());
            stateData.setUid(groupEntity.getUid());
            stateData.setEnd(false);
            stateData.setStart(false);

            String key = name + "|State";
            boolean hasSemantics = getSemanticsMap().containsKey(key);

            if (hasSemantics) stateData.setSemantics(getSemanticsMap().get(key));
            if (groupEntity.getParentContainer().isRoot()) {
                stateDatas.add(stateData);
            }
        }
    }

    private RelationshipData extractTransition(Link link) {
        String sourceID = link.getEntity1().getUid();
        String targetID = link.getEntity2().getUid();
        String relationshipType = "Transition";
        RelationshipData relationshipData = new RelationshipData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()));
        relationshipData.setSourceID(sourceID);
        relationshipData.setTargetID(targetID);
        return relationshipData;
    }

    private void extractLeaf(Entity entity) {
        LeafType leafType = entity.getLeafType();


        switch (leafType) {
            case STATE:
                stateDatas.add(extractState(entity, false, false));
                break;
            case CIRCLE_START:
                stateDatas.add(extractState(entity, true, false));
                break;
            case CIRCLE_END:
                stateDatas.add(extractState(entity, false, true));
                break;
            case STATE_FORK_JOIN:
                forkJoins.add(extractForkJoin(entity));
                break;
            case DEEP_HISTORY:
                break;
            case STATE_CHOICE:
                stateChoices.add(extractChoice(entity));
                break;
            default:
                ApplicationManager.instance().getViewManager().showMessage("NOT EXTRACTED LEAF TYPE : " + leafType);

        }

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
}

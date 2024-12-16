package plugins.plantUML.imports.importers;

import com.vp.plugin.ApplicationManager;
import net.sourceforge.plantuml.sequencediagram.*;
import plugins.plantUML.models.ActorData;
import plugins.plantUML.models.LifelineData;
import plugins.plantUML.models.MessageData;
import plugins.plantUML.models.SemanticsData;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceDiagramImporter extends  DiagramImporter {

    private SequenceDiagram sequenceDiagram;
    private List<ActorData> actorDatas = new ArrayList<>();
    private List<LifelineData> lifelineDatas = new ArrayList<>();
    private List<MessageData> messageDatas = new ArrayList<>();

    public SequenceDiagramImporter(SequenceDiagram sequenceDiagram, Map<String, SemanticsData> semanticsMap) {
        super(semanticsMap);
        this.sequenceDiagram = sequenceDiagram;
    }


    @Override
    public void extract() {

        // Lifelines, Interaction Actors
        for (Participant participant : sequenceDiagram.participants()) {
            extractParticipant(participant);
        }

        // Message, MessageExo
        for (Event event : sequenceDiagram.events()) {
            extractEvent(event);
        }

        //sequenceDiagram.get
    }

    private void extractEvent(Event event) {
        if (event instanceof Message) {
            // classic message with 2 participants

        } else if (event instanceof MessageExo) {

        }
    }

    private void extractParticipant(Participant participant) {
        // Extract name and type of participant
        String name = participant.getCode();
        ParticipantType participantType = participant.getType();

        if (participantType == ParticipantType.ACTOR) {
            ActorData actorData = new ActorData(name, null);
            String key = name + "|InteractionActor"; // TODO check if accurate type

            if (getSemanticsMap().containsKey(key)) {
                actorData.setSemantics(getSemanticsMap().get(key));
            }

            List<String> stereotypes = extractStereotypes(participant, actorData);
            stereotypes.forEach(actorData::addStereotype);

            actorDatas.add(actorData);
        } else {
            LifelineData lifelineData = new LifelineData(name, null);
            String key = name + "|InteractionLifeline"; // TODO check if accurate type

            if (getSemanticsMap().containsKey(key)) {
                lifelineData.setSemantics(getSemanticsMap().get(key));
            }

            List<String> stereotypes = extractStereotypes(participant, lifelineData);
            stereotypes.forEach(lifelineData::addStereotype);

            switch (participantType) {
                case BOUNDARY:
                    lifelineData.addStereotype("boundary");
                    break;
                case CONTROL:
                    lifelineData.addStereotype("control");
                    break;
                case ENTITY:
                    lifelineData.addStereotype("entity");
                    break;
                case COLLECTIONS:
                    lifelineData.addStereotype("collections");
                    break;
                case QUEUE:
                    lifelineData.addStereotype("queue");
                    break;
                case DATABASE:
                    lifelineData.addStereotype("database");
                    break;
                default:
                    break;
            }

            lifelineDatas.add(lifelineData);
        }
    }


    public List<LifelineData> getLifelineDatas() {
        return lifelineDatas;
    }

    public List<MessageData> getMessageDatas() {
        return messageDatas;
    }

    public List<ActorData> getActorDatas() {
        return actorDatas;
    }
}

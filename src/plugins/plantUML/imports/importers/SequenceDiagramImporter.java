package plugins.plantUML.imports.importers;

import com.vp.plugin.ApplicationManager;
import net.sourceforge.plantuml.sequencediagram.*;
import net.sourceforge.plantuml.sequencediagram.Reference;
import plugins.plantUML.models.*;

import java.util.*;

public class SequenceDiagramImporter extends  DiagramImporter {

    private SequenceDiagram sequenceDiagram;
    private List<ActorData> actorDatas = new ArrayList<>();
    private List<LifelineData> lifelineDatas = new ArrayList<>();
    private List<MessageData> messageDatas = new ArrayList<>();
    private List<SequenceRef> refDatas = new ArrayList<>();
    private List<NoteData> noteDatas = new ArrayList<>();

    private Deque<CombinedFragment> fragmentStack = new ArrayDeque<>();
    private List<CombinedFragment> combinedFragments = new ArrayList<>();

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

        for (Event event : sequenceDiagram.events()) {
            extractEvent(event);
        }

    }

    private void extractEvent(Event event) {
        if (event instanceof AbstractMessage) { // Message, MessageExo
            extractMessage((AbstractMessage) event);
        } else if (event instanceof Grouping) { // fragments
            extractGrouping((Grouping) event);
        } else if (event instanceof LifeEvent) { // activate, deactivate, create
            ApplicationManager.instance().getViewManager().showMessage("LifeEvent");
        } else if (event instanceof Note) {
            extractNoteEvent((Note) event); // In Sequence notes are events, can't use the entity method like with the other diagrams.
        } else if (event instanceof Reference) { // ref over
            SequenceRef ref = new SequenceRef();
            List<Participant> participants = ((Reference) event).getParticipant();
            for (Participant participant : participants) {
                ref.getParticipantCodes().add(participant.getCode());
            }
            String label = removeBrackets(((Reference) event).getStrings().toString());
            ref.setLabel(label);
            refDatas.add(ref);
        }
    }

    private void extractNoteEvent(Note note) {
        ApplicationManager.instance().getViewManager().showMessage("extracting event note display, part, part2  " + note.getDisplay() + " , " + note.getParticipant().getCode() + " , " );

        String noteContent = removeBrackets(note.getDisplay().toString()) ;
        NoteData noteData = new NoteData("note", noteContent, null);
        if (note.getParticipant() != null) {
            noteData.getParticipants().add(note.getParticipant().getCode());
        }
        if (note.getParticipant2() != null) {
            noteData.getParticipants().add(note.getParticipant2().getCode());
        }
        noteDatas.add(noteData);
    }

    private void extractGrouping(Grouping grouping) {
        switch (grouping.getType()) {
            case START: {
                CombinedFragment fragment = new CombinedFragment(grouping.getTitle());
                if (!fragmentStack.isEmpty()) {
                    // nested fragment
                    fragmentStack.peek().getNestedCombinedFragments().add(fragment);
                } else {
                    // level 0 fragment
                    combinedFragments.add(fragment);
                }
                fragmentStack.push(fragment);
                break;
            }
            case ELSE: {
                // new Operand
                if (!fragmentStack.isEmpty()) {
                    CombinedFragment fragment = fragmentStack.peek();
                    CombinedFragment.Operand operand = new CombinedFragment.Operand();
                    operand.setLabel(grouping.getTitle());
                    fragment.getOperands().add(operand);
                }
                break;
            }
            case END: {
                if (!fragmentStack.isEmpty()) {
                    fragmentStack.pop();
                }
                break;
            }
            default:
                break;
        }
    }

    private void extractMessage(AbstractMessage message) {

        MessageData messageData = null;
        String source;
        String target;
        String relationshipType = "Message";
        String labelWithoutNumbering = removeBrackets(message.getLabel().toString()) ;
        String sequenceNumber = message.getMessageNumber();
        source = message.getParticipant1().getCode();
        target = message.getParticipant2().getCode();

        messageData = new MessageData(source, target, relationshipType, labelWithoutNumbering);
        messageData.setSourceID(source);
        messageData.setTargetID(target);

        if (message instanceof Message) {
            messageData.setSequenceNumber(sequenceNumber);

            if (message.getArrowConfiguration().isDotted()) messageData.setReply(true);
            if (message.isCreate()) messageData.setCreate(true);
            if (message.isDestroy() || message.getArrowConfiguration().getDressing2().name().equals("CROSSX")) {
                messageData.setDestroy(true);
            }
            if (message.getArrowConfiguration().getInclination2() != 0) {
                messageData.setDuration(message.getArrowConfiguration().getInclination2());
            }
        } else if (message instanceof  MessageExo) {

            switch (((MessageExo) message).getType()) { // Todo
                case TO_LEFT:
                case TO_RIGHT:
                    ApplicationManager.instance().getViewManager().showMessage("LOST MESSAGE FOUND");
                    messageData.setLost(true);
                    break;
                case FROM_LEFT:
                case FROM_RIGHT:

                    ApplicationManager.instance().getViewManager().showMessage("FOUND MESSAGE FOUND");
                    messageData.setFound(true);
                    break;
            }
        }


        if (!fragmentStack.isEmpty() && !fragmentStack.peek().getOperands().isEmpty()) {
            CombinedFragment fragment = fragmentStack.peek();
            CombinedFragment.Operand currentOperand = fragment.getOperands().get(fragment.getOperands().size() - 1);
            currentOperand.getMessages().add(messageData);

        }
        else {
            messageDatas.add(messageData);
        }
    }

    private void extractParticipant(Participant participant) {
        // Extract name and type of participant
        String alias = participant.getCode();
        String name = removeBrackets(participant.getDisplay(false).toString());
        ParticipantType participantType = participant.getType();

        if (participantType == ParticipantType.ACTOR) {
            ActorData actorData = new ActorData(name);
            actorData.setUid(alias);
            String key = name + "|InteractionActor";

            if (getSemanticsMap().containsKey(key)) {
                actorData.setSemantics(getSemanticsMap().get(key));
            }

            List<String> stereotypes = extractStereotypes(participant, actorData);
            stereotypes.forEach(actorData::addStereotype);

            actorDatas.add(actorData);
        } else {
            LifelineData lifelineData = new LifelineData(name);
            lifelineData.setAlias(alias);
            String key = name + "|InteractionLifeLine";
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

    public List<SequenceRef> getRefDatas() {
        return refDatas;
    }

    public List<CombinedFragment> getCombinedFragments() {
        return combinedFragments;
    }

    public List<NoteData> getNoteDatas() {
        return noteDatas;
    }
}

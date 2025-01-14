package plugins.plantUML.export.writers;

import plugins.plantUML.models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StateUMLWriter extends PlantUMLWriter {

    private List<StateData> states;
    private List<RelationshipData> transitions;
    private List<StateChoice> choices;
    private List<History> histories;
    private List<ForkJoin> forkJoins;

    public StateUMLWriter(List<NoteData> notes, List<StateData> states, List<RelationshipData> transitions, List<StateChoice> choices, List<History> histories, List<ForkJoin> forkJoins) {
        super(notes);
        this.states = states;
        this.choices = choices;
        this.histories = histories;
        this.transitions = transitions;
        this.forkJoins = forkJoins;
    }

    @Override
    public void writeToFile(File file) throws IOException {

        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        for (StateData stateData : states) {
            if(!stateData.isInState())
                plantUMLContent.append(writeState(stateData, ""));
        }

        for (StateChoice stateChoice : choices) {
            if(!stateChoice.isInState()) {
                plantUMLContent.append(writeChoice(stateChoice, ""));
            }
        }

        for (History history : histories) {
            if (!history.isInState()) {
                plantUMLContent.append(writeHistory(history, ""));
            }
        }

        for (ForkJoin forkJoin : forkJoins) {
            plantUMLContent.append(writeState(forkJoin, ""));
        }

        plantUMLContent.append(writeNotes());

        for (RelationshipData transition : transitions) {
            plantUMLContent.append(writeRelationship(transition));
        }

        plantUMLContent.append("@enduml");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private String writeHistory(History history, String indent) {
        StringBuilder historyString = new StringBuilder();
        String stereo = (history.isDeep() ? "<<history*>>" : "<<history>>");
        historyString.append(indent).append("state ").append(history.getAlias()).append( " ").append(stereo).append("\n");

        return historyString.toString();
    }

    private String writeChoice(StateChoice stateChoice, String indent) {

        StringBuilder choiceString = new StringBuilder();
        choiceString.append(indent).append("state ").append(stateChoice.getAlias()).append(" <<choice>>\n");

        return choiceString.toString();
    }

    private String writeRelationship(RelationshipData transition) {
        return transition.toExportFormat();
    }

    private String writeState(StateData stateData, String indent) {
        StringBuilder stateString = new StringBuilder();
        boolean isStart = stateData.isStart();
        boolean isEnd = stateData.isEnd();
        String alias = stateData.getAlias();

        String name = stateData.getName();
        stateString.append(indent).append("state ").append(alias);

        if (isStart) {
            return stateString.append(" <<start>>\n").toString();
        }

        if (isEnd) {
            return stateString.append(" <<end>>\n").toString();
        }

        if (stateData instanceof ForkJoin) {
            stateString.append(((ForkJoin) stateData).isFork() ? " <<fork>>\n" : " <<join>>\n");
            return stateString.toString();
        }


        if (name != null) {
            // Escape newlines
            name = name.replace("\n", "\\n");
        }


        if (!stateData.getRegions().isEmpty()) {
            stateString.append( " {\n");

            List<StateData.StateRegion> regions = stateData.getRegions();
            for (int i = 0; i < regions.size(); i++) {
                StateData.StateRegion region = regions.get(i);
                for (StateData stateInRegion : region.getSubStates()) {
                    if (stateInRegion instanceof History) {
                        stateString.append(writeHistory((History) stateInRegion, indent + "\t"));
                    } else {
                        stateString.append(writeState(stateInRegion, indent + "\t"));
                    }
                }
                // concurrent if there's more than one region
                if (i < regions.size() - 1) {
                    stateString.append(indent).append("\t").append("--\n");
                }
            }

            stateString.append("}\n");
            stateString.append(indent).append("state ").append(alias);
        }
        stateString.append(" : ").append(name);

        stateString.append("\n");

        return  stateString.toString();

    }
}

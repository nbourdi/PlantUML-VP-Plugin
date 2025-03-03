package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class SequenceRef {
    private String label;
    private List<String> participantCodes;


    public SequenceRef() {
        this.participantCodes = new ArrayList<>();
    }

    public List<String> getParticipantCodes() {
        return participantCodes;
    }

    public void setParticipantCodes(List<String> participantCodes) {
        this.participantCodes = participantCodes;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

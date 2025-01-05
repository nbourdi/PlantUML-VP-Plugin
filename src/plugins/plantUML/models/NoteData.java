package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class NoteData {
    private String name;
    private String content;
    private String id;
    private String alias;
    private String Uid;
    private List<String> participants; // for seq

    public NoteData(String name, String content, String id) {
        this.name = (name != null && !name.isEmpty()) ? name : null;
        this.content = content;
        this.id = id;
        this.alias = generateAlias();
        this.participants = new ArrayList<>();
    }

    private String generateAlias() {
        if (this.name == null) {
            return "note_" + id.replaceAll("[^a-zA-Z0-9]", "_");
        }
        return name;
    }

    public String getName() {
        return name != null ? name : alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}

package plugins.plantUML.export.models;

public class NoteData {
    private String name;
    private String content;
    private String id;
    private String alias;

    public NoteData(String name, String content, String id) {
        this.name = (name != null && !name.isEmpty()) ? name : null;
        this.content = content;
        this.id = id;
        this.alias = generateAlias();
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
}

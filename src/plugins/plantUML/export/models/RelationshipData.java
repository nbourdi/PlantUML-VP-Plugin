package plugins.plantUML.export.models;

public class RelationshipData {
    private String source;
    private String target;
    private String type;

    public RelationshipData(String source, String target, String type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getType() {
        return type;
    }
}

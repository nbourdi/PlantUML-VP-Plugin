package plugins.plantUML.models;

public class NaryData {
    private String name;
    private String id;
    private boolean isInPackage;
    private String alias;
    
    public NaryData(String name, String id, boolean isInPackage) {
        this.name = (name != null && !name.isEmpty()) ? name : null;
        this.id = id;
        this.isInPackage = isInPackage;
        this.alias = generateAlias();
    }
    
    private String generateAlias() {
        if (this.name == null) {
            return "diamond_" + id.replaceAll("[^a-zA-Z0-9]", "_");
        } 
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isInPackage() {
        return isInPackage;
    }
}

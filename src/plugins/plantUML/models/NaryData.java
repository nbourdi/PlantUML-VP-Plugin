package plugins.plantUML.models;

public class NaryData extends BaseWithSemanticsData {
    private String name;
    private String id;
    private boolean isInPackage;
    private String alias;
    private String Uid;
    
    public NaryData(String name, String description, String id, boolean isInPackage) {
        super(name, description);
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

    public String getAlias() {
        return alias;
    }

    public boolean isInPackage() {
        return isInPackage;
    }

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}
}

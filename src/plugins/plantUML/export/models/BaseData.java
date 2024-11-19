package plugins.plantUML.export.models;

public class BaseData {
	
	private String name;
	private String description;
	// subdiagrams, references
	
	public BaseData(String name, String description) {
		this.setName(name);
		this.setDescription(description);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}

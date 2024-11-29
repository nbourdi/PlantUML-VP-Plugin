package plugins.plantUML.models;

public class BaseWithSemanticsData {
	
	private String name;
	private String description;
	private SemanticsData semantics;
	// maybe even stereotypes?
	
	public BaseWithSemanticsData(String name, String description) {
		this.setName(name);
		this.setDescription(description);
		this.setSemantics(new SemanticsData());
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

	public SemanticsData getSemantics() {
		return semantics;
	}

	public void setSemantics(SemanticsData semantics) {
		this.semantics = semantics;
	}
	
}

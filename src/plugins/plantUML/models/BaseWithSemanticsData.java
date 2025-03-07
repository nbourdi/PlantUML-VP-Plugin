package plugins.plantUML.models;

public class BaseWithSemanticsData {
	
	private String name;
	private String description;
	private SemanticsData semantics;
	
	public BaseWithSemanticsData(String name) {
		this.setName(name);
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

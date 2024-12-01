package plugins.plantUML.models;

public class SubDiagramData {
	private String name;
	private String type;
	
	public SubDiagramData(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public SubDiagramData() {} // required for jackson json parsing
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}

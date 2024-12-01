package plugins.plantUML.models;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Reference {
	
	public Reference() {} // required for jackson json parsing
 
	private String type; // diagram, url, file, folder, shape, model
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description; // description of reference
	private String name; // file/folder name, diagram title, url link, model name
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String diagramType; // type of diagram, relevant when type is "diagram"

	public Reference(String type, String description, String name, String diagramType) {
		setType(type);
		setDescription(description);
		setName(name);
		setDiagramType(diagramType);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDiagramType() {
		return diagramType;
	}

	public void setDiagramType(String diagramType) {
		if ("diagram".equals(this.type)) {
			this.diagramType = diagramType;
		} else {
			this.diagramType = null;
		}
	}
}

package plugins.plantUML.models;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.teamdev.jxbrowser.deps.org.checkerframework.common.returnsreceiver.qual.This;

public class Reference {
	
	public Reference() {} // required for jackson json parsing
 
	private String type; // diagram, url, file, folder, shape, model
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description; // description of reference
	private String name; // file/folder name, diagram title, url link, model name
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String diagramType; // type of diagram, relevant when type is "diagram"
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String modelType; // type of model element, relevant when type is "model_element"

	public Reference(String type, String description, String name, String diagramType, String modelType) {
		setType(type);
		setDescription(description);
		setName(name);
		setDiagramType(diagramType);
		setModelType(modelType);
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
	
	public void setModelType(String modelType) {
		if("model_element".equals(this.type)) {
			this.modelType = modelType;
		} else {
			this.modelType = null;
		}
		
	}

	public String getModelType() {
		return modelType;
	}
}

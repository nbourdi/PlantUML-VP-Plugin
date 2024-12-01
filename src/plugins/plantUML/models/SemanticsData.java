package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY) // Annotation to omit empty or null fields when constructing the json
public class SemanticsData {
	
	private String ownerName; // owner element name
	private String ownerType;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<Reference> references = new ArrayList<Reference>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<SubDiagramData> subDiagrams = new ArrayList<SubDiagramData>();
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description;
	
	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SubDiagramData> getSubDiagrams() {
		return subDiagrams;
	}

	public void setSubDiagrams(List<SubDiagramData> subDiagrams) {
		this.subDiagrams = subDiagrams;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
}

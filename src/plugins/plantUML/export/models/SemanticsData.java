package plugins.plantUML.export.models;

import java.util.List;

public class SemanticsData {
	private String ownerName;
	private List<Reference> references;
	private List<SubDiagramData> subDiagrams;
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
}

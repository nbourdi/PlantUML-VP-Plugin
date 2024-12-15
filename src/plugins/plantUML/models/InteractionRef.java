package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class InteractionRef {
	private List<String> coveredLifelines = new ArrayList<>();
	private String refName;
	
	public InteractionRef(String referenceName) {
		this.setRefName(referenceName);
	}

	public List<String> getCoveredLifelines() {
		return coveredLifelines;
	}

	public void setCoveredLifelines(List<String> coveredLifelines) {
		this.coveredLifelines = coveredLifelines;
	}

	public String getRefName() {
		return refName;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}
}

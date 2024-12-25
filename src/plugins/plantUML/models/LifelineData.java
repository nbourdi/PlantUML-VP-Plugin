package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class LifelineData extends BaseWithSemanticsData {

	private List<String> stereotypes = new ArrayList<String>();
	private boolean isInPackage;
	private boolean isCreatedByMessage;

	public LifelineData(String name) {
		super(name);
	}

	public List<String> getStereotypes() {
		return stereotypes;
	}

	public void setStereotypes(List<String> stereotypes) {
		this.stereotypes = stereotypes;
	}

	public void addStereotype(String stereotype) {
		getStereotypes().add(stereotype);
	}

	public boolean isInPackage() {
		return isInPackage;
	}

	public void setInPackage(boolean isInPackage) {
		this.isInPackage = isInPackage;
	}

	public boolean isCreatedByMessage() {
		return isCreatedByMessage;
	}

	public void setCreatedByMessage(boolean isCreatedByMessage) {
		this.isCreatedByMessage = isCreatedByMessage;
	}

}

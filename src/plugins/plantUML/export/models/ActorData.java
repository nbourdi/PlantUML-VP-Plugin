package plugins.plantUML.export.models;

import java.util.ArrayList;
import java.util.List;

public class ActorData {
	private String name;
	private List<String> stereotypes = new ArrayList<String>();
	private boolean isInPackage;
	private boolean isBusiness;
	
	public ActorData(String name, List<String> stereotypes) {
		this.setName(name);
		this.stereotypes = stereotypes == null ? new ArrayList<String>() : stereotypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean isBusiness() {
		return isBusiness;
	}

	public void setBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
	}


}

package plugins.plantUML.export.models;

import java.util.List;
import java.util.jar.Attributes;

import org.glassfish.grizzly.utils.StringFilter;

public class UseCaseData {
	private String name;
	private String description;
	private List<String> stereotypes;
	private boolean isInPackage;
	
	
	public UseCaseData(String name) {
		this.name = name;
		this.description = description;
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
}

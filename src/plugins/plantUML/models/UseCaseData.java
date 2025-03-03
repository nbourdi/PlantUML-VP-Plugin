package plugins.plantUML.models;

import java.util.List;
import java.util.jar.Attributes;

import org.glassfish.grizzly.utils.StringFilter;

public class UseCaseData extends BaseWithSemanticsData {
	private String name;
	private String description;
	private List<String> stereotypes;
	private boolean isInPackage;
	private boolean isBusiness;
	private String Uid;
	
	
	public UseCaseData(String name) {
        super(name);
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
	public boolean isBusiness() {
		return isBusiness;
	}
	public void setBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
	}

    public String getUid() {
		return  Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}

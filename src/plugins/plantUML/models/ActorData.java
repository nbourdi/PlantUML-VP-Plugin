package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class ActorData extends BaseWithSemanticsData {
	private List<String> stereotypes;
	private boolean isInPackage;
	private boolean isBusiness;
	private String Uid;
	
	public ActorData(String name) {
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

	public boolean isBusiness() {
		return isBusiness;
	}

	public void setBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
	}


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}

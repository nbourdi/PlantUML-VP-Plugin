package plugins.plantUML.export.models;

import java.util.ArrayList;
import java.util.List;

import com.teamdev.jxbrowser.deps.org.checkerframework.checker.units.qual.m;

public class ClassData {
    private String name;
    // private String visibility;
    private boolean isAbstract;
    private List<AttributeData> attributes;
    private List<OperationData> operations;
    private List<String> stereotypes;

    public ClassData(String name, boolean isAbstract) {
        this.setName(name);
        this.setAbstract(isAbstract);
        // this.visibility = "public"; // TODO : is there a way to represent private/protected in puml? NO
        this.setAttributes(new ArrayList<>());
        this.setOperations(new ArrayList<>());
        this.stereotypes = new ArrayList<>();
    }

    public void addAttribute(AttributeData attribute) {
        getAttributes().add(attribute);
    }

    public void addOperation(OperationData operation) {
        getOperations().add(operation);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AttributeData> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeData> attributes) {
		this.attributes = attributes;
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

	public List<OperationData> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationData> operations) {
		this.operations = operations;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}


//	public String getVisibility() {
//		return visibility;
//	}
//
//	public void setVisibility(String visibility) {
//		this.visibility = visibility;
//	}

}




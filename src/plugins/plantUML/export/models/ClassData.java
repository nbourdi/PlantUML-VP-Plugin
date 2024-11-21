package plugins.plantUML.export.models;

import java.util.ArrayList;
import java.util.List;

import com.teamdev.jxbrowser.deps.org.checkerframework.checker.units.qual.m;

public class ClassData extends BaseData {
    private boolean isAbstract;
    private List<AttributeData> attributes;
    private List<OperationData> operations;
    private List<String> stereotypes;
    private boolean isInPackage;
    private String visibility;
    // private List<SubDiagramData> subDiagrams;
    private SemanticsData semantics = new SemanticsData();

    public ClassData(String name, boolean isAbstract, String visibility, boolean isInPackage, String description) {
    	super(name, description);
        this.setAbstract(isAbstract);
        this.visibility = visibility; 
        this.setAttributes(new ArrayList<>());
        this.setOperations(new ArrayList<>());
        this.stereotypes = new ArrayList<>();
        this.setInPackage(isInPackage);
        // semantics.setOwnerName(name);
        // prob need new semantics
        // this.setSubDiagrams(new ArrayList<>());
    }

    public void addAttribute(AttributeData attribute) {
        getAttributes().add(attribute);
    }

    public void addOperation(OperationData operation) {
        getOperations().add(operation);
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

	public boolean isInPackage() {
		return isInPackage;
	}

	public void setInPackage(boolean isInPackage) {
		this.isInPackage = isInPackage;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public SemanticsData getSemantics() {
		return semantics;
	}

	public void setSemantics(SemanticsData semantics) {
		this.semantics = semantics;
	}



}




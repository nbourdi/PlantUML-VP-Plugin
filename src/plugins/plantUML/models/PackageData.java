package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class PackageData extends BaseWithSemanticsData {
    private List<ClassData> classes = new ArrayList<>(); 
    private List<PackageData> subPackages = new ArrayList<>(); // Nested packages if any
    private List<NaryData> naries = new ArrayList<>();
    private List<ActorData> actors = new ArrayList<>();
    private List<UseCaseData> usecases = new ArrayList<>();
    private List<ComponentData> components = new ArrayList<ComponentData>();
	private List<ArtifactData> artifacts = new ArrayList<>();
    private boolean isSubpackage;
    private boolean isRectangle = false; // is System in reality, but systems are not a different type in puml , just a rectangle shape
    private String Uid;

    public PackageData(String packageName, boolean isSubpackage) {
    	super(packageName);
    	this.isSubpackage = isSubpackage;
    	
    }
    public PackageData(String packageName, List<ClassData> classes, List<PackageData> subPackages, List<NaryData> naries, boolean isSubpackage, boolean isRectangle) {
        super(packageName);
        this.classes = classes != null ? classes : new ArrayList<>();
        this.subPackages = subPackages != null ? subPackages : new ArrayList<>();
        this.setNaries(naries != null ? naries : new ArrayList<>());
        this.setSubpackage(isSubpackage);
        this.usecases = usecases != null ? usecases : new ArrayList<>();
        this.actors = actors != null ? actors : new ArrayList<>();
        this.classes = classes != null ? classes : new ArrayList<>();
        this.isRectangle = isRectangle;
    }

    
    public List<ClassData> getClasses() {
        return classes;
    }

    public List<PackageData> getSubPackages() {
        return subPackages;
    }

	public boolean isSubpackage() {
		return isSubpackage;
	}

	public void setSubpackage(boolean isSubpackage) {
		this.isSubpackage = isSubpackage;
	}

	public List<NaryData> getNaries() {
		return naries;
	}

	public void setNaries(List<NaryData> naries) {
		this.naries = naries;
	}

	public List<ActorData> getActors() {
		return actors;
	}

	public void setActors(List<ActorData> actors) {
		this.actors = actors;
	}

	public List<UseCaseData> getUseCases() {
		return usecases;
	}

	public boolean isRectangle() {
		return this.isRectangle;
	}

	public void setRectangle(boolean rectangle) {
		isRectangle = rectangle;
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}
	
	public List<ComponentData> getComponents() {
		return components;
	}
	public void setComponents(List<ComponentData> components) {
		this.components = components;
	}

	public List<ArtifactData> getArtifacts() {
		return artifacts;
	}
}

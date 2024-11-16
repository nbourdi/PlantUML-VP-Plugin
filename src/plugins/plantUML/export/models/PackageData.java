package plugins.plantUML.export.models;

import java.util.ArrayList;
import java.util.List;

public class PackageData {
    private String packageName;
    private List<ClassData> classes; 
    private List<PackageData> subPackages; // Nested packages if any
    private List<NaryData> naries;
    private List<ActorData> actors;
    private List<UseCaseData> usecases;
    private boolean isSubpackage;
    private boolean isRectangle; // is System in reality, but systems are not a different type in puml , just a rectangle shape

    public PackageData(String packageName, List<ClassData> classes, List<PackageData> subPackages, List<NaryData> naries, boolean isSubpackage, boolean isRectangle) {
        this.packageName = packageName;
        this.classes = classes != null ? classes : new ArrayList<>();
        this.subPackages = subPackages != null ? subPackages : new ArrayList<>();
        this.setNaries(naries != null ? naries : new ArrayList<>());
        this.setSubpackage(isSubpackage);
        this.usecases = usecases != null ? usecases : new ArrayList<>();
        this.actors = actors != null ? actors : new ArrayList<>();
        this.classes = classes != null ? classes : new ArrayList<>();
        this.isRectangle = isRectangle;
    }

    public String getPackageName() {
        return packageName;
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


}

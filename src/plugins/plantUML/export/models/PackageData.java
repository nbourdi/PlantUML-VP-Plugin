package plugins.plantUML.export.models;

import java.util.ArrayList;
import java.util.List;

public class PackageData {
    private String packageName;
    private List<ClassData> classes; 
    private List<PackageData> subPackages; // Nested packages if any
    private List<NaryData> naries;
    private boolean isSubpackage;

    public PackageData(String packageName, List<ClassData> classes, List<PackageData> subPackages, List<NaryData> naries, boolean isSubpackage) {
        this.packageName = packageName;
        this.classes = classes != null ? classes : new ArrayList<>();
        this.subPackages = subPackages != null ? subPackages : new ArrayList<>();
        this.naries = naries != null ? naries : new ArrayList<>();
        this.setSubpackage(isSubpackage);
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
}

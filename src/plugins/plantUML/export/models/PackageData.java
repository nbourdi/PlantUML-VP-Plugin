package plugins.plantUML.export.models;

import java.util.List;

public class PackageData {
    private String packageName;
    private List<ClassData> classes; 
    private List<PackageData> subPackages; // Nested packages if any

    public PackageData(String packageName, List<ClassData> classes, List<PackageData> subPackages) {
        this.packageName = packageName;
        this.classes = classes;
        this.subPackages = subPackages;
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
}

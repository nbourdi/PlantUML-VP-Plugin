package plugins.plantUML.models;

public class ArtifactData extends BaseWithSemanticsData {

    private boolean isInPackage;
    private boolean isInNode;
    private String Uid;


    public ArtifactData(String name, boolean isInPackage, boolean isInNode) {
        super(name);
        this.isInPackage = isInPackage;
        this.isInNode = isInNode;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public boolean isInPackage() {
        return isInPackage;
    }

    public boolean isInNode() {
        return isInNode;
    }

    public void setInPackage(boolean inPackage) {
        isInPackage = inPackage;
    }
}

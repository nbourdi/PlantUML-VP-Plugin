package plugins.plantUML.models;

public class AttributeData {
    private String name;
    private String visibility;
    private String type; // int, bool, etc.
    private String initialValue;
    private boolean isStatic;

    public AttributeData() {} // for json

    public AttributeData(String visibility, String name, String type, String initValue, String scope) {
        this.setName(name);
        this.visibility = visibility;
        this.isStatic = (scope == "classifier") ? true : false;
        
        if (type != "") {
            this.type = type;
            if (initValue != "") {
                this.setInitialValue(initValue);
            }
        } else {
            this.type = null;
            this.setInitialValue(null);
        }
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}

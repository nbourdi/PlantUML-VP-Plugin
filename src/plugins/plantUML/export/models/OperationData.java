package plugins.plantUML.export.models;

import java.util.ArrayList;
import java.util.List;

public class OperationData {

    private String visibility;
    private String name;
    private String returnType;
    private boolean isAbstract;
    private boolean isStatic;
    private List<Parameter> parameters = new ArrayList<>();

	 
	 public OperationData(String visibility, String name, String returnType, boolean isAbstract, List<Parameter> parameters, String scope) {
	     this.setName(name);
	     this.visibility = visibility;
	     this.setReturnType(returnType);
	     this.setAbstract(isAbstract);
	     this.parameters = parameters != null ? parameters : new ArrayList<>();
	     this.isStatic = (scope == "classifier") ? true : false;
	 }


    public String getVisibility() {
        return visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(Parameter parameter) {
    	this.getParameters().add(parameter);
    }

    // Inner class to represent a parameter
    public static class Parameter {
        private String name;
        private String type;
        private String defaultValue;

        public Parameter(String name, String type, String defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
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

        public void setType(String type) {
            this.type = type;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

	public boolean isStatic() {
		return this.isStatic;
	}
}

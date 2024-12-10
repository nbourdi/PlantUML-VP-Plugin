package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class ComponentData extends BaseWithSemanticsData {

	private boolean isInPackage;
	private List<String> stereotypes;
	private String Uid;
	private List<ComponentData> residents;
	private boolean isResident;
	private List<PortData> ports;
	
	public ComponentData(String name, String description, boolean isInPackage) {
		super(name, description);
		this.isInPackage = isInPackage;
		this.stereotypes = new ArrayList<>();
		this.residents = new ArrayList<>();
		this.ports = new ArrayList<>();
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
	
	public String getUid() {
		return Uid;
	}

	public void setUid(String Uid) {
		this.Uid = Uid;
	}

	public boolean isResident() {
		return isResident;
	}

	public void setResident(boolean isResident) {
		this.isResident = isResident;
	}

	public List<ComponentData> getResidents() {
		return residents;
	}

	public void setResidents(List<ComponentData> residents) {
		this.residents = residents;
	}

	public List<PortData> getPorts() {
		return ports;
	}

	public static class PortData {
	    private String name;
	    private String id;
	    private String alias;
	    //private String Uid;
	    
	    public PortData(String name, String id) {
	    	this.name = (name != null && !name.isEmpty()) ? name : null;
	        this.id = id;
	        this.alias = generateAlias();
		}
	    
	    private String generateAlias() {
	        //if (this.name == null) {
	            return "port_" + id.replaceAll("[^a-zA-Z0-9]", "_");
	        //} 
	        // return name;
	    }
	    
	    public String getId() {
	        return id;
	    }

	    public String getAlias() {
	        return alias;
	    }

		public String getName() {
			if (name != null) {
				return name;
			} 
			// unnamed 
			return " ";
		}

//
//		public String getUid() {
//			return Uid;
//		}
//
//		public void setUid(String uid) {
//			Uid = uid;
//		}
	}

}

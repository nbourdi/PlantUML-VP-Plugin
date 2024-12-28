package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class ComponentData extends BaseWithSemanticsData {

	private boolean isInPackage;
	private List<String> stereotypes;
	private String Uid;
	private List<ComponentData> residents;
	private List<ClassData> interfaces;
	private List<PackageData> packages;
	private boolean isResident;
	private List<PortData> ports;
	private boolean isNodeComponent; // components and nodes are basically the same.
	private List<ArtifactData> artifacts;


	public ComponentData(String name, boolean isInPackage) {
		super(name);
		this.isInPackage = isInPackage;
		this.stereotypes = new ArrayList<>();
		this.residents = new ArrayList<>();
		this.interfaces = new ArrayList<>();
		this.packages = new ArrayList<>();
		this.ports = new ArrayList<>();
		this.artifacts = new ArrayList<>();
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

	public List<ArtifactData> getArtifacts() {
		return artifacts;
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

    public boolean isNodeComponent() {
        return isNodeComponent;
    }

    public void setNodeComponent(boolean nodeComponent) {
        isNodeComponent = nodeComponent;
    }

    public static class PortData {
		private String name;
		private String id;
		private String alias;
		private String Uid;

		public PortData(String name) {
			this.name = (name != null && !name.isEmpty()) ? name : null;
			
		}

		private String generateAlias() {
			//if (this.name == null) {
			return "port_" + id.replaceAll("[^a-zA-Z0-9]", "_");
			//} 
			// return name;
		}
		
		public void setId(String id) {
			this.id = id;
			this.alias = generateAlias();
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


		public String getUid() {
			return Uid;
		}

		public void setUid(String uid) {
			Uid = uid;
		}
	}

	public List<ClassData> getInterfaces() {
		return interfaces;
	}
	public List<PackageData> getPackages() {
		return packages;
	}
}

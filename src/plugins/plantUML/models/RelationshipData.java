package plugins.plantUML.models;

public class RelationshipData {
	private String source;
	private String target;
	private String type;
	private String sourceID;
	private String targetID;
	private String name;

	public RelationshipData(String source, String target, String type, String name) {
		this.source = source;
		this.target = target;
		setType(type);
		this.name = name != null ? name : "";
	}

//	protected String formatName(String name) { // TODO: duplicate code..
//		/*
//		 * Spaces and other non-letter characters are not supported as names for
//		 * plantUML
//		 */
//		if (!name.matches("[a-zA-Z0-9]+")) {
//			return "\"" + name + "\"";
//		}
//		return name;
//	}

	protected String formatAlias(String name) {
		return name.replaceAll("[^a-zA-Z0-9\u0370-\u03FF]", "_");
	}



	public String toExportFormat() {
		String symbol = "--";
		String label = "";
		String prefix = "";

		switch (type) {
		case "Generalization":
			symbol = "<|--";
			break;
		case "Realization":
			symbol = "<|..";
			break;
		case "Abstraction":
			symbol = "..>";
			label = "<<abstraction>>  ";
			break;
			case "Permission":
				symbol = "..>";
				label = "<<permit>>  ";
				break;
		case "Usage":
			symbol = "..>";
			label = "<<use>>  ";
			break;
		case "Dependency":
			symbol = "..>";
			break;
		case "Anchor":
			symbol = "..";
			break;
		case "Extend":
			symbol = "<..";
			label = "<<Extend>>  ";
			break;
		case "Include":
			symbol = "..>";
			label = "<<Include>>  ";
			break;
		case "Containment":
			symbol = "+--";
			break;
		case "Transition2":
			symbol = "-->";
			break;
		}

		if (!label.isEmpty() || !name.isEmpty()) {
			prefix = " : ";
		}

		if ("AssociationClass".equals(type)) {
			symbol = "..";
			if (source.contains(",")) {
				target = formatAlias(target);
			} else {
				source = formatAlias(source);
			}
			return source + " " + symbol + " " + target + prefix + name + "\n";
		}

		return formatAlias(source) + " " + symbol + " " + formatAlias(target) + prefix + label + name + "\n";
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getTargetID() {
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}
	

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

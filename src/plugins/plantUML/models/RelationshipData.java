package plugins.plantUML.models;

import java.util.HashMap;
import java.util.Map;

import org.orm.ertodb.DialectDBTypeMapper;

public class RelationshipData {
	private String source;
	private String target;
	private String type;
	private String sourceID;
	private String targetID;
	private String name;
	Map<String, String> typeMap = new HashMap<String, String>();

	public RelationshipData(String source, String target, String type, String name) {
		this.source = source;
		this.target = target;
		setType(type);
		this.name = name != null ? name : "";
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
	protected String formatName(String name) { // TODO: duplicate code..
		/*
		 * Spaces and other non-letter characters are not 
		 * supported as names for plantUML
		 */
		if (!name.matches("[a-zA-Z0-9]+")) {
			return "\"" + name + "\"";
		}
		return name;
	}
	protected String formatAlias(String name) {
		return name.replaceAll("[^a-zA-Z0-9]", "_");
	}
	public String toExportFormat() {

		String symbol = "--";
		String label = "";
		String prefix = "";




		if (type == "Generalization") {
			symbol = "<|--";
		} else if (type == "Realization") {
			symbol = "<|..";
		} else if (type == "Abstraction") {
			symbol = "<..";
			label = "<<abstraction>> \\n ";
		} else if (type == "Usage") {
			symbol = "<..";
			label = "<<use>> \\n ";
		} else if (type == "Dependency") {
			symbol = "..>";
		} else if (type == "Anchor") {
			symbol = "..";
		} else if (type == "Extend") {
			symbol = "<..";
			label = "<<Extend>> \\n ";
		}
		else if (type == "Include") {
			symbol = "..>";
			label = "<<Include>> \\n ";
		} else if (type == "Containment") {
			symbol = "}--";
		}


		if (!label.isEmpty() || !name.isEmpty()) {
			prefix = " : ";
		}

		if (type == "AssociationClass") {
			// TODO
			symbol = "..";
			if (source.contains(",")) {
				target = formatAlias(target);
			} else {
				source = formatAlias(source);
			}
			return source  + " " + symbol + " " + target + prefix + name + "\n";
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
}

package plugins.plantUML.export.models;

public class RelationshipData {
    private String source;
    private String target;
    private String type;
    private String name;

    public RelationshipData(String source, String target, String type, String name) {
        this.source = source;
        this.target = target;
        this.type = type;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toExportFormat() {

		String symbol = "--";
		String label = "";
		String prefix = "";
		
        if (type == "Generalization") {
            symbol = "<|--";
//        } else if (type == "Aggregation") {
//            symbol = "o--";
//        } else if (type == "Composition") {
//            symbol = "*--";
        } else if (type == "Realization") {
        	symbol = "<|..";
        } else if (type == "Abstraction") {
        	symbol = "<..";
        	label = "<<abstraction>> \\n ";
        } else if (type == "Usage") {
        	symbol = "<..";
        	label = "<<use>> \\n ";
        }
        
        if (!label.isEmpty() || !name.isEmpty()) {
        	prefix = " : ";
        }
        
        return source + " " + symbol + " " + target + prefix + label + name + "\n";

	}
}

package plugins.plantUML.models;

public class AssociationData extends RelationshipData {

	private String fromEndMultiplicity;
	private String toEndMultiplicity;
	// private boolean fromEndNavigable; // basically unsupported 
	private boolean toEndNavigable;
	
	private String Uid;
	
	public AssociationData(String source, String target, String type, String name, String fromEndMultiplicity, 
			String toEndMultiplicity, boolean toEndNavigable, String fromEndAggregation) {
		super(source, target, type, name);
		this.fromEndMultiplicity = fromEndMultiplicity == null ? "" : fromEndMultiplicity;
		this.toEndMultiplicity = toEndMultiplicity == null ? "" : toEndMultiplicity;
		// this.fromEndNavigable = fromEndNavigable;
		this.toEndNavigable = toEndNavigable;
		if (fromEndAggregation == "shared") {
			this.setType("Aggregation");
		} else if (fromEndAggregation == "composite") {
			this.setType("Composition");
		} else {
			this.setType("Simple");
		}
	}

	@Override
	public String toExportFormat() {
	    StringBuilder output = new StringBuilder();

	    // Define symbol and navigability markers
	    String symbol = "--";
	    String prefix = (!getName().isEmpty()) ? " : " : "";
	    String fromMultiplicity = (fromEndMultiplicity != null && !fromEndMultiplicity.isEmpty()) ? "\"" + fromEndMultiplicity + "\" " : "";
	    String toMultiplicity = (toEndMultiplicity != null && !toEndMultiplicity.isEmpty()) ? " \"" + toEndMultiplicity + "\"" : "";
	    String toNavig = toEndNavigable ? "" : "x";
	    
	    // Determine the association type symbol
	    String type = getType();
	    if ("Aggregation".equals(type)) {
	        symbol = "o--";
	    } else if ("Composition".equals(type)) {
	        symbol = "*--";
	    }

	    // Construct the export format string with multiplicities and navigability
	    output.append(formatAlias(getSource()))
	          .append(" ")
	          .append(fromMultiplicity)
	          .append(symbol)
	          .append(toNavig)
	          .append(toMultiplicity)
	          .append(" ")
	          .append(formatAlias(getTarget()))
	          .append(prefix)
	          .append(getName())
	          .append("\n");

	    return output.toString();
	}

	public boolean isToEndNavigable() {
		return toEndNavigable;
	}
	
	public String getFromEndMultiplicity() {
		return fromEndMultiplicity;
	}
	
	public String getToEndMultiplicity() {
		return toEndMultiplicity;
	}

	public String getUid() {
		return Uid;
	}

	public void setUid(String uid) {
		Uid = uid;
	}

}
	

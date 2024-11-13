package plugins.plantUML.export.models;

import com.teamdev.jxbrowser.deps.org.checkerframework.common.returnsreceiver.qual.This;

public class AssociationData extends RelationshipData {

	private String fromEndMultiplicity;
	private String toEndMultiplicity;
	
	public AssociationData(String source, String target, String type, String name, String fromEndMultiplicity, String toEndMultiplicity) {
		super(source, target, type, name);
		this.fromEndMultiplicity = fromEndMultiplicity;
		this.toEndMultiplicity = toEndMultiplicity;
	}

	@Override
	public String toExportFormat() {
		String symbol = "--";
		String label = "";
		String prefix = "";
		String source = this.getSource();
        String target = this.getTarget();
        String name = this.getName();
        String type = this.getType();
        
      // TODO multiplicities
		
        if (type == "Aggregation") {
            symbol = "o--";
        } else if (type == "Composition") {
            symbol = "*--";
        }
        if (!label.isEmpty() || !name.isEmpty()) {
        	prefix = " : ";
        }
        
       
        
        return source + " " + symbol + " " + target + prefix + label + name + "\n";
	
	}
}
	

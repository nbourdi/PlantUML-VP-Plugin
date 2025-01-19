package plugins.plantUML.models;


import java.util.ArrayList;
import java.util.List;

public class SplitFlowNode extends BaseWithSemanticsData implements FlowNode {

    private String type;
    private List<FlowNode> branches = new ArrayList<>();

    public SplitFlowNode(String name, String type) {
        super(name);
        this.type = type;
    }
    public List<FlowNode> getBranches() {
        return branches;
    }

    public String getType() {
        return type;
    }

    public void addBranch(FlowNode node) {
        branches.add(node);
    }


}

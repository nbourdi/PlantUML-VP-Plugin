package plugins.plantUML.models;

public class JoinFlowNode extends BaseWithSemanticsData implements FlowNode {

    private FlowNode nextNode;
    private int branchesToMerge;

    public JoinFlowNode(String name) {
        super(name);
    }

    public FlowNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(FlowNode nextNode) {
        this.nextNode = nextNode;
    }

    public int getBranchesToMerge() {
        return branchesToMerge;
    }

    public void setBranchesToMerge(int branchesToMerge) {
        this.branchesToMerge = branchesToMerge;
    }
}

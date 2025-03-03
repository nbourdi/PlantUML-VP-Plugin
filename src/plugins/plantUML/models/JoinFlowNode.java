package plugins.plantUML.models;

public class JoinFlowNode extends BaseWithSemanticsData implements FlowNode {

    private FlowNode nextNode;
    private String prevLabel;
    private boolean isMerge;

    public JoinFlowNode(String name) {
        super(name);
    }

    public FlowNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(FlowNode nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public String getPrevLabelBranch() {
        return this.prevLabel;
    }

    @Override
    public void setPrevLabelBranch(String label) {
        this.prevLabel = label;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }
}

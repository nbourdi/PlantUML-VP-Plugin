package plugins.plantUML.models;

public class ActionData extends BaseWithSemanticsData implements FlowNode {
    private boolean isInitial;
    private boolean isFinal;
    private boolean isFinalFlow;
    private FlowNode nextNode;
    private String swimlane;
    private String nextLabel;
    private String prevBranchLabel;

    public ActionData(String name) {
        super(name);
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public FlowNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(FlowNode nextNode) {
        this.nextNode = nextNode;
    }

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        this.swimlane = swimlane;
    }

    public String getNextLabel() {
        return nextLabel;
    }

    public void setNextLabel(String nextLabel) {
        this.nextLabel = nextLabel;
    }

    public boolean isFinalFlow() {
        return isFinalFlow;
    }

    public void setFinalFlow(boolean finalFlow) {
        isFinalFlow = finalFlow;
    }


    @Override
    public String getPrevLabelBranch() {
        return this.prevBranchLabel;
    }

    @Override
    public void setPrevLabelBranch(String label) {
        this.prevBranchLabel = label;
    }
}

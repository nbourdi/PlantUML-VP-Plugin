package plugins.plantUML.models;

public class ActionData extends BaseWithSemanticsData implements FlowNode {
    private boolean isInitial;
    private boolean isFinal;
    private FlowNode nextNode;

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
}

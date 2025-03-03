package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class WhileFlowNode implements FlowNode{

    private List<FlowNode> flowNodeList = new ArrayList<>();
    private String testLabel;
    private String yesLabel;
    private FlowNode specialOut;
    private String backwardAction;

    public String getBackwardAction() {
        return backwardAction;
    }

    public void setBackwardAction(String backwardAction) {
        this.backwardAction = backwardAction;
    }

    public FlowNode getSpecialOut() {
        return specialOut;
    }

    public void setSpecialOut(FlowNode specialOut) {
        this.specialOut = specialOut;
    }

    public String getTestLabel() {
        return testLabel;
    }

    public String getYesLabel() {
        return yesLabel;
    }

    public void setTestLabel(String testLabel) {
        this.testLabel = testLabel;
    }

    public void setYesLabel(String yesLabel) {
        this.yesLabel = yesLabel;
    }

    public List<FlowNode> getFlowNodeList() {
        return flowNodeList;
    }

    @Override
    public String getPrevLabelBranch() {
        return "";
    }

    @Override
    public void setPrevLabelBranch(String label) {

    }
}

package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class SplitBranch implements FlowNode {
    private List<FlowNode> nodeList = new ArrayList<>();

    public List<FlowNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<FlowNode> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public String getPrevLabelBranch() {
        return "";
    }

    @Override
    public void setPrevLabelBranch(String label) {

    }
}

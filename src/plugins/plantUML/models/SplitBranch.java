package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class SplitBranch {
    private List<FlowNode> nodeList = new ArrayList<>();

    public List<FlowNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<FlowNode> nodeList) {
        this.nodeList = nodeList;
    }
}

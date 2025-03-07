package plugins.plantUML.models;

import java.util.ArrayList;
import java.util.List;

public class SplitFlowNode extends BaseWithSemanticsData implements FlowNode {

    private String type;
    private List<FlowNode> branches = new ArrayList<>();
    private String prevLabel;
    private List<SplitBranch> splitBranches = new ArrayList<>();
    private boolean isMergeStyleJoin;
    private String swimlane;

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

    @Override
    public String getPrevLabelBranch() {
        return this.prevLabel;
    }

    @Override
    public void setPrevLabelBranch(String label) {
        this.prevLabel = label;
    }

    public List<SplitBranch> getSplitBranches() {
        return splitBranches;
    }

    public boolean isMergeStyleJoin() {
        return isMergeStyleJoin;
    }

    public void setMergeStyleJoin(boolean mergeStyleJoin) {
        isMergeStyleJoin = mergeStyleJoin;
    }

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        this.swimlane = swimlane;
    }
}

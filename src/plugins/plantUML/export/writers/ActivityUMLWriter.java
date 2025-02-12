package plugins.plantUML.export.writers;

import plugins.plantUML.models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ActivityUMLWriter extends PlantUMLWriter {

    private final FlowNode rootFlowNode;
    private final Set<FlowNode> processedNodes = new HashSet<>();
    Stack<JoinFlowNode> joinStack = new Stack<>();
    private String activeSwimlane = "";

    public ActivityUMLWriter(List<NoteData> notes, FlowNode rootFlowNode) {
        super(notes);
        this.rootFlowNode = rootFlowNode;
    }

    @Override
    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        generateFlowUML(rootFlowNode, plantUMLContent);

        plantUMLContent.append("@enduml\n");

        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private void generateFlowUML(FlowNode node, StringBuilder plantUMLContent) {
        if (node == null || processedNodes.contains(node)) {
            return;
        }

        processedNodes.add(node);

        if (node instanceof ActionData) {
            ActionData action = (ActionData) node;
            if (action.getSwimlane() != null) {
                if (activeSwimlane != action.getSwimlane())
                    plantUMLContent.append("|" + action.getSwimlane() + "|\n");
                activeSwimlane = action.getSwimlane();
            }

            if (action.isInitial()) {
               plantUMLContent.append("start\n");
            } else if (action.isFinal()) {
                plantUMLContent.append("stop\n");
            } else if (action.isFinalFlow()) {
                plantUMLContent.append("end\n");
            } else {
                plantUMLContent.append(":" + action.getName() + ";\n");
            }

            if (action.getNextLabel() != null && !action.getNextLabel().isEmpty()) plantUMLContent.append("-> " + action.getNextLabel() + ";\n");

            if (action.getNextNode() != null) {
                generateFlowUML(action.getNextNode(), plantUMLContent);
            } else if (!action.isFinal() && !action.isFinal()) {
                plantUMLContent.append("kill\n");
            }

        }
        // Handle SplitFlowNode
        else if (node instanceof SplitFlowNode) {
            SplitFlowNode splitNode = (SplitFlowNode) node;
            String type = splitNode.getType();

            if ("decision".equals(type)) {
                writeDecision(plantUMLContent, splitNode);
            } else if ("fork".equals(type)) {
                writeForkAndJoin(plantUMLContent, splitNode);
            }
        } else if (node instanceof JoinFlowNode) {
            JoinFlowNode joinNode = (JoinFlowNode) node;
            if (!joinStack.contains(joinNode)) joinStack.push(joinNode);
        }
    }

    private void writeDecision(StringBuilder plantUMLContent, SplitFlowNode decisionNode) {
        plantUMLContent.append("if (" + decisionNode.getName() + ") then ");

        List<FlowNode> branches = decisionNode.getBranches();
        String branchLabel = branches.get(0).getPrevLabelBranch();
        if (branchLabel != null && !branchLabel.isEmpty())
            plantUMLContent.append("(" + branchLabel + ")");
        plantUMLContent.append("\n");
        for (int i = 0; i < branches.size(); i++) {
            generateFlowUML(branches.get(i), plantUMLContent);
            if (i < branches.size() - 1) {
                String branchLabel2 = branches.get(i+1).getPrevLabelBranch();
                plantUMLContent.append("else ");
                if (branchLabel2 != null && !branchLabel2.isEmpty())
                     plantUMLContent.append("("+ branches.get(i+1).getPrevLabelBranch() + ")");
                plantUMLContent.append("\n");
            }
        }
        plantUMLContent.append("endif\n");

        if (!joinStack.isEmpty()) {
            JoinFlowNode join = joinStack.pop();
            generateFlowUML(join.getNextNode(), plantUMLContent);
        }
        FlowNode continuationNode = findJoinContinuation(decisionNode);
        if (continuationNode != null) {
            generateFlowUML(continuationNode, plantUMLContent);
        }
    }

    private void writeForkAndJoin(StringBuilder plantUMLContent, SplitFlowNode forkNode) {
        plantUMLContent.append("fork\n");

        List<FlowNode> branches = forkNode.getBranches();
        for (int i = 0; i < branches.size(); i++) {
            generateFlowUML(branches.get(i), plantUMLContent);
            if (i < branches.size() - 1) {
                plantUMLContent.append("fork again\n");
            }
        }

        if (!joinStack.isEmpty()) {
            JoinFlowNode join = joinStack.pop();
            if (join.isMerge()) plantUMLContent.append("end merge\n");
            else plantUMLContent.append("end fork\n");
            generateFlowUML(join.getNextNode(), plantUMLContent);
        }
        FlowNode continuationNode = findJoinContinuation(forkNode);
        if (continuationNode != null) {
            generateFlowUML(continuationNode, plantUMLContent);
        }
    }

    private FlowNode findJoinContinuation(SplitFlowNode forkNode) {
        for (FlowNode branch : forkNode.getBranches()) {
            if (branch instanceof JoinFlowNode) {
                return ((JoinFlowNode) branch).getNextNode();
            }
        }
        return null;
    }
}

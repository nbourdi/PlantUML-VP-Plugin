package plugins.plantUML.export.writers;

import plugins.plantUML.models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ActivityUMLWriter extends PlantUMLWriter {

    private final FlowNode rootFlowNode;
    private final Set<FlowNode> processedNodes = new HashSet<>();
    Stack<JoinFlowNode> joinStack = new Stack<>();

    public ActivityUMLWriter(List<NoteData> notes, FlowNode rootFlowNode) {
        super(notes);
        this.rootFlowNode = rootFlowNode;
    }

    @Override
    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        // Start the diagram
        generateFlowUML(rootFlowNode, plantUMLContent);

        // End the diagram
        plantUMLContent.append("@enduml\n");

        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private void generateFlowUML(FlowNode node, StringBuilder plantUMLContent) {
        if (node == null || processedNodes.contains(node)) {
            return;
        }

        processedNodes.add(node);

        // Handle ActionData nodes
        if (node instanceof ActionData) {
            ActionData action = (ActionData) node;
            if (action.isInitial()) {
               plantUMLContent.append("start\n");
            } else if (action.isFinal()) {
                plantUMLContent.append("stop\n");
            } else {
                plantUMLContent.append(":" + action.getName() + ";\n");
            }

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
        plantUMLContent.append("if (" + decisionNode.getName() + ") then (branch0)\n");

        List<FlowNode> branches = decisionNode.getBranches();
        for (int i = 0; i < branches.size(); i++) {
            generateFlowUML(branches.get(i), plantUMLContent);
            if (i < branches.size() - 1) {
                plantUMLContent.append("else (branch " + (i + 1) + ")\n");
            }
        }

        plantUMLContent.append("endif\n");
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

        plantUMLContent.append("end fork\n");
        if (!joinStack.isEmpty()) {
            JoinFlowNode join = joinStack.pop();
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

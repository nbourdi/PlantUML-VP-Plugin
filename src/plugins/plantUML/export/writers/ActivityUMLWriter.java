package plugins.plantUML.export.writers;

import plugins.plantUML.models.FlowNode;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.ActionData;
import plugins.plantUML.models.SplitFlowNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ActivityUMLWriter extends PlantUMLWriter {

    private final FlowNode rootFlowNode;

    public ActivityUMLWriter(List<NoteData> notes, FlowNode rootFlowNode) {
        super(notes);
        this.rootFlowNode = rootFlowNode;
    }

    @Override
    public void writeToFile(File file) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

        // Traverse the flow and generate PlantUML content
        generateFlowUML(rootFlowNode, plantUMLContent);

        // Append notes if any


        plantUMLContent.append("@enduml\n");

        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(plantUMLContent.toString());
        }
    }

    private void generateFlowUML(FlowNode node, StringBuilder plantUMLContent) {
        if (node == null) {
            return;
        }

        // Handle ActionData nodes
        if (node instanceof ActionData) {
            ActionData action = (ActionData) node;
            String actionDeclaration = "";
            if (action.isInitial()) actionDeclaration = "start\n";
            else if (action.isFinal()) {
                actionDeclaration = "stop\n";
            }
            else actionDeclaration = ":" + action.getName() + ";\n";
            plantUMLContent.append(actionDeclaration);

            // Handle the next node if it exists
            if (action.getNextNode() != null) {
                generateFlowUML(action.getNextNode(), plantUMLContent);
            }
        }
        // Handle SplitFlowNode (Decision, Fork, Merge, Join)
        else if (node instanceof SplitFlowNode) {
            SplitFlowNode splitNode = (SplitFlowNode) node;
            String type = splitNode.getType();
            if (type == "decision") {
                writeDecision(plantUMLContent, splitNode);
            } else if (type == "fork") {
                writeFork(plantUMLContent, splitNode);
            } else if (type == "join") {
                plantUMLContent.append("end fork\n");
            }
        }

    }

    private void writeFork(StringBuilder plantUMLContent, SplitFlowNode splitNode) {
        plantUMLContent.append("fork\n");

        for (int i = 0; i < splitNode.getBranches().size(); i++) {
            FlowNode branch = splitNode.getBranches().get(i);
            generateFlowUML(branch, plantUMLContent);
            if (i < splitNode.getBranches().size() - 1) {
                plantUMLContent.append("fork again\n");
            }
        }
        // plantUMLContent.append("endif\n");
    }

    private void writeDecision(StringBuilder plantUMLContent, SplitFlowNode splitNode) {

        plantUMLContent.append("if (" + splitNode.getName() + ") then (branch0)\n");

        for (int i = 0; i < splitNode.getBranches().size(); i++) {
            FlowNode branch = splitNode.getBranches().get(i);
            generateFlowUML(branch, plantUMLContent);
            if (i < splitNode.getBranches().size() - 1) {
                plantUMLContent.append("else (branch " + (i + 1) + ")\n");
            }
        }
        plantUMLContent.append("endif\n");
    }
}

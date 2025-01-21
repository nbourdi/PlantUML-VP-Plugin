package plugins.plantUML.imports.importers;

import net.sourceforge.plantuml.activitydiagram3.*;
import plugins.plantUML.models.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityDiagramImporter extends DiagramImporter {

    private ActivityDiagram3 activityDiagram;
    private List<FlowNode> rootNodeList = new ArrayList<>();

    public ActivityDiagramImporter(ActivityDiagram3 activityDiagram, Map<String, SemanticsData> semanticsMap) {
        super(semanticsMap);
        this.activityDiagram = activityDiagram;
    }

    @Override
    public void extract() {
        reflectionPrep();
        Method currentMethod = null;
        try {
            currentMethod = ActivityDiagram3.class.getDeclaredMethod("current");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        currentMethod.setAccessible(true);
        Instruction rootInstruction = null;
        try {
            rootInstruction = (Instruction) currentMethod.invoke(activityDiagram);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        try {
            traverseInstructions(rootInstruction, rootNodeList);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void reflectionPrep() {
        Method currentMethod = null;
        try {
            currentMethod = ActivityDiagram3.class.getDeclaredMethod("current");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        currentMethod.setAccessible(true);
    }

    private void traverseInstructions(Instruction instruction, List<FlowNode> nodeList) throws IllegalAccessException {

        System.out.println("Instruction: " + instruction.getClass().getSimpleName());
        Field allField = null;
        try {
            allField = ((InstructionList) instruction).getClass().getDeclaredField("all");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        allField.setAccessible(true);

        // root should always be an instructionList
        List<Instruction> children = null;
        try {
            children = (List<Instruction>) allField.get(instruction);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

//        IModelElement previous = null;

        for (Instruction child : children) {

            if (child instanceof InstructionSimple || child instanceof InstructionStart || child instanceof InstructionStop || child instanceof InstructionEnd)  nodeList.add(handleSimpleInstruction(child));
            else if (child instanceof InstructionLabel) { // ?
                // CANNT IMPORT LABELS. / GOTO
            } else if (child instanceof InstructionIf) {
                nodeList.add(handleDecisionInstruction((InstructionIf) child));
            }
        }
    }

    private SplitFlowNode handleDecisionInstruction(InstructionIf instruction) {

        try {
            Field branchesListField = (instruction).getClass().getDeclaredField("thens");
            branchesListField.setAccessible(true);

            Field elseField = (instruction).getClass().getDeclaredField("elseBranch");
            elseField.setAccessible(true);

            List<Branch> branches = (List<Branch>) branchesListField.get(instruction);

            Branch first = branches.get(0);
            Field branchLabelField = first.getClass().getDeclaredField("labelTest");
            branchLabelField.setAccessible(true);

            String conditionLabel = (String) branchLabelField.get(first);

            SplitFlowNode decisionNode = new SplitFlowNode( conditionLabel,  "decision");

            for (Branch branch : branches) {
                Field listOfBranch = branch.getClass().getDeclaredField("list");
                listOfBranch.setAccessible(true);
                InstructionList list = (InstructionList) listOfBranch.get(branch);

                decisionNode.getSplitBranches().add(handleBranch(list));
            }

            Branch elseBranch = (Branch) elseField.get(instruction);
            Field listOfBranch = elseBranch.getClass().getDeclaredField("list");
            listOfBranch.setAccessible(true);
            InstructionList list = (InstructionList) listOfBranch.get(elseBranch);
            decisionNode.getSplitBranches().add(handleBranch(list));

            return decisionNode;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private SplitBranch handleBranch(InstructionList list) throws IllegalAccessException {

        SplitBranch branch = new SplitBranch();

        Field allField = null;
        try {
            allField = ((list).getClass().getDeclaredField("all"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        allField.setAccessible(true);

        List<Instruction> children = null;

        try {
            children = (List<Instruction>) allField.get(list);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (Instruction child : children) {

            if (child instanceof InstructionSimple || child instanceof InstructionStart || child instanceof InstructionStop || child instanceof InstructionEnd)  branch.getNodeList().add(handleSimpleInstruction(child));
            else if (child instanceof InstructionIf) {
                branch.getNodeList().add(handleDecisionInstruction((InstructionIf) child));
            }
        }

        return branch;

    }

    private FlowNode handleSimpleInstruction(Instruction instruction) throws IllegalAccessException {
        String actionName = "";
        if (instruction instanceof InstructionSimple) {
            Field labelField = null;
            try {
                labelField = instruction.getClass().getDeclaredField("label");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            labelField.setAccessible(true);
            System.out.println("label:: " + labelField.get(instruction));  //NAME
            actionName = removeBrackets(labelField.get(instruction).toString());
        }

        String swimlaneName = instruction.getSwimlaneIn() != null ? instruction.getSwimlaneIn().toString() : "";
//        String log = "Simple instruction: in swimlane : " + instruction.getSwimlaneIn() + "  " + labelField.get(instruction); // SWIMLANE

        ActionData actionData = new ActionData(actionName);
        if(swimlaneName != null) actionData.setSwimlane(swimlaneName);

        if (instruction instanceof InstructionStart) actionData.setInitial(true);
        else if (instruction instanceof InstructionStop) actionData.setFinal(true);
        else if (instruction instanceof InstructionEnd) actionData.setFinalFlow(true);

        return actionData;
    }

    public List<FlowNode> getNodeList() {
        return rootNodeList;
    }
}

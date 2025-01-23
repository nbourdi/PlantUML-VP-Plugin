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

        logFlow(rootNodeList);

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

        for (Instruction child : children) {

            if (child instanceof InstructionSimple || child instanceof InstructionStart || child instanceof InstructionStop || child instanceof InstructionEnd)  nodeList.add(handleSimpleInstruction(child));
            else if (child instanceof InstructionLabel) { // ?
                // CANNT IMPORT LABELS. / GOTO
            } else if (child instanceof InstructionIf) {
                nodeList.add(handleDecisionInstruction((InstructionIf) child));
            } else if (child instanceof InstructionFork) {
                nodeList.add(handleFork((InstructionFork) child));
            }
        }
    }

    private SplitFlowNode handleFork(InstructionFork instruction) {

        try {
            Field forksListField = instruction.getClass().getDeclaredField("forks");
            forksListField.setAccessible(true);

            List<Instruction> forksList = (List<Instruction>) forksListField.get(instruction);

            SplitFlowNode forkNode = new SplitFlowNode("testFork", "fork");

            // each fork is another branch, and contains an InstructionList
            for (Instruction forkListItem : forksList) {
                SplitBranch forkBranch = new SplitBranch();
                forkNode.getSplitBranches().add(forkBranch);
                traverseInstructions(forkListItem, forkBranch.getNodeList());
            }
            return forkNode;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private SplitFlowNode handleDecisionInstruction(InstructionIf instruction) {

        try {
            Field branchesListField = (instruction).getClass().getDeclaredField("thens");
            branchesListField.setAccessible(true);

            Field elseField = (instruction).getClass().getDeclaredField("elseBranch");
            elseField.setAccessible(true);

            List<Branch> thens = (List<Branch>) branchesListField.get(instruction);

            Branch first = thens.get(0);
            Field branchLabelField = first.getClass().getDeclaredField("labelTest");
            branchLabelField.setAccessible(true);
            Field listOfBranch = first.getClass().getDeclaredField("list");
            listOfBranch.setAccessible(true);



            String conditionLabel = branchLabelField.get(first).toString();
            SplitFlowNode decisionNode = new SplitFlowNode( conditionLabel,  "decision");

            // "b1" list
            InstructionList firstThenList = (InstructionList) listOfBranch.get(thens.get(0));
            SplitBranch firstThen = handleBranch(firstThenList);
            decisionNode.getSplitBranches().add(firstThen);
            // done adding the first "then", now onto the decision node elses (elseifs)

            // elseIfsBranch has the else ifs chain etc, even the final else branch
            // "b2"
            SplitBranch elseIfsBranch = new SplitBranch();

            System.out.println("THENS SIZE: " + thens.size());

            decisionNode.getSplitBranches().add(elseIfsBranch);
            SplitBranch currentBranch = elseIfsBranch;
            SplitFlowNode lastSubdecision = decisionNode;

            for (int i = 1; i < thens.size(); i++) {
                // create a splitflow node!
                SplitFlowNode  subdecision = new SplitFlowNode( "testConditionLabel",  "decision");
                // this subdecision is in the "elseif " branch we are on
                currentBranch.getNodeList().add(subdecision);
                InstructionList listsubThen = (InstructionList) listOfBranch.get(thens.get(i));
                SplitBranch subThenBranch = handleBranch(listsubThen);
                subdecision.getSplitBranches().add(subThenBranch);

                lastSubdecision = subdecision;
                // in the begin
                if (i < thens.size() - 1) {
                    SplitBranch subElseIfsBranch = new SplitBranch();
                    currentBranch = subElseIfsBranch;
                    subdecision.getSplitBranches().add(subElseIfsBranch);
                }

            }


            // now to handle the else branch, the very last one
            Branch elseBranch = (Branch) elseField.get(instruction);
            InstructionList listElse = (InstructionList) listOfBranch.get(elseBranch);
            SplitBranch elseSplitBranch = handleBranch(listElse);
            lastSubdecision.getSplitBranches().add(elseSplitBranch);

            return decisionNode;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private SplitBranch handleBranch(InstructionList list) throws IllegalAccessException {

        SplitBranch branch = new SplitBranch();


        Field allField;
        try {
            allField = ((list).getClass().getDeclaredField("all"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        allField.setAccessible(true);

        List<Instruction> children;

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

    public void logFlow(List<FlowNode> nodeList) {

        for (FlowNode node : nodeList) {
            if (node instanceof ActionData) {
                System.out.println(((ActionData) node).getName() + ((ActionData) node).isFinal());
            } else
              System.out.println("Node: " + node); // Log the current node

            // Check if the node is a SplitFlowNode to handle its branches
            if (node instanceof SplitFlowNode) {
                SplitFlowNode splitNode = (SplitFlowNode) node;

                System.out.println("SplitFlowNode: " + splitNode.getName() + ", Branches: " + splitNode.getSplitBranches().size());

                for (SplitBranch branch : splitNode.getSplitBranches()) {
                    System.out.println("Branch: "); // Log the branch
                    logFlow(branch.getNodeList()); // Recursively log the branch nodes
                }
            }
        }
    }


}

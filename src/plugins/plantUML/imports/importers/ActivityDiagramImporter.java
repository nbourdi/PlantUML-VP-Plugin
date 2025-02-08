package plugins.plantUML.imports.importers;

import com.vp.plugin.ApplicationManager;
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
        Method currentMethod;
        try {
            currentMethod = ActivityDiagram3.class.getDeclaredMethod("current");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        currentMethod.setAccessible(true);
        Instruction rootInstruction = null;
        try {
            rootInstruction = (Instruction) currentMethod.invoke(activityDiagram);
        } catch (IllegalAccessException | InvocationTargetException e) {
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

        if (instruction instanceof InstructionIf) {
            nodeList.add(handleDecisionInstruction((InstructionIf) instruction));
            return;
        }
        if (instruction instanceof InstructionFork) {
            nodeList.add(handleFork((InstructionFork) instruction));
            return;
        }
        if (instruction instanceof InstructionList) {
            Field allField;
            try {
                allField = ((InstructionList) instruction).getClass().getDeclaredField("all");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            allField.setAccessible(true);

            List<Instruction> children;
            try {
                children = (List<Instruction>) allField.get(instruction);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            for (Instruction child : children) {

                if (child instanceof InstructionSimple || child instanceof InstructionStart || child instanceof InstructionStop || child instanceof InstructionEnd)  nodeList.add(handleSimpleInstruction(child));
                 else if (child instanceof InstructionIf) {
                    nodeList.add(handleDecisionInstruction((InstructionIf) child));
                } else if (child instanceof InstructionFork) {
                    nodeList.add(handleFork((InstructionFork) child));
                }
            }
            return;
        }

        ApplicationManager.instance().getViewManager().showMessage("Error: unhandled type of activity instruction\n (supported complex types: if, fork");


    }

    private SplitFlowNode handleFork(InstructionFork instruction) {

        try {
            Field forksListField = instruction.getClass().getDeclaredField("forks");
            forksListField.setAccessible(true);
            Field forkStyleField = instruction.getClass().getDeclaredField("style");
            forkStyleField.setAccessible(true);
            ForkStyle forkStyle = (ForkStyle) forkStyleField.get(instruction);

            List<Instruction> forksList = (List<Instruction>) forksListField.get(instruction);

            SplitFlowNode forkNode = new SplitFlowNode("testFork", "fork");
            if (forkStyle == ForkStyle.MERGE) forkNode.setMergeStyleJoin(true);

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
            System.out.println("THENS SIZE: " + thens.size());


            Branch first = thens.get(0);
            Field branchLabelField = first.getClass().getDeclaredField("labelTest");
            branchLabelField.setAccessible(true);
            Field listOfBranch = first.getClass().getDeclaredField("list");
            listOfBranch.setAccessible(true);

            //root decision
            String conditionLabel = branchLabelField.get(first).toString();
            SplitFlowNode decisionNode = new SplitFlowNode( removeBrackets(conditionLabel),  "decision");

            // "b1" list
            InstructionList firstThenList = (InstructionList) listOfBranch.get(thens.get(0));
            SplitBranch firstThen = handleBranch(firstThenList);
            decisionNode.getSplitBranches().add(firstThen);
            // done adding the first "then", now onto the decision node elses (elseifs)

            // elseIfsBranch has the else ifs chain etc, even the final else branch
            // "b2"
            SplitBranch elseIfsBranch = new SplitBranch();


            SplitBranch currentBranch = null;
            SplitFlowNode lastSubdecision;
            lastSubdecision = decisionNode;

            if (thens.size() > 1) { // otherwise empty branch
                decisionNode.getSplitBranches().add(elseIfsBranch);
                currentBranch = elseIfsBranch;
            }

            for (int i = 1; i < thens.size(); i++) {
                // create a splitflow node!
                SplitFlowNode  subdecision = new SplitFlowNode( removeBrackets(branchLabelField.get(thens.get(i)).toString()),  "decision");
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
            } else if (child instanceof  InstructionFork) {
                branch.getNodeList().add(handleFork((InstructionFork) child));
            }
        }

        return branch;
    }

    private FlowNode handleSimpleInstruction(Instruction instruction) throws IllegalAccessException {
        String actionName = "";
        if (instruction instanceof InstructionSimple) {
            Field labelField;
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

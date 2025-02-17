package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.*;
import com.vp.plugin.diagram.connector.IMessageUIModel;
import com.vp.plugin.diagram.shape.IInteractionActorUIModel;
import com.vp.plugin.diagram.shape.IInteractionLifeLineUIModel;
import com.vp.plugin.diagram.shape.IInteractionOccurrenceUIModel;
import com.vp.plugin.diagram.shape.ILostFoundMessageEndShapeUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_CLASS;
import static com.vp.plugin.model.factory.IModelElementFactory.MODEL_TYPE_COMPONENT;

public class SequenceDiagramCreator extends  DiagramCreator {

    // In case numbering isn't explicit in Puml, assume numbering in order of declaration.
    // Numbering is crucial for proper layout
    int sequenceNumberCounter = 0;
    IInteractionDiagramUIModel sequenceDiagram = (IInteractionDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_INTERACTION_DIAGRAM);
    IFrame rootFrame = sequenceDiagram.getRootFrame(true);

    public SequenceDiagramCreator(String diagramTitle) {
        super(diagramTitle);
        diagram = sequenceDiagram;
    }

    public void createDiagram(List<LifelineData> lifelineDatas, List<ActorData> actorDatas, List<MessageData> messageDatas, List<SequenceRef> refDatas, List<CombinedFragment> fragments, List<NoteData> noteDatas) {

        sequenceDiagram.setName(getDiagramTitle());
        sequenceDiagram.setAutoExtendActivations(true);

        lifelineDatas.forEach(this::createLifeline);
        actorDatas.forEach(this::createActor);
        messageDatas.forEach(this::createMessage);
        refDatas.forEach(this::createRef);
        fragments.forEach(this::createFragment);
        noteDatas.forEach(this::createNote);

        ApplicationManager.instance().getDiagramManager().openDiagram(sequenceDiagram);
        sequenceDiagram.setAutoExtendActivations(true);
        diagramManager.openDiagram(sequenceDiagram);
        diagramManager.layout(sequenceDiagram, DiagramManager.LAYOUT_AUTO);
        IDiagramElement[] iDiagramElements = sequenceDiagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : iDiagramElements) {
            diagramElement.resetCaption();
            diagramElement.resetCaptionSize();
        }
    }

    private void createFragment(CombinedFragment fragment) {

    }

    private void createRef(SequenceRef refData) {
        IInteractionOccurrence refModel = IModelElementFactory.instance().createInteractionOccurrence();
        rootFrame.addChild(refModel);
        ApplicationManager.instance().getViewManager().showMessage("refModel has been created");
        refModel.setName(refData.getLabel());
        for (String coverLifelineCode : refData.getParticipantCodes()) {
            ApplicationManager.instance().getViewManager().showMessage("adding coverage?");
            IModelElement lifelineOrActorModel = elementMap.get(coverLifelineCode);
            refModel.addCoveredLifeLine(lifelineOrActorModel);
        }
        IInteractionOccurrenceUIModel refShape = (IInteractionOccurrenceUIModel) diagramManager.createDiagramElement(sequenceDiagram, refModel);
        for (IModelElement coveredModel : refModel.toCoveredLifeLineArray()) {
            refShape.addChild((IShapeUIModel) shapeMap.get(coveredModel));
        }
        refShape.fitSize();
     }

    private void createMessage(MessageData messageData) {

        IMessage messageModel = IModelElementFactory.instance().createMessage();
        messageModel.setName(messageData.getName());
        if (messageData.getSequenceNumber() != null) messageModel.setSequenceNumber(messageData.getSequenceNumber());
        else messageModel.setSequenceNumber(String.valueOf(sequenceNumberCounter++));

        String fromCode = messageData.getSourceID();
        String toCode = messageData.getTargetID();
        IModelElement fromElement = elementMap.get(fromCode);
        IModelElement toElement = elementMap.get(toCode);
        checkForCallOperation(toElement, messageModel, messageData);
        if (messageData.isLost()) {
            messageModel.setFrom(fromElement);
            ILostFoundMessageEndShapeUIModel foundShape = (ILostFoundMessageEndShapeUIModel) sequenceDiagram.createDiagramElement(IInteractionDiagramUIModel.SHAPETYPE_LOST_FOUND_MESSAGE_END);
            diagramManager.createConnector(sequenceDiagram, messageModel, foundShape, shapeMap.get(messageModel.getTo()), null);
            return;
        }
        messageModel.setTo(toElement);
        if (messageData.isFound()) {
            ILostFoundMessageEndShapeUIModel lostShape = (ILostFoundMessageEndShapeUIModel) sequenceDiagram.createDiagramElement(IInteractionDiagramUIModel.SHAPETYPE_LOST_FOUND_MESSAGE_END);
            diagramManager.createConnector(sequenceDiagram, messageModel, shapeMap.get(messageModel.getFrom()), lostShape, null);
            return;
        }
        messageModel.setFrom(fromElement);
        handleActionTypes(messageData, messageModel);
        diagramManager.createConnector(sequenceDiagram, messageModel, shapeMap.get(messageModel.getFrom()), shapeMap.get(messageModel.getTo()),null);
    }

    private void handleActionTypes(MessageData messageData, IMessage messageModel) {
        if (messageData.isCreate()) {
            messageModel.setType(IMessage.TYPE_CREATE_MESSAGE);
            messageModel.setActionType(IModelElementFactory.instance().createActionTypeCreate());
        } else if (messageData.isReply())
            messageModel.setActionType(IModelElementFactory.instance().createActionTypeReturn());
        else if (messageData.isDestroy()) {
            messageModel.setActionType(IModelElementFactory.instance().createActionTypeDestroy());
        }
        else if (messageData.isRecursive()) {
            messageModel.setType(IMessage.TYPE_RECURSIVE_MESSAGE);
        }
        else if (messageData.isDuration()) {
            messageModel.setType(IMessage.TYPE_DURATION_MESSAGE);
            messageModel.setDurationHeight(messageData.getDurationHeight());
        }
    }

    private void checkForCallOperation(IModelElement toElement, IMessage messageModel, MessageData messageData) {
        // If the message is being sent to a lifeline with a base classifier, search in the classifier's operations for a possible call to them.
        if (toElement instanceof IInteractionLifeLine) {
            IModelElement baseClassifierModel = ((IInteractionLifeLine) toElement).getBaseClassifierAsModel();

            if (baseClassifierModel != null) {
                IActionTypeCall callAction = findMatchingOperation(baseClassifierModel, messageData.getName());
                if (callAction != null) {
                    messageModel.setActionType(callAction);
                    messageModel.setName(""); // Unset the label for the message as it will be set by the operation name
                }
            }
        }
    }

    private IActionTypeCall findMatchingOperation(IModelElement baseClassifierModel, String operationName) {
        List<IOperation> operations = new ArrayList<>();

        if (baseClassifierModel instanceof IComponent) {
            operations.addAll(Arrays.asList(((IComponent) baseClassifierModel).toOperationArray()));
        } else if (baseClassifierModel instanceof IClass) {
            operations.addAll(Arrays.asList(((IClass) baseClassifierModel).toOperationArray()));
        }

        for (IOperation operation : operations) {
            if (operation.getName().equals(operationName)) {
                IActionTypeCall callAction = IModelElementFactory.instance().createActionTypeCall();
                callAction.setOperation(operation);
                return callAction;
            }
        }
        return null; // No matching operation found
    }

    private void createActor(ActorData actorData) {

        IInteractionActor actorModel = IModelElementFactory.instance().createInteractionActor();
        rootFrame.addChild(actorModel);
        checkAndSettleNameConflict(actorData.getName(), "InteractionActor");
        actorModel.setName(actorData.getName());
        elementMap.put(actorData.getUid(), actorModel);

        putInSemanticsMap(actorModel, actorData);
        IInteractionActorUIModel actorShape = (IInteractionActorUIModel) diagramManager.createDiagramElement(sequenceDiagram, actorModel);
        shapeMap.put(actorModel, actorShape);
        actorShape.setY(50);
    }

    private void createLifeline(LifelineData lifelineData) {
        IInteractionLifeLine lifelineModel = IModelElementFactory.instance().createInteractionLifeLine();
        rootFrame.addChild(lifelineModel);

        /*
         * Lifelines can have Base Classifiers (components or classes)
         * If the name of the Puml participant matches a component or class we assign the element as the classifier
         */
        IModelElement[] allComponentsAndClasses = project.toAllLevelModelElementArray(new String[]{MODEL_TYPE_COMPONENT, MODEL_TYPE_CLASS});
        IModelElement baseClassifier = Arrays.stream(allComponentsAndClasses)
                .filter(element -> element instanceof IComponent && element.getName().equals(lifelineData.getName()))
                .findFirst()
                .orElseGet(() ->
                        Arrays.stream(allComponentsAndClasses)
                                .filter(element -> element instanceof IClass && element.getName().equals(lifelineData.getName()))
                                .findFirst()
                                .orElse(null)
                );

        if (baseClassifier != null) {
            lifelineModel.setBaseClassifier(baseClassifier);
        }

        lifelineModel.setName(lifelineData.getName());
        elementMap.put(lifelineData.getAlias(), lifelineModel);
        putInSemanticsMap(lifelineModel, lifelineData);
        lifelineData.getStereotypes().forEach(lifelineModel::addStereotype);

        IInteractionLifeLineUIModel lifelineShape = (IInteractionLifeLineUIModel) diagramManager.createDiagramElement(sequenceDiagram, lifelineModel);
        shapeMap.put(lifelineModel, lifelineShape);
        lifelineShape.setY(50); // avoiding lifelines stuck at the very top
    }
}

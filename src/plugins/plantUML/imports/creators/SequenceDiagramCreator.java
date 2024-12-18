package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.*;
import com.vp.plugin.diagram.connector.IMessageUIModel;
import com.vp.plugin.diagram.shape.ICombinedFragmentUIModel;
import com.vp.plugin.diagram.shape.IInteractionActorUIModel;
import com.vp.plugin.diagram.shape.IInteractionLifeLineUIModel;
import com.vp.plugin.diagram.shape.IInteractionOccurrenceUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import plugins.plantUML.models.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequenceDiagramCreator extends  DiagramCreator {

    List<LifelineData> lifelineDatas = new ArrayList<>();
    List<ActorData> actorDatas = new ArrayList<>();
    List<MessageData> messageDatas = new ArrayList<>();
    List<SequenceRef> refDatas = new ArrayList<>();
    List<CombinedFragment> fragments = new ArrayList<>();

    IInteractionDiagramUIModel sequenceDiagram = (IInteractionDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_INTERACTION_DIAGRAM);
    IFrame rootFrame = sequenceDiagram.getRootFrame(true);

    public SequenceDiagramCreator(String diagramTitle, List<LifelineData> lifelineDatas,
                                  List<ActorData> actorDatas, List<MessageData> messageDatas, List<SequenceRef> refDatas, List<CombinedFragment> fragments) {
        super(diagramTitle);
        this.lifelineDatas = lifelineDatas;
        this.actorDatas = actorDatas;
        this.messageDatas = messageDatas;
        this.refDatas = refDatas;
        this.fragments = fragments;
    }

    @Override
    public void createDiagram() {

        sequenceDiagram.setName(getDiagramTitle());
        sequenceDiagram.setAutoExtendActivations(true);


        for (LifelineData lifelineData : lifelineDatas) {
            IInteractionLifeLine lifelineModel = createLifeline(lifelineData);
        }

        for (ActorData actorData : actorDatas) {
            IInteractionActor actorModel = createActor(actorData);
        }


        for (MessageData messageData : messageDatas) {
            IMessage messageModel = createMessage(messageData);
        }

        for (SequenceRef refData : refDatas) {
            createRef(refData);
        }

        for (CombinedFragment fragment : fragments) {
            createFragment(fragment);
        }

        ApplicationManager.instance().getDiagramManager().openDiagram(sequenceDiagram);
        sequenceDiagram.setAutoExtendActivations(true);
        diagramManager.openDiagram(sequenceDiagram);
        diagramManager.layout(sequenceDiagram, DiagramManager.LAYOUT_AUTO);
//        diagramManager.autoLayout(sequenceDiagram);
        IDiagramElement[] iDiagramElements = sequenceDiagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : iDiagramElements) {
            diagramElement.resetCaption();
            diagramElement.resetCaptionSize();
        }
        diagramManager.layout(sequenceDiagram, DiagramManager.LAYOUT_AUTO);
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
        ApplicationManager.instance().getViewManager().showMessage("refShape creation");
        IInteractionOccurrenceUIModel refShape = (IInteractionOccurrenceUIModel) diagramManager.createDiagramElement(sequenceDiagram, refModel);
        for (IModelElement coveredModel : refModel.toCoveredLifeLineArray()) {
            ApplicationManager.instance().getViewManager().showMessage("covering shapes");
            refShape.addChild((IShapeUIModel) shapeMap.get(coveredModel));
        }
        refShape.fitSize();
     }

    private IMessage createMessage(MessageData messageData) {
        IMessage messageModel = IModelElementFactory.instance().createMessage();
        messageModel.setName(messageData.getName());
        messageModel.setSequenceNumber(messageData.getSequenceNumber());

        String fromCode = messageData.getSourceID();
        String toCode = messageData.getTargetID();

        IModelElement fromElement = elementMap.get(fromCode);
        IModelElement toElement = elementMap.get(toCode);

//        if (fromElement instanceof IInteractionLifeLine) {
//            IActivation fromActivation;
//            if ((((IInteractionLifeLine) fromElement).toActivationArray()) == null) {
//                fromActivation = IModelElementFactory.instance().createActivation();
//                ((IInteractionLifeLine) fromElement).addActivation(fromActivation);
//            } else {
//                ApplicationManager.instance().getViewManager().showMessage("ACTIVATION ARRAY NOT NULL");
//                fromActivation = ((IInteractionLifeLine) fromElement).getActivationByIndex(0);
//            }
//            messageModel.setFromActivation(fromActivation);
//        }
//
//        if (toElement instanceof IInteractionLifeLine) {
//            IActivation toActivation;
//            if ((((IInteractionLifeLine) toElement).toActivationArray()) == null) {
//                toActivation = IModelElementFactory.instance().createActivation();
//                ((IInteractionLifeLine) toElement).addActivation(toActivation);
//            } else {
//                ApplicationManager.instance().getViewManager().showMessage("ACTIVATION ARRAY NOT NULL");
//                toActivation = ((IInteractionLifeLine) toElement).getActivationByIndex(0);
//            }
//            messageModel.setToActivation(toActivation);
//        }

        messageModel.setFrom(fromElement);
        messageModel.setTo(toElement);

        if (messageData.isCreate()) {
            ApplicationManager.instance().getViewManager().showMessage("messageData is create");
            messageModel.setType(IMessage.TYPE_CREATE_MESSAGE);
            messageModel.setActionType(IModelElementFactory.instance().createActionTypeCreate());
        } else if (messageData.isReply()) messageModel.setActionType(IModelElementFactory.instance().createActionTypeReturn());
        else if (messageData.isDestroy()) {
            ApplicationManager.instance().getViewManager().showMessage("RECOGNIZED AS DESTROY");
            messageModel.setActionType(IModelElementFactory.instance().createActionTypeDestroy());
        }
        else if (messageData.isRecursive()) {
            messageModel.setType(IMessage.TYPE_RECURSIVE_MESSAGE);
        }
        else if (messageData.isDuration()) {
            messageModel.setType(IMessage.TYPE_DURATION_MESSAGE);
            messageModel.setDurationHeight(messageData.getDurationHeight());
        }

        Point[] points = null;
        if (fromCode.equals(toCode)) {
            int lifelineXCoord = shapeMap.get(messageModel.getFrom()).getX(); // X position of the lifeline
            int lifelineYCoord = shapeMap.get(messageModel.getFrom()).getY(); // Starting Y position

            int offsetX = 25; // Horizontal offset (width of rectangle)
            int offsetY = 25; // Vertical offset (height of rectangle)

            Point start = new Point(lifelineXCoord, lifelineYCoord);                       // Starting point
            Point corner1 = new Point(lifelineXCoord + offsetX, lifelineYCoord);           // Top-right corner
            Point corner2 = new Point(lifelineXCoord + offsetX, lifelineYCoord + offsetY); // Bottom-right corner
            Point end = new Point(lifelineXCoord, lifelineYCoord + offsetY);               // Bottom-left corner
            points = new Point[] { start, corner1, corner2, end }; // Rectangular self-message points
            System.out.println(Arrays.toString(points)); ;
            messageModel.setType(IMessage.TYPE_SELF_MESSAGE);
        }

        IMessageUIModel messageShape = (IMessageUIModel) diagramManager.createConnector(
                sequenceDiagram,
                messageModel,
                shapeMap.get(messageModel.getFrom()), // Same lifeline as "from"
                shapeMap.get(messageModel.getTo()),
                null
        );
        return messageModel;
    }

    private IInteractionActor createActor(ActorData actorData) {

        IInteractionActor actorModel = IModelElementFactory.instance().createInteractionActor();
        rootFrame.addChild(actorModel);
        checkAndSettleNameConflict(actorData.getName(), "InteractionActor");
        actorModel.setName(actorData.getName());
        elementMap.put(actorData.getName(), actorModel);

        putInSemanticsMap(actorModel, actorData);
        IInteractionActorUIModel actorShape = (IInteractionActorUIModel) diagramManager.createDiagramElement(sequenceDiagram, actorModel);
        shapeMap.put(actorModel, actorShape);
        actorShape.setY(50);
        return actorModel;
    }

    private IInteractionLifeLine createLifeline(LifelineData lifelineData) {
        IInteractionLifeLine lifelineModel = IModelElementFactory.instance().createInteractionLifeLine();
        rootFrame.addChild(lifelineModel);
        checkAndSettleNameConflict(lifelineData.getName(), "InteractionLifeline");

        lifelineModel.setName(lifelineData.getName());

        elementMap.put(lifelineModel.getName(), lifelineModel);
        putInSemanticsMap(lifelineModel, lifelineData);

        lifelineData.getStereotypes().forEach(lifelineModel::addStereotype);

        IInteractionLifeLineUIModel lifelineShape = (IInteractionLifeLineUIModel) diagramManager.createDiagramElement(sequenceDiagram, lifelineModel);
        shapeMap.put(lifelineModel, lifelineShape);
        lifelineShape.setY(50);
        return lifelineModel;
    }
}

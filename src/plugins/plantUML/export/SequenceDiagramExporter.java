package plugins.plantUML.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;

import plugins.plantUML.models.ActorData;
import plugins.plantUML.models.CombinedFragment;
import plugins.plantUML.models.CombinedFragment.Operand;
import plugins.plantUML.models.InteractionRef;
import plugins.plantUML.models.LifelineData;
import plugins.plantUML.models.MessageData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.RelationshipData;

public class SequenceDiagramExporter extends DiagramExporter {

	private IDiagramUIModel diagram;

	List<ActorData> exportedInteractionActors = new ArrayList<>();
	List<NoteData> exportedNotes = new ArrayList<>();
	List<LifelineData> exportedLifelines = new ArrayList<>();
	List<MessageData> exportedMessages = new ArrayList<>();
	List<CombinedFragment> exportedFragments = new ArrayList<>(); 
	List<InteractionRef> exportedRefs = new ArrayList<>();
	List<RelationshipData> exportedAnchors = new ArrayList<>();

	Map<IInteractionLifeLine, LifelineData> lifelineMap = new HashMap<IInteractionLifeLine, LifelineData>();
	private final Set<IMessage> processedMessages = new HashSet<>(); // Set to track processed messages

	public SequenceDiagramExporter(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	@Override
	public void extract() {
		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		List<IMessage> iMessages = new ArrayList<>();
		List<ICombinedFragment> iCombinedFragments = new ArrayList<>();
		List<IAnchor> iAnchors = new ArrayList<>();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement == null) {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
				continue;
			}
			// Add to exported elements preemptively
			allExportedElements.add(modelElement);

			if (modelElement instanceof IInteractionActor) {
				extractInteractionActor((IInteractionActor) modelElement);
			} else if (modelElement instanceof IInteractionLifeLine) {
				extractLifeline((IInteractionLifeLine) modelElement);
			} else if (modelElement instanceof IInteractionOccurrence) {
				extracRef((IInteractionOccurrence) modelElement);
			} else if (modelElement instanceof INOTE) {
				extractNote((INOTE) modelElement);
			} else if (modelElement instanceof IMessage) {
				iMessages.add((IMessage) modelElement); // Defer processing messages
			} else if (modelElement instanceof ICombinedFragment) {
				iCombinedFragments.add((ICombinedFragment) modelElement); // Defer processing fragments
			} else if (modelElement instanceof IAnchor) {
				iAnchors.add((IAnchor) modelElement); // Defer processing anchors
			} else {
				allExportedElements.remove(modelElement);
				ApplicationManager.instance().getViewManager().showMessage(
						"Warning: diagram element " + modelElement.getName()
								+ " is of unsupported type and will not be processed ..."
				);
			}
		}

		iCombinedFragments.forEach(this::extractFragment);

		for (IMessage message : iMessages) {
			if (!processedMessages.contains(message)) { // Check if message is already processed
				exportedMessages.add(extractMessage(message));
			}
		}

		exportedNotes = getNotes();
		iAnchors.forEach(this::extractAnchor);
	}

	private void extractAnchor(IRelationship relationship) {
		IModelElement source = relationship.getFrom();
		IModelElement target = relationship.getTo();
		ApplicationManager.instance().getViewManager().showMessage("rel type? " + relationship.getModelType());
		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof INOTE) {
			sourceName = getNoteAliasById(source.getId());

		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(target.getId());
		} 
		if (sourceName == null || targetName == null) {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
			return;
		}
		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(),
				relationship.getName());
		exportedAnchors.add(relationshipData);
	}

	private void extracRef(IInteractionOccurrence refModel) {
		if(refModel.getRefersTo() == null) {
			ApplicationManager.instance().getViewManager()
			.showMessage("A ref referring to nothing was skipped.");
			return;
		}
		String referenceName = refModel.getRefersTo().getName();
		InteractionRef ref = new InteractionRef(referenceName);


		for(IModelElement coveredLifeLine : refModel.toCoveredLifeLineArray()) {
			ref.getCoveredLifelines().add(coveredLifeLine.getName());
		}
		exportedRefs.add(ref);
	}

	private void extractFragment(ICombinedFragment fragmentModel) {
		CombinedFragment fragment = new CombinedFragment(fragmentModel.getInteractionOperator()); 

		for(IInteractionOperand childOperand : fragmentModel.toOperandArray()) {

			Operand operand = new Operand();
			if (childOperand.toMessageArray() != null) {
				for (IMessage message : childOperand.toMessageArray()) {

					operand.getMessages().add(extractMessage(message));
					processedMessages.add(message); // Mark message as processed, avoid duplication 
				}
			}
			fragment.getOperands().add(operand);
		}
		exportedFragments.add(fragment);
	}

	private MessageData extractMessage(IMessage messageModel) {
		IModelElement source = messageModel.getFrom();
		IModelElement target = messageModel.getTo();

		String sourceName = (source == null) ? "[" : source.getName();
		String targetName = (target == null) ? "]" : target.getName();

		MessageData messageData = new MessageData(sourceName, targetName, "Message", messageModel.getName());

		if (messageModel.getType() == IMessage.TYPE_CREATE_MESSAGE) {
			LifelineData createLifelineData = lifelineMap.get((IInteractionLifeLine) target);
			createLifelineData.setCreatedByMessage(true);
			messageData.setCreate(true, createLifelineData);
		} else if (messageModel.getType() == IMessage.TYPE_DURATION_MESSAGE) {
			int durationHeight = messageModel.getDurationHeight();
			messageData.setDuration(durationHeight);
		} else if (messageModel.getType() == IMessage.TYPE_RECURSIVE_MESSAGE) {
			messageData.setRecursive(true);
		}

		IModelElement actionType = messageModel.getActionType();
		if (actionType != null) {
			if (actionType.getName().equals("Return"))
				messageData.setReply(true);
			else if (actionType.getName().equals("Destroy")) {
				messageData.setDestroy(true);
			} else if (actionType.getName().equals("Call")) {
				IModelElement operation = ((IActionTypeCall) actionType).getOperation();
				if (operation != null) messageData.setName(operation.getName() + "()");
			}
		}

		messageData.setSequenceNumber(messageModel.getSequenceNumber());
		return messageData;
	}

	private void extractLifeline(IInteractionLifeLine lifelifeModel) {
		String name = lifelifeModel.getName();

		LifelineData lifelineData = new LifelineData(name);
		lifelineData.setDescription(lifelifeModel.getDescription());
		IModelElement classifierModel = lifelifeModel.getBaseClassifierAsModel();
		if (classifierModel != null) {
			lifelineData.setClassifier(classifierModel.getName());
		}


		lifelineData.setStereotypes(extractStereotypes(lifelifeModel));
		addSemanticsIfExist(lifelifeModel, lifelineData);
		exportedLifelines.add(lifelineData);
		lifelineMap.put(lifelifeModel, lifelineData);
	}

	private void extractInteractionActor(IInteractionActor modelElement) {
		String name = modelElement.getName();
		ActorData actorData = new ActorData(name);
		actorData.setStereotypes(extractStereotypes(modelElement));
		actorData.setDescription(modelElement.getDescription());
		addSemanticsIfExist(modelElement,actorData);
		exportedInteractionActors.add(actorData);
	}

	public List<ActorData> getExportedInteractionActors() {
		return exportedInteractionActors;
	}

	public List<LifelineData> getExportedLifelines() {
		return exportedLifelines;
	}

	public List<MessageData> getExportedMessages() {
		return exportedMessages;
	}

	public List<CombinedFragment> getExportedFragments() {
		return exportedFragments;
	}

	public List<InteractionRef> getExportedRefs() {
		return exportedRefs;
	}
	
	public List<RelationshipData> getExportedAnchors() {
		return exportedAnchors;
	}
}

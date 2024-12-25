package plugins.plantUML.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.poifs.storage.ListManagedBlock;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.diagram.shape.ICombinedFragmentUIModel;
import com.vp.plugin.model.IAnchor;
import com.vp.plugin.model.ICombinedFragment;
import com.vp.plugin.model.IInteractionActor;
import com.vp.plugin.model.IInteractionLifeLine;
import com.vp.plugin.model.IInteractionOccurrence;
import com.vp.plugin.model.IInteractionOperand;
import com.vp.plugin.model.IMessage;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IRelationship;

import javassist.expr.NewArray;
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
	private final Set<IMessage> processedMessages = new HashSet<>(); // New Set to track processed messages



	public SequenceDiagramExporter(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	@Override
	public void extract() {
		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		// hold messages and fragments until all basic elements are exported
		List<IMessage> iMessages = new ArrayList<IMessage>();
		List<ICombinedFragment> iCombinedFragments = new ArrayList<ICombinedFragment>();
		List<IAnchor> iAnchors = new ArrayList<IAnchor>();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement != null) {

				// Add the model element as exported preemptively, remove later if unsupported
				allExportedElements.add(modelElement);
				if (modelElement instanceof IInteractionActor) {
					extractInteractionActor((IInteractionActor) modelElement);

				} else if (modelElement instanceof IInteractionLifeLine) {
					extractLifeline((IInteractionLifeLine) modelElement);

				} else if (modelElement instanceof IInteractionOccurrence) {
					extracRef((IInteractionOccurrence) modelElement);
				} else if (modelElement instanceof INOTE) {
					this.extractNote((INOTE) modelElement);
				} else if (modelElement instanceof IMessage) {
					iMessages.add((IMessage) modelElement);
				} else if (modelElement instanceof ICombinedFragment) {
					iCombinedFragments.add((ICombinedFragment) modelElement);
				} else if (modelElement instanceof IAnchor) {
					iAnchors.add((IAnchor) modelElement);
				}

				else {
					allExportedElements.remove(modelElement);
					ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element "
							+ modelElement.getName() + " is of unsupported type and will not be processed ... ");
				}
			} else {
				ApplicationManager.instance().getViewManager()
				.showMessage("Warning: modelElement is null for a diagram element.");
			}
		}

		for (ICombinedFragment modelElement : iCombinedFragments) {
			if (modelElement != null) {
				extractFragment(modelElement);
			}
		}

		for (IMessage modelElement : iMessages) {

			if (!processedMessages.contains(modelElement)) { // Check if message is already processed
				exportedMessages.add(extractMessage((IMessage) modelElement));
			}
		}
		exportedNotes = getNotes(); // from base diagram exporter

		for (IAnchor anchor : iAnchors) {
			extractAnchor(anchor);
		}
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

		String name = messageModel.getName();

		String sequenceNumber = messageModel.getSequenceNumber();

		IModelElement source = messageModel.getFrom();
		IModelElement target = messageModel.getTo();
		ApplicationManager.instance().getViewManager().showMessage("rel type? " + messageModel.getModelType());

		String sourceName = (source == null) ? "[" : source.getName();
		String targetName = (target == null) ? "]" : target.getName();

		ApplicationManager.instance().getViewManager()
		.showMessage("Relationship from: " + sourceName + " to: " + targetName);
		ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + messageModel.getModelType());

		MessageData messageData = new MessageData(sourceName, targetName, "Message", messageModel.getName());

		if (messageModel.getType() == IMessage.TYPE_CREATE_MESSAGE) {
			LifelineData createLifelineData = lifelineMap.get((IInteractionLifeLine) target);
			createLifelineData.setCreatedByMessage(true);
			messageData.setCreate(true, createLifelineData);
		} else if (messageModel.getType() == IMessage.TYPE_DURATION_MESSAGE) {
			int durationHeight = messageModel.getDurationHeight();
			messageData.setDuration(durationHeight);
		} else if (messageModel.getType() == IMessage.TYPE_RECURSIVE_MESSAGE) {
			// recursive is like self but with an extra activation 
			messageData.setRecursive(true);
		}

		IModelElement actionType = messageModel.getActionType();
		if (actionType != null) {

			if (actionType.getName().equals("Return"))
				messageData.setReply(true);
			else if (actionType.getName().equals("Destroy")) {
				messageData.setDestroy(true);
			}
		}

		messageData.setSequenceNumber(sequenceNumber);
		return messageData;
	}

	private LifelineData extractLifeline(IInteractionLifeLine modelElement) {
		String name = modelElement.getName();
		LifelineData lifelineData = new LifelineData(name);
		lifelineData.setStereotypes(extractStereotypes(modelElement));
		exportedLifelines.add(lifelineData);
		lifelineMap.put((IInteractionLifeLine) modelElement, lifelineData);
		return lifelineData;
	}

	private void extractInteractionActor(IInteractionActor modelElement) {
		String name = modelElement.getName();
		ActorData actorData = new ActorData(name);
		actorData.setStereotypes(extractStereotypes(modelElement));
		exportedInteractionActors.add(actorData);
	}

	public List<NoteData> getExportedNotes() {
		return exportedNotes;
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

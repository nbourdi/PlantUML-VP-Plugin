package plugins.plantUML.export.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;

import plugins.plantUML.models.*;

public class SequenceUMLWriter extends PlantUMLWriter {

	private List<ActorData> actors;
	private List<LifelineData> lifelines;
	private List<MessageData> messages;
	private List<CombinedFragment> fragments;
	private List<InteractionRef> refs;
	private List<RelationshipData> anchors;

	private Set<String> activatedLifelines = new HashSet<String>();

	public SequenceUMLWriter(List<NoteData> notes, List<ActorData> actors, List<LifelineData> lifelines,
			List<MessageData> messages, List<CombinedFragment> fragments, List<InteractionRef> refs, List<RelationshipData> anchors) {
		super(notes);
		this.actors = actors;
		this.lifelines = lifelines;
		this.messages = messages;
		this.fragments = fragments;
		this.refs = refs;
		this.anchors = anchors;
	}

	@Override
	public void writeToFile(File file) throws IOException {
		StringBuilder plantUMLContent = new StringBuilder("@startuml\n");

		for (ActorData actorData : actors) {
			plantUMLContent.append(writeActor(actorData, ""));
		}

		for (LifelineData lifelineData : lifelines) {
			// if it is created by a message we hold until that message is about to be written for proper puml syntax
			if (!lifelineData.isCreatedByMessage())	plantUMLContent.append(writeLifeline(lifelineData, ""));
		}
		for (MessageData messageData : messages) {
			plantUMLContent.append(writeMessage(messageData, ""));
		}
		for (CombinedFragment fragment : fragments) {
			plantUMLContent.append(writeFragment(fragment));
		}

		for (InteractionRef ref : refs) {
			plantUMLContent.append(writeRef(ref));
		}
		
		plantUMLContent.append(writeNotes());
		
		for (RelationshipData anchor : anchors) {
			plantUMLContent.append(anchor.toExportFormat());
		}

		plantUMLContent.append("@enduml");
		try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
			writer.write(plantUMLContent.toString());
		}
	}

	private String writeRef(InteractionRef ref) {
		
		if (ref.getCoveredLifelines() == null || ref.getCoveredLifelines().isEmpty()) return "";
		StringBuilder refString = new StringBuilder();

		refString.append("ref over ")
				.append(String.join(", ", ref.getCoveredLifelines()))
				.append(" : ")
				.append(ref.getRefName());

		refString.append("\n");
		return refString.toString();
	}

	private String writeFragment(CombinedFragment fragment) {
		StringBuilder fragmentString = new StringBuilder();

		String fragmentType = fragment.getType();
		if (fragmentType.equals("alt") || fragmentType.equals("opt") || fragmentType.equals("loop") ||
		    fragmentType.equals("par") || fragmentType.equals("break") || fragmentType.equals("critical")) {
		    
		    fragmentString.append(fragmentType).append("\n\n");

		    List<CombinedFragment.Operand> operands = fragment.getOperands();
		    boolean isFirstOperand = true; 

		    for (CombinedFragment.Operand operand : operands) {
		        if (!isFirstOperand) {
		            fragmentString.append("else\n\n");
		        }

		        for (MessageData messageData : operand.getMessages()) {
		            fragmentString.append(writeMessage(messageData, "\t"));
		        }

		        isFirstOperand = false; 
		    }

		    fragmentString.append("end\n");
		} else {
			ApplicationManager.instance().getViewManager().showMessage("Combined fragments of type " + fragmentType + " have no PlantUML equivalent");
			return "";
		}
		return fragmentString.toString();
	}

	private String writeMessage(MessageData messageData, String indent) {
		boolean activate = false;
		String lifelineString = "";
		if (!activatedLifelines.contains(messageData.getTarget())) {
			activate = true;
			activatedLifelines.add(messageData.getTarget());
		}

		if (messageData.isCreate()) lifelineString = "create " + writeLifeline(messageData.getCreatedLifeline(), indent);
		return lifelineString + messageData.toExportFormat(activate, indent);
	}

	private String writeLifeline(LifelineData lifelineData, String indent) {
		StringBuilder lifelineString = new StringBuilder();
		String name = lifelineData.getName();

		String aliasDeclaration = formatAlias(lifelineData.getName()).equals(lifelineData.getName()) ? "" : (" as " + formatAlias(lifelineData.getName()));

		String declaration = "participant";

		if (lifelineData.getStereotypes().contains("control")) {
			declaration = "control";
		} else if (lifelineData.getStereotypes().contains("entity")) {
			declaration = "entity";
		} else if (lifelineData.getStereotypes().contains("boundary")) {
			declaration = "boundary";
		}

		lifelineString.append(indent).append(declaration).append(" ").append(formatName(name)).append(aliasDeclaration);

		if (!lifelineData.getStereotypes().isEmpty()) {
			String stereotypesString = lifelineData.getStereotypes().stream()
					.filter(stereotype -> !"control".equals(stereotype) && !"entity".equals(stereotype) && !"boundary".equals(stereotype))
					.map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			if (!stereotypesString.isEmpty()) {
				lifelineString.append(" ").append(stereotypesString);
			}
		}
		lifelineString.append("\n");
		if (lifelineData.getClassifier() != null && !lifelineData.getClassifier().isEmpty()) {
			lifelineString.append("note over ").append(formatAlias(lifelineData.getName())).append(" : ").append("Classifier: ").append(lifelineData.getClassifier());
			lifelineString.append("\n");
		}
		return lifelineString.toString();
	}

	private String writeActor(ActorData actorData, String indent) {
		StringBuilder actorString = new StringBuilder();
		String name = actorData.getName();

		String aliasDeclaration = formatAlias(actorData.getName()).equals(actorData.getName()) ? "" : (" as " + formatAlias(actorData.getName()));
		actorString.append(indent).append("actor ").append(formatName(name)).append(aliasDeclaration);


		if (!actorData.getStereotypes().isEmpty()) {
			String stereotypesString = actorData.getStereotypes().stream().map(stereotype -> "<<" + stereotype + ">>")
					.collect(Collectors.joining(", "));
			actorString.append(" ").append(stereotypesString);
		}
		actorString.append("\n");
		return actorString.toString();
	}
}
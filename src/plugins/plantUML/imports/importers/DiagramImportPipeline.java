package plugins.plantUML.imports.importers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IReference;
import com.vp.plugin.model.factory.IModelElementFactory;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.decoration.symbol.USymbol;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.jsondiagram.JsonDiagram;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.UmlDiagramType;
import net.sourceforge.plantuml.syntax.SyntaxChecker;
import net.sourceforge.plantuml.syntax.SyntaxResult;
import plugins.plantUML.imports.creators.ClassDiagramCreator;
import plugins.plantUML.imports.creators.ComponentDiagramCreator;
import plugins.plantUML.imports.creators.DescriptionDiagramCreator;
import plugins.plantUML.imports.creators.SequenceDiagramCreator;
import plugins.plantUML.models.Reference;
import plugins.plantUML.models.SemanticsData;
import plugins.plantUML.models.SubDiagramData;

public class DiagramImportPipeline {

	IProject project = ApplicationManager.instance().getProjectManager().getProject();

	// Hash map to contain the mapping of entities to semanticsData for lookup when creating 
	// String key is of format ownerName|ownerType to uniquely map to an element
	Map<String, SemanticsData> semanticsMap = new HashMap<>();

	// Have to wait for every model element and reference to be added so that
	// no null references or subdiagrams are left hanging or unimported.
	// For that, collect and update this map from every diagram import
	Map<IHasChildrenBaseModelElement, SemanticsData> modelSemanticsMap = new HashMap<IHasChildrenBaseModelElement, SemanticsData>();

	public void importMultipleFiles(List<File> files) {
		List<File> jsonFiles = new ArrayList<>();
		List<File> otherFiles = new ArrayList<>();

		for (File file : files) {
			try {
				String source = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				SourceStringReader reader = new SourceStringReader(source);
				if (reader.getBlocks().get(0).getDiagram() instanceof JsonDiagram) {
					jsonFiles.add(file);
				} else {
					otherFiles.add(file);
				}
			} catch (IOException e) {
				ApplicationManager.instance().getViewManager().showMessageDialog(
						ApplicationManager.instance().getViewManager().getRootFrame(),
						"Error reading file: " + file.getName() + "\n" + e.getMessage()
						);
			} catch (Exception e) {
				ApplicationManager.instance().getViewManager().showMessageDialog(
						ApplicationManager.instance().getViewManager().getRootFrame(),
						"Error processing file: " + file.getName() + "\n" + e.getMessage()
						);
			}
		}

		if (jsonFiles.size() > 1) {
			ApplicationManager.instance().getViewManager().showMessageDialog(
					ApplicationManager.instance().getViewManager().getRootFrame(),
					"Error: Cannot process more than one json diagram file.");
			return;
		}

		if (!jsonFiles.isEmpty()) {
			File jsonFile = jsonFiles.get(0);

			// Import JSON semantics file FIRST. This way we can know ahead of time what semanticsData to add to the BaseWithSemanticsDatas.
			try {
				String source = new String(Files.readAllBytes(jsonFiles.get(0).toPath()), StandardCharsets.UTF_8);
				importSemantics(source);
			} catch (IOException e) {
				ApplicationManager.instance().getViewManager().showMessageDialog(
						ApplicationManager.instance().getViewManager().getRootFrame(),
						"Error importing JSON diagram: " + jsonFile.getName() + "\n" + e.getMessage());
			}
		}

		// Import other diagrams
		for (File otherFile : otherFiles) {
			importFromSource(otherFile);
		}

		IDiagramUIModel[] diagrams = project.toDiagramArray();
		IModelElement[] allModelElements = project.toAllLevelModelElementArray();

		for (Map.Entry<IHasChildrenBaseModelElement, SemanticsData> entry : modelSemanticsMap.entrySet()) {
			IHasChildrenBaseModelElement modelElement = entry.getKey();
			SemanticsData semanticsData = entry.getValue();

			setModelElementSemantics(modelElement, semanticsData, diagrams, allModelElements);
		}
	}
	private void importSemantics(String source) {

		String jsonContent = extractJsonContent(source);

		if (jsonContent == null) {
			ApplicationManager.instance().getViewManager().showMessageDialog(
					ApplicationManager.instance().getViewManager().getRootFrame(), "No valid JSON content found in the source.");
			return;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			// Parsing  JSON source string into a list of SemanticsData 
			List<SemanticsData> semanticsDataList = objectMapper.readValue(jsonContent, new TypeReference<List<SemanticsData>>() {});

			for (SemanticsData semanticsData : semanticsDataList) {
				String key = semanticsData.getOwnerName() + "|" + semanticsData.getOwnerType();
				semanticsMap.put(key, semanticsData);
			}
		} catch (IOException e) {
			ApplicationManager.instance().getViewManager().showMessageDialog(
					ApplicationManager.instance().getViewManager().getRootFrame(), "Error parsing SemanticsData JSON: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String extractJsonContent(String source) {
		// regex to match json between @startuml and @enduml
		Pattern pattern = Pattern.compile("@startjson(.*?)@endjson", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(source);

		if (matcher.find()) {
			String content = matcher.group(1).trim();
			if (content.startsWith("{") || content.startsWith("[")) {
				return content;
			}
		}
		return null;
	}

	public void importFromSource(File sourceFile) {
		try {
			List<String> lines = Files.readAllLines(sourceFile.toPath());
			SyntaxResult result = SyntaxChecker.checkSyntax(lines);

			if (result.isError()) {
				// If there are syntax errors, display line at fault
				StringBuilder errorMessage = new StringBuilder("Syntax errors detected:\n");
				for (String error : result.getErrors()) {
					errorMessage.append(" - ").append(error)
					.append("\nAt line: ").append(result.getLineLocation().getPosition());
				}
				ApplicationManager.instance().getViewManager().showMessageDialog(
						ApplicationManager.instance().getViewManager().getRootFrame(),
						errorMessage.toString()
						);
				return;
			}

			String source = new String(Files.readAllBytes(sourceFile.toPath()), StandardCharsets.UTF_8);
			SourceStringReader reader = new SourceStringReader(source);


			Diagram diagram = reader.getBlocks().get(0).getDiagram();
			UmlDiagramType umlDiagramType; 
			
			try {
				UmlDiagram umlDiagram = (UmlDiagram) diagram;
				umlDiagramType = umlDiagram.getUmlDiagramType();
			} catch (ClassCastException e) {
				ApplicationManager.instance().getViewManager().showMessageDialog(
						ApplicationManager.instance().getViewManager().getRootFrame(),
						"Non-UML diagrams are not supported."
						);
				return;
			}
			
			String diagramTitle = sourceFile.getName();
			if (diagramTitle.contains(".")) {
				diagramTitle = diagramTitle.substring(0, diagramTitle.lastIndexOf('.'));
			}
			
			switch (umlDiagramType) {
			case CLASS:

				ClassDiagram classDiagram = (ClassDiagram) diagram;

				ClassDiagramImporter classDiagramImporter = new ClassDiagramImporter(classDiagram, semanticsMap);
				classDiagramImporter.extract();

				ClassDiagramCreator creator = new ClassDiagramCreator(
						diagramTitle,
						classDiagramImporter.getClassDatas(),
						classDiagramImporter.getPackageDatas(),
						classDiagramImporter.getNaryDatas(),
						classDiagramImporter.getRelationshipDatas(),
						classDiagramImporter.getNoteDatas(),
						classDiagramImporter.getAssociationPoints()
						);
				creator.createDiagram();
				modelSemanticsMap.putAll(creator.getDiagramSemanticsMap()); // update the projectmap with the new diagram
				break;


				case DESCRIPTION:
					DescriptionDiagram descriptionDiagram = (DescriptionDiagram) diagram;

					// type DESCRIPTION in puml is used for component, deployment and use case & their mixing
					// Determine the specific type of diagram
					String specificType = determineDiagramType(descriptionDiagram);

					switch (specificType) {
						case "usecase":
							DescriptionDiagramImporter useCaseDiagramImporter = new DescriptionDiagramImporter(descriptionDiagram, semanticsMap);
							useCaseDiagramImporter.extract();
							DescriptionDiagramCreator useCaseDiagramCreator = new DescriptionDiagramCreator(
									diagramTitle,
									specificType,
									useCaseDiagramImporter.getComponentDatas(),
									useCaseDiagramImporter.getInterfaceDatas(),
									useCaseDiagramImporter.getPackageDatas(),
									useCaseDiagramImporter.getRelationshipDatas(),
									useCaseDiagramImporter.getActorDatas(),
									useCaseDiagramImporter.getUseCaseDatas(),
									useCaseDiagramImporter.getNoteDatas()
							);
							useCaseDiagramCreator.createDiagram();
							modelSemanticsMap.putAll(useCaseDiagramCreator.getDiagramSemanticsMap());
							break;

						case "component":
							DescriptionDiagramImporter componentDiagramImporter = new DescriptionDiagramImporter(descriptionDiagram, semanticsMap);
							componentDiagramImporter.extract();
							ComponentDiagramCreator componentDiagramCreator = new ComponentDiagramCreator(
									diagramTitle,
									componentDiagramImporter.getComponentDatas(),
									componentDiagramImporter.getInterfaceDatas(),
									componentDiagramImporter.getPackageDatas(),
									componentDiagramImporter.getRelationshipDatas(),
									componentDiagramImporter.getNoteDatas()
							);
							componentDiagramCreator.createDiagram();
							modelSemanticsMap.putAll(componentDiagramCreator.getDiagramSemanticsMap());
							break;

						case "deployment":
//							DeploymentDiagramImporter deploymentDiagramImporter = new DeploymentDiagramImporter(descriptionDiagram, semanticsMap);
//							deploymentDiagramImporter.extract();
//							DeploymentDiagramCreator deploymentDiagramCreator = new DeploymentDiagramCreator(
//									diagramTitle,
//									deploymentDiagramImporter.getNodeDatas(),
//									deploymentDiagramImporter.getArtifactDatas(),
//									deploymentDiagramImporter.getRelationshipDatas(),
//									deploymentDiagramImporter.getNoteDatas()
//							);
//							deploymentDiagramCreator.createDiagram();
//							modelSemanticsMap.putAll(deploymentDiagramCreator.getDiagramSemanticsMap());
							break;
					}
					break;

			case SEQUENCE:

				SequenceDiagram sequenceDiagram = (SequenceDiagram) diagram;
				SequenceDiagramImporter sequenceDiagramImporter = new SequenceDiagramImporter(sequenceDiagram, semanticsMap);
				sequenceDiagramImporter.extract();

				SequenceDiagramCreator sequenceDiagramCreator = new SequenceDiagramCreator(
						diagramTitle,
						sequenceDiagramImporter.getLifelineDatas(),
						sequenceDiagramImporter.getActorDatas(),
						sequenceDiagramImporter.getMessageDatas(),
						sequenceDiagramImporter.getRefDatas(),
						sequenceDiagramImporter.getCombinedFragments()
				);

				sequenceDiagramCreator.createDiagram();
				modelSemanticsMap.putAll(sequenceDiagramCreator.getDiagramSemanticsMap());
				break;

			default:

				ApplicationManager.instance().getViewManager().showMessageDialog(
						ApplicationManager.instance().getViewManager().getRootFrame(),
						"Unsupported diagram type: " + umlDiagramType.name());
			}

		} catch (IOException e) {
			ApplicationManager.instance().getViewManager().showMessageDialog(
					ApplicationManager.instance().getViewManager().getRootFrame(),
					"Error reading source: " + e.getMessage()
					);
		}
	}

	private String determineDiagramType(DescriptionDiagram descriptionDiagram) {
		int actorCount = 0;
		int componentCount = 0;
		int nodeCount = 0;

		for (Entity leafEntity : descriptionDiagram.leafs()) {
			USymbol symbol = leafEntity.getUSymbol();
			if (symbol == null) continue;

			switch (symbol.getSName()) {
				case actor:
					actorCount++;
					break;
				case component:
					componentCount++;
					break;
				case node:
				case artifact:
					nodeCount++;
					break;
			}
		}

		if (actorCount > componentCount && actorCount > nodeCount) {
			return "usecase";
		} else if (componentCount > actorCount && componentCount > nodeCount) {
			return "component";
		} else if (nodeCount > actorCount && nodeCount > componentCount) {
			return "deployment";
		}

		return "component"; // Default to Component Diagram if no clear majority
	}



	private void setModelElementSemantics(IHasChildrenBaseModelElement modelElement, SemanticsData semanticsData, IDiagramUIModel[] diagrams, IModelElement[] modelElements) {

		String desc = semanticsData.getDescription();
		List<SubDiagramData> subDiagramDatas = semanticsData.getSubDiagrams();
		List<Reference> references = semanticsData.getReferences();

		Map<String, IDiagramUIModel> diagramLookup = new HashMap<>();
		for (IDiagramUIModel diagram : diagrams) {
			diagramLookup.put(diagram.getName(), diagram);
		}


		for (SubDiagramData subDiagramData : subDiagramDatas) {
			String subDiagramName = subDiagramData.getName();

			IDiagramUIModel diagram = diagramLookup.get(subDiagramName);
			if (diagram != null) {
				modelElement.addSubDiagram(diagram);
			}
		}

		Map<String, IModelElement> modelElementLookup = new HashMap<>();
		// TODO: sos, this doesnt account for type, just puts name, need fix + not all packages
		for (IModelElement projectModelElement : modelElements) {
			modelElementLookup.put(projectModelElement.getName() + "|" +projectModelElement.getModelType(), projectModelElement);
		}

		for (Reference reference : references) {
			IReference referenceModel = IModelElementFactory.instance().createReference();
			modelElement.addReference(referenceModel);
			referenceModel.setDescription(reference.getDescription());

			switch (reference.getType()) {
			case "diagram":
				referenceModel.setUrlAsDiagram(diagramLookup.get(reference.getName()));
				referenceModel.setType(IReference.TYPE_DIAGRAM);
				referenceModel.setName(reference.getName());
				// referenceModel.setUrl(reference.getName());
				break;
			case "url":
				referenceModel.setType(IReference.TYPE_URL);
				referenceModel.setUrl(reference.getName());
				break;
			case "file":
				referenceModel.setType(IReference.TYPE_FILE);
				referenceModel.setUrl(reference.getName());
				break;
			case "folder":
				referenceModel.setType(IReference.TYPE_FOLDER);
				referenceModel.setUrl(reference.getName());
				break;
			case "shape":
				referenceModel.setType(IReference.TYPE_SHAPE);
				referenceModel.setName(reference.getName());
				break;
			case "model_element":
				referenceModel.setUrlAsModel(modelElementLookup.get(reference.getName()+ "|" + reference.getModelType()));

				ApplicationManager.instance().getViewManager().showMessage("reference.getName() " +reference.getName() + " referencemodel url " + referenceModel.getUrl() + " res of lookup " );

				referenceModel.setType(IReference.TYPE_MODEL_ELEMENT);
				referenceModel.setName(reference.getName());
				break;

			default:
				ApplicationManager.instance().getViewManager()
				.showMessage("Unknown reference type: " + reference.getType());
				break;
			}

		}

		modelElement.setDescription(desc);
	}

}

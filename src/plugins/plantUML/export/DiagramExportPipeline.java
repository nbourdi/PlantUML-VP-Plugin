package plugins.plantUML.export;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IDiagramUIModel;

import plugins.plantUML.export.writers.*;
import plugins.plantUML.models.SemanticsData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiagramExportPipeline {

	private final File outputFolder;

	public DiagramExportPipeline(File outputFolder) {
		this.outputFolder = outputFolder;
	}

	/**
	 * Executes the export pipeline for a given diagram.
	 */

	private List<SemanticsData> projectSemanticsDatas = new ArrayList<SemanticsData>();

	public void export(IDiagramUIModel diagram) throws IOException, UnfitForExportException {
		String diagramType = diagram.getType();
		String diagramTitle = diagram.getName();
		File outputFile = createOutputFile(diagramTitle, "uml");

		DiagramExporter exporter;

		try {
			switch (diagramType) {
			case "ClassDiagram":
				ClassDiagramExporter cde = new ClassDiagramExporter(diagram);
				exporter = cde;
				cde.extract();
				ClassUMLWriter classWriter = new ClassUMLWriter(
						cde.getExportedClasses(), 
						cde.getRelationshipDatas(), 
						cde.getExportedPackages(), 
						cde.getExportedNary(), 
						cde.getNotes()
						);
				classWriter.writeToFile(outputFile);

				if (cde.getExportedSemantics() != null && !cde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(cde.getExportedSemantics());
				}

				break;

			case "ComponentDiagram":
			case "DeploymentDiagram":
				ComponentDeploymentDiagramExporter comde = new ComponentDeploymentDiagramExporter(diagram);
				exporter = comde;
				comde.extract();
				ComponentDeploymentUMLWriter componentWriter = new ComponentDeploymentUMLWriter(
						comde.getNotes(), 
						comde.getExportedComponents(), 
						comde.getExportedInterfaces(),
						comde.getExportedArtifacts(),
						comde.getExportedPackages(),
						comde.getRelationshipDatas()
						);
				componentWriter.writeToFile(outputFile);

				if (comde.getExportedSemantics() != null && !comde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(comde.getExportedSemantics());
				}
				break;

			case "InteractionDiagram":
				SequenceDiagramExporter seqde = new SequenceDiagramExporter(diagram);
				exporter = seqde;
				seqde.extract();
				SequenceUMLWriter sequenceWriter = new SequenceUMLWriter(
						seqde.getNotes(),
						seqde.getExportedInteractionActors(),
						seqde.getExportedLifelines(),
						seqde.getExportedMessages(),
						seqde.getExportedFragments(),
						seqde.getExportedRefs(),
						seqde.getExportedAnchors()
						);
				sequenceWriter.writeToFile(outputFile);

				if (seqde.getExportedSemantics() != null && !seqde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(seqde.getExportedSemantics());
				}
				
				break;

			case "UseCaseDiagram":
				UseCaseDiagramExporter ucde = new UseCaseDiagramExporter(diagram);
				exporter = ucde;
				ucde.extract();
				UseCaseWriter useCaseWriter = new UseCaseWriter(
						ucde.getExportedUseCases(), 
						ucde.getExportedRelationships(),
						ucde.getExportedPackages(),
						ucde.getExportedActors(), 
						ucde.getNotes()
						);
				useCaseWriter.writeToFile(outputFile);
				if (ucde.getExportedSemantics() != null && !ucde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(ucde.getExportedSemantics());
				}

				break;
			case "StateDiagram":
				StateDiagramExporter stde = new StateDiagramExporter(diagram);
				exporter = stde;
				stde.extract();
				StateUMLWriter stateUMLWriter = new StateUMLWriter(
						stde.getNotes(),
						stde.getStateDatas(),
						stde.getTransitions(),
						stde.getChoices(),
						stde.getHistories(),
						stde.getForkJoins()
				);
				stateUMLWriter.writeToFile(outputFile);
				if (stde.getExportedSemantics() != null && !stde.getExportedSemantics().isEmpty()) {

					projectSemanticsDatas.addAll(stde.getExportedSemantics());
				}

				break;
			case "ActivityDiagram":
				ActivityDiagramExporter acde = new ActivityDiagramExporter(diagram);
				exporter = acde;
				acde.extract();
				ActivityUMLWriter activityUMLWriter =  new ActivityUMLWriter(
						acde.getNotes(),
						acde.getRootNode()
				);
				activityUMLWriter.writeToFile(outputFile);
				if (acde.getExportedSemantics() != null && !acde.getExportedSemantics().isEmpty()) {
					projectSemanticsDatas.addAll(acde.getExportedSemantics());
				}
				break;
			default:
				throw new UnfitForExportException("Error: " + diagramType + " not supported for export yet.");
			}
		} catch (IOException ex) {
			ApplicationManager.instance().getViewManager()
			.showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(),  "Error processing diagram: " + diagram.getName() + "\n" + ex.getMessage());
			throw ex;

		} catch (UnfitForExportException e) {
			ApplicationManager.instance().getViewManager()
					.showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(),  "Error: " + diagram.getName() + " is unfit for exporting\n" + e.getMessage());
			throw e;
		}

		// all Warnings in popup
		exporter.showPopupWarnings();
	}

	public List<SemanticsData> exportPartialSemantics(DiagramExporter diagramExporter) {
		if (diagramExporter.getExportedSemantics() != null && !diagramExporter.getExportedSemantics().isEmpty()) {
			return diagramExporter.getExportedSemantics(); 
		}
		return null;
	}

	private File createOutputFile(String title, String contentType) throws IOException {
		StringBuilder fileName = new StringBuilder();
		fileName.append(title.replaceAll("[^a-zA-Z0-9\u0370-\u03FF]", "_"));
		if (contentType.equals("json")) fileName.append("_semantics");
		fileName.append(".txt");
		File outputFile = new File(outputFolder, fileName.toString());
		if (!outputFile.exists() && !outputFile.createNewFile()) {
			throw new IOException("Failed to create file: " + outputFile.getAbsolutePath());
		}
		return outputFile;
	}

	public boolean exportDiagramList(List<IDiagramUIModel> selectedDiagrams) {
		boolean allSuccessful = true;
		for (IDiagramUIModel activeDiagram : selectedDiagrams) {
			try {
				export(activeDiagram);
			} catch (IOException ex) {
				ApplicationManager.instance().getViewManager()
				.showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Error processing diagram: " + activeDiagram.getName() + "\n" + ex.getMessage());
				allSuccessful = false;
			} catch (UnsupportedOperationException ex) {
				ApplicationManager.instance().getViewManager()
				.showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), ex.getMessage());
				allSuccessful = false;
			} catch (UnfitForExportException e) {
				allSuccessful = false;
			}
		}

		File jsonFile;
		try {
			jsonFile = createOutputFile("project_semantics", "json");
			PlantJSONWriter.writeToFile(jsonFile, projectSemanticsDatas);
		} catch (IOException e) {
			ApplicationManager.instance().getViewManager()
			.showMessageDialog(ApplicationManager.instance().getViewManager().getRootFrame(), "Error writing to json semantics file");
			e.printStackTrace();
			allSuccessful = false;
		}


		return allSuccessful;
	}

	public void exportAllDiagrams() {
		ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
		IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
		this.exportDiagramList(Arrays.asList(allDiagrams));
	}

	public void exportSpecificDiagram(String target) throws IOException {
		ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
		IDiagramUIModel targetDiagram = projectManager.getProject().getDiagramById(target);
		this.export(targetDiagram);
	}
}
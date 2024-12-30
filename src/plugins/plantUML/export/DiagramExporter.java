package plugins.plantUML.export;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IReference;
import com.vp.plugin.model.IStereotype;
import com.vp.vpuml.plugin.umlpluginmodel.ModelElement;

import plugins.plantUML.models.BaseWithSemanticsData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.Reference;
import plugins.plantUML.models.SemanticsData;
import plugins.plantUML.models.SubDiagramData;

public abstract class DiagramExporter {

	public abstract void extract();

	protected List<NoteData> noteDatas = new ArrayList<>();
	protected List<SemanticsData> exportedSemantics = new ArrayList<SemanticsData>();
	
	// Set of all exported elements for constant lookup so that no relationships with un-exported elements are written
	protected Set<IModelElement> allExportedElements = new HashSet<>();

	protected SemanticsData extractSemantics(IModelElement modelElement) {
		List<Reference> extractedReferences = extractReferences((IHasChildrenBaseModelElement) modelElement);
		List<SubDiagramData> extractedSubdiagrams = extractSubdiagrams(modelElement);
		String description = modelElement.getDescription();

		// Include semantics only if there is at least 1 of the 3 elements
		if ((!extractedReferences.isEmpty()) ||
				(!extractedSubdiagrams.isEmpty()) ||
				(description != null && !description.isEmpty())) {

			SemanticsData semanticsData = new SemanticsData();
			semanticsData.setOwnerName(modelElement.getName());
			semanticsData.setOwnerType(modelElement.getModelType());
			semanticsData.setReferences(extractedReferences);
			semanticsData.setSubDiagrams(extractedSubdiagrams);
			semanticsData.setDescription(description); 

			return semanticsData;

		} else { // No meaningful information to extract	
			return null; 
		}
	}


	private List<Reference> extractReferences(IHasChildrenBaseModelElement modelElement) { 
		List<Reference> exportedReferences = new ArrayList<Reference>();
		// ApplicationManager.instance().getViewManager().showMessage("========= References::");
		Iterator referenceIter = modelElement.referenceIterator();
		while (referenceIter.hasNext()) {
			IReference reference = (IReference) referenceIter.next();
			// ApplicationManager.instance().getViewManager().showMessage(reference.getUrl() + " " + reference.getType());

			Reference referenceData = null;

			try {
				switch (reference.getType()) {
				case IReference.TYPE_DIAGRAM:
					referenceData = new Reference("diagram", reference.getDescription(), reference.getUrlAsDiagram().getName(), reference.getUrlAsDiagram().getType(), null); // TODO: throws nullpointer bc getUrlAsDiagram when the diagram is not in project
					break;

				case IReference.TYPE_URL:
					referenceData = new Reference("url", reference.getDescription(), reference.getUrl(), null, null);
					break;

				case IReference.TYPE_FILE:
					referenceData = new Reference("file", reference.getDescription(), reference.getUrl(), null, null);
					break;

				case IReference.TYPE_FOLDER:
					referenceData = new Reference("folder", reference.getDescription(), reference.getUrl(), null, null);
					break;

				case IReference.TYPE_SHAPE:
					referenceData = new Reference("shape", reference.getDescription(), reference.getName(), null, null);
					break;

				case IReference.TYPE_MODEL_ELEMENT:
					referenceData = new Reference("model_element", reference.getDescription(), reference.getUrlAsModel().getName(), null, reference.getUrlAsModel().getModelType());
					break;

				default:
					ApplicationManager.instance().getViewManager().showMessage("Found and ignored an unsupported reference");
					break;
				}
			} catch (NullPointerException e) {
				ApplicationManager.instance().getViewManager().showMessage("Warning: a reference by model element " + modelElement.getName() + " was null possibly due to referencing a deleted element/diagram.");
			}

			if (referenceData != null)
				exportedReferences.add(referenceData);
		}
		return exportedReferences;
	}

	protected void extractNote(INOTE noteModel) {
		String name = noteModel.getName();
		String content = noteModel.getDescription();
		String id = noteModel.getId();
		NoteData noteData = new NoteData(name, content, id);
		noteDatas.add(noteData);
	}

	protected String getNoteAliasById(String naryId) {
		for (NoteData noteData : noteDatas) {
			if (noteData.getId().equals(naryId)) {
				return noteData.getAlias();
			}
		}
		return null;
	}

	protected List<String> extractStereotypes(IModelElement modelElement) {
		List<String> stereotypes = new ArrayList<String>();

		Iterator stereoIter = modelElement.stereotypeModelIterator();
		while (stereoIter.hasNext()) {
			IStereotype stereotype = (IStereotype) stereoIter.next();
			String stereotypeString = stereotype.getName();
			ApplicationManager.instance().getViewManager().showMessage("Stereotype: " + stereotypeString);
			stereotypes.add(stereotypeString);
		}
		return stereotypes;
	}

	private List<SubDiagramData> extractSubdiagrams(IModelElement modelElement) {
		List<SubDiagramData> subDiagramDatas = new ArrayList<SubDiagramData>();
		IDiagramUIModel[] subDiagrams = modelElement.toSubDiagramArray();
		if (subDiagrams != null) {
			for (IDiagramUIModel subDiagram : subDiagrams) {
				subDiagramDatas.add(new SubDiagramData(subDiagram.getName(), subDiagram.getType()));
			}
		}
		return subDiagramDatas;
	}

	protected void addSemanticsIfExist(IModelElement modelElement, BaseWithSemanticsData modelData) {
		SemanticsData semantics = extractSemantics(modelElement);

		if (semantics != null) {
			modelData.setSemantics(extractSemantics(modelElement));
			exportedSemantics.add(modelData.getSemantics());
		}
	}

	protected boolean isRootLevel(IModelElement element) {
		return (element.getParent() == null);
	}

	public List<NoteData> getNotes() {
		return noteDatas;
	}

	public List<SemanticsData> getExportedSemantics() {
		return exportedSemantics;
	}
}

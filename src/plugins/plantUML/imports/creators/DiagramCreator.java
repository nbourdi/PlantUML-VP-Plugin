package plugins.plantUML.imports.creators;

import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IReference;
import com.vp.plugin.model.factory.IModelElementFactory;

import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.Reference;
import plugins.plantUML.models.SemanticsData;
import plugins.plantUML.models.SubDiagramData;

public class DiagramCreator {
	
	DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
	private String diagramTitle;
	
	public DiagramCreator(String diagramTitle) {
		this.setDiagramTitle(diagramTitle);
	}

	protected INOTE createNote(NoteData noteData) {	
		
		INOTE noteModel = IModelElementFactory.instance().createNOTE();
		noteModel.setName(noteData.getName());
		noteModel.setDescription(noteData.getContent());
		return noteModel;
	}

	public String getDiagramTitle() {
		return diagramTitle;
	}

	public void setDiagramTitle(String diagramTitle) {
		this.diagramTitle = diagramTitle;
	}
	
	protected void setModelElementSemantics(IModelElement modelElement, SemanticsData semanticsData) {
		
		String desc = semanticsData.getDescription();
		List<SubDiagramData> subDiagramDatas = semanticsData.getSubDiagrams();
		List<Reference> references = semanticsData.getReferences();
		
		for (SubDiagramData subDiagramData : subDiagramDatas) {
			
		}
		
		for (Reference reference : references) {
			IReference referenceModel = IModelElementFactory.instance().createReference();
			
			referenceModel.setDescription(reference.getDescription());
		    
			switch (reference.getType()) {
		    case "diagram":
		        referenceModel.setType(IReference.TYPE_DIAGRAM);
		        referenceModel.setName(reference.getName());
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
		        referenceModel.setType(IReference.TYPE_MODEL_ELEMENT);
		        referenceModel.setName(reference.getName());
		        break;
		    
		    default:
		    	ApplicationManager.instance().getViewManager()
		         .showMessage("Unknown reference type: " + reference.getType());
		        break;
		}

			referenceModel.setType(0);
		}
			
		modelElement.setDescription(desc);
	}
}

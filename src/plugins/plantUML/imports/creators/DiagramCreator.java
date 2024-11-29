package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.factory.IModelElementFactory;

import plugins.plantUML.models.NoteData;

public class DiagramCreator {
	
	DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
	
	protected INOTE createNote(NoteData noteData) {	
		
		INOTE noteModel = IModelElementFactory.instance().createNOTE();
		noteModel.setName(noteData.getName());
		noteModel.setDescription(noteData.getContent());
		return noteModel;
	}
}

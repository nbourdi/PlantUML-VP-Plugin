package plugins.plantUML.export;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IReference;
import com.vp.plugin.model.IStereotype;

import plugins.plantUML.export.models.NaryData;
import plugins.plantUML.export.models.NoteData;
import plugins.plantUML.export.models.SubdiagramData;

public class DiagramExporter {
	
	protected List<NoteData> noteDatas = new ArrayList<>();;
	
	protected void extractReferences(IClass classModel) {
		// TODO: what is this does it work
		ApplicationManager.instance().getViewManager().showMessage("=========References::");
		Iterator referenceIter = classModel.referenceIterator();
		while (referenceIter.hasNext()) {
	            IReference reference = (IReference) referenceIter.next();
				ApplicationManager.instance().getViewManager().showMessage(reference.getUrlAsDiagram().getName()); 
	    }
	}
	
	protected void extractNote (INOTE noteModel) {
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
	
	protected List<SubdiagramData> extractSubdiagrams(IModelElement modelElement) {
		List<SubdiagramData> subdiagramDatas = new ArrayList<SubdiagramData>();
		IDiagramUIModel[] subDiagrams = modelElement.toSubDiagramArray();
		if (subDiagrams != null) {
			for (IDiagramUIModel subDiagram : subDiagrams) {
				subdiagramDatas.add(new SubdiagramData(subDiagram.getName(), subDiagram.getType()));
				ApplicationManager.instance().getViewManager().showMessage("=====SUBDIAGRAM:: " + subDiagram.getName() + " " + subDiagram.getType());
			}
		}
		return subdiagramDatas;
    }

	public List<NoteData> getNotes() {
        return noteDatas;
    }
}

package plugins.plantUML.imports.creators;

import java.util.HashMap;
import java.util.Map;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.factory.IModelElementFactory;

import plugins.plantUML.models.BaseWithSemanticsData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.SemanticsData;

public class DiagramCreator {
	
	DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
	private String diagramTitle;
	Map<IHasChildrenBaseModelElement, SemanticsData> diagramSemanticsMap = new HashMap<IHasChildrenBaseModelElement, SemanticsData>(); // to pass that back to the pipeline to update the master map
	
	public DiagramCreator(String diagramTitle) {
		this.setDiagramTitle(diagramTitle);
	}

	protected INOTE createNote(NoteData noteData) {	
		INOTE noteModel = IModelElementFactory.instance().createNOTE();
		noteModel.setName(noteData.getName());
		noteModel.setDescription(noteData.getContent());
		return noteModel;
	}

	protected void putInSemanticsMap(IHasChildrenBaseModelElement modelElement, BaseWithSemanticsData modelData)  {
		SemanticsData semantics = modelData.getSemantics();
		if (semantics != null)
			diagramSemanticsMap.put(modelElement, modelData.getSemantics());
	}
	
	public String getDiagramTitle() {
		return diagramTitle;
	}

	public void setDiagramTitle(String diagramTitle) {
		this.diagramTitle = diagramTitle;
	}
	public Map<IHasChildrenBaseModelElement, SemanticsData> getDiagramSemanticsMap() {
		return diagramSemanticsMap;
	}

}

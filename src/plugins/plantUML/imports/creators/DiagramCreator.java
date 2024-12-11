package plugins.plantUML.imports.creators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import plugins.plantUML.models.BaseWithSemanticsData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.SemanticsData;
import v.ajp.ig;

public abstract class DiagramCreator {
	
	DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();
	private String diagramTitle;
	Map<IHasChildrenBaseModelElement, SemanticsData> diagramSemanticsMap = new HashMap<IHasChildrenBaseModelElement, SemanticsData>(); // to pass that back to the pipeline to update the master map
	
	IProject project = ApplicationManager.instance().getProjectManager().getProject();
	
	IModelElement[] allModelElements = project.toAllLevelModelElementArray();
	
	Map<String, List<IModelElement>> allModelsMap = new HashMap<>();
	
	// IPackage package1 = IModelElementFactory.instance().createPackage();
	
	protected Map<String, IModelElement> elementMap = new HashMap<>(); // map of entity IDs to modelelements. needed for links
	protected Map<IModelElement, IDiagramElement> shapeMap = new HashMap<>(); // map of modelelements to their created shape UImodels
	
	protected IDiagramUIModel diagram;
		
	
	public DiagramCreator(String diagramTitle) {
		this.setDiagramTitle(diagramTitle);	
		
		for (IModelElement projectModelElement : allModelElements) {
			String key = projectModelElement.getName() + "|" + projectModelElement.getModelType();
			allModelsMap.putIfAbsent(key, new ArrayList<>());
		    allModelsMap.get(key).add(projectModelElement);
		}
		// alt approach
		// TODO: this need be moved up to importer
		// package1.setName("testing plugin package3");
	}
	
	public abstract void createDiagram();
	
	protected void checkAndSettleNameConflict(String name, String type) {
		String key = name + "|" + type; 
		
		List<IModelElement> conflictingElements = allModelsMap.get(key);
		if (conflictingElements == null || conflictingElements.isEmpty()) {
			return;
		}
		else {
			for (IModelElement conflictingElement : conflictingElements) {
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
				String formattedDateTime = now.format(formatter);

				conflictingElement.setName(name + "_renamed_on_import_" + formattedDateTime);
				ApplicationManager.instance().getViewManager().
					showMessage("Warning: the modelElement \""+ name + "\" of type " + type + " was renamed to " + conflictingElement.getName() +
							" due to import of conflicting plantuml element.");
				// alternative approach with packages
				// package1.addChild(conflictingElement);				
			}
			
			return;
		}
	}

	protected INOTE createNote(NoteData noteData) {	
		INOTE noteModel = IModelElementFactory.instance().createNOTE();
		noteModel.setName(noteData.getName());
		noteModel.setDescription(noteData.getContent());
		INoteUIModel noteShape = (INoteUIModel) diagramManager.createDiagramElement(diagram, noteModel);
		elementMap.put(noteData.getUid(), noteModel);
		shapeMap.put(noteModel, noteShape);
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

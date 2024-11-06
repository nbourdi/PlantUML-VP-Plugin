package plugins.plantUML.export;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IUseCase;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IActor;

public class UseCaseDiagramExporter extends DiagramExporter {

    public void extract(IDiagramUIModel diagram) {
        String diagramID = diagram.getId();
        ApplicationManager.instance().getViewManager().showMessage("DIAGRAM ID : " + diagramID);
        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement instanceof IUseCase) {
                // Retrieve use case name
                String useCaseName = modelElement.getName();
                String useCaseDesc = modelElement.getDescription();
                String useCaseId = modelElement.getId();
                String masterView = ((IUseCase) modelElement).getParent().getId();
                Boolean isMasterView = diagramElement.isMasterView();
                String isMasterString = (isMasterView ? "master" : "aux");
                ApplicationManager.instance().getViewManager().showMessage("Use Case: "  + useCaseName + " /////// Description: " + useCaseDesc + " // id: " + useCaseId + " // isMasterView?:  " + isMasterString);

            } else if (modelElement instanceof IActor) {
            	
                // Retrieve actor name
                String actorName = modelElement.getName();
                String acrorDesc = modelElement.getDescription();
                String actorGoals = ((IActor) modelElement).getGoals();
                String actorId = modelElement.getId();
                // String masterView = ((IActor) modelElement).getParent().getId();
                ApplicationManager.instance().getViewManager().showMessage("Actor: " + actorName + " ///////// Description: " + acrorDesc + " ////// Goals: " + actorGoals  + " /// id: " + actorId );
                
            }
            if (modelElement instanceof IRelationship) {
                IRelationship relationship = (IRelationship) modelElement;
                String fromElement = relationship.getFrom().getName();
                String toElement = relationship.getTo().getName();
                String relationshipType = relationship.getModelType();

                ApplicationManager.instance().getViewManager().showMessage("Relationship: " 
                      + fromElement
                      + " -> "
                      + toElement
                      + " [Type: " 
                      + relationshipType
                      + "]");
            }
            else {
            	String Id = modelElement.getId();
            	String name = modelElement.getName();
            	ApplicationManager.instance().getViewManager().showMessage("id : " + Id + name );
			}
        }
    }
}

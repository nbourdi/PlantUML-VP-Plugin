package plugins.plantUML.export;

import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IReference;

public class ClassDiagramExporter extends DiagramExporter {
	
	public void extract(IDiagramUIModel diagram) {
		
		IDiagramElement[] allElements = diagram.toDiagramElementArray(); 
		
		for (IDiagramElement diagramElement : allElements){
			

			IModelElement connectorModel = diagramElement.getModelElement();
			if (connectorModel instanceof IRelationship) {
		    	
		    	IRelationship relationship = (IRelationship) connectorModel;
		    	String id = relationship.getId();
		    	IModelElement source = relationship.getFrom();
                IModelElement target = relationship.getTo();

                String sourceAlias = source.getName();
                String targetAlias = target.getName();
		    	
		        ApplicationManager.instance().getViewManager().showMessage("connector of type " + diagramElement.getModelElement().getModelType() + " from: " + sourceAlias + " to " + targetAlias);
				ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getModelType());
		        
		    }
			ApplicationManager.instance().getViewManager().showMessage("NAME:");
			ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getName());
//			ApplicationManager.instance().getViewManager().showMessage("DESCRIPTION:");
//			ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getDescription()); // getDocumentation is deprecated
			ApplicationManager.instance().getViewManager().showMessage("TYPE:");
			ApplicationManager.instance().getViewManager().showMessage(diagramElement.getModelElement().getModelType());
			
			if (diagramElement.getModelElement() instanceof IClass) {
				
				IClass classModel = (IClass) diagramElement.getModelElement();
				extractAttributes(classModel);
				extractOperations(classModel);
				extractReferences(classModel); // TODO : this can be for all diagrams, superclass of exporters
				
				
			}
			
		}
	}

	

	private void extractOperations(IClass classModel) {
		/* 
		 * Given an IClass class element,
		 * extracts the operations defined
		 */
		ApplicationManager.instance().getViewManager().showMessage("Operations::");
		Iterator operationIter = classModel.operationIterator();
		while (operationIter.hasNext()) {
            IOperation operation = (IOperation) operationIter.next();
            ApplicationManager.instance().getViewManager().showMessage(operation.getName());
            ApplicationManager.instance().getViewManager().showMessage(" == Parameters::");
    		Iterator paramIter = operation.parameterIterator();
    		while (paramIter.hasNext()) {
    			IParameter param = (IParameter) paramIter.next();
    			ApplicationManager.instance().getViewManager().showMessage(param.getName());
    		}
		}
	}

	private void extractAttributes(IClass classModel) {
		/* 
		 * Given an IClass class element,
		 * extracts the attribute names in it along 
		 * with their visibility (private, public), if they're abstract,
		 * their type and initial value if one is defined
		 */
		ApplicationManager.instance().getViewManager().showMessage("Attributes::");
		Iterator attributeIter = classModel.attributeIterator();
		while (attributeIter.hasNext()) {
	            IAttribute attribute = (IAttribute) attributeIter.next();
				ApplicationManager.instance().getViewManager().showMessage(attribute.getName() + " " + attribute.getVisibility() + " abstract?: " +
	                    attribute.isAbstract()+ " "+ attribute.getTypeAsString() +" " + attribute.getInitialValue());
	           
	    }
		
	}
		
}

	


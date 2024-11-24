package plugins.plantUML.imports.creators;

import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.vpuml.plugin.umlpluginmodel.Stereotype;

import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.OperationData.Parameter;

public class ClassDiagramCreator extends DiagramCreator {

	List<ClassData> classDatas;
	
	public ClassDiagramCreator(List<ClassData> classDatas) {
		this.classDatas = classDatas;
	}
	
	public void createDiagram () { 
		IClassDiagramUIModel classDiagram = (IClassDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
        classDiagram.setName("Im Class Diagram");
		
		for (ClassData classData : classDatas) {
			IClass classModel = createClass(classData);
			IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, classModel);
			
		}
		diagramManager.layout(classDiagram, DiagramManager.LAYOUT_ORTHOGONAL);
        ApplicationManager.instance().getProjectManager().saveProject();
	}

	private IClass createClass(ClassData classData) {
		
		IClass classModel = IModelElementFactory.instance().createClass();
		
		// TODO: design Q: importing a class name that already exists
		// sets the name to a different unique one, or should we design so that aux view is created?
		classModel.setName(classData.getName());
		 ApplicationManager.instance().getViewManager()
         .showMessage("classData name: " + classData.getName() + " classModel name " + classModel.getName());
		classModel.setVisibility(classData.getVisibility());
		for (String stereotype : classData.getStereotypes()) {
			classModel.addStereotype(stereotype);
		}
		
		for (AttributeData attributeData : classData.getAttributes()) {
			IAttribute attributeModel = IModelElementFactory.instance().createAttribute();
			attributeModel.setName(attributeData.getName());
			attributeModel.setType(attributeData.getType());
			attributeModel.setInitialValue(attributeData.getInitialValue());
			attributeModel.setVisibility(attributeData.getVisibility()); 
			if (attributeData.isStatic()) {
				attributeModel.setScope("classifier");
			}
			classModel.addAttribute(attributeModel); 
		}
		
		for (OperationData operationData : classData.getOperations()) {
			IOperation operationModel = IModelElementFactory.instance().createOperation();
			operationModel.setAbstract(operationData.isAbstract());
			operationModel.setName(operationData.getName());
			operationModel.setReturnType(operationData.getReturnType());
			
			for (Parameter parameter : operationData.getParameters()) {
				IParameter paramModel = IModelElementFactory.instance().createParameter();
				paramModel.setName(parameter.getName());
				paramModel.setType(parameter.getType());
				paramModel.setDefaultValue(parameter.getDefaultValue());
				operationModel.addParameter(paramModel);
			}
			classModel.addOperation(operationModel);
		}
		return classModel;
	}
}

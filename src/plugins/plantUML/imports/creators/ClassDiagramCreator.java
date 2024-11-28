package plugins.plantUML.imports.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.INARYUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.vpuml.plugin.umlpluginmodel.Stereotype;

import com.vp.plugin.model.IModelElement;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.OperationData.Parameter;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;

public class ClassDiagramCreator extends DiagramCreator {

	List<ClassData> classDatas = new ArrayList<ClassData>();
	List<PackageData> packageDatas = new ArrayList<PackageData>();
	List<NaryData> naryDatas = new ArrayList<NaryData>();
	List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
	IClassDiagramUIModel classDiagram = (IClassDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
	
	Map<String, IModelElement> elementMap = new HashMap<>(); // map of entity IDs to modelelements. needed for links.

	public ClassDiagramCreator(List<ClassData> classDatas, List<PackageData> packageDatas, List<NaryData> naryDatas) {
		this.classDatas = classDatas;
		this.packageDatas = packageDatas;
		this.naryDatas = naryDatas;
	}
	
	public void createDiagram () { 
		
        classDiagram.setName("pakcage diagram");
		for (ClassData classData : classDatas) {
			IClass classModel = createClass(classData);
			IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, classModel);
			classShape.fitSize();
		}
		
		for (PackageData packageData : packageDatas) {
			IPackage packageModel = createPackage(packageData);
//			IPackageUIModel packageShape = (IPackageUIModel) diagramManager.createDiagramElement(classDiagram, packageModel);
		}
		
		for (NaryData naryData : naryDatas) {
			INARY naryModel = createNary(naryData);
			INARYUIModel naryShape = (INARYUIModel) diagramManager.createDiagramElement(classDiagram, naryModel);
		}
		
		for (RelationshipData relationshipData : relationshipDatas) {
			IGeneralization generalization = IModelElementFactory.instance().createGeneralization();
			String fromID = relationshipData.getSource()
			// generalization.setFrom();
		}
		
		diagramManager.layout(classDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getProjectManager().saveProject();
        ApplicationManager.instance().getDiagramManager().openDiagram(classDiagram);
	}

	private INARY createNary(NaryData naryData) {
		
		//String entityId = naryData.getUid();
		INARY naryModel = IModelElementFactory.instance().createNARY();
		
		//elementMap.put(entityId, naryModel);
		naryModel.setName(naryData.getName());
		
		return naryModel;
	}

	private IPackage createPackage(PackageData packageData) {
		IPackage packageModel = IModelElementFactory.instance().createPackage();
		packageModel.setName(packageData.getPackageName());
		ApplicationManager.instance().getViewManager()
        .showMessage("package name: " + packageModel.getName());
		
		List<IClassUIModel> classShapes = new ArrayList<IClassUIModel>();
		for (ClassData packagedClassData : packageData.getClasses()) {
			IClass packagedClassModel = createClass(packagedClassData);
			IClassUIModel packagedclassShape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, packagedClassModel);
			packageModel.addChild(packagedClassModel);
			classShapes.add(packagedclassShape);
		}
		IPackageUIModel packageShape = (IPackageUIModel) diagramManager.createDiagramElement(classDiagram, packageModel);
		
		for (IClassUIModel classShape : classShapes) {
			packageShape.addChild(classShape);
		}
		
		return packageModel; //TODO : unecessary? could be void maybe need for subdiagrams actually
	}

	private IClass createClass(ClassData classData) {
		
		
		IClass classModel = IModelElementFactory.instance().createClass();
		String entityId = classData.getUid();
		elementMap.put(entityId, classModel);
		
		// TODO: design Q: importing a class name that already exists
		// sets the name to a different unique one, or should we design so that aux view is created?
		classModel.setName(classData.getName());
		 ApplicationManager.instance().getViewManager()
         .showMessage("classData name: " + classData.getName() + " classModel name " + classModel.getName());
		classModel.setVisibility(classData.getVisibility());
		classModel.setAbstract(classData.isAbstract());
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

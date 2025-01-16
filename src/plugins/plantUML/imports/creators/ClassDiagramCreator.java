package plugins.plantUML.imports.creators;

import java.util.ArrayList;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.connector.IAnchorUIModel;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IContainmentUIModel;
import com.vp.plugin.diagram.connector.IDependencyUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.connector.IRealizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.INARYUIModel;
import com.vp.plugin.diagram.shape.INoteUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IAnchor;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IRealization;
import com.vp.plugin.model.factory.IModelElementFactory;

import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AssociationPoint;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.OperationData.Parameter;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;

public class ClassDiagramCreator extends DiagramCreator {

	IClassDiagramUIModel classDiagram = (IClassDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_CLASS_DIAGRAM);
	
	public ClassDiagramCreator(String diagramTitle) {
		super(diagramTitle);
		diagram = classDiagram;
	}

	public void createDiagram (List<ClassData> classDatas, List<PackageData> packageDatas, List<NaryData> naryDatas, List<RelationshipData> relationshipDatas, List<NoteData> noteDatas, List<AssociationPoint> assocPoints) {
		
        classDiagram.setName(getDiagramTitle());
		classDatas.forEach(this::createClass);
		naryDatas.forEach(this::createNary);
		packageDatas.forEach(this::createPackage);
		noteDatas.forEach(this::createNote);
		relationshipDatas.forEach(this::createRelationship);
		assocPoints.forEach(this::createAssocPoint);

		diagramManager.layout(classDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getDiagramManager().openDiagram(classDiagram);
	}

	private void createAssocPoint(AssociationPoint assocPoint) {
		
		IModelElement fromElement = elementMap.get(assocPoint.getFromUid1());
		IModelElement toElement = elementMap.get(assocPoint.getFromUid2());
		IAssociation association = IModelElementFactory.instance().createAssociation();
		association.setFrom(fromElement);
		association.setTo(toElement);
		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(classDiagram, association, shapeMap.get(fromElement), shapeMap.get(toElement), null);
		
		shapeMap.put(association, associationConnector); // cast may not work?
		
		IAssociationClass associationClass = IModelElementFactory.instance().createAssociationClass();
		
		associationClass.setFrom(association); 
		associationClass.setTo(elementMap.get(assocPoint.getToUid())); 
		diagramManager.createConnector(classDiagram, associationClass, associationConnector, shapeMap.get(elementMap.get(assocPoint.getToUid())), null);
		
	}

	private INARY createNary(NaryData naryData) {		
		INARY naryModel = IModelElementFactory.instance().createNARY();
		String entityId = naryData.getUid();
		elementMap.put(entityId, naryModel);
		// TODO: naries dont conflict for some reason..
		checkAndSettleNameConflict(naryData.getName(), "NARY");
		naryModel.setName(naryData.getName());
		putInSemanticsMap(naryModel, naryData);
		INARYUIModel naryShape = (INARYUIModel) diagramManager.createDiagramElement(classDiagram, naryModel);
		shapeMap.put(naryModel, naryShape);
		return naryModel;
	}

	private IPackage createPackage(PackageData packageData) {
		IPackage packageModel = IModelElementFactory.instance().createPackage();
		elementMap.put(packageData.getUid(), packageModel);
		
		checkAndSettleNameConflict(packageData.getName(), "Package");
		
		packageModel.setName(packageData.getName());		
		IPackageUIModel packageShape = (IPackageUIModel) diagramManager.createDiagramElement(classDiagram, packageModel);
		shapeMap.put(packageModel, packageShape);
		
		for (ClassData packagedClassData : packageData.getClasses()) {
			IClass packagedClassModel = createClass(packagedClassData);
			packageModel.addChild(packagedClassModel);
			packageShape.addChild((IShapeUIModel) shapeMap.get(packagedClassModel));
		}
		
		for (NaryData packagedNaryData : packageData.getNaries()) {
			INARY packagedNaryModel = createNary(packagedNaryData);
			packageModel.addChild(packagedNaryModel);
			packageShape.addChild((IShapeUIModel) shapeMap.get(packagedNaryModel));
		}
		
		for (PackageData subPackageData : packageData.getSubPackages()) {
			IPackage subPackageModel = createPackage(subPackageData);
			packageModel.addChild(subPackageModel);
			packageShape.addChild((IShapeUIModel) shapeMap.get(subPackageModel));			
		}

		putInSemanticsMap(packageModel, packageData);
		return packageModel; 
	}

	private IClass createClass(ClassData classData) {
		
		IClass classModel = IModelElementFactory.instance().createClass();
		String entityId = classData.getUid();
		elementMap.put(entityId, classModel);
				
		checkAndSettleNameConflict(classData.getName(), "Class");
		
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

		putInSemanticsMap(classModel, classData);
		
		IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, classModel);
		shapeMap.put(classModel, classShape);
		classShape.fitSize();
		return classModel;
	}
}
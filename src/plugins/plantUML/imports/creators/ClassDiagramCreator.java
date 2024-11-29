package plugins.plantUML.imports.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.connector.IAnchorUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.connector.IRealizationUIModel;
import com.vp.plugin.diagram.connector.IDependencyUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.INARYUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IAnchor;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IRealization;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.plugin.model.IModelElement;

import plugins.plantUML.models.AssociationData;
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
	
	Map<String, IModelElement> elementMap = new HashMap<>(); // map of entity IDs to modelelements. needed for links
	Map<IModelElement, IShapeUIModel> shapeMap = new HashMap<>(); // map of modelelements to their created shape UImodels

	public ClassDiagramCreator(List<ClassData> classDatas, List<PackageData> packageDatas, List<NaryData> naryDatas, List<RelationshipData> relationshipDatas) {
		this.classDatas = classDatas;
		this.packageDatas = packageDatas;
		this.naryDatas = naryDatas;
		this.relationshipDatas = relationshipDatas;
	}
	
	public void createDiagram () { 
		
        classDiagram.setName("pakcage diagram");
		for (ClassData classData : classDatas) {
			IClass classModel = createClass(classData);
		}
		
		for (PackageData packageData : packageDatas) {
			IPackage packageModel = createPackage(packageData);
//			IPackageUIModel packageShape = (IPackageUIModel) diagramManager.createDiagramElement(classDiagram, packageModel);
		}
		
		for (NaryData naryData : naryDatas) {
			INARY naryModel = createNary(naryData);
			INARYUIModel naryShape = (INARYUIModel) diagramManager.createDiagramElement(classDiagram, naryModel);
			// elementMap.put(naryData.getUid(), naryModel);
			shapeMap.put(naryModel, naryShape);
		}
		
		for (RelationshipData relationshipData : relationshipDatas) {
			
			String fromID = relationshipData.getSourceID();
			String toID = relationshipData.getTargetID();
			IModelElement fromModelElement = elementMap.get(fromID);
			IModelElement toModelElement = elementMap.get(toID);
			if (fromModelElement == null || toModelElement == null) {
				ApplicationManager.instance().getViewManager()
		        .showMessage("Warning: a relationship was skipped because one of its ends was not a previously imported modelElement");
				continue;
			}
			
			if (relationshipData instanceof AssociationData) {
				IAssociation association = IModelElementFactory.instance().createAssociation();
				association.setFrom(fromModelElement);
				association.setTo(toModelElement);
				
				if (relationshipData.getType() == "Aggregation") {
					IAssociationEnd aggregationFromEnd = (IAssociationEnd) association.getFromEnd();
					aggregationFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_AGGREGATION);
				}
				else if (relationshipData.getType() == "Composition") {
					IAssociationEnd compositionFromEnd = (IAssociationEnd) association.getFromEnd();
					compositionFromEnd.setAggregationKind(IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
				}
				if (((AssociationData) relationshipData).isToEndNavigable()) {
					IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();
					// toEnd.setNavigable();
				}
				
				IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(classDiagram, association, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
				
				if (relationshipData.getName() != "NULL") // label
					association.setName(relationshipData.getName());
			}
			
			else {			
				switch (relationshipData.getType()) {
				
				case "Generalization":
					IGeneralization generalization = IModelElementFactory.instance().createGeneralization();					
					generalization.setFrom(fromModelElement);
					generalization.setTo(toModelElement);
					IGeneralizationUIModel generalizationConnector = (IGeneralizationUIModel) diagramManager.createConnector(classDiagram, generalization, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
					if (relationshipData.getName() != "NULL") // label
						generalization.setName(relationshipData.getName());
					break;

				case "Realization":
					IRealization realization = IModelElementFactory.instance().createRealization();
					realization.setFrom(fromModelElement);
					realization.setTo(toModelElement);
					IRealizationUIModel realizationConnector = (IRealizationUIModel) diagramManager.createConnector(classDiagram, realization, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
					if (relationshipData.getName() != "NULL") // label
						realization.setName(relationshipData.getName());
					break;
					
				case "Dependency":
					IDependency dependency = IModelElementFactory.instance().createDependency();
					dependency.setFrom(fromModelElement);
					dependency.setTo(toModelElement);
					IDependencyUIModel dependencyConnector = (IDependencyUIModel) diagramManager.createConnector(classDiagram, dependency, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
					if (relationshipData.getName() != "NULL") // label
						dependency.setName(relationshipData.getName());
					break;
				case "Anchor": // be ware of constraint on notes.
					IAnchor anchor = IModelElementFactory.instance().createAnchor();
					anchor.setFrom(fromModelElement);
					anchor.setTo(toModelElement);
					IAnchorUIModel anchorConnector = (IAnchorUIModel) diagramManager.createConnector(classDiagram, anchor, shapeMap.get(fromModelElement), shapeMap.get(toModelElement), null);
					if (relationshipData.getName() != "NULL") // label
						anchor.setName(relationshipData.getName());
					break;
				default:
					ApplicationManager.instance().getViewManager()
			        .showMessage("Warning: unsupported type of relationship was skipped");
					break;
				}			
			}			
		}
		
		diagramManager.layout(classDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getProjectManager().saveProject();
        ApplicationManager.instance().getDiagramManager().openDiagram(classDiagram);
	}

	private INARY createNary(NaryData naryData) {		
		INARY naryModel = IModelElementFactory.instance().createNARY();
		String entityId = naryData.getUid();
		elementMap.put(entityId, naryModel);
		naryModel.setName(naryData.getName());
		
		return naryModel;
	}

	private IPackage createPackage(PackageData packageData) {
		IPackage packageModel = IModelElementFactory.instance().createPackage();
		elementMap.put(packageData.getUid(), packageModel);
		packageModel.setName(packageData.getPackageName());		
		IPackageUIModel packageShape = (IPackageUIModel) diagramManager.createDiagramElement(classDiagram, packageModel);
		shapeMap.put(packageModel, packageShape);
		
//		List<IClassUIModel> classShapes = new ArrayList<IClassUIModel>();
		for (ClassData packagedClassData : packageData.getClasses()) {
			IClass packagedClassModel = createClass(packagedClassData);
//			IClassUIModel packagedclassShape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, packagedClassModel);
//			shapeMap.put(packagedClassModel, packagedclassShape);
			packageModel.addChild(packagedClassModel);
			packageShape.addChild(shapeMap.get(packagedClassModel));
		}
		
		for (NaryData packagedNaryData : packageData.getNaries()) {
			// TODO
		}
		
		for (PackageData subPackageData : packageData.getSubPackages()) {
			IPackage subPackageModel = createPackage(subPackageData);
			packageModel.addChild(subPackageModel);
			ApplicationManager.instance().getViewManager()
	        .showMessage("trying to add sub pack line 200");
			packageShape.addChild(shapeMap.get(subPackageModel));			
		}
		
//		for (IClassUIModel classShape : classShapes) {
//			packageShape.addChild(classShape);
//		}
		
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
		
		IClassUIModel classShape = (IClassUIModel) diagramManager.createDiagramElement(classDiagram, classModel);
		shapeMap.put(classModel, classShape);
		classShape.fitSize();
		
		return classModel;
	}
}

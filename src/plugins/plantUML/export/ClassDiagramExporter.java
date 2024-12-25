package plugins.plantUML.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IRelationship;

import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.OperationData.Parameter;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;

public class ClassDiagramExporter extends DiagramExporter {

	private IDiagramUIModel diagram;

	List<ClassData> exportedClasses = new ArrayList<>();
	List<RelationshipData> relationshipDatas = new ArrayList<>();
	List<PackageData> exportedPackages = new ArrayList<>();
	List<NaryData> exportedNary = new ArrayList<>();
	List<NoteData> exportedNotes = new ArrayList<>();

	private List<NaryData> allExportedNary = new ArrayList<>();

	public ClassDiagramExporter(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	@Override
	public void extract() {

		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement != null) {
				// Add the model element as exported beforehand, remove later if unsupported 
				allExportedElements.add(modelElement);
				if (modelElement instanceof IClass) {
					if (!(modelElement.getParent() instanceof IPackage))
						extractClass((IClass) modelElement, null);
				} else if (modelElement instanceof IPackage) {
					extractPackage((IPackage) modelElement);
				} else if (modelElement instanceof INARY) {
					if (!(modelElement.getParent() instanceof IPackage))
						extractNary((INARY) modelElement, null);
				} else if (modelElement instanceof INOTE) {
					this.extractNote((INOTE) modelElement);
				} else if (modelElement instanceof IRelationship) {
					//  just to not  show the message
				} else {
					allExportedElements.remove(modelElement);
					ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element "
							+ modelElement.getName() +" is of unsupported type and will not be processed ... ");
				}
			} else {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
			}
		}

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement != null) {

				if (modelElement instanceof IRelationship /*&&  !(modelElement instanceof IAssociationClass) */) {
					extractRelationship((IRelationship) modelElement);
				} 
			}
		}
		
		exportedNotes = getNotes(); // from base diagram exporter

	}

	private void extractClass(IClass classModel, PackageData packageData) {
		boolean isInPackage = (classModel.getParent() instanceof IPackage);
		ClassData classData = new ClassData(classModel.getName(), classModel.isAbstract(), classModel.getVisibility(),
				isInPackage);
		classData.setDescription(classModel.getDescription());
		classData.setStereotypes(extractStereotypes(classModel));
		extractAttributes(classModel, classData);
		extractOperations(classModel, classData);
		
		addSemanticsIfExist(classModel, classData);
		exportedClasses.add(classData);
		if (packageData != null)
			packageData.getClasses().add(classData);
	}

	private void extractNary(INARY naryModel, PackageData packageData) {
		boolean isInPackage = (naryModel.getParent() instanceof IPackage);
		String name = naryModel.getName();
		String id = naryModel.getId();
		NaryData naryData = new NaryData(name, id, isInPackage);
		naryData.setDescription(naryModel.getDescription());
		addSemanticsIfExist(naryModel, naryData);
		
		if (packageData != null)
			packageData.getNaries().add(naryData);
		else exportedNary.add(naryData); // i changed if bug
		
		allExportedNary.add(naryData); // naries are to be reversed by id so whether in package or not, need to add so that relationships arent pointing to null.
	}

	private String getNaryAliasById(String naryId) {
		for (NaryData naryData : allExportedNary) {
			if (naryData.getId().equals(naryId)) {
				return naryData.getAlias();
			}
		}
		return null;
	}

	private void extractRelationship(IRelationship relationship) {
		IModelElement source = (IModelElement) relationship.getFrom();
		IModelElement target = (IModelElement) relationship.getTo();
		ApplicationManager.instance().getViewManager().showMessage("rel type? " + relationship.getModelType());
		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof INARY) {
			sourceName = getNaryAliasById(((INARY) source).getId());
		} else if (source instanceof INOTE) {
			sourceName = getNoteAliasById(((INOTE) source).getId());
		} 
		if (target instanceof INARY) {
			targetName = getNaryAliasById(((INARY) target).getId());
		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(((INOTE) target).getId());
		} 

		if (relationship instanceof IAssociation) {
			IAssociation association = (IAssociation) relationship;

			IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
			IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();
			String fromEndMultiplicity = "";
			String toEndMultiplicity = "" ;
			
			if (fromEnd.getMultiplicity() != null) {
				 fromEndMultiplicity = fromEnd.getMultiplicity().equals("Unspecified") ? "" : fromEnd.getMultiplicity();
			}
			if (toEnd.getMultiplicity() != null) {
				 toEndMultiplicity = toEnd.getMultiplicity().equals("Unspecified") ? "" : toEnd.getMultiplicity();
			}
			AssociationData associationData = new AssociationData(sourceName, targetName, relationship.getModelType(),
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity,
					// fromEnd.getNavigable() == 0,
					toEnd.getNavigable() == 0, fromEnd.getAggregationKind());
			relationshipDatas.add(associationData);
			return;
		}
		if (relationship instanceof IAssociationClass) {
			
			IAssociation association;
			
				if (source instanceof IAssociation) {
				association = (IAssociation) source;
				String associationFrom = formatAlias(association.getFrom().getName());
				String associationTo = formatAlias(association.getTo().getName());
				sourceName = "(" + associationFrom + ", " + associationTo + ")";
			} else {
				association = (IAssociation) target;
				String associationFrom = association.getFrom().getName();
				String associationTo = association.getTo().getName();
				targetName = "(" + associationFrom + ", " + associationTo + ")";
			}
			
			ApplicationManager.instance().getViewManager().showMessage("In associationClass block with getFrom: " + sourceName + " target: " +targetName);
			
		}
		

		ApplicationManager.instance().getViewManager()
				.showMessage("Relationship from: " + sourceName + " to: " + targetName);
		ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());

		if (sourceName == null || targetName == null) {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
		}
		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(),
				relationship.getName());
		relationshipDatas.add(relationshipData);
	}

	private void extractPackage(IPackage packageModel) {

		if (!(packageModel.getParent() instanceof IPackage)) {
			PackageData packageData = new PackageData(packageModel.getName(), null, null, null, false, false);
			packageData.setDescription(packageModel.getDescription());
			IModelElement[] childElements = packageModel.toChildArray();
			for (IModelElement childElement : childElements) {
				if (childElement instanceof IClass) {
					extractClass((IClass) childElement, packageData);
				} else if (childElement instanceof INARY) {
					extractNary((INARY) childElement, packageData);
				} else if (childElement instanceof IPackage) {
					PackageData parent = packageData;
					extractPackagedPackage((IPackage) childElement, parent);

				}
			}
			addSemanticsIfExist(packageModel, packageData);
			exportedPackages.add(packageData);
		}
	}

	private void extractPackagedPackage(IPackage packageModel, PackageData parent) {
		ApplicationManager.instance().getViewManager().showMessage("Extracting package: " + packageModel.getName());

		PackageData packageData = new PackageData(packageModel.getName(), null, null, null, true, false);
		packageData.setDescription(packageModel.getDescription());
		IModelElement[] childElements = packageModel.toChildArray();
		for (IModelElement childElement : childElements) {
			if (childElement instanceof IClass) {
				extractClass((IClass) childElement, packageData);
			} else if (childElement instanceof INARY) {
				extractNary((INARY) childElement, packageData);
			} else if (childElement instanceof IPackage) {
				extractPackagedPackage((IPackage) childElement, packageData);
			}
		}
		parent.getSubPackages().add(packageData);
		exportedPackages.add(packageData);
	}

	private void extractAttributes(IClass classModel, ClassData classData) {
		Iterator attributeIter = classModel.attributeIterator();
		while (attributeIter.hasNext()) {
			IAttribute attribute = (IAttribute) attributeIter.next();
			AttributeData attr = new AttributeData(attribute.getVisibility(), attribute.getName(),
					attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

			classData.addAttribute(attr);
		}
	}

	private void extractOperations(IClass classModel, ClassData classData) {
		Iterator operationIter = classModel.operationIterator();
		while (operationIter.hasNext()) {
			IOperation operation = (IOperation) operationIter.next();
			OperationData op = new OperationData(operation.getVisibility(), operation.getName(),
					operation.getReturnTypeAsString(), operation.isAbstract(), null, operation.getScope());

			Iterator paramIterator = operation.parameterIterator();
			while (paramIterator.hasNext()) {
				IParameter parameter = (IParameter) paramIterator.next();
				Parameter paramData = new Parameter(parameter.getName(), parameter.getTypeAsString(),
						parameter.getDefaultValueAsString());
				op.addParameter(paramData);
			}

			classData.addOperation(op);
		}
	}

	public List<ClassData> getExportedClasses() {
		return exportedClasses;
	}

	public List<RelationshipData> getRelationshipDatas() {
		return relationshipDatas;
	}

	public List<PackageData> getExportedPackages() {
		return exportedPackages;
	}

	public List<NaryData> getExportedNary() {
		return exportedNary;
	}

	public List<NoteData> getExportedNotes() {
		return exportedNotes;
	}
	private String formatAlias(String name) {
			return name.replaceAll("[^a-zA-Z0-9]", "_");
	}


}

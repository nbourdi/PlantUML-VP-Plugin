package plugins.plantUML.export;

import java.util.*;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IContainmentUIModel;
import com.vp.plugin.model.*;

import org.antlr.v4.codegen.model.ModelElement;
import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.OperationData.Parameter;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;

import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_INITIAL_NODE;
import static com.vp.plugin.diagram.IShapeTypeConstants.SHAPE_TYPE_PACKAGE;

public class ClassDiagramExporter extends DiagramExporter {

	private IDiagramUIModel diagram;

	List<ClassData> exportedClasses = new ArrayList<>();
	List<RelationshipData> relationshipDatas = new ArrayList<>();
	List<PackageData> exportedPackages = new ArrayList<>();
	List<NaryData> exportedNary = new ArrayList<>();
	List<NoteData> exportedNotes = new ArrayList<>();


	private final List<NaryData> allExportedNary = new ArrayList<>();

	public ClassDiagramExporter(IDiagramUIModel diagram) {
		this.diagram = diagram;
	}

	@Override
	public void extract() {
		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		IDiagramElement[] packageDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_PACKAGE);
		for (IDiagramElement packageElement : packageDiagramElems) {
			String packageModelId = packageElement.getModelElement().getId();
			packageModelIds.add(packageModelId);
		}


		List<IRelationship> deferredRelationships = new ArrayList<>();

		for (IDiagramElement diagramElement : allElements) {


			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement == null) {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
				addWarning("ModelElement is null for a diagram element.");
				continue;
			}

			// Add to exported elements list
			allExportedElements.add(modelElement);

			if (modelElement instanceof IClass) {
			if (isRootLevelInDiagram(modelElement)) {
					extractClass((IClass) modelElement, null);
			}
			} else if (modelElement instanceof IPackage) {
				extractPackage((IPackage) modelElement);
			} else if (modelElement instanceof INARY) {
				if (isRootLevelInDiagram(modelElement)) {
					extractNary((INARY) modelElement, null);
				}
			} else if (modelElement instanceof INOTE) {
				extractNote((INOTE) modelElement);
			} else if (modelElement instanceof IRelationship) {
				deferredRelationships.add((IRelationship) modelElement); // Defer relationships
			} else {
				allExportedElements.remove(modelElement);
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: diagram element " + modelElement.getName()
								+ " is of unsupported type and will not be processed ... ");

				addWarning("Diagram element " + modelElement.getName()
								+ " is of unsupported type and was not processed. ");
			}
		}

		for (IDiagramElement diagramElement : allElements) {
			if (diagramElement instanceof IContainmentUIModel) {
				extractContainment((IContainmentUIModel) diagramElement);
			}
		}

		for (IRelationship relationship : deferredRelationships) {
			extractRelationship(relationship);
		}

		exportedNotes = getNotes();
	}



	private void extractClass(IClass classModel, PackageData packageData) {
		boolean isInPackage = !isRootLevelInDiagram(classModel);
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
		boolean isInPackage = !isRootLevelInDiagram(naryModel);
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

	private void extractContainment(IContainmentUIModel relationship) {
		IModelElement source = relationship.getFromShape().getModelElement();
		IModelElement target = relationship.getToShape().getModelElement();
		String sourceName = source.getName();
		String targetName = target.getName();
		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, "Containment", "");
		relationshipDatas.add(relationshipData);
	}

	private void extractRelationship(IRelationship relationship) {
		IModelElement source = relationship.getFrom();
		IModelElement target = relationship.getTo();
//		ApplicationManager.instance().getViewManager().showMessage("rel type? " + relationship.getModelType());
		if (!allExportedElements.contains(source) || !allExportedElements.contains(target)) {
			return;
		}
		if (source.getName() == null || target.getName() == null) {
			ApplicationManager.instance().getViewManager()
					.showMessage("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
			addWarning("Warning: One of the relationship's elements were null possibly due to illegal relationship (e.g. an Anchor between classes)");
		}

		String sourceName = source.getName();
		String targetName = target.getName();

		if (source instanceof INARY) {
			sourceName = getNaryAliasById(source.getId());
		} else if (source instanceof INOTE) {
			sourceName = getNoteAliasById(source.getId());
		} 
		if (target instanceof INARY) {
			targetName = getNaryAliasById(target.getId());
		} else if (target instanceof INOTE) {
			targetName = getNoteAliasById(target.getId());
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
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity, fromEnd.getAggregationKind());
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
		}

//		ApplicationManager.instance().getViewManager()
//				.showMessage("Relationship from: " + sourceName + " to: " + targetName);
//		ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());


		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(), relationship.getName());
		relationshipDatas.add(relationshipData);
	}

	private void extractPackage(IPackage packageModel) {

		if (isRootLevelInDiagram(packageModel)) {
			PackageData packageData = new PackageData(packageModel.getName(), null, null, null, false, false);
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
			addSemanticsIfExist(packageModel, packageData);
			exportedPackages.add(packageData);
		}
	}

	private void extractPackagedPackage(IPackage packageModel, PackageData parent) {

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
		addSemanticsIfExist(packageModel, packageData);
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

	private String formatAlias(String name) {
			return name.replaceAll("[^a-zA-Z0-9]", "_");
	}
}

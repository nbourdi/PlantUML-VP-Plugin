package plugins.plantUML.export;

import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IRelationship;

import plugins.plantUML.export.writers.ClassUMLWriter;
import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.SemanticsData;
import plugins.plantUML.models.SubDiagramData;
import plugins.plantUML.models.OperationData.Parameter;

import com.vp.plugin.model.IParameter;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.Package;

public class ClassDiagramExporter extends DiagramExporter {

	private IDiagramUIModel diagram;

	List<ClassData> exportedClasses = new ArrayList<>();
	List<RelationshipData> relationshipDatas = new ArrayList<>();
	List<PackageData> exportedPackages = new ArrayList<>();
	List<NaryData> exportedNary = new ArrayList<>();
	List<NoteData> exportedNotes = new ArrayList<>();
	// List<SemanticsData> exportedSemantics = new ArrayList<SemanticsData>();

	public ClassDiagramExporter(IDiagramUIModel diagram) throws IOException {
		this.diagram = diagram;
	}

	public void extract() {

		IDiagramElement[] allElements = diagram.toDiagramElementArray();

		for (IDiagramElement diagramElement : allElements) {
			IModelElement modelElement = diagramElement.getModelElement();

			if (modelElement != null) {

				if (modelElement instanceof IClass) {
					if (!(modelElement.getParent() instanceof IPackage))
						extractClass((IClass) modelElement, null);
				} else if (modelElement instanceof IRelationship) {
					extractRelationship((IRelationship) modelElement);
				} else if (modelElement instanceof IPackage) {
					extractPackage((IPackage) modelElement);
				} else if (modelElement instanceof INARY) {
					if (!(modelElement.getParent() instanceof IPackage))
						extractNary((INARY) modelElement, null);
				} else if (modelElement instanceof INOTE) {
					this.extractNote((INOTE) modelElement);
				} else {
					ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element "
							+ modelElement.getName() + " is of unsupported type and will not be processed ... ");
				}
			} else {
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: modelElement is null for a diagram element.");
			}
		}
		exportedNotes = getNotes(); // from base diagram exporter

	}

	private void extractClass(IClass classModel, PackageData packageData) {
		boolean isInPackage = (classModel.getParent() instanceof IPackage);
		ClassData classData = new ClassData(classModel.getName(), classModel.isAbstract(), classModel.getVisibility(),
				isInPackage, classModel.getDescriptionWithReferenceModels());
		classData.setStereotypes(extractStereotypes(classModel));
		extractAttributes(classModel, classData);
		extractOperations(classModel, classData);

//		List<plugins.plantUML.models.Reference> references = extractReferences((IHasChildrenBaseModelElement) classModel); // TODO: clean up
//		List<SubDiagramData> subdiagrams = extractSubdiagrams(classModel);

//		// cluttered : extractSemantics move to base diagram exporter, semantics data move to base element (need create?)
//		boolean hasSemantics = false;
//		if (!references.isEmpty() && references != null)
//			hasSemantics = true;
//			classData.getSemantics().setReferences(references);
//		if (!subdiagrams.isEmpty() && references != null)
//			hasSemantics = true;
//			classData.getSemantics().setSubDiagrams(subdiagrams);
//			
//		if(hasSemantics) {
//			classData.getSemantics().setOwnerName(classData.getName());
//			classData.getSemantics().setDescription(classData.getDescription());
//		}
//		

		SemanticsData semantics = extractSemantics(classModel);

		if (semantics != null) {
			classData.setSemantics(extractSemantics(classModel));
			getExportedSemantics().add(classData.getSemantics());
		}
		
		exportedClasses.add(classData);
		if (packageData != null)
			packageData.getClasses().add(classData);
	}

	private void extractNary(INARY naryModel, PackageData packageData) {
		boolean isInPackage = (naryModel.getParent() instanceof IPackage);
		String name = naryModel.getName();
		String id = naryModel.getId();
		NaryData naryData = new NaryData(name, id, isInPackage);
		exportedNary.add(naryData);
		if (packageData != null)
			packageData.getNaries().add(naryData);

	}

	private String getNaryAliasById(String naryId) {
		for (NaryData naryData : exportedNary) {
			if (naryData.getId().equals(naryId)) {
				return naryData.getAlias();
			}
		}
		return null;
	}

	private void extractRelationship(IRelationship relationship) {
		IModelElement source = (IModelElement) relationship.getFrom();
		IModelElement target = (IModelElement) relationship.getTo();

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

			String fromEndMultiplicity = fromEnd.getMultiplicity().equals("Unspecified") ? ""
					: fromEnd.getMultiplicity();
			String toEndMultiplicity = toEnd.getMultiplicity().equals("Unspecified") ? "" : toEnd.getMultiplicity();

			AssociationData associationData = new AssociationData(sourceName, targetName, relationship.getModelType(),
					relationship.getName(), fromEndMultiplicity, toEndMultiplicity,
					// fromEnd.getNavigable() == 0,
					toEnd.getNavigable() == 0, fromEnd.getAggregationKind());

			relationshipDatas.add(associationData);
			return;
		}

		ApplicationManager.instance().getViewManager()
				.showMessage("Relationship from: " + sourceName + " to: " + targetName);
		ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());
		ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());

		RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(),
				relationship.getName());
		relationshipDatas.add(relationshipData);
	}

	private void extractPackage(IPackage packageModel) {

		if (!(packageModel.getParent() instanceof IPackage)) {
			PackageData packageData = new PackageData(packageModel.getName(), null, null, null, false, false);
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
			exportedPackages.add(packageData);
		}
	}

	private void extractPackagedPackage(IPackage packageModel, PackageData parent) {
		ApplicationManager.instance().getViewManager().showMessage("Extracting package: " + packageModel.getName());

		PackageData packageData = new PackageData(packageModel.getName(), null, null, null, true, false);
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
}

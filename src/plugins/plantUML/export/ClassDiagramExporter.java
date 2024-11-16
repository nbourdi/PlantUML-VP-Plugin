package plugins.plantUML.export;

import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IRelationship;

import plugins.plantUML.export.models.AssociationData;
import plugins.plantUML.export.models.AttributeData;
import plugins.plantUML.export.models.ClassData;
import plugins.plantUML.export.models.NaryData;
import plugins.plantUML.export.models.NoteData;
import plugins.plantUML.export.models.OperationData;
import plugins.plantUML.export.models.PackageData;
import plugins.plantUML.export.models.RelationshipData;
import plugins.plantUML.export.models.OperationData.Parameter;

import com.vp.plugin.model.IParameter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.Package;

public class ClassDiagramExporter extends DiagramExporter {

    private File file;

    public ClassDiagramExporter(File file) {
        this.file = file;
    }
    List<ClassData> exportedClasses = new ArrayList<>();
    List<RelationshipData> relationshipDatas = new ArrayList<>();
    List<PackageData> exportedPackages = new ArrayList<>();
    List<NaryData> exportedNary = new ArrayList<>();
    List<NoteData> exportedNotes = new ArrayList<>(); 

    public void extract(IDiagramUIModel diagram) {


        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement != null) {

                if (modelElement instanceof IClass) {
                	if (!(modelElement.getParent() instanceof IPackage)) extractClass((IClass) modelElement, null);
                } else if (modelElement instanceof IRelationship) {
                    extractRelationship((IRelationship) modelElement);
                } else if (modelElement instanceof IPackage) {
                    extractPackage((IPackage) modelElement);
                } else if (modelElement instanceof INARY) {
                	if (!(modelElement.getParent() instanceof IPackage)) extractNary((INARY) modelElement, null);
                } else if (modelElement instanceof INOTE) {
                	this.extractNote((INOTE) modelElement);
                } else {
                	ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element " + modelElement.getName() + 
                			" is UNSUPPORTED and will not be processed ... .");
                }
                
            } else {
                ApplicationManager.instance().getViewManager().showMessage("Warning: modelElement is null for a diagram element.");
            }
        }
        List<NoteData> exportedNotes = getNotes(); // from base diagram exporter

        ClassUMLWriter writer = new ClassUMLWriter(exportedClasses, relationshipDatas, exportedPackages, exportedNary, exportedNotes);
        try {
            writer.writeToFile(file);
        } catch (IOException e) {
            ApplicationManager.instance().getViewManager().showMessage("Failed to write PlantUML file: " + e.getMessage());
        }
    }
    
     

    private void extractNary(INARY naryModel, PackageData packageData) {
        boolean isInPackage = (naryModel.getParent() instanceof IPackage);
        String name = naryModel.getName();
        String id = naryModel.getId();
        NaryData naryData = new NaryData(name, id, isInPackage);
        exportedNary.add(naryData);
        if (packageData != null) packageData.getNaries().add(naryData);
        ApplicationManager.instance().getViewManager().showMessage(
            "Extracted n-ary relationship: " + naryData.getAlias());
        
    }


	private void extractClass(IClass classModel, PackageData packageData) {
		boolean isInPackage = (classModel.getParent() instanceof IPackage);
    	ClassData classData = new ClassData(classModel.getName(), classModel.isAbstract(), isInPackage);
        classData.setStereotypes(extractStereotypes(classModel)); 
        extractAttributes(classModel, classData);
        extractOperations(classModel, classData);
        exportedClasses.add(classData);
        if (packageData != null) packageData.getClasses().add(classData);
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
        }
        if (target instanceof INARY) {
            targetName = getNaryAliasById(((INARY) target).getId());
        }
        if (source instanceof INOTE) {
            sourceName = getNoteAliasById(((INOTE) source).getId());
        }
        if (target instanceof INOTE) {
            targetName = getNoteAliasById(((INOTE) target).getId());
        }
        
        if (relationship instanceof IAssociation) {
        	IAssociation association = (IAssociation) relationship;
        	
        	IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
            IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();

            String fromEndMultiplicity = fromEnd.getMultiplicity().equals("Unspecified") ? "" : fromEnd.getMultiplicity();
            String toEndMultiplicity = toEnd.getMultiplicity().equals("Unspecified") ? "" : toEnd.getMultiplicity();
            
            AssociationData associationData = new AssociationData(
            		sourceName,
                    targetName,
                    relationship.getModelType(),
                    relationship.getName(),
                    fromEndMultiplicity,
                    toEndMultiplicity,
                    fromEnd.getNavigable() == 0, 
                    toEnd.getNavigable() == 0, 
                    fromEnd.getAggregationKind(),
                    toEnd.getAggregationKind()
            );

            relationshipDatas.add(associationData);
        	return;
        }

        ApplicationManager.instance().getViewManager().showMessage("Relationship from: " + sourceName + " to: " + targetName);
        ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());
        ApplicationManager.instance().getViewManager().showMessage("Relationship type: " + relationship.getModelType());

        RelationshipData relationshipData = new RelationshipData(
                sourceName,
                targetName,
                relationship.getModelType(),
                relationship.getName()
        );
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
            AttributeData attr = new AttributeData(attribute.getVisibility(), attribute.getName(), attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

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
                Parameter paramData = new Parameter(parameter.getName(), parameter.getTypeAsString(), parameter.getDefaultValueAsString());
                op.addParameter(paramData);
            }

            classData.addOperation(op);
        }
    }
}

package plugins.plantUML.export;

import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IConnectorUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INARY;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IStereotype;
import com.vp.vpuml.plugin.umlpluginmodel.ModelElement;

import plugins.plantUML.export.models.AttributeData;
import plugins.plantUML.export.models.ClassData;
import plugins.plantUML.export.models.NaryData;
import plugins.plantUML.export.models.OperationData;
import plugins.plantUML.export.models.OperationData.Parameter;
import plugins.plantUML.export.models.PackageData;
import plugins.plantUML.export.models.RelationshipData;

import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IReference;

import java.io.File;
import java.io.IOException;
import java.sql.ParameterMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class ClassDiagramExporter extends DiagramExporter {

    private File file;

    public ClassDiagramExporter(File file) {
        this.file = file;
    }

    public void extract(IDiagramUIModel diagram) {
        List<ClassData> exportedClasses = new ArrayList<>();
        List<RelationshipData> relationshipDatas = new ArrayList<>();
        List<PackageData> exportedPackages = new ArrayList<>();
        List<NaryData> exportedNary = new ArrayList<>();

        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement != null) {
//                ApplicationManager.instance().getViewManager().showMessage("NAME: " + modelElement.getName());
//                ApplicationManager.instance().getViewManager().showMessage("type: " + modelElement.getModelType());

                if (modelElement instanceof IClass) {
                    extractClass((IClass) modelElement, exportedClasses);
                } else if (modelElement instanceof IRelationship) {
                    extractRelationship((IRelationship) modelElement, relationshipDatas, exportedNary);
                } else if (modelElement instanceof IPackage) {
                    extractPackage((IPackage) modelElement, exportedClasses, relationshipDatas, exportedPackages, exportedNary);
                } else if (modelElement instanceof INARY) {
                	extractNary((INARY) modelElement, exportedNary);
                }
                
            } else {
                ApplicationManager.instance().getViewManager().showMessage("Warning: modelElement is null for a diagram element.");
            }
        }

        PlantUMLWriter writer = new PlantUMLWriter(exportedClasses, relationshipDatas, exportedPackages, exportedNary);
        try {
            writer.writeToFile(file);
        } catch (IOException e) {
            ApplicationManager.instance().getViewManager().showMessage("Failed to write PlantUML file: " + e.getMessage());
        }
    }
    
     

    private void extractNary(INARY naryModel, List<NaryData> exportedNary) {
        boolean isInPackage = (naryModel.getParent() instanceof IPackage);
        if (!isInPackage) { 
            String name = naryModel.getName();
            String id = naryModel.getId();
            NaryData naryData = new NaryData(name, id, isInPackage);
            exportedNary.add(naryData);
            
            ApplicationManager.instance().getViewManager().showMessage(
                "Extracted n-ary relationship: " + naryData.getAlias());
        }
    }


	private void extractClass(IClass classModel, List<ClassData> exportedClasses) {
    	boolean isInPackage = false;
    	if  (classModel.getParent() instanceof IPackage) {
    		ApplicationManager.instance().getViewManager().showMessage("in package detected..");
    		isInPackage = true;// we want it to be extracted in its package to avoid double extraction
    	} else {
	    	ClassData classData = new ClassData(classModel.getName(), classModel.isAbstract(), isInPackage);
	        extractStereotypes(classModel, classData);
	        extractAttributes(classModel, classData);
	        extractOperations(classModel, classData);
	        exportedClasses.add(classData);
    	}
    	
    }
    
    private void extractPackagedClass(IClass classModel, List<ClassData> exportedClasses, PackageData packageData) {
    	boolean isInPackage = true;

    	ClassData classData = new ClassData(classModel.getName(), classModel.isAbstract(), isInPackage);
        extractStereotypes(classModel, classData);
        extractAttributes(classModel, classData);
        extractOperations(classModel, classData);
        packageData.getClasses().add(classData);
        exportedClasses.add(classData);
    }

    private String getNaryAliasById(String naryId, List<NaryData> exportedNary) {
        for (NaryData naryData : exportedNary) {
            if (naryData.getId().equals(naryId)) {
                return naryData.getAlias();
            }
        }
        return null;  
    }


    private void extractRelationship(IRelationship relationship, List<RelationshipData> relationshipDatas, List<NaryData> exportedNary) {
        IModelElement source = (IModelElement) relationship.getFrom();
        IModelElement target = (IModelElement) relationship.getTo();

        
        String sourceName = source.getName();
        String targetName = target.getName();

        
        if (source instanceof INARY) {
            sourceName = getNaryAliasById(((INARY) source).getId(), exportedNary);
        }
        if (target instanceof INARY) {
            targetName = getNaryAliasById(((INARY) target).getId(), exportedNary);
        }
        
        ApplicationManager.instance().getViewManager().showMessage( " TOCLASS ================================"+ relationship.getClass().getSimpleName());
        if (relationship instanceof IAssociation) {
        	IAssociation association = (IAssociation) relationship;
        	
        	extractAssociation(association);
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


    private void extractAssociation(IAssociation association) {
		// should create relationship
    	// should add to extracted relationships. or associations??
    	
	}

	private void extractPackage(IPackage packageModel, List<ClassData> exportedClasses, List<RelationshipData> relationshipDatas, List<PackageData> exportedPackages, List<NaryData> exportedNaries) {
        
        if (!(packageModel.getParent() instanceof IPackage)) {
	        PackageData packageData = new PackageData(packageModel.getName(), null, null, null, false);
	        IModelElement[] childElements = packageModel.toChildArray();
	        for (IModelElement childElement : childElements) {
	            if (childElement instanceof IClass) {
	                extractPackagedClass((IClass) childElement, exportedClasses, packageData);
	          
	            } else if (childElement instanceof INARY) {
	            	extractPackagedNary((INARY) childElement, exportedNaries);
	            } else if (childElement instanceof IPackage) {
	            	PackageData parent = packageData;
	                extractPackagedPackage((IPackage) childElement, exportedClasses, relationshipDatas, exportedPackages, parent);
	                
	            }
	        }
	        exportedPackages.add(packageData);
        }
    }

    private void extractPackagedNary(INARY childElement, List<NaryData> exportedNaries) {
		// TODO Auto-generated method stub
		
	}

	private void extractPackagedPackage(IPackage packageModel, List<ClassData> exportedClasses,
			List<RelationshipData> relationshipDatas, List<PackageData> exportedPackages, PackageData parent) {
        ApplicationManager.instance().getViewManager().showMessage("Extracting package: " + packageModel.getName());
        
        PackageData packageData = new PackageData(packageModel.getName(), null, null, null, true);
        IModelElement[] childElements = packageModel.toChildArray();
        for (IModelElement childElement : childElements) {
            if (childElement instanceof IClass) {
                extractPackagedClass((IClass) childElement, exportedClasses, packageData);
          
            } else if (childElement instanceof IPackage) {
                extractPackagedPackage((IPackage) childElement, exportedClasses, relationshipDatas, exportedPackages, packageData);

            }
        }
        parent.getSubPackages().add(packageData);
        exportedPackages.add(packageData);
    }
	

	private void extractStereotypes(IClass classModel, ClassData classData) {
        Iterator stereoIter = classModel.stereotypeIterator();
        while (stereoIter.hasNext()) {
            String stereotype = (String) stereoIter.next();
            ApplicationManager.instance().getViewManager().showMessage("Stereotype: " + stereotype);
            classData.addStereotype(stereotype);
        }
    }

    private void extractAttributes(IClass classModel, ClassData classData) {
        Iterator attributeIter = classModel.attributeIterator();
        while (attributeIter.hasNext()) {
            IAttribute attribute = (IAttribute) attributeIter.next();
            AttributeData attr = new AttributeData(attribute.getVisibility(), attribute.getName(), attribute.getTypeAsString(), attribute.getInitialValueAsString(), attribute.getScope());

            ApplicationManager.instance().getViewManager().showMessage("Attribute: " + attribute.getName() + attribute.getTypeAsString());

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

            // ApplicationManager.instance().getViewManager().showMessage("Operation: " + op);
            classData.addOperation(op);
        }
    }
}

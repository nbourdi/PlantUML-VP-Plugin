package plugins.plantUML.export;

import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IActor;
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
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ISystem;
import com.vp.plugin.model.IUseCase;

import javassist.expr.Instanceof;
import plugins.plantUML.export.writers.UseCaseWriter;
import plugins.plantUML.models.ActorData;
import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.UseCaseData;
import plugins.plantUML.models.OperationData.Parameter;

import com.vp.plugin.model.IParameter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UseCaseDiagramExporter extends DiagramExporter {

    private File file;
    private IDiagramUIModel diagram;

    public UseCaseDiagramExporter(IDiagramUIModel diagram) {
        this.diagram = diagram;
    }
    
    List<ActorData> exportedActors = new ArrayList<>();
    
    List<RelationshipData> relationshipDatas = new ArrayList<>();
    List<PackageData> exportedPackages = new ArrayList<>();
    List<NoteData> exportedNotes = new ArrayList<>(); 
    List<UseCaseData> exportedUseCases = new ArrayList<>();

    public void extract() {


        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement != null) {
//                ApplicationManager.instance().getViewManager().showMessage("NAME: " + modelElement.getName());
//                ApplicationManager.instance().getViewManager().showMessage("type: " + modelElement.getModelType());

                if (modelElement instanceof IActor) {
                    extractActor((IActor) modelElement, null);
                } else if (modelElement instanceof IUseCase) {
                    extractUseCase((IUseCase) modelElement, null);
                } else if (modelElement instanceof IRelationship) {
                    extractRelationship((IRelationship) modelElement);
                } else if (modelElement instanceof IPackage || modelElement instanceof ISystem) {
                    extractPackage(modelElement);
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

        UseCaseWriter writer = new UseCaseWriter(exportedUseCases, relationshipDatas, exportedPackages, exportedActors, exportedNotes);
        try {
            writer.writeToFile(file);
        } catch (IOException e) {
            ApplicationManager.instance().getViewManager().showMessage("Failed to write PlantUML file: " + e.getMessage());
        }
    }


	private void extractUseCase(IUseCase modelElement, PackageData packageData) {
    	boolean isInPackage = (modelElement.getParent() instanceof IPackage);
    	String name = modelElement.getName();
    	boolean isBusiness = modelElement.isBusinessModel();
		UseCaseData useCaseData = new UseCaseData(name);
		useCaseData.setInPackage(isInPackage);
		useCaseData.setStereotypes(extractStereotypes(modelElement));
		useCaseData.setBusiness(isBusiness);
		exportedUseCases.add(useCaseData);
		if (packageData != null) packageData.getUseCases().add(useCaseData);
	}


	private void extractActor(IActor modelElement, PackageData packageData) {
		boolean isInPackage = (modelElement.getParent() instanceof IPackage);
    	String name = modelElement.getName();
    	ActorData actorData = new ActorData(name, null);
    	actorData.setInPackage(isInPackage);
    	actorData.setStereotypes(extractStereotypes(modelElement));
    	exportedActors.add(actorData);
    	if (packageData != null) packageData.getActors().add(actorData);
	}


    private void extractRelationship(IRelationship relationship) {
        IModelElement source = (IModelElement) relationship.getFrom();
        IModelElement target = (IModelElement) relationship.getTo();
        
        String sourceName = source.getName();
        String targetName = target.getName();

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
                    // fromEnd.getNavigable() == 0, 
                    toEnd.getNavigable() == 0, 
                    fromEnd.getAggregationKind()
                   // toEnd.getAggregationKind()
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



	private void extractPackage(IModelElement modelElement) {
        
        if (!(modelElement.getParent() instanceof IPackage || modelElement.getParent() instanceof ISystem)) {
	        PackageData packageData = new PackageData(modelElement.getName(), null, null, null, null, false, modelElement instanceof ISystem);
	        IModelElement[] childElements = modelElement.toChildArray();
	        for (IModelElement childElement : childElements) {
	            if (childElement instanceof IActor) {
	               extractActor((IActor) childElement, packageData);
	            } else if (childElement instanceof IUseCase) {
	            	extractUseCase((IUseCase) childElement, packageData);
	            } else if (childElement instanceof IPackage || childElement instanceof ISystem) {
	            	PackageData parent = packageData;
	                extractPackagedPackage(childElement, parent);
	                
	            }
	        }
	        exportedPackages.add(packageData);
        }
    }

	private void extractPackagedPackage(IModelElement childElement, PackageData parent) {
        ApplicationManager.instance().getViewManager().showMessage("Extracting package: " + childElement.getName());
        
        PackageData packageData = new PackageData(childElement.getName(), null, null, null, null, true, childElement instanceof ISystem);
        IModelElement[] childElements = childElement.toChildArray();
        for (IModelElement childElement1 : childElements) {
        	if (childElement1 instanceof IActor) {
	               extractActor((IActor) childElement1, packageData);
	        } else if (childElement1 instanceof IUseCase) {
	            	extractUseCase((IUseCase) childElement1, packageData);
            } else if (childElement1 instanceof IPackage || childElement1 instanceof ISystem) {
                extractPackagedPackage(childElement1, packageData);
            }
        }
        parent.getSubPackages().add(packageData);
        exportedPackages.add(packageData);
    }


	public List<UseCaseData> getExportedUseCases() {
		return exportedUseCases;
	}


	public List<RelationshipData> getExportedRelationships() {
		return relationshipDatas;
	}


	public List<PackageData> getExportedPackages() {
		return exportedPackages;
	}


	public List<ActorData> getExportedActors() {
		return exportedActors;
	}
	
}

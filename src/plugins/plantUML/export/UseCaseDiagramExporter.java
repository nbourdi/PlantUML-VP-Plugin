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
import com.vp.plugin.model.IUseCase;

import plugins.plantUML.export.models.ActorData;
import plugins.plantUML.export.models.AssociationData;
import plugins.plantUML.export.models.AttributeData;
import plugins.plantUML.export.models.ClassData;
import plugins.plantUML.export.models.NaryData;
import plugins.plantUML.export.models.NoteData;
import plugins.plantUML.export.models.OperationData;
import plugins.plantUML.export.models.PackageData;
import plugins.plantUML.export.models.RelationshipData;
import plugins.plantUML.export.models.UseCaseData;
import plugins.plantUML.export.models.OperationData.Parameter;

import com.vp.plugin.model.IParameter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UseCaseDiagramExporter extends DiagramExporter {

    private File file;

    public UseCaseDiagramExporter(File file) {
        this.file = file;
    }
    
    List<ActorData> exportedActors = new ArrayList<>();
    
    List<RelationshipData> relationshipDatas = new ArrayList<>();
    List<PackageData> exportedPackages = new ArrayList<>();
    List<NoteData> exportedNotes = new ArrayList<>(); 
    List<UseCaseData> exportedUseCases = new ArrayList<>();

    public void extract(IDiagramUIModel diagram) {


        IDiagramElement[] allElements = diagram.toDiagramElementArray();

        for (IDiagramElement diagramElement : allElements) {
            IModelElement modelElement = diagramElement.getModelElement();

            if (modelElement != null) {
//                ApplicationManager.instance().getViewManager().showMessage("NAME: " + modelElement.getName());
//                ApplicationManager.instance().getViewManager().showMessage("type: " + modelElement.getModelType());

                if (modelElement instanceof IActor) {
                    extractActor((IActor) modelElement);
                } else if (modelElement instanceof IUseCase) {
                    extractUseCase((IUseCase) modelElement);
                } else if (modelElement instanceof IRelationship) {
                    extractRelationship((IRelationship) modelElement);
                } else if (modelElement instanceof IPackage) {
                    extractPackage((IPackage) modelElement);
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
    
     

    private void extractUseCase(IUseCase modelElement) {
    	String name = modelElement.getName();
		UseCaseData useCaseData = new UseCaseData(name);
	}



	private void extractActor(IActor modelElement) {
    	String name = modelElement.getName();
    	ActorData actorData = new ActorData(name, null);
    	actorData.setStereotypes(extractStereotypes(modelElement));
    	exportedActors.add(actorData);
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
        
		// TODO:: 
        if (!(packageModel.getParent() instanceof IPackage)) {
	        PackageData packageData = new PackageData(packageModel.getName(), null, null, null, false);
	        IModelElement[] childElements = packageModel.toChildArray();
	        for (IModelElement childElement : childElements) {
	            if (childElement instanceof IActor) {
	               
	          
	            } else if (childElement instanceof IUseCase) {
	            	
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
        
        PackageData packageData = new PackageData(packageModel.getName(), null, null, null, true);
        IModelElement[] childElements = packageModel.toChildArray();
        for (IModelElement childElement : childElements) {
            if (childElement instanceof IActor) {
                
          
            } else if (childElement instanceof IPackage) {
                extractPackagedPackage((IPackage) childElement, packageData);
            }
        }
        parent.getSubPackages().add(packageData);
        exportedPackages.add(packageData);
    }
	

//	private void extractStereotypes(IActor actorModel, ActorData actorData) {
//        Iterator stereoIter = actorModel.stereotypeIterator();
//        while (stereoIter.hasNext()) {
//            String stereotype = (String) stereoIter.next();
//            ApplicationManager.instance().getViewManager().showMessage("Stereotype: " + stereotype);
//            actorData.addStereotype(stereotype);
//        }
//    }
	
	

}

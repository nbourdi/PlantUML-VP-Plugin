package plugins.plantUML.export;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IActor;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.INOTE;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.ISystem;
import com.vp.plugin.model.IUseCase;

import plugins.plantUML.models.ActorData;
import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.UseCaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.vp.plugin.diagram.IShapeTypeConstants.*;


public class UseCaseDiagramExporter extends DiagramExporter {

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


        List<IRelationship> deferredRelationships = new ArrayList<>();
        IDiagramElement[] allElements = diagram.toDiagramElementArray();


        IDiagramElement[] packageDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_PACKAGE);
        IDiagramElement[] systemDiagramElems = diagram.toDiagramElementArray(SHAPE_TYPE_SYSTEM);

        for (IDiagramElement packageElement : packageDiagramElems) {
            String packageModelId = packageElement.getModelElement().getId();
            packageModelIds.add(packageModelId);
        }
        for (IDiagramElement packageElement : systemDiagramElems) {
            String packageModelId = packageElement.getModelElement().getId();
            packageModelIds.add(packageModelId);
        }



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

            if (modelElement instanceof IActor) {
                extractActor((IActor) modelElement, null);
            } else if (modelElement instanceof IUseCase) {
                extractUseCase((IUseCase) modelElement, null);
            } else if (modelElement instanceof IRelationship) {
                deferredRelationships.add((IRelationship) modelElement);
            } else if (modelElement instanceof IPackage || modelElement instanceof ISystem) {
                extractPackage(modelElement);
            } else if (modelElement instanceof INOTE) {
                this.extractNote((INOTE) modelElement);
            } else {
                allExportedElements.remove(modelElement);
                ApplicationManager.instance().getViewManager().showMessage("Warning: diagram element " + modelElement.getName() +
                        " is UNSUPPORTED and will not be processed ... .");
                addWarning("Diagram element " + modelElement.getName() +
                        " is of unsupported type and was not processed");
            }

        }

        for (IRelationship relationship : deferredRelationships) {
            extractRelationship(relationship);
        }

        exportedNotes = getNotes();

    }


	private void extractUseCase(IUseCase modelElement, PackageData packageData) {
    	boolean isInPackage = !isRootLevelInDiagram(modelElement);
    	String name = modelElement.getName();
    	boolean isBusiness = modelElement.isBusinessModel();
		UseCaseData useCaseData = new UseCaseData(name);
		useCaseData.setInPackage(isInPackage);
        useCaseData.setDescription(modelElement.getDescription());
		useCaseData.setStereotypes(extractStereotypes(modelElement));
		useCaseData.setBusiness(isBusiness);
        addSemanticsIfExist(modelElement, useCaseData);
		exportedUseCases.add(useCaseData);
		if (packageData != null) packageData.getUseCases().add(useCaseData);
	}


	private void extractActor(IActor modelElement, PackageData packageData) {
		boolean isInPackage = !isRootLevelInDiagram(modelElement);
        boolean isBusiness = modelElement.isBusinessModel();
    	String name = modelElement.getName();
    	ActorData actorData = new ActorData(name);
    	actorData.setInPackage(isInPackage);
        actorData.setDescription(modelElement.getDescription());
    	actorData.setStereotypes(extractStereotypes(modelElement));
        actorData.setBusiness(isBusiness);
        addSemanticsIfExist(modelElement, actorData);
    	exportedActors.add(actorData);
    	if (packageData != null) packageData.getActors().add(actorData);
	}


    private void extractRelationship(IRelationship relationship) {
        IModelElement source = relationship.getFrom();
        IModelElement target = relationship.getTo();

        if (!allExportedElements.contains(source) || !allExportedElements.contains(target)) {
            return;
        }
        
        String sourceName = source.getName();
        String targetName = target.getName();

        if (source.getName() == null || target.getName() == null) {
            ApplicationManager.instance().getViewManager()
                    .showMessage("Warning: One of the relationship's " +(relationship.getName())+ " elements were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
            addWarning("One of the relationship's elements " + (relationship.getName()) + " were null possibly due to illegal relationship (e.g. Anchor between classes) or a hanging connector End");
        }

        if (source instanceof INOTE) {
            sourceName = getNoteAliasById(source.getId());
        }
        if (target instanceof INOTE) {
            targetName = getNoteAliasById(target.getId());
        }
        
        if (relationship instanceof IAssociation) {
        	IAssociation association = (IAssociation) relationship;
        	
        	IAssociationEnd fromEnd = (IAssociationEnd) association.getFromEnd();
            IAssociationEnd toEnd = (IAssociationEnd) association.getToEnd();



            String fromEndMultiplicity = Objects.toString(fromEnd.getMultiplicity(), "").equals("Unspecified") ? "" : fromEnd.getMultiplicity();
            String toEndMultiplicity = Objects.toString(toEnd.getMultiplicity(), "").equals("Unspecified") ? "" : toEnd.getMultiplicity();


            AssociationData associationData = new AssociationData(
            		sourceName,
                    targetName,
                    relationship.getModelType(),
                    relationship.getName(),
                    fromEndMultiplicity,
                    toEndMultiplicity,
                    fromEnd.getAggregationKind()
            );

            relationshipDatas.add(associationData);
        	return;
        }

        RelationshipData relationshipData = new RelationshipData(sourceName, targetName, relationship.getModelType(), relationship.getName());
        relationshipDatas.add(relationshipData);
    }


	private void extractPackage(IModelElement modelElement) {
        
        if (isRootLevelInDiagram(modelElement)) {
	        PackageData packageData = new PackageData(modelElement.getName(), null, null, null, false, modelElement instanceof ISystem);
            packageData.setDescription(modelElement.getDescription());
            IModelElement[] childElements = modelElement.toChildArray();
	        for (IModelElement childElement : childElements) {
	            if (childElement instanceof IActor) {
	               extractActor((IActor) childElement, packageData);
	            } else if (childElement instanceof IUseCase) {
	            	extractUseCase((IUseCase) childElement, packageData);
	            } else if (childElement instanceof IPackage || childElement instanceof ISystem) {
                    extractPackagedPackage(childElement, packageData);
	                
	            }
	        }
            addSemanticsIfExist(modelElement, packageData);
	        exportedPackages.add(packageData);
        }
    }

	private void extractPackagedPackage(IModelElement childElement, PackageData parent) {
//        ApplicationManager.instance().getViewManager().showMessage("Extracting package: " + childElement.getName());
        
        PackageData packageData = new PackageData(childElement.getName(), null, null, null, true, childElement instanceof ISystem);
        packageData.setDescription(childElement.getDescription());
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
        addSemanticsIfExist(childElement, packageData);
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

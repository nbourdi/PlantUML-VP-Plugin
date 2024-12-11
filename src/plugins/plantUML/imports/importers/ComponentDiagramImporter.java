package plugins.plantUML.imports.importers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jniwrapper.e;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.factory.IModelElementFactory;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;
import net.sourceforge.plantuml.decoration.symbol.USymbol;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.style.SName;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.ComponentData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.SemanticsData;
import plugins.plantUML.models.ComponentData.PortData;
import v.chx.cor;

public class ComponentDiagramImporter extends DiagramImporter {

	private DescriptionDiagram componentDiagram;

	private List<PackageData> packageDatas = new ArrayList<PackageData>(); 
	private List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
	private List<NoteData> noteDatas = new ArrayList<NoteData>();
	private List<ClassData> interfaceDatas = new ArrayList<ClassData>();
	private List<ComponentData> componentDatas = new ArrayList<ComponentData>();


	public ComponentDiagramImporter(DescriptionDiagram componentDiagram, Map<String, SemanticsData> semanticsMap) {
		super(semanticsMap);
		this.componentDiagram = componentDiagram;
	}



	public void extract() {

		for (Entity groupEntity : componentDiagram.groups()) { 

			if (groupEntity.getParentContainer().isRoot()) {
				ApplicationManager.instance().getViewManager()
				.showMessage("entity par groups in root : " +groupEntity.getName());
				extractGroup(groupEntity, componentDatas, packageDatas);
			}
		}

		for (Entity entity : componentDiagram.leafs()) {
			if (entity.getParentContainer().isRoot()) {
				ApplicationManager.instance().getViewManager()
				.showMessage("entity leafs in root : " +entity.getName());
				extractLeaf(entity, componentDatas, interfaceDatas);
			}
		}

		//		for (Link link : componentDiagram.getLinks()) {
		//			RelationshipData relationship = extractRelationship(link);
		//			if(relationship != null) 
		//				relationshipDatas.add(relationship);
		//
		//		}
	}

	private void extractLeaf(Entity entity, List<ComponentData> components, List<ClassData> interfaces) {

		// ideally leafTypes would be more specific than they are for component, workaround by getting SNames fromUSymbols

		SName sName = entity.getUSymbol().getSName();
		ApplicationManager.instance().getViewManager().showMessage("extracting leaf " + sName.toString() +" " + entity.getDisplay().toString());
		switch (sName) {
		case component:
			components.add(extractComponent(entity));
			break;

		case interface_:
			interfaces.add(extractInterface(entity));
			break;
		default:
			break;

		}
	}

	private ClassData extractInterface(Entity entity) {

		String name = removeBrackets(entity.getDisplay().toString());

		ClassData interfaceData = new ClassData(name, null, false);
		String key = name + "|Class";

		boolean hasSemantics = getSemanticsMap().containsKey(key);

		if (hasSemantics) interfaceData.setSemantics(getSemanticsMap().get(key));
		List<String> stereotypes = extractStereotypes(entity, interfaceData);

		for (String stereotype : stereotypes) {
			interfaceData.addStereotype(stereotype);
		}

		interfaceData.setUid(entity.getUid());
		return interfaceData;
	}



	private ComponentData extractComponent(Entity entity) {
		String name = removeBrackets(entity.getDisplay().toString());

		ComponentData componentData = new ComponentData(name, null, false);
		String key = name + "|Component";

		boolean hasSemantics = getSemanticsMap().containsKey(key);
		if (hasSemantics) componentData.setSemantics(getSemanticsMap().get(key));

		List<String> stereotypes = extractStereotypes(entity, componentData);

		for (String stereotype : stereotypes) {
			componentData.addStereotype(stereotype);
		}

		componentData.setUid(entity.getUid());
		
		for (Entity residentGroup : entity.groups()) {
			extractGroup(residentGroup, componentData.getResidents(), componentData.getPackages());
		}

		for (Entity residentLeaf : entity.leafs()) {

			// Ports need special handling since their getUSymbols return null for unknown reason
			if (residentLeaf.getLeafType() == LeafType.PORTIN || residentLeaf.getLeafType() == LeafType.PORTOUT) {
				String portName = removeBrackets(residentLeaf.getDisplay().toString());
				PortData portData = new PortData(portName);
				portData.setUid(residentLeaf.getUid());
				componentData.getPorts().add(portData);
			}

			else extractLeaf(residentLeaf, componentData.getResidents(), componentData.getInterfaces());
		}
		
		return componentData;
	}


	private void extractGroup(Entity groupEntity, List<ComponentData> components, List<PackageData> packages) {
		SName sName = groupEntity.getUSymbol().getSName();

		ApplicationManager.instance().getViewManager().showMessage("usymbol " + sName.toString());
		switch (sName) {
		case component:

			ComponentData componentWithResidents = extractComponent(groupEntity);
			components.add(componentWithResidents);

			break;

		case package_:

			PackageData packageData = extractPackage(groupEntity);
			packages.add(packageData);

			break;
		default:
			break;
		}

	}

	private PackageData extractPackage(Entity groupEntity) {

		String name = removeBrackets(groupEntity.getDisplay().toString());
		PackageData packageData = new PackageData(groupEntity.getName(), null, false);
		packageData.setUid(groupEntity.getUid());

		for (Entity packagedLeaf : groupEntity.leafs()) {
			extractLeaf(packagedLeaf, packageData.getComponents(), packageData.getClasses());
		}

		for (Entity subgroupEntity : groupEntity.groups()) {
			extractGroup(subgroupEntity, packageData.getComponents(), packageData.getSubPackages());
		}

		String key = name + "|Package";

		boolean hasSemantics = getSemanticsMap().containsKey(key);

		if (hasSemantics) packageData.setSemantics(getSemanticsMap().get(key));
		
		return packageData;
	}

	public List<ComponentData> getComponentDatas() {
		return componentDatas;
	}

	public List<ClassData> getInterfaceDatas() {
		return interfaceDatas;
	}

	public List<RelationshipData> getRelationshipDatas() {
		return relationshipDatas;
	}

	public List<PackageData> getPackageDatas() {
		return packageDatas;
	}

	public List<NoteData> getNotes() {
		return noteDatas;
	}
}

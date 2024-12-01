package plugins.plantUML.imports.importers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vp.plugin.ApplicationManager;

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.SemanticsData;
import plugins.plantUML.models.OperationData.Parameter;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;

public class ClassDiagramImporter extends DiagramImporter {

	private ClassDiagram classDiagram;
	private List<ClassData> classDatas = new ArrayList<ClassData>();
	private List<PackageData> packageDatas = new ArrayList<PackageData>(); 
	private List<NaryData> naryDatas = new ArrayList<NaryData>();
	private List<RelationshipData> relationshipDatas = new ArrayList<RelationshipData>();
	private List<NoteData> noteDatas = new ArrayList<NoteData>();


	public ClassDiagramImporter(ClassDiagram classDiagram, Map<String, SemanticsData> semanticsMap) {
		super(semanticsMap);
		this.classDiagram = classDiagram;
	}

	public void extract() {

		for (Entity groupEntity : classDiagram.groups()) {
			if (groupEntity.getParentContainer().isRoot()) {
				extractGroup(groupEntity);
			}
		}

		for (Entity entity : classDiagram.leafs()) {

			if (entity.getParentContainer().isRoot()) {
				extractLeaf(entity, classDatas, naryDatas);
			}
		}

		for (Link link : classDiagram.getLinks()) {
			RelationshipData relationship = extractRelationship(link);
			if(relationship != null) 
				relationshipDatas.add(relationship);

		}

	}

	private PackageData extractGroup(Entity groupEntity) {
		GroupType groupType = groupEntity.getGroupType();
		PackageData packageData = null; // TODO: 1) this is temp solution 2) not always will this be a package?

		if (groupType == GroupType.PACKAGE) {
			List<ClassData> packageClassDatas = new ArrayList<ClassData>();
			List<PackageData> packagedPackageDatas = new ArrayList<PackageData>(); 
			List<NaryData> packageNaryDatas = new ArrayList<NaryData>();


			for (Entity packagedLeaf : groupEntity.leafs()) {
				extractLeaf(packagedLeaf, packageClassDatas, packageNaryDatas);
			}


			for (Entity subgroupEntity : groupEntity.groups()) {
				packagedPackageDatas.add(extractGroup(subgroupEntity));
			}

			packageData = new PackageData(groupEntity.getName(), packageClassDatas, packagedPackageDatas, packageNaryDatas, false, false);
			packageData.setUid(groupEntity.getUid());
			if (groupEntity.getParentContainer().isRoot()) {
				packageDatas.add(packageData);
			}
		}
		return packageData;

	}

	private RelationshipData extractRelationship(Link link) {
		// TODO source and target may be anapoda an einai apo thn allh to arrow
		String sourceID;
		String targetID;

		String relationshipType = "";
		String decor1 = link.getType().getDecor1().toString();
		String decor2 = link.getType().getDecor2().toString();

		// DESIGN CONSTRAINT : double-ended relationships do not exist in VP.
		boolean isDecorated1 = (decor1 != "NONE" && decor1 != "NOT_NAVIGABLE");
		boolean isDecorated2 = (decor2 != "NONE" && decor2 != "NOT_NAVIGABLE");
		String decor = (isDecorated1 ? decor1 : decor2);
		boolean isNotNavigable = (decor1 == "NOT_NAVIGABLE" || decor2 == "NOT_NAVIGABLE");

		if (isDecorated1 && isDecorated2) {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: an unsupported type of relationship with TWO ends was found and not imported.");
			return null;
		}
		String lineStyle = link.getType().getStyle().toString();
		ApplicationManager.instance().getViewManager()
		.showMessage(lineStyle);
		boolean isReverse = (lineStyle.contains("NORMAL")); // meaning the "from" side has the decoration
		boolean isAssoc = false;

		//TODO check if theyre in the right way
		String fromEndMultiplicity = link.getLinkArg().getQuantifier1();
		String toEndMultiplicity = link.getLinkArg().getQuantifier2();
		String fromEndAggregation = "";
		if (lineStyle.contains("NORMAL")) {
			// association, containment, composition, agregation
			// TODO containment

			if (decor == "COMPOSITION") {
				relationshipType = "Composition";
				fromEndAggregation = "composite";
				isAssoc = true;
			} else if (decor == "AGREGATION") {
				relationshipType = "Aggregation";
				fromEndAggregation = "shared";
				isAssoc = true;
			} else if (!isDecorated1 && !isDecorated2) {
				relationshipType = "Simple";
				isAssoc = true;
			} else if (decor == "EXTENDS") { // TODO : arrow with solid line isnt in VP.. but means extend in puml
				relationshipType = "Generalization";
			} else if (decor == "CROWFOOT") {
				relationshipType = "Containment";
			}
		} else {
			// DASHED
			switch (decor) {
			case "EXTENDS": // |>
				relationshipType = "Realization";

				break;
			case "ARROW": // > , abstraction and all stereotypes + dependency stereotypes all look the same...
				relationshipType = "Dependency";
				break;
			case "NONE": // SHOULD ONLY BE FOR NOTES.
				relationshipType = "Anchor";
				break;

			default:
				ApplicationManager.instance().getViewManager()
				.showMessage("Warning: an unsupported type of relationship was found and not imported.");
				break;
			}
		}

		//TODO IS THIS RIGHT?
		if (isDecorated1 && !isReverse || isDecorated2 && isReverse) {
			sourceID = link.getEntity1().getUid();
			targetID = link.getEntity2().getUid();
		} else {
			sourceID = link.getEntity2().getUid();
			targetID = link.getEntity1().getUid();
		}

		if(relationshipType == "") return null; // TODO: temp fix. 

		if (isAssoc) {
			ApplicationManager.instance().getViewManager()
			.showMessage("in isAssoc ");
			AssociationData associationData = new AssociationData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()) , fromEndMultiplicity, toEndMultiplicity, !isNotNavigable, fromEndAggregation);
			associationData.setSourceID(sourceID);
			associationData.setTargetID(targetID);
			return associationData;

		} else {
			RelationshipData relationshipData = new RelationshipData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()));
			relationshipData.setSourceID(sourceID);
			relationshipData.setTargetID(targetID);
			return relationshipData;
		}



	}

	private void extractLeaf(Entity entity, List<ClassData> classes, List<NaryData> naries) {
		LeafType leafType = entity.getLeafType();
		ApplicationManager.instance().getViewManager()
		.showMessage("leaf type : "+ entity.getLeafType().toString());
		if (leafType == LeafType.CLASS || leafType == LeafType.ABSTRACT_CLASS || leafType == LeafType.ANNOTATION
				|| leafType == LeafType.STEREOTYPE || leafType == LeafType.STRUCT || leafType == LeafType.ENUM
				|| leafType == LeafType.ENTITY || leafType == LeafType.INTERFACE || leafType == LeafType.PROTOCOL
				|| leafType == LeafType.METACLASS) {
			// Create a ClassData model from the plantuml entity
			// name, isAbstract = false as it is a different leaf type, visibility = converted to string from enum,

			classes.add(extractClass(entity, leafType));		    
		} else if (leafType == leafType.STATE_CHOICE || leafType == leafType.ASSOCIATION) { 
			// TYPE DIAMOND (n-ary)
			// Due to plantUml's internal purpose being simply rendering, STATE_CHOICE (state diagram choice)
			// is also used for the diamond entity as they are displayed the same.
			// When declared as "<>" instead of "diamond", the leaf type is ASSOCIATION.
			naries.add(extractNary(entity));
		} else if (leafType == leafType.NOTE) {
			noteDatas.add(extractNote(entity));
		}

		else {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: a leaf was not imported due to unsupported type..");
		}
	}

	private NaryData extractNary(Entity entity) {
		NaryData naryData = new NaryData(entity.getName(), null, false);
		naryData.setUid(entity.getUid());
		return naryData;
	}

	private ClassData extractClass(Entity entity, LeafType classLeafType) {

		String visibility = convertVisibility(entity.getVisibilityModifier());
		ClassData classData = new ClassData(entity.getDisplay().toString(), false, visibility, false, null);
		String rawStereotypes = entity.getStereotype() == null ? "" :  entity.getStereotype().toString(); // in a single string like <<Stereo1>><<stereo2>>
		
		// TODO : get display or get name.... might cause trouble
		String key = entity.getDisplay() + "|Class";
		
		boolean hasSemantics = getSemanticsMap().containsKey(key);
		
		if (hasSemantics) classData.setSemantics(getSemanticsMap().get(key));

		Pattern pattern = Pattern.compile("<<([^>]+)>>");
		List<String> stereotypes = new ArrayList<>();
		Matcher matcher = pattern.matcher(rawStereotypes);
		while (matcher.find()) {
			stereotypes.add(matcher.group(1)); // group(1) gets the part inside << >>
		}

		Map<LeafType, String> stereotypeMap = new HashMap<>();
		stereotypeMap.put(LeafType.ABSTRACT_CLASS, "Abstract");
		stereotypeMap.put(LeafType.INTERFACE, "Interface");
		stereotypeMap.put(LeafType.ENUM, "Enum");
		stereotypeMap.put(LeafType.ANNOTATION, "Annotation");
		stereotypeMap.put(LeafType.METACLASS, "metaclass");
		stereotypeMap.put(LeafType.STEREOTYPE, "Stereotype");
		stereotypeMap.put(LeafType.ENTITY, "Entity");
		stereotypeMap.put(LeafType.EXCEPTION, "Exception");
		stereotypeMap.put(LeafType.PROTOCOL, "Protocol");
		stereotypeMap.put(LeafType.STRUCT, "Struct");

		String leafTypeStereo = stereotypeMap.get(classLeafType);
		if (leafTypeStereo != null) {
			if (classLeafType == LeafType.ABSTRACT_CLASS) {
				classData.setAbstract(true);
			} else {
				classData.addStereotype(leafTypeStereo);
			}
		}

		for (String stereotype : stereotypes) {
			classData.addStereotype(stereotype);
		}

		List<String> basicTypes = Arrays.asList("int", "char", "string", "boolean", "float", "double", "void", "short",
				"byte");
		// fields:: MOST OF THIS IS CONVENTION BASED, NOT SOLIDLY DEFINED SYNTAX
		for (CharSequence cs : entity.getBodier().getFieldsToDisplay()) {
			final Member m = (Member) cs;
			final VisibilityModifier fieldModifier = m.getVisibilityModifier();
			String attrVisibility = convertVisibility(fieldModifier);
			String scope = m.isStatic() ? "classifier" : "instance";
			String typesPattern = String.join("|", basicTypes);
			Pattern pattern1 = Pattern.compile("\\b(" + typesPattern + ")\\b");
			Matcher matcher1 = pattern1.matcher(m.getDisplay(false));
			String detectedType = "";

			if (matcher1.find()) {
				detectedType = matcher1.group(1);
			}
			String attributeName = m.getDisplay(false).replaceAll("\\b" + detectedType + "\\b", "").replaceAll(":", "")
					.trim();

			Pattern valuePattern = Pattern.compile("=\\s*(.*)");
			Matcher valueMatcher = valuePattern.matcher(attributeName);

			String detectedValue = null;

			if (valueMatcher.find())
				detectedValue = valueMatcher.group(1); // Capture the value after '='

			if (detectedValue != null)
				attributeName = attributeName.replaceAll("=\\s*" + Pattern.quote(detectedValue), "").trim();
			AttributeData attributeData = new AttributeData(attrVisibility, attributeName, detectedType, detectedValue,
					scope);
			classData.addAttribute(attributeData);
		}

		for (CharSequence cs : entity.getBodier().getMethodsToDisplay()) {
			final Member m = (Member) cs;
			final VisibilityModifier methodModifier = m.getVisibilityModifier();
			String opVisibility = convertVisibility(methodModifier);

			String scope = m.isStatic() ? "classifier" : "instance";
			String typesPattern = String.join("|", basicTypes);
			Pattern returnTypePattern = Pattern.compile("\\b(" + typesPattern + ")\\b(?=\\s+\\w+\\()");
			Matcher returnTypeMatcher = returnTypePattern.matcher(m.getDisplay(false));
			String detectedReturnType = "";

			if (returnTypeMatcher.find()) {
				detectedReturnType = returnTypeMatcher.group(1);
			}

			Pattern paramPattern = Pattern.compile("\\((.*?)\\)"); // parentheses
			Matcher paramMatcher = paramPattern.matcher(m.getDisplay(false));

			StringBuilder paramsStr = new StringBuilder();
			if (paramMatcher.find()) {
				paramsStr.append(paramMatcher.group(1));
			}
			List<Parameter> parameters = new ArrayList<>();
			// split parameters by commas
			String[] paramStrings = paramsStr.toString().split(",");
			for (String param : paramStrings) {
				param = param.trim();
				String paramType = null;
				String paramName = null;
				String paramValue = null;

				String[] parts = param.split("\\s+");
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i];

					if (basicTypes.contains(part)) {
						paramType = part;
					} else if (i == parts.length - 1 && part.contains("=")) {
						String[] nameValue = part.split("=");
						paramName = nameValue[0].trim();
						paramValue = nameValue[1].trim();
					} else {
						paramName = part;
					}
				}
				if (paramName != null && !paramName.isEmpty()) {
					Parameter parameter = new Parameter(paramName, paramType, paramValue);
					parameters.add(parameter);
				}
			}
			String opName = m.getDisplay(false).replaceFirst("\\b" + detectedReturnType + "\\b", "")
					.replaceAll("\\(.*\\)", "").trim();
			OperationData operationData = new OperationData(opVisibility, opName, detectedReturnType, m.isAbstract(),
					parameters, scope);
			classData.addOperation(operationData);
		}
		classData.setUid(entity.getUid());
		return classData;
	}

	public List<ClassData> getClassDatas() {
		return classDatas;
	}
	public List<PackageData> getPackageDatas() {
		return packageDatas;
	}

	public List<NaryData> getNaryDatas() {
		return naryDatas;
	}

	public List<RelationshipData> getRelationshipDatas() {
		return relationshipDatas;
	}

	public List<NoteData> getNoteDatas() {
		return noteDatas;
	}
}

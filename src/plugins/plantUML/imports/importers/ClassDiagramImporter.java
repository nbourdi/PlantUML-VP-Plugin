package plugins.plantUML.imports.importers;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vp.plugin.ApplicationManager;

import javassist.expr.NewArray;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.decoration.LinkType;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.wire.WLinkType;
import org.apache.tools.ant.taskdefs.Pack;
import plugins.plantUML.models.AssociationData;
import plugins.plantUML.models.AssociationPoint;
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

	private List<AssociationPoint> associationPoints = new ArrayList<AssociationPoint>();

	private Map<String, AssociationPoint> assocPointMap = new HashMap<String, AssociationPoint>();


	public ClassDiagramImporter(ClassDiagram classDiagram, Map<String, SemanticsData> semanticsMap) {
		super(semanticsMap);
		this.classDiagram = classDiagram;
	}

	@Override
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
        PackageData packageData = null;

        if (groupType == GroupType.PACKAGE) {
            List<ClassData> packageClassDatas = new ArrayList<ClassData>();
            List<PackageData> packagedPackageDatas = new ArrayList<PackageData>();
            List<NaryData> packageNaryDatas = new ArrayList<NaryData>();

            for (Entity packagedLeaf : groupEntity.leafs())
                extractLeaf(packagedLeaf, packageClassDatas, packageNaryDatas);

            for (Entity subgroupEntity : groupEntity.groups())
                packagedPackageDatas.add(extractGroup(subgroupEntity));

            String name = removeBrackets(groupEntity.getDisplay().toString());
            packageData = new PackageData(groupEntity.getName(), null, packageClassDatas, packagedPackageDatas, packageNaryDatas, false, false);
            packageData.setUid(groupEntity.getUid());

            String key = name + "|Package";

            boolean hasSemantics = getSemanticsMap().containsKey(key);

            if (hasSemantics) packageData.setSemantics(getSemanticsMap().get(key));

            if (groupEntity.getParentContainer().isRoot()) {
                packageDatas.add(packageData);
            }
        }
        return packageData;

    }

	private RelationshipData extractRelationship(Link link) {
		String sourceID;
		String targetID;

		String relationshipType = "";
		String decor1 = link.getType().getDecor1().toString();
		String decor2 = link.getType().getDecor2().toString();

		// DESIGN CONSTRAINT : double-ended relationships do not exist in VP.
		boolean isDecorated1 = (!Objects.equals(decor1, "NONE") && !Objects.equals(decor1, "NOT_NAVIGABLE"));
		boolean isDecorated2 = (!Objects.equals(decor2, "NONE") && !Objects.equals(decor2, "NOT_NAVIGABLE"));
		String decor = (isDecorated1 ? decor1 : decor2);
		boolean isNotNavigable = (Objects.equals(decor1, "NOT_NAVIGABLE") || Objects.equals(decor2, "NOT_NAVIGABLE"));

		if (isDecorated1 && isDecorated2) {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: an unsupported type of relationship with TWO ends was found and not imported.");
			return null;
		}
		String lineStyle = link.getType().getStyle().toString();

		boolean isReverse = (lineStyle.contains("NORMAL")); // meaning the "from" side has the decoration
		boolean isAssoc = false;
		boolean isAssocClassSolid = false;
		boolean isAssocClassDashed= false;

		String fromEndMultiplicity = link.getLinkArg().getQuantifier1();
		String toEndMultiplicity = link.getLinkArg().getQuantifier2();
		String fromEndAggregation = "";
		if (lineStyle.contains("NORMAL")) {
			// association, containment, composition, agregation are solid line links

			if (decor == "COMPOSITION") {
				relationshipType = "Composition";
				fromEndAggregation = "composite";
				isAssoc = true;
				if (link.getEntity1().getLeafType() == LeafType.POINT_FOR_ASSOCIATION || link.getEntity2().getLeafType() == LeafType.POINT_FOR_ASSOCIATION)
				{
					isAssocClassSolid = true;
				}
			} else if (decor == "AGREGATION") {
				relationshipType = "Aggregation";
				fromEndAggregation = "shared";
				isAssoc = true;
				if (link.getEntity1().getLeafType() == LeafType.POINT_FOR_ASSOCIATION || link.getEntity2().getLeafType() == LeafType.POINT_FOR_ASSOCIATION)
				{
					isAssocClassSolid = true;
				}
			} else if (!isDecorated1 && !isDecorated2) {
				relationshipType = "Simple";
				isAssoc = true;
				if (link.getEntity1().getLeafType() == LeafType.POINT_FOR_ASSOCIATION || link.getEntity2().getLeafType() == LeafType.POINT_FOR_ASSOCIATION)
				{
					isAssocClassSolid = true;
				}
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
			case "NONE": // ".." note anchor or assoc class
				if (link.getEntity1().getLeafType() == LeafType.POINT_FOR_ASSOCIATION || link.getEntity2().getLeafType() == LeafType.POINT_FOR_ASSOCIATION)
				{
					relationshipType = "AssociationClass";
					isAssocClassDashed = true;
				}
				else relationshipType = "Anchor"; 
				break;

			default:
				ApplicationManager.instance().getViewManager()
				.showMessage("Warning: an unsupported type of relationship was found and not imported.");
				break;
			}
		}

		if (isDecorated1 && !isReverse || isDecorated2 && isReverse) {
			sourceID = link.getEntity1().getUid();
			targetID = link.getEntity2().getUid();
		} else {
			sourceID = link.getEntity2().getUid();
			targetID = link.getEntity1().getUid();
		}

		if(relationshipType == "") return null; // TODO: temp fix. 

		if (isAssocClassSolid || isAssocClassDashed)  {

			Entity pointEntity;
			Entity otherEntity;

			if (link.getEntity1().getLeafType() == LeafType.POINT_FOR_ASSOCIATION) {
				pointEntity = link.getEntity1(); 
				otherEntity = link.getEntity2();
			} else {
				pointEntity = link.getEntity2();
				otherEntity = link.getEntity1();
			}


			AssociationPoint associationPoint = assocPointMap.get(pointEntity.getUid()); 

			if(isAssocClassDashed) { // the associationclass dashed relationship 
				associationPoint.setToUid(otherEntity.getUid());
			}
			else if (isAssocClassSolid) { // 
				if (associationPoint.getFromUid1() == null) associationPoint.setFromUid1(otherEntity.getUid());
				else {
					// uid1 has been filled so we fill in uid2 and the full VP association is ready 
					associationPoint.setFromUid2(otherEntity.getUid());
				}
			}
			

		} else if (isAssoc) {
				AssociationData associationData = new AssociationData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()) , fromEndMultiplicity, toEndMultiplicity, !isNotNavigable, fromEndAggregation);
				associationData.setSourceID(sourceID);
				associationData.setTargetID(targetID);

				return associationData;
		}
		else {
			RelationshipData relationshipData = new RelationshipData(link.getEntity1().getName(), link.getEntity2().getName(), relationshipType, removeBrackets(link.getLabel().toString()));
			relationshipData.setSourceID(sourceID);
			relationshipData.setTargetID(targetID);
			return relationshipData;
		}
		return null;
	}

	private void extractLeaf(Entity entity, List<ClassData> classes, List<NaryData> naries) {
		LeafType leafType = entity.getLeafType();
		
		if (leafType.isLikeClass()) {
			
			classes.add(extractClass(entity, leafType));		    
		} else if (leafType == LeafType.STATE_CHOICE || leafType == LeafType.ASSOCIATION) { 
			// TYPE DIAMOND (n-ary)
			// Due to plantUml's internal purpose being simply rendering, STATE_CHOICE (state diagram choice)
			// is also used for the diamond entity as they are displayed the same.
			// When declared as "<>" instead of "diamond", the leaf type is ASSOCIATION.
			naries.add(extractNary(entity));
		} 
		else if (leafType == LeafType.NOTE) {
			noteDatas.add(extractNote(entity));
		} else if (leafType == LeafType.POINT_FOR_ASSOCIATION) {
			AssociationPoint point = new AssociationPoint(entity.getUid());
			associationPoints.add(point);
			assocPointMap.put(entity.getUid(), point);
		}

		else {
			ApplicationManager.instance().getViewManager()
			.showMessage("Warning: a leaf was not imported due to unsupported type..");
		}
	}

	private NaryData extractNary(Entity entity) {

		String name = removeBrackets(entity.getDisplay().toString());
		NaryData naryData = new NaryData(entity.getName(), null, null, false);
		naryData.setUid(entity.getUid());

		String key = name + "|NARY";
		boolean hasSemantics = getSemanticsMap().containsKey(key);

		if (hasSemantics) naryData.setSemantics(getSemanticsMap().get(key));
		return naryData;
	}

	private ClassData extractClass(Entity entity, LeafType classLeafType) {

		String visibility = convertVisibility(entity.getVisibilityModifier());
		String name = removeBrackets(entity.getDisplay().toString());
		ClassData classData = new ClassData(name, false, visibility, false, null);
		String key = name + "|Class";

		boolean hasSemantics = getSemanticsMap().containsKey(key);

		if (hasSemantics) classData.setSemantics(getSemanticsMap().get(key));

		List<String> stereotypes = extractStereotypes(entity, classData);

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
				"byte", "String");
		String typesPattern = String.join("|", basicTypes);
		Pattern pattern1 = Pattern.compile("\\b(" + typesPattern + ")\\b");

		// fields:: MOST OF THIS IS CONVENTION BASED, NOT SOLIDLY DEFINED SYNTAX
		for (CharSequence cs : entity.getBodier().getFieldsToDisplay()) {
			final Member m = (Member) cs;
			final VisibilityModifier fieldModifier = m.getVisibilityModifier();
			String attrVisibility = convertVisibility(fieldModifier);
			String scope = m.isStatic() ? "classifier" : "instance";
			//String typesPattern = String.join("|", basicTypes);
			//Pattern pattern1 = Pattern.compile("\\b(" + typesPattern + ")\\b");
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
				detectedValue = valueMatcher.group(1); // Captures the value after '='

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

				String[] nameValueParts = param.split("=", 2); 
				String namePart = nameValueParts[0].trim(); 
				if (nameValueParts.length > 1) {
					paramValue = nameValueParts[1].trim(); 
				}

				String[] nameParts = namePart.split("\\s+"); 
				if (nameParts.length == 1) {
					paramName = nameParts[0];
				} else {
					if (basicTypes.contains(nameParts[0])) {
						paramType = nameParts[0];
						paramName = nameParts[1]; 
					} else {
						paramName = nameParts[1] + " : " + nameParts[0];			        }
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
	public List<AssociationPoint> getAssociationPoints() {
		return associationPoints;
	}
}

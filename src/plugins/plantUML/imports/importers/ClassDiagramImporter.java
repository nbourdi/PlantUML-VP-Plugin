package plugins.plantUML.imports.importers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.cfg.beanvalidation.GroupsPerOperation;

import com.jniwrapper.n;
import com.vp.plugin.ApplicationManager;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.Bodier;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.NaryData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.PackageData;
import plugins.plantUML.models.RelationshipData;
import plugins.plantUML.models.OperationData.Parameter;
import v.ber.op;
import net.sourceforge.plantuml.abel.CucaNote;
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

	public ClassDiagramImporter(ClassDiagram classDiagram) {
		this.classDiagram = classDiagram;
	}

	public void extract() {
//		LeafType.
		
		
		for (Entity groupEntity : classDiagram.groups()) {
			GroupType groupType = groupEntity.getGroupType();
			
			if (groupType == GroupType.PACKAGE) {
				ApplicationManager.instance().getViewManager()
		         .showMessage("in groups() extractuon ");
				List<ClassData> packageClassDatas = new ArrayList<ClassData>();
				List<PackageData> packagedPackageDatas = new ArrayList<PackageData>(); 
				List<NaryData> packageNaryDatas = new ArrayList<NaryData>();
				
				
				for (Entity packagedLeaf : groupEntity.leafs()) {
					extractLeaf(packagedLeaf, packageClassDatas, packageNaryDatas);
				}
				
				// then also iterate over the groups groups...
				PackageData packageData = new PackageData(groupEntity.getName(), packageClassDatas, null, packageNaryDatas, false, false);
				
				packageDatas.add(packageData);
			}
		}
		
		// leafs can be etc, but for now assume everything is "levelled"
		for (Entity entity : classDiagram.leafs()) {
			
			if (entity.getParentContainer().isRoot()) {
				extractLeaf(entity, classDatas, naryDatas);
			}
		}
		
		for (Link link : classDiagram.getLinks()) {
			// only doing generaliz for now
			relationshipDatas.add(extractRelationship(link));
		}
	}

	private RelationshipData extractRelationship(Link link) {
		String source = link.getEntity1().getUid();
		String target = link.getEntity2().getName();
		RelationshipData relationshipData = new RelationshipData(source, target, "Generalization", link.getLabel().toString());
		return relationshipData;
	}

	private void extractLeaf(Entity entity, List<ClassData> classes, List<NaryData> naries) {
		LeafType leafType = entity.getLeafType();
		if (leafType == LeafType.CLASS || leafType == LeafType.ABSTRACT_CLASS || leafType == LeafType.ANNOTATION
				|| leafType == LeafType.STEREOTYPE || leafType == LeafType.STRUCT || leafType == LeafType.ENUM
				|| leafType == LeafType.ENTITY || leafType == LeafType.INTERFACE || leafType == LeafType.PROTOCOL
				|| leafType == LeafType.METACLASS) {
		    // Create a ClassData model from the plantuml entity
		    // name, isAbstract = false as it is a different leaf type, visibility = converted to string from enum,
		    // isinPackage = false as its leaf, null description always.
		    classes.add(extractClass(entity, leafType));		    
		} else if (leafType == leafType.STATE_CHOICE) { 
			// TYPE DIAMOND (n-ary)
			// Due to plantUml's internal purpose being simply rendering, STATE_CHOICE (state diagram choice)
			// is also used for the diamond entity as they are displayed the same.
			naries.add(extractNary(entity));
			
		}
		
		else {
		    ApplicationManager.instance().getViewManager()
		            .showMessage("Warning: a leaf was not imported due to unsupported type..");
		}
	}

	private NaryData extractNary(Entity entity) {
		NaryData naryData = new NaryData(entity.getName(), null, false);
		return naryData;
	}

	private ClassData extractClass(Entity entity, LeafType classLeafType) {
		String visibility = convertVisibility(entity.getVisibilityModifier());
		ClassData classData = new ClassData(entity.getName(), false, visibility, false, null);
		String rawStereotypes = entity.getStereotype() == null ? "" :  entity.getStereotype().toString(); // in a single string like <<Stereo1>><<stereo2>>...

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
				Parameter parameter = new Parameter(paramName, paramType, paramValue);
				parameters.add(parameter);
			}
			String opName = m.getDisplay(false).replaceFirst("\\b" + detectedReturnType + "\\b", "")
					.replaceAll("\\(.*\\)", "").trim();
			OperationData operationData = new OperationData(opVisibility, opName, detectedReturnType, m.isAbstract(),
					parameters, scope);
			classData.addOperation(operationData);
			classData.setUid(entity.getUid());
		}
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
}

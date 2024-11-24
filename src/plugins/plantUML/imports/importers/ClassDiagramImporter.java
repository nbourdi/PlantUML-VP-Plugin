package plugins.plantUML.imports.importers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vp.plugin.ApplicationManager;

import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.cucadiagram.Bodier;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.utils.Position;
import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.ClassData;
import plugins.plantUML.models.OperationData;
import plugins.plantUML.models.OperationData.Parameter;
import v.ber.op;
import net.sourceforge.plantuml.abel.CucaNote;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.abel.Link;

public class ClassDiagramImporter extends DiagramImporter {

	// first step in the importing pipeline
	// this should go in importController or pipeline:
//    SourceStringReader reader = new SourceStringReader(source);
//    ClassDiagram classDiagram = (ClassDiagram) reader.getBlocks().get(0).getDiagram();

	private ClassDiagram classDiagram;
	private List<ClassData> classDatas = new ArrayList<ClassData>();

	public ClassDiagramImporter(ClassDiagram classDiagram) {
		this.classDiagram = classDiagram;
	}

	public void extract() {
//		LeafType.

		// leafs can be packages etc, but for now assume everything is "levelled"
		for (Entity entity : classDiagram.leafs()) {

			switch (entity.getLeafType()) {
			case CLASS:

				// create a ClassData model from the plantuml entity
				// name, isAbstract = false as it is a different leaf type, visibility =
				// converted to string from enum, isinPackage=false as its leaf
				// null description always.
				classDatas.add(extractClass(entity));
				break;
				
//			case 

			default:
				ApplicationManager.instance().getViewManager()
						.showMessage("Warning: a leaf was not imported due to unsupported type..");
				break;
			}
		}
	}

	private ClassData extractClass(Entity entity) {
		String visibility = convertVisibility(entity.getVisibilityModifier());
		ClassData classData = new ClassData(entity.getName(), false, visibility, false, null);
		String rawStereotypes = entity.getStereotype().toString(); // in a single string like <<Stereo1>><<stereo2>>...

		Pattern pattern = Pattern.compile("<<([^>]+)>>");
		List<String> stereotypes = new ArrayList<>();
		Matcher matcher = pattern.matcher(rawStereotypes);
		while (matcher.find()) {
			stereotypes.add(matcher.group(1)); // group(1) gets the part inside << >>
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
		}
		return classData;
	}

	public List<ClassData> getClassDatas() {
		return classDatas;
	}
}

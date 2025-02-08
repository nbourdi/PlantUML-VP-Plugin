package plugins.plantUML.imports.importers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import plugins.plantUML.models.*;

public abstract class DiagramImporter {
	
	private Map<String, SemanticsData> semanticsMap;
	
	public DiagramImporter(Map<String, SemanticsData> semanticsMap) {
		this.setSemanticsMap(semanticsMap);
	}

	public abstract void  extract();
	protected String convertVisibility(VisibilityModifier visibilityModifier) {
		if (visibilityModifier == VisibilityModifier.PUBLIC_METHOD 
				|| visibilityModifier == VisibilityModifier.PUBLIC_FIELD)
			return "public";
		else if (visibilityModifier == VisibilityModifier.PRIVATE_METHOD 
				|| visibilityModifier == VisibilityModifier.PRIVATE_FIELD)
			return "private";
		else if (visibilityModifier == VisibilityModifier.PROTECTED_METHOD 
				|| visibilityModifier == VisibilityModifier.PROTECTED_FIELD) {
			return "protected";
		}
		else if (visibilityModifier == VisibilityModifier.PACKAGE_PRIVATE_METHOD 
				|| visibilityModifier == VisibilityModifier.PACKAGE_PRIVATE_FIELD) {
			return "package";
		}
	
	return "unspecified";
	}
	
	protected NoteData extractNote(Entity noteEntity) {
		String noteContent = removeBrackets(noteEntity.getDisplay().toString()) ;
		NoteData noteData = new NoteData(ignoreLinebreak(noteEntity.getName()), noteContent, null);
		noteData.setUid(noteEntity.getUid());
		return noteData;
	}

	private String ignoreLinebreak(String name) {
		return name.replace("\n", " ").replace("\r", " ");
	}


	protected String removeBrackets(String bracketedString) {
	    if (bracketedString != null && bracketedString.length() > 1 
	        && bracketedString.charAt(0) == '[' 
	        && bracketedString.charAt(bracketedString.length() - 1) == ']') {
	        return bracketedString.substring(1, bracketedString.length() - 1);
	    }
	    return bracketedString;
	}
	
	protected List<String> extractStereotypes(Entity entity, BaseWithSemanticsData data) {
		String rawStereotypes = entity.getStereotype() == null ? "" :  entity.getStereotype().toString(); // in a single string like <<Stereo1>><<stereo2>>
		Pattern pattern = Pattern.compile("<<([^>]+)>>");
		List<String> stereotypes = new ArrayList<>();
		Matcher matcher = pattern.matcher(rawStereotypes);
		while (matcher.find()) {
			stereotypes.add(matcher.group(1)); // group(1) gets the part inside << >>
		}
		return stereotypes;
	}

	// For lifelines + interaction Actors
	protected List<String> extractStereotypes(Participant entity, BaseWithSemanticsData data) {
		String rawStereotypes = entity.getStereotype() == null ? "" :  entity.getStereotype().toString(); // in a single string like <<Stereo1>><<stereo2>>
		Pattern pattern = Pattern.compile("<<([^>]+)>>");
		List<String> stereotypes = new ArrayList<>();
		Matcher matcher = pattern.matcher(rawStereotypes);
		while (matcher.find()) {
			stereotypes.add(matcher.group(1)); // group(1) gets the part inside << >>
		}
		return stereotypes;
	}

	protected void extractAttrsAndOps(Entity entity, ClassData classData) {
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

			Matcher matcher1 = pattern1.matcher(m.getDisplay(false));
			String detectedType = "";

			if (matcher1.find()) detectedType = matcher1.group(1);
			String attributeName = m.getDisplay(false).replaceAll("\\b" + detectedType + "\\b", "").replaceAll(":", "").trim();
			Pattern valuePattern = Pattern.compile("=\\s*(.*)");
			Matcher valueMatcher = valuePattern.matcher(attributeName);
			String detectedValue = null;
			if (valueMatcher.find()) detectedValue = valueMatcher.group(1); // Captures the value after '='

			if (detectedValue != null)
				attributeName = attributeName.replaceAll("=\\s*" + Pattern.quote(detectedValue), "").trim();
			AttributeData attributeData = new AttributeData(attrVisibility, attributeName, detectedType, detectedValue, scope);
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

			if (returnTypeMatcher.find()) detectedReturnType = returnTypeMatcher.group(1);

			Pattern paramPattern = Pattern.compile("\\((.*?)\\)"); // parentheses
			Matcher paramMatcher = paramPattern.matcher(m.getDisplay(false));

			StringBuilder paramsStr = new StringBuilder();
			if (paramMatcher.find()) {
				paramsStr.append(paramMatcher.group(1));
			}
			List<OperationData.Parameter> parameters = new ArrayList<>();
			// split parameters by commas
			String[] paramStrings = paramsStr.toString().split(",");
			for (String param : paramStrings) {
				param = param.trim();
				String paramType = null;
				String paramName;
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
					OperationData.Parameter parameter = new OperationData.Parameter(paramName, paramType, paramValue);
					parameters.add(parameter);
				}
			}
			String opName = m.getDisplay(false).replaceFirst("\\b" + detectedReturnType + "\\b", "")
					.replaceAll("\\(.*\\)", "").trim();
			OperationData operationData = new OperationData(opVisibility, opName, detectedReturnType, m.isAbstract(),
					parameters, scope);
			classData.addOperation(operationData);
		}
	}

	public Map<String, SemanticsData> getSemanticsMap() {
		return semanticsMap;
	}

	public void setSemanticsMap(Map<String, SemanticsData> semanticsMap) {
		this.semanticsMap = semanticsMap;
	}

	
}

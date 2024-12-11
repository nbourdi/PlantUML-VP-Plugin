package plugins.plantUML.imports.importers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import plugins.plantUML.models.BaseWithSemanticsData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.SemanticsData;

public class DiagramImporter {	
	
	private Map<String, SemanticsData> semanticsMap;
	
	public DiagramImporter(Map<String, SemanticsData> semanticsMap) {
		this.setSemanticsMap(semanticsMap);
	}
	
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
		NoteData noteData = new NoteData(noteEntity.getName(), noteContent, null);
		noteData.setUid(noteEntity.getUid());
		return noteData;
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

	public Map<String, SemanticsData> getSemanticsMap() {
		return semanticsMap;
	}

	public void setSemanticsMap(Map<String, SemanticsData> semanticsMap) {
		this.semanticsMap = semanticsMap;
	}

	
}

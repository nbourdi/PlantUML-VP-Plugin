package plugins.plantUML.imports.importers;

import java.util.Map;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.skin.VisibilityModifier;
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

	public Map<String, SemanticsData> getSemanticsMap() {
		return semanticsMap;
	}

	public void setSemanticsMap(Map<String, SemanticsData> semanticsMap) {
		this.semanticsMap = semanticsMap;
	}

	
}

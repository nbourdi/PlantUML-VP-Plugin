package plugins.plantUML.imports.importers;

import net.sourceforge.plantuml.skin.VisibilityModifier;

public class DiagramImporter {
	
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
}

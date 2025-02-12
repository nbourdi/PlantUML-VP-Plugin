package plugins.plantUML.export.writers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import plugins.plantUML.models.AttributeData;
import plugins.plantUML.models.NoteData;
import plugins.plantUML.models.OperationData;

public abstract class PlantUMLWriter {
    
    protected List<NoteData> notes = new ArrayList<NoteData>();
    
    public PlantUMLWriter(List<NoteData> notes) {
        if (notes != null) {
            this.notes = notes;
        }
    }

    public abstract void writeToFile(File file) throws IOException;
    
    protected String writeNotes() {
        StringBuilder notesContent = new StringBuilder();
        for (NoteData noteData : notes) {
            notesContent.append(writeNote(noteData)).append("\n");
        }
        return notesContent.toString();
    }
    
    protected String writeNote(NoteData noteData) {
        String content = noteData.getContent();
        
        // Return an empty string if content is null or empty
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        StringBuilder noteString = new StringBuilder();
        noteString.append("note ")
                  .append("\"").append(content).append("\" as ")
                  .append(noteData.getName());
        
        return noteString.toString();
    }
    
    protected String formatName(String name) {
		/*
		 * Spaces and other non-letter characters are not 
		 * supported as names for plantUML
		 */
	    if (!name.matches("[a-zA-Z0-9]+")) {
	        return "\"" + name + "\"";
	    }
	    return name;
    }

    protected String formatAlias(String name) {
        return name.replaceAll("[^a-zA-Z0-9\u0370-\u03FF]", "_");
    }

    protected String writeVisibility(String visibility) {
        String visibilityCharacter = "";
        switch (visibility) {
            case "private":
                visibilityCharacter = "-";
                break;
            case "protected":
                visibilityCharacter = "#";
                break;
            case
                    "package": visibilityCharacter = "~";
                break;
            case "public":
                visibilityCharacter = "+";
                break;
        }
        return visibilityCharacter;
    }
    protected void writeAttributesAndOperations(List<AttributeData> attributes, List<OperationData> operations, String indent, StringBuilder classString) {
        for (AttributeData attribute : attributes) {
            String visibilityChar = writeVisibility(attribute.getVisibility());

            classString.append(indent).append("\t").append(visibilityChar).append(" ");
            if (attribute.isStatic()) classString.append("{static} ");
            classString.append(attribute.getName());

            if (attribute.getType() != null) {
                classString.append(": ").append(attribute.getType());
            }
            if (attribute.getInitialValue() != null) {
                classString.append(" = ").append(attribute.getInitialValue());
            }
            classString.append("\n");
        }

        // Add operations
        for (OperationData operation : operations) {
            String visibilityChar = writeVisibility(operation.getVisibility());

            classString.append(indent).append("\t").append(visibilityChar);

            if (operation.isAbstract()) classString.append("{abstract} ");

            if (operation.isStatic()) classString.append("{static} ");

            classString.append(operation.getName()).append("(");

            // Add parameters
            List<OperationData.Parameter> parameters = operation.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                OperationData.Parameter param = parameters.get(i);

                classString.append(param.getName());

                if (param.getType() != null && !param.getType().isEmpty()) {
                    classString.append(": ").append(param.getType());
                }

                if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
                    classString.append(" = ").append(param.getDefaultValue());
                }

                if (i < parameters.size() - 1) {
                    classString.append(", ");
                }
            }

            classString.append(")");

            if (operation.getReturnType() != null && !operation.getReturnType().isEmpty()) {
                classString.append(": ").append(operation.getReturnType());
            }

            classString.append("\n");
        }
    }
}

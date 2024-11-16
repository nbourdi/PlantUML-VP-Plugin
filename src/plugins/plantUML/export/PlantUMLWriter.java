package plugins.plantUML.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import plugins.plantUML.export.models.NoteData;

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
}

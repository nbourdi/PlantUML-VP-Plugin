package plugins.plantUML.export.writers;

import plugins.plantUML.models.NoteData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ActivityUMLWriter extends PlantUMLWriter {
    public ActivityUMLWriter(List<NoteData> notes) {
        super(notes);
    }

    @Override
    public void writeToFile(File file) throws IOException {

    }
}

package plugins.plantUML.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import plugins.plantUML.export.models.SemanticsData;

public class PlantJSONWriter {
	
	public static void writeToFile(File file, List<SemanticsData> semantics) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startjson\n");
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);  // otherwise its a single line
        
        String semanticsDataJson = objectMapper.writeValueAsString(semantics);
        
        plantUMLContent.append(semanticsDataJson).append("\n");
        
        plantUMLContent.append("@endjson");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(plantUMLContent.toString());
        }
	}
}

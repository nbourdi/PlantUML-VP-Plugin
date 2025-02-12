package plugins.plantUML.export.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import plugins.plantUML.models.SemanticsData;

public class PlantJSONWriter {
	
	public static void writeToFile(File file, List<SemanticsData> semantics) throws IOException {
        StringBuilder plantUMLContent = new StringBuilder("@startjson\n");
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);  // otherwise its a single line
        
        String semanticsDataJson = objectMapper.writeValueAsString(semantics);
        
        plantUMLContent.append(semanticsDataJson).append("\n");
        
        plantUMLContent.append("@endjson");
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
                    writer.write(plantUMLContent.toString());
            }
	}
}

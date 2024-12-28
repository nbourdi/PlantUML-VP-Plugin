package plugins.plantUML.export;

import plugins.plantUML.imports.importers.DiagramImporter;
import plugins.plantUML.models.SemanticsData;

import java.util.Map;

public class ActivityDiagramExporter extends DiagramImporter {

    public ActivityDiagramExporter(Map<String, SemanticsData> semanticsMap) {
        super(semanticsMap);
    }

    @Override
    public void extract() {

    }
}

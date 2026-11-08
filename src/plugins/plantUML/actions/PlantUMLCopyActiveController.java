package plugins.plantUML.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;

import plugins.plantUML.export.DiagramExportPipeline;

import java.util.Collections;

public class PlantUMLCopyActiveController implements VPActionController {

    @Override
    public void performAction(VPAction action) {
        IDiagramUIModel diagram = ApplicationManager.instance()
                .getDiagramManager()
                .getActiveDiagram();

        if (diagram == null) {
            ApplicationManager.instance().getViewManager()
                    .showMessageDialog(
                            ApplicationManager.instance().getViewManager().getRootFrame(),
                            "There is no active diagram to copy."
                    );
            return;
        }

        DiagramExportPipeline pipeline = new DiagramExportPipeline();
        if (pipeline.exportDiagramList(Collections.singletonList(diagram))) {
            ApplicationManager.instance().getViewManager()
                    .showMessageDialog(
                            ApplicationManager.instance().getViewManager().getRootFrame(),
                            "PlantUML copied to clipboard."
                    );
        }
    }

    @Override
    public void update(VPAction action) {
        action.setEnabled(ApplicationManager.instance()
                .getDiagramManager()
                .getActiveDiagram() != null);
    }
}

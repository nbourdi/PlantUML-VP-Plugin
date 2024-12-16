package plugins.plantUML.imports.creators;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.diagram.IInteractionDiagramUIModel;
import com.vp.plugin.diagram.shape.IInteractionLifeLineUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IInteractionLifeLine;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.vpuml.plugin.umlpluginmodel.Actor;
import net.sourceforge.plantuml.sequencediagram.Message;
import plugins.plantUML.models.ActorData;
import plugins.plantUML.models.LifelineData;
import plugins.plantUML.models.MessageData;

import java.util.ArrayList;
import java.util.List;

public class SequenceDiagramCreator extends  DiagramCreator {

    List<LifelineData> lifelineDatas = new ArrayList<>();
    List<ActorData> actorDatas = new ArrayList<>();
    List<MessageData> messageDatas = new ArrayList<>();

    IInteractionDiagramUIModel sequenceDiagram = (IInteractionDiagramUIModel) diagramManager.createDiagram(DiagramManager.DIAGRAM_TYPE_INTERACTION_DIAGRAM);
    public SequenceDiagramCreator(String diagramTitle, List<LifelineData> lifelineDatas, List<ActorData> actorDatas) {
        super(diagramTitle);
        this.lifelineDatas = lifelineDatas;
        this.actorDatas = actorDatas;
    }

    @Override
    public void createDiagram() {

        sequenceDiagram.setName(getDiagramTitle());

        for (LifelineData lifelineData : lifelineDatas) {
            IInteractionLifeLine lifelineModel = createLifeline(lifelineData);
        }


        diagramManager.layout(sequenceDiagram, DiagramManager.LAYOUT_AUTO);
        ApplicationManager.instance().getProjectManager().saveProject();
        ApplicationManager.instance().getDiagramManager().openDiagram(sequenceDiagram);

    }

    private IInteractionLifeLine createLifeline(LifelineData lifelineData) {
        IInteractionLifeLine lifelineModel = IModelElementFactory.instance().createInteractionLifeLine();

        checkAndSettleNameConflict(lifelineData.getName(), "InteractionLifeline");

        lifelineModel.setName(lifelineData.getName());
        putInSemanticsMap(lifelineModel, lifelineData);

        lifelineData.getStereotypes().forEach(lifelineModel::addStereotype);

        IInteractionLifeLineUIModel lifelineShape = (IInteractionLifeLineUIModel) diagramManager.createDiagramElement(sequenceDiagram, lifelineModel);
        shapeMap.put(lifelineModel, lifelineShape); // TODO is this needed in sequence

        return lifelineModel;
    }


}

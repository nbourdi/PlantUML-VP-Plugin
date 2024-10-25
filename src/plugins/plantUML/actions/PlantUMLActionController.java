package plugins.plantUML.actions;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStep;
import com.vp.plugin.model.IStepContainer;
import com.vp.plugin.model.IUseCase;
import com.vp.plugin.model.factory.IModelElementFactory;

public class PlantUMLActionController implements VPActionController{

	public void performAction(VPAction action) {
		// get the view manager and the parent component for modal the dialog.
		ViewManager viewManager = ApplicationManager.instance().getViewManager();
		Component parentFrame = viewManager.getRootFrame();
		
		// popup a file chooser for choosing the output file
		JFileChooser fileChooser = viewManager.createJFileChooser();
		fileChooser.setFileFilter(new FileFilter() {
		
			public String getDescription() {
				return "*.htm";
			}
		
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(".htm");
			}
		
		});
		fileChooser.showSaveDialog(parentFrame);
		File file = fileChooser.getSelectedFile();
		
		if (file!=null && !file.isDirectory()) {
			String htmlContent = "";
			String result = "";
			
			// Retrieve all use cases from project
			ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
			IModelElement[] models = projectManager.getProject().toModelElementArray(IModelElementFactory.MODEL_TYPE_USE_CASE);

			// Retrieve an HTML string of flow of events info from every use case
			for (int i = 0; i < models.length; i++) {
				IModelElement modelElement = models[i];
				IUseCase useCase = (IUseCase)modelElement;
				htmlContent += generate(useCase);
			}
			
			// write to file
			try {
				FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
				System.out.println(file.getAbsolutePath());
				writer.write(htmlContent);
				writer.close();
				result = "Success! HTML generated to "+file.getAbsolutePath();
			} catch (IOException e) {
			}
			
			// show the generation result
			viewManager.showMessageDialog(parentFrame, result);
		}
	}
	
	public String generate(IUseCase useCase) {
		String content = "";
		
		// Retrieve flow of events sets from use case. Each IStepContainer is a set of flow of events
		IStepContainer[] stepContainers = useCase.toStepContainerArray();
		for (int i = 0; i < stepContainers.length; i++) {
			IStepContainer stepContainer = stepContainers[i];
			
			// Print the name of use case and flow of events to HTML string
			content += "<table border=\"1\" width=\"500\"><tr><th>" + useCase.getName() + " - " + stepContainer.getName() + "</th></tr>";
			
			// Print the flow of events content to HTML string
			IStep[] stepArray = stepContainer.toStepArray();
			for (int j = 0; j < stepArray.length; j++) {
				IStep step = stepArray[j];
				content += "<tr><td>"+ (j+1) + ". " + step.getName()+"</td></tr>";
			}
			content += "</table><br>";
		}
		
		return content;
		
	}

	public void update(VPAction action) {
	}

}

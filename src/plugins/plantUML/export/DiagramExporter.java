package plugins.plantUML.export;

import java.util.Iterator;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IReference;

public class DiagramExporter {
	
	protected void extractReferences(IClass classModel) {
		// TODO Auto-generated method stub
		ApplicationManager.instance().getViewManager().showMessage("=========References::");
		Iterator referenceIter = classModel.referenceIterator();
		while (referenceIter.hasNext()) {
	            IReference reference = (IReference) referenceIter.next();
				ApplicationManager.instance().getViewManager().showMessage(reference.getUrlAsDiagram().getName()); 
	    }
	}
}

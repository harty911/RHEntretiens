package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.MainWindow;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.ContextAction;

public class TalkDeleteAction extends ContextAction {
	
	public TalkDeleteAction() {
		super("Suppr. entretien", Icons.getDescriptor(Icons.DELETE));
	}

	@Override
	protected boolean isValidSelection(IStructuredSelection sel) {
		return (sel.size()==1 && sel.getFirstElement() instanceof Talk);
	}

	@Override
	protected boolean execute(IStructuredSelection sel) {
		Shell shell = Display.getCurrent().getActiveShell();
		RHModel model = RHToolApp.getModel();

		Talk talk = (Talk)sel.getFirstElement();
		boolean res = MessageDialog.openQuestion( shell, "Confirmation de suppression", 
				"Voulez-vous vraiment supprimer cet entretien ?");
		if( res) {
			talk.delete();
			model.save(talk);
			MainWindow.LOGGER.info( talk+" deleted");
			RHToolApp.getWindow().updateFromModel();
		}
		return false;
	}	
}

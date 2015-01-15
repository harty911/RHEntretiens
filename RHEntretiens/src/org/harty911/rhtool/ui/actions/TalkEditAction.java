package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.ContextAction;
import org.harty911.rhtool.ui.utils.UIModelUtils;
import org.harty911.rhtool.ui.wizards.TalkWizard;

public class TalkEditAction extends ContextAction {
	
	public TalkEditAction() {
		super("Modifier entretien", Icons.getDescriptor(Icons.EDIT));
	}

	@Override
	protected boolean isValidSelection(IStructuredSelection sel) {
		return (sel.getFirstElement() instanceof Talk);
	}

	@Override
	protected boolean execute(IStructuredSelection sel) {
		Shell shell = Display.getCurrent().getActiveShell();
		RHModel model = RHToolApp.getModel();
		Talk talk = (Talk)sel.getFirstElement();

		if( !UIModelUtils.refreshAndCheck(talk))
			return false;

		WizardDialog dlg = new WizardDialog(shell, new TalkWizard( talk));
		if( dlg.open() == Window.OK) {
			model.save(talk);
			RHToolApp.getWindow().updateFromModel();
		}
		return true;
	}
}

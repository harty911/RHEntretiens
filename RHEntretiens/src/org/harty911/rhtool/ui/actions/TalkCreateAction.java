package org.harty911.rhtool.ui.actions;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.ContextAction;
import org.harty911.rhtool.ui.utils.UIModelUtils;
import org.harty911.rhtool.ui.wizards.TalkWizard;

public class TalkCreateAction extends ContextAction {
	
	public TalkCreateAction() {
		super("Créer entretien", Icons.getDescriptor(Icons.CREATE));
	}

	@Override
	protected boolean isValidSelection(IStructuredSelection sel) {
		return (sel.getFirstElement() instanceof Employee);
	}

	@Override
	protected boolean execute(IStructuredSelection sel) {
		Shell shell = Display.getCurrent().getActiveShell();
		RHModel model = RHToolApp.getModel();
		Employee emp = (Employee)sel.getFirstElement();

		if( !UIModelUtils.refreshAndCheck(emp))
			return false;
		
		List<Talk> lasts = model.getTalks(emp);
		Talk talk = new Talk( emp, model.getUserContext(), lasts.isEmpty() ? null : lasts.get(0));
		
		WizardDialog dlg = new WizardDialog(shell, new TalkWizard( talk));
		if( dlg.open() == Window.OK) {
			RHToolApp.getWindow().updateFromModel();
		}
		return true;
	}
}
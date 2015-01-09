package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.harty911.rhtool.ui.dialogs.AboutDialog;
import org.harty911.rhtool.ui.resources.Icons;

public class AboutAction extends Action {

	public AboutAction() {
		super("&A propos...");
		setImageDescriptor( Icons.getDescriptor(Icons.HELP));
	}

	@Override
	public void run() {
		AboutDialog dlg = new AboutDialog( Display.getCurrent().getActiveShell());
		dlg.open();
	}
}

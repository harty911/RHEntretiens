package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.harty911.rhtool.ui.dialogs.ManageUsersDialog;
import org.harty911.rhtool.ui.resources.Icons;

public class ManageUsersAction extends Action {

	public ManageUsersAction() {
		super("&Gestion des utilisateurs");
		setImageDescriptor( Icons.getDescriptor(Icons.USER_ADMIN));
	}

	@Override
	public void run() {
		
		ManageUsersDialog dlg = new ManageUsersDialog( Display.getCurrent().getActiveShell());
		dlg.open();
	}
}

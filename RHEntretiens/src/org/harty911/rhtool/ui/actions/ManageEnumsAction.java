package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.core.model.RHEnum;
import org.harty911.rhtool.ui.dialogs.ManageEnumsDialog;

public class ManageEnumsAction<T extends RHEnum> extends Action {

	private final String enumTitle;
	private final Class<T> enumClass;

	public ManageEnumsAction( Class<T> enumClass, String enumTitle) {
		super("Gestion '"+enumTitle+"'");
		this.enumTitle = enumTitle;
		this.enumClass = enumClass;
	}

	@Override
	public void run() {
		Shell shell = Display.getCurrent().getActiveShell();
		ManageEnumsDialog<T> dlg = new ManageEnumsDialog<T>( shell, enumClass, enumTitle);
		dlg.open();
	}
}

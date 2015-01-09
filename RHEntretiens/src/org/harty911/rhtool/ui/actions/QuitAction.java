package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.harty911.rhtool.RHToolApp;

public class QuitAction extends Action {

	public QuitAction() {
		super("&Quitter\tCtrl+Q");
		setAccelerator( SWT.CTRL | 'Q');
	}

	@Override
	public void run() {
		RHToolApp.getWindow().close();
	}

}

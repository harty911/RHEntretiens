package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.ui.dialogs.EmployeeFormDialog;
import org.harty911.rhtool.ui.resources.Icons;

public class EmployeeCreateAction extends Action {
	
	public EmployeeCreateAction() {
		super("Ajouter collab.", Icons.getDescriptor(Icons.CREATE));
	}

	@Override
	public void run() {
		Shell shell = Display.getCurrent().getActiveShell();
		RHModel model = RHToolApp.getModel();

		Employee emp = new Employee();
		EmployeeFormDialog dlg = new EmployeeFormDialog( shell, emp);
		if( dlg.open()==Window.OK) {
			model.save(emp);
			RHToolApp.getWindow().updateFromModel();
		}
	}	
}

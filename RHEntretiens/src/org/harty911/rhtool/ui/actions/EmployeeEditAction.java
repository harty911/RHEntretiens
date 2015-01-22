package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.ui.MainWindow;
import org.harty911.rhtool.ui.dialogs.EmployeeFormDialog;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.ContextAction;
import org.harty911.rhtool.ui.utils.UIModelUtils;

public class EmployeeEditAction extends ContextAction {
	
	public EmployeeEditAction() {
		super("Modifier collab.", Icons.getDescriptor(Icons.EDIT));
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
		
		EmployeeFormDialog dlg = new EmployeeFormDialog( shell, emp);
		if( dlg.open()==Window.OK) {
			model.save(emp);
			MainWindow.LOGGER.info( emp+" modified");
			RHToolApp.getWindow().updateFromModel();
		}
		return true;
	}
}

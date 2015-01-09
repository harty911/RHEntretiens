package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.ContextAction;

public class EmployeeDeleteAction extends ContextAction {
	
	public EmployeeDeleteAction() {
		super("Suppr. collab.", Icons.getDescriptor(Icons.DELETE));
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
		boolean res = MessageDialog.openQuestion( shell, "Confirmation de suppression", 
				"Voulez-vous vraiment sortir '"+emp.getNomUsuel()+"' des effectifs ?");
		if( res) {
			emp.delete();
			model.save(emp);
			RHToolApp.getWindow().updateFromModel();
		}
		return false;
	}	
}

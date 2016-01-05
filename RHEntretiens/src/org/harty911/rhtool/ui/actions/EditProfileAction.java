package org.harty911.rhtool.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.core.utils.RHModelUtils;
import org.harty911.rhtool.ui.dialogs.UserFormDialog;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.UIModelUtils;

public class EditProfileAction extends Action {

	public EditProfileAction() {
		super("&Profil utilisateur");
		setImageDescriptor( Icons.getDescriptor(Icons.USER_ADMIN));
	}

	@Override
	public void run() {
		Shell shell = Display.getCurrent().getActiveShell();
		RHModel model = RHToolApp.getModel();
		User user = RHToolApp.getModel().getUserContext();

		if( !UIModelUtils.refreshAndCheck(user)) {
			MessageDialog.openError(shell, "Profil utilisateur", "Votre profil a été désactivé, seul l'administrateur peut le modifier !");
			return;
		}
		
		if( RHModelUtils.isDefaultAdminContext(RHToolApp.getModel())) {
			MessageDialog.openError(shell, "Profil utilisateur", "Impossible de modifier l'administrateur par défaut");
			return;
		}
		
		UserFormDialog dlg = new UserFormDialog( shell, user);
		if( dlg.open()==Window.OK) {
			model.save(user);
			RHToolApp.getWindow().updateFromModel();
		}
	}
}

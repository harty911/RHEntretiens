package org.harty911.rhtool.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.ui.resources.Icons;

public class LoginDialog extends TitleAreaDialog {

	private Text txtPassword;
	private Text txtLogin;
	private final RHModel model;

	/**
	 * Create the dialog (no parent)
	 * @param rhModel 
	 */
	public LoginDialog(RHModel rhModel) {
		super((Shell)null);
		this.model = rhModel;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage( Icons.getImage( Icons.LOGO_CAL));
		setMessage("Connection");
		setTitle(RHToolApp.APP_NAME);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginHeight = 10;
		gl_container.marginWidth = 10;
		gl_container.horizontalSpacing = 10;
		gl_container.verticalSpacing = 10;
		container.setLayout(gl_container);
		
		Label lblLogin = new Label(container, SWT.NONE);
		lblLogin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLogin.setText("Utilisateur");

		txtLogin = new Text(container, SWT.BORDER);
		txtLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Mot de passe");
		
		txtPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,	true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		String pwd = txtPassword.getText();
		String log = txtLogin.getText();
		// search user
		User user = null;
		for( User u : model.getUsers()) {
			if(	u.getLogin().equals(log))
				user = u;
		}
		if( user==null) {
			setMessage("Utilisateur inconnu !");
			txtLogin.setFocus();
			txtLogin.selectAll();
			return;
		}
		if( !user.checkPassword(pwd)) {
			setMessage("Mot de passe incorrect !");
			txtPassword.setFocus();
			txtPassword.selectAll();
			return;
		}
		
		model.setUserContext(user);
		super.okPressed();
	}	

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(303, 218);
	}

}

package org.harty911.rhtool.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.core.model.objects.User;

public class UserFormDialog extends TitleAreaDialog {
	
	private static final String DUMMY_PASSWORD = "²²²²²²²²²²";
	private Text txtLogin;
	private Text txtPwd;
	private Text txtPwd2;
	private Text txtNom;
	private Text txtPrenom;
	
	private final User user;
	private Button btnAdmin;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public UserFormDialog( Shell parentShell, User user) {
		super(parentShell);
		this.user = user;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		setTitle("Modification d'utilisateur : " + user.getLogin());
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.marginWidth = 10;
		gl_container.verticalSpacing = 12;
		container.setLayout(gl_container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblLogin = new Label(container, SWT.NONE);
		lblLogin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLogin.setText("Login :");
		
		txtLogin = new Text(container, SWT.BORDER);
		GridData gd_txtLogin = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtLogin.minimumWidth = 100;
		txtLogin.setLayoutData(gd_txtLogin);
		
		new Label(container, SWT.NONE);
		
		btnAdmin = new Button(container, SWT.CHECK);
		btnAdmin.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAdmin.setText("Administrateur");
		
		Label lblMotDePasse = new Label(container, SWT.NONE);
		lblMotDePasse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMotDePasse.setText("Mot de passe :");
		
		txtPwd = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txtPwd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Label lblConfirmerMotDe = new Label(container, SWT.NONE);
		lblConfirmerMotDe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConfirmerMotDe.setText("confirmer :");
		
		txtPwd2 = new Text(container, SWT.BORDER | SWT.PASSWORD);
		GridData gd_txtPwd2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtPwd2.minimumWidth = 100;
		txtPwd2.setLayoutData(gd_txtPwd2);
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		Label lblPrnom = new Label(container, SWT.NONE);
		lblPrnom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPrnom.setText("Pr\u00E9nom :");
		
		txtPrenom = new Text(container, SWT.BORDER);
		txtPrenom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblNom = new Label(container, SWT.NONE);
		lblNom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNom.setText("Nom :");
		
		txtNom = new Text(container, SWT.BORDER);
		txtNom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		
		// Load initial data
		
		txtLogin.setText( user.getLogin());
		btnAdmin.setSelection(user.isAdmin());
		String pwd = user.isDeleted() ? "" : DUMMY_PASSWORD;
		txtPwd.setText(pwd);
		txtPwd2.setText(pwd);

		txtPrenom.setText( user.getPrenom());
		txtNom.setText( user.getNom());
		
		return area;
	}

	
	private boolean validate() {
		if( txtLogin.getText().isEmpty() || txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty()){
			setMessage("Les champs login, prenom, nom sont obligatoires");
			return false;
		}
		String pwd1 = txtPwd.getText();
		String pwd2 = txtPwd2.getText();
		if( pwd1.isEmpty() || pwd2.isEmpty()) {
			setMessage("Le mot de passe ne peut être vide");
			return false;
		}
		if( !pwd1.equals(pwd2)) {
			setMessage("Les deux mot de passe ne correspondent pas");
			return false;
		}

		return true;
	}

	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(406, 295);
	}

	@Override
	protected void okPressed() {
		if( !validate())
			return;
		
		user.setLogin( txtLogin.getText());
		if( btnAdmin.getSelection())
			user.setAdmin();
		else
			user.setStandard();
		
		if( !txtPwd.getText().equals(DUMMY_PASSWORD))
			user.setPassword( txtPwd.getText());

		user.setPrenom( txtPrenom.getText());
		user.setNom( txtNom.getText());
		
		super.okPressed();
	}


}

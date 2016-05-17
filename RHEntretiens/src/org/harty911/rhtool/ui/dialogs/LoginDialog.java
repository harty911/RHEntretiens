package org.harty911.rhtool.ui.dialogs;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.AppInfos;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.db.DbVersionException;
import org.harty911.rhtool.core.db.RHDbConnector;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.core.utils.RHModelUtils;
import org.harty911.rhtool.ui.resources.Icons;

public class LoginDialog extends TitleAreaDialog {

	private Text txtPassword;
	private Text txtLogin;
	private RHModel model;

	private static final Logger LOGGER = Logger.getLogger(LoginDialog.class.getName());

	private static final String PREF_DBDIR = "Application.db";
	private File dbDir;
	
	/**
	 * Create the dialog (no parent)
	 */
	public LoginDialog() {
		super((Shell)null);
		this.dbDir = new File( RHToolApp.getPreferenceStore().getString(PREF_DBDIR));
		_openDatabase( false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage( Icons.getImage( Icons.LOGO_CAL));
		setMessage("Connection");
		setTitle(AppInfos.APP_NAME);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginTop = 20;
		gl_container.marginRight = 20;
		gl_container.marginLeft = 20;
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
		
		if( model==null) {
			selectDatabaseDir();
			return;
		}
		if( model==null) {
			setMessage("Pas de base de donnée selectionnée !");
			return;
		}
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
		
		RHToolApp.getPreferenceStore().setValue(PREF_DBDIR, dbDir.getAbsolutePath());
		
		super.okPressed();
	}	
	
	public RHModel getModel() {
		return model;
	}
	

	private void selectDatabaseDir() {

		FileDialog dlg = new FileDialog( this.getShell(), isCreateMode() ? SWT.SAVE : SWT.OPEN);
		
		dlg.setText("Pas de base de donnée RH sélectionnée : Veuillez en selectionner une." + (isCreateMode() ? " [Mode création]" : ""));
		dlg.setFilterNames(new String[] {"Base de donnée RH (RHTool.db)"});
		dlg.setFilterExtensions(new String[] {"RHTool.db"});
			
		String filename = dlg.open();
		
		if( filename==null)
			return;
			
		dbDir = new File(filename).getParentFile();
		
		_openDatabase( isCreateMode());
	}
	
	
	private void _openDatabase( boolean create) {
		model = null;
		if( !RHDbConnector.isDatabase(dbDir))
			return;

		try {
			RHDbConnector rhDb = RHDbConnector.openDatabase(dbDir, create);
			model = new RHModel(rhDb);
		}
		catch ( DbVersionException e) {
			Shell parent = getShell();
			if( parent==null) 
				parent = new Shell();
			MessageDialog.openError( parent, "Version d'application obsolète", "L'application doit être mise à jour, veuillez contacter l'administrateur");
			throw new RuntimeException("Exit : Obsolete application version", e);
		}
		catch ( Exception e) {
			LOGGER.log(Level.SEVERE,"Cannot open DB "+ dbDir +" : ",e);
		}
	}
	

	protected boolean isCreateMode() {
		return RHModelUtils.ADMIN_PASSWORD.equals(txtPassword.getText()) && RHModelUtils.ADMIN_LOGIN.equals(txtLogin.getText());
	}

}

package org.harty911.rhtool.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.RHEContrat;
import org.harty911.rhtool.ui.utils.ControlUtils;
import org.harty911.rhtool.ui.utils.ObjectViewerController;

public class EmployeeFormDialog extends TitleAreaDialog {
	
	private boolean isFullEdit = true;
	
	private final Employee employee;

	private Text txtMatricule;
	private Text txtNom;
	private Text txtPrenom;
	private ObjectViewerController<RHEContrat> cmbCtrlContract;
	private DateTime dtInput;
	private DateTime dtBirth;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EmployeeFormDialog( Shell parentShell, Employee emp) {
		super(parentShell);
		this.employee = emp;
	}

	public void setPartialMode( boolean partialMode) {
		isFullEdit = !partialMode;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		if( employee.getMatricule()==0)
			setTitle("Creation d'un collaborateur");
		else
			setTitle("Edition de collaborateur : " + employee.getNomUsuel());
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.marginWidth = 10;
		gl_container.verticalSpacing = 10;
		container.setLayout(gl_container);

		// NOM
		
		Label lblNom = new Label(container, SWT.NONE);
		lblNom.setAlignment(SWT.RIGHT);
		lblNom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNom.setText("Nom :");
		
		txtNom = new Text(container, SWT.BORDER);
		txtNom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// PRENOM
		
		Label lblPrnom = new Label(container, SWT.NONE);
		lblPrnom.setAlignment(SWT.RIGHT);
		lblPrnom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPrnom.setText("Pr\u00E9nom :");
		
		txtPrenom = new Text(container, SWT.BORDER);
		txtPrenom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		// MATRICULE
		
		Label lblMatricule = new Label(container, SWT.NONE);
		lblMatricule.setAlignment(SWT.RIGHT);
		lblMatricule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblMatricule.setText("Matricule :");

		txtMatricule = new Text(container, SWT.BORDER);
		txtMatricule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// DATE NAISSANCE
		
		Label lblBirthDate = new Label(container, SWT.NONE);
		lblBirthDate.setAlignment(SWT.RIGHT);
		lblBirthDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblBirthDate.setText("Date de Naissance :");
		
		dtBirth = new DateTime(container, SWT.BORDER | SWT.DROP_DOWN);
		
		// separator
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		// CONTRAT

		Label lblContract = new Label(container, SWT.NONE);
		lblContract.setAlignment(SWT.RIGHT);
		lblContract.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContract.setText("Type de contrat:");
		
		ComboViewer combo = new ComboViewer(container, SWT.READ_ONLY);
		combo.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		cmbCtrlContract = new ObjectViewerController<RHEContrat>(combo, RHToolApp.getModel().getEnumValues(RHEContrat.class)); 
		
		// INPUT DATE
		
		Label lblInputDate = new Label(container, SWT.NONE);
		lblInputDate.setAlignment(SWT.RIGHT);
		lblInputDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblInputDate.setText("Date d'anciennet\u00E9 :");
		
		dtInput = new DateTime(container, SWT.BORDER | SWT.DROP_DOWN);

		// set initial data
		
		txtNom.setText( employee.getNom());
		txtPrenom.setText( employee.getPrenom());
		txtMatricule.setText( String.valueOf(employee.getMatricule()));
		ControlUtils.setControlDate( dtBirth, employee.getNaissance());
		cmbCtrlContract.setValue( employee.getContrat());
		ControlUtils.setControlDate( dtInput, employee.getAnciennete());

		// Field behavior
		
		txtMatricule.setEnabled( isFullEdit);
		txtNom.setEnabled( isFullEdit);
		txtPrenom.setEnabled( isFullEdit);

		txtMatricule.addVerifyListener(new ControlUtils.LongVerifier( 0, 99999999));

		
		return area;
	}

	
	private boolean validate() {
		if( txtMatricule.getText().isEmpty() 
		 || txtNom.getText().isEmpty() 
		 || txtPrenom.getText().isEmpty()){
			setMessage("Les champs nom, prenom et matricule sont obligatoires");
			return false;
		}
		
		int mat = Integer.parseInt(txtMatricule.getText());
		if( mat < 100000) {
			setMessage("Le matricule doit comporter au moins 6 chiffres");
			return false;
		}
		for( Employee emp : RHToolApp.getModel().getEmployees()) {
			if( emp.equals(employee)) continue;
			if( emp.getMatricule() == mat) {
				setMessage("Le matricule est déjà utilisé dans l'effectif présent");
				return false;
			}
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
		return new Point(408, 293);
	}

	@Override
	protected void okPressed() {
		if( !validate())
			return;

		employee.setNom( txtNom.getText());
		employee.setPrenom( txtPrenom.getText());
		employee.setMatricule( Long.parseLong(txtMatricule.getText()));
		employee.setNaissance( ControlUtils.getControlDate( dtBirth));
		employee.setContrat( cmbCtrlContract.getValue());
		employee.setAnciennete( ControlUtils.getControlDate( dtInput));
		
		super.okPressed();
	}


}

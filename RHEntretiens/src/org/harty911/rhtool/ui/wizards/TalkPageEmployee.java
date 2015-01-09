package org.harty911.rhtool.ui.wizards;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.RHEClassif;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.utils.ControlUtils;
import org.harty911.rhtool.ui.utils.EHEnumController;

public class TalkPageEmployee extends WizardPage {
	
	private final Talk talk;
	private Text txtInput;
	private Text txtContract;
	private Text txtMatricule;
	private Text txtBirth;
	private Text txtNom;
	private EHEnumController<RHEClassif> cmbClassif;
	private Text txtPCE;
	private Text txtEmploi;
	private Text txtPCP;
	private DateTime dtPoste;

	public TalkPageEmployee( Talk talk) {
		super("collab", "Informations collaborateur", null);
		this.talk = talk;
		setPageComplete(true);
	}

	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(6, false));

		// NOM
		
		Label lblNom = new Label(container, SWT.NONE);
		lblNom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNom.setAlignment(SWT.RIGHT);
		lblNom.setText("Nom :");
		
		txtNom = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtNom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		// DATE NAISSANCE
		
		Label lblBirthDate = new Label(container, SWT.NONE);
		lblBirthDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblBirthDate.setAlignment(SWT.RIGHT);
		lblBirthDate.setText("Date de Naiss. :");
		
		txtBirth = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtBirth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// MATRICULE
		
		Label lblMatricule = new Label(container, SWT.NONE);
		lblMatricule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblMatricule.setAlignment(SWT.RIGHT);
		lblMatricule.setText("Matricule :");

		txtMatricule = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtMatricule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		// CONTRAT

		Label lblContract = new Label(container, SWT.NONE);
		lblContract.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContract.setAlignment(SWT.RIGHT);
		lblContract.setText("Contrat:");
		
		txtContract = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtContract.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// INPUT DATE
		
		Label lblInputDate = new Label(container, SWT.NONE);
		lblInputDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblInputDate.setAlignment(SWT.RIGHT);
		lblInputDate.setText("Date d'entr\u00E9e :");
		
		txtInput = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// separator
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 6, 1));
		
		// PCE
		
		Label lblPce = new Label(container, SWT.NONE);
		lblPce.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPce.setText("PCE :");
		lblPce.setAlignment(SWT.RIGHT);
		
		txtPCE = new Text(container, SWT.BORDER);
		txtPCE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		// CLASSIF
		
		Label lblClassif = new Label(container, SWT.NONE);
		lblClassif.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClassif.setAlignment(SWT.RIGHT);
		lblClassif.setText("Classification :");
	
		ComboViewer combo2 = new ComboViewer(container, SWT.READ_ONLY);
		Combo combo = combo2.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		cmbClassif = new EHEnumController<RHEClassif>(combo2, RHEClassif.class); 
		
		// PCP
		
		Label lblPcp = new Label(container, SWT.NONE);
		lblPcp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPcp.setText("PCP :");
		lblPcp.setAlignment(SWT.RIGHT);
		
		txtPCP = new Text(container, SWT.BORDER);
		txtPCP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		// DATE POSTE
		
		Label lblDtPoste = new Label(container, SWT.NONE);
		lblDtPoste.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		lblDtPoste.setText("Dur\u00E9e dans le poste:");
		
		dtPoste = new DateTime(container, SWT.BORDER);
		dtPoste.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		// EMPLOI
		
		Label lblEmploi = new Label(container, SWT.NONE);
		lblEmploi.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEmploi.setAlignment(SWT.RIGHT);
		lblEmploi.setText("Emploi :");
		
		txtEmploi = new Text(container, SWT.BORDER);
		txtEmploi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));

		
		// required to avoid an error in the system
		setControl(container);
		setPageComplete(true);
		
		System.out.println("CREATE");

		refresh();
	}
	
	
	@Override
	public IWizardPage getNextPage() {
		IWizardPage nextPage = super.getNextPage();

		// TODO - do stuff

		return nextPage;
	}
	
	public void refresh() {
		
		// Employee
		
		Employee employee = talk.getEmployee();
		RHToolApp.getModel().refresh(employee);

		txtNom.setText( employee.getNomUsuel());
		txtMatricule.setText( String.valueOf(employee.getMatricule()));
		ControlUtils.setControlDate( txtBirth, employee.getNaissance());
		txtContract.setText( employee.getContrat()==null ? "" : employee.getContrat().getText());
		ControlUtils.setControlDate( txtInput, employee.getArrivee());

		// Talk
		
		txtPCE.setText( String.valueOf(employee.getPCE()));
		txtPCP.setText( String.valueOf(employee.getPCP()));
		cmbClassif.setValue( employee.getClassif());
		txtEmploi.setText( employee.getEmploi());
		
		ControlUtils.setControlDate(dtPoste, null);
		
		talk.getEmployee();
	}

	
	
}

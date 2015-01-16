package org.harty911.rhtool.ui.wizards;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.harty911.rhtool.ui.utils.ObjectViewerController;

public class TalkPageEmployee extends WizardPage implements SelectionListener, ISelectionChangedListener, ModifyListener {
	
	private final Talk talk;
	private Text txtInput;
	private Text txtContract;
	private Text txtMatricule;
	private Text txtBirth;
	private Text txtNom;
	private ObjectViewerController<RHEClassif> cmbCtrlClassif;
	private Text txtPCE;
	private Text txtEmploi;
	private Text txtPCP;
	private DateTime dtPoste;
	private Text txtAffect;
	private String[] affectValues;
	private String[] emploiValues;

	public TalkPageEmployee( Talk talk) {
		super("collab", "Informations collaborateur", null);
		this.talk = talk;
		
		buildAssistList();
	}

	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl_container = new GridLayout(6, false);
		gl_container.verticalSpacing = 10;
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);

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
		cmbCtrlClassif = new ObjectViewerController<RHEClassif>( combo2, RHToolApp.getModel().getEnumValues(RHEClassif.class)); 

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
		ControlUtils.addTextAssist( txtEmploi, emploiValues);

		Label lblAffect = new Label(container, SWT.NONE);
		lblAffect.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAffect.setText("Affectation :");
		lblAffect.setAlignment(SWT.RIGHT);
		
		txtAffect = new Text(container, SWT.BORDER);
		txtAffect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		ControlUtils.addTextAssist( txtAffect, affectValues);
		
		// init contents from model
		fromModel();

		// add listeners
		dtPoste.addSelectionListener(this);
		combo2.addSelectionChangedListener(this);
		txtPCE.addModifyListener(this);
		txtPCP.addModifyListener(this);
		txtEmploi.addModifyListener(this);
		txtAffect.addModifyListener(this);
		
		// fire check and save to model
		toModel();

		setControl(container);
		
	}


	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		toModel();
	}
	@Override
	public void modifyText(ModifyEvent e) {
		toModel();
	}
	@Override
	public void widgetSelected(SelectionEvent e) {
		toModel();
	}
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		toModel();
	}


	private void buildAssistList() {
		Set<String> emplois = new LinkedHashSet<String>();
		Set<String> affects = new LinkedHashSet<String>();
		for( Talk talk : RHToolApp.getModel().getTalks()) {
			String str = talk.getEmploi();
			if( str!=null)
				emplois.add(str);
			str = talk.getAffectation();
			if( str!=null)
				affects.add(str);
		}
		this.emploiValues =  emplois.toArray(new String[0]);
		this.affectValues =  affects.toArray(new String[0]);
	}
	
	
	public void fromModel() {		
		// load Employee data (read only)
		Employee employee = talk.getEmployee();
		RHToolApp.getModel().refresh(employee);
		txtNom.setText( employee.getNomUsuel());
		txtMatricule.setText( String.valueOf(employee.getMatricule()));
		ControlUtils.setControlDate( txtBirth, employee.getNaissance());
		txtContract.setText( ControlUtils.printEnum(employee.getContrat()));
		ControlUtils.setControlDate( txtInput, employee.getAnciennete());
		
		// load Talk data
		txtPCE.setText( String.valueOf( talk.getPCE()));
		txtPCP.setText( String.valueOf( talk.getPCP()));
		cmbCtrlClassif.setValue( talk.getClassif());
		txtEmploi.setText( talk.getEmploi());
		txtAffect.setText( talk.getAffectation());
		ControlUtils.setControlDate( dtPoste, talk.getDatePoste());
	}


	public void toModel() {
		String errMsg = null;
		
		// Ancienneté poste
		
		Date dt = ControlUtils.getControlDate( dtPoste);
		talk.setDatePoste( dt);

		// Affectation
		
		String txt = txtAffect.getText();
		if( txt!=null && !txt.isEmpty()) 
			talk.setAffectation( txt);			
		else
			errMsg = "L'affectation n'est pas renseigné";

		// Emploi
		
		txt = txtEmploi.getText();
		if( txt!=null && !txt.isEmpty()) 
			talk.setEmploi( txt);			
		else
			errMsg = "L'emploi n'est pas renseigné";

		// Classif
		
		RHEClassif classif = cmbCtrlClassif.getValue();
		if( classif!=null) 
			talk.setClassif( classif);			
		else
			errMsg = "La Classification n'est pas renseignée";
		
		// PCP / PCE
		
		try {
			int pce = Integer.parseInt( txtPCE.getText());
			if( pce < 1 || pce > 20) throw new NumberFormatException();
			talk.setPCE(pce);
			int pcp = Integer.parseInt( txtPCP.getText());
			if( pcp < 1 || pcp > 20) throw new NumberFormatException();
			talk.setPCP(pcp);			
		}
		catch( NumberFormatException e) {
			errMsg = "PCP et PCE doivent être compris entre 1 et 20";
		}
		

		setErrorMessage(errMsg);
		setPageComplete( errMsg==null);
	}

}

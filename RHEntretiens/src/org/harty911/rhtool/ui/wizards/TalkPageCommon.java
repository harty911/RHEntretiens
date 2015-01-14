package org.harty911.rhtool.ui.wizards;

import java.util.Date;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.utils.ControlUtils;

public class TalkPageCommon extends WizardPage implements SelectionListener {

	private final Talk talk;

	private DateTime dtTalk;
	private Text text;

	public TalkPageCommon( Talk talk) {
		super("commun", "Informations entretien", null);
		this.talk = talk;		
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.verticalSpacing = 10;
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);
		
		// DATE

		Label lblDate = new Label(container, SWT.NONE);
		lblDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblDate.setText("Date de l'entretien :");
		
		dtTalk = new DateTime(container, SWT.BORDER);
		
		// DUREE 
		//TODO 15' 30' 45' 1:00 1:30 2:00 2:30 3:00 3:30 4:00

		Label lblDuree = new Label(container, SWT.NONE);
		lblDuree.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDuree.setText("Durée de l'entretien:");
		
		ComboViewer cmbDuree = new ComboViewer(container, SWT.READ_ONLY);
		cmbDuree.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		// valeurs en min ...
		
		// USER 1 & 2 (combo) 

		Label lblUser1 = new Label(container, SWT.NONE);
		lblUser1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser1.setText("Interlocuteurs RH :");
		
		ComboViewer cmbUser1 = new ComboViewer(container, SWT.READ_ONLY);
		cmbUser1.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		ComboViewer cmbUser2 = new ComboViewer(container, SWT.READ_ONLY);
		cmbUser2.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(container, SWT.NONE);
		
		// Separator
		
		Label label_1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		// CANAL

		Label lblCanal = new Label(container, SWT.NONE);
		lblCanal.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCanal.setText("Canal :");
		
		ComboViewer comboViewer_1 = new ComboViewer(container, SWT.READ_ONLY);
		Combo cmbCanal = comboViewer_1.getCombo();
		cmbCanal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		// INITIATIVE
		//TODO ajouter un attribut commentaire
		
		Label lblInit = new Label(container, SWT.NONE);
		lblInit.setText("Initiateur de la demande :");
		lblInit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo cmbInit = comboViewer.getCombo();
		cmbInit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
				
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
				
		// Separator

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
				
		// TYPE (combo enum)

		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type d'entretien:");
		
		ComboViewer cmbType = new ComboViewer(container, SWT.READ_ONLY);
		cmbType.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		new Label(container, SWT.NONE);

		// init contents from model
		fromModel();

		// add listeners
		dtTalk.addSelectionListener(this);
//		combo2.addSelectionChangedListener(this);
//		txtPCE.addModifyListener(this);
//		txtPCP.addModifyListener(this);
//		txtEmploi.addModifyListener(this);
		
		// fire check and save to model
		toModel();
		
		setControl(container);
				
	}
	
	
	public void fromModel() {		
		// load Talk data
		
		ControlUtils.setControlDate( dtTalk, talk.getDate());
		
//		txtNom.setText( employee.getNomUsuel());
//		txtMatricule.setText( String.valueOf(employee.getMatricule()));
//		ControlUtils.setControlDate( txtBirth, employee.getNaissance());
//		txtContract.setText( ControlUtils.getEnumText(employee.getContrat()));
//		ControlUtils.setControlDate( txtInput, employee.getAnciennete());
//		
//		// load Talk data
//		txtPCE.setText( String.valueOf( talk.getPCE()));
//		txtPCP.setText( String.valueOf( talk.getPCP()));
//		cmbClassif.setValue( talk.getClassif());
//		txtEmploi.setText( talk.getEmploi());
//		ControlUtils.setControlDate( dtPoste, talk.getDatePoste());
	}


	public void toModel() {
		String errMsg = null;
		
		// Date
		
		Date dt = ControlUtils.getControlDate( dtTalk);
		Date today = new Date();
		if( today.before(dt))
			errMsg = "La date ne peut pas être dans le futur.";
		talk.setDate( dt);

		// Duree (obligatoire)
		
		// User 1 & 2 (1 obligatoire)

		// Canal (obligatoire)
		
		// Initiative + commentaire
		
		// Type (obligatoire)
		
//		// Emploi
//		
//		String txt = txtEmploi.getText();
//		if( txt!=null && !txt.isEmpty()) 
//			talk.setEmploi( txt);			
//		else
//			errMsg = "L'emploi n'est pas renseigné";
//
//		// Classif
//		
//		RHEClassif classif = cmbClassif.getValue();
//		if( classif!=null) 
//			talk.setClassif( classif);			
//		else
//			errMsg = "La Classification n'est pas renseignée";
//		
//		// PCP / PCE
//		
//		try {
//			int pce = Integer.parseInt( txtPCE.getText());
//			if( pce < 1 || pce > 20) throw new NumberFormatException();
//			talk.setPCE(pce);
//			int pcp = Integer.parseInt( txtPCP.getText());
//			if( pcp < 1 || pcp > 20) throw new NumberFormatException();
//			talk.setPCP(pcp);			
//		}
//		catch( NumberFormatException e) {
//			errMsg = "PCP et PCE doivent être compris entre 1 et 20";
//		}
		

		setErrorMessage(errMsg);
		setPageComplete( errMsg==null);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		toModel();
	}
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		toModel();
	}


}

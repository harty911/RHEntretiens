package org.harty911.rhtool.ui.wizards;

import java.util.Calendar;
import java.util.Date;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.EActionStatus;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.utils.RHModelUtils;
import org.harty911.rhtool.ui.utils.ControlUtils;
import org.harty911.rhtool.ui.utils.ObjectViewerController;

public class TalkPageDetail extends WizardPage implements ModifyListener, SelectionListener, ISelectionChangedListener {
	
	private static final String MESSAGE_DETAIL_ENTRETIEN = "Résumé de l'entretien...";
	
	private final Talk talk;

	private Text txtDetail;
	private Text txtAccomp;
	private Text txtAction;
	private DateTime dtNext;
	private DateTime dtAction;
	private ObjectViewerController<EActionStatus> cmbCtrlAction;
	private ObjectViewerController<EActionStatus> cmbCtrlAccomp;
	private Button btnNextTalk;
	
	public TalkPageDetail(Talk talk) {
		super("CARRIERE", "Entretien de carrière", null);
		this.talk = talk;
	}

	@Override
	public void createControl(Composite parent) {		
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.horizontalSpacing = 10;
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		container.setLayout(gl_container);
		
		// RESUME 
		
		txtDetail = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		txtDetail.setToolTipText(MESSAGE_DETAIL_ENTRETIEN);
		txtDetail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		txtDetail.setMessage(MESSAGE_DETAIL_ENTRETIEN);
		
		// ACTION A MENER
		
		Group grpAction = new Group(container, SWT.NONE);
		grpAction.setText("Actions \u00E0 mener");
		grpAction.setLayout(new GridLayout(2, false));
		grpAction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		ComboViewer cmbAction = new ComboViewer(grpAction, SWT.READ_ONLY);
		cmbAction.getCombo().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		cmbCtrlAction = new ObjectViewerController<EActionStatus>( cmbAction, EActionStatus.values());
		
		dtAction = new DateTime(grpAction, SWT.BORDER | SWT.DROP_DOWN);
		dtAction.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		txtAction = new Text(grpAction, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		txtAction.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		// MESURE D'ACCOMPAGNEMENT
		
		Group grpAccomp = new Group(container, SWT.NONE);
		grpAccomp.setLayout(new GridLayout(1, false));
		grpAccomp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		grpAccomp.setText("Mesures d'accompagnement");
		
		ComboViewer cmbAccomp = new ComboViewer(grpAccomp, SWT.READ_ONLY);
		cmbAccomp.getCombo().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		cmbCtrlAccomp = new ObjectViewerController<EActionStatus>( cmbAccomp, EActionStatus.values());
		
		txtAccomp = new Text(grpAccomp, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		txtAccomp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		// PROCHAIN ENTRETIEN
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		btnNextTalk = new Button(composite, SWT.CHECK);
		btnNextTalk.setText("Date du prochain entretien:");
		
		dtNext = new DateTime(composite, SWT.BORDER | SWT.DROP_DOWN);

		// init contents from model
		fromModel();

		// add listeners
		txtDetail.addModifyListener(this);
		cmbAction.addSelectionChangedListener(this);
		txtAction.addModifyListener(this);
		dtAction.addSelectionListener(this);
		cmbAccomp.addSelectionChangedListener(this);
		txtAccomp.addModifyListener(this);
		btnNextTalk.addSelectionListener(this);
		dtNext.addSelectionListener(this);
		
		// fire check and save to model
		toModel();
		
		setControl(container);
	}
	
	public void fromModel() {		
		Date today = new Date();
		
		// Resumé
		txtDetail.setText(talk.getDetail());

		// Action
		cmbCtrlAction.setValue(talk.getActionStatus());
		if( EActionStatus.NOTHING.equals(talk.getActionStatus())) {
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(today); 
			cal.add(Calendar.DATE, 7);
			ControlUtils.setControlDate(dtAction, cal.getTime());
		}
		else
			ControlUtils.setControlDate( dtAction, talk.getActionDate());
		txtAction.setText(talk.getActionDetail());

		// Mesures Accompagnement
		cmbCtrlAccomp.setValue(talk.getAccompStatus());
		txtAccomp.setText(talk.getAccompDetail());
		
		// Date Next
		
		if( talk.getNextDate() == null) {
			btnNextTalk.setSelection(false);
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(today); 
			cal.add(Calendar.DATE, 365);
			ControlUtils.setControlDate(dtNext, cal.getTime());
		}
		else {
			btnNextTalk.setSelection(true);
			ControlUtils.setControlDate( dtNext, talk.getNextDate());
		}
	}
	
	public void toModel() {		
		String errMsg = null;
		Date orig = talk.getDate();
		
		// Resumé
		String txt = txtDetail.getText();
		if( txt.isEmpty())
			errMsg = "Le résumé de l'entretien doit être saisi";
		talk.setDetail(txt);
		
		// Action
		EActionStatus newSt = cmbCtrlAction.getValue();
		talk.setActionStatus( newSt);
		if( EActionStatus.NOTHING.equals(newSt) || EActionStatus.CANCELED.equals(newSt)) {
			dtAction.setEnabled(false);
			txtAction.setEnabled(false);
		}
		else {
			dtAction.setEnabled(true);
			txtAction.setEnabled(true);
			Date dt = ControlUtils.getControlDate( dtAction);
			if( orig.after(dt))
				errMsg = "La date d'echeance action ne peut pas être dans le passé.";
		}
		talk.setActionDate( ControlUtils.getControlDate( dtAction));
		talk.setActionDetail( txtAction.getText());
		
		// Mesures Accompagnement
		newSt = cmbCtrlAccomp.getValue();
		talk.setAccompStatus( newSt);
		if( EActionStatus.NOTHING.equals(newSt) || EActionStatus.CANCELED.equals(newSt))
			txtAccomp.setEnabled(false);
		else
			txtAccomp.setEnabled(true);
		talk.setAccompDetail( txtAccomp.getText());
		
		// Date Next
		if(	btnNextTalk.getSelection()) {
			Date dt = ControlUtils.getControlDate( dtNext);
			if( orig.after(dt))
				errMsg = "La date du prochain entretien ne peut pas être dans le passé.";
			talk.setNextDate( dt);
			dtNext.setEnabled(true);
		}
		else {
			talk.setNextDate( null);
			dtNext.setEnabled(false);
		}

		setErrorMessage( errMsg);
		setPageComplete( errMsg==null || RHModelUtils.isDefaultAdminContext(RHToolApp.getModel()));
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
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

	@Override
	public void modifyText(ModifyEvent e) {
		toModel();
	}
}

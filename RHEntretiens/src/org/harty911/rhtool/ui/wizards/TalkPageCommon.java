package org.harty911.rhtool.ui.wizards;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.RHECanal;
import org.harty911.rhtool.core.model.objects.RHEInitiative;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.Talk.ETypeEntretien;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.core.utils.RHModelUtils;
import org.harty911.rhtool.ui.utils.ControlUtils;
import org.harty911.rhtool.ui.utils.ObjectViewerController;

public class TalkPageCommon extends WizardPage implements SelectionListener, ModifyListener, ISelectionChangedListener {

	private final Talk talk;

	private DateTime dtTalk;
	private Combo cmbDuree;
	private Text txtInit;
	private ObjectViewerController<User> cmbCtrlUser1;
	private ObjectViewerController<User> cmbCtrlUser2;
	private ObjectViewerController<RHECanal> cmbCtrlCanal;
	private ObjectViewerController<RHEInitiative> cmbCtrlInit;
	private ObjectViewerController<ETypeEntretien> cmbCtrlType;
	
	private final Map<String,Integer> durationMap;



	public TalkPageCommon( Talk talk) {
		super("COMMUN", "Informations entretien", null);
		this.talk = talk;
		
		durationMap = new LinkedHashMap<String, Integer>();
		durationMap.put( "15'", 15);
		durationMap.put( "30'", 30);
		durationMap.put( "45'", 45);
		durationMap.put( "1h00", 60);
		durationMap.put( "1h30", 90);
		durationMap.put( "2h00", 120);
		durationMap.put( "2h30", 150);
		durationMap.put( "3h00", 180);
	}

	@Override
	public void createControl(Composite parent) {
		RHModel model = RHToolApp.getModel();
		
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

		Label lblDuree = new Label(container, SWT.NONE);
		lblDuree.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDuree.setText("Durée de l'entretien:");
		
		cmbDuree = new Combo(container, SWT.READ_ONLY);
		cmbDuree.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		for( String s : durationMap.keySet())
			cmbDuree.add(s);
		
		// USER 1 & 2 (combo) 

		Label lblUser = new Label(container, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("Interlocuteurs RH :");
		
		ComboViewer cmbUser1 = new ComboViewer(container, SWT.READ_ONLY);
		cmbUser1.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		cmbCtrlUser1 = new ObjectViewerController<User>( cmbUser1, RHModelUtils.getUserList(model));

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		ComboViewer cmbUser2 = new ComboViewer(container, SWT.READ_ONLY);
		cmbUser2.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		cmbCtrlUser2 = new ObjectViewerController<User>( cmbUser2, RHModelUtils.getUserList(model));
		
		new Label(container, SWT.NONE);
		
		// Separator
		
		Label sep1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		
		// CANAL

		Label lblCanal = new Label(container, SWT.NONE);
		lblCanal.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCanal.setText("Canal :");
		
		ComboViewer cmbCanal = new ComboViewer(container, SWT.READ_ONLY);
		cmbCanal.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbCtrlCanal = new ObjectViewerController<RHECanal>( cmbCanal, model.getEnumValues(RHECanal.class));
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		// INITIATIVE
		
		Label lblInit = new Label(container, SWT.NONE);
		lblInit.setText("Initiateur de la demande :");
		lblInit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ComboViewer cmbInit = new ComboViewer(container, SWT.READ_ONLY);
		cmbInit.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cmbCtrlInit = new ObjectViewerController<RHEInitiative>( cmbInit, model.getEnumValues(RHEInitiative.class));
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
				
		txtInit = new Text(container, SWT.BORDER);
		txtInit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
				
		// Separator

		Label sep2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
				
		// TYPE (combo enum)

		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Type d'entretien:");
		
		ComboViewer cmbType = new ComboViewer( container, SWT.READ_ONLY);
		cmbType.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		cmbCtrlType = new ObjectViewerController<Talk.ETypeEntretien>( cmbType, Talk.ETypeEntretien.values());
		
		new Label(container, SWT.NONE);

		// init contents from model
		fromModel();

		// add listeners
		dtTalk.addSelectionListener(this);
		cmbDuree.addModifyListener(this);
		cmbUser1.addSelectionChangedListener(this);
		cmbUser2.addSelectionChangedListener(this);
		cmbCanal.addSelectionChangedListener(this);
		cmbInit.addSelectionChangedListener(this);
		txtInit.addModifyListener(this);
		cmbType.addSelectionChangedListener(this);
		
		// fire check and save to model
		toModel();
		
		setControl(container);
				
	}
	
	
	public void fromModel() {		
		// load Talk data
		
		// Date
		
		ControlUtils.setControlDate( dtTalk, talk.getDate());

		// Duree (obligatoire)
		
		String txt = null;
		for( Entry<String, Integer> each : durationMap.entrySet()) {
			if( each.getValue().intValue() == talk.getDuration()) {
				txt = each.getKey();
				cmbDuree.setText( txt);
				break;
			}
		}
		
		// User 1 & 2 (1 obligatoire)
		cmbCtrlUser1.setValue(talk.getUser1());
		cmbCtrlUser2.setValue(talk.getUser2());
		
		// Canal (obligatoire)
		cmbCtrlCanal.setValue(talk.getCanal());		
		
		// Initiative + commentaire
		cmbCtrlInit.setValue( talk.getInitiative());
		txtInit.setText(talk.getInitiativeDetail());
		
		// Type (obligatoire)
		cmbCtrlType.setValue(talk.getType());
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

		Integer duree = durationMap.get( cmbDuree.getText());
		if( duree==null) {
			errMsg = "La durée doit être renseignée";
		}
		else {
			talk.setDuration(duree.intValue());			
		}
		
		// User 1 & 2 (1 obligatoire)
		User u1, u2;
		u1 = cmbCtrlUser1.getValue();
		u2 = cmbCtrlUser2.getValue();
		if( u1!=null) {
			talk.setUser1(u1);
			talk.setUser2(u2);
		}
		else if( u2!=null) {
			talk.setUser1(u2);
			talk.setUser2(null);
		}
		else
			errMsg = "Au moins un interlocuteur RH doit être renseigné";
		
		// Canal (obligatoire)
		RHECanal c = cmbCtrlCanal.getValue();
		if( c==null)
			errMsg = "La canal doit être renseigné";
		else
			talk.setCanal(c);
		
		// Initiative + commentaire
		talk.setInitiative( cmbCtrlInit.getValue());
		talk.setInitiativeDetail( txtInit.getText());
		
		// Type (obligatoire)
		talk.setType( cmbCtrlType.getValue());
		
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
	@Override
	public void modifyText(ModifyEvent e) {
		toModel();
	}
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		toModel();
	}


}

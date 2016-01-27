package org.harty911.rhtool.ui.dialogs;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.ui.utils.ControlUtils;

public class ExportStatsDialog extends TitleAreaDialog implements SelectionListener {

	protected Object result;
	protected Shell shell;
	private DateTime dtEnd;
	private DateTime dtStart;
	private Date start;
	private Date end;
	private Button butOk;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExportStatsDialog(Shell parent) {
		super(parent);
		
		// init dates
		Calendar cal = Calendar.getInstance(); 
		cal.set(Calendar.MONTH, 0); // january
		cal.set(Calendar.DAY_OF_MONTH, 1);
		start = cal.getTime();
		cal.set(Calendar.MONTH, 11); // december
		cal.set(Calendar.DAY_OF_MONTH, 31);
		end = cal.getTime();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Export statistiques");
		setMessage("Saisir les paramètres...");
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginHeight = 10;
		gl_container.marginWidth = 10;
		gl_container.horizontalSpacing = 10;
		gl_container.verticalSpacing = 10;
		container.setLayout(gl_container);
		
		Label lblStart = new Label(container, SWT.NONE);
		lblStart.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblStart.setText("Période de (inclus):");

		dtStart = new DateTime(container, SWT.BORDER | SWT.DROP_DOWN);
		dtStart.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		dtStart.addSelectionListener(this);

		Label lblEnd = new Label(container, SWT.NONE);
		lblEnd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblEnd.setText("à (inclus):");
		
		dtEnd = new DateTime(container, SWT.BORDER | SWT.DROP_DOWN);
		dtEnd.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		dtEnd.addSelectionListener(this);

		// init values
		ControlUtils.setControlDate( dtStart, start);
		ControlUtils.setControlDate( dtEnd, end);
		
		return area;
	}

	
	private void updateData() {
		String msg = null;
		
		start = ControlUtils.getControlDate( dtStart);
		end = ControlUtils.getControlDate( dtEnd);
		
		if( start.compareTo(end) > 0 )
			msg = "La date de fin doit être égale ou supérieure à la date de début.";

		setErrorMessage( msg);
		butOk.setEnabled( msg==null); 
	}

	
	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}


	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		butOk = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,	true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	
	@Override
	public void create() {
		super.create();
		
		// force first data update/validation 
		updateData();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		updateData();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		updateData();
	}

}

package org.harty911.rhtool.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.harty911.rhtool.core.model.objects.Talk;

public class TalkCarA extends WizardPage {

	public TalkCarA(Talk talk) {
		super("CARRIERE", "Entretien de carrière", null);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		// TODO Pages wizard CAR
		setControl(container);
	}

}

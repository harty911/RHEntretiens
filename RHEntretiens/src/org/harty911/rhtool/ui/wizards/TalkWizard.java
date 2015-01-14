package org.harty911.rhtool.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.harty911.rhtool.core.model.objects.Talk;

public class TalkWizard extends Wizard {
	
	private final Talk talk;

	public TalkWizard( Talk talk) {
		super();
		this.talk = talk;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public String getWindowTitle() {
		if( talk.getId()==0)
			return "Création d'un entretien";
		else
			return "Modification d'un entretien";
	}

	@Override
	public void addPages() {
		addPage( new TalkPageEmployee(talk));
		addPage( new TalkPageCommon(talk));
	}

}

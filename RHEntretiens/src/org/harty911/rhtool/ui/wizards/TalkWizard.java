package org.harty911.rhtool.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
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
	public IWizardPage getNextPage(IWizardPage page) {
		if( "COMMUN".equals(page.getName())) {
			// choose next page from type
			switch( talk.getType()) {
			case CARRIERE:
				return getPage("CARRIERE");
			case PROFESSIONEL:
				return getPage("PROFESSIONEL");
			default:
				break;
			}
		}
		else if( "CARRIERE".equals(page.getName())
			  || "PROFESSIONEL".equals(page.getName())) {
			return null;
		}
		
		return super.getNextPage(page);
	}

	@Override
	public void addPages() {
		addPage( new TalkPageEmployee(talk));
		addPage( new TalkPageCommon(talk));
		
		addPage( new TalkProA(talk));
		
		addPage( new TalkCarA(talk));
	}

}

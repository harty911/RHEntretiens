package org.harty911.rhtool.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHDocument;
import org.harty911.rhtool.core.model.objects.Talk;

public class TalkWizard extends Wizard {
	
	private final Talk talk;
	private TalkPageDocs docPage;

	public TalkWizard( Talk talk) {
		super();
		this.talk = talk;
	}


	@Override
	public boolean performFinish() {
		docPage.finish(true);
		RHToolApp.getModel().save(talk);
		for( RHDocument doc : docPage.getAllDocs())
			RHToolApp.getModel().save(doc);
		return true;
	}
	
	
	@Override
	public boolean performCancel() {
		docPage.finish(false);
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
			return getPage("DOCUMENTS");
		}
		
		return super.getNextPage(page);
	}

	@Override
	public void addPages() {

		addPage( new TalkPageEmployee(talk));
		addPage( new TalkPageCommon(talk));
		
		addPage( new TalkProA(talk));
		
		addPage( new TalkCarA(talk));
		
		docPage = new TalkPageDocs(talk);
		addPage( docPage);
	}


}

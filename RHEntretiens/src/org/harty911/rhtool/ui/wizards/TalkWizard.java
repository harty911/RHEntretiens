package org.harty911.rhtool.ui.wizards;

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
	public void addPages() {

		addPage( new TalkPageEmployee(talk));
		addPage( new TalkPageCommon(talk));
		addPage( new TalkPageDetail(talk));
		docPage = new TalkPageDocs(talk);
		addPage( docPage);
	}


}


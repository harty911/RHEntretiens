package org.harty911.rhtool.ui.printer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Date;

import org.harty911.framework.report.TemplatePrinter;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.TalkDoc;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.core.utils.TextFormater;
import org.harty911.rhtool.ui.actions.HyperlinkHandler;
import org.harty911.rhtool.ui.utils.ControlUtils;
import org.harty911.rhtool.ui.utils.UIModelUtils;

public class TalkPrinter extends TemplatePrinter {
	
	/**
	 * Generate HTML page from a Talk object
	 * @param talk
	 * @throws IOException
	 */
	public static String toHTML( Talk talk) throws IOException {
		if( !UIModelUtils.refreshAndCheck(talk))
			return null;

		StringWriter strWriter = new StringWriter();
			
		TalkPrinter printer = new TalkPrinter( talk, strWriter);
		printer.readTemplate( new FileInputStream("templates/Talk.html"));
		printer.printTemplate();
		printer.close();
		
		return strWriter.toString();
	}
	
	
	public Talk talk;
	
	public TalkPrinter( Talk talk, Writer writer) throws FileNotFoundException {
		super(writer);
		this.talk = talk;
		
		debug = true;	// TODO remove when ready
		
		initVariables();
	}
		
	///////////////////////////////////////////////////////////
	// Variables du template
	///////////////////////////////////////////////////////////
	
	public String COLLAB;
	public String MATRICULE;
	public String CONTRAT;
	public String DATE_NAISS;
	public String DATE_ENTREE;
	public String PCE;
	public String PCP;
	public String CLASSIF;
	public String DATE_POSTE;
	public String DUREE_POSTE;
	public String EMPLOI;
	public String AFFECTATION;

	public String DATE;
	public String SHORTDATE;
	public String DUREE;
	public String RHS;
	public String CANAL;
	public String INIT;
	public String INIT_TEXT;
	public String MOTIFS;

	public String RESUME;
	public String ACTION;
	public String ACTION_CLASS;
	public String ACTION_DATE;
	public String ACTION_TEXT;
	public String ACCOMP;
	public String ACCOMP_CLASS;
	public String ACCOMP_TEXT;
	public String NEXTDATE;

	public int PJ_NUM;
	public String PJ_URI;
	public String PJ_NAME;
	public String PJ_TYPE;
	
	private boolean hasAction;
	private boolean isActionOpen;
	private boolean isActionOver;
	private boolean hasAccomp;
	private boolean isAccompOpen;
	private boolean hasNextDate;
	private boolean hasDocs;
	
	private boolean debug;

	
	private void initVariables() {
		RHModel model = RHToolApp.getModel();
		
		Employee emp = talk.getEmployee();
		model.refresh(emp);
		COLLAB = emp.getNomUsuel();
		MATRICULE = String.format("%06d", emp.getMatricule());
		CONTRAT = ControlUtils.printEnum(emp.getContrat());
		DATE_NAISS = ControlUtils.printDate( emp.getNaissance());
		DATE_ENTREE = ControlUtils.printDate( emp.getEntree());
		
		// Attention ces champs sont relatif à l'entretien, ont pu evoluer pour le collaborateur
		PCE = String.valueOf( talk.getPCE());
		PCP = String.valueOf( talk.getPCP());
		CLASSIF = ControlUtils.printEnum(talk.getClassif());
		DATE_POSTE = ControlUtils.printDate( talk.getDatePoste());
		DUREE_POSTE = ControlUtils.printDateDiff( talk.getDatePoste(), talk.getDate());
		EMPLOI = talk.getEmploi();
		AFFECTATION = talk.getAffectation();
		
		DATE = ControlUtils.printDate( talk.getDate());
		SHORTDATE = ControlUtils.printMonth( talk.getDate());
		DUREE = ControlUtils.printDuration( talk.getDuration());
			
		MOTIFS = ControlUtils.printEnum(talk.getMotif1());
		String motif2 = ControlUtils.printEnum(talk.getMotif2());
		if( !motif2.isEmpty()) {
			if( !MOTIFS.isEmpty())
				MOTIFS = MOTIFS + "<br>";
			MOTIFS = MOTIFS + motif2;
		}
		
		User u1 = talk.getUser1();
		model.refresh(u1);
		RHS = u1.getNomUsuel();
		User u2 = talk.getUser2();
		if(u2!=null) {
			model.refresh(u2);
			RHS += "<br>" + u2.getNomUsuel();
		}

		INIT = ControlUtils.printEnum( talk.getInitiative());
		INIT_TEXT = talk.getInitiativeDetail();
		CANAL = ControlUtils.printEnum( talk.getCanal());

		RESUME = TextFormater.toRichHTML(talk.getDetail());

		hasAction = !talk.getActionStatus().isNothing();;
		isActionOpen = talk.getActionStatus().isOpen();
		isActionOver = ( isActionOpen && talk.getActionDate()!=null && 
					talk.getActionDate().compareTo(new Date()) < 0 ); 
		
		ACTION = talk.getActionStatus().toString();
		ACTION_CLASS = hasAction ? ( isActionOpen ? ( isActionOver ? "late" : "open") : "close" ) : "off";
		ACTION_DATE = ControlUtils.printDate(talk.getActionDate());
		ACTION_TEXT = TextFormater.toRichHTML(talk.getActionDetail());		
		
		hasAccomp = !talk.getAccompStatus().isNothing();
		isAccompOpen = talk.getAccompStatus().isOpen();
		ACCOMP = talk.getAccompStatus().toString();
		ACCOMP_CLASS = hasAccomp ? ( isAccompOpen ? "open" : "close" ) : "off";
		ACCOMP_TEXT = TextFormater.toRichHTML(talk.getAccompDetail());
		
		hasNextDate = ( talk.getNextDate()!=null );
		NEXTDATE = ControlUtils.printDate(talk.getNextDate());
		
		hasDocs = ( talk.getDocs().size() > 0 );
	}

	///////////////////////////////////////////////////////////
	// Methodes du template
	///////////////////////////////////////////////////////////

	public void HAS_ACTION() {
		if( hasAction)
			printTemplate();
	}
	
	public void HASNT_ACTION() {
		if( !hasAction)
			printTemplate();
	}
	
	public void IS_ACTION_OPEN() {
		if( isActionOpen)
			printTemplate();
	}
	
	public void ISNT_ACTION_OPEN() {
		if( !isActionOpen)
			printTemplate();
	}
	
	public void IS_ACTION_OVER() {
		if( isActionOver)
			printTemplate();
	}
	
	public void ISNT_ACTION_OVER() {
		if( !isActionOver)
			printTemplate();
	}
	
	public void HAS_ACCOMP() {
		if( hasAccomp)
			printTemplate();
	}
	 
	public void HASNT_ACCOMP() {
		if( !hasAccomp)
			printTemplate();
	}
	 
	public void IS_ACCOMP_OPEN() {
		if( isAccompOpen)
			printTemplate();
	}
	
	public void ISNT_ACCOMP_OPEN() {
		if( !isAccompOpen)
			printTemplate();
	}
	
	public void HAS_NEXTDATE() {
		if( hasNextDate)
			printTemplate();
	}
	
	public void HASNT_NEXTDATE() {
		if( !hasNextDate)
			printTemplate();
	}
	
	public void HAS_PJ() {
		if( hasDocs)
			printTemplate();
	}
	
	public void HASNT_PJ() {
		if( !hasDocs)
			printTemplate();
	}

	public void FOREACH_PJ() {
		PJ_NUM = 0;
		for( TalkDoc doc : talk.getDocs()) {
			if( doc.isDeleted()) 
				continue;
			RHToolApp.getModel().refresh(doc);
			PJ_NUM++;
			URI uri = HyperlinkHandler.toURI(HyperlinkHandler.SCHEME_DOC, doc);
			PJ_URI  = uri==null?"":uri.toString();
			PJ_TYPE = ControlUtils.printEnum(doc.getType());
			PJ_NAME = ControlUtils.print( doc.getName());
			printTemplate();
		}
	}

	public void DEBUG() {
		if( debug)
			printTemplate();
	}
	

}

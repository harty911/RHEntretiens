package org.harty911.rhtool.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.RHECanal;
import org.harty911.rhtool.core.model.objects.RHEClassif;
import org.harty911.rhtool.core.model.objects.RHEContrat;
import org.harty911.rhtool.core.model.objects.RHEInitiative;
import org.harty911.rhtool.core.model.objects.RHEMotif;
import org.harty911.rhtool.core.model.objects.RHETypeTalkDoc;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.User;

public class RHModelUtils {

	public static final String ADMIN_PASSWORD = "harty911";
	public static final String ADMIN_LOGIN    = "admin";

	public static void createAdminIfNotExists(RHModel model) {
		User u = model.getUserByLogin(ADMIN_LOGIN);
		if( u==null) {
			u = new User();
			u.setLogin(ADMIN_LOGIN);
			u.setPassword(ADMIN_PASSWORD);
			u.setAdmin();
			model.save(u);
		}	
	}
	

	public static List<User> getUserList(RHModel model) {
		List<User> lst = new LinkedList<User>(); 
		for( User u : model.getUsers()) {
			if( ! u.getLogin().equals(ADMIN_LOGIN))
				lst.add(u);
		}
		return lst;
	}
	
	/**
	 * Remove Time part of a DateTime (Date)
	 * @param datetime
	 * @return the date part
	 */
	public static Date toDate( Date datetime) {
		if( datetime==null)
			return null;
		Calendar cal = Calendar.getInstance(); 
		cal.setTime( datetime);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));		
		return cal.getTime();
	}
	
	/**
	 * Compute months between two Dates (Date)
	 * @param dateOld
	 * @param dateNew
	 * @return the number months between Old and New
	 */
	public static int toMonths( Date dateOld, Date dateNew) {
		if( dateOld==null)
			return 0;
		if( dateNew==null)
			return 0;
		Calendar start = new GregorianCalendar();
		start.setTime( dateOld);
		Calendar end = new GregorianCalendar();
		end.setTime( dateNew);
		return ( end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 
			   + end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
	}
	
	public static void createTestingData(RHModel model) {

		model.setBatchMode(true);
		
		try {

			User u = new User();
			u.setLogin("beber");
			u.setPassword("azerty");
			u.setPrenom("Bernard");
			u.setNom("GRAND-CLAUDE");
			model.save(u);
	
			u = new User();
			u.setLogin("vivir");
			u.setPassword("azerty");
			u.setPrenom("Virginie");
			u.setNom("FORGEOT");
			model.save(u);
	
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Employee e = new Employee();
			e.setMatricule(54670);
			e.setPrenom("Virginie");
			e.setNom("FORGEOT");
		    e.setAnciennete( df.parse("01/04/2014"));
		    e.setNaissance( df.parse("26/08/1977"));
			model.save(e);

			Employee e2 = new Employee();
			e2.setMatricule(233670);
			e2.setPrenom("Robert");
			e2.setNom("PIPIN");
		    e2.setAnciennete( df.parse("01/11/2012"));
		    e2.setNaissance( df.parse("26/11/1973"));
			model.save(e2);

			Employee e3 = new Employee();
			e3.setMatricule(433670);
			e3.setPrenom("Delphine");
			e3.setNom("BILLAT");
		    e3.setAnciennete( df.parse("01/11/2011"));
		    e3.setNaissance( df.parse("12/10/1989"));
			model.save(e3);

			// CONTRAT
			model.save( new RHEContrat("CDI"));
			model.save( new RHEContrat("CDD"));
			
			// CLASSIF
			model.save( new RHEClassif("Classif 1"));
			model.save( new RHEClassif("Classif 2"));
			model.save( new RHEClassif("Classif 3"));
			
			// CANAL
			model.save( new RHECanal("Telephone"));
			model.save( new RHECanal("Siege"));
			model.save( new RHECanal("Agence"));
	
			// INITIATIVE
			model.save( new RHEInitiative("Collaborateur"));
			model.save( new RHEInitiative("RH"));
			model.save( new RHEInitiative("Manager"));
			model.save( new RHEInitiative("Obligation Legale"));
	
			// MOTIF
			model.save( new RHEMotif("Rémunération"));
			model.save( new RHEMotif("Conditions de travail"));
		
			// TYPE DOC (TALK)
			model.save( new RHETypeTalkDoc("Passeport Formation"));
			model.save( new RHETypeTalkDoc("Compte rendu"));
			
			// Talks
			Talk talk = new Talk(e, u, null);
			talk.setEmploi("Conseiller clientelle");
		    talk.setDate( df.parse("01/04/2014"));
		    talk.setDuration(40);
		    model.save(talk);
			
			talk = new Talk(e, u, null);
			talk.setEmploi("Conseiller financier crédit");
		    talk.setDate( df.parse("01/10/2014"));
		    talk.setDuration(60);
			model.save(talk);
			
			talk = new Talk(e2, u, null);
			talk.setEmploi("Conseiller financier collecte");
		    talk.setDate( df.parse("01/12/2014"));
		    talk.setDuration(60);
			model.save(talk);		

			talk = new Talk(e2, u, null);
			talk.setEmploi("Conseiller financier collecte");
		    talk.setDate( df.parse("01/11/2014"));
		    talk.setDuration(95);
			model.save(talk);		

			talk = new Talk(e3, u, null);
			talk.setEmploi("Conseiller professionnel");
		    talk.setDate( df.parse("01/01/2014"));
		    talk.setDuration(70);
			model.save(talk);		
		}
		catch( ParseException e) {}				
		
		model.setBatchMode(false);
	}

}

package org.harty911.rhtool.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.RHECanal;
import org.harty911.rhtool.core.model.objects.RHEClassif;
import org.harty911.rhtool.core.model.objects.RHEContrat;
import org.harty911.rhtool.core.model.objects.RHEInitiative;
import org.harty911.rhtool.core.model.objects.RHEMotif;
import org.harty911.rhtool.core.model.objects.RHEMotifPro;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.User;

public class RHModelUtils {

	public static void createAdminIfNotExists(RHModel model) {
		User u = model.getUserByLogin("admin");
		if( u==null) {
			u = new User();
			u.setLogin("admin");
			u.setPassword("harty911");
			u.setAdmin();
			model.save(u);
		}	
	}
	

	public static List<User> getUserList(RHModel model) {
		List<User> lst = new LinkedList<User>(); 
		for( User u : model.getUsers()) {
			if( ! u.getLogin().equals("admin"))
				lst.add(u);
		}
		return lst;
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
			model.save( new RHEMotif("R�mun�ration"));
			model.save( new RHEMotif("Conditions de travail"));
	
			// MOTIF PRO
			model.save( new RHEMotifPro("Retour CIF"));
			model.save( new RHEMotifPro("Retour longue maladie"));
	
			// Talks
			Talk talk = new Talk(e, u, null);
			talk.setEmploi("Conseiller clientelle");
			model.save(talk);
			
			talk = new Talk(e, u, null);
			talk.setEmploi("Conseiller financier cr�dit");
			model.save(talk);
			
			talk = new Talk(e2, u, null);
			talk.setEmploi("Conseiller financier collecte");
			model.save(talk);		

			talk = new Talk(e2, u, null);
			talk.setEmploi("Conseiller financier collecte");
			model.save(talk);		

			talk = new Talk(e3, u, null);
			talk.setEmploi("Conseiller professionnel");
			model.save(talk);		
		}
		catch( ParseException e) {}				
		
		model.setBatchMode(false);
	}

}

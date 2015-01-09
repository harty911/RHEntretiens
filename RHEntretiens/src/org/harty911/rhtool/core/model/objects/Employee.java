package org.harty911.rhtool.core.model.objects;

import java.util.Date;

import org.harty911.rhtool.core.model.RHModelObject;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_collaborateur")
public class Employee extends RHModelObject {
	
	@DatabaseField
    private long matricule = 0;
    @DatabaseField
    private String prenom ="";
    @DatabaseField
    private String nom ="";
    @DatabaseField(dataType=DataType.DATE_STRING)
    private Date naissance = null;
    @DatabaseField(foreign = true)
    private RHEContrat contrat = null;
    @DatabaseField(dataType=DataType.DATE_STRING)
    private Date anciennete = null;
    
    
    public Employee() {  
    }


	@Override
	public String toString() {
		return super.toString()+" "+prenom+" "+nom;
	}

	public long getMatricule() {
		return matricule;
	}

	public void setMatricule(long matricule) {
		this.matricule = matricule;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Date getAnciennete() {
		return anciennete;
	}

	public void setAnciennete(Date date) {
		this.anciennete = date;
	}

	public Date getNaissance() {
		return naissance;
	}

	public void setNaissance(Date naissance) {
		this.naissance = naissance;
	}

	public RHEContrat getContrat() {
		return contrat;
	}

	public void setContrat(RHEContrat contrat) {
		this.contrat = contrat;
	}


	public String getNomUsuel() {
		return getNom()+" "+getPrenom();
	}


	public int compareTo(Employee emp) {
		int cmp = getNom().compareTo(emp.getNom());
		if( cmp!=0)
			return cmp;
		return getPrenom().compareTo(emp.getPrenom());
	}

}

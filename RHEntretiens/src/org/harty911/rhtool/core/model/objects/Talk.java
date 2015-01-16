package org.harty911.rhtool.core.model.objects;

import java.util.Date;

import org.harty911.rhtool.core.model.RHModelObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_entretien")
public class Talk extends RHModelObject {

	public enum ETypeEntretien { CARRIERE, PROFESSIONEL };

	@DatabaseField(dataType=DataType.ENUM_INTEGER)
    private ETypeEntretien type = ETypeEntretien.PROFESSIONEL; // obligatoire

	// COLLABORATEUR

	@DatabaseField(foreign = true)
	private Employee employee = null;
    @DatabaseField
    private int pce = 1;
    @DatabaseField
    private int pcp = 1;
    @DatabaseField(foreign = true)
    private RHEClassif classif = null;
    @DatabaseField
    private String emploi = "";
	@DatabaseField(dataType = DataType.DATE_STRING)
	private Date datePoste = null;

	// COMMUN
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user1 = null;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user2 = null;
	@DatabaseField(dataType = DataType.DATE_STRING)
	private Date date = null;
	@DatabaseField(foreign = true)
	private RHECanal canal = null;
	@DatabaseField
	private int duration = 0;
	@DatabaseField(foreign = true)
    private RHEInitiative initiative = null;
	@DatabaseField
	private String initiative_detail = "";
	
	@ForeignCollectionField
	private ForeignCollection<TalkDoc> docs;

	// CARRIERE

	@DatabaseField(foreign = true)
    private RHEMotif motif1 = null; // obligatoire - Motif principal
	@DatabaseField(foreign = true)
    private RHEMotif motif2 = null; // facultatif - Motif secondaire 	
	
	// PROFESSIONEL

	@DatabaseField(foreign = true)
    private final RHEMotifPro motifpro = null; // obligatoire - Motif Pro
	
	// TODO liste des champs � terminer
	
	public Talk() {
		super();
	}

	public Talk(Employee emp, User user, Talk model) {
		this();
		this.employee = emp;
		this.user1 = user;
		if( model!=null)
			_copyFromModel( model);
	}

	private void _copyFromModel(Talk model) {
		//TODO recuperer les donn�es du dernier entretien
		
	}


	
	/**
	 * Renvoie le collaborateur associ� � l'entretien
	 * @return un objet de type Employee
	 */
	public Employee getEmployee() {
		return employee;
	}

	public int getPCE() {
		return pce;
	}

	public void setPCE(int pce) {
		this.pce = pce;
	}

	public int getPCP() {
		return pcp;
	}

	public void setPCP(int pcp) {
		this.pcp = pcp;
	}

	public RHEClassif getClassif() {
		return classif;
	}

	public void setClassif(RHEClassif classif) {
		this.classif = classif;
	}

	public String getEmploi() {
		return emploi;
	}

	public void setEmploi(String emploi) {
		this.emploi = emploi;
	}
	
	public Date getDatePoste() {
		return datePoste;
	}

	public void setDatePoste(Date datePoste) {
		this.datePoste = datePoste;
	}

	public void setDate(Date dt) {
		this.date = dt;		
	}

	public Date getDate() {
		return this.date;		
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public User getUser1() {
		return user1;
	}

	public void setUser1(User user) {
		this.user1 = user;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user) {
		this.user2 = user;
	}

	public RHECanal getCanal() {
		return canal;
	}

	public void setCanal(RHECanal canal) {
		this.canal = canal;
	}

	public RHEInitiative getInitiative() {
		return initiative;
	}

	public void setInitiative(RHEInitiative initiative) {
		this.initiative = initiative;
	}

	public String getInitiativeDetail() {
		return initiative_detail;
	}
	
	public void setInitiativeDetail(String intitiativeDetail) {
		this.initiative_detail = intitiativeDetail;
	}
	
	public ETypeEntretien getType() {
		return type;
	}
	
	public void setType(ETypeEntretien type) {
		this.type = type;
	}
	

}
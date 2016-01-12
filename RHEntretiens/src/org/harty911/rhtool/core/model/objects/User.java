
package org.harty911.rhtool.core.model.objects;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.harty911.rhtool.core.model.RHModelObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_utilisateur")
public class User extends RHModelObject {
	
	private static final int ROLE_SET_STANDARD = 0x00;
	private static final int ROLE_SET_ADVANCED = 0x0F;
	private static final int ROLE_SET_ADMIN    = 0xFF;

	private static final int ROLE_MSK_ADVANCED = 0x08;
	private static final int ROLE_MSK_ADMIN    = 0x80;
	
	@DatabaseField
	private String login ="";	
	@DatabaseField
    private long password = 0;
    @DatabaseField
    private String prenom ="";
    @DatabaseField
    private String nom ="";
    @DatabaseField
    private int role = ROLE_SET_STANDARD;
    @DatabaseField
    private int replace = -1;

    public User() {  
    }

    public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPrenom() {
		return prenom==null ? "" : prenom;
	}

	public String getNom() {
		return nom==null ? "" : nom;
	}

	public boolean checkPassword(String password) {
		if( this.password==0)
			return false;
		return this.password == _encryptPassword(password);
	}

	public void setPassword(String password) {
		this.password = _encryptPassword(password);
		undelelte();
	}

	public void setStandard() {
		role = ROLE_SET_STANDARD;
	}

	public void setAdvanced() {
		role = ROLE_SET_ADVANCED;
	}

	public void setAdmin() {
		role = ROLE_SET_ADMIN;
	}


	public boolean isAdmin() {
		return (role & ROLE_MSK_ADMIN)!=0;
	}

	public boolean isAdvanced() {
		return (role & ROLE_MSK_ADVANCED)!=0;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	private long _encryptPassword(String pwd) {
		long val = 0;
    	try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update("SALT555".getBytes());
			byte[] md5 = md.digest(pwd.getBytes());
			for( byte b : md5) {
				val <<= 4;
				val += b;
			}
    	} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	return val;
    }
	
	public String getNomUsuel() {
		return getNom()+" "+getPrenom();
	}

	@Override
	public String toString() {
		return super.toString()+" '"+getLogin()+"' ("+prenom+" "+nom+") "+(isAdmin()?"admin":"");
	}
}

package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_contrat")
public class RHEContrat extends RHEnum {

	public static final String TITLE = "Type de contrat"; 

	public RHEContrat() {
		super();
	}	

	public RHEContrat(String text) {
		super(text);
	}
}

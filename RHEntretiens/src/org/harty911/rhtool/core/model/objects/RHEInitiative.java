package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_intitiative")
public class RHEInitiative extends RHEnum {

	public static final String TITLE = "Initiative"; 

	public RHEInitiative() {
		super();
	}	

	public RHEInitiative(String text) {
		super(text);
	}
}

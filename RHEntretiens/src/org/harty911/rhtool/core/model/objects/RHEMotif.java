package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_motif")
public class RHEMotif extends RHEnum {

	public static final String TITLE = "Motif Gestion de carrière"; 

	public RHEMotif() {
		super();
	}	

	public RHEMotif(String text) {
		super(text);
	}
}

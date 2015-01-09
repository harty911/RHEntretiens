package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_motifpro")
public class RHEMotifPro extends RHEnum {

	public static final String TITLE = "Motif Professionnel"; 

	public RHEMotifPro() {
		super();
	}	

	public RHEMotifPro(String text) {
		super(text);
	}
}

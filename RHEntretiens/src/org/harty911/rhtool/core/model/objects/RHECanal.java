package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_canal")
public class RHECanal extends RHEnum {

	public static final String TITLE = "Canal"; 

	public RHECanal() {
		super();
	}	

	public RHECanal(String text) {
		super(text);
	}
}

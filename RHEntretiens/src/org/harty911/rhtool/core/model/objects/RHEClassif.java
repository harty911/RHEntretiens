package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_classif")
public class RHEClassif extends RHEnum {

	public static final String TITLE = "Classification"; 

	public RHEClassif() {
		super();
	}

	public RHEClassif(String text) {
		super(text);
	}	
}

package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHEnum;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_e_typetalkdoc")
public class RHETypeTalkDoc extends RHEnum {

	public static final String TITLE = "Type de document"; 

	public RHETypeTalkDoc() {
		super();
	}	

	public RHETypeTalkDoc(String text) {
		super(text);
	}

}

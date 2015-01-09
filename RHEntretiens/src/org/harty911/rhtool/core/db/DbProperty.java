package org.harty911.rhtool.core.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_property")
public class DbProperty {
	
	@DatabaseField(id = true)
	private String name;
	@DatabaseField
	private String value;
	
	public DbProperty() {
		super();
	}

	public DbProperty( String name, String value) {
		this();
		setName(name);
		setValue(value);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}

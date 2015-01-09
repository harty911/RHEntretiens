package org.harty911.rhtool.core.model;

import com.j256.ormlite.field.DatabaseField;

public abstract class RHEnum extends RHModelObject {

	@DatabaseField
	private String text = "";
	
	public RHEnum() {
		super();
	}

	public RHEnum(String text) {
		setText(text);
	}


	public void setText( String text) {
		this.text = text;
	}	

	public String getText() {
		return text;
	}

	
}

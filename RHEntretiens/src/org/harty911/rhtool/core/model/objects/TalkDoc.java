package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHDocument;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_talk_doc")
public class TalkDoc extends RHDocument {
	
	@DatabaseField(foreign=true)
	private Talk talk;
	@DatabaseField(foreign=true, foreignAutoRefresh=true)
	private RHETypeTalkDoc type;
	@DatabaseField(foreign=true, foreignAutoRefresh=true)
	private String comment;

	protected TalkDoc() {	
	}
	
	public TalkDoc(Talk talk) {
		this();
		this.talk = talk;
	}

	public Talk getTalk() {
		return talk;
	}

		
}

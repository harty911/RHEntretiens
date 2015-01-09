package org.harty911.rhtool.core.model.objects;

import org.harty911.rhtool.core.model.RHDocument;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_talk_doc")
public class TalkDoc extends RHDocument {
	
	@DatabaseField(foreign=true)
	private Talk talk;
	 
}

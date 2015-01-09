package org.harty911.rhtool.core.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public abstract class RHDocument extends RHModelObject {
	
    @DatabaseField
    private final String titre ="";
    @DatabaseField(dataType=DataType.DATE_STRING)
    private final Date date = null;
    @DatabaseField
    private final String filename = "";
    
    public RHDocument() {
    	super(); // for ORMLite
    }
    
    
    
 
    // TODO relative path management, file copy and tmp file for open (outlook like)
}

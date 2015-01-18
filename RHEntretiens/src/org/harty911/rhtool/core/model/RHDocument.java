package org.harty911.rhtool.core.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.harty911.rhtool.RHToolApp;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Database document.
 * Process to create a database document :
 * <li>Create a RHDocument object, and save it to DB (to set an ID) 
 * <li>Upload the file to the DB file system via the named method, successive uploads will replace previous document
 * To get a document from database:
 * <li>Load or Refresh a RHDocument
 * <li>Download via the named method
 * @author harty911
 *
 */
public abstract class RHDocument extends RHModelObject {
	
    @DatabaseField(dataType=DataType.DATE_STRING)
    private Date date = new Date();
    @DatabaseField
    private String origName = null;
    @DatabaseField
    private String baseref = null;
    
    private static final CopyOption[] options = new CopyOption[]{
    	      StandardCopyOption.REPLACE_EXISTING,
    	      StandardCopyOption.COPY_ATTRIBUTES
    	    }; 
    
    public RHDocument() {
    	super(); // for ORMLite
    }
    
    /**
     * Upload the DB file
     * @param source source file to upload
     * @throws IOException if fail
     */
    public void upload( File source) throws IOException {
    	if( getId()==0)
    		throw new IOException("Unable to upload file on a new RHDocument, save it first");
    	File dbFile = getDBFile();

    	Files.copy( source.toPath(), dbFile.toPath(), options);
    	origName = source.getName();
    }
    
    /**
     * Download the DB file
     * @param dest destination file or directory (if a directory is specified, the original filename will be used)
     * @throws IOException if fail
     */
    public void download( File dest) throws IOException {
    	if( origName==null)
    		throw new IOException("No uploaded file for this RHDocument");
    	File dbFile = getDBFile();
    	if( dest.isDirectory())
    		dest = new File( dest, dbFile.getName());
    	
    	Files.copy( dbFile.toPath(), dest.toPath(), options);
    }
    
    /**
     * @return original filename
     */
    public String getName() {
    	return origName;
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
     * build a DB file relative to RHModel base directory 
     * @return
     */
    private File getDBFile() {
    	File docDir = RHToolApp.getModel().getDbConnector().getDocDir();
    	File dstDir = new File( docDir, getClass().getSimpleName());
    	if( baseref==null)
    		baseref = String.format("%06d.dbdoc", getId());
    	return new File( dstDir, baseref);
    }
}

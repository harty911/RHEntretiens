package org.harty911.rhtool.ui.actions;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHDocument;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.RHModelObject;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk;

public class HyperlinkHandler extends LocationAdapter {

	// singleton
	public static HyperlinkHandler DEFAULT = new HyperlinkHandler(); 
	
	public final static Logger LOGGER = Logger.getLogger( HyperlinkHandler.class.getName());

	public static final String SCHEME_DOC    = "rhdoc";
	public static final String SCHEME_SELECT = "rhsel";
//	public static final String SCHEME_EDIT   = "rhmod";
	
	@Override
	public void changing(LocationEvent event) {
		URI uri = URI.create(event.location);
		try {
			if( uri.getScheme().equalsIgnoreCase(SCHEME_DOC)) {
				openDocument( uri);
				event.doit = false;
			}
			else if( uri.getScheme().equalsIgnoreCase(SCHEME_SELECT)) {
				selectObject(uri);
				event.doit = false;
			}
		}
		catch( Exception e) {
			LOGGER.log(Level.SEVERE, "Hyperlink " + uri + " failed", e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Erreur", "Impossible d'ouvrir le lien !\n(voir les logs pour plus de détail)");
		}
	}
	
	protected void selectObject(URI uri) {
		RHModelObject obj = fromURI(uri);	
		if( obj instanceof Talk || obj instanceof Employee) {
			// TODO force Employee and/or Talk selection
			RHToolApp.getWindow().select(obj);
		}
		else {
			throw new IllegalArgumentException("unable to select (type) "+ obj);
		}
	}
	
	protected void openDocument(URI uri) throws Exception {
		RHDocument doc = (RHDocument)fromURI(uri);
		LOGGER.log(Level.INFO,  "Openning Doc : "+doc);
		File tmpDir = new File( System.getProperty("java.io.tmpdir"));
		File docFile = doc.download( tmpDir);
		Program.launch( docFile.toString());
	}
	
	
	/**
	 * Get Object from URI
	 * @param uri
	 * @return RHModelObject
	 * @throws ClassNotFoundException 
	 */
	public static RHModelObject fromURI( URI uri) {
		LOGGER.fine( "Get Object "+uri);
		// No check for scheme 
		RHModel m = RHToolApp.getModel();
		try {
			return m.getObject( m.getClass(uri.getSchemeSpecificPart()), uri.getFragment());
		}
		catch( ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Error in URI "+uri, e);
			return null;
		}
	}

	
	/**
	 * Convert Object to URI
	 * @param uri
	 * @return RHModelObject
	 * @throws ClassNotFoundException 
	 */
	public static URI toURI( String scheme, RHModelObject obj) {
		try {
			return new URI( scheme, obj.getClass().getSimpleName(), String.valueOf(obj.getId()));
		} catch (URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "URI Syntax", e);
			return null;
		}
	}

}

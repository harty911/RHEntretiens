package org.harty911.rhtool;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.core.db.RHDbConnector;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.utils.RHModelUtils;
import org.harty911.rhtool.logging.LogUtil;
import org.harty911.rhtool.ui.MainWindow;
import org.harty911.rhtool.ui.dialogs.LoginDialog;

public class RHToolApp {
	
	public static final boolean TESTING = false;
	
	private static RHDbConnector rhDb = null; 
	private static RHModel rhModel = null;
	private static MainWindow mainWin = null;
	
	public static final String ID = "org.harty911.rhtool";
	public static final String APP_NAME = "Gestion des entretiens RH";
	public static final String APP_VERSION = "0.1";
	
	final public static Logger LOGGER = Logger.getLogger(RHToolApp.class.getName());

	
	public static void main(String[] args) {

		LogUtil.setup();
		
		if( !lockInstance()) {
			Display disp = new Display();
			MessageBox msg = new MessageBox(new Shell());
			msg.setText(APP_NAME);
			msg.setMessage( "L'application '"+APP_NAME+"' est déjà lancée sur cette machine !");
			msg.open();
			disp.dispose();
		}
		
		LOGGER.info("=========== Start Application ==========");
		
		System.setProperty("user.language", "fr");
		
		try {
			// Database root directory
			Map<String,String> opts = parseOptions( args);
			
			File dbDir;
			if( opts.containsKey("-db"))
				dbDir = new File( opts.get("-db"));
			else
				dbDir = new File( ".");
			
			//TODO to be removed before use
			if( opts.containsKey("-testing")) {
				LOGGER.info("--- TESTING MODE ---");
				File f = new File( dbDir, "RHTool.db");
				f.delete();
			}
				
			if( opts.containsKey("-create"))
				rhDb = RHDbConnector.createDatabase(dbDir);
			else
				rhDb = RHDbConnector.openDatabase(dbDir);
			rhModel = new RHModel(rhDb);
			
			if( opts.containsKey("-testing")) {
				RHModelUtils.createTestingData(rhModel);
				rhModel.setUserContext( rhModel.getUsers().get(0));
				
				simpleTest();
			} 
			else {
				// login first (no default user)
				LoginDialog loginDlg = new LoginDialog(rhModel);
				loginDlg.open();
			}
			
			if( rhModel.getUserContext()!=null)
			{
				// User logged : Start with main application dialog
				mainWin = new MainWindow();
				mainWin.run();		
			}
			
			if( rhModel!=null)
				rhModel.close();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,"Fatal Exception !",e);
		}
		LOGGER.info("=========== Quit Application ==========");
	}

	
	private static void simpleTest() {
/*
 		RHEContrat[] cs = getModel().getEnumValues(RHEContrat.class).toArray(new RHEContrat[0]);
 
		Employee[] es = getModel().getEmployees().toArray(new Employee[0]);
		es[0].setContrat( cs[0]);
		getModel().save(es[0]);
		
		Employee[] es2 = getModel().getEmployees().toArray(new Employee[0]);
		System.out.println( es2[0].getContrat().equals(cs[0]));
*/				
	}


	private static Map<String, String> parseOptions(String[] args) {
		Map<String,String> opts = new LinkedHashMap<String,String>();
		String key = null;
		String val = null;
		for( int i=0; i<args.length; i++) {
			if( args[i].startsWith("-")) {
				// register previous key/value
				if( key!=null) {
					opts.put(key, val);
					val = null;
				}
				key = args[i];
			}
			else {
				val = args[i];
			}
		}
		if( key!=null)
			opts.put(key, val);

		return opts;
	}
	
	
	private static boolean lockInstance() {
	    try {
	        final File file = new File(".lock");
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                @Override
					public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                        LOGGER.log(Level.SEVERE, "Unable to remove lock file" + file.getName(), e);
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, "Unable to create and/or lock file", e);
	    }
	    return false;
	}
	

	public static RHModel getModel() {
		return rhModel;
	}

	public static MainWindow getWindow() {
		return mainWin;
	}
}

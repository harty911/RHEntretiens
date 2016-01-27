package org.harty911.rhtool.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.AppInfos;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.objects.RHECanal;
import org.harty911.rhtool.core.model.objects.RHEClassif;
import org.harty911.rhtool.core.model.objects.RHEContrat;
import org.harty911.rhtool.core.model.objects.RHEInitiative;
import org.harty911.rhtool.core.model.objects.RHEMotif;
import org.harty911.rhtool.core.model.objects.RHETypeTalkDoc;
import org.harty911.rhtool.ui.actions.AboutAction;
import org.harty911.rhtool.ui.actions.EditProfileAction;
import org.harty911.rhtool.ui.actions.EmployeeCreateAction;
import org.harty911.rhtool.ui.actions.EmployeeDeleteAction;
import org.harty911.rhtool.ui.actions.EmployeeEditAction;
import org.harty911.rhtool.ui.actions.ExportPptStatsAction;
import org.harty911.rhtool.ui.actions.ExportTalksAction;
import org.harty911.rhtool.ui.actions.ImportEmployeeAction;
import org.harty911.rhtool.ui.actions.ManageEnumsAction;
import org.harty911.rhtool.ui.actions.ManageUsersAction;
import org.harty911.rhtool.ui.actions.QuitAction;
import org.harty911.rhtool.ui.actions.TalkCreateAction;
import org.harty911.rhtool.ui.actions.TalkDeleteAction;
import org.harty911.rhtool.ui.actions.TalkEditAction;
import org.harty911.rhtool.ui.utils.BigToolBarManager;
import org.harty911.rhtool.ui.utils.ContextAction;

public class MainWindow extends ApplicationWindow {
	
	private static final String PREF_RATIO_TOP = "MainWindow.ratioTop";
	private static final String PREF_RATIO_LEFT = "MainWindow.ratioLeft";
	private static final String PREF_HEIGHT = "MainWindow.height";
	private static final String PREF_WIDTH = "MainWindow.width";

	public final static Logger LOGGER = Logger.getLogger( MainWindow.class.getName());

	private Action actionAbout;
	private Action actionQuit;
	private Action actionImport;
	private Action actionExport;
	private Action actionExportStat;

	private Action actionEmployeeCreate;
	private ContextAction actionEmployeeEdit;
	private ContextAction actionEmployeeDelete;
	
	private ContextAction actionTalkCreate;
	private ContextAction actionTalkEdit;
	private ContextAction actionTalkDelete;
	
	private Action actionEditProfile;

	private Action actionManageUser;
	private Action actionManageContract;
	private Action actionManageClassif;	
	private Action actionManageInitiative;
	private Action actionManageCanal;
	private Action actionManageMotif;
	private Action actionManageTypeTalkDoc;
	
	private SashForm mainSash;
	private SashForm talkSash;

	private TalkView talkView;
	private CollabView collabView;
	private TalkPreview talkPreview;
	
	public MainWindow() {
		super(null);	
		
		PreferenceStore ps = RHToolApp.getPreferenceStore();
		ps.setDefault(PREF_HEIGHT, 768);
		ps.setDefault(PREF_WIDTH, 1024);
		ps.setDefault(PREF_RATIO_LEFT, 25);
		ps.setDefault(PREF_RATIO_TOP, 25);
		
		createActions(); 		
		addToolBar( SWT.FLAT | SWT.WRAP); 		
		addMenuBar(); 		
		addStatusLine();
	}
	

	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager mgr = super.createStatusLineManager();
		return mgr;
	}


	private void createActions() {
		actionQuit = new QuitAction();
		actionImport = new ImportEmployeeAction();
		actionExport = new ExportTalksAction();
		actionExportStat = new ExportPptStatsAction();
		actionAbout = new AboutAction();
		actionManageUser = new ManageUsersAction();
		actionEditProfile = new EditProfileAction();
		
		actionEmployeeCreate = new EmployeeCreateAction();
		actionEmployeeDelete = new EmployeeDeleteAction();
		actionEmployeeEdit = new EmployeeEditAction();
		
		actionTalkCreate = new TalkCreateAction();
		actionTalkDelete = new TalkDeleteAction();
		actionTalkEdit =  new TalkEditAction();
				
		actionManageContract = new ManageEnumsAction<RHEContrat>( RHEContrat.class, RHEContrat.TITLE);
		actionManageClassif  = new ManageEnumsAction<RHEClassif>( RHEClassif.class, RHEClassif.TITLE);
		actionManageInitiative = new ManageEnumsAction<RHEInitiative>( RHEInitiative.class, RHEInitiative.TITLE);
		actionManageMotif = new ManageEnumsAction<RHEMotif>( RHEMotif.class, RHEMotif.TITLE);
		actionManageCanal = new ManageEnumsAction<RHECanal>( RHECanal.class, RHECanal.TITLE);
		actionManageTypeTalkDoc = new ManageEnumsAction<RHETypeTalkDoc>( RHETypeTalkDoc.class, RHETypeTalkDoc.TITLE);
	}

	
	@Override
	protected MenuManager createMenuManager() {
		MenuManager mgr = super.createMenuManager();
		   
		MenuManager fileMenu = new MenuManager("&Fichier");
		mgr.add(fileMenu);
		fileMenu.add(actionEditProfile);
		fileMenu.add( actionQuit);
		
		MenuManager talkMenu = new MenuManager("&Entretiens");
		talkMenu.add(actionTalkCreate);
		talkMenu.add(actionTalkEdit);
		talkMenu.add(actionTalkDelete);
		mgr.add(talkMenu);

		if(RHToolApp.getModel().getUserContext().isAdmin()) { 
			MenuManager adminMenu = new MenuManager("&Administration");
			adminMenu.add(actionManageUser);
			adminMenu.add(actionImport);
			adminMenu.add(actionExport);
			adminMenu.add(actionExportStat);
			MenuManager enumMenu =  new MenuManager("Listes de choix");
			enumMenu.add(actionManageClassif);
			enumMenu.add(actionManageContract);
			enumMenu.add(actionManageInitiative);
			enumMenu.add(actionManageCanal);
			enumMenu.add(actionManageMotif);
			enumMenu.add(actionManageTypeTalkDoc);
			adminMenu.add(enumMenu);
			mgr.add(adminMenu);
		}
		
		MenuManager helpMenu = new MenuManager("&Aide");
		helpMenu.add(actionAbout);
		mgr.add(helpMenu);
	    
		return mgr;
	}


	@Override
	protected ToolBarManager createToolBarManager(int style) {
		BigToolBarManager mgr = new BigToolBarManager(style);
		mgr.add(actionEmployeeCreate);
		mgr.add(actionEmployeeEdit);
		mgr.add(actionEmployeeDelete);
		mgr.add(new Separator());
		mgr.add(actionTalkCreate);
		mgr.add(actionTalkEdit);
		if(RHToolApp.getModel().getUserContext().isAdmin()) { 
			mgr.add(new Separator());
			mgr.add(actionExportStat);
		}
		return mgr;
	}


	@Override
	protected Control createContents(Composite parent) {
		mainSash = new SashForm(parent, SWT.HORIZONTAL);
		
		// Collaborateurs
				
		final Group grpCollab = new Group(mainSash, SWT.NONE);
		grpCollab.setText("Collaborateurs");
		grpCollab.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		collabView = new CollabView( grpCollab, SWT.NONE);
		collabView.getViewer().addSelectionChangedListener(actionEmployeeDelete);
		collabView.getViewer().addSelectionChangedListener(actionEmployeeEdit);
		collabView.getViewer().addSelectionChangedListener(actionTalkCreate);
		
		// Entretiens
		
		final Group grpTalk = new Group(mainSash, SWT.NONE);
		grpTalk.setText("Entretiens");
		grpTalk.setLayout(new FillLayout(SWT.VERTICAL));
		talkSash = new SashForm(grpTalk, SWT.VERTICAL);
		
		// liste des entretiens
		
		talkView = new TalkView( talkSash, SWT.NONE);
 		talkView.getViewer().addSelectionChangedListener(actionTalkDelete);
		talkView.getViewer().addSelectionChangedListener(actionTalkEdit);
		collabView.getViewer().addSelectionChangedListener( talkView);

		// preview de l'entretien
		
		talkPreview = new TalkPreview( talkSash, SWT.BORDER);
		talkView.getViewer().addSelectionChangedListener( talkPreview);
		
		// TODO ENHANCE save restore window layout
		PreferenceStore ps = RHToolApp.getPreferenceStore();
		mainSash.setWeights( new int[] { ps.getInt(PREF_RATIO_LEFT), 100-ps.getInt(PREF_RATIO_LEFT) });
		talkSash.setWeights( new int[] { ps.getInt(PREF_RATIO_TOP),  100-ps.getInt(PREF_RATIO_TOP) });
		
		return mainSash;
	}



	@Override
	protected void configureShell(Shell shell) {
		String title = AppInfos.APP_NAME;
		if(RHToolApp.getModel().getUserContext().isAdmin())
			title = title + " (admin)";
		shell.setText(title);
		super.configureShell(shell);
	}
	
	


	@Override
	public boolean close() {
	    // Update Layout preferences
		PreferenceStore ps = RHToolApp.getPreferenceStore();
		ps.setValue(PREF_WIDTH, getShell().getSize().x);
		ps.setValue(PREF_HEIGHT, getShell().getSize().y);
		int[] ws = talkSash.getWeights(); 
		ps.setValue(PREF_RATIO_TOP, ws[0]*100/(ws[0]+ws[1]));
		ws = mainSash.getWeights();
		ps.setValue(PREF_RATIO_LEFT, ws[0]*100/(ws[0]+ws[1]));
		
		// and close
		return super.close();
	}


	@Override
	protected Point getInitialSize() {
		PreferenceStore ps = RHToolApp.getPreferenceStore();
		return new Point( ps.getInt(PREF_WIDTH), ps.getInt(PREF_HEIGHT));
	}


	public void run() {
		  // Don't return from open() until window closes
	    setBlockOnOpen(true);
	    // Open the main window
	    open();
	    // Dispose the display before exit
	    Display.getCurrent().dispose();		
	}
	


	///////////////////////////////////////////////////////////////////////////
	// ERROR REPORTING
	///////////////////////////////////////////////////////////////////////////

	public void reportException( final String userMsg, Throwable t) {

		RHToolApp.LOGGER.log( Level.SEVERE, userMsg, t);

		// convert StackTrace into lines string array 
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
	    String[] lines = sw.toString().split(System.getProperty("line.separator")); // stack trace as a string

	    // Convert to multi-statuses
	    Status[] statuses = new Status[lines.length];
	    for( int i=0; i<lines.length; i++) {
	        statuses[i] = new Status( IStatus.ERROR,  AppInfos.ID, lines[i]);
	    }
	    final MultiStatus ms = new MultiStatus( AppInfos.ID, IStatus.ERROR,
	            statuses, t.getClass().getSimpleName() + " : " + t.getLocalizedMessage(), t);

		Display.getDefault().syncExec(new Runnable() {
	        @Override
	        public void run() {
	            String msg = (userMsg==null) ? "Une erreur s'est produite (voir détail)" : userMsg;
	            ErrorDialog.openError( Display.getDefault().getActiveShell(), "Rapport d'erreur", msg, ms);
	        }
	    });
	}


	public void updateFromModel() {
		// TODO This method is BRUTE FORCE UPDATE, can be removed when Observer is managed
		collabView.refresh();
		talkView.refresh();
		talkPreview.refresh();
	}
}

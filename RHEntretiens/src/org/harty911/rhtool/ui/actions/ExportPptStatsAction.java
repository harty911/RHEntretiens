package org.harty911.rhtool.ui.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.utils.PPTStatsExporter;
import org.harty911.rhtool.ui.dialogs.ExportStatsDialog;
import org.harty911.rhtool.ui.resources.Icons;

public class ExportPptStatsAction extends Action {

	private static final String[] FILE_TYPES = {"Presentation Powerpoint (*.ppt)" };
	private static final String[] FILE_EXTS = { "*.ppt" };

	private static final File TEMPLATE = new File("templates/Stats.ppt");
	
	private File currentDir = null; 

	public ExportPptStatsAction() {
		super("&Exporter statistiques");
		setImageDescriptor( Icons.getDescriptor(Icons.STATS));
	}

	@Override
	public void run() {
		Shell shell = RHToolApp.getWindow().getShell();
		
		// Select params
		final ExportStatsDialog dlgP = new ExportStatsDialog(shell);
		if( dlgP.open() != Window.OK) 
			return;	
		
		// Select file
		final FileDialog dlgF = new FileDialog( shell, SWT.SAVE);
		dlgF.setText("Exporter les statistiques");
		dlgF.setFilterNames(FILE_TYPES);
		dlgF.setFilterExtensions(FILE_EXTS);
		if( currentDir!=null)
			dlgF.setFilterPath(currentDir.getPath());
		
		String filename = dlgF.open();
		if( filename==null)
			return;
		
		final File file = new File(filename);
		currentDir = file.getParentFile();
		
		final RHModel model = RHToolApp.getModel();
		model.setBatchMode(true);
		
		try {
			ProgressMonitorDialog dlg2 = new ProgressMonitorDialog(shell);
			dlg2.run( true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					try {
						monitor.beginTask("Export statistiques PPT '"+file.getName()+"' :", 2);

						PPTStatsExporter exp = new PPTStatsExporter(model, TEMPLATE);
						exp.setPeriod( dlgP.getStart(), dlgP.getEnd());						
						monitor.worked(1);

						exp.export( file);
						monitor.worked(1);

					} catch( Exception e) {
						throw new InvocationTargetException(e);
					}
					finally {
						monitor.done();
					}
				}	
			});

		} catch (InvocationTargetException e) {
			RHToolApp.getWindow().reportException("Erreur d'export PPT '"+file.getName()+"'", e.getCause());
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}		
		
		model.setBatchMode(false);
	}
}

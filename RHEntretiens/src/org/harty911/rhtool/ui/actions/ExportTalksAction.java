package org.harty911.rhtool.ui.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.utils.XLSTalkExporter;
import org.harty911.rhtool.ui.resources.Icons;

public class ExportTalksAction extends Action {

	private static final String[] FILE_TYPES = {"Classeur Microsoft Excel 97-2003 (*.xls)",
		 										"Classeur Microsoft Excel 2007-.. (*.xlsx)" };
	private static final String[] FILE_EXTS = { "*.xls", "*.xlsx"};

	private File currentDir = null; 

	public ExportTalksAction() {
		super("&Exporter entretiens");
		setImageDescriptor( Icons.getDescriptor(Icons.EXPORT));
	}

	@Override
	public void run() {
		Shell shell = RHToolApp.getWindow().getShell();
		FileDialog dlg = new FileDialog( shell, SWT.SAVE);
		dlg.setText("Exporter les entretiens");
		dlg.setFilterNames(FILE_TYPES);
		dlg.setFilterExtensions(FILE_EXTS);
		if( currentDir!=null)
			dlg.setFilterPath(currentDir.getPath());
		
		String filename = dlg.open();
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

					XLSTalkExporter exp = new XLSTalkExporter( RHToolApp.getModel());
					try {
					
						int estim = exp.prepareExportXLS("Entretiens");
						monitor.beginTask("Export Excel entretiens '"+file.getName()+"' :", estim);
						
						while(exp.performExportXLS())
							monitor.worked(1);

						exp.finishExportXLS(file);
						
					} catch( Exception e) {
						throw new InvocationTargetException(e);
					}
					finally {
						monitor.done();
					}
				}	
			});

		} catch (InvocationTargetException e) {
			RHToolApp.getWindow().reportException("Erreur d'import collaborateur XLS '"+file.getName()+"'", e.getCause());
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}		
		
		model.setBatchMode(false);
		
		RHToolApp.getWindow().updateFromModel();
	}
}

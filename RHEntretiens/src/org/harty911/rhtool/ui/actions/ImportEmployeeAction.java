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
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.utils.XLSImporter;
import org.harty911.rhtool.ui.resources.Icons;

public class ImportEmployeeAction extends Action {

	private static final String[] FILE_TYPES = {"Classeur Microsoft Excel 97-2003 (*.xls)",
		 										"Classeur Microsoft Excel 2007-.. (*.xlsx)" };
	private static final String[] FILE_EXTS = { "*.xls", "*.xlsx"};

	protected static final String COLLAB_SHEET_NAME = "Collaborateurs";

	private File currentDir = null; 

	public ImportEmployeeAction() {
		super("&Importer collaborateurs");
		setImageDescriptor( Icons.getDescriptor(Icons.IMPORT));
	}

	@Override
	public void run() {
		Shell shell = RHToolApp.getWindow().getShell();
		FileDialog dlg = new FileDialog( shell, SWT.OPEN);
		dlg.setText("Importer liste de collaborateurs");
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
					XLSImporter<Employee> imp = new XLSImporter<Employee>( RHToolApp.getModel(), Employee.class);

					try {
					
						int estim = imp.prepareImportXLS(file, COLLAB_SHEET_NAME);
						monitor.beginTask("Import Excel collaborateurs '"+file.getName()+"' :", estim);
						
						while(imp.performImportXLS())
							monitor.worked(1);

					} catch( Exception e) {
						throw new InvocationTargetException(e);
					}
					finally {
						imp.finishImportXLS();
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

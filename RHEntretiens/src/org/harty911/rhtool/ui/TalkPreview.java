package org.harty911.rhtool.ui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHDocument;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.utils.TalkPrinter;

public class TalkPreview extends Composite implements ISelectionChangedListener {

	private Browser browser;
	private Talk talk = null;

	public TalkPreview(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		browser = new Browser( this, SWT.NONE);
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changing(LocationEvent event) {
				URI uri = URI.create(event.location);
				if( uri.getScheme().equalsIgnoreCase(RHDocument.SCHEME)) {
					openDocument( uri);
					event.doit = false;
				}
			}
		});
		refresh();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection)event.getSelection();
		if( sel.getFirstElement() instanceof Talk) {
			talk = (Talk)sel.getFirstElement();
			refresh();
		}
	}
	
	public void print() {
		browser.execute("javascript:window.print();");
	}
	
	private void openDocument(URI uri) {
		RHDocument doc = RHToolApp.getModel().getDoc(uri);
		System.out.println( "Openning Doc : "+doc);
		try {
			File tmpDir = new File( System.getProperty("java.io.tmpdir"));
			File docFile = doc.download( tmpDir);
			Program.launch( docFile.toString());
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Erreur", "Impossible d'ouvrir la pièce jointe !");
		}
	}

	public void refresh() {
		try {
			String html = TalkPrinter.toHTML( talk ); 
			if( html==null)
				html = "<center><i><small>Selectionner un entretien pour le visualiser</i></small></center>";
			browser.setText( html);
		} catch (IOException e) {
			MainWindow.LOGGER.log(Level.SEVERE, "Unable to render TalkPreview", e);
		}		
	}
}

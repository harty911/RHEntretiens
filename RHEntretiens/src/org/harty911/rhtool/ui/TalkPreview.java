package org.harty911.rhtool.ui;

import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.actions.HyperlinkHandler;
import org.harty911.rhtool.ui.printer.TalkPrinter;

public class TalkPreview extends Composite implements ISelectionChangedListener {

	private Browser browser;
	private Talk talk = null;
	private boolean refreshing = false;
	
	public TalkPreview(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		browser = new Browser( this, SWT.NONE);
		browser.addLocationListener( new HyperlinkHandler());
		
		// handle refresh context menu
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void changed(ProgressEvent event) {
				if( !refreshing)
					refresh();
			}
			@Override
			public void completed(ProgressEvent event) {
				refreshing = false;
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
	
	public void refresh() {
		try {
			String html = TalkPrinter.toHTML( talk ); 
			if( html==null)
				html = "<center><i><small>Selectionner un entretien pour le visualiser</i></small></center>";
			refreshing = true;
			browser.setText( html);
		} catch (IOException e) {
			MainWindow.LOGGER.log(Level.SEVERE, "Unable to render TalkPreview", e);
		}		
	}
}

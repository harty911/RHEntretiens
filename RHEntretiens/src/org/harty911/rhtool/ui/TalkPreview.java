package org.harty911.rhtool.ui;

import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.ui.utils.TalkPrinter;

public class TalkPreview extends Composite implements ISelectionChangedListener {

	private Browser browser;

	public TalkPreview(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		browser = new Browser( this, SWT.NONE);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection)event.getSelection();
		if( sel.getFirstElement() instanceof Talk) {
			try {
				browser.setText( TalkPrinter.toHTML( (Talk)sel.getFirstElement() ));
			} catch (IOException e) {
				MainWindow.LOGGER.log(Level.SEVERE, "Unable to render TalkPreview", e);
			}
		}
	}
	
	public void print() {
		browser.execute("javascript:window.print();");
	}
}

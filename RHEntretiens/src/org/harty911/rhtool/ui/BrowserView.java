package org.harty911.rhtool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.harty911.rhtool.ui.actions.HyperlinkHandler;

public class BrowserView extends Composite {
	
	public interface IHTMLProvider {
		public String getHTML();
	}

	private Browser browser;
	private String title;
	private IHTMLProvider htmlProvider;
	
	public BrowserView(Composite parent, String title, IHTMLProvider ihtmlProvider) {
		super(parent, SWT.NONE);
		this.title = title;
		this.htmlProvider = ihtmlProvider;
		
		setLayout(new FillLayout(SWT.HORIZONTAL));			
		createContents( this);
		
		refresh();
	}

	private void createContents( Composite parent) {
		Group group = new Group(this, SWT.NONE); 
		group.setText(title);
		group.setLayout(new GridLayout(1, false));

		browser = new Browser( group, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		browser.addLocationListener(HyperlinkHandler.DEFAULT);
	}

	public void refresh() {
		String html = null;
		if( htmlProvider!=null)
			html = htmlProvider.getHTML();
		if( html!=null)
			browser.setText(html);
		else
			browser.setText("<center><i><small>No HTML content to display</small></i></center>");
	}
}

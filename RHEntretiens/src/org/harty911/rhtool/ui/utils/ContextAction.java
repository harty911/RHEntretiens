package org.harty911.rhtool.ui.utils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public abstract class ContextAction extends Action implements ISelectionChangedListener {

	public ContextAction() {
		super();
		setEnabled(false);
	}

	public ContextAction(String text, ImageDescriptor image) {
		super(text, image);
		setEnabled(false);
	}

	public ContextAction(String text, int style) {
		super(text, style);
		setEnabled(false);
	}

	public ContextAction(String text) {
		super(text);
		setEnabled(false);
	}

	private IStructuredSelection currentSelection = null;
	private ISelectionProvider selProvider;
	
	public ISelectionProvider getSelectionProvider() {
		return selProvider;
	}

	@Override
	public void selectionChanged( SelectionChangedEvent event) {
		selProvider = event.getSelectionProvider();
		currentSelection = (IStructuredSelection)event.getSelection();
		boolean valid = isValidSelection( currentSelection);
		setEnabled(valid);
		if(!valid)
			currentSelection = null;
	}

	
	@Override
	public void run() {
		execute( currentSelection);
	}

	protected abstract boolean isValidSelection(IStructuredSelection sel);

	protected abstract boolean execute(IStructuredSelection sel);

}

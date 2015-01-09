package org.harty911.rhtool.ui.utils;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHEnum;

public class EHEnumController <T extends RHEnum> {
	
	private final StructuredViewer viewer;

	public class EnumLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((RHEnum)element).getText();
		}
	}

	
	public EHEnumController( StructuredViewer viewer, Class<T> clazz) {
		this.viewer = viewer;
		viewer.setContentProvider( ArrayContentProvider.getInstance() );
		viewer.setLabelProvider( new EnumLabelProvider());
		List<T> lst = RHToolApp.getModel().getEnumValues(clazz);
		viewer.setInput(lst.toArray());
	}
	
	
	public void setValue( T enumObj) {
		if( enumObj!=null)
			viewer.setSelection( new StructuredSelection(enumObj));	
		else
			viewer.setSelection( StructuredSelection.EMPTY);
	}

	
	@SuppressWarnings("unchecked")
	public T getValue() {
		IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
		if( sel.isEmpty())
			return null;
		else
			return (T)sel.getFirstElement();
	}
}

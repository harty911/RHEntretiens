package org.harty911.rhtool.ui.utils;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.harty911.rhtool.core.model.RHEnum;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk.ETypeEntretien;
import org.harty911.rhtool.core.model.objects.User;

public class ObjectViewerController <T> {
	
	private final StructuredViewer viewer;

	public class ObjectLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if( element instanceof RHEnum)
				return ((RHEnum)element).getText();
			else if( element instanceof Employee)
				return ((Employee)element).getNomUsuel();
			else if( element instanceof User)
				return ((User)element).getNomUsuel();
			else if( element instanceof ETypeEntretien) {
				switch((ETypeEntretien)element) {
					case CARRIERE:
						return "Entretien de carrière";
					case PROFESSIONEL:
						return "Entretien professionnel";
					default:
						return "";
				}
			}
			else 
				return element.toString();
		}
	}

	protected ObjectViewerController( StructuredViewer viewer) {
		this.viewer = viewer;
		viewer.setContentProvider( ArrayContentProvider.getInstance() );
		viewer.setLabelProvider( new ObjectLabelProvider());
	}
	
	public ObjectViewerController( StructuredViewer viewer, List<T> list) {
		this(viewer);
		viewer.setInput(list.toArray());
	}

	
	public ObjectViewerController( StructuredViewer viewer, T[] list) {
		this(viewer);
		viewer.setInput(list);
	}
	
	
	public void setValue( T obj) {
		if( obj!=null)
			viewer.setSelection( new StructuredSelection(obj));	
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

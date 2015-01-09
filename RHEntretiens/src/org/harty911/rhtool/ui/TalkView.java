package org.harty911.rhtool.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk;

public class TalkView extends Composite {

	private TableViewer tableViewer;
	private TalkFilter viewerFilter;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TalkView(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		createContents();
	}


	private void createContents() {
		
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE);
		col1.getColumn().setAlignment(SWT.RIGHT);
		col1.getColumn().setText("Matricule");
		col1.getColumn().setWidth(66);
		col1.setLabelProvider( new TalkLabelProvider(0));
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE);
		col2.getColumn().setText("NOM Prénom");
		col2.getColumn().setWidth(175);
		col2.setLabelProvider(new TalkLabelProvider(1));
		
		tableViewer.setComparator( new TalkComparator());

		viewerFilter = new TalkFilter();
		tableViewer.setFilters( new ViewerFilter[] { viewerFilter });

		tableViewer.setContentProvider( new TalkContentProvider());
		tableViewer.setInput(RHToolApp.getModel());
	}

	/** Talk from RHModel content provider */
	public class TalkContentProvider implements IStructuredContentProvider {

		Object[] cache = null;
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object input) {}
		
		@Override
		public Object[] getElements(Object input) {
			if( input instanceof RHModel ) {
				RHModel model = ((RHModel)input);
				return model.getTalks().toArray();
			}
			return null;
		}

		@Override
		public void dispose() {}
	}
	
	public class TalkLabelProvider extends ColumnLabelProvider {
		final int col;
		
		public TalkLabelProvider(int column) {
			this.col = column;
		}
		
		@Override
		public Image getImage(Object o) {
			return null;
		}
		
		@Override
		public String getText(Object o) {
			if(o instanceof Talk) {
				Talk talk = (Talk)o;	
				if( col==0)
					return String.valueOf(talk.getId());
				if( col==1)
					return String.valueOf(talk.getId());
			}
			return "";
		}
	}
	
	
	public class TalkFilter extends ViewerFilter {
		
		@Override
		public boolean select( Viewer viewer, Object parentElem, Object elem) {
			return true;
		}
	}

	
	public void refresh() {
		tableViewer.refresh();		
	}


	public Viewer getViewer() {
		return tableViewer;
	}

	
	public class TalkComparator extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			Employee emp1 = (Employee)e1;	
			Employee emp2 = (Employee)e2;
			return emp1.compareTo(emp2);
		}
	}
}

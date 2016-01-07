package org.harty911.rhtool.ui;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.ui.utils.ControlUtils;

public class TalkView extends Composite implements ISelectionChangedListener {

	private TableViewer tableViewer;
	private TalkFilter viewerFilter;

	enum ECol {
		DATE, USER, CANAL, DUREE, MOTIF, COLLAB
	};
	
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
		
		// DATE
		
		TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.getColumn().setAlignment(SWT.RIGHT);
		col.getColumn().setText("Date");
		col.getColumn().setWidth(80);
		col.setLabelProvider( new TalkLabelProvider(ECol.DATE));
				
		// Collab
		
		col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.getColumn().setText("Collaborateur");
		col.getColumn().setWidth(180);
		col.setLabelProvider(new TalkLabelProvider(ECol.COLLAB));

		// Motif
		
		col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.getColumn().setText("Motif");
		col.getColumn().setWidth(180);
		col.setLabelProvider(new TalkLabelProvider(ECol.MOTIF));

		// Canal
		
		col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.getColumn().setText("Canal");
		col.getColumn().setWidth(80);
		col.setLabelProvider(new TalkLabelProvider(ECol.CANAL));
		
		// Durée
		
		col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.getColumn().setText("Durée");
		col.getColumn().setWidth(70);
		col.setLabelProvider(new TalkLabelProvider(ECol.DUREE));

		// User1
		
		col = new TableViewerColumn(tableViewer, SWT.NONE);
		col.getColumn().setText("RH");
		col.getColumn().setWidth(180);
		col.setLabelProvider(new TalkLabelProvider(ECol.USER));
		
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
		final ECol col;
		
		public TalkLabelProvider(ECol column) {
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
				switch( col) {
					case DATE:
						return ControlUtils.printDate( talk.getDate());
					case USER:
						User u = talk.getUser1();
						// ORMLite autorefresh : RHToolApp.getModel().refresh(u);
						return u.getNomUsuel();
					case CANAL:
						return ControlUtils.printEnum(talk.getCanal());					
					case DUREE:
						return ControlUtils.printDuration( talk.getDuration());					
					case MOTIF:
						return ControlUtils.printEnum( talk.getMotif1());					
					case COLLAB:
						Employee e = talk.getEmployee();
						return e.getNomUsuel();
				}
			}
			return "";
		}
	}
	
	
	public class TalkFilter extends ViewerFilter {
		
		private Collection<Employee> employees = null;

		@Override
		public boolean select( Viewer viewer, Object parentElem, Object elem) {
			if( employees==null || employees.isEmpty())
				return false;
			
			if( elem instanceof Talk) {
				Talk talk = (Talk)elem;
				return( !talk.getEmployee().isDeleted() && employees.contains( talk.getEmployee()));
			}
			return false;
		}

		public void setEmployees( Collection<Employee> employees) {
			this.employees = employees;
			refresh();
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
			// Date decroissante
			Talk t1 = (Talk)e1;
			Talk t2 = (Talk)e2;
			return - t1.getDate().compareTo(t2.getDate());
		}
	}


	/**
	 * Manage Talk Filter when Employee is selected selection
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection)event.getSelection();
		if( sel.getFirstElement() instanceof Employee) {
			// Employee Filter
			viewerFilter.setEmployees( (List<Employee>)sel.toList());
		}
		else {
			// no Filter (all displayed collabs or null)
			viewerFilter.setEmployees( null);
		}
	}
}

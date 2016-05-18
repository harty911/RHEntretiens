package org.harty911.rhtool.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;

public class CollabView extends Composite {

	private TableViewer tableViewer;
	private Text filterText;
	private TextFilter viewerFilter;

	private Object[] filteredList = new Object[]{};


	public CollabView(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.HORIZONTAL));		
		
		createContents( this);
	}


	private void createContents( Composite parent) {
		Group group = new Group(this, SWT.NONE); 
		group.setText("Collaborateurs");
		group.setLayout(new GridLayout(3, false));

		Label lblFiltre = new Label(group, SWT.NONE);
		lblFiltre.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFiltre.setText("Filtre:");
		
		filterText = new Text(group, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				viewerFilter.setFilterText(filterText.getText());
				tableViewer.refresh();
				selectAll();
			}
		});
		
		Button butAll = new Button(group, SWT.NONE);
		butAll.setText("Tous");
		butAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filterText.setText(""); // will fire FilterModify
			}			
		});
		
		tableViewer = new TableViewer(group, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI );
		Table table = tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_table.widthHint = 259;
		table.setLayoutData(gd_table);
		tableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn col1 = new TableViewerColumn(tableViewer, SWT.NONE);
		col1.getColumn().setAlignment(SWT.RIGHT);
		col1.getColumn().setText("Matricule");
		col1.getColumn().setWidth(66);
		col1.setLabelProvider( new CollabLabelProvider(0));
		
		TableViewerColumn col2 = new TableViewerColumn(tableViewer, SWT.NONE);
		col2.getColumn().setText("NOM Prénom");
		col2.getColumn().setWidth(175);
		col2.setLabelProvider(new CollabLabelProvider(1));

		// DoubleClic anywhere => unselect All
		//  (not using addDoubleClickListener, because linked to a row and not anywhere) 
		tableViewer.getControl().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				selectAll();
			}
		});
			

		tableViewer.setComparator( new CollabComparator());
		viewerFilter = new TextFilter();
		tableViewer.setFilters( new ViewerFilter[] { viewerFilter });
		tableViewer.setContentProvider( new CollabContentProvider());
		tableViewer.setInput(RHToolApp.getModel());
	}

	private void selectAll() {
		tableViewer.getTable().selectAll();
		tableViewer.setSelection( new StructuredSelection(filteredList));		
	}
	
	/** Employee from RHModel content provider */
	public class CollabContentProvider implements IStructuredContentProvider {

		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object input) {}
		
		@Override
		public Object[] getElements(Object input) {
			if( input instanceof RHModel ) {
				RHModel model = ((RHModel)input);
				return model.getEmployees().toArray();
			}
			return null;
		}

		@Override
		public void dispose() {}
	}
	
	public class CollabLabelProvider extends ColumnLabelProvider {
		final int col;
		
		public CollabLabelProvider(int column) {
			this.col = column;
		}
		
		@Override
		public Image getImage(Object o) {
			return null;
		}
		
		@Override
		public String getText(Object o) {
			if(o instanceof Employee) {
				Employee emp = (Employee)o;	
				if( col==0)
					return String.valueOf(emp.getMatricule());
				if( col==1)
					return emp.getNomUsuel();
			}
			return "";
		}
	}
	
	
	public class TextFilter extends ViewerFilter {
		
		private String filter = null;
		
		void setFilterText( String txt) {
			filter = txt.toUpperCase();
		}
		
		@Override
		public boolean select( Viewer viewer, Object parentElem, Object elem) {
			if( filter==null || filter.isEmpty())
				return true;

			if( elem instanceof Employee) {
				Employee emp = (Employee)elem;	
				if( emp.getNomUsuel().toUpperCase().contains(filter) 
				 || String.valueOf(emp.getMatricule()).contains(filter))
					return true;
			}
			return false;
		}

		@Override
		public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
			filteredList = super.filter(viewer, parent, elements);
			return filteredList;
		}

	}

	
	public void refresh() {
		tableViewer.refresh();		
	}


	public Viewer getViewer() {
		return tableViewer;
	}

	
	public class CollabComparator extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			Employee emp1 = (Employee)e1;	
			Employee emp2 = (Employee)e2;
			return emp1.compareTo(emp2);
		}
	}
}

package org.harty911.rhtool.ui.dialogs;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.BigToolBarManager;
import org.harty911.rhtool.ui.utils.ContextAction;

public class ManageUsersDialog extends Dialog {

	private TableViewer tableViewer;
	
	private final RHModel model;

	public boolean withTrash;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ManageUsersDialog(Shell parentShell) {
		super(parentShell);
		model = RHToolApp.getModel();
		
		setBlockOnOpen(true);
	}

	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite)super.createDialogArea(parent);
		
		ToolBarManager  toolMgr = new BigToolBarManager();
		toolMgr.add(new CreateAction());
		EditAction actionEdit = new EditAction();
		toolMgr.add( actionEdit);
		DeleteAction actionDelete = new DeleteAction();
		toolMgr.add( actionDelete);
		toolMgr.add(new ShowDeletedAction());
		ToolBar toolBar = toolMgr.createControl(area);
		toolBar.setLayoutData(new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		tableViewer = createTableViewer(area);
		tableViewer.setContentProvider( new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			@Override
			public void dispose() {
			}
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object input) {
				List<User> users = new LinkedList<User>();
				for( User each : (Collection<User>)input) {
					if( withTrash || !each.isDeleted() )
						users.add(each);
				}
				return users.toArray();
			}
		});

		tableViewer.addDoubleClickListener( actionEdit);
		tableViewer.addSelectionChangedListener(actionEdit);
		tableViewer.addSelectionChangedListener(actionDelete);
		
		refresh();
		
		return area;
	}

	
	public void refresh() {
		tableViewer.setInput( model.getUsers());
		tableViewer.refresh();
	}


	private TableViewer createTableViewer(Composite area) {
		TableViewer tblView = new TableViewer(area, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tblView.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

		// User
		TableViewerColumn colLogin = new TableViewerColumn(tblView, SWT.NONE);
		colLogin.getColumn().setWidth(100);
		colLogin.getColumn().setText("Utilisateur");
		colLogin.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((User)element).getLogin();
			}
			@Override
			public Color getForeground(Object element) {
				return userColor(element);
			}
			@Override
			public Image getImage(Object element) {
				User user = (User)element;
				if( user.isDeleted())
					return Icons.getImage(Icons.DELETE);
				else if( user.isAdmin())
					return Icons.getImage(Icons.USER_ADMIN);
				else
					return null;
			}
		});

		// real Name
		TableViewerColumn colName = new TableViewerColumn(tblView, SWT.NONE);
		colName.getColumn().setWidth(260);
		colName.getColumn().setText("Nom usuel");
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				User user = (User) element;
				return user.getPrenom() + " " + user.getNom();
			}
			@Override
			public Color getForeground(Object element) {
				return userColor(element);
			}
		});
		
		return tblView;
	}


	private Color userColor(Object element) {
		User user = (User)element;
		if( user.isDeleted()) 
			return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
		else
			return null;
	}




	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Fermer",	true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(405, 309);
	}
	
	
	
	public class CreateAction extends Action {
		
		public CreateAction() {
			super("Nouveau", Icons.getDescriptor(Icons.CREATE));
		}

		@Override
		public void run() {
			User user = new User();
			UserFormDialog dlg = new UserFormDialog( getShell(), user);
			dlg.setAdminMode(true);
			if( dlg.open()==Window.OK) {
				model.save(user);
			}
			refresh();
		}
		
	}

	
	public class EditAction extends ContextAction implements IDoubleClickListener {
		
		public EditAction() {
			super("Modifier", Icons.getDescriptor(Icons.EDIT));
		}

		@Override
		protected boolean isValidSelection(IStructuredSelection sel) {
			User user = (User)sel.getFirstElement();
			return ( user!=null);
		}

		@Override
		protected boolean execute(IStructuredSelection sel) {
			User user = (User)sel.getFirstElement();
			model.refresh(user);
			UserFormDialog dlg = new UserFormDialog( getShell(), user);
			dlg.setAdminMode(true);
			if( dlg.open()==Window.OK) {
				model.save(user);
				refresh();
			}
			return true;
		}
		
		@Override
		public void doubleClick(DoubleClickEvent event) {
			IStructuredSelection sel = (IStructuredSelection) event.getSelection();
			if( isValidSelection(sel))
				execute(sel);
		}
	}

	public class DeleteAction extends ContextAction {
		
		public DeleteAction() {
			super("Supprimer", Icons.getDescriptor(Icons.DELETE));
		}

		@Override
		protected boolean isValidSelection(IStructuredSelection sel) {
			User user = (User)sel.getFirstElement();
			return ( user!=null && ! "admin".equals(user.getLogin()));
		}

		@Override
		protected boolean execute(IStructuredSelection sel) {
			User user = (User)sel.getFirstElement();
			user.delete();
			model.save(user);
			refresh();
			return true;
		}
	}


	public class ShowDeletedAction extends Action {
		
		public ShowDeletedAction() {
			super("Corbeille", AS_CHECK_BOX);
			setImageDescriptor( Icons.getDescriptor(Icons.TRASH));
			setChecked(withTrash);
		}

		@Override
		public void run() {
			withTrash = isChecked();
			refresh();
		}
	}
}

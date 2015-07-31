package org.harty911.rhtool.ui.dialogs;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHEnum;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.BigToolBarManager;

public class ManageEnumsDialog<T extends RHEnum> extends Dialog {

	private ListViewer listViewer;
	
	private final RHModel model;

	public boolean withTrash;

	private final Class<T> enumClass;
	private final String enumTitle;

	private static final String CLIPBOARD_SEP = System.getProperty("line.separator");

	private Clipboard clipboard;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ManageEnumsDialog(Shell parentShell, Class<T> enumClass, String enumTitle) {
		super(parentShell);
		this.enumClass = enumClass;
		this.enumTitle = enumTitle;
		
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
		toolMgr.add(new EditAction());
		toolMgr.add(new DeleteAction());
		toolMgr.add(new CopyAction());
		toolMgr.add(new PasteAction());
		ToolBar toolBar = toolMgr.createControl(area);
		toolBar.setLayoutData(new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		listViewer = new ListViewer(area, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		listViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RHEnum)element).getText();
			}
			
			@Override
			public Color getForeground(Object element) {
				RHEnum rhe = (RHEnum)element;
				if( rhe.isDeleted()) 
					return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);

				return null;
			}
			@Override
			public Image getImage(Object element) {
				RHEnum rhe = (RHEnum)element;
				if( rhe.isDeleted())
					return Icons.getImage(Icons.DELETE);
				else
					return null;
			}
		});

		listViewer.setContentProvider( new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			@Override
			public void dispose() {
			}
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object input) {
				List<T> rhe = new LinkedList<T>();
				for( T each : (Collection<T>)input) {
					if( withTrash || !each.isDeleted() )
						rhe.add(each);
				}
				return rhe.toArray();
			}
		});
		
		listViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)listViewer.getSelection();
				@SuppressWarnings("unchecked")
				T rhe = (T)sel.getFirstElement();
				if( rhe!=null)
					editEnumText( rhe);
			}
		});
		
		clipboard = new Clipboard( getShell().getDisplay());
		
		refresh();
		
		return area;
	}

	
	public void refresh() {
		listViewer.setInput( model.getEnumValues(enumClass));
		listViewer.refresh();
	}

	
	private void editEnumText( T rhe) {
		
		IInputValidator textValidator = new IInputValidator() {
			@Override
			public String isValid(String newText) {
				if( newText.isEmpty())
					return "Le nom ne peut être vide";
				if( model.enumValueExists(enumClass, newText))
					return "Ce nom figure déjà dans la liste, selectionner un autre nom.";
				return null;
			}
		};
				
		InputDialog dlg = new InputDialog(getShell(), 
				enumTitle, 
				"Entrer le texte", 
				rhe.getText(), 
				textValidator);

		if( dlg.open()==Window.OK) {
			rhe.setText(dlg.getValue());
			model.save(rhe);
			refresh();
		}
	}
	
	private void insertEnumText( String text) {
		if( model.enumValueExists(enumClass, text)) {
			return;
		}
	
		try {
			T rhe = enumClass.newInstance();
			rhe.setText(text);
			model.save(rhe);
		} catch ( Exception e) {
			RHToolApp.LOGGER.log(Level.SEVERE, "Unable to create "+enumClass.getSimpleName()+" '"+text+"'", e);
			e.printStackTrace();
		}
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
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Gestion de liste '"+enumTitle+"'");
	}
	
	
	public class CreateAction extends Action {

		public CreateAction() {
			super("Nouveau", Icons.getDescriptor(Icons.CREATE));
		}

		@Override
		public void run() {
			try {
				T rhe = enumClass.newInstance();
				editEnumText( rhe);
			} catch ( Exception e) {
				RHToolApp.LOGGER.log(Level.SEVERE, "Unable to create '"+enumClass.getSimpleName()+"'", e);
				e.printStackTrace();
			}
		}
		
	}

	public class EditAction extends Action {
		
		public EditAction() {
			super("Modifier", Icons.getDescriptor(Icons.EDIT));
		}

		@Override
		public void run() {
			IStructuredSelection sel = (IStructuredSelection)listViewer.getSelection();
			@SuppressWarnings("unchecked")
			T rhe = (T)sel.getFirstElement();
			if( rhe!=null)
				editEnumText( rhe);
		}
	}

	public class DeleteAction extends Action {
		
		public DeleteAction() {
			super("Supprimer", Icons.getDescriptor(Icons.DELETE));
		}

		@Override
		public void run() {
			IStructuredSelection sel = (IStructuredSelection)listViewer.getSelection();
			boolean res = MessageDialog.openQuestion(getShell(), "Confirmation de suppression", 
					"Voulez-vous vraiment supprimer les éléments de la liste ?");

			if( res) {
				for( Object each : sel.toList()) {
					@SuppressWarnings("unchecked")
					T rhe = (T)each;
	
					rhe.delete();
					model.save(rhe);
				}
				refresh();
			}
		}
		
	}
	
	
	public class CopyAction extends Action {
		
		public CopyAction() {
			super("Copier", Icons.getDescriptor(Icons.COPY));
		}

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();
			IStructuredSelection sel = (IStructuredSelection)listViewer.getSelection();
			for( Object each : sel.toList()) {
				@SuppressWarnings("unchecked")
				T rhe = (T)each;
				if( sb.length()>0) sb.append( CLIPBOARD_SEP);
				sb.append( rhe.getText());
			}
			clipboard.setContents( 	new Object[] { sb.toString() },
									new Transfer[] { TextTransfer.getInstance()});

		}
	}
	

	public class PasteAction extends Action {
		
		public PasteAction() {
			super("Coller", Icons.getDescriptor(Icons.PASTE));
		}

		@Override
		public void run() {
			String data = (String)clipboard.getContents(TextTransfer.getInstance());
			for( String each : data.split("\\n")) {
				String text = each.trim();
				if( !text.isEmpty())
					insertEnumText(text);
			}
			refresh();
		}
	}

}

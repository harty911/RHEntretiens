package org.harty911.rhtool.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHDocument;
import org.harty911.rhtool.core.model.objects.RHETypeTalkDoc;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.TalkDoc;
import org.harty911.rhtool.ui.resources.Icons;
import org.harty911.rhtool.ui.utils.ObjectViewerController;

/**
 * orphelin TalkDoc are created in this page : a talkdoc need to be saved to enable upload
 * the docs will be saved after Talk is saved (Terminate) to refresh or delete ... 
 * @author harty911
 *
 */
public class TalkPageDocs extends WizardPage {

	private Talk talk;
	
	private static final int NB_MAX_DOCS = 6;
	private final TalkDocControl[]  ctrFile = new TalkDocControl[NB_MAX_DOCS];
	private final List<TalkDoc> docs = new LinkedList<>();
	private final List<TalkDoc> newDocs = new LinkedList<>();

	
	public TalkPageDocs(Talk talk) {
		super("DOCUMENTS", "Documents joints", null);
		this.talk = talk;
		// TODO : intialiaze with existing docs
		
		// create an "not uploaded" file for the end of list
		appendEmptyDoc();
	}


	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		FillLayout fl_container = new FillLayout(SWT.VERTICAL);
		container.setLayout(fl_container);
		
		for( int i=0; i<NB_MAX_DOCS; i++)
			ctrFile[i] = new TalkDocControl(container, SWT.NONE);

		rebuildList();
		
		setControl(container);
	}


	/**
	 * Add an empty document to the end of list
	 */
	private void appendEmptyDoc() {
		TalkDoc emptyDoc = new TalkDoc(talk);
		RHToolApp.getModel().save(emptyDoc);
		docs.add( emptyDoc);
		// list new docs,
		newDocs.add( emptyDoc);
	}
	
	/**
	 * In case of CANCEL delete new created documents
	 */
	public void deleteNewDocs() {
		for( RHDocument doc : newDocs) {
			doc.delete();
			RHToolApp.getModel().save(doc);
		}
	}

	
	/**
	 * rebuild controls based on list
	 */
	private void rebuildList() {
		// last doc should be "saved but not yet uploaded"
		int i=0;
		for( TalkDoc doc : docs ) {
			if( doc.isDeleted()) 
				continue;
			// set control
			ctrFile[i].setDoc(doc);
			// next control
			if( i++ >= NB_MAX_DOCS) break;
		}
		
		// hide last controls
		while( i<NB_MAX_DOCS) {
			ctrFile[i].setDoc(null);
			i++;
		}
	}
	
	
	/** 
	 * Control to manage each TalkDoc element
	 * - if attached doc is uploaded : only delete is allowed
	 * - if no attached doc : propose to attach
	 * - if null : hide item
	 */
	public class TalkDocControl extends Composite implements SelectionListener, ISelectionChangedListener {

		private TalkDoc doc;
		private Text txtFile;
		private Button btnDoIt;
		private ObjectViewerController<RHETypeTalkDoc> cmbCtrlType;

		final List<RHETypeTalkDoc> values = RHToolApp.getModel().getEnumValues(RHETypeTalkDoc.class);
		
		public TalkDocControl(Composite parent, int style) {
			super(parent, style);
			
			GridLayout gl_container = new GridLayout(3, false);
			gl_container.horizontalSpacing = 10;
			gl_container.verticalSpacing = 10;
			setLayout(gl_container);
			
			ComboViewer cmbType = new ComboViewer(this, SWT.READ_ONLY);
			Combo combo = cmbType.getCombo();
			GridData gd_cmbType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			gd_cmbType.heightHint = 25;
			combo.setLayoutData(gd_cmbType);
			cmbCtrlType = new ObjectViewerController<RHETypeTalkDoc>( cmbType, values); 
			
			txtFile = new Text( this, SWT.BORDER | SWT.READ_ONLY);
			GridData gd_txtFile = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_txtFile.widthHint = 180;
			txtFile.setLayoutData(gd_txtFile);
			
			btnDoIt = new Button( this, SWT.NONE);
			btnDoIt.addSelectionListener(this);
			GridData gd_btn = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_btn.widthHint = 30;
			btnDoIt.setLayoutData(gd_btn);
						
			cmbType.addSelectionChangedListener(this);

			setDoc(null);
		}
		
		
		public void setDoc(TalkDoc doc) {
			this.doc = doc;
			if( doc!=null && doc.getId()!=0 && !doc.isDeleted()) {
				setVisible(true);
				if( doc.isUploaded()) {
					// uploaded : only delete button
					btnDoIt.setImage(Icons.getImage(Icons.DELETE));;
					txtFile.setText(doc.getName());
				}
				else {	
					// not yet saved : add/upload button
					btnDoIt.setImage(Icons.getImage(Icons.CREATE));;
					txtFile.setText("Cliquez sur + pour ajouter...");
				}
				cmbCtrlType.setValue(doc.getType());
			}
			else {
				// no doc : hide
				setVisible(false);				
			}
		}


		@Override
		public void widgetSelected(SelectionEvent e) {

			if( doc.isUploaded()) {
				// remove file
				doc.delete();
				rebuildList();
			}
			else {
				// upload file
				Shell shell = getShell();
				FileDialog dlg = new FileDialog( shell, SWT.OPEN);
				dlg.setText("Ajouter une pièce jointe");
				
				String filename = dlg.open();
				if( filename==null)
					return;
				
				final File file = new File(filename);
				try{
					doc.upload(file);
					// create an "not uploaded" file for the end of list
					appendEmptyDoc();
				
				} catch( IOException ex) {
					RHToolApp.getWindow().reportException("Impossible d'ajouter '"+file.getName()+"'", ex);
				};
				
				rebuildList();
			}
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if( doc!=null)
				doc.setType( cmbCtrlType.getValue());
		}
	}


	/**
	 * @return All docs including marked as deleted
	 */
	public List<TalkDoc> getAllDocs() {
		return docs;
	}

}

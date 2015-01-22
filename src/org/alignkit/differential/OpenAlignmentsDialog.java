package org.alignkit.differential;

import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class OpenAlignmentsDialog extends Dialog {

	//protected List<List<A3Entry>> result = new ArrayList<List<A3Entry>>();
	protected List<String> result = null; 
	protected Shell shlOpenAlignments;
	private Text textAlign1;
	private Text textAlign2;
	
	private static Logger logger = Logger.getLogger(Differential.class);

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public OpenAlignmentsDialog(Shell parent, int style) {
		super(parent, style);
		setText("Open Alignments");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public List<String> open() {
		createContents();
		shlOpenAlignments.open();
		shlOpenAlignments.layout();
		Display display = getParent().getDisplay();
		while (!shlOpenAlignments.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlOpenAlignments = new Shell(getParent(), getStyle());
		shlOpenAlignments.setSize(450, 148);
		shlOpenAlignments.setText("Open Alignments");
		shlOpenAlignments.setLayout(new GridLayout(3, false));
		
		Label lblAlignment = new Label(shlOpenAlignments, SWT.NONE);
		lblAlignment.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAlignment.setText("Alignment 1");
		
		textAlign1 = new Text(shlOpenAlignments, SWT.BORDER);
		textAlign1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowseAlignment1 = new Button(shlOpenAlignments, SWT.NONE);
		btnBrowseAlignment1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Align1 Browse Selected");
				FileDialog alignFileDialog = new FileDialog(shlOpenAlignments, SWT.NONE);
				alignFileDialog.setText("Open Align File 1");
				textAlign1.setText(alignFileDialog.open());
			}
		});
		btnBrowseAlignment1.setText("Browse");
		
		Label lblAlignment_1 = new Label(shlOpenAlignments, SWT.NONE);
		lblAlignment_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAlignment_1.setText("Alignment 2");
		
		textAlign2 = new Text(shlOpenAlignments, SWT.BORDER);
		textAlign2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowseAlignment2 = new Button(shlOpenAlignments, SWT.NONE);
		btnBrowseAlignment2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Align2 Browse Selected");
				FileDialog alignFileDialog = new FileDialog(shlOpenAlignments, SWT.NONE);
				alignFileDialog.setText("Open Align File 2");
				textAlign2.setText(alignFileDialog.open());
			}
		});
		btnBrowseAlignment2.setText("Browse");
		
		Composite composite = new Composite(shlOpenAlignments, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.center = true;
		composite.setLayout(rl_composite);
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("Cancel selected");
				shlOpenAlignments.close();
			}
		});
		btnCancel.setText("Cancel");
		
		Button btnOk = new Button(composite, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("OK Selected");
				
				//check to make sure results exist?
				
				result = new ArrayList<String>();
				result.add(textAlign1.getText()); //add align 1
				result.add(textAlign2.getText()); //add align 2
				
				shlOpenAlignments.close();
			}
		});
		btnOk.setText("Ok");
		new Label(shlOpenAlignments, SWT.NONE);
		new Label(shlOpenAlignments, SWT.NONE);
		new Label(shlOpenAlignments, SWT.NONE);


	}
	
	private void loadAlignments(List<String> filenames) {
		
	}

}

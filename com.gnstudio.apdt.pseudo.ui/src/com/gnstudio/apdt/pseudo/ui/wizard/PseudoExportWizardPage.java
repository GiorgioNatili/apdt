/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.pseudo.ui.wizard;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.gnstudio.apdt.APDTImages;

import com.gnstudio.apdt.pseudo.ui.APDPseudoMessages;
import com.gnstudio.apdt.pseudo.ui.PseudoPlugin;

public class PseudoExportWizardPage extends WizardPage {
	private static final String OVERWRITE = "PseudoExportWizardPage.isOverwrite";//$NON-NLS-1$;
	private static final String PATH = "PseudoExportWizardPage.outputPath";//$NON-NLS-1$;

	private final String fileName;

	public final static String PAGE_NAME = "com.gnstudio.apdt.pseudo.ui.exportPage";

	private Button browseButton = null;

	private Text destDirText = null;

	private Button overwriteCheckBox = null;

	public PseudoExportWizardPage(String fileName) {
		super(PAGE_NAME, APDPseudoMessages.PseudoPage_export_header,
				APDTImages.DESC_EXPORT_MODEL_WIZ);
		this.fileName = fileName;
		setPageComplete(false);
	}

	/** Called to indicate that a control's value has changed */
	public void controlChanged() {
		setPageComplete(validate());
	}

	/** Convenience method for creating a new checkbox */
	protected Button createCheckBox(Composite parent, String text) {
		final Button newButton = new Button(parent, SWT.CHECK);
		newButton.setText(text);
		newButton.setSelection(PseudoPlugin.getDefault().getPreferenceStore()
				.getBoolean(OVERWRITE));
		newButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				controlChanged();
				PseudoPlugin.getDefault().getPreferenceStore()
						.setValue(OVERWRITE, newButton.getSelection());
			}
		});

		return newButton;
	}

	/**
	 * Create the widgets on the page
	 */
	public void createControl(Composite parent) {
		try {
			final Composite container = new Composite(parent, SWT.NONE);
			final GridLayout layout = new GridLayout(1, false);
			container.setLayout(layout);

			createExportDirectoryControl(container);

			overwriteCheckBox = createCheckBox(container,
					"Overwrite existing file");

			setControl(container);

			setPageComplete(validate());
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create widgets for specifying the destination directory
	 */
	private void createExportDirectoryControl(Composite parent) {
		final Group destDirGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		destDirGroup.setText("Export destination");
		destDirGroup.setLayout(new GridLayout(3, false));
		destDirGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(destDirGroup, SWT.NONE).setText("File:");
		final Label fileNameLabel = new Label(destDirGroup, SWT.NONE);
		fileNameLabel.setText(fileName);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fileNameLabel.setLayoutData(gd);
		new Label(destDirGroup, SWT.NONE).setText("Folder:");

		destDirText = new Text(destDirGroup, SWT.BORDER);
		destDirText.setEditable(false);
		destDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destDirText.setText(PseudoPlugin.getDefault().getPreferenceStore()
				.getString(PATH));
		destDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();

				PseudoPlugin.getDefault().getPreferenceStore()
						.putValue(PATH, destDirText.getText());
			}
		});

		browseButton = new Button(destDirGroup, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Folder Selection");
				dialog.setMessage("Specify the destination folder for pesudocode file");
				String dir = destDirText.getText();
				dialog.setFilterPath(dir);
				dir = dialog.open();
				controlChanged();
				if (dir == null || dir.equals("")) {
					return;
				}
				destDirText.setText(dir);
			}
		});
	}

	/** Returns the directory where data files are to be saved */
	public String getDestinationDirectory() {
		return destDirText.getText();
	}

	@Override
	public String getName() {
		return PAGE_NAME;
	}

	/** True if the user wants to overwrite files by default */
	public boolean overwrite() {
		return overwriteCheckBox.getSelection();
	}

	/** Returns true if the information entered by the user is valid */
	protected boolean validate() {
		setMessage("export pseudocode file into an external folder");
		setErrorMessage(null);
		// Check that a destination dir has been specified
		if (destDirText.getText().equals("")) {
			setMessage("Please choose an export destination", IStatus.INFO);
			return false;
		}
		final File destDirFile = new File(getDestinationDirectory());
		if (!destDirFile.exists() || !destDirFile.isDirectory()) {
			setErrorMessage("Output Directory dose not exists");
			return false;
		}
		final File desFile = new File(destDirFile, fileName);
		if (desFile.exists()) {
			if (overwrite()) {
				setMessage("output file already exists, but will be overwrite",
						IStatus.WARNING);
				return true;
			} else {
				setErrorMessage("output file already exists");
				return false;
			}
		}
		return true;
	}

}

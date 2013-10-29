/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.snippet.export;

import java.util.Date;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
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
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider;
import org.gnstudio.apdt.model.editor.snippets.SnippetUtil;

import com.gnstudio.apdt.APDTImages;

public class ExportWizardPage extends WizardPage {

	protected final static String PAGE_TITLE = "Export Snippets";

	public final static String PAGE_NAME = PAGE_TITLE;

	private Button browseButton = null;

	private Text destDirText = null;

	// Key values for the dialog settings object
	private final static String SETTINGS_SAVED = "settings.saved";

	private final static String DEST_DIR_SETTING = "destination.directory.setting";

	private SnippetSelectionItem[] items;
	private SnippetProvider provider;

	public ExportWizardPage() {
		super("com.gnstudio.apdt.snippet.exportWizard", PAGE_TITLE,
				APDTImages.DESC_SNIPPET_EXPORT);
		setPageComplete(false);
		provider = SnippetUtil.getSnippetProvider();
		items = SnippetSelectionItem.getSelectionItems(provider);
	}

	public SnippetProvider getSnippetProvider() {
		return provider;
	}

	/** Called to indicate that a control's value has changed */
	public void controlChanged() {
		setPageComplete(validate());
	}

	protected void createSnippetSelectionTable(Composite parent) {

		CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(parent,
				SWT.CHECK | SWT.BORDER);

		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {

				return items;
			}
		});
		viewer.setCheckStateProvider(new ICheckStateProvider() {

			public boolean isGrayed(Object element) {
				return false;
			}

			public boolean isChecked(Object element) {
				if (element instanceof SnippetSelectionItem) {
					return ((SnippetSelectionItem) element).isSelected();
				}
				return false;
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				Object element = event.getElement();
				if (element instanceof SnippetSelectionItem) {
					((SnippetSelectionItem) element).setSelected(event
							.getChecked());
					controlChanged();
				}

			}
		});
		viewer.setInput(new Object());
	}

	/**
	 * Create the widgets on the page
	 */
	public void createControl(Composite parent) {
		try {
			final Composite container = new Composite(parent, SWT.NONE);
			final GridLayout layout = new GridLayout(1, false);
			container.setLayout(layout);

			createSnippetSelectionTable(container);
			createExportDirectoryControl(container);

			initSettings();

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

		new Label(destDirGroup, SWT.NONE).setText("Folder:");

		destDirText = new Text(destDirGroup, SWT.BORDER);
		destDirText.setEditable(false);
		destDirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();
			}
		});

		browseButton = new Button(destDirGroup, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Folder Selection");
				dialog.setMessage("Specify the destination folder for task data");
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

		new Label(destDirGroup, SWT.NONE).setText("File:");
		final Label l = new Label(destDirGroup, SWT.NONE);
		l.setText(getOutputFile());
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);
	}

	/** Returns the directory where data files are to be saved */
	public String getDestinationDirectory() {
		return destDirText.getText();
	}

	@Override
	public String getName() {
		return PAGE_NAME;
	}

	/**
	 * Initializes controls with values from the Dialog Settings object
	 */
	protected void initSettings() {
		final IDialogSettings settings = getDialogSettings();

		if (settings.get(SETTINGS_SAVED) == null) {
			// Set default values
			destDirText.setText("");

		} else {
			// Retrieve previous values from the dialog settings

			final String directory = settings.get(DEST_DIR_SETTING);
			if (directory != null) {
				destDirText.setText(settings.get(DEST_DIR_SETTING));
			}

		}
	}

	
	public void saveSettings() {
		final IDialogSettings settings = getDialogSettings();
		settings.put(DEST_DIR_SETTING, destDirText.getText());

		settings.put(SETTINGS_SAVED, SETTINGS_SAVED);
	}

	/** Returns true if the information entered by the user is valid */
	protected boolean validate() {
		setMessage(null);
		setErrorMessage(null);

		boolean isSelected = false;
		for (SnippetSelectionItem item : items) {
			if (item.isSelected()) {
				isSelected = true;
				break;
			}
		}
		if (!isSelected) {
			setErrorMessage("No snippets selected");
			return false;
		}

		// Check that a destination dir has been specified
		if (destDirText.getText().equals("")) {
			setErrorMessage("Please choose an export destination");
			return false;
		}
		setMessage("Export snippets to the local file system.");
		return true;
	}

	public static String getOutputFile() {
		return String.format("apdt-snippet-export-%1$tY%1$tm%1$te.apdbk",
				new Date());
	}

	public SnippetSelectionItem[] getItems() {
		return items;
	}
}

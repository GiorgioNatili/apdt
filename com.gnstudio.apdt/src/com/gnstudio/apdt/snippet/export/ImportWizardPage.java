/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.snippet.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.gnstudio.apdt.APDTLog;
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider;
import org.gnstudio.apdt.model.editor.snippets.SnippetUtil;

import com.gnstudio.apdt.APDTImages;
import com.gnstudio.apdt.snippet.LocalSnippetProvider;

public class ImportWizardPage extends WizardPage {

	protected final static String PAGE_TITLE = "Import Snippets";
	private static final String LABEL_IMPORT = "From file";
	public final static String PAGE_NAME = PAGE_TITLE;

	private SnippetSelectionItem[] items = new SnippetSelectionItem[0];
	private LocalSnippetProvider provider;
	private Button browseButton;
	private Label importLable;
	private Text sourceZipText;
	private CheckboxTableViewer viewer;
	// Key values for the dialog settings object
	private final static String SETTINGS_SAVED = "settings.saved";

	private final static String IMPORT_DIR_SETTING = "source.directory.setting";

	public ImportWizardPage() {
		super("com.gnstudio.apdt.snippet.importWizard", PAGE_TITLE,
				APDTImages.DESC_SNIPPET_IMPORT);
		setPageComplete(false);
	}

	/** Called to indicate that a control's value has changed */
	public void controlChanged() {
		setPageComplete(validate());
	}

	public LocalSnippetProvider getSnippetProvider() {
		return provider;
	}

	public SnippetSelectionItem[] getItems() {
		return items;
	}

	/**
	 * Create the widgets on the page
	 */
	public void createControl(Composite parent) {
		try {
			final Composite tableComposite = new Composite(parent, SWT.NONE);
			tableComposite.setLayout(new GridLayout(1, false));

			createImportDirectoryControl(tableComposite);

			createSnippetSelectionTable(tableComposite);
			initSettings();
			setControl(tableComposite);
			setPageComplete(validate());
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create widgets for specifying the destination directory
	 */
	private void createImportDirectoryControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 15;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		importLable = new Label(container, SWT.LEFT);
		importLable.setText(LABEL_IMPORT);
		sourceZipText = new Text(container, SWT.BORDER);
		sourceZipText.setEditable(false);
		sourceZipText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sourceZipText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlChanged();
			}
		});

		browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setText("Specify the source .apdbk file for snippet data");
				String dir = sourceZipText.getText();
				// dialog.setFileName(dir);
				dialog.setFilterExtensions(new String[] { "*.apdbk" });
				dir = dialog.open();
				if (dir == null || dir.equals("")) {
					return;
				}
				sourceZipText.setText(dir);
				loadSnippets();
			}
		});
	}

	private void loadSnippets() {
		items = new SnippetSelectionItem[0];
		if (viewer != null) {
			viewer.setInput(new Object());
		}
		controlChanged();

		final File zipFile = new File(getImportFromDirectory());

		if (zipFile.exists()) {

			try {
				items = new SnippetSelectionItem[0];
				File temDir = File.createTempFile("apdt-snippet", "dir");
				temDir.delete();
				temDir.mkdir();
				byte[] buf = new byte[1024];
				ZipInputStream zipinputstream = null;
				ZipEntry zipentry;
				zipinputstream = new ZipInputStream(
						new FileInputStream(zipFile));

				zipentry = zipinputstream.getNextEntry();
				while (zipentry != null) {
					// for each entry to be extracted
					String entryName = zipentry.getName();
					int n;
					FileOutputStream fileoutputstream;
					File newFile = new File(entryName);
					String directory = newFile.getParent();

					if (directory == null) {
						if (newFile.isDirectory())
							break;
					}

					fileoutputstream = new FileOutputStream(new File(temDir,
							entryName));

					while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
						fileoutputstream.write(buf, 0, n);

					fileoutputstream.close();
					zipinputstream.closeEntry();
					zipentry = zipinputstream.getNextEntry();

				}// while

				zipinputstream.close();

				provider = new LocalSnippetProvider(temDir.getAbsolutePath());
				items = SnippetSelectionItem.getSelectionItems(provider);
				SnippetProvider baseProvider = SnippetUtil.getSnippetProvider();
				for (SnippetSelectionItem item : items) {
					String snippet = item.getHandle().getName();
					boolean hasSnippet = baseProvider.hasSnippet(snippet, null);
					if(hasSnippet){
						int i = 1;
						String tempName = snippet;
						while (hasSnippet) {
							tempName = String.format("%s_%s", snippet,String.valueOf(i++));
							hasSnippet = baseProvider.hasSnippet(tempName, null);
						}
						item.getHandle().setName(tempName);
					}
					
				}
			} catch (IOException e) {
				APDTLog.log(e);

			} finally {
				if (viewer != null) {
					viewer.setInput(new Object());
				}
				controlChanged();

			}

		}

	}

	protected void createSnippetSelectionTable(Composite parent) {

		viewer = CheckboxTableViewer.newCheckList(parent, SWT.CHECK
				| SWT.BORDER);

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

	private String getImportFromDirectory() {
		return sourceZipText.getText();
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
			sourceZipText.setText("");

		} else {
			// Retrieve previous values from the dialog settings

			final String directory = settings.get(IMPORT_DIR_SETTING);
			if (directory != null) {
				sourceZipText.setText(settings.get(IMPORT_DIR_SETTING));
				loadSnippets();
			}

		}
	}

	/**
	 * Saves the control values in the dialog settings to be used as defaults
	 * the next time the page is opened
	 */
	public void saveSettings() {
		final IDialogSettings settings = getDialogSettings();
		settings.put(IMPORT_DIR_SETTING, sourceZipText.getText());

		settings.put(SETTINGS_SAVED, SETTINGS_SAVED);
	}

	/** Returns true if the information entered by the user is valid */
	protected boolean validate() {
		setMessage(null);
		setErrorMessage(null);

		// Check that a destination dir has been specified
		if (getImportFromDirectory().equals("")) {
			setErrorMessage("Please choose an Import file");
			return false;
		}
		File zipFile = new File(getImportFromDirectory());

		if (!zipFile.exists()) {
			setErrorMessage(String.format("The File '%s' dose not exist",
					zipFile.getName()));
			return false;

		}

		boolean isSelected = false;
		for (SnippetSelectionItem item : items) {
			if (item.isSelected()) {
				isSelected = true;
				break;
			}
		}
		if (!isSelected || provider == null) {
			setErrorMessage("No snippets selected");
			return false;
		}
		setMessage("Import selected snippets to the workspace.");
		return true;
	}

}

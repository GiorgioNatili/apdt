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
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.gnstudio.apdt.APDTLog;
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider;

import com.gnstudio.apdt.APDTPlugin;
import com.gnstudio.apdt.snippet.LocalSnippetProvider;

public class ExportWizard extends Wizard implements IExportWizard {

	private final static String SETTINGS_SECTION = "com.gnstudio.apdt.snippet.exportWizard";

	private final static String WINDOW_TITLE = "Export";

	private ExportWizardPage exportPage = null;

	public ExportWizard() {
		final IDialogSettings masterSettings = APDTPlugin.getDefault()
				.getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
		setWindowTitle(WINDOW_TITLE);
	}

	@Override
	public void addPages() {
		exportPage = new ExportWizardPage();
		exportPage.setWizard(this);
		addPage(exportPage);
	}

	@Override
	public boolean canFinish() {
		return exportPage.isPageComplete();
	}

	/**
	 * Finds or creates a dialog settings section that is used to make the
	 * dialog control settings persistent
	 */
	public IDialogSettings getSettingsSection(IDialogSettings master) {
		IDialogSettings settings = master.getSection(SETTINGS_SECTION);
		if (settings == null) {
			settings = master.addNewSection(SETTINGS_SECTION);
		}
		return settings;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	/**
	 * Called when the user clicks finish. Saves the connection data. Waits
	 * until all overwrite decisions have been made before starting to save
	 * files. If any overwrite is canceled, no files are saved and the user must
	 * adjust the dialog.
	 */
	@Override
	public boolean performFinish() {

		// Get file paths to check for existence
		final String destDir = exportPage.getDestinationDirectory();
		final File destDirFile = new File(destDir);
		assert !destDirFile.exists() || !destDirFile.isDirectory() : "Output Detractory should exists";
		String outputFile = ExportWizardPage.getOutputFile();
		final File destZipFile = new File(destDir, outputFile);

		if (destZipFile.exists()) {

			if (!MessageDialog
					.open(MessageDialog.CONFIRM,
							getShell(),
							"Override",
							String.format(
									"The file '%s' already exist. Do you want to override it?",
									outputFile), SWT.SHEET)) {
				return false;
			}

		}

		try {
			final IRunnableWithProgress job = new IRunnableWithProgress() {

				SnippetSelectionItem[] items = exportPage.getItems();

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						File temDir = File
								.createTempFile("apdt-snippet", "dir");
						temDir.delete();
						temDir.mkdir();

						LocalSnippetProvider exportProvider = new LocalSnippetProvider(
								temDir.getAbsolutePath());

						SnippetProvider snippetProvider = exportPage
								.getSnippetProvider();

						for (SnippetSelectionItem item : items) {
							if (!item.isSelected()) {
								continue;
							}
							EObject snippetData = snippetProvider
									.getSnippetData(item.getHandle());
							if (snippetData != null) {

								exportProvider.addSnippet(item.getHandle()
										.getName(), snippetData);
							}
						}

						ZipOutputStream out = new ZipOutputStream(
								new FileOutputStream(destZipFile));

						File[] list = temDir.listFiles();
						// Compress the files
						byte[] buf = new byte[1024];
						for (int i = 0; i < list.length; i++) {
							FileInputStream in = new FileInputStream(list[i]);

							// Add ZIP entry to output stream.
							out.putNextEntry(new ZipEntry(list[i].getName()));

							// Transfer bytes from the file to the ZIP file
							int len;
							while ((len = in.read(buf)) > 0) {
								out.write(buf, 0, len);
							}

							// Complete the entry
							out.closeEntry();
							in.close();
						}
						out.close();
					} catch (IOException e) {
						APDTLog.log(e);
					}

				}
			};
			getContainer().run(true, true, job);
		} catch (final InvocationTargetException e) {
			APDTLog.log(e);
		} catch (final InterruptedException e) {
			APDTLog.log(e);
		}

		exportPage.saveSettings();
		return true;
	}
}

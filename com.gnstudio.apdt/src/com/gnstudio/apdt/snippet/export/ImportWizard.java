/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.snippet.export;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider;
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider.ViewerHandle;
import org.gnstudio.apdt.model.editor.snippets.SnippetUtil;

import com.gnstudio.apdt.APDTPlugin;
import com.gnstudio.apdt.snippet.LocalSnippetProvider;

public class ImportWizard extends Wizard implements IImportWizard {

	private final static String SETTINGS_SECTION = "com.gnstudio.apdt.snippet.importWizard";

	private final static String WINDOW_TITLE = "Import";

	private ImportWizardPage importPage = null;

	public ImportWizard() {
		final IDialogSettings masterSettings = APDTPlugin.getDefault()
				.getDialogSettings();
		setDialogSettings(getSettingsSection(masterSettings));
		setNeedsProgressMonitor(true);
		setWindowTitle(WINDOW_TITLE);
	}

	@Override
	public void addPages() {
		importPage = new ImportWizardPage();
		importPage.setWizard(this);
		addPage(importPage);
	}

	@Override
	public boolean canFinish() {
		return importPage.isPageComplete();
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

	@Override
	public boolean performFinish() {

		final SnippetSelectionItem[] items = importPage.getItems();
		final LocalSnippetProvider importProvider = importPage
				.getSnippetProvider();
		final SnippetProvider snippetProvider = SnippetUtil
				.getSnippetProvider();
		try {
			final IRunnableWithProgress job = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor) {
					for (SnippetSelectionItem item : items) {
						if (!item.isSelected()) {
							continue;
						}
						EObject data = importProvider.getSnippetData(item
								.getHandle());
						if (data != null) {
							snippetProvider.addSnippet(item.getHandle()
									.getName(), data);
						}
					}
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							ViewerHandle viewerHandle = snippetProvider
									.getViewerHandle();
							if (viewerHandle != null) {
								viewerHandle.refresh();
							}
						}
					});
				}
			};
			getContainer().run(true, true, job);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		importPage.saveSettings();
		return true;
	}
}

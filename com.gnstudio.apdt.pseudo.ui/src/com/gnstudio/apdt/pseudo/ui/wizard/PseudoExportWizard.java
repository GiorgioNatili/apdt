/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.pseudo.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.gnstudio.apdt.APDTLog;
import org.gnstudio.apdt.model.Model;
import org.gnstudio.apdt.model.editor.APDModelEditor;

import com.gnstudio.apdt.pseudo.core.PseudoGenerator;
import com.gnstudio.apdt.pseudo.ui.APDPseudoMessages;

public class PseudoExportWizard extends Wizard implements IExportWizard {

	private String fileName;
	private Model model;
	private PseudoExportWizardPage exportPage = null;

	public PseudoExportWizard() {
		setNeedsProgressMonitor(true);
		setWindowTitle(APDPseudoMessages.PseudoPage_export);
	}

	public void init(APDModelEditor editor) {

		this.model = editor.getModel();
		IFile modelFile = editor.getModelFile();
		if (modelFile != null) {
			fileName = modelFile.getName();

		} else {
			File externalModelFile = editor.getExternalModelFile();
			if (externalModelFile != null) {
				fileName = externalModelFile.getName();
			}
		}
		if (fileName != null && fileName.endsWith("apd")) {//$NON-NLS-1$
			fileName = fileName.substring(0, fileName.length() - 3) + "txt";//$NON-NLS-1$ 
		}
	}

	@Override
	public void addPages() {
		exportPage = new PseudoExportWizardPage(fileName);
		exportPage.setWizard(this);
		addPage(exportPage);
	}

	@Override
	public boolean canFinish() {
		return exportPage.isPageComplete();
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

		final File desFile = new File(destDir, fileName);

		try {
			final IRunnableWithProgress job = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(
								APDPseudoMessages.PseudoPage_info_pseudo_gen, 2);
						PseudoGenerator pseudoGenerator = new PseudoGenerator(
								model, desFile, true);
						monitor.worked(1);
						pseudoGenerator.doGenerate(BasicMonitor
								.toMonitor(monitor));
						monitor.worked(2);
						monitor.done();
					} catch (IOException e) {
						APDTLog.logException(e);
					}

				}

			};
			getContainer().run(true, true, job);
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
}

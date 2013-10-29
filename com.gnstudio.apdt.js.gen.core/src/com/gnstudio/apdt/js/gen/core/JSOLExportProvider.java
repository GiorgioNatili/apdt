package com.gnstudio.apdt.js.gen.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicMonitor;
import org.gnstudio.apdt.APDTLog;
import org.gnstudio.apdt.model.Model;
import org.gnstudio.apdt.model.editor.export.ModelExportProvider;

import com.gnstudio.apdt.APDTPlugin;

public class JSOLExportProvider implements ModelExportProvider {

	public void export(IProgressMonitor monitor,String name, Model model, File target) {
		if (isSupported(model))
			try {
				monitor.beginTask("JavaScript Code Generating", 2);
				JSGenerator pseudoGenerator = new JSGenerator(model, target,name,"OL");
				monitor.worked(1);
				pseudoGenerator.doGenerate(BasicMonitor.toMonitor(monitor));
				monitor.worked(2);
				monitor.done();
			} catch (IOException e) {
				APDTLog.logException(e);
			}

	}

	public String getProviderName() {
		return "APD Model to JavaScript Code - OL (Object Literals)";
	}

	public String getProviderId() {
		return "gnstudio.apdt.js.gen.core.ol";
	}

	public boolean isSupported(Model model) {

		return APDTPlugin.getDefault().getApdtContext().validateActivation();
	}
}

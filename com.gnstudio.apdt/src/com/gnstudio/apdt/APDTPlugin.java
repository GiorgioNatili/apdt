/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class APDTPlugin extends AbstractUIPlugin implements IStartup {
	private BundleContext bundleContext;
	private APDTContext apdtContext;

	private static APDTPlugin plugin;

	public static final String VERSION = "1.1.0";

	public APDTPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.bundleContext = context;
		this.apdtContext = new APDTContext();
		this.apdtContext.bootup();
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public APDTContext getApdtContext() {
		return apdtContext;
	}

	/**
	 * Returns the shared instance.
	 */
	public static APDTPlugin getDefault() {
		return plugin;
	}

	public void earlyStartup() {
		// ignore
	}

	public static String getID() {
		return getDefault().getBundle().getSymbolicName();
	}

	public static Display getStandardDisplay() {
		Display display;
		display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	
}

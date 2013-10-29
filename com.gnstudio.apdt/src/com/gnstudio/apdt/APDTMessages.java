/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt;

import org.eclipse.osgi.util.NLS;

public class APDTMessages extends NLS {
	private static final String BUNDLE_NAME = "com.gnstudio.apdt.resources"; //$NON-NLS-1$

	// public static String PreferencesPage_summary;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, APDTMessages.class);
	}

	private APDTMessages() {
	}

	public static String ActivationPage_summary;
}

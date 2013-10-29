/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.pseudo.ui;

import org.eclipse.osgi.util.NLS;

public class APDPseudoMessages extends NLS {
	private static final String BUNDLE_NAME = "com.gnstudio.apdt.pseudo.ui.resources"; //$NON-NLS-1$

	
	public static String PseudoPage_header;
	public static String PseudoPage_title;
	public static String PseudoPage_job_pseudo_gen;
	public static String PseudoPage_info_pseudo_gen;
	public static String PseudoPage_refresh;
	public static String PseudoPage_export;
	public static String PseudoPage_export_header;
	public static String PseudoPage_copy_selected;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, APDPseudoMessages.class);
	}

	private APDPseudoMessages() {
	}
}

/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt;

import org.eclipse.ui.IStartup;

public class APDTBoot implements IStartup {

	public void earlyStartup() {
		//just activate APDTPlugin
		APDTPlugin.getDefault();
		//System.out.println("BOOT_APDT");
	}

}

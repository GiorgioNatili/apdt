/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.snippet;

import java.io.File;

public class SnippetContextStore {
     private final File contextDirectory;
     
     public SnippetContextStore(File contextDirectory) {
		this.contextDirectory = contextDirectory;
	}

 	public File getContextDirectory() {
 		return contextDirectory;
 	}
}

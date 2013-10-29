/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.pseudo.ui;

import org.gnstudio.apdt.model.editor.APDModelEditor;
import org.gnstudio.apdt.model.editor.AbstractEditorPage;
import org.gnstudio.apdt.model.editor.EditorPageProvider;

public class PseudoPageProviderImpl implements EditorPageProvider {

	public AbstractEditorPage[] createPages(APDModelEditor editor) {
		return new AbstractEditorPage[] { new PseudoPage(editor) };
	}

}

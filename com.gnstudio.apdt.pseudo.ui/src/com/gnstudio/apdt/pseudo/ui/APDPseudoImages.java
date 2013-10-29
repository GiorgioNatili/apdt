/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/

package com.gnstudio.apdt.pseudo.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.graphics.Image;

public class APDPseudoImages {
	private APDPseudoImages() {
	}

	private final static ImageRegistry PLUGIN_REGISTRY = new ImageRegistry();
	public final static String ICONS_PATH = "icons/"; //$NON-NLS-1$

	private static final String PATH_LCL = ICONS_PATH + "elcl16/"; //$NON-NLS-1$
	

	public static final ImageDescriptor DESC_EXPORT_PSEUDO = create(PATH_LCL,
			"export_pseudocode.gif"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name) {
		return ImageDescriptor.createFromURL(makeImageURL(prefix, name));
	}

	public static ImageDescriptor createOverlay(ImageDescriptor base,
			ImageDescriptor overlay, int quadrant) {
		return new DecorationOverlayIcon(getImage(base), overlay, quadrant);
	}

	private static URL makeImageURL(String prefix, String name) {
		String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		return FileLocator.find(PseudoPlugin.getDefault().getBundle(),
				new Path(path), null);
	}

	public static Image getImage(ImageDescriptor desc) {
		String key = String.valueOf(desc.hashCode());
		Image image = PLUGIN_REGISTRY.get(key);
		if (image == null) {
			image = desc.createImage();
			PLUGIN_REGISTRY.put(key, image);
		}
		return image;
	}
}

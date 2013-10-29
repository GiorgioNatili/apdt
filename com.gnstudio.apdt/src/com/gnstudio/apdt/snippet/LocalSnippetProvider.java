/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.snippet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.gnstudio.apdt.APDTLog;
import org.gnstudio.apdt.model.editor.snippets.SnippetHandle;
import org.gnstudio.apdt.model.editor.snippets.SnippetProvider;
import org.gnstudio.apdt.model.util.ModelOperation;
import org.gnstudio.apdt.model.util.ModelOperationException;

public class LocalSnippetProvider implements SnippetProvider {

	private ViewerHandle viewerHandle;
	private List<SnippetHandle> handlers = new ArrayList<SnippetHandle>();
	private final String dir;

	public LocalSnippetProvider() {
		this(getDefaultSnippetDataDirectory());
	}

	public LocalSnippetProvider(String dir) {
		this.dir = dir;
		loadSnippetHandles();
	}

	private static final String DIRECTORY_METADATA = ".metadata"; //$NON-NLS-1$
	private static final String NAME_DATA_DIR = ".apdt/.snippet";

	public static String getDefaultSnippetDataDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString()
				+ '/' + DIRECTORY_METADATA + '/' + NAME_DATA_DIR;
	}

	public EObject getSnippetData(SnippetHandle handle) {
		File dataFile = new File(dir, handle.getKey());
		if (dataFile.exists()) {
			try {
				return ModelOperation.read(dataFile);
			} catch (ModelOperationException e) {
				APDTLog.log(e);
			}
		}
		return null;
	}

	public SnippetHandle[] getSnippets() {
		return handlers.toArray(new SnippetHandle[handlers.size()]);
	}

	public void setViewerHandle(ViewerHandle handle) {
		viewerHandle = handle;
	}

	public void addSnippet(String name, EObject data) {
		try {
			File dataFile = File.createTempFile("snp-", ".data",
					getSnippetDir());
			ModelOperation.write(ModelOperation.copyElement(data), dataFile);

			SnippetHandle handle = new SnippetHandle(dataFile.getName(), name);
			handlers.add(handle);
			saveSnippetHandles();

			
		} catch (IOException e) {
			APDTLog.log(e);

		} catch (ModelOperationException e) {
			APDTLog.log(e);
		}

	}

	public boolean hasSnippet(String name, SnippetHandle current) {
		if (name != null) {
			for (SnippetHandle handle : handlers) {
				if (!handle.equals(current) && handle.getName().equals(name))
					return true;
			}
		}
		return false;
	}

	private File getSnippetDir() {
		File file = new File(dir);
		file.mkdirs();
		return file;
	}

	private void saveSnippetHandles() {
		Properties snippetHandlers = new Properties();
		for (SnippetHandle handle : handlers) {
			snippetHandlers.put(handle.getKey(), handle.getName());
		}
		try {
			snippetHandlers.store(new FileOutputStream(new File(
					getSnippetDir(), ".handlers")), null);
		} catch (IOException e) {
			APDTLog.log(e);
		}
	}

	private void loadSnippetHandles() {
		File handlersFile = new File(getSnippetDir(), ".handlers");
		if (handlersFile.exists()) {
			Properties snippetHandlers = new Properties();

			try {
				snippetHandlers.load(new FileInputStream(handlersFile));
			} catch (IOException e) {
				APDTLog.log(e);
			}

			for (Entry<Object, Object> handle : snippetHandlers.entrySet()) {

				handlers.add(new SnippetHandle((String) handle.getKey(),
						(String) handle.getValue()));
			}
		}

	}

	public ViewerHandle getViewerHandle() {
		return viewerHandle;
	}

	public boolean deleteSnippet(SnippetHandle handle) {

		File dataFile = new File(dir, handle.getKey());
		if (dataFile.exists()) {
			boolean delete = dataFile.delete();
			if (!delete) {
				dataFile.deleteOnExit();
			}
			handlers.remove(handle);
			saveSnippetHandles();
			return true;
		}
		return false;
	}

	public void renameSnippet(SnippetHandle current, String name) {
		if (name != null) {
			for (SnippetHandle handle : handlers) {
				if (handle.equals(current) && !handle.getName().equals(name)) {
					handle.setName(name);
					saveSnippetHandles();
				}
			}
		}
	}
}

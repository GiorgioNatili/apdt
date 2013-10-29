/*******************************************************************************
 * Copyright (c)  2010, 2012 GNstudio s.r.l.
 * GNStudio PROPRIETARY/CONFIDENTIAL PROPERTIES. Use is subject to license terms.
 * You CANNOT use this software unless you receive a written permission from GNStudio
 *******************************************************************************/
package com.gnstudio.apdt.pseudo.core;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.acceleo.engine.service.AbstractAcceleoGenerator;
import org.eclipse.acceleo.engine.service.AcceleoService;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.gnstudio.apdt.model.ModelPackage;

/**
 * Entry point of the 'Generate' generation module.
 * 
 */
public class PseudoGenerator extends AbstractAcceleoGenerator {
	/**
	 * The name of the module.
	 * 
	 */
	public static final String MODULE_FILE_NAME = "PseudoGenerator";

	/**
	 * The name of the templates that are to be generated.
	 * 
	 */
	public static final String[] TEMPLATE_NAMES = { "genModel", };

	private final File pesudoFile;
	private final boolean keywordUppercase;

	public PseudoGenerator(EObject model, File pesudoFile) throws IOException {
		this.pesudoFile = pesudoFile;
		this.keywordUppercase = false;
		initialize(model, pesudoFile.getParentFile(), Collections.emptyList());

	}

	public PseudoGenerator(EObject model, File pesudoFile,
			boolean keywordUppercase) throws IOException {
		this.pesudoFile = pesudoFile;
		this.keywordUppercase = keywordUppercase;
		initialize(model, pesudoFile.getParentFile(), Collections.emptyList());

	}

	@Override
	protected AcceleoService createAcceleoService() {

		AcceleoService service = super.createAcceleoService();
		Map<String, String> props = new HashMap<String, String>();
		props.put("file.name", pesudoFile.getName());
		if (keywordUppercase)
			props.put("keywords.case", "uppercase");
		service.addProperties(props);
		return service;
	}

	/**
	 * This will be called in order to find and load the module that will be
	 * launched through this launcher. We expect this name not to contain file
	 * extension, and the module to be located beside the launcher.
	 * 
	 * @return The name of the module that is to be launched.
	 */
	@Override
	public String getModuleName() {
		return MODULE_FILE_NAME;
	}

	/**
	 * This will be used to get the list of templates that are to be launched by
	 * this launcher.
	 * 
	 * @return The list of templates to call on the module
	 *         {@link #getModuleName()}.
	 */
	@Override
	public String[] getTemplateNames() {
		return TEMPLATE_NAMES;
	}

	/**
	 * This can be used to update the resource set's package registry with all
	 * needed EPackages.
	 * 
	 * @param resourceSet
	 *            The resource set which registry has to be updated.
	 */
	@Override
	public void registerPackages(ResourceSet resourceSet) {
		super.registerPackages(resourceSet);

		resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI,
				ModelPackage.eINSTANCE);
	}

	/**
	 * This can be used to update the resource set's resource factory registry
	 * with all needed factories.
	 * 
	 * @param resourceSet
	 *            The resource set which registry has to be updated.
	 * 
	 */
	@Override
	public void registerResourceFactories(ResourceSet resourceSet) {
		super.registerResourceFactories(resourceSet);

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("apd", new EcoreResourceFactoryImpl());
	}

	@Override
	public void doGenerate(Monitor monitor) throws IOException {
		super.doGenerate(monitor);
	}
}

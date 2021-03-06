/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Stefan Mijatov
 */
package org.modelexecution.fumltesting.uml.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class UmlTestLangUIPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "org.modelexecution.fumltesting.uml.ui"; //$NON-NLS-1$
	private final static String ICONS_PATH = "icons/"; //$NON-NLS-1$

	// The shared instance
	private static UmlTestLangUIPlugin plugin;

	// Images
	public final static String IMG_TESTSUITE_LAUNCH = "IMG_TESTSUITE_LAUNCH"; //$NON-NLS-1$

	public UmlTestLangUIPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static UmlTestLangUIPlugin getDefault() {
		return plugin;
	}

	protected void initializeImageRegistry(ImageRegistry reg) {
		declareImage(IMG_TESTSUITE_LAUNCH, ICONS_PATH + "test.png"); //$NON-NLS-1$
	}

	private void declareImage(String key, String path) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (path != null) {
			URL iconURL = FileLocator.find(bundle, new Path(path), null);
			if (iconURL != null) {
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconURL);
				super.getImageRegistry().put(key, descriptor);
			}
		}
	}
}
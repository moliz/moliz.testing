/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.eclipse.xtext.uml;

import org.eclipse.xtext.resource.generic.AbstractGenericResourceSupport;
import com.google.inject.Module;

/**
 * 
 * @author Stefan Mijatov
 * 
 */
public class UmlSupport extends AbstractGenericResourceSupport {
	@Override
	protected Module createGuiceModule() {
		return new UmlRuntimeModule();
	}
}
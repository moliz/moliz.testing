/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.core.testlang;

import fUML.Syntax.Actions.BasicActions.Action;

/**
 * @author Stefan Mijatov
 * 
 */
public class ActionReferencePoint implements ReferencePoint {
	private Action action;

	public Action getAction() {
		return this.action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}

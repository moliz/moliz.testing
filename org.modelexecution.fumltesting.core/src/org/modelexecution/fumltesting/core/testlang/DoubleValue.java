/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.core.testlang;

/**
 * @author Stefan Mijatov
 * 
 */
public class DoubleValue implements Value {
	private double value;

	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public boolean isNegative() {
		if (value >= 0)
			return false;
		else
			return true;
	}
}
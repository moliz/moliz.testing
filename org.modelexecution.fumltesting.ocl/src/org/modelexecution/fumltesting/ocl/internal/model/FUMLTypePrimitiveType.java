/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.ocl.internal.model;

import java.util.ArrayList;
import java.util.List;

import org.dresdenocl.pivotmodel.Namespace;
import org.dresdenocl.pivotmodel.Operation;
import org.dresdenocl.pivotmodel.Property;
import org.dresdenocl.pivotmodel.Type;
import org.dresdenocl.pivotmodel.base.AbstractType;

import fUML.Syntax.Classes.Kernel.NamedElement;
import fUML.Syntax.Classes.Kernel.PrimitiveType;

/**
 * 
 * @author Stefan Mijatov
 * 
 */
public class FUMLTypePrimitiveType extends AbstractType implements Type {
	private PrimitiveType dslPrimitiveType;
	private FUMLAdapterFactory factory;

	public FUMLTypePrimitiveType(PrimitiveType dslPrimitiveType, FUMLAdapterFactory factory) {
		this.dslPrimitiveType = dslPrimitiveType;
		this.factory = factory;
	}

	@Override
	public String getName() {
		return dslPrimitiveType.name;
	}

	@Override
	public Namespace getNamespace() {
		return factory.createNamespace(dslPrimitiveType.package_);
	}

	@Override
	protected List<Property> getOwnedPropertyImpl() {
		List<Property> result = new ArrayList<Property>();
		for (fUML.Syntax.Classes.Kernel.Property property : dslPrimitiveType.ownedAttribute) {
			result.add(factory.createProperty(property));
		}
		return result;
	}

	@Override
	protected List<Operation> getOwnedOperationImpl() {
		List<Operation> result = new ArrayList<Operation>();
		for (NamedElement operation : dslPrimitiveType.ownedMember) {
			if (operation instanceof fUML.Syntax.Classes.Kernel.Operation) {
				result.add(factory.createOperation((fUML.Syntax.Classes.Kernel.Operation) operation));
			}
		}
		return result;
	}

	@Override
	protected List<Type> getSuperTypeImpl() {
		return new ArrayList<Type>();
	}
}
/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.ocl.internal.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.dresdenocl.pivotmodel.Namespace;
import org.dresdenocl.pivotmodel.PrimitiveType;
import org.dresdenocl.pivotmodel.PrimitiveTypeKind;
import org.dresdenocl.pivotmodel.base.AbstractPrimitiveType;

import fUML.Syntax.Classes.Kernel.Type;

/**
 * 
 * @author Stefan Mijatov
 * 
 */
public class FUMLPrimitiveType extends AbstractPrimitiveType implements PrimitiveType {
	private static String booleanKindNames[] = new String[] { "Boolean", boolean.class.getCanonicalName(), Boolean.class.getCanonicalName(), "EBoolean",
			"EBooleanObject", "OclBoolean" };
	private static String integerKindNames[] = new String[] { "Integer", "UnlimitedNatural", byte.class.getCanonicalName(), Byte.class.getCanonicalName(),
			short.class.getCanonicalName(), Short.class.getCanonicalName(), int.class.getCanonicalName(), Integer.class.getCanonicalName(),
			long.class.getCanonicalName(), Long.class.getCanonicalName(), BigInteger.class.getCanonicalName(), BigDecimal.class.getCanonicalName(), "EByte",
			"EByteObject", "EShort", "EShortObject", "EInt", "EIntegerObject", "ELong", "ELongObject", "EBigInteger", "EBigDecimal" };
	private static String realKindNames[] = new String[] { float.class.getCanonicalName(), Float.class.getCanonicalName(), double.class.getCanonicalName(),
			Double.class.getCanonicalName(), "EFloat", "EFloatObject", "EDouble", "EDoubleObject" };
	private static String stringKindNames[] = new String[] { "String", char.class.getCanonicalName(), Character.class.getCanonicalName(),
			String.class.getCanonicalName(), "EChar", "ECharacter", "EString" };

	private fUML.Syntax.Classes.Kernel.PrimitiveType dslPrimitiveType;
	private FUMLAdapterFactory factory;

	public FUMLPrimitiveType(fUML.Syntax.Classes.Kernel.PrimitiveType dslPrimitiveType, FUMLAdapterFactory factory) {
		this.dslPrimitiveType = dslPrimitiveType;
		this.factory = factory;
	}

	public static PrimitiveTypeKind getKind(Type type) {
		for (String name : booleanKindNames) {
			if (name.equals(type.name)) {
				return PrimitiveTypeKind.BOOLEAN;
			}
		}
		for (String name : integerKindNames) {
			if (name.equals(type.name)) {
				return PrimitiveTypeKind.INTEGER;
			}
		}
		for (String name : realKindNames) {
			if (name.equals(type.name)) {
				return PrimitiveTypeKind.REAL;
			}
		}
		for (String name : stringKindNames) {
			if (name.equals(type.name)) {
				return PrimitiveTypeKind.STRING;
			}
		}
		return PrimitiveTypeKind.UNKNOWN;
	}

	@Override
	public PrimitiveTypeKind getKind() {
		return getKind(dslPrimitiveType);
	}

	@Override
	public String getName() {
		String result = dslPrimitiveType.name;
		if (result == null) {
			result = getKind().getName();
		}
		return result;
	}

	@Override
	public Namespace getNamespace() {
		return factory.createNamespace(dslPrimitiveType.package_);
	}
}
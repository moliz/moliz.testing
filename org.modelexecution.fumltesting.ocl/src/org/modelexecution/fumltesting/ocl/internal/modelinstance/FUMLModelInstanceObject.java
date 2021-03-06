/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.ocl.internal.modelinstance;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dresdenocl.essentialocl.standardlibrary.OclBoolean;
import org.dresdenocl.essentialocl.standardlibrary.OclInteger;
import org.dresdenocl.essentialocl.standardlibrary.OclString;
import org.dresdenocl.modelinstance.base.AbstractModelInstance;
import org.dresdenocl.modelinstancetype.exception.AsTypeCastException;
import org.dresdenocl.modelinstancetype.exception.CopyForAtPreException;
import org.dresdenocl.modelinstancetype.exception.OperationAccessException;
import org.dresdenocl.modelinstancetype.exception.OperationNotFoundException;
import org.dresdenocl.modelinstancetype.exception.PropertyAccessException;
import org.dresdenocl.modelinstancetype.exception.PropertyNotFoundException;
import org.dresdenocl.modelinstancetype.exception.TypeNotFoundInModelException;
import org.dresdenocl.modelinstancetype.types.IModelInstanceBoolean;
import org.dresdenocl.modelinstancetype.types.IModelInstanceCollection;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceEnumerationLiteral;
import org.dresdenocl.modelinstancetype.types.IModelInstanceInteger;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.modelinstancetype.types.IModelInstancePrimitiveType;
import org.dresdenocl.modelinstancetype.types.IModelInstanceReal;
import org.dresdenocl.modelinstancetype.types.IModelInstanceString;
import org.dresdenocl.modelinstancetype.types.base.AbstractModelInstanceObject;
import org.dresdenocl.pivotmodel.AssociationProperty;
import org.dresdenocl.pivotmodel.Operation;
import org.dresdenocl.pivotmodel.Parameter;
import org.dresdenocl.pivotmodel.Property;
import org.dresdenocl.pivotmodel.Type;
import org.dresdenocl.standardlibrary.java.JavaStandardlibraryPlugin;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.osgi.util.NLS;
import org.modelexecution.fumltesting.ocl.internal.model.FUMLAssociationProperty;
import org.modelexecution.fumltesting.ocl.internal.util.FUMLModelInstanceTypeUtil;

import fUML.Semantics.Classes.Kernel.BooleanValue;
import fUML.Semantics.Classes.Kernel.FeatureValue;
import fUML.Semantics.Classes.Kernel.IntegerValue;
import fUML.Semantics.Classes.Kernel.Object_;
import fUML.Semantics.Classes.Kernel.StringValue;

/**
 * 
 * @author Stefan Mijatov
 * 
 */
public class FUMLModelInstanceObject extends AbstractModelInstanceObject implements IModelInstanceObject {
	private Object dslObject;
	private ArrayList<FeatureValue> associationProperties;
	private Class<?> myAdaptedClass;
	private FUMLModelInstanceFactory myFactory;

	protected FUMLModelInstanceObject(Object object, Type type, Type originalType, FUMLModelInstanceFactory factory) {
		super(type, originalType);
		dslObject = object;
		myFactory = factory;
		associationProperties = new ArrayList<FeatureValue>();
		if (object != null)
			adaptEmptyProperties(object, type);
	}

	protected FUMLModelInstanceObject(Object object, Class<?> typeClass, Type type, Type originalType, FUMLModelInstanceFactory factory) {
		super(type, originalType);
		dslObject = object;
		myAdaptedClass = typeClass;
		myType = type;
		myFactory = factory;
		associationProperties = new ArrayList<FeatureValue>();
	}

	public void addAssociationProperty(FeatureValue property) {
		associationProperties.add(property);
	}

	public List<FeatureValue> getAssociationProperties() {
		return Collections.unmodifiableList(associationProperties);
	}

	public IModelInstanceElement asType(Type type) throws AsTypeCastException {
		if (type == null) {
			throw new IllegalArgumentException("Parameter 'type' must not be null!");
		}
		IModelInstanceElement result = null;
		if (getOriginalType().conformsTo(type)) {
			if (dslObject == null) {
				result = new FUMLModelInstanceObject(null, type, getOriginalType(), myFactory);
			} else {
				Class<?> typeClass = FUMLModelInstanceTypeUtil.findClassOfType(dslObject.getClass(), type);
				if (typeClass == null) {
					throw new AsTypeCastException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotCastTypeClassNotFound, type));
				}
				result = new FUMLModelInstanceObject(dslObject, typeClass, type, getOriginalType(), myFactory);
			}
		} else {
			throw new AsTypeCastException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotCastTypeClassNotFound, type));
		}
		return result;
	}

	public IModelInstanceElement copyForAtPre() throws CopyForAtPreException {
		IModelInstanceElement result = null;
		if (dslObject instanceof Cloneable) {
			try {
				result = copyForAtPreWithClone();
			} catch (CopyForAtPreException e) {
				e.printStackTrace();
			}
		}
		if (result == null) {
			copyForAtPreWithReflections();
		}
		return result;
	}

	public Object getObject() {
		return dslObject;
	}

	public IModelInstanceElement getProperty(Property property) throws PropertyAccessException, PropertyNotFoundException {
		if (property == null) {
			throw new IllegalArgumentException("Property 'property' must not be null!");
		}
		if (dslObject == null) {
			return myFactory.createModelInstanceElement(null, property.getType());
		} else {
			if (dslObject instanceof Object_) {
				Object_ object = (Object_) dslObject;
				IModelInstanceElement result = null;

				ArrayList<FeatureValue> allFeatureValues = new ArrayList<FeatureValue>();
				allFeatureValues.addAll(object.getFeatureValues());
				allFeatureValues.addAll(associationProperties);

				for (FeatureValue featureValue : allFeatureValues) {
					result = getProperty(featureValue, property);
					if (result != null)
						return result;
				}
			}
		}
		throw new PropertyNotFoundException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstanceObject_PropertyNotFoundInModelInstanceElement, property));
	}

	private IModelInstanceElement getProperty(FeatureValue featureValue, Property property) {
		if (featureValue.feature.name.equals(property.getName())) {
			IModelInstanceElement result = null;
			if (featureValue.values.size() == 1) {
				result = AbstractModelInstance.adaptInvocationResult(featureValue.values.get(0), property.getType(), myFactory);
			} else if (featureValue.values.size() > 1) {
				ArrayList<IModelInstanceElement> adaptedElements = new ArrayList<IModelInstanceElement>();
				for (Object element : featureValue.values) {
					adaptedElements.add(AbstractModelInstance.adaptInvocationResult(element, property.getType(), myFactory));
				}
				result = myFactory.createModelInstanceCollection(adaptedElements, true, false, property.getType());
			} else {
				if (property instanceof AssociationProperty) {
					result = new FUMLModelInstanceObject(null, property.getType(), property.getType(), myFactory);
				} else {
					result = AbstractModelInstance.adaptInvocationResult(null, property.getType(), myFactory);
				}
			}
			return result;
		}
		return null;
	}

	public IModelInstanceElement invokeOperation(Operation operation, List<IModelInstanceElement> args) throws OperationNotFoundException,
			OperationAccessException {
		if (operation == null) {
			throw new IllegalArgumentException("Parameter 'operation' must not be null!");
		} else if (args == null) {
			throw new IllegalArgumentException("Parameter 'args' must not be null!");
		}
		IModelInstanceElement result = null;
		if (dslObject == null) {
			result = myFactory.createModelInstanceElement(null, operation.getType());
		} else {
			Method operationMethod = findMethodOfAdaptedObject(operation);
			int argSize = operation.getInputParameter().size();
			Class<?>[] argumentTypes;
			Object[] argumentValues;

			argSize = Math.min(args.size(), operation.getSignatureParameter().size());
			argumentValues = new Object[args.size()];

			if (operationMethod != null) {
				argumentTypes = operationMethod.getParameterTypes();

				for (int index = 0; index < argSize; index++) {
					argumentValues[index] = createAdaptedElement(args.get(index), argumentTypes[index]);
				}
			} else {
				argumentTypes = new Class[args.size()];

				for (int i = 0; i < args.size(); i++) {
					argumentTypes[i] = args.get(i).getType().getClass();
					argumentValues[i] = createAdaptedElement(args.get(i), argumentTypes[i]);
				}

				for (int index = 0; index < argSize; index++) {
					argumentValues[index] = createAdaptedElement(args.get(index), argumentTypes[index]);
				}
			}

			try {
				Object adapteeResult = null;
				if (operationMethod != null) {
					operationMethod.setAccessible(true);
				}

				if (dslObject instanceof StringValue || dslObject instanceof String || dslObject instanceof IModelInstanceString
						|| dslObject instanceof OclString) {
					String stringValue = null;

					if (dslObject instanceof StringValue) {
						stringValue = ((StringValue) dslObject).value;
					} else if (dslObject instanceof String) {
						stringValue = (String) dslObject;
					} else if (dslObject instanceof IModelInstanceString) {
						stringValue = ((IModelInstanceString) dslObject).getString();
					} else {
						stringValue = ((OclString) dslObject).toString();
					}

					if (operation.getName().equals("=")) {
						if (argumentValues[0] instanceof StringValue || argumentValues[0] instanceof String) {
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(stringValue.equals(argumentValues[0]));
						} else if (argumentValues[0] instanceof IModelInstanceString) {
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(
									stringValue.equals(((IModelInstanceString) argumentValues[0]).getString()));
						}
					} else if (operation.getName().equals("<>")) {
						if (argumentValues[0] instanceof StringValue || argumentValues[0] instanceof String) {
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(!stringValue.equals(argumentValues[0]));
						} else if (argumentValues[0] instanceof IModelInstanceString) {
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(
									!stringValue.equals(((IModelInstanceString) argumentValues[0]).getString()));
						}
					} else if (operation.getName().equals("size")) {
						for (Method aMethod : String.class.getMethods()) {
							if (aMethod.getName().equals("length")) {
								operationMethod = aMethod;
								operationMethod.setAccessible(true);
								if (argumentValues[0] instanceof StringValue || argumentValues[0] instanceof String) {
									adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclInteger(
											(Long) operationMethod.invoke(stringValue, argumentValues));
								} else if (argumentValues[0] instanceof IModelInstanceString) {
									Object[] argumentsToPass = new Object[1];
									argumentsToPass[0] = ((IModelInstanceString) argumentValues[0]).getString();
									adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclInteger(
											(Long) operationMethod.invoke(stringValue, argumentsToPass));
								}

							}
						}
					} else if (operation.getName().equals("concat")) {
						for (Method aMethod : String.class.getMethods()) {
							if (aMethod.getName().equals("concat")) {
								operationMethod = aMethod;
								operationMethod.setAccessible(true);
								if (argumentValues[0] instanceof StringValue || argumentValues[0] instanceof String) {
									adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclString(
											(String) operationMethod.invoke(stringValue, argumentValues));
								} else if (argumentValues[0] instanceof IModelInstanceString) {
									Object[] argumentsToPass = new Object[1];
									argumentsToPass[0] = ((IModelInstanceString) argumentValues[0]).getString();
									adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclString(
											(String) operationMethod.invoke(stringValue, argumentsToPass));
								}
							}
						}
					}
				} else if (dslObject instanceof IntegerValue || dslObject instanceof Integer || dslObject instanceof Long
						|| dslObject instanceof IModelInstanceInteger || dslObject instanceof OclInteger) {
					Long longAnalog = null;

					if (dslObject instanceof IntegerValue) {
						longAnalog = Long.parseLong(String.valueOf(((IntegerValue) dslObject).value));
					} else if (dslObject instanceof Integer) {
						longAnalog = Long.parseLong(String.valueOf((Integer) dslObject));
					} else if (dslObject instanceof Long) {
						longAnalog = (Long) dslObject;
					} else if (dslObject instanceof IModelInstanceInteger) {
						longAnalog = ((IModelInstanceInteger) dslObject).getLong();
					} else {
						longAnalog = ((OclInteger) dslObject).getModelInstanceInteger().getLong();
					}

					if (!(argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer || argumentValues[0] instanceof IModelInstanceInteger || argumentValues[0] instanceof IntegerValue)) {
						adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(false);
					} else {
						Object theResult = null;
						if (operation.getName().equals("=")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() == ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() == ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() == ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean((boolean) theResult);
						} else if (operation.getName().equals("<>")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() != ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() != ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() != ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean((boolean) theResult);
						} else if (operation.getName().equals(">")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() > ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() > ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() > ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean((boolean) theResult);
						} else if (operation.getName().equals("<")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() < ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() < ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() < ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean((boolean) theResult);
						} else if (operation.getName().equals(">=")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() >= ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() >= ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() >= ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean((boolean) theResult);
						} else if (operation.getName().equals("<=")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() <= ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() <= ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() <= ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean((boolean) theResult);
						} else if (operation.getName().equals("+")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() + ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() + ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() + ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclInteger((Long) theResult);
						} else if (operation.getName().equals("-")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() - ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() - ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() - ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclInteger((Long) theResult);
						} else if (operation.getName().equals("*")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() * ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() * ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() * ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclInteger((Long) theResult);
						} else if (operation.getName().equals("/")) {
							if (argumentValues[0] instanceof Long || argumentValues[0] instanceof Integer) {
								theResult = (longAnalog.longValue() / ((Long) argumentValues[0]).longValue());
							} else if (argumentValues[0] instanceof IModelInstanceInteger) {
								theResult = (longAnalog.longValue() / ((IModelInstanceInteger) argumentValues[0]).getLong());
							} else if (argumentValues[0] instanceof IntegerValue) {
								theResult = (longAnalog.longValue() / ((IntegerValue) argumentValues[0]).value);
							}
							adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclInteger((Long) theResult);
						}
					}
				} else if (dslObject instanceof BooleanValue || dslObject instanceof Boolean || dslObject instanceof IModelInstanceBoolean
						|| dslObject instanceof OclBoolean) {
					Boolean booleanValue = null;

					if (dslObject instanceof BooleanValue) {
						booleanValue = ((BooleanValue) dslObject).value;
					} else if (dslObject instanceof Boolean) {
						booleanValue = (Boolean) dslObject;
					} else if (dslObject instanceof IModelInstanceBoolean) {
						booleanValue = ((IModelInstanceBoolean) dslObject).getBoolean();
					} else {
						booleanValue = ((OclBoolean) dslObject).isTrue();
					}

					if (!(argumentValues[0] instanceof Boolean || argumentValues[0] instanceof IModelInstanceBoolean || argumentValues[0] instanceof OclBoolean)) {
						adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(false);
					} else {
						if (operation.getName().equals("and")) {
							if (argumentValues[0] instanceof Boolean) {
								boolean theResult = (booleanValue && (Boolean) argumentValues[0]);
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof IModelInstanceBoolean) {
								boolean theResult = (booleanValue && ((IModelInstanceBoolean) argumentValues[0]).getBoolean());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof OclBoolean) {
								boolean theResult = (booleanValue && ((OclBoolean) argumentValues[0]).isTrue());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							}
						} else if (operation.getName().equals("or")) {
							if (argumentValues[0] instanceof Boolean) {
								boolean theResult = (booleanValue || (Boolean) argumentValues[0]);
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof IModelInstanceBoolean) {
								boolean theResult = (booleanValue || ((IModelInstanceBoolean) argumentValues[0]).getBoolean());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof OclBoolean) {
								boolean theResult = (booleanValue || ((OclBoolean) argumentValues[0]).isTrue());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							}
						} else if (operation.getName().equals("=")) {
							if (argumentValues[0] instanceof Boolean) {
								boolean theResult = (booleanValue == (Boolean) argumentValues[0]);
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof IModelInstanceBoolean) {
								boolean theResult = (booleanValue == ((IModelInstanceBoolean) argumentValues[0]).getBoolean());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof OclBoolean) {
								boolean theResult = (booleanValue == ((OclBoolean) argumentValues[0]).isTrue());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							}
						} else if (operation.getName().equals("<>")) {
							if (argumentValues[0] instanceof Boolean) {
								boolean theResult = (booleanValue != (Boolean) argumentValues[0]);
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof IModelInstanceBoolean) {
								boolean theResult = (booleanValue != ((IModelInstanceBoolean) argumentValues[0]).getBoolean());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							} else if (argumentValues[0] instanceof OclBoolean) {
								boolean theResult = (booleanValue != ((OclBoolean) argumentValues[0]).isTrue());
								adapteeResult = JavaStandardlibraryPlugin.getStandardLibraryFactory().createOclBoolean(theResult);
							}
						}
					}
				}

				/* Adapt the result to the expected result type. */

				try {
					Type adapteeType = myFactory.getTypeUtil().findTypeOfObjectInModel(adapteeResult);
					result = AbstractModelInstance.adaptInvocationResult(adapteeResult, adapteeType, this.myFactory);
				} catch (TypeNotFoundInModelException e) {
					throw new OperationAccessException("Result of invocation of Operation '" + operation.getName()
							+ "' could not be adapted to any model type.", e);
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				throw new OperationAccessException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstanceObject_OperationAccessFailed, operation), e);
			}
		}
		return result;
	}

	@Override
	public boolean isKindOf(Type type) {
		return getOriginalType().conformsTo(type);
	}

	public String toString() {
		StringBuffer result;
		result = new StringBuffer();
		result.append(this.getClass().getSimpleName());
		result.append("[type=");
		result.append(this.getType().getName());
		result.append(",originalType=");
		result.append(this.getOriginalType().getName());
		result.append(",");
		result.append(this.dslObject.toString());
		result.append("]");
		return result.toString();
	}

	private IModelInstanceElement copyForAtPreWithClone() throws CopyForAtPreException {
		IModelInstanceElement result;
		Method cloneMethod;
		try {
			Object adaptedResult;
			cloneMethod = dslObject.getClass().getMethod("clone");
			cloneMethod.setAccessible(true);
			adaptedResult = (Object) cloneMethod.invoke(dslObject);
			result = new FUMLModelInstanceObject(adaptedResult, myAdaptedClass, myType, getOriginalType(), myFactory);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new CopyForAtPreException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstanceObject_CannotCopyForAtPre, getName(), e.getMessage()), e);
		}
		return result;
	}

	private IModelInstanceObject copyForAtPreWithReflections() throws CopyForAtPreException {
		IModelInstanceObject result;
		Class<?> adapteeClass = dslObject.getClass();
		try {
			Object copiedAdaptedObject;
			Constructor<?> emptyConstructor;
			emptyConstructor = adapteeClass.getConstructor(new Class<?>[0]);
			copiedAdaptedObject = (Object) emptyConstructor.newInstance(new Object[0]);
			while (adapteeClass != null) {
				for (Field field : adapteeClass.getDeclaredFields()) {
					field.setAccessible(true);
					if (!(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))) {
						field.set(copiedAdaptedObject, field.get(dslObject));
					}
				}
				adapteeClass = adapteeClass.getSuperclass();
			}
			result = new FUMLModelInstanceObject(copiedAdaptedObject, myAdaptedClass, myType, getOriginalType(), myFactory);
		} catch (SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InstantiationException
				| InvocationTargetException e) {
			throw new CopyForAtPreException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstanceObject_CannotCopyForAtPre, this.getName(), e.getMessage()),
					e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object createAdaptedElement(IModelInstanceElement modelInstanceElement, Class<?> typeClass) {
		Object result;
		if (modelInstanceElement == null) {
			result = null;
		} else if (modelInstanceElement instanceof IModelInstancePrimitiveType) {
			if (modelInstanceElement instanceof IModelInstanceBoolean) {
				result = ((IModelInstanceBoolean) modelInstanceElement).getBoolean();
			} else if (modelInstanceElement instanceof IModelInstanceInteger) {
				result = createAdaptedIntegerValue((IModelInstanceInteger) modelInstanceElement, typeClass);
			} else if (modelInstanceElement instanceof IModelInstanceReal) {
				result = createAdaptedRealValue((IModelInstanceReal) modelInstanceElement, typeClass);
			} else if (modelInstanceElement instanceof IModelInstanceString) {
				result = createAdaptedStringValue((IModelInstanceString) modelInstanceElement, typeClass);
			} else {
				result = null;
			}
		} else if (modelInstanceElement instanceof IModelInstanceEnumerationLiteral) {
			result = createAdaptedEnumerationLiteral((IModelInstanceEnumerationLiteral) modelInstanceElement, typeClass);
		} else if (modelInstanceElement instanceof IModelInstanceCollection<?>) {
			if (typeClass.isArray()) {
				result = createAdaptedArray(modelInstanceElement, typeClass);
			} else if (Collection.class.isAssignableFrom(typeClass)) {
				result = createAdaptedCollection((IModelInstanceCollection<IModelInstanceElement>) modelInstanceElement, typeClass);
			} else {
				throw new IllegalArgumentException(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotRecreateCollection);
			}
		} else if (modelInstanceElement instanceof IModelInstanceObject) {
			result = ((IModelInstanceObject) modelInstanceElement).getObject();
		} else {
			result = null;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Object createAdaptedArray(IModelInstanceElement modelInstanceElement, Class<?> type) {
		Object result;
		if (modelInstanceElement instanceof IModelInstanceCollection) {
			IModelInstanceCollection<IModelInstanceElement> modelInstanceCollection;
			Collection<IModelInstanceElement> adaptedCollection;
			Class<?> componentType;
			int index = 0;
			componentType = type.getComponentType();
			modelInstanceCollection = (IModelInstanceCollection<IModelInstanceElement>) modelInstanceElement;
			adaptedCollection = modelInstanceCollection.getCollection();
			if (componentType.isPrimitive()) {
				if (boolean.class.isAssignableFrom(componentType)) {
					boolean[] array;
					array = new boolean[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceBoolean) anElement).getBoolean().booleanValue();
					}
					result = array;
				} else if (byte.class.isAssignableFrom(componentType)) {
					byte[] array;
					array = new byte[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceInteger) anElement).getLong().byteValue();
					}
					result = array;
				} else if (char.class.isAssignableFrom(componentType)) {
					char[] array;
					array = new char[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceString) anElement).getString().charAt(0);
					}
					result = array;
				} else if (double.class.isAssignableFrom(componentType)) {
					double[] array;
					array = new double[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceReal) anElement).getDouble().doubleValue();
					}
					result = array;
				} else if (float.class.isAssignableFrom(componentType)) {
					float[] array;
					array = new float[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceReal) anElement).getDouble().floatValue();
					}
					result = array;
				} else if (int.class.isAssignableFrom(componentType)) {
					int[] array;
					array = new int[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceInteger) anElement).getLong().intValue();
					}
					result = array;
				} else if (long.class.isAssignableFrom(componentType)) {
					long[] array;
					array = new long[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceInteger) anElement).getLong().longValue();
					}
					result = array;
				} else if (short.class.isAssignableFrom(componentType)) {
					short[] array;
					array = new short[adaptedCollection.size()];
					for (IModelInstanceElement anElement : adaptedCollection) {
						array[index] = ((IModelInstanceInteger) anElement).getLong().shortValue();
					}
					result = array;
				} else {
					throw new IllegalArgumentException(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotRecreateArray);
				}
			} else {
				Object[] array;
				array = (Object[]) Array.newInstance(componentType, adaptedCollection.size());
				for (IModelInstanceElement anElement : adaptedCollection) {
					array[index] = createAdaptedElement(anElement, componentType);
				}
				result = array;
			}
		} else {
			throw new IllegalArgumentException(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotRecreateArray);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Collection<?> createAdaptedCollection(IModelInstanceCollection<IModelInstanceElement> modelInstanceCollection, Class<?> clazzType) {
		Collection<Object> result;
		if (Collection.class.isAssignableFrom(clazzType)) {
			try {
				Constructor<?> collectionConstructor;
				collectionConstructor = clazzType.getConstructor(new Class<?>[0]);
				result = (Collection<Object>) collectionConstructor.newInstance(new Object[0]);
			} catch (SecurityException e) {
				result = null;
			} catch (NoSuchMethodException e) {
				result = null;
			} catch (IllegalArgumentException e) {
				result = null;
			} catch (InstantiationException e) {
				result = null;
			} catch (IllegalAccessException e) {
				result = null;
			} catch (InvocationTargetException e) {
				result = null;
			}
			if (result == null) {
				if (UniqueEList.class.isAssignableFrom(clazzType)) {
					result = new UniqueEList<Object>();
				} else if (List.class.isAssignableFrom(clazzType)) {
					result = new BasicEList<Object>();
				} else if (Set.class.isAssignableFrom(clazzType)) {
					result = new HashSet<Object>();
				}
			}
			if (result != null) {
				Class<?> elementClassType;
				if (clazzType.getTypeParameters().length == 1 && clazzType.getTypeParameters()[0].getBounds().length == 1
						&& clazzType.getTypeParameters()[0].getBounds()[0] instanceof Class) {
					elementClassType = (Class<?>) clazzType.getTypeParameters()[0].getBounds()[0];
				} else {
					elementClassType = Object.class;
				}
				for (IModelInstanceElement anElement : modelInstanceCollection.getCollection()) {
					result.add(createAdaptedElement(anElement, elementClassType));
				}
			} else {
				throw new IllegalArgumentException(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotRecreateCollection);
			}
		} else {
			throw new IllegalArgumentException(FUMLModelInstanceTypeMessages.FUMLModelInstance_CannotRecreateCollection);
		}
		return result;
	}

	private Object createAdaptedEnumerationLiteral(IModelInstanceEnumerationLiteral modelInstanceEnumerationLiteral, Class<?> typeClass) {
		Object result;
		if (typeClass.isEnum()) {
			result = null;
			for (Object anEnumConstant : typeClass.getEnumConstants()) {
				if (anEnumConstant.toString().equals(modelInstanceEnumerationLiteral.getLiteral().getName())) {
					result = anEnumConstant;
					break;
				}
			}
			if (result == null) {
				throw new IllegalArgumentException(NLS.bind(modelInstanceEnumerationLiteral.getLiteral().getQualifiedName(),
						"The enumeration literal could not be adapted to any constant of the given Enum class."));
			}
		} else {
			List<String> enumerationQualifiedName;
			String enumClassName;
			enumerationQualifiedName = modelInstanceEnumerationLiteral.getLiteral().getQualifiedNameList();
			enumerationQualifiedName.remove(enumerationQualifiedName.size() - 1);

			enumClassName = FUMLModelInstanceTypeUtil.toCanonicalName(enumerationQualifiedName);
			try {
				Class<?> enumClass;
				enumClass = loadJavaClass(enumClassName);
				if (enumClass.isEnum()) {
					result = null;
					for (Object anEnumConstant : enumClass.getEnumConstants()) {
						if (anEnumConstant.toString().equals(modelInstanceEnumerationLiteral.getLiteral().getName())) {
							result = anEnumConstant;
							break;
						}
					}
					if (result == null) {
						throw new IllegalArgumentException(NLS.bind(modelInstanceEnumerationLiteral.getLiteral().getQualifiedName(),
								"The enumeration literal could not be adapted to any constant of the given Enum class."));
					}
				} else {
					throw new IllegalArgumentException(NLS.bind(modelInstanceEnumerationLiteral.getLiteral().getQualifiedName(), "The found class " + enumClass
							+ " is not an Enum."));
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(NLS.bind(modelInstanceEnumerationLiteral.getLiteral().getQualifiedName(), e.getMessage()), e);
			}
		}
		return result;
	}

	private Object createAdaptedIntegerValue(IModelInstanceInteger modelInstanceInteger, Class<?> type) {
		Object result;
		if (type.equals(BigDecimal.class)) {
			result = new BigDecimal(modelInstanceInteger.getLong());
		} else if (type.equals(BigInteger.class)) {
			result = BigInteger.valueOf(modelInstanceInteger.getLong());
		} else if (type.equals(byte.class) || type.equals(Byte.class)) {
			result = modelInstanceInteger.getLong().byteValue();
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			result = modelInstanceInteger.getLong().intValue();
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			result = modelInstanceInteger.getLong();
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			result = modelInstanceInteger.getLong().shortValue();
		} else {
			result = modelInstanceInteger.getLong();
		}
		return result;
	}

	private Object createAdaptedRealValue(IModelInstanceReal modelInstanceReal, Class<?> type) {
		Object result;
		if (type.equals(double.class) || type.equals(BigInteger.class)) {
			result = modelInstanceReal.getDouble();
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			result = modelInstanceReal.getDouble().floatValue();
		} else {
			result = modelInstanceReal.getDouble();
		}
		return result;
	}

	private Object createAdaptedStringValue(IModelInstanceString modelInstanceString, Class<?> type) {
		Object result;
		String stringValue = modelInstanceString.getString();

		if (type.equals(char.class) || type.equals(BigInteger.class)) {
			if (stringValue.length() > 0) {
				result = stringValue.toCharArray()[0];
			} else {
				result = null;
			}
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			if (stringValue.length() > 0) {
				result = new Character(stringValue.toCharArray()[0]);
			} else {
				result = null;
			}
		} else {
			result = stringValue;
		}
		return result;
	}

	private Method findMethodOfAdaptedObject(Operation operation) throws OperationNotFoundException {
		Method result = null;
		if (myAdaptedClass == null)
			return null;
		Class<?> methodSourceClass = myAdaptedClass.getClass();
		while (methodSourceClass != null && result == null) {
			for (Method aMethod : methodSourceClass.getDeclaredMethods()) {
				boolean nameIsEqual;
				boolean resultTypeIsConform;
				boolean argumentSizeIsEqual;
				nameIsEqual = aMethod.getName().equals(operation.getName());
				resultTypeIsConform = FUMLModelInstanceTypeUtil.conformsTypeToType(aMethod.getGenericReturnType(), operation.getType());
				argumentSizeIsEqual = aMethod.getParameterTypes().length == operation.getSignatureParameter().size();
				if (nameIsEqual && resultTypeIsConform && argumentSizeIsEqual) {

					java.lang.reflect.Type[] javaTypes = aMethod.getGenericParameterTypes();
					List<Parameter> pivotModelParamters = operation.getSignatureParameter();
					boolean matches = true;
					for (int index = 0; index < operation.getSignatureParameter().size(); index++) {
						if (!FUMLModelInstanceTypeUtil.conformsTypeToType(javaTypes[index], pivotModelParamters.get(index).getType())) {
							matches = false;
							break;
						}
					}
					if (matches) {
						result = aMethod;
						break;
					}
				}
			}
			methodSourceClass = methodSourceClass.getSuperclass();
		}
		if (result == null) {
			throw new OperationNotFoundException(NLS.bind(FUMLModelInstanceTypeMessages.FUMLModelInstanceObject_OperationNotFound, operation,
					dslObject.getClass()));
		}
		return result;
	}

	private Class<?> loadJavaClass(String canonicalName) throws ClassNotFoundException {
		Class<?> result = null;
		result = myAdaptedClass.getClass().getClassLoader().loadClass(canonicalName);
		return result;
	}

	private void adaptEmptyProperties(Object object, Type type) {
		if (object instanceof Object_) {
			Object_ dslObject = (Object_) object;
			for (Property property : type.getOwnedProperty()) {
				if (property instanceof FUMLAssociationProperty) {
					boolean propertyExists = false;
					for (FeatureValue featureValue : dslObject.getFeatureValues()) {
						if (featureValue.feature.name.equals(property.getName())) {
							propertyExists = true;
							break;
						}
					}
					if (!propertyExists) {
						FeatureValue featureValue = new FeatureValue();
						featureValue.feature = ((FUMLAssociationProperty) property).getDslProperty();
						((Object_) object).getFeatureValues().add(featureValue);
					}
				}
			}
		}
	}
}
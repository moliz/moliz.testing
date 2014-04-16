/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.execution.assertions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.uml2.uml.Action;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityParameterNode;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.OutputPin;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.xtext.xbase.XBooleanLiteral;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNullLiteral;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.XStringLiteral;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActionExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;
import org.modelexecution.fumldebug.core.trace.tracemodel.Input;
import org.modelexecution.fumldebug.core.trace.tracemodel.InputParameterSetting;
import org.modelexecution.fumldebug.core.trace.tracemodel.Output;
import org.modelexecution.fumldebug.core.trace.tracemodel.OutputParameterSetting;
import org.modelexecution.fumldebug.core.trace.tracemodel.ValueInstance;
import org.modelexecution.fumldebug.core.trace.tracemodel.ValueSnapshot;
import org.modelexecution.fumltesting.convert.TestDataConverter;
import org.modelexecution.fumltesting.convert.UmlConverter;
import org.modelexecution.fumltesting.execution.OclExecutor;
import org.modelexecution.fumltesting.results.ConstraintResult;
import org.modelexecution.fumltesting.results.StateAssertionResult;
import org.modelexecution.fumltesting.results.StateExpressionResult;
import org.modelexecution.fumltesting.sequence.State;
import org.modelexecution.fumltesting.testLang.ActionReferencePoint;
import org.modelexecution.fumltesting.testLang.ArithmeticOperator;
import org.modelexecution.fumltesting.testLang.Check;
import org.modelexecution.fumltesting.testLang.ConstraintCheck;
import org.modelexecution.fumltesting.testLang.ConstraintReferencePoint;
import org.modelexecution.fumltesting.testLang.FinallyStateAssertion;
import org.modelexecution.fumltesting.testLang.ObjectStateExpression;
import org.modelexecution.fumltesting.testLang.ObjectValue;
import org.modelexecution.fumltesting.testLang.PropertyStateExpression;
import org.modelexecution.fumltesting.testLang.SimpleValue;
import org.modelexecution.fumltesting.testLang.StateAssertion;
import org.modelexecution.fumltesting.testLang.StateExpression;
import org.modelexecution.fumltesting.testLang.TemporalOperator;
import org.modelexecution.fumltesting.testLang.TemporalQuantifier;
import org.modelexecution.fumltesting.testLang.TestLangFactory;
import org.modelexecution.fumltesting.trace.SnapshotUtil;
import org.modelexecution.fumltesting.trace.TraceUtil;

import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.exception.TypeNotFoundInModelException;
import UMLPrimitiveTypes.UnlimitedNatural;
import fUML.Semantics.Classes.Kernel.BooleanValue;
import fUML.Semantics.Classes.Kernel.FeatureValue;
import fUML.Semantics.Classes.Kernel.IntegerValue;
import fUML.Semantics.Classes.Kernel.Link;
import fUML.Semantics.Classes.Kernel.Object_;
import fUML.Semantics.Classes.Kernel.Reference;
import fUML.Semantics.Classes.Kernel.StringValue;
import fUML.Semantics.Classes.Kernel.UnlimitedNaturalValue;
import fUML.Syntax.Classes.Kernel.Class_;
import fUML.Syntax.Classes.Kernel.Property;

/**
 * Utility class for state assertion validation.
 * 
 * @author Stefan Mijatov
 * 
 */
@SuppressWarnings("restriction")
public class StateAssertionValidator {
	/** Utility class for converting input data for activity under test. */
	private TestDataConverter testDataConverter;
	/** Utility class for managing the execution trace. */
	private TraceUtil traceUtil;
	/** Utility class for managing a concrete value instance from an expression. */
	private SnapshotUtil snapshotUtil;

	public StateAssertionValidator(TraceUtil traceUtil) {
		testDataConverter = TestDataConverter.getInstance();
		this.traceUtil = traceUtil;
		snapshotUtil = new SnapshotUtil(traceUtil);
	}

	public StateAssertionResult check(StateAssertion assertion) {
		AssertionPrinter.printStateAssertion(assertion);
		StateAssertionResult result = new StateAssertionResult(assertion);

		if (assertion.getReferencePoint() instanceof ConstraintReferencePoint) {
			System.out.println("CONSTRAINT REFERENCE POINT NOT YET SUPPORTED!");
			return result;
		}

		if (assertion.getChecks() != null && assertion.getChecks().size() > 0) {
			List<State> states = traceUtil.getStates(assertion);
			for (Check check : assertion.getChecks()) {
				if (check instanceof ConstraintCheck) {
					ArrayList<Boolean> results = new ArrayList<Boolean>();
					for (XExpression constraintName : ((ConstraintCheck) check).getConstraintNames()) {
						String name = ((XStringLiteral) constraintName).getValue();
						ValueInstance context = null;
						if (((ConstraintCheck) check).getObject() != null) {
							Object nodeExecution = traceUtil.getExecution(((ConstraintCheck) check).getObject().getRef().eContainer());
							context = traceUtil.getValueInstance(((ConstraintCheck) check).getObject().getRef(), nodeExecution);
						}
						for (State state : states) {
							boolean constraintResultInSingleState = checkConstraint(name, context, state);
							results.add(constraintResultInSingleState);
						}
						boolean overallResult = compileResult(results, assertion);
						ConstraintResult constraintResult = new ConstraintResult(name, assertion);
						constraintResult.setValidationResult(overallResult);
						result.addConstraintResult(constraintResult);

						if (overallResult == false) {
							System.out.println("Constraint " + ((XStringLiteral)constraintName).getValue() + " validation failed!");
						} else {
							System.out.println("Constraint validation success.");
						}
					}
				}
			}
		}

		for (Check check : assertion.getChecks()) {
			if (check instanceof StateExpression) {
				result.addExpressionResult(checkExpression((StateExpression) check));
			}
		}

		AssertionPrinter.printStartEnd();
		return result;
	}

	public StateAssertionResult check(FinallyStateAssertion assertion) {
		StateAssertion stateAssertion = TestLangFactory.eINSTANCE.createStateAssertion();

		stateAssertion.setQuantifier(TemporalQuantifier.ALWAYS);
		stateAssertion.setOperator(TemporalOperator.AFTER);

		ActionReferencePoint point = TestLangFactory.eINSTANCE.createActionReferencePoint();
		point.setAction((Action) traceUtil.getLastExecutedAction());

		stateAssertion.setReferencePoint(point);

		stateAssertion.getChecks().addAll(assertion.getChecks());
		return check(stateAssertion);
	}

	private boolean checkConstraint(String constraintName, ValueInstance contextObject, State state) {
		IModelInstance modelInstance = OclExecutor.getInstance().getEmptyModelInstance();

		try {
			for (org.modelexecution.fuml.Semantics.Classes.Kernel.Object object : state.getObjects()) {
				modelInstance.addModelInstanceElement(object);
			}
			for (org.modelexecution.fuml.Semantics.Classes.Kernel.Link link : state.getLinks()) {
				modelInstance.addModelInstanceElement(link);
			}
		} catch (TypeNotFoundInModelException e) {
			e.printStackTrace();
		}
		org.modelexecution.fuml.Semantics.Classes.Kernel.Object contextObjectSnapshot = state.getStateSnapshot(contextObject);
		boolean validationResult = OclExecutor.getInstance().evaluateConstraint(constraintName, contextObjectSnapshot, modelInstance);

		return validationResult;
	}

	private StateExpressionResult checkExpression(StateExpression expression) {
		StateExpressionResult result = new StateExpressionResult(expression);
		ArrayList<ValueSnapshot> list = snapshotUtil.getRelevantSnapshots(expression);

		if (expression.getValue() instanceof SimpleValue) {
			// special case
			if (expression instanceof PropertyStateExpression
					&& ((PropertyStateExpression) expression).getProperty().getOwner() != expression.getPin().getRef().getType()) {
				result.setValidationResult(processObject(expression, list));
			} else {
				result.setValidationResult(processValue(expression, list));
			}
			AssertionPrinter.print(expression, result.getValidationResult());
			return result;
		} else if (expression.getValue() instanceof ObjectValue) {
			result.setValidationResult(processObject(expression, list));
			AssertionPrinter.print(expression, result.getValidationResult());
			return result;
		} else {
			result.setValidationResult(false);
			result.setError("Type of specified value is not allowed!");
			AssertionPrinter.print(expression, result.getValidationResult());
			return result;
		}

	}

	private boolean processValue(StateExpression expression, List<ValueSnapshot> list) {
		ArrayList<Boolean> results = new ArrayList<Boolean>();
		SimpleValue simpleValue = (SimpleValue) expression.getValue();

		if (list.size() == 0) {
			if (simpleValue.getValue() instanceof XNullLiteral && expression.getOperator() == ArithmeticOperator.EQUAL)
				results.add(true);
			else
				return false;
		}

		for (ValueSnapshot snapshot : list) {
			if (expression instanceof PropertyStateExpression) {
				Object_ object = (Object_) snapshot.getValue();
				for (FeatureValue featureValue : object.featureValues) {
					String featureName = featureValue.feature.name;
					String targetFeatureName = ((PropertyStateExpression) expression).getProperty().getName();
					if (featureName.equals(targetFeatureName)) {
						if (featureValue.values.size() > 0) {
							if (simpleValue.getValue() instanceof XStringLiteral) {
								String target = ((XStringLiteral) simpleValue.getValue()).getValue();
								String value = ((StringValue) featureValue.values.get(0)).value;
								boolean result = compareValues(expression.getOperator(), value, target);
								results.add(result);
								break;
							}
							if (simpleValue.getValue() instanceof XBooleanLiteral) {
								Boolean target = ((XBooleanLiteral) simpleValue.getValue()).isIsTrue();
								Boolean value = ((BooleanValue) featureValue.values.get(0)).value;
								boolean result = compareValues(expression.getOperator(), value, target);
								results.add(result);
								break;
							}
							if (simpleValue.getValue() instanceof XNumberLiteral) {
								Double target = Double.valueOf(((XNumberLiteral) simpleValue.getValue()).getValue());
								Double value = 0.0;
								if (featureValue.values.get(0) instanceof IntegerValue)
									value = Double.valueOf(((IntegerValue) featureValue.values.get(0)).value);
								if (featureValue.values.get(0) instanceof UnlimitedNaturalValue)
									value = Double.valueOf(((UnlimitedNaturalValue) featureValue.values.get(0)).value.naturalValue);
								boolean result = compareValues(expression.getOperator(), value, target);
								results.add(result);
								break;
							}
						} else {
							if (simpleValue.getValue() instanceof XNullLiteral) {
								if (expression.getOperator() == ArithmeticOperator.EQUAL)
									if (featureValue.values.size() != 0) {
										results.add(false);
									} else {
										results.add(true);
									}
								if (expression.getOperator() == ArithmeticOperator.NOT_EQUAL)
									if (featureValue.values.size() == 0) {
										results.add(false);
									} else {
										results.add(true);
									}
							} else {
								results.add(false);
							}
						}
					}
				}
			}
			if (expression instanceof ObjectStateExpression) {
				if (simpleValue.getValue() instanceof XStringLiteral) {
					String target = ((XStringLiteral) simpleValue.getValue()).getValue();
					String value = ((StringValue) snapshot.getValue()).value;
					boolean result = compareValues(expression.getOperator(), value, target);
					results.add(result);
				}
				if (simpleValue.getValue() instanceof XBooleanLiteral) {
					Boolean target = ((XBooleanLiteral) simpleValue.getValue()).isIsTrue();
					Boolean value = ((BooleanValue) snapshot.getValue()).value;
					boolean result = compareValues(expression.getOperator(), value, target);
					results.add(result);
				}
				if (simpleValue.getValue() instanceof XNumberLiteral) {
					Double target = Double.valueOf(((XNumberLiteral) simpleValue.getValue()).getValue());
					Double value = 0.0;
					if (snapshot.getValue() instanceof IntegerValue)
						value = Double.valueOf(((IntegerValue) snapshot.getValue()).value);
					if (snapshot.getValue() instanceof UnlimitedNaturalValue)
						value = Double.valueOf(((UnlimitedNaturalValue) snapshot.getValue()).value.naturalValue);
					boolean result = compareValues(expression.getOperator(), value, target);
					results.add(result);
				}
				if (simpleValue.getValue() instanceof XNullLiteral) {
					if (expression.getOperator() == ArithmeticOperator.EQUAL)
						if (list.size() != 0) {// at the beginning everything
												// was equal to null
							if (((StateAssertion) expression.eContainer()).getQuantifier() == TemporalQuantifier.SOMETIMES
									&& ((StateAssertion) expression.eContainer()).getOperator() == TemporalOperator.UNTIL)
								results.add(true);
							else
								results.add(false);
						} else {
							results.add(true);
						}
					if (expression.getOperator() == ArithmeticOperator.NOT_EQUAL)
						if (list.size() == 0) {
							results.add(false);
						} else {
							results.add(true);
						}
				}
			}
		}
		return compileResult(results, (StateAssertion) expression.eContainer());
	}

	private boolean processObject(StateExpression expression, List<ValueSnapshot> list) {
		if (expression.getOperator() != ArithmeticOperator.EQUAL && expression.getOperator() != ArithmeticOperator.NOT_EQUAL
				&& expression.getOperator() != ArithmeticOperator.INCLUDES && expression.getOperator() != ArithmeticOperator.EXCLUDES) {
			System.out.println("Operator <, >, <=, and => not allowed!");
			return false;
		}

		Object_ fumlTarget = (Object_) testDataConverter.getFUMLElement(expression.getValue());
		boolean sameType = false;

		if (expression instanceof ObjectStateExpression) {
			ArrayList<Boolean> results = new ArrayList<Boolean>();
			for (ValueSnapshot snapshot : list) {
				Object_ object_ = (Object_) snapshot.getValue();

				// compare types
				search: for (Class_ class_ : object_.types) {
					for (Class_ targetClass_ : fumlTarget.types) {
						if (class_.qualifiedName.equals(targetClass_.qualifiedName))
							sameType = true;
						break search;
					}
				}
				if (sameType == false)
					return false;

				// compare each feature
				for (FeatureValue featureValue : object_.featureValues) {
					if (expression.getOperator() == ArithmeticOperator.EQUAL) {
						for (FeatureValue targetFeatureValue : fumlTarget.featureValues) {
							if (targetFeatureValue.feature.name.equals(featureValue.feature.name)) {
								boolean result = compare(targetFeatureValue, featureValue);
								results.add(result);
							}
						}
					}
					if (expression.getOperator() == ArithmeticOperator.NOT_EQUAL) {
						for (FeatureValue targetFeatureValue : fumlTarget.featureValues) {
							if (targetFeatureValue.feature.name.equals(featureValue.feature.name)) {
								boolean result = compare(targetFeatureValue, featureValue) == false;
								results.add(result);
							}
						}
					}
				}
			}
			return compileResult(results, (StateAssertion) expression.eContainer());
		}

		// link validation
		if (expression instanceof PropertyStateExpression) {
			List<ValueInstance> links = new ArrayList<ValueInstance>();

			PropertyStateExpression propertyExpression = (PropertyStateExpression) expression;
			if (propertyExpression.getProperty().getType() instanceof org.eclipse.uml2.uml.Class) {
				Object variableAction = propertyExpression.getPin().getRef().eContainer();
				Object_ source = null;
				if (propertyExpression.getPin().getRef().eContainer() instanceof Action) {
					ActionExecution execution = (ActionExecution) traceUtil.getExecution(variableAction);
					if (propertyExpression.getPin().getRef() instanceof InputPin) {
						for (Input input : execution.getInputs()) {
							if (input.getInputPin().name.equals(propertyExpression.getPin().getRef().getName()))
								source = (Object_) ((ValueSnapshot) input.getInputValues().get(0).getInputValueSnapshot()).getValue();
						}
					}
					if (propertyExpression.getPin().getRef() instanceof OutputPin) {
						for (Output output : execution.getOutputs()) {
							if (output.getOutputPin().name.equals(propertyExpression.getPin().getRef().getName()))
								source = (Object_) ((ValueSnapshot) output.getOutputValues().get(0).getOutputValueSnapshot()).getValue();
						}
					}
				}
				if (propertyExpression.getPin().getRef().eContainer() instanceof Activity) {
					ActivityExecution execution = (ActivityExecution) traceUtil.getExecution(variableAction);
					ActivityParameterNode parameterNode = (ActivityParameterNode) propertyExpression.getPin().getRef();
					if (parameterNode.getParameter().getDirection().getValue() == ParameterDirectionKind.OUT) {
						for (OutputParameterSetting output : execution.getActivityOutputs()) {
							if (output.getParameter().name.equals(parameterNode.getName()))
								source = (Object_) ((ValueSnapshot) output.getParameterValues().get(0).getValueSnapshot()).getValue();
						}
					}
					if (parameterNode.getParameter().getDirection().getValue() == ParameterDirectionKind.IN) {
						for (InputParameterSetting input : execution.getActivityInputs()) {
							if (input.getParameter().name.equals(parameterNode.getName()))
								source = (Object_) ((ValueSnapshot) input.getParameterValues().get(0).getValueSnapshot()).getValue();
						}
					}
				}

				if (propertyExpression.getValue() instanceof SimpleValue) {
					// this is the case where we might compare null to a link
					SimpleValue value = (SimpleValue) propertyExpression.getValue();
					if (!(value.getValue() instanceof XNullLiteral)) {
						System.out.println("For links only null is allowed!");
						return false;
					}
				}

				for (ValueInstance linkValueInstance : traceUtil.getAllLinks()) {
					Link link = (Link) linkValueInstance.getRuntimeValue();
					boolean sourceContained = false;
					if (link.type == UmlConverter.getInstance().getAssociation(propertyExpression.getProperty().getAssociation())) {
						for (FeatureValue value : link.getFeatureValues()) {
							Reference reference = (Reference) value.values.get(0);
							if (reference.referent == source)
								sourceContained = true;
						}
					}
					if (sourceContained) {
						links.add(linkValueInstance);
					}
				}

				// for each temporal constraint it is enough to remove all those
				// links that the destroyer is not null and is before the
				// referenced action
				List<ValueInstance> linksToRemove = new ArrayList<ValueInstance>();

				for (ValueInstance link : links) {
					if (link.getDestroyer() != null
							&& !traceUtil.isAfter(link.getDestroyer(), getReferencePointExecution((StateAssertion) expression.eContainer()))) {
						linksToRemove.add(link);
					}
				}
				links.removeAll(linksToRemove);

				if (fumlTarget == null) {
					if (propertyExpression.getOperator() == ArithmeticOperator.EQUAL) {
						if (links.size() > 0)
							return false;
						if (links.size() == 0)
							return true;
					} else if (propertyExpression.getOperator() == ArithmeticOperator.NOT_EQUAL) {
						if (links.size() > 0)
							return true;
						if (links.size() == 0)
							return false;
					}
				} else {
					if (links.size() == 0)
						return false;
				}
			}

			ArrayList<Boolean> results = new ArrayList<Boolean>();

			for (ValueInstance link : links) {
				Link theLink = (Link) link.getRuntimeValue();
				for (FeatureValue featureValue : theLink.featureValues) {
					if (featureValue.feature.name.equals(propertyExpression.getProperty().getName())) {
						Object_ realValue = ((Reference) featureValue.values.get(0)).referent;
						if (fumlTarget != null) {
							for (FeatureValue targetValue : fumlTarget.featureValues) {
								for (FeatureValue checkedFeature : realValue.featureValues) {
									if (targetValue.feature.name.equals(checkedFeature.feature.name)) {
										if (expression.getOperator() == ArithmeticOperator.EQUAL) {
											boolean result = compare(targetValue, checkedFeature);
											results.add(result);
										}
										if (expression.getOperator() == ArithmeticOperator.NOT_EQUAL) {
											boolean result = !compare(targetValue, checkedFeature);
											results.add(result);
										}
									}
								}
							}
						}
						if (fumlTarget == null) {
							return false;
						}
					}
				}
			}
			return compileResult(results, (StateAssertion) expression.eContainer());
		}
		return false;
	}

	private ActionExecution getReferencePointExecution(StateAssertion assertion) {
		Action referredAction = null;
		if (assertion.getReferencePoint() instanceof ActionReferencePoint)
			referredAction = ((ActionReferencePoint) assertion.getReferencePoint()).getAction();
		// TODO maybe here I can add constraint reference point?!
		return (ActionExecution) traceUtil.getExecution(referredAction);
	}

	/**
	 * Compares all values for target and real feature values, and returns a
	 * result.
	 */
	private boolean compare(FeatureValue targetFeatureValue, FeatureValue featureValue) {
		if (targetFeatureValue.values.size() != featureValue.values.size()) {
			System.out.println("Feature " + targetFeatureValue.feature.name + " of compared objects contain different values!");
			return false;
		}
		Property property = (Property) targetFeatureValue.feature;
		if (property.association == null) {
			if (targetFeatureValue.values.size() == 0 && featureValue.values.size() == 0)
				return true;
			for (int i = 0; i < targetFeatureValue.values.size(); i++) {
				if (targetFeatureValue.values.get(i) instanceof StringValue) {
					StringValue targetValue = (StringValue) targetFeatureValue.values.get(i);
					StringValue value = (StringValue) featureValue.values.get(i);
					if (targetValue.equals(value))
						return true;
					else {
						System.out.println("Expected: " + targetValue + " Real value: " + value);
						return false;
					}
				}
				if (targetFeatureValue.values.get(i) instanceof IntegerValue) {
					IntegerValue targetValue = (IntegerValue) targetFeatureValue.values.get(i);
					IntegerValue value = (IntegerValue) featureValue.values.get(i);
					if (targetValue.value == value.value)
						return true;
					else {
						System.out.println("Expected: " + targetValue.value + " Real value: " + value.value);
						return false;
					}
				}
				if (targetFeatureValue.values.get(i) instanceof UnlimitedNaturalValue) {
					UnlimitedNaturalValue targetValue = (UnlimitedNaturalValue) targetFeatureValue.values.get(i);
					UnlimitedNaturalValue value = null;
					if (featureValue.values.get(i) instanceof UnlimitedNaturalValue) {
						value = (UnlimitedNaturalValue) featureValue.values.get(i);
					} else if (featureValue.values.get(i) instanceof IntegerValue) {
						value = new UnlimitedNaturalValue();
						value.type = ((IntegerValue) featureValue.values.get(i)).type;
						value.value = new UnlimitedNatural(((IntegerValue) featureValue.values.get(i)).value);
					}
					if (targetValue.value.naturalValue == value.value.naturalValue)
						return true;
					else {
						System.out.println("Expected: " + targetValue.value.naturalValue + " Real value: " + value.value.naturalValue);
						return false;
					}
				}
				if (targetFeatureValue.values.get(i) instanceof BooleanValue) {
					BooleanValue targetValue = (BooleanValue) targetFeatureValue.values.get(i);
					BooleanValue value = (BooleanValue) featureValue.values.get(i);
					if (targetValue.value == value.value)
						return true;
					else {
						System.out.println("Expected: " + targetValue.value + " Real value: " + value.value);
						return false;
					}
				}
			}
		} else {
			// link empty features are skipped
			return true;
		}
		return false;
	}

	/** Comparison of simple values: String, Boolean, Double */
	private boolean compareValues(ArithmeticOperator operator, Object value, Object target) {
		System.out.println("Expected value: " + target + " Real value: " + value);
		if (value instanceof String || value instanceof Boolean) {
			if (operator == ArithmeticOperator.EQUAL)
				if (!value.equals(target))
					return false;
			if (operator == ArithmeticOperator.NOT_EQUAL)
				if (value.equals(target))
					return false;
		}
		if (value instanceof Double) {
			if (operator == ArithmeticOperator.EQUAL)
				if (!value.equals(target))
					return false;
			if (operator == ArithmeticOperator.NOT_EQUAL)
				if (value.equals(target))
					return false;
			if (operator == ArithmeticOperator.GREATER)
				if ((Double) value <= (Double) target)
					return false;
			if (operator == ArithmeticOperator.GREATER_EQUAL)
				if ((Double) value < (Double) target)
					return false;
			if (operator == ArithmeticOperator.SMALLER)
				if ((Double) value >= (Double) target)
					return false;
			if (operator == ArithmeticOperator.SMALLER_EQUAL)
				if ((Double) value > (Double) target)
					return false;
		}
		return true;
	}

	private boolean compileResult(ArrayList<Boolean> results, StateAssertion assertion) {
		switch (assertion.getQuantifier()) {
		case ALWAYS:
			for (boolean result : results) {
				if (result == false)
					return false;
			}
			return true;
		case EVENTUALLY:
			for (int i = results.size() - 1; i >= 0; i--) {
				if (results.get(i) == false) {
					for (int j = i + 1; j < results.size(); j++) {
						if (results.get(j) == false)
							return false;
					}
					return true;
				}
			}
		case IMMEDIATELY:
			return results.get(0);
		case SOMETIMES:
			for (boolean result : results) {
				if (result)
					return true;
			}
			return false;
		}
		return false;
	}
}
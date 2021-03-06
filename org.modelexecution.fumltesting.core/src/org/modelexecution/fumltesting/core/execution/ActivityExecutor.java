/*
 * Copyright (c) 2012 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html 
 */
package org.modelexecution.fumltesting.core.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.modelexecution.fumldebug.core.ExecutionContext;
import org.modelexecution.fumldebug.core.ExecutionEventListener;
import org.modelexecution.fumldebug.core.event.ActivityEntryEvent;
import org.modelexecution.fumldebug.core.event.ActivityExitEvent;
import org.modelexecution.fumldebug.core.event.ActivityNodeExitEvent;
import org.modelexecution.fumldebug.core.event.Event;
import org.modelexecution.fumldebug.core.trace.tracemodel.ActivityExecution;

import fUML.Semantics.Classes.Kernel.Object_;
import fUML.Semantics.Classes.Kernel.Reference;
import fUML.Semantics.Classes.Kernel.Value;
import fUML.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import fUML.Semantics.CommonBehaviors.BasicBehaviors.ParameterValueList;
import fUML.Syntax.Activities.IntermediateActivities.Activity;
import fUML.Syntax.Activities.IntermediateActivities.ActivityNode;
import fUML.Syntax.Activities.IntermediateActivities.ActivityParameterNode;
import fUML.Syntax.Activities.IntermediateActivities.FinalNode;

/**
 * Utility class for executing UML activities.
 * 
 * @author Stefan Mijatov
 * 
 */
public class ActivityExecutor implements ExecutionEventListener {

	private int mainActivityID;
	private int currentActivityID;

	private boolean running;
	private List<Event> eventlist = new ArrayList<Event>();
	private ParameterValueList parameters = new ParameterValueList();
	private Object_ contextObject = null;

	public int executeActivity(Activity activity, HashMap<ActivityParameterNode, Object> activityInputs, Object_ contextObject) {
		loadInputs(activity, activityInputs);
		this.contextObject = contextObject;

		eventlist.removeAll(eventlist);
		ExecutionContext.getInstance().addEventListener(this);

		executeActivity(activity);
		return mainActivityID;
	}

	public ActivityExecution getActivityExecution(int executionID) {
		return ExecutionContext.getInstance().getTrace(executionID).getActivityExecutionByID(executionID);
	}

	private void loadInputs(Activity fumlActivity, HashMap<ActivityParameterNode, Object> inputValues) {
		for (ActivityParameterNode parameter : inputValues.keySet()) {
			ParameterValue parameterValue = new ParameterValue();
			for (ActivityNode node : fumlActivity.node) {
				if (node instanceof ActivityParameterNode && node.name.equals(parameter.parameter.name)) {
					parameterValue.parameter = ((ActivityParameterNode) node).parameter;
					break;
				}
			}
			Object value = inputValues.get(parameter);
			if (value instanceof Object_) {
				Reference reference = new Reference();
				reference.referent = (Object_) value;
				parameterValue.values.add(reference);
			} else {
				parameterValue.values.add((Value) value);
			}
			parameters.add(parameterValue);
		}
	}

	private void executeActivity(Activity fumlActivity) {
		running = true;
		ExecutionContext.getInstance().executeStepwise(fumlActivity, contextObject, parameters);
		while (running) {
			List<ActivityNode> enabledNodes = ExecutionContext.getInstance().getEnabledNodes(currentActivityID);
			ActivityNode nonFinalNode = null;
			if (enabledNodes.size() > 1) {
				for (ActivityNode node : enabledNodes) {
					if (!(node instanceof FinalNode)) {
						nonFinalNode = node;
						break;
					}
				}
				ExecutionContext.getInstance().nextStep(currentActivityID, nonFinalNode);
			}
			if (enabledNodes.size() == 1) {
				nonFinalNode = enabledNodes.get(0);
				ExecutionContext.getInstance().nextStep(currentActivityID, nonFinalNode);
			}
		}
	}

	@Override
	public void notify(Event event) {
		if (event instanceof ActivityEntryEvent && (((ActivityEntryEvent) event).getParent() == null))
			mainActivityID = ((ActivityEntryEvent) event).getActivityExecutionID();
		if (event instanceof ActivityEntryEvent)
			currentActivityID = ((ActivityEntryEvent) event).getActivityExecutionID();
		if (event instanceof ActivityNodeExitEvent)
			currentActivityID = ((ActivityNodeExitEvent) event).getActivityExecutionID();
		if (event instanceof ActivityExitEvent && (((ActivityExitEvent) event).getActivityExecutionID() == mainActivityID))
			running = false;
		eventlist.add(event);
	}
}
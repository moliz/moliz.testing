/*
 * generated by Xtext
 */
package org.modelexecution.fumltesting.ui;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

import org.modelexecution.fumltesting.ui.internal.TestLangActivator;

/**
 * This class was generated. Customizations should only happen in a newly
 * introduced subclass. 
 */
public class TestLangExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return TestLangActivator.getInstance().getBundle();
	}
	
	@Override
	protected Injector getInjector() {
		return TestLangActivator.getInstance().getInjector(TestLangActivator.ORG_MODELEXECUTION_FUMLTESTING_TESTLANG);
	}
	
}

/*
* generated by Xtext
*/
package org.modelexecution.fumltesting;

import org.eclipse.xtext.junit4.IInjectorProvider;

import com.google.inject.Injector;

public class TestLangUiInjectorProvider implements IInjectorProvider {
	
	public Injector getInjector() {
		return org.modelexecution.fumltesting.ui.internal.TestLangActivator.getInstance().getInjector("org.modelexecution.fumltesting.TestLang");
	}
	
}

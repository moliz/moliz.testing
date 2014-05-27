/*
 * generated by Xtext
 */
package org.modelexecution.fumltesting.uml;

import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.modelexecution.fumltesting.uml.scoping.UmlTestLangScopeProvider;
import org.modelexecution.fumltesting.xtext.UmlQualifiedNameProvider;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class UmlTestLangRuntimeModule extends org.modelexecution.fumltesting.uml.AbstractUmlTestLangRuntimeModule {
	public Class<? extends IScopeProvider> bindIScopeProvider() {
		return UmlTestLangScopeProvider.class;
	}

	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return UmlQualifiedNameProvider.class;
	}
}
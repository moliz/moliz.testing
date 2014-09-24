/*
 * generated by Xtext
 */
package org.modelexecution.fumltesting.uml.validation;

import org.eclipse.xtext.validation.Check;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLNodeSpecification;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLOrderAssertion;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLStateAssertion;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLTemporalOperator;
import org.modelexecution.fumltesting.uml.umlTestLang.UmlTestLangPackage;

/**
 * Custom validation rules.
 * 
 * see http://www.eclipse.org/Xtext/documentation.html#validation
 */
public class UmlTestLangJavaValidator extends AbstractUmlTestLangJavaValidator {
	@Check
	public void checkAfterSpecified(UMLStateAssertion assertion) {
		if (assertion.getOperator() == UMLTemporalOperator.UNTIL && assertion.getUntilPoint() != null) {
			warning("Subsequent usage of UNTIL is not allowed!", UmlTestLangPackage.Literals.UML_STATE_ASSERTION__UNTIL_POINT);
		}
	}

	@Check
	public void checkUseOfJokers(UMLOrderAssertion assertion) {
		boolean subsequentJokerUsed = false;
		for (UMLNodeSpecification nodeSpecification : assertion.getOrder().getNodes()) {
			if (isStar(nodeSpecification) || isUnderscore(nodeSpecification)) {
				if (assertion.getOrder().getNodes().size() > assertion.getOrder().getNodes().indexOf(nodeSpecification) + 1) {
					int nextNodeIndex = assertion.getOrder().getNodes().indexOf(nodeSpecification) + 1;
					UMLNodeSpecification nextNode = assertion.getOrder().getNodes().get(nextNodeIndex);
					if (isStar(nextNode) || isUnderscore(nextNode))
						subsequentJokerUsed = true;
				}
			}

		}
		if (subsequentJokerUsed)
			error("Use of subsequent jokers is not allowed!", UmlTestLangPackage.Literals.UML_ORDER_ASSERTION__ORDER);
	}

	private boolean isStar(UMLNodeSpecification nodeSpecification) {
		if (nodeSpecification.getJoker() != null && nodeSpecification.getJoker().equals("*"))
			return true;
		return false;
	}

	private boolean isUnderscore(UMLNodeSpecification nodeSpecification) {
		if (nodeSpecification.getJoker() != null && nodeSpecification.getJoker().equals("_"))
			return true;
		return false;
	}
}
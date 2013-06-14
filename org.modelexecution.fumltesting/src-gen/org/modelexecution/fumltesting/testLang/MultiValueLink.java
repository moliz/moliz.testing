/**
 */
package org.modelexecution.fumltesting.testLang;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multi Value Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.modelexecution.fumltesting.testLang.MultiValueLink#getValues <em>Values</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.modelexecution.fumltesting.testLang.TestLangPackage#getMultiValueLink()
 * @model
 * @generated
 */
public interface MultiValueLink extends Link
{
  /**
   * Returns the value of the '<em><b>Values</b></em>' reference list.
   * The list contents are of type {@link org.modelexecution.fumltesting.testLang.ObjectSpecification}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Values</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Values</em>' reference list.
   * @see org.modelexecution.fumltesting.testLang.TestLangPackage#getMultiValueLink_Values()
   * @model
   * @generated
   */
  EList<ObjectSpecification> getValues();

} // MultiValueLink

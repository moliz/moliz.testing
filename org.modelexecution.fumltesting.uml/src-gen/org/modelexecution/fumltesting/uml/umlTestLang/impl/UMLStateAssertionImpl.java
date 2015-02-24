/**
 */
package org.modelexecution.fumltesting.uml.umlTestLang.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.modelexecution.fumltesting.uml.umlTestLang.UMLCheck;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLReferencePoint;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLStateAssertion;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLTemporalOperator;
import org.modelexecution.fumltesting.uml.umlTestLang.UMLTemporalQuantifier;
import org.modelexecution.fumltesting.uml.umlTestLang.UmlTestLangPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>UML State Assertion</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.modelexecution.fumltesting.uml.umlTestLang.impl.UMLStateAssertionImpl#getQuantifier <em>Quantifier</em>}</li>
 *   <li>{@link org.modelexecution.fumltesting.uml.umlTestLang.impl.UMLStateAssertionImpl#getOperator <em>Operator</em>}</li>
 *   <li>{@link org.modelexecution.fumltesting.uml.umlTestLang.impl.UMLStateAssertionImpl#getReferencePoint <em>Reference Point</em>}</li>
 *   <li>{@link org.modelexecution.fumltesting.uml.umlTestLang.impl.UMLStateAssertionImpl#getUntilPoint <em>Until Point</em>}</li>
 *   <li>{@link org.modelexecution.fumltesting.uml.umlTestLang.impl.UMLStateAssertionImpl#getChecks <em>Checks</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UMLStateAssertionImpl extends UMLAssertionImpl implements UMLStateAssertion
{
  /**
   * The default value of the '{@link #getQuantifier() <em>Quantifier</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQuantifier()
   * @generated
   * @ordered
   */
  protected static final UMLTemporalQuantifier QUANTIFIER_EDEFAULT = UMLTemporalQuantifier.ALWAYS;

  /**
   * The cached value of the '{@link #getQuantifier() <em>Quantifier</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getQuantifier()
   * @generated
   * @ordered
   */
  protected UMLTemporalQuantifier quantifier = QUANTIFIER_EDEFAULT;

  /**
   * The default value of the '{@link #getOperator() <em>Operator</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOperator()
   * @generated
   * @ordered
   */
  protected static final UMLTemporalOperator OPERATOR_EDEFAULT = UMLTemporalOperator.AFTER;

  /**
   * The cached value of the '{@link #getOperator() <em>Operator</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOperator()
   * @generated
   * @ordered
   */
  protected UMLTemporalOperator operator = OPERATOR_EDEFAULT;

  /**
   * The cached value of the '{@link #getReferencePoint() <em>Reference Point</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReferencePoint()
   * @generated
   * @ordered
   */
  protected UMLReferencePoint referencePoint;

  /**
   * The cached value of the '{@link #getUntilPoint() <em>Until Point</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUntilPoint()
   * @generated
   * @ordered
   */
  protected UMLReferencePoint untilPoint;

  /**
   * The cached value of the '{@link #getChecks() <em>Checks</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChecks()
   * @generated
   * @ordered
   */
  protected EList<UMLCheck> checks;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected UMLStateAssertionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return UmlTestLangPackage.Literals.UML_STATE_ASSERTION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UMLTemporalQuantifier getQuantifier()
  {
    return quantifier;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setQuantifier(UMLTemporalQuantifier newQuantifier)
  {
    UMLTemporalQuantifier oldQuantifier = quantifier;
    quantifier = newQuantifier == null ? QUANTIFIER_EDEFAULT : newQuantifier;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, UmlTestLangPackage.UML_STATE_ASSERTION__QUANTIFIER, oldQuantifier, quantifier));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UMLTemporalOperator getOperator()
  {
    return operator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOperator(UMLTemporalOperator newOperator)
  {
    UMLTemporalOperator oldOperator = operator;
    operator = newOperator == null ? OPERATOR_EDEFAULT : newOperator;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, UmlTestLangPackage.UML_STATE_ASSERTION__OPERATOR, oldOperator, operator));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UMLReferencePoint getReferencePoint()
  {
    return referencePoint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetReferencePoint(UMLReferencePoint newReferencePoint, NotificationChain msgs)
  {
    UMLReferencePoint oldReferencePoint = referencePoint;
    referencePoint = newReferencePoint;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT, oldReferencePoint, newReferencePoint);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReferencePoint(UMLReferencePoint newReferencePoint)
  {
    if (newReferencePoint != referencePoint)
    {
      NotificationChain msgs = null;
      if (referencePoint != null)
        msgs = ((InternalEObject)referencePoint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT, null, msgs);
      if (newReferencePoint != null)
        msgs = ((InternalEObject)newReferencePoint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT, null, msgs);
      msgs = basicSetReferencePoint(newReferencePoint, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT, newReferencePoint, newReferencePoint));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UMLReferencePoint getUntilPoint()
  {
    return untilPoint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetUntilPoint(UMLReferencePoint newUntilPoint, NotificationChain msgs)
  {
    UMLReferencePoint oldUntilPoint = untilPoint;
    untilPoint = newUntilPoint;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT, oldUntilPoint, newUntilPoint);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUntilPoint(UMLReferencePoint newUntilPoint)
  {
    if (newUntilPoint != untilPoint)
    {
      NotificationChain msgs = null;
      if (untilPoint != null)
        msgs = ((InternalEObject)untilPoint).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT, null, msgs);
      if (newUntilPoint != null)
        msgs = ((InternalEObject)newUntilPoint).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT, null, msgs);
      msgs = basicSetUntilPoint(newUntilPoint, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT, newUntilPoint, newUntilPoint));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UMLCheck> getChecks()
  {
    if (checks == null)
    {
      checks = new EObjectContainmentEList<UMLCheck>(UMLCheck.class, this, UmlTestLangPackage.UML_STATE_ASSERTION__CHECKS);
    }
    return checks;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT:
        return basicSetReferencePoint(null, msgs);
      case UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT:
        return basicSetUntilPoint(null, msgs);
      case UmlTestLangPackage.UML_STATE_ASSERTION__CHECKS:
        return ((InternalEList<?>)getChecks()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case UmlTestLangPackage.UML_STATE_ASSERTION__QUANTIFIER:
        return getQuantifier();
      case UmlTestLangPackage.UML_STATE_ASSERTION__OPERATOR:
        return getOperator();
      case UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT:
        return getReferencePoint();
      case UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT:
        return getUntilPoint();
      case UmlTestLangPackage.UML_STATE_ASSERTION__CHECKS:
        return getChecks();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case UmlTestLangPackage.UML_STATE_ASSERTION__QUANTIFIER:
        setQuantifier((UMLTemporalQuantifier)newValue);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__OPERATOR:
        setOperator((UMLTemporalOperator)newValue);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT:
        setReferencePoint((UMLReferencePoint)newValue);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT:
        setUntilPoint((UMLReferencePoint)newValue);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__CHECKS:
        getChecks().clear();
        getChecks().addAll((Collection<? extends UMLCheck>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case UmlTestLangPackage.UML_STATE_ASSERTION__QUANTIFIER:
        setQuantifier(QUANTIFIER_EDEFAULT);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__OPERATOR:
        setOperator(OPERATOR_EDEFAULT);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT:
        setReferencePoint((UMLReferencePoint)null);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT:
        setUntilPoint((UMLReferencePoint)null);
        return;
      case UmlTestLangPackage.UML_STATE_ASSERTION__CHECKS:
        getChecks().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case UmlTestLangPackage.UML_STATE_ASSERTION__QUANTIFIER:
        return quantifier != QUANTIFIER_EDEFAULT;
      case UmlTestLangPackage.UML_STATE_ASSERTION__OPERATOR:
        return operator != OPERATOR_EDEFAULT;
      case UmlTestLangPackage.UML_STATE_ASSERTION__REFERENCE_POINT:
        return referencePoint != null;
      case UmlTestLangPackage.UML_STATE_ASSERTION__UNTIL_POINT:
        return untilPoint != null;
      case UmlTestLangPackage.UML_STATE_ASSERTION__CHECKS:
        return checks != null && !checks.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (quantifier: ");
    result.append(quantifier);
    result.append(", operator: ");
    result.append(operator);
    result.append(')');
    return result.toString();
  }

} //UMLStateAssertionImpl

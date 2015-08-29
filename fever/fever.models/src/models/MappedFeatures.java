/**
 */
package models;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapped Features</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link models.MappedFeatures#getTargets <em>Targets</em>}</li>
 *   <li>{@link models.MappedFeatures#getFeatureName <em>Feature Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see models.ModelsPackage#getMappedFeatures()
 * @model
 * @generated
 */
public interface MappedFeatures extends EObject {
	/**
	 * Returns the value of the '<em><b>Targets</b></em>' reference list.
	 * The list contents are of type {@link models.CompilationTarget}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Targets</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Targets</em>' reference list.
	 * @see models.ModelsPackage#getMappedFeatures_Targets()
	 * @model
	 * @generated
	 */
	EList<CompilationTarget> getTargets();

	/**
	 * Returns the value of the '<em><b>Feature Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Feature Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature Name</em>' attribute.
	 * @see #setFeatureName(String)
	 * @see models.ModelsPackage#getMappedFeatures_FeatureName()
	 * @model
	 * @generated
	 */
	String getFeatureName();

	/**
	 * Sets the value of the '{@link models.MappedFeatures#getFeatureName <em>Feature Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Feature Name</em>' attribute.
	 * @see #getFeatureName()
	 * @generated
	 */
	void setFeatureName(String value);

} // MappedFeatures

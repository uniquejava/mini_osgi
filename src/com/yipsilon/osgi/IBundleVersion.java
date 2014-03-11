package com.yipsilon.osgi; 
 
/** 
 * Version identifier for bundles and packages. 
 * <p> 
 * Version identifiers have four components. 
 * <ol> 
 * <li>Major version. A non-negative integer.</li> 
 * <li>Minor version. A non-negative integer.</li> 
 * <li>Micro version. A non-negative integer.</li> 
 * <li>Qualifier. A text string. See <code>IBundleVersion(String)</code> for the format of the qualifier string.</li> 
 * </ol> 
 * <p> 
 * <code>IBundleVersion</code> objects are immutable. 
 *  
 * @author yipsilon 
 * @since 1.0 
 */ 
public interface IBundleVersion extends Comparable<IBundleVersion> { 
 
  /** 
   * Returns the major component of this version identifier. 
   *  
   * @return The major component. 
   */ 
  public abstract int getMajor(); 
 
  /** 
   * Returns the minor component of this version identifier. 
   *  
   * @return The minor component. 
   */ 
  public abstract int getMinor(); 
 
  /** 
   * Returns the micro component of this version identifier. 
   *  
   * @return The micro component. 
   */ 
  public abstract int getMicro(); 
 
  /** 
   * Returns the qualifier component of this version identifier. 
   *  
   * @return The qualifier component. 
   */ 
  public abstract String getQualifier(); 
 
  /** 
   * Returns a hash code value for the object. 
   *  
   * @return An integer which is a hash code value for this object. 
   */ 
  public abstract int hashCode(); 
 
  /** 
   * Returns the string representation of this version identifier. 
   * <p> 
   * The format of the version string will be <code>major.minor.micro</code> if qualifier is the empty string or 
   * <code>major.minor.micro.qualifier</code> otherwise. 
   *  
   * @return The string representation of this version identifier. 
   */ 
  public abstract String toString(); 
 
  /** 
   * Compares this <code>IBundleVersion</code> object to another object. 
   * <p> 
   * A version is considered to be <b>equal to </b> another version if the major, minor and micro components are equal 
   * and the qualifier component is equal (using <code>String.equals</code>). 
   *  
   * @param object 
   *            The <code>IBundleVersion</code> object to be compared. 
   * @return <code>true</code> if <code>object</code> is a <code>IBundleVersion</code> and is equal to this 
   *         object; <code>false</code> otherwise. 
   */ 
  public abstract boolean equals(Object object); 
 
  /** 
   * Compares this <code>IBundleVersion</code> object to another object. 
   * <p> 
   * A version is considered to be <b>less than </b> another version if its major component is less than the other 
   * version's major component, or the major components are equal and its minor component is less than the other 
   * version's minor component, or the major and minor components are equal and its micro component is less than the 
   * other version's micro component, or the major, minor and micro components are equal and it's qualifier component is 
   * less than the other version's qualifier component (using <code>String.compareTo</code>). 
   * <p> 
   * A version is considered to be <b>equal to</b> another version if the major, minor and micro components are equal 
   * and the qualifier component is equal (using <code>String.compareTo</code>). 
   *  
   * @param version 
   *            The <code>IBundleVersion</code> object to be compared. 
   * @return A negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the 
   *         specified <code>IBundleVersion</code> object. 
   * @throws ClassCastException 
   *             If the specified object is not a <code>IBundleVersion</code>. 
   */ 
  public abstract int compareTo(IBundleVersion version); 
 
}
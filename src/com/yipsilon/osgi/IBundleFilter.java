package com.yipsilon.osgi; 
 
import java.util.Map; 
 
/** 
 * An RFC 1960-based Filter. 
 * <p> 
 * A <code>Filter</code> object can be used numerous times to determine if the match argument matches the filter 
 * string that was used to create the <code>Filter</code> object. 
 * <p> 
 * Some examples of LDAP filters are: 
 *  
 * <pre> 
 *            &quot;(cn=Babs Jensen)&quot; 
 *            &quot;(!(cn=Tim Howes))&quot; 
 *            &quot;(&amp;(&quot; + Constants.OBJECTCLASS + &quot;=Person)(|(sn=Jensen)(cn=Babs J*)))&quot; 
 *            &quot;(o=univ*of*mich*)&quot; 
 * </pre> 
 *  
 * @version 1.0 
 * @see "Framework specification for a description of the filter string syntax." 
 */ 
public interface IBundleFilter { 
 
  /** 
   * Filter using a <code>Dictionary</code> object. The Filter is executed using the <code>Dictionary</code> 
   * object's keys and values. The keys are case insensitively matched with the filter. 
   *  
   * @param dictionary 
   *            The <code>Dictionary</code> object whose keys are used in the match. 
   * @return <code>true</code> if the <code>Dictionary</code> object's keys and values match this filter; 
   *         <code>false</code> otherwise. 
   * @throws IllegalArgumentException 
   *             If <code>dictionary</code> contains case variants of the same key name. 
   */ 
  public abstract boolean match(Map<String, Object> dictionary); 
 
  /** 
   * Filter with case sensitivity using a <code>Dictionary</code> object. The Filter is executed using the 
   * <code>Dictionary</code> object's keys and values. The keys are case sensitively matched with the filter. 
   *  
   * @param dictionary 
   *            The <code>Dictionary</code> object whose keys are used in the match. 
   * @return <code>true</code> if the <code>Dictionary</code> object's keys and values match this filter; 
   *         <code>false</code> otherwise. 
   * @since 1.3 
   */ 
  public abstract boolean matchCase(Map<String, Object> dictionary); 
 
  /** 
   * Returns this <code>Filter</code> object's filter string. 
   * <p> 
   * The filter string is normalized by removing whitespace which does not affect the meaning of the filter. 
   *  
   * @return Filter string. 
   */ 
  public abstract String toString(); 
 
  /** 
   * Compares this <code>Filter</code> object to another object. 
   *  
   * @param obj 
   *            The object to compare against this <code>Filter</code> object. 
   * @return If the other object is a <code>Filter</code> object, then returns 
   *         <code>this.toString().equals(obj.toString()</code>;<code>false</code> otherwise. 
   */ 
  public abstract boolean equals(Object obj); 
 
  /** 
   * Returns the hashCode for this <code>Filter</code> object. 
   *  
   * @return The hashCode of the filter string; that is, <code>this.toString().hashCode()</code>. 
   */ 
  public abstract int hashCode(); 
}
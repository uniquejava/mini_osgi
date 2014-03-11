package com.yipsilon.osgi.service; 
 
import com.yipsilon.osgi.IBundleFilter; 
 
 
/** 
 * An RFC 1960-based Filter. 
 * <p> 
 * <code>Filter</code> objects can be created by calling <code>IServiceContext#createFilter(String)</code> with the chosen filter 
 * string. 
 * <p> 
 * A <code>Filter</code> object can be used numerous times to determine if the match argument matches the filter 
 * string that was used to create the <code>Filter</code> object. 
 * <p> 
 * Some examples of LDAP filters are: 
 *  
 * <pre> 
 *           &quot;(cn=Babs Jensen)&quot; 
 *           &quot;(!(cn=Tim Howes))&quot; 
 *           &quot;(&amp;(&quot; + Constants.OBJECTCLASS + &quot;=Person)(|(sn=Jensen)(cn=Babs J*)))&quot; 
 *           &quot;(o=univ*of*mich*)&quot; 
 * </pre> 
 *  
 * @version $Revision: 1.14 $ 
 * @since 1.1 
 * @see "Framework specification for a description of the filter string syntax." 
 */ 
public interface IServiceFilter extends IBundleFilter { 
  /** 
   * Filter using a service's properties. 
   * <p> 
   * The filter is executed using the keys and values of the referenced service's properties. The keys are case 
   * insensitively matched with the filter. 
   *  
   * @param reference 
   *            The reference to the service whose properties are used in the match. 
   * @return <code>true</code> if the service's properties match this filter; <code>false</code> otherwise. 
   */ 
  public boolean match(IServiceReference reference); 
} 
package com.yipsilon.osgi.service.internal; 
 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.internal.BundleFilter; 
import com.yipsilon.osgi.service.IServiceConstants; 
import com.yipsilon.osgi.service.IServiceFilter; 
import com.yipsilon.osgi.service.IServiceReference; 
 
public class ServiceFilter extends BundleFilter implements IServiceFilter { 
 
  /** 
   * Constructs a {@link ServiceFilter} object. This filter object may be used to match a {@link IServiceReference} or a 
   * Dictionary. 
   * <p> 
   * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be thrown with a human readable message 
   * where the filter became unparsable. 
   *  
   * @param filter 
   *            the filter string. 
   * @exception InvalidSyntaxException 
   *                If the filter parameter contains an invalid filter string that cannot be parsed. 
   */ 
  public ServiceFilter(String filter) throws InvalidSyntaxException { 
    super(filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceFilter#match(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public boolean match(IServiceReference reference) { 
    return match0(((ServiceRegistration) reference.getRegistration()).getProperties()); 
  } 
 
  /** 
   * Returns the leftmost required objectClass value for the filter to evaluate to true. 
   *  
   * @return The leftmost required objectClass value or null if none could be determined. 
   */ 
  public String getRequiredObjectClass() { 
    // just checking for simple filters here where objectClass is the only attr or it is one attr of a base '&' clause 
    // (objectClass=org.acme.BrickService) OK 
    // (&(objectClass=org.acme.BrickService)(|(vendor=IBM)(vendor=SUN))) OK 
    // (objectClass=org.acme.*) NOT OK 
    // (|(objectClass=org.acme.BrickService)(objectClass=org.acme.CementService)) NOT OK 
    // (&(objectClass=org.acme.BrickService)(objectClass=org.acme.CementService)) OK but only the first objectClass is 
    // returned 
    switch (operation) { 
      case EQUAL: 
        if (attr.equalsIgnoreCase(IServiceConstants.OBJECTCLASS) && (value instanceof String)) 
          return (String) value; 
        break; 
      case AND: 
        ServiceFilter[] clauses = (ServiceFilter[]) value; 
        for (int i = 0; i < clauses.length; i++) 
          if (clauses[i].operation == EQUAL) { 
            String result = clauses[i].getRequiredObjectClass(); 
            if (result != null) 
              return result; 
          } 
        break; 
    } 
    return null; 
  } 
 
  /** 
   * Returns a objectClass filter string for the specified objectClass. 
   *  
   * @return A filter string for the specified objectClass or null if the specified objectClass is null. 
   */ 
  public static String getObjectClassFilterString(String objectClass) { 
    if (objectClass == null) 
      return null; 
    return "(" + IServiceConstants.OBJECTCLASS + "=" + objectClass + ")"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ 
  } 
} 
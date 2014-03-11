package com.yipsilon.osgi.service; 
 
import com.yipsilon.osgi.IBundleContext; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
public interface IServiceConstants { 
 
  /** 
   * Service property (named &quot;objectClass&quot;) identifying all of the class names under which a service was 
   * registered in the Framework (of type <code>java.lang.String[]</code>). 
   * <p> 
   * This property is set by the Framework when a service is registered. 
   */ 
  public static final String OBJECTCLASS = "objectClass"; 
 
  /** 
   * Service property (named &quot;service.id&quot;) identifying a service's registration number (of type 
   * <code>java.lang.Long</code>). 
   * <p> 
   * The value of this property is assigned by the Framework when a service is registered. The Framework assigns a 
   * unique value that is larger than all previously assigned values since the Framework was started. These values are 
   * NOT persistent across restarts of the Framework. 
   */ 
  public static final String SERVICE_ID = "service.id"; 
 
  /** 
   * Service property (named &quot;service.ranking&quot;) identifying a service's ranking number (of type 
   * <code>java.lang.Integer</code>). 
   * <p> 
   * This property may be supplied in the <code>properties 
   * Dictionary</code> object passed to the 
   * <code>IBundleContext.registerService</code> method. 
   * <p> 
   * The service ranking is used by the Framework to determine the <i>default </i> service to be returned from a call to 
   * the {@link IBundleContext#getServiceReference} method: If more than one service implements the specified class, the 
   * <code>ServiceReference</code> object with the highest ranking is returned. 
   * <p> 
   * The default ranking is zero (0). A service with a ranking of <code>Integer.MAX_VALUE</code> is very likely to be 
   * returned as the default service, whereas a service with a ranking of <code>Integer.MIN_VALUE</code> is very 
   * unlikely to be returned. 
   * <p> 
   * If the supplied property value is not of type <code>java.lang.Integer</code>, it is deemed to have a ranking 
   * value of zero. 
   */ 
  public static final String SERVICE_RANKING = "service.ranking"; 
}
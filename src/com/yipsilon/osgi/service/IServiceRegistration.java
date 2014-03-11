package com.yipsilon.osgi.service; 
 
import java.util.Map; 
 
import com.yipsilon.osgi.IBundleContext; 
 
/** 
 * A registered service. 
 * <p> 
 * The Framework returns a <code>IServiceRegistration</code> object when a <code>IBundleContext.registerService</code> 
 * method invocation is successful. The <code>IServiceRegistration</code> object is for the private use of the 
 * registering bundle and should not be shared with other bundles. 
 * <p> 
 * The <code>IServiceRegistration</code> object may be used to update the properties of the service or to unregister 
 * the service. 
 *  
 * @version 1.0 
 * @see IBundleContext#registerService(String[],Object,Map) 
 */ 
public interface IServiceRegistration { 
 
  /** 
   * Returns a <code>IServiceReference</code> object for a service being registered. 
   * <p> 
   * The <code>IServiceReference</code> object may be shared with other bundles. 
   *  
   * @throws java.lang.IllegalStateException 
   *             If this <code>IServiceRegistration</code> object has already been unregistered. 
   * @return <code>IServiceReference</code> object. 
   */ 
  public IServiceReference getReference(); 
 
  /** 
   * Updates the properties associated with a service. 
   * <p> 
   * The {@link IServiceConstants#OBJECTCLASS} and {@link IServiceConstants#SERVICE_ID} keys cannot be modified by this 
   * method. These values are set by the Framework when the service is registered in the OSGi environment. 
   * <p> 
   * The following steps are required to modify service properties: 
   * <ol> 
   * <li>The service's properties are replaced with the provided properties. 
   * <li>A service event of type {@link IServiceEvent.TYPE#MODIFIED} is fired. 
   * </ol> 
   *  
   * @param properties 
   *            The properties for this service. See {@link IServiceConstants} for a list of standard service property 
   *            keys. Changes should not be made to this object after calling this method. To update the service's 
   *            properties this method should be called again. 
   * @throws IllegalStateException 
   *             If this <code>IServiceRegistration</code> object has already been unregistered. 
   * @throws IllegalArgumentException 
   *             If <code>properties</code> contains case variants of the same key name. 
   */ 
  public void setProperties(Map<String, Object> properties); 
 
  /** 
   * Unregisters a service. Remove a <code>IServiceRegistration</code> object from the Framework service registry. All 
   * <code>IServiceReference</code> objects associated with this <code>IServiceRegistration</code> object can no 
   * longer be used to interact with the service. 
   * <p> 
   * The following steps are required to unregister a service: 
   * <ol> 
   * <li>The service is removed from the Framework service registry so that it can no longer be used. 
   * <code>IServiceReference</code> objects for the service may no longer be used to get a service object for the 
   * service. 
   * <li>A service event of type {@link IServiceEvent.TYPE#UNREGISTERING} is fired so that bundles using this service 
   * can release their use of it. 
   * <li>For each bundle whose use count for this service is greater than zero: <br> 
   * The bundle's use count for this service is set to zero. <br> 
   * If the service was registered with a {@link IServiceFactory} object, the <code>IServiceFactory.ungetService</code> 
   * method is called to release the service object for the bundle. 
   * </ol> 
   *  
   * @throws java.lang.IllegalStateException 
   *             If this <code>IServiceRegistration</code> object has already been unregistered. 
   * @see IBundleContext#ungetService 
   * @see IServiceFactory#ungetService 
   */ 
  public void unregister(); 
 
  /** 
   * Get hold class names. 
   *  
   * @return class names. 
   */ 
  public String[] getClasses(); 
 
  /** 
   * Get service id. 
   *  
   * @return service id. 
   */ 
  public long getId(); 
 
  /** 
   * Get service ranking. 
   *  
   * @return service ranking. 
   */ 
  public int getRanking(); 
} 
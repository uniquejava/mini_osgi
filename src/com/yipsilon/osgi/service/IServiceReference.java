package com.yipsilon.osgi.service; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.service.internal.ServiceFilter; 
 
/** 
 * A reference to a service. 
 * <p> 
 * The Framework returns <code>IServiceReference</code> objects from the 
 * <code>IBundleContext.getServiceReference</code> and <code>IBundleContext.getServiceReferences</code> methods. 
 * <p> 
 * A <code>IServiceReference</code> object may be shared between bundles and can be used to examine the properties of 
 * the service and to get the service object. 
 * <p> 
 * Every service registered in the Framework has a unique <code>IServiceRegistration</code> object and may have 
 * multiple, distinct <code>IServiceReference</code> objects referring to it. <code>IServiceReference</code> objects 
 * associated with a <code>IServiceRegistration</code> object have the same <code>hashCode</code> and are considered 
 * equal (more specifically, their <code>equals()</code> method will return <code>true</code> when compared). 
 * <p> 
 * If the same service object is registered multiple times, <code>IServiceReference</code> objects associated with 
 * different <code>IServiceRegistration</code> objects are not equal. 
 *  
 * @version 1.0 
 * @see IBundleContext#getServiceReference 
 * @see IBundleContext#getServiceReferences 
 * @see IBundleContext#getService 
 */ 
public interface IServiceReference { 
 
  /** 
   * Returns the property value to which the specified property key is mapped in the properties <code>Map</code> 
   * object of the service referenced by this <code>IServiceReference</code> object. 
   * <p> 
   * Property keys are case-insensitive. 
   * <p> 
   * This method must continue to return property values after the service has been unregistered. This is so references 
   * to unregistered services (for example, <code>IServiceReference</code> objects stored in the log) can still be 
   * interrogated. 
   *  
   * @param key 
   *            The property key. 
   * @return The property value to which the key is mapped; <code>null</code> if there is no property named after the 
   *         key. 
   */ 
  public Object getProperty(String key); 
 
  /** 
   * Returns an array of the keys in the properties <code>Map</code> object of the service referenced by this 
   * <code>IServiceReference</code> object. 
   * <p> 
   * This method will continue to return the keys after the service has been unregistered. This is so references to 
   * unregistered services (for example, <code>IServiceReference</code> objects stored in the log) can still be 
   * interrogated. 
   * <p> 
   * This method is <i>case-preserving </i>; this means that every key in the returned array must have the same case as 
   * the corresponding key in the properties <code>Map</code> that was passed to the 
   * {@link IBundleContext#registerService(String[],Object,java.util.Map)} or {@link IServiceRegistration#setProperties} 
   * methods. 
   *  
   * @return An array of property keys. 
   */ 
  public String[] getPropertyKeys(); 
 
  /** 
   * Returns the bundle that registered the service referenced by this <code>IServiceReference</code> object. 
   * <p> 
   * This method must return <code>null</code> when the service has been unregistered. This can be used to determine 
   * if the service has been unregistered. 
   *  
   * @return The bundle that registered the service referenced by this <code>IServiceReference</code> object; 
   *         <code>null</code> if that service has already been unregistered. 
   * @see IBundleContext#registerService(String[],Object,java.util.Map) 
   */ 
  public IBundle getBundle(); 
 
  /** 
   * Returns the bundles that are using the service referenced by this <code>IServiceReference</code> object. 
   * Specifically, this method returns the bundles whose usage count for that service is greater than zero. 
   *  
   * @return An array of bundles whose usage count for the service referenced by this <code>IServiceReference</code> 
   *         object is greater than zero; <code>null</code> if no bundles are currently using that service. 
   * @since 1.1 
   */ 
  public IBundle[] getUsingBundles(); 
 
  /** 
   * Tests if the bundle that registered the service referenced by this <code>IServiceReference</code> and the 
   * specified bundle use the same source for the package of the specified class name. 
   * <p> 
   * This method performs the following checks: 
   * <ol> 
   * <li>Get the package name from the specified class name.</li> 
   * <li>For the bundle that registered the service referenced by this <code>IServiceReference</code> (registrant 
   * bundle); find the source for the package. If no source is found then return <code>true</code> if the registrant 
   * bundle is equal to the specified bundle; otherwise return <code>false</code>.</li> 
   * <li>If the package source of the registrant bundle is equal to the package source of the specified bundle then 
   * return <code>true</code>; otherwise return <code>false</code>.</li> 
   * </ol> 
   *  
   * @param bundle 
   *            The <code>IBundle</code> object to check. 
   * @param className 
   *            The class name to check. 
   * @return <code>true</code> if the bundle which registered the service referenced by this 
   *         <code>IServiceReference</code> and the specified bundle use the same source for the package of the 
   *         specified class name. Otherwise <code>false</code> is returned. 
   * @since 1.3 
   */ 
  public boolean isAssignableTo(IBundle bundle, String className); 
 
  /** 
   * Temporary method. 
   *  
   * @see ServiceFilter#match(IServiceReference) 
   */ 
  public IServiceRegistration getRegistration(); 
} 
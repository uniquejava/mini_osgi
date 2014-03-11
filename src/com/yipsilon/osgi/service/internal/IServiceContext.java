package com.yipsilon.osgi.service.internal; 
 
import java.util.Map; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.IBundleFilter; 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IServiceConstants; 
import com.yipsilon.osgi.service.IServiceEvent; 
import com.yipsilon.osgi.service.IServiceFactory; 
import com.yipsilon.osgi.service.IServiceFilter; 
import com.yipsilon.osgi.service.IServiceListener; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
 
public interface IServiceContext { 
 
  /** 
   * Adds the specified <code>IServiceListener</code> object to the context bundle's list of listeners. 
   * <p> 
   * This method is the same as calling <code>IBundleContext.addServiceListener(IServiceListener listener, 
   * String filter)</code> 
   * with <code>filter</code> set to <code>null</code>. 
   *  
   * @param listener 
   *            The <code>IServiceListener</code> object to be added. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see #addServiceListener(IServiceListener, String) 
   */ 
  public void addServiceListener(IServiceListener listener); 
 
  /** 
   * Adds the specified <code>IServiceListener</code> object with the specified <code>filter</code> to the context 
   * bundle's list of listeners. See {@link IServiceFilter} for a description of the filter syntax. 
   * <code>ServiceListener</code> objects are notified when a service has a lifecycle state change. 
   * <p> 
   * If the context bundle's list of listeners already contains a listener <code>l</code> such that 
   * <code>(l==listener)</code>, then this method replaces that listener's filter (which may be <code>null</code>) 
   * with the specified one (which may be <code>null</code>). 
   * <p> 
   * The listener is called if the filter criteria is met. To filter based upon the class of the service, the filter 
   * should reference the {@link IServiceConstants#OBJECTCLASS} property. If <code>filter</code> is <code>null</code>, 
   * all services are considered to match the filter. 
   * <p> 
   * When using a <code>filter</code>, it is possible that the <code>ServiceEvent</code>s for the complete 
   * lifecycle of a service will not be delivered to the listener. For example, if the <code>filter</code> only 
   * matches when the property <code>x</code> has the value <code>1</code>, the listener will not be called if the 
   * service is registered with the property <code>x</code> not set to the value <code>1</code>. Subsequently, when 
   * the service is modified setting property <code>x</code> to the value <code>1</code>, the filter will match and 
   * the listener will be called with a <code>ServiceEvent</code> of type <code>MODIFIED</code>. Thus, the listener 
   * will not be called with a <code>IServiceEvent</code> of type <code>REGISTERED</code>. 
   * <p> 
   *  
   * @param listener 
   *            The <code>IServiceListener</code> object to be added. 
   * @param filter 
   *            The filter criteria. 
   * @throws InvalidSyntaxException 
   *             If <code>filter</code> contains an invalid filter string that cannot be parsed. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see IServiceEvent 
   * @see IServiceListener 
   */ 
  public void addServiceListener(IServiceListener listener, String filter) throws InvalidSyntaxException; 
 
  /** 
   * Removes the specified <code>IServiceListener</code> object from the context bundle's list of listeners. 
   * <p> 
   * If <code>listener</code> is not contained in this context bundle's list of listeners, this method does nothing. 
   *  
   * @param listener 
   *            The <code>IServiceListener</code> to be removed. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   */ 
  public void removeServiceListener(IServiceListener listener); 
 
  /** 
   * Creates a <code>IServiceFilter</code> object. This <code>IServiceFilter</code> object may be used to match a 
   * <code>IServiceReference</code> object or a <code>Map</code> object. 
   * <p> 
   * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be thrown with a human readable message 
   * where the filter became unparsable. 
   *  
   * @param filter 
   *            The filter string. 
   * @return A <code>IServiceFilter</code> object encapsulating the filter string. 
   * @throws InvalidSyntaxException 
   *             If <code>filter</code> contains an invalid filter string that cannot be parsed. 
   * @throws NullPointerException 
   *             If <code>filter</code> is null. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @since 1.1 
   * @see "Framework specification for a description of the filter string syntax." 
   */ 
  public IBundleFilter createFilter(String filter) throws InvalidSyntaxException; 
 
  /** 
   * Returns an array of <code>IServiceReference</code> objects. The returned array of <code>IServiceReference</code> 
   * objects contains services that were registered under the specified class and match the specified filter criteria. 
   * <p> 
   * The list is valid at the time of the call to this method, however since the Framework is a very dynamic 
   * environment, services can be modified or unregistered at anytime. 
   * <p> 
   * <code>filter</code> is used to select the registered service whose properties objects contain keys and values 
   * which satisfy the filter. See {@link IServiceFilter} for a description of the filter string syntax. 
   * <p> 
   * If <code>filter</code> is <code>null</code>, all registered services are considered to match the filter. If 
   * <code>filter</code> cannot be parsed, an {@link InvalidSyntaxException} will be thrown with a human readable 
   * message where the filter became unparsable. 
   * <p> 
   * The following steps are required to select a set of <code>IServiceReference</code> objects: 
   * <ol> 
   * <li>If the filter string is not <code>null</code>, the filter string is parsed and the set 
   * <code>IServiceReference</code> objects of registered services that satisfy the filter is produced. If the filter 
   * string is <code>null</code>, then all registered services are considered to satisfy the filter. 
   * <li>If the Java Runtime Environment supports permissions, the set of <code>IServiceReference</code> objects 
   * produced by the previous step is reduced by checking that the caller has the <code>IServicePermission</code> to 
   * get at least one of the class names under which the service was registered. If the caller does not have the correct 
   * permission for a particular <code>IServiceReference</code> object, then it is removed from the set. 
   * <li>If <code>clazz</code> is not <code>null</code>, the set is further reduced to those services that are an 
   * <code>instanceof</code> and were registered under the specified class. The complete list of classes of which a 
   * service is an instance and which were specified when the service was registered is available from the service's 
   * {@link IServiceConstants#OBJECTCLASS} property. 
   * <li>An array of the remaining <code>IServiceReference</code> objects is returned. 
   * </ol> 
   *  
   * @param clazz 
   *            The class name with which the service was registered or <code>null</code> for all services. 
   * @param filter 
   *            The filter criteria. 
   * @return An array of <code>IServiceReference</code> objects or <code>null</code> if no services are registered 
   *         which satisfy the search. 
   * @throws InvalidSyntaxException 
   *             If <code>filter</code> contains an invalid filter string that cannot be parsed. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @since 1.3 
   */ 
  public IServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException; 
 
  /** 
   * Registers the specified service object with the specified properties under the specified class names into the 
   * Framework. A <code>IServiceRegistration</code> object is returned. The <code>IServiceRegistration</code> object 
   * is for the private use of the bundle registering the service and should not be shared with other bundles. The 
   * registering bundle is defined to be the context bundle. Other bundles can locate the service by using either the 
   * {@link #getServiceReferences} or {@link #getServiceReference} method. 
   * <p> 
   * A bundle can register a service object that implements the {@link IServiceFactory} interface to have more 
   * flexibility in providing service objects to other bundles. 
   * <p> 
   * The following steps are required to register a service: 
   * <ol> 
   * <li>If <code>service</code> is not a <code>IServiceFactory</code>, an <code>IllegalArgumentException</code> 
   * is thrown if <code>service</code> is not an <code>instanceof</code> all the classes named. 
   * <li>The Framework adds these service properties to the specified <code>Map</code> (which may be 
   * <code>null</code>): a property named {@link IServiceConstants#SERVICE_ID} identifying the registration number of 
   * the service and a property named {@link IServiceConstants#OBJECTCLASS} containing all the specified classes. If any 
   * of these properties have already been specified by the registering bundle, their values will be overwritten by the 
   * Framework. 
   * <li>The service is added to the Framework service registry and may now be used by other bundles. 
   * <li>A service event of type <code> IServiceEvent.TYPE#REGISTERED</code> is fired. 
   * <li>A <code>IServiceRegistration</code> object for this registration is returned. 
   * </ol> 
   *  
   * @param clazzes 
   *            The class names under which the service can be located. The class names in this array will be stored in 
   *            the service's properties under the key {@link IServiceConstants#OBJECTCLASS}. 
   * @param service 
   *            The service object or a <code>IServiceFactory</code> object. 
   * @param properties 
   *            The properties for this service. The keys in the properties object must all be <code>String</code> 
   *            objects. See {@link IServiceConstants} for a list of standard service property keys. Changes should not 
   *            be made to this object after calling this method. To update the service's properties the 
   *            {@link IServiceRegistration#setProperties} method must be called. The set of properties may be 
   *            <code>null</code> if the service has no properties. 
   * @return A <code>IServiceRegistration</code> object for use by the bundle registering the service to update the 
   *         service's properties or to unregister the service. 
   * @throws java.lang.IllegalArgumentException 
   *             If one of the following is true: 
   *             <ul> 
   *             <li><code>service</code> is <code>null</code>. 
   *             <li><code>service</code> is not a <code>IServiceFactory</code> object and is not an instance of 
   *             all the named classes in <code>clazzes</code>. 
   *             <li><code>properties</code> contains case variants of the same key name. 
   *             </ul> 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see IServiceRegistration 
   * @see IServiceFactory 
   */ 
  public IServiceRegistration registerService(String[] clazzes, Object service, Map<String, Object> properties); 
 
  /** 
   * Registers the specified service object with the specified properties under the specified class name with the 
   * Framework. 
   * <p> 
   * This method is otherwise identical to {@link #registerService(java.lang.String[], java.lang.Object, java.util.Map)} 
   * and is provided as a convenience when <code>service</code> will only be registered under a single class name. 
   * Note that even in this case the value of the service's {@link IServiceConstants#OBJECTCLASS} property will be an 
   * array of strings, rather than just a single string. 
   *  
   * @param clazz 
   *            The class name under which the service can be located. 
   * @param service 
   *            The service object or a <code>IServiceFactory</code> object. 
   * @param properties 
   *            The properties for this service. 
   * @return A <code>IServiceRegistration</code> object for use by the bundle registering the service to update the 
   *         service's properties or to unregister the service. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see #registerService(java.lang.String[], java.lang.Object, java.util.Map) 
   */ 
  public IServiceRegistration registerService(String clazz, Object service, Map<String, Object> properties); 
 
  /** 
   * Returns an array of <code>IServiceReference</code> objects. The returned array of <code>IServiceReference</code> 
   * objects contains services that were registered under the specified class, match the specified filter criteria, and 
   * the packages for the class names under which the services were registered match the context bundle's packages as 
   * defined in {@link IServiceReference#isAssignableTo(IBundle, String)}. 
   * <p> 
   * The list is valid at the time of the call to this method, however since the Framework is a very dynamic 
   * environment, services can be modified or unregistered at anytime. 
   * <p> 
   * <code>filter</code> is used to select the registered service whose properties objects contain keys and values 
   * which satisfy the filter. See {@link IServiceFilter} for a description of the filter string syntax. 
   * <p> 
   * If <code>filter</code> is <code>null</code>, all registered services are considered to match the filter. If 
   * <code>filter</code> cannot be parsed, an {@link InvalidSyntaxException} will be thrown with a human readable 
   * message where the filter became unparsable. 
   * <p> 
   * The following steps are required to select a set of <code>IServiceReference</code> objects: 
   * <ol> 
   * <li>If the filter string is not <code>null</code>, the filter string is parsed and the set 
   * <code>IServiceReference</code> objects of registered services that satisfy the filter is produced. If the filter 
   * string is <code>null</code>, then all registered services are considered to satisfy the filter. 
   * <li>If the Java Runtime Environment supports permissions, the set of <code>IServiceReference</code> objects 
   * produced by the previous step is reduced by checking that the caller has the <code>IServicePermission</code> to 
   * get at least one of the class names under which the service was registered. If the caller does not have the correct 
   * permission for a particular <code>IServiceReference</code> object, then it is removed from the set. 
   * <li>If <code>clazz</code> is not <code>null</code>, the set is further reduced to those services that are an 
   * <code>instanceof</code> and were registered under the specified class. The complete list of classes of which a 
   * service is an instance and which were specified when the service was registered is available from the service's 
   * {@link IServiceConstants#OBJECTCLASS} property. 
   * <li>The set is reduced one final time by cycling through each <code>IServiceReference</code> object and calling 
   * {@link IServiceReference#isAssignableTo(IBundle, String)} with the context bundle and each class name under which 
   * the <code>IServiceReference</code> object was registered. For any given <code>IServiceReference</code> object, 
   * if any call to {@link IServiceReference#isAssignableTo(IBundle, String)} returns <code>false</code>, then it is 
   * removed from the set of <code>IServiceReference</code> objects. 
   * <li>An array of the remaining <code>IServiceReference</code> objects is returned. 
   * </ol> 
   *  
   * @param clazz 
   *            The class name with which the service was registered or <code>null</code> for all services. 
   * @param filter 
   *            The filter criteria. 
   * @return An array of <code>IServiceReference</code> objects or <code>null</code> if no services are registered 
   *         which satisfy the search. 
   * @throws InvalidSyntaxException 
   *             If <code>filter</code> contains an invalid filter string that cannot be parsed. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   */ 
  public IServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException; 
 
  /** 
   * Releases the service object referenced by the specified <code>IServiceReference</code> object. If the context 
   * bundle's use count for the service is zero, this method returns <code>false</code>. Otherwise, the context 
   * bundle's use count for the service is decremented by one. 
   * <p> 
   * The service's service object should no longer be used and all references to it should be destroyed when a bundle's 
   * use count for the service drops to zero. 
   * <p> 
   * The following steps are required to unget the service object: 
   * <ol> 
   * <li>If the context bundle's use count for the service is zero or the service has been unregistered, 
   * <code>false</code> is returned. 
   * <li>The context bundle's use count for this service is decremented by one. 
   * <li>If the context bundle's use count for the service is currently zero and the service was registered with a 
   * <code>IServiceFactory</code> object, the 
   * {@link IServiceFactory#ungetService(IBundle, IServiceRegistration, Object)} method is called to release the service 
   * object for the context bundle. 
   * <li><code>true</code> is returned. 
   * </ol> 
   *  
   * @param reference 
   *            A reference to the service to be released. 
   * @return <code>false</code> if the context bundle's use count for the service is zero or if the service has been 
   *         unregistered; <code>true</code> otherwise. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see #getService 
   * @see IServiceFactory 
   */ 
  public boolean ungetService(IServiceReference reference); 
 
  /** 
   * Returns the specified service object for a service. 
   * <p> 
   * A bundle's use of a service is tracked by the bundle's use count of that service. Each time a service's service 
   * object is returned by {@link #getService(IServiceReference)} the context bundle's use count for that service is 
   * incremented by one. Each time the service is released by {@link #ungetService(IServiceReference)} the context 
   * bundle's use count for that service is decremented by one. 
   * <p> 
   * When a bundle's use count for a service drops to zero, the bundle should no longer use that service. 
   * <p> 
   * This method will always return <code>null</code> when the service associated with this <code>reference</code> 
   * has been unregistered. 
   * <p> 
   * The following steps are required to get the service object: 
   * <ol> 
   * <li>If the service has been unregistered, <code>null</code> is returned. 
   * <li>The context bundle's use count for this service is incremented by one. 
   * <li>If the context bundle's use count for the service is currently one and the service was registered with an 
   * object implementing the <code>IServiceFactory</code> interface, the 
   * {@link IServiceFactory#getService(IBundle, IServiceRegistration)} method is called to create a service object for 
   * the context bundle. This service object is cached by the Framework. While the context bundle's use count for the 
   * service is greater than zero, subsequent calls to get the services's service object for the context bundle will 
   * return the cached service object. <br> 
   * If the service object returned by the <code>IServiceFactory</code> object is not an <code>instanceof</code> all 
   * the classes named when the service was registered or the <code>IServiceFactory</code> object throws an exception, 
   * <code>null</code> is returned and a Framework event of type <code>IFrameworkEvent.TYPE#ERROR</code> is fired. 
   * <li>The service object for the service is returned. 
   * </ol> 
   *  
   * @param reference 
   *            A reference to the service. 
   * @return A service object for the service associated with <code>reference</code> or <code>null</code> if the 
   *         service is not registered or does not implement the classes under which it was registered in the case of a 
   *         <code>IServiceFactory</code>. 
   * @throws java.lang.SecurityException 
   *             If the caller does not have the <code>IServicePermission</code> to get the service using at least one 
   *             of the named classes the service was registered under and the Java Runtime Environment supports 
   *             permissions. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see #ungetService(IServiceReference) 
   * @see IServiceFactory 
   */ 
  public Object getService(IServiceReference reference); 
 
  /** 
   * Returns a <code>IServiceReference</code> object for a service that implements and was registered under the 
   * specified class. 
   * <p> 
   * This <code>IServiceReference</code> object is valid at the time of the call to this method, however as the 
   * Framework is a very dynamic environment, services can be modified or unregistered at anytime. 
   * <p> 
   * This method is the same as calling {@link IBundleContext#getServiceReferences(String, String)} with a 
   * <code>null</code> filter string. It is provided as a convenience for when the caller is interested in any service 
   * that implements the specified class. 
   * <p> 
   * If multiple such services exist, the service with the highest ranking (as specified in its 
   * {@link IServiceConstants#SERVICE_RANKING} property) is returned. 
   * <p> 
   * If there is a tie in ranking, the service with the lowest service ID (as specified in its 
   * {@link IServiceConstants#SERVICE_ID} property); that is, the service that was registered first is returned. 
   *  
   * @param clazz 
   *            The class name with which the service was registered. 
   * @return A <code>IServiceReference</code> object, or <code>null</code> if no services are registered which 
   *         implement the named class. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   * @see #getServiceReferences(String, String) 
   */ 
  public IServiceReference getServiceReference(String clazz); 
}
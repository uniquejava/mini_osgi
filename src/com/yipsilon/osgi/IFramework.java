package com.yipsilon.osgi; 
 
import com.yipsilon.osgi.service.IAllServiceListener; 
import com.yipsilon.osgi.service.IServiceConstants; 
import com.yipsilon.osgi.service.IServiceFilter; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.internal.IService; 
import com.yipsilon.osgi.services.ILibrary; 
import com.yipsilon.osgi.services.IStartLevel; 
 
public interface IFramework extends IService, IStartLevel, ILibrary{ 
 
  public IFrameworkLog getLog(); 
   
  public void startup(); 
 
  public void shutdown(); 
 
  /** 
   * Adds the specified <code>IFrameworkListener</code> object to the context bundle's list of listeners if not 
   * already present. FrameworkListeners are notified of general Framework events. 
   * <p> 
   * If the context bundle's list of listeners already contains a listener <code>l</code> such that 
   * <code>(l==listener)</code>, this method does nothing. 
   *  
   * @param listener 
   *            The <code>IFrameworkListener</code> object to be added. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   * @see IFrameworkEvent 
   * @see IFrameworkListener 
   */ 
  public void addFrameworkListener(IFrameworkListener listener); 
 
  /** 
   * Removes the specified <code>IFrameworkListener</code> object from the context bundle's list of listeners. 
   * <p> 
   * If <code>listener</code> is not contained in the context bundle's list of listeners, this method does nothing. 
   *  
   * @param listener 
   *            The <code>IFrameworkListener</code> object to be removed. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   */ 
  public void removeFrameworkListener(IFrameworkListener listener); 
 
  /** 
   * Adds the specified <code>IBundleListener</code> object to the context bundle's list of listeners if not already 
   * present. BundleListener objects are notified when a bundle has a lifecycle state change. 
   * <p> 
   * If the context bundle's list of listeners already contains a listener <code>l</code> such that 
   * <code>(l==listener)</code>, this method does nothing. 
   *  
   * @param listener 
   *            The <code>IBundleListener</code> to be added. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   * @see IBundleEvent 
   * @see IBundleListener 
   */ 
  public void addBundleListener(IBundleListener listener); 
 
  /** 
   * Removes the specified <code>IBundleListener</code> object from the context bundle's list of listeners. 
   * <p> 
   * If <code>listener</code> is not contained in the context bundle's list of listeners, this method does nothing. 
   *  
   * @param listener 
   *            The <code>IBundleListener</code> object to be removed. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   */ 
  public void removeBundleListener(IBundleListener listener); 
 
  /** 
   * Adds the specified <code>IAllServiceListener</code> object to the context bundle's list of listeners. 
   *  
   * @param listener 
   *            The <code>IAllServiceListener</code> object to be added. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   */ 
  public void addServiceListener(IAllServiceListener listener); 
 
  /** 
   * Removes the specified <code>IAllServiceListener</code> object from the context bundle's list of listeners. 
   * <p> 
   * If <code>listener</code> is not contained in this context bundle's list of listeners, this method does nothing. 
   *  
   * @param listener 
   *            The <code>IAllServiceListener</code> to be removed. 
   * @throws java.lang.IllegalStateException 
   *             If this IBundleContext is no longer valid. 
   */ 
  public void removeServiceListener(IAllServiceListener listener); 
 
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
   * Returns the bundle with the specified identifier. 
   *  
   * @param id 
   *            The identifier of the bundle to retrieve. 
   * @return A <code>IBundle</code> object or <code>null</code> if the identifier does not match any installed 
   *         bundle. 
   */ 
  public IBundle getBundle(long id); 
 
  /** 
   * Returns the bundle with the specified symbolic name. 
   *  
   * @param symbolicName 
   *            The symbolic name of the bundle to retrieve. 
   * @return A <code>IBundle</code> object or <code>null</code> if the identifier does not match any installed 
   *         bundle. 
   */ 
  public IBundle getBundle(String symbolicName); 
 
  /** 
   * Installs a bundle from the specified location string. A bundle is obtained from <code>location</code> as 
   * interpreted by the Framework in an implementation dependent manner. 
   * <p> 
   * Every installed bundle is uniquely identified by its location string, typically in the form of a URL. 
   * <p> 
   * The following steps are required to install a bundle: 
   * <ol> 
   * <li>If a bundle containing the same location string is already installed, the <code>IBundle</code> object for 
   * that bundle is returned. 
   * <li>The bundle's content is read from the location string. If this fails, a {@link BundleException} is thrown. 
   * <li>The bundle's <code>Bundle-NativeCode</code> dependencies are resolved. If this fails, a 
   * <code>BundleException</code> is thrown. 
   * <li>The bundle's associated resources are allocated. The associated resources minimally consist of a unique 
   * identifier and a persistent storage area if the platform has file system support. If this step fails, a 
   * <code>BundleException</code> is thrown. 
   * <li>If the bundle has declared an Bundle-RequiredExecutionEnvironment header, then the listed execution 
   * environments must be verified against the installed execution environments. If they are not all present, a 
   * <code>BundleException</code> must be thrown. 
   * <li>The bundle's state is set to <code>INSTALLED</code>. 
   * <li>A bundle event of type {@link IBundleEvent.TYPE#INSTALLED} is fired. 
   * <li>The <code>IBundle</code> object for the newly or previously installed bundle is returned. 
   * </ol> 
   * <b>Postconditions, no exceptions thrown </b> 
   * <ul> 
   * <li><code>getState()</code> in {<code>INSTALLED</code>,<code>RESOLVED</code>}. 
   * <li>Bundle has a unique ID. 
   * </ul> 
   * <b>Postconditions, when an exception is thrown </b> 
   * <ul> 
   * <li>Bundle is not installed and no trace of the bundle exists. 
   * </ul> 
   *  
   * @param location 
   *            The location identifier of the bundle to install. 
   * @return The <code>IBundle</code> object of the installed bundle. 
   * @throws BundleException 
   *             If the installation failed. 
   * @throws java.lang.SecurityException 
   *             If the caller does not have the appropriate <code>AdminPermission[installed bundle,LIFECYCLE]</code>, 
   *             and the Java Runtime Environment supports permissions. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   */ 
  public IBundle installBundle(String location) throws BundleException; 
 
  /** 
   * Install bundles from a directory. 
   *  
   * @param location 
   *            bundles directory. 
   * @return installed bundles array. 
   * @throws BundleException 
   *             If the installation failed. 
   */ 
  public IBundle[] installBundles(String location) throws BundleException; 
 
  /** 
   * Returns a list of all installed bundles. 
   * <p> 
   * This method returns a list of all bundles installed in the OSGi environment at the time of the call to this method. 
   * However, since the Framework is a very dynamic environment, bundles can be installed or uninstalled at anytime. 
   *  
   * @return An array of <code>IBundle</code> objects, one object per installed bundle. 
   */ 
  public IBundle[] getBundles(); 
}
package com.yipsilon.osgi; 
 
import java.io.File; 
 
import com.yipsilon.osgi.service.internal.IServiceContext; 
 
/** 
 * A bundle's execution context within the Framework. The context is used to grant access to other methods so that this 
 * bundle can interact with the Framework. 
 * <p> 
 * <code>IBundleContext</code> methods allow a bundle to: 
 * <ul> 
 * <li>Subscribe to events published by the Framework. 
 * <li>Register service objects with the Framework service registry. 
 * <li>Retrieve <code>ServiceReferences</code> from the Framework service registry. 
 * <li>Get and release service objects for a referenced service. 
 * <li>Install new bundles in the Framework. 
 * <li>Get the list of bundles installed in the Framework. 
 * <li>Get the {@link IBundle} object for a bundle. 
 * <li>Create <code>File</code> objects for files in a persistent storage area provided for the bundle by the 
 * Framework. 
 * </ul> 
 * <p> 
 * A <code>IBundleContext</code> object will be created and provided to the bundle associated with this context when 
 * it is started using the {@link IBundleActivator#start} method. The same <code>IBundleContext</code> object will be 
 * passed to the bundle associated with this context when it is stopped using the {@link IBundleActivator#stop} method. 
 * A <code>IBundleContext</code> object is generally for the private use of its associated bundle and is not meant to 
 * be shared with other bundles in the OSGi environment. 
 * <p> 
 * The <code>IBundle</code> object associated with a <code>IBundleContext</code> object is called the 
 * <em>context bundle</em>. 
 * <p> 
 * The <code>IBundleContext</code> object is only valid during the execution of its context bundle; that is, during 
 * the period from when the context bundle is in the <code>STARTING</code>, <code>STOPPING</code>, and 
 * <code>ACTIVE</code> bundle states. If the <code>IBundleContext</code> object is used subsequently, an 
 * <code>IllegalStateException</code> must be thrown. The <code>IBundleContext</code> object must never be reused 
 * after its context bundle is stopped. 
 * <p> 
 * The Framework is the only entity that can create <code>IBundleContext</code> objects and they are only valid within 
 * the Framework that created them. 
 */ 
public interface IBundleContext extends IServiceContext { 
 
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
   * Returns the <code>IBundle</code> object associated with this <code>IBundleContext</code>. This bundle is 
   * called the context bundle. 
   *  
   * @return The <code>IBundle</code> object associated with this <code>IBundleContext</code>. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   */ 
  public IBundle getBundle(); 
 
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
   * Returns a list of all installed bundles. 
   * <p> 
   * This method returns a list of all bundles installed in the OSGi environment at the time of the call to this method. 
   * However, since the Framework is a very dynamic environment, bundles can be installed or uninstalled at anytime. 
   *  
   * @return An array of <code>IBundle</code> objects, one object per installed bundle. 
   */ 
  public IBundle[] getBundles(); 
   
  /** 
   * Creates a <code>File</code> object for a file in the persistent storage area provided for the bundle by the 
   * Framework. This method will return <code>null</code> if the platform does not have file system support. 
   * <p> 
   * A <code>File</code> object for the base directory of the persistent storage area provided for the context bundle 
   * by the Framework can be obtained by calling this method with an empty string as <code>filename</code>. 
   * <p> 
   * If the Java Runtime Environment supports permissions, the Framework will ensure that the bundle has the 
   * <code>java.io.FilePermission</code> with actions <code>read</code>,<code>write</code>,<code>delete</code> 
   * for all files (recursively) in the persistent storage area provided for the context bundle. 
   *  
   * @param filename 
   *            A relative name to the file to be accessed. 
   * @return A <code>File</code> object that represents the requested file or <code>null</code> if the platform does 
   *         not have file system support. 
   * @throws java.lang.IllegalStateException 
   *             If this BundleContext is no longer valid. 
   */ 
  public File getDataFile(String filename); 
} 
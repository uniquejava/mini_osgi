package com.yipsilon.osgi.service; 
 
import com.yipsilon.osgi.IBundle; 
 
/** 
 * A <code>IServiceEvent</code> listener. When a <code>IServiceEvent</code> is fired, it is synchronously delivered 
 * to a <code>BundleListener</code>. 
 * <p> 
 * <code>IServiceListener</code> is a listener interface that may be implemented by a bundle developer. 
 * <p> 
 * A <code>IServiceListener</code> object is registered with the Framework using the 
 * <code>IBundleContext.addServiceListener</code> method. <code>IServiceListener</code> objects are called with a 
 * <code>IServiceEvent</code> object when a service is registered, modified, or is in the process of unregistering. 
 * <p> 
 * <code>IServiceEvent</code> object delivery to <code>IServiceListener</code> objects is filtered by the filter 
 * specified when the listener was registered. If the Java Runtime Environment supports permissions, then additional 
 * filtering is done. <code>IServiceEvent</code> objects are only delivered to the listener if the bundle which 
 * defines the listener object's class has the appropriate <code>ServicePermission</code> to get the service using at 
 * least one of the named classes the service was registered under. 
 * <p> 
 * <code>IServiceEvent</code> object delivery to <code>IServiceListener</code> objects is further filtered according 
 * to package sources as defined in {@link IServiceReference#isAssignableTo(IBundle, String)}. 
 *  
 * @version 1.0 
 * @see IServiceEvent 
 */ 
public interface IServiceListener { 
 
  /** 
   * Receives notification that a service has had a lifecycle change. 
   *  
   * @param event 
   *            The <code>IServiceEvent</code> object. 
   */ 
  public void serviceChanged(IServiceEvent event); 
} 
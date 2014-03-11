package com.yipsilon.osgi; 
 
/** 
 * A <code>IFrameworkEvent</code> listener. When a <code>IFrameworkEvent</code> is fired, it is asynchronously 
 * delivered to a <code>FrameworkListener</code>. 
 * <p> 
 * <code>IFrameworkListener</code> is a listener interface that may be implemented by a bundle developer. A 
 * <code>IFrameworkListener</code> object is registered with the Framework using the 
 * {@link IBundleContext#addFrameworkListener} method. <code>IFrameworkListener</code> objects are called with a 
 * <code>IFrameworkEvent</code> objects when the Framework starts and when asynchronous errors occur. 
 *  
 * @see IFrameworkEvent 
 */ 
public interface IFrameworkListener { 
 
  /** 
   * Receives notification of a general <code>IFrameworkEvent</code> object. 
   *  
   * @param event 
   *            The <code>IFrameworkEvent</code> object. 
   */ 
  public void frameworkEvent(IFrameworkEvent event); 
 
} 
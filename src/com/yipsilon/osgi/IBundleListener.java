package com.yipsilon.osgi; 
 
/** 
 * A <code>IBundleEvent</code> listener. When a <code>IBundleEvent</code> is fired, it is asynchronously delivered 
 * to a <code>IBundleListener</code>. 
 * <p> 
 * <code>IBundleListener</code> is a listener interface that may be implemented by a bundle developer. 
 * <p> 
 * A <code>IBundleListener</code> object is registered with the Framework using the 
 * {@link IBundleContext#addBundleListener} method. <code>IBundleListener</code>s are called with a 
 * <code>IBundleEvent</code> object when a bundle has been installed, resolved, started, stopped, updated, unresolved, 
 * or uninstalled. 
 *  
 * @see IBundleEvent 
 */ 
public interface IBundleListener { 
 
  /** 
   * Receives notification that a bundle has had a lifecycle change. 
   *  
   * @param event 
   *            The <code>IBundleEvent</code>. 
   */ 
  public void bundleChanged(IBundleEvent event); 
 
} 
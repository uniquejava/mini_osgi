package com.yipsilon.osgi;

/**
 * A synchronous <code>IBundleEvent</code> listener. When a <code>IBundleEvent</code> is fired, it is synchronously
 * delivered to a <code>IBundleListener</code>.
 * <p>
 * <code>ISynchronousBundleListener</code> is a listener interface that may be implemented by a bundle developer.
 * <p>
 * A <code>ISynchronousBundleListener</code> object is registered with the Framework using the
 * {@link IBundleContext#addBundleListener} method. <code>ISynchronousBundleListener</code> objects are called with a
 * <code>IBundleEvent</code> object when a bundle has been installed, resolved, starting, started, stopping, stopped,
 * updated, unresolved, or uninstalled.
 * <p>
 * Unlike normal <code>IBundleListener</code> objects, <code>ISynchronousBundleListener</code>s are synchronously
 * called during bundle lifecycle processing. The bundle lifecycle processing will not proceed until all
 * <code>ISynchronousBundleListener</code>s have completed. <code>ISynchronousBundleListener</code> objects will be
 * called prior to <code>IBundleListener</code> objects.
 * <p>
 * 
 * @see IBundleEvent
 */

public interface ISynchronousBundleListener extends IBundleListener {
  // This is a marker interface
}
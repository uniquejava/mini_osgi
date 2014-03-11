package com.yipsilon.osgi.service; 
 
/** 
 * A <code>IServiceEvent</code> listener. 
 * <p> 
 * <code>IAllServiceListener</code> is a listener interface that may be implemented by a bundle developer. 
 * <p> 
 * An <code>IAllServiceListener</code> object is registered with the Framework using the 
 * <code>IBundleContext.addServiceListener</code> method. <code>IAllServiceListener</code> objects are called with a 
 * <code>IServiceEvent</code> object when a service is registered, modified, or is in the process of unregistering. 
 * <p> 
 * <p> 
 * Unlike normal <code>IServiceListener</code> objects, <code>IAllServiceListener</code> objects receive all 
 * ServiceEvent objects regardless of the whether the package source of the listening bundle is equal to the package 
 * source of the bundle that registered the service. This means that the listener may not be able to cast the service 
 * object to any of its corresponding service interfaces if the service object is retrieved. 
 *  
 * @version $Revision: 1.9 $ 
 * @see IServiceEvent 
 * @since 1.3 
 */ 
public interface IAllServiceListener extends IServiceListener {}
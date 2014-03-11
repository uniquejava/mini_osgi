package com.yipsilon.osgi.service.internal; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.IFrameworkEvent; 
import com.yipsilon.osgi.service.IServiceFactory; 
import com.yipsilon.osgi.service.IServiceRegistration; 
 
class ServiceUsing { 
  /** 
   * ServiceFactory object if the service instance represents a factory, null otherwise 
   */ 
  private IServiceFactory factory; 
 
  /** 
   * Service object either registered or that returned by ServiceFactory.getService() 
   */ 
  private Object service; 
 
  /** BundleContext associated with this service use */ 
  private ServiceContext context; 
 
  /** ServiceDescription of the registered service */ 
  private IServiceRegistration registration; 
 
  /** bundle's use count for this service */ 
  private int useCount; 
 
  /** Internal framework object. */ 
 
  /** 
   * Constructs a service use encapsulating the service object. Objects of this class should be constrcuted while 
   * holding the registrations lock. 
   *  
   * @param context 
   *            bundle getting the service 
   * @param registration 
   *            ServiceRegistration of the service 
   */ 
  public ServiceUsing(ServiceContext context, ServiceRegistration registration) { 
    this.context = context; 
    this.registration = registration; 
    this.useCount = 0; 
 
    Object service = registration.getService(); 
    if (service instanceof IServiceFactory) { 
      factory = (IServiceFactory) service; 
      this.service = null; 
    } else { 
      this.factory = null; 
      this.service = service; 
    } 
  } 
 
  /** 
   * Get a service's service object. Retrieves the service object for a service. A bundle's use of a service is tracked 
   * by a use count. Each time a service's service object is returned by {@link #getService}, the context bundle's use 
   * count for the service is incremented by one. Each time the service is release by {@link #ungetService}, the 
   * context bundle's use count for the service is decremented by one. When a bundle's use count for a service drops to 
   * zero, the bundle should no longer use the service. 
   * <p> 
   * The following steps are followed to get the service object: 
   * <ol> 
   * <li>The context bundle's use count for this service is incremented by one. 
   * <li>If the context bundle's use count for the service is now one and the service was registered with a 
   * {@link IServiceFactory}, the {@link IServiceFactory#getService IServiceFactory.getService} method is called to 
   * create a service object for the context bundle. This service object is cached by the framework. While the context 
   * bundle's use count for the service is greater than zero, subsequent calls to get the services's service object for 
   * the context bundle will return the cached service object. <br> 
   * If the service object returned by the {@link IServiceFactory} is not an <code>instanceof</code> all the classes 
   * named when the service was registered or the {@link IServiceFactory} throws an exception, <code>null</code> is 
   * returned and a {@link IFrameworkEvent} of type {@link IFrameworkEvent#ERROR} is broadcast. 
   * <li>The service object for the service is returned. 
   * </ol> 
   *  
   * @return A service object for the service associated with this reference. 
   */ 
  public Object getService() { 
    if ((useCount == 0) && (factory != null)) { 
      Object service = factory.getService(getBundle(), registration); 
 
      if (service == null) { 
        // TODO 
        return (null); 
      } 
 
      String[] clazzes = registration.getClasses(); 
      String invalidService = ServiceContext.checkServiceClass(clazzes, service); 
      if (invalidService != null) { 
        throw new IllegalArgumentException("Service object is not an instanceof " + invalidService); 
      } 
      this.service = service; 
    } 
 
    useCount++; 
 
    return (this.service); 
  } 
 
  /** 
   * Unget a service's service object. Releases the service object for a service. If the context bundle's use count for 
   * the service is zero, this method returns <code>false</code>. Otherwise, the context bundle's use count for the 
   * service is decremented by one. 
   * <p> 
   * The service's service object should no longer be used and all references to it should be destroyed when a bundle's 
   * use count for the service drops to zero. 
   * <p> 
   * The following steps are followed to unget the service object: 
   * <ol> 
   * <li>If the context bundle's use count for the service is zero or the service has been unregistered, 
   * <code>false</code> is returned. 
   * <li>The context bundle's use count for this service is decremented by one. 
   * <li>If the context bundle's use count for the service is now zero and the service was registered with a 
   * {@link IServiceFactory}, the {@link IServiceFactory#ungetService IServiceFactory.ungetService} method is called to 
   * release the service object for the context bundle. 
   * <li><code>true</code> is returned. 
   * </ol> 
   *  
   * @return <code>true</code> if the context bundle's use count for the service is zero otherwise <code>false</code>. 
   */ 
  public boolean ungetService() { 
    if (useCount == 0) { 
      return (true); 
    } 
 
    useCount--; 
 
    if (useCount == 0) { 
      if (factory != null) { 
 
        factory.ungetService(getBundle(), registration, service); 
 
        service = null; 
      } 
 
      return (true); 
    } 
 
    return (false); 
  } 
 
  /** 
   * Release a service's service object. 
   * <ol> 
   * <li>The bundle's use count for this service is set to zero. 
   * <li>If the service was registered with a {@link IServiceFactory}, the 
   * {@link IServiceFactory#ungetService IServiceFactory.ungetService} method is called to release the service object 
   * for the bundle. 
   * </ol> 
   */ 
  public void releaseService() { 
    if ((useCount > 0) && (factory != null)) { 
 
      factory.ungetService(getBundle(), registration, service); 
 
      service = null; 
    } 
 
    useCount = 0; 
  } 
 
  private IBundle getBundle() { 
    IBundleContext context = this.context.getBundleContext(); 
    if (context != null) { 
      return context.getBundle(); 
    } else { 
      return null; 
    } 
  } 
} 
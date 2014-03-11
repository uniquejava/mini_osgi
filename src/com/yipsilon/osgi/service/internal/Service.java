package com.yipsilon.osgi.service.internal; 
 
import java.util.HashMap; 
import java.util.HashSet; 
import java.util.Map; 
import java.util.Set; 
 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.IBundleFilter; 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IAllServiceListener; 
import com.yipsilon.osgi.service.IServiceEvent; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
public class Service implements IService { 
 
  private Map<ServiceContext, IBundleContext> contexts; 
 
  private ServiceRegistry registry; 
 
  private Set<IAllServiceListener> listeners; 
 
  private long serviceId; 
 
  public Service() { 
    contexts = new HashMap<ServiceContext, IBundleContext>(); 
    listeners = new HashSet<IAllServiceListener>(); 
    registry = new ServiceRegistry(); 
    serviceId = 1; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.internal.IService#addServiceListener(com.yipsilon.osgi.service.IAllServiceListener) 
   */ 
  public void addServiceListener(IAllServiceListener listener) { 
    listeners.add(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.internal.IService#removeServiceListener(com.yipsilon.osgi.service.IAllServiceListener) 
   */ 
  public void removeServiceListener(IAllServiceListener listener) { 
    listeners.remove(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.internal.IService#getAllServiceReferences(java.lang.String, java.lang.String) 
   */ 
  public IServiceReference[] getAllServiceReferences(String clazz, String filterstring) throws InvalidSyntaxException { 
    ServiceFilter filter = (filterstring == null) ? null : new ServiceFilter(filterstring); 
    IServiceReference[] services = null; 
    synchronized (registry) { 
      services = registry.lookupServiceReferences(clazz, filter); 
    } 
    return services == null ? InternalConstants.EMPTY_REFERENCE_ARRAY : services; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.internal.IService#getService(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public Object getService(IServiceReference reference) { 
    return ((ServiceRegistration) reference.getRegistration()).getService(); 
  } 
 
  public IServiceContext createContext(IBundleContext bundleContext) { 
 
    // Create service context. 
    ServiceContext serviceContext = new ServiceContext(this); 
 
    // Initialize service context. 
    serviceContext.initialize(bundleContext); 
 
    // Add to cache. 
    contexts.put(serviceContext, bundleContext); 
 
    return serviceContext; 
  } 
 
  public void removeContext(IServiceContext context) { 
    if (contexts.containsKey(context)) { 
 
      // Release service context 
      ((ServiceContext) context).release(); 
 
      // Remove from cache 
      contexts.remove(context); 
    } 
  } 
 
  protected long getNextServiceId() { 
    long id = serviceId; 
    serviceId++; 
    return id; 
  } 
 
  protected IBundleFilter createFilter(String filter) throws InvalidSyntaxException { 
    return new ServiceFilter(filter); 
  } 
 
  protected IServiceReference[] getServiceReferences(ServiceContext context, String clazz) { 
    return registry.lookupServiceReferences(context); 
  } 
 
  protected IServiceReference[] getServiceReferences(ServiceContext context) { 
    return registry.lookupServiceReferences(context); 
  } 
 
  protected void publishService(ServiceContext context, IServiceRegistration serviceReg) { 
    registry.publishService(context, serviceReg); 
  } 
 
  protected void unpublishService(ServiceContext context, IServiceRegistration serviceReg) { 
    registry.unpublishService(context, serviceReg); 
  } 
 
  protected void fireServiceChanged(IServiceEvent event) { 
    for (IAllServiceListener listener : listeners) { 
      listener.serviceChanged(event); 
    } 
  } 
} 
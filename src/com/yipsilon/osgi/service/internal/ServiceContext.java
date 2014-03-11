package com.yipsilon.osgi.service.internal; 
 
import java.security.AccessController; 
import java.security.PrivilegedAction; 
import java.util.HashMap; 
import java.util.HashSet; 
import java.util.Map; 
import java.util.Set; 
 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.IBundleFilter; 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IAllServiceListener; 
import com.yipsilon.osgi.service.IServiceEvent; 
import com.yipsilon.osgi.service.IServiceFactory; 
import com.yipsilon.osgi.service.IServiceListener; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
class ServiceContext implements IServiceContext { 
 
  private Set<IServiceListener> listeners; 
 
  private IBundleContext context; 
 
  private Service service; 
 
  private Map<ServiceReference, ServiceUsing> servicesUsing; 
 
  private Object contextLock = new Object(); 
 
  protected static final ServiceContext[] EMPTY_CONTEXT_ARRAY = new ServiceContext[0]; 
 
  /** 
   *  
   */ 
  public ServiceContext(Service service) { 
    this.service = service; 
    listeners = new HashSet<IServiceListener>(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#addServiceListener(com.yipsilon.osgi.service.IServiceListener) 
   */ 
  public void addServiceListener(IServiceListener listener) { 
    if (listener instanceof IAllServiceListener) { 
      service.addServiceListener((IAllServiceListener) listener); 
    } else { 
      listeners.add(listener); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#removeServiceListener(com.yipsilon.osgi.service.IServiceListener) 
   */ 
  public void removeServiceListener(IServiceListener listener) { 
    if (listener instanceof IAllServiceListener) { 
      service.removeServiceListener((IAllServiceListener) listener); 
    } else { 
      listeners.remove(listener); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#addServiceListener(com.yipsilon.osgi.service.IServiceListener, 
   *      java.lang.String) 
   */ 
  public void addServiceListener(IServiceListener listener, String filter) throws InvalidSyntaxException { 
    listeners.add(new FilteredServiceListener(filter, listener, this)); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#createFilter(java.lang.String) 
   */ 
  public IBundleFilter createFilter(String filter) throws InvalidSyntaxException { 
    return service.createFilter(filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getAllServiceReferences(java.lang.String, java.lang.String) 
   */ 
  public IServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException { 
    return service.getAllServiceReferences(clazz, filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getServiceReferences(java.lang.String, java.lang.String) 
   */ 
  public IServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException { 
    IServiceReference[] services = service.getAllServiceReferences(clazz, filter); 
    if (services.length > 0) { 
      int removed = 0; 
      for (int i = services.length - 1; i >= 0; i--) { 
        if (!isAssignableTo(services[i])) { 
          services[i] = null; 
          removed++; 
        } 
      } 
      if (removed > 0) { 
        IServiceReference[] temp = services; 
        services = new IServiceReference[temp.length - removed]; 
        for (int i = temp.length - 1; i >= 0; i--) { 
          if (temp[i] == null) 
            removed--; 
          else 
            services[i - removed] = temp[i]; 
        } 
      } 
    } 
    return services; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getServiceReference(java.lang.String) 
   */ 
  public IServiceReference getServiceReference(String clazz) { 
    try { 
      IServiceReference[] references = getServiceReferences(clazz, null); 
 
      if (references != null) { 
        int index = 0; 
 
        int length = references.length; 
 
        if (length > 1) /* if more than one service, select highest ranking */{ 
          int rankings[] = new int[length]; 
          int count = 0; 
          int maxRanking = Integer.MIN_VALUE; 
 
          for (int i = 0; i < length; i++) { 
            int ranking = references[i].getRegistration().getRanking(); 
 
            rankings[i] = ranking; 
 
            if (ranking > maxRanking) { 
              index = i; 
              maxRanking = ranking; 
              count = 1; 
            } else { 
              if (ranking == maxRanking) { 
                count++; 
              } 
            } 
          } 
 
          if (count > 1) /* if still more than one service, select lowest id */{ 
            long minId = Long.MAX_VALUE; 
 
            for (int i = 0; i < length; i++) { 
              if (rankings[i] == maxRanking) { 
                long id = references[i].getRegistration().getId(); 
 
                if (id < minId) { 
                  index = i; 
                  minId = id; 
                } 
              } 
            } 
          } 
        } 
 
        return references.length > index ? references[index] : null; 
      } 
    } catch (InvalidSyntaxException e) { 
      // TODO 
      e.printStackTrace(); 
    } 
 
    return null; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#ungetService(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public boolean ungetService(IServiceReference reference) { 
    ServiceRegistration registration = (ServiceRegistration) reference.getRegistration(); 
    return registration.ungetService(this); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getService(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public Object getService(IServiceReference reference) { 
    if (reference == null) { 
      return null; 
    } 
 
    synchronized (contextLock) { 
      if (servicesUsing == null) 
        // Cannot predict how many services a bundle will use, start with a small table. 
        servicesUsing = new HashMap<ServiceReference, ServiceUsing>(10); 
    } 
 
    ServiceRegistration registration = (ServiceRegistration) reference.getRegistration(); 
    return registration.getService(this); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#registerService(java.lang.String[], java.lang.Object, java.util.Map) 
   */ 
  public IServiceRegistration registerService(String[] clazzes, Object service, Map<String, Object> properties) { 
    assert clazzes == null || clazzes.length == 0 : "Class list is null or empty"; 
    assert service == null : "Service is null"; 
 
    /* copy the array so that changes to the original will not affect us. */ 
    String[] copy = new String[clazzes.length]; 
    // doing this the hard way so we can intern the strings 
    for (int i = clazzes.length - 1; i >= 0; i--) { 
      copy[i] = clazzes[i].intern(); 
    } 
    clazzes = copy; 
 
    if (!(service instanceof IServiceFactory)) { 
      String invalidService = checkServiceClass(clazzes, service); 
      assert invalidService != null : "Service object is not an instanceof " + invalidService; 
    } 
 
    return createServiceRegistration(clazzes, service, properties); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#registerService(java.lang.String, java.lang.Object, java.util.Map) 
   */ 
  public IServiceRegistration registerService(String clazz, Object service, Map<String, Object> properties) { 
    String[] clazzes = new String[] { clazz }; 
 
    return (registerService(clazzes, service, properties)); 
  } 
 
  protected void initialize(IBundleContext context) { 
    this.context = context; 
  } 
 
  protected boolean isAssignableTo(IServiceReference reference) { 
    if (context != null) { 
      String[] clazzes = reference.getRegistration().getClasses(); 
      for (int i = 0; i < clazzes.length; i++) { 
        if (!reference.isAssignableTo(context.getBundle(), clazzes[i])) { 
          return false; 
        } 
      } 
      return true; 
    } else { 
      return false; 
    } 
  } 
 
  protected void release() { 
 
    /* service's registered by the bundle, if any, are unregistered. */ 
    for (IServiceReference reference : service.getServiceReferences(this)) { 
      try { 
        reference.getRegistration().unregister(); 
      } catch (IllegalStateException e) { 
        /* already unregistered */ 
      } 
    } 
 
    /* service's used by the bundle, if any, are released. */ 
    if (servicesUsing != null) { 
      int usedSize; 
      ServiceReference[] usedRefs = null; 
 
      synchronized (servicesUsing) { 
        usedSize = servicesUsing.size(); 
 
        if (usedSize > 0) { 
 
          usedRefs = new ServiceReference[usedSize]; 
 
          int i = 0; 
          for (ServiceReference reference : servicesUsing.keySet()) { 
            usedRefs[i++] = reference; 
          } 
        } 
      } 
 
      for (int i = 0; i < usedSize; i++) { 
        ((ServiceRegistration) usedRefs[i].getRegistration()).releaseService(this); 
      } 
 
      servicesUsing = null; 
    } 
 
    context = null; 
  } 
 
  protected void fireServiceChanged(IServiceEvent event) { 
 
    // Trigger bundle listeners 
    for (IServiceListener listener : listeners) { 
      listener.serviceChanged(event); 
    } 
 
    // Trigger global listeners 
    service.fireServiceChanged(event); 
  } 
 
  protected IBundleContext getBundleContext() { 
    return context; 
  } 
 
  protected Service getService() { 
    return service; 
  } 
 
  protected ServiceUsing getUsing(ServiceReference reference) { 
    return servicesUsing.get(reference); 
  } 
 
  protected void setUsing(ServiceReference reference, ServiceUsing use) { 
    servicesUsing.put(reference, use); 
  } 
 
  protected ServiceUsing delUsing(ServiceReference reference) { 
    return servicesUsing.remove(reference); 
  } 
 
  private ServiceRegistration createServiceRegistration(String[] clazzes, Object service, Map<String, Object> properties) { 
    return new ServiceRegistration(this, clazzes, service, properties); 
  } 
 
  // Return the name of the class that is not satisfied by the service object 
  protected static String checkServiceClass(final String[] clazzes, final Object serviceObject) { 
    ClassLoader cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction<Object>() { 
      public Object run() { 
        return serviceObject.getClass().getClassLoader(); 
      } 
    }); 
    for (int i = 0; i < clazzes.length; i++) { 
      try { 
        Class<?> serviceClazz = cl == null ? Class.forName(clazzes[i]) : cl.loadClass(clazzes[i]); 
        if (!serviceClazz.isInstance(serviceObject)) 
          return clazzes[i]; 
      } catch (ClassNotFoundException e) { 
        // This check is rarely done 
        if (extensiveCheckServiceClass(clazzes[i], serviceObject.getClass())) 
          return clazzes[i]; 
      } 
    } 
    return null; 
  } 
 
  private static boolean extensiveCheckServiceClass(String clazz, Class<?> serviceClazz) { 
    if (clazz.equals(serviceClazz.getName())) 
      return false; 
    Class<?>[] interfaces = serviceClazz.getInterfaces(); 
    for (int i = 0; i < interfaces.length; i++) 
      if (!extensiveCheckServiceClass(clazz, interfaces[i])) 
        return false; 
    Class<?> superClazz = serviceClazz.getSuperclass(); 
    if (superClazz != null) 
      if (!extensiveCheckServiceClass(clazz, superClazz)) 
        return false; 
    return true; 
  } 
} 
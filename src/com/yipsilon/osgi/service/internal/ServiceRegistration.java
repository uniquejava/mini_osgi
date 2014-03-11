package com.yipsilon.osgi.service.internal; 
 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.service.IServiceConstants; 
import com.yipsilon.osgi.service.IServiceEvent; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
class ServiceRegistration implements IServiceRegistration { 
 
  /** Reference to this registration. */ 
  private ServiceReference reference; 
 
  /** context which registered this service. */ 
  private ServiceContext context; 
 
  /** 
   * list of contexts using the service. Access to this should be protected by the registrationLock 
   */ 
  private List<ServiceContext> contextsUsing; 
 
  /** service classes for this registration. */ 
  private String[] clazzes; 
 
  /** properties for this registration. */ 
  private Map<String, Object> properties; 
 
  private Service service; 
 
  // protected Service service; 
 
  /** service id. */ 
  private long serviceId; 
 
  /** service ranking. */ 
  private int serviceRanking; 
 
  /** service object for this registration. */ 
  private Object serviceObject; 
 
  /* internal object to use for synchronization */ 
  private Object registrationLock = new Object(); 
 
  /** The registration state */ 
  private int state = REGISTERED; 
 
  public static final int REGISTERED = 0x00; 
 
  public static final int UNREGISTERING = 0x01; 
 
  public static final int UNREGISTERED = 0x02; 
 
  /** 
   * Construct a ServiceRegistration and register the service in the framework's service registry. 
   */ 
  public ServiceRegistration(ServiceContext context, String[] clazzes, Object object, Map<String, Object> properties) { 
    this.context = context; 
    this.clazzes = clazzes; /* must be set before calling createProperties. */ 
    this.serviceObject = object; 
    this.contextsUsing = null; 
    this.reference = new ServiceReference(this); 
    this.service = context.getService(); 
 
    serviceId = service.getNextServiceId(); /* must be set before calling createProperties. */ 
    this.properties = createProperties(properties); /* must be valid after unregister is called. */ 
    service.publishService(context, this); 
 
    /* must not hold the registrations lock when this event is published */ 
    context.fireServiceChanged(new ServiceEvent(IServiceEvent.TYPE.REGISTERED, reference)); 
  } 
 
  /** 
   * Unregister the service. Remove a service registration from the framework's service registry. All 
   * {@link ServiceReferenceImpl} objects for this registration can no longer be used to interact with the service. 
   * <p> 
   * The following steps are followed to unregister a service: 
   * <ol> 
   * <li>The service is removed from the framework's service registry so that it may no longer be used. 
   * {@link ServiceReferenceImpl}s for the service may no longer be used to get a service object for the service. 
   * <li>A {@link ServiceEvent} of type {@link ServiceEvent#UNREGISTERING} is synchronously sent so that bundles using 
   * this service may release their use of the service. 
   * <li>For each bundle whose use count for this service is greater than zero: 
   * <ol> 
   * <li>The bundle's use count for this service is set to zero. 
   * <li>If the service was registered with a {@link ServiceFactory}, the 
   * {@link ServiceFactory#ungetService ServiceFactory.ungetService} method is called to release the service object for 
   * the bundle. 
   * </ol> 
   * </ol> 
   *  
   * @exception java.lang.IllegalStateException 
   *                If this ServiceRegistration has already been unregistered. 
   * @see IBundleContext#ungetService 
   */ 
  public void unregister() { 
    synchronized (registrationLock) { 
      if (state != REGISTERED) { 
        throw new IllegalStateException("Service already unregistered"); 
      } 
 
      service.unpublishService(context, this); 
 
      state = UNREGISTERING; /* mark unregisterING */ 
    } 
 
    /* must not hold the registrationLock when this event is published */ 
    context.fireServiceChanged(new ServiceEvent(IServiceEvent.TYPE.UNREGISTERING, reference)); 
 
    /* we have published the ServiceEvent, now mark the service fully unregistered */ 
    serviceObject = null; 
    state = UNREGISTERED; 
 
    /* must not hold the registrationLock while releasing services */ 
    if (contextsUsing != null) { 
      ServiceContext[] scs = contextsUsing.toArray(ServiceContext.EMPTY_CONTEXT_ARRAY); 
      for (ServiceContext sc : scs) { 
        releaseService(sc); 
      } 
    } 
 
    contextsUsing = null; 
    reference = null; /* mark registration dead */ 
    context = null; 
  } 
 
  /** 
   * Returns a {@link ServiceReferenceImpl} object for this registration. The {@link ServiceReferenceImpl} object may be 
   * shared with other bundles. 
   *  
   * @exception java.lang.IllegalStateException 
   *                If this ServiceRegistration has already been unregistered. 
   * @return A {@link ServiceReferenceImpl} object. 
   */ 
  public IServiceReference getReference() { 
    /* 
     * use reference instead of unregistered so that ServiceFactorys, called by releaseService after the registration is 
     * unregistered, can get the ServiceReference. Note this technically may voilate the spec but makes more sense. 
     */ 
    if (reference == null) { 
      throw new IllegalStateException("Service already unregistered"); 
    } 
 
    return (reference); 
  } 
 
  /** 
   * Update the properties associated with this service. 
   * <p> 
   * The key "objectClass" cannot be modified by this method. It's value is set when the service is registered. 
   * <p> 
   * The following steps are followed to modify a service's properties: 
   * <ol> 
   * <li>The service's properties are replaced with the provided properties. 
   * <li>A {@link ServiceEvent} of type {@link ServiceEvent#MODIFIED} is synchronously sent. 
   * </ol> 
   *  
   * @param props 
   *            The properties for this service. Changes should not be made to this object after calling this method. To 
   *            update the service's properties this method should be called again. 
   * @exception java.lang.IllegalStateException 
   *                If this ServiceRegistration has already been unregistered. 
   * @exception IllegalArgumentException 
   *                If the <tt>properties</tt> parameter contains case variants of the same key name. 
   */ 
  public void setProperties(Map<String, Object> props) { 
    synchronized (registrationLock) { 
      if (state != REGISTERED) /* in the process of unregistering */ 
      { 
        throw new IllegalStateException("Service already unregistered"); 
      } 
 
      this.properties = createProperties(props); 
    } 
 
    /* must not hold the registrationLock when this event is published */ 
    context.fireServiceChanged(new ServiceEvent(IServiceEvent.TYPE.MODIFIED, reference)); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceRegistration#getClasses() 
   */ 
  public String[] getClasses() { 
    return clazzes; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceRegistration#getId() 
   */ 
  public long getId() { 
    return serviceId; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceRegistration#getRanking() 
   */ 
  public int getRanking() { 
    return serviceRanking; 
  } 
 
  /** 
   * Get service object. 
   *  
   * @return service object. 
   */ 
  protected Object getService() { 
    return serviceObject; 
  } 
 
  /** 
   * Get service registration properties. 
   *  
   * @return service registration properties. 
   */ 
  protected Map<String, Object> getProperties() { 
    return properties; 
  } 
 
  /** 
   * Construct a properties object from the dictionary for this ServiceRegistration. 
   *  
   * @param props 
   *            The properties for this service. 
   * @return A Properties object for this ServiceRegistration. 
   */ 
  protected Map<String, Object> createProperties(Map<String, Object> props) { 
    Map<String, Object> properties; 
    if (props != null) { 
      properties = new HashMap<String, Object>(props); 
    } else { 
      properties = new HashMap<String, Object>(); 
    } 
 
    properties.put(IServiceConstants.OBJECTCLASS, clazzes); 
    properties.put(IServiceConstants.SERVICE_ID, new Long(serviceId)); 
 
    Object ranking = properties.get(IServiceConstants.SERVICE_RANKING); 
 
    serviceRanking = (ranking instanceof Integer) ? ((Integer) ranking).intValue() : 0; 
 
    return (properties); 
  } 
 
  /** 
   * Get the value of a service's property. 
   * <p> 
   * This method will continue to return property values after the service has been unregistered. This is so that 
   * references to unregistered service can be interrogated. (For example: ServiceReference objects stored in the log.) 
   *  
   * @param key 
   *            Name of the property. 
   * @return Value of the property or <code>null</code> if there is no property by that name. 
   */ 
  protected Object getProperty(String key) { 
    synchronized (registrationLock) { 
      return (properties.get(key)); 
    } 
  } 
 
  /** 
   * Get the list of key names for the service's properties. 
   * <p> 
   * This method will continue to return the keys after the service has been unregistered. This is so that references to 
   * unregistered service can be interrogated. (For example: ServiceReference objects stored in the log.) 
   *  
   * @return The list of property key names. 
   */ 
  protected String[] getPropertyKeys() { 
    synchronized (registrationLock) { 
      return (properties.keySet().toArray(InternalConstants.EMPTY_STRING_ARRAY)); 
    } 
  } 
 
  /** 
   * Return the bundle which registered the service. 
   * <p> 
   * This method will always return <code>null</code> when the service has been unregistered. This can be used to 
   * determine if the service has been unregistered. 
   *  
   * @return The bundle which registered the service. 
   */ 
  protected IBundle getBundle() { 
    if (reference == null) { 
      return (null); 
    } 
 
    return context.getBundleContext() == null ? null : context.getBundleContext().getBundle(); 
  } 
 
  /** 
   * Return the list of bundle which are using this service. 
   *  
   * @return Array of Bundles using this service. 
   */ 
  protected IBundle[] getUsingBundles() { 
    synchronized (registrationLock) { 
      if (state == UNREGISTERED) /* service unregistered */ 
        return (null); 
 
      if (contextsUsing == null) 
        return (null); 
 
      int size = contextsUsing.size(); 
      if (size == 0) 
        return (null); 
 
      /* Copy list of BundleContext into an array of Bundle. */ 
      IBundle[] bundles = new IBundle[size]; 
      for (int i = 0; i < size; i++) { 
        IBundleContext context = contextsUsing.get(i).getBundleContext(); 
        if (context != null) { 
          bundles[i] = context.getBundle(); 
        } 
      } 
      return (bundles); 
    } 
  } 
 
  /** 
   * Get a service object for the using BundleContext. 
   *  
   * @param context 
   *            BundleContext using service. 
   * @return Service object 
   */ 
  protected Object getService(ServiceContext context) { 
    synchronized (registrationLock) { 
      if (state == UNREGISTERED) /* service unregistered */ 
      { 
        return (null); 
      } 
 
      ServiceUsing use = (ServiceUsing) context.getUsing(reference); 
 
      if (use == null) { 
        use = new ServiceUsing((ServiceContext) context, this); 
 
        Object service = use.getService(); 
 
        if (service != null) { 
          context.setUsing(reference, use); 
 
          if (contextsUsing == null) { 
            contextsUsing = new ArrayList<ServiceContext>(10); 
          } 
 
          contextsUsing.add(context); 
        } 
 
        return (service); 
      } else { 
        return (use.getService()); 
      } 
    } 
  } 
 
  /** 
   * Unget a service for the using BundleContext. 
   *  
   * @param context 
   *            BundleContext using service. 
   * @return <code>false</code> if the context bundle's use count for the service is zero or if the service has been 
   *         unregistered, otherwise <code>true</code>. 
   */ 
  protected boolean ungetService(ServiceContext context) { 
    synchronized (registrationLock) { 
      if (state == UNREGISTERED) { 
        return (false); 
      } 
 
      ServiceUsing use = (ServiceUsing) context.getUsing(reference); 
 
      if (use != null) { 
        if (use.ungetService()) { 
 
          /* use count is now zero */ 
          context.delUsing(reference); 
 
          contextsUsing.remove(context); 
        } 
        return (true); 
      } 
 
      return (false); 
    } 
  } 
 
  /** 
   * Release the service for the using BundleContext. 
   *  
   * @param context 
   *            BundleContext using service. 
   */ 
  protected void releaseService(ServiceContext context) { 
    synchronized (registrationLock) { 
      if (reference == null) { 
        return; 
      } 
 
      ServiceUsing use = (ServiceUsing) context.delUsing(reference); 
 
      if (use != null) { 
        use.releaseService(); 
        // contextsUsing may have been nulled out by use.releaseService() if the registrant bundle 
        // is listening for events and unregisters the service 
        if (contextsUsing != null) 
          contextsUsing.remove(context); 
      } 
    } 
  } 
 
  /** 
   * Return a String representation of this object. 
   *  
   * @return String representation of this object. 
   */ 
  public String toString() { 
    String[] clazzes = this.clazzes; 
    int size = clazzes.length; 
    StringBuffer sb = new StringBuffer(50 * size); 
 
    sb.append('{'); 
 
    for (int i = 0; i < size; i++) { 
      if (i > 0) { 
        sb.append(", "); //$NON-NLS-1$ 
      } 
      sb.append(clazzes[i]); 
    } 
 
    sb.append("}="); //$NON-NLS-1$ 
    sb.append(properties); 
 
    return (sb.toString()); 
  } 
} 
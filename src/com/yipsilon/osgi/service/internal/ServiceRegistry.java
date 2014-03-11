package com.yipsilon.osgi.service.internal; 
 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
 
import com.yipsilon.osgi.service.IServiceFilter; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
class ServiceRegistry { 
 
  /** 
   * Published services by class name. Key is a String class name; Value is a ArrayList of IServiceRegistrations 
   */ 
  private HashMap<String, List<IServiceRegistration>> publishedServicesByClass; 
 
  /** 
   * All published services. Value is IServiceRegistrations 
   */ 
  private ArrayList<IServiceRegistration> allPublishedServices; 
 
  /** 
   * Published services by IBundleContext. Key is a IBundleContext; Value is a ArrayList of IServiceRegistrations 
   */ 
  private HashMap<ServiceContext, List<IServiceRegistration>> publishedServicesByContext; 
 
  public ServiceRegistry() { 
    publishedServicesByClass = new HashMap<String, List<IServiceRegistration>>(50); 
    publishedServicesByContext = new HashMap<ServiceContext, List<IServiceRegistration>>(50); 
    allPublishedServices = new ArrayList<IServiceRegistration>(50); 
  } 
 
  /** 
   * Publishes a service to this IServiceRegistry. 
   *  
   * @param context 
   *            the IBundleContext that registered the service. 
   * @param serviceReg 
   *            the IServiceRegistration to register. 
   */ 
  public void publishService(ServiceContext context, IServiceRegistration serviceReg) { 
 
    // Add the IServiceRegistration to the list of IServices published by IBundleContext. 
    List<IServiceRegistration> contextServices = publishedServicesByContext.get(context); 
    if (contextServices == null) { 
      contextServices = new ArrayList<IServiceRegistration>(10); 
      publishedServicesByContext.put(context, contextServices); 
    } 
    contextServices.add(serviceReg); 
 
    // Add the IServiceRegistration to the list of IServices published by Class Name. 
    String[] clazzes = ((IServiceRegistration) serviceReg).getClasses(); 
 
    for (String clazz : clazzes) { 
 
      List<IServiceRegistration> services = publishedServicesByClass.get(clazz); 
 
      if (services == null) { 
        services = new ArrayList<IServiceRegistration>(10); 
        publishedServicesByClass.put(clazz, services); 
      } 
 
      services.add(serviceReg); 
    } 
 
    // Add the IServiceRegistration to the list of all published IServices. 
    allPublishedServices.add(serviceReg); 
  } 
 
  /** 
   * Unpublishes a service from this IServiceRegistry 
   *  
   * @param context 
   *            the IBundleContext that registered the service. 
   * @param serviceReg 
   *            the IServiceRegistration to unpublish. 
   */ 
  public void unpublishService(ServiceContext context, IServiceRegistration serviceReg) { 
 
    // Remove the IServiceRegistration from the list of IServices published by IBundleContext. 
    List<IServiceRegistration> contextIServices = publishedServicesByContext.get(context); 
    if (contextIServices != null) { 
      contextIServices.remove(serviceReg); 
    } 
 
    // Remove the IServiceRegistration from the list of IServices published by Class Name. 
    String[] clazzes = ((IServiceRegistration) serviceReg).getClasses(); 
    for (String clazz : clazzes) { 
      List<IServiceRegistration> services = publishedServicesByClass.get(clazz); 
      services.remove(serviceReg); 
    } 
 
    // Remove the IServiceRegistration from the list of all published IServices. 
    allPublishedServices.remove(serviceReg); 
 
  } 
 
  /** 
   * Unpublishes all services from this IServiceRegistry that the specified IBundleContext registered. 
   *  
   * @param context 
   *            the IBundleContext to unpublish all services for. 
   */ 
  public void unpublishServices(ServiceContext context) { 
    // Get all the IServices published by the IBundleContext. 
    List<IServiceRegistration> serviceRegs = publishedServicesByContext.get(context); 
    if (serviceRegs != null) { 
      // Remove this list for the IBundleContext 
      publishedServicesByContext.remove(context); 
      int size = serviceRegs.size(); 
      for (int i = 0; i < size; i++) { 
        IServiceRegistration serviceReg = (IServiceRegistration) serviceRegs.get(i); 
        // Remove each service from the list of all published IServices 
        allPublishedServices.remove(serviceReg); 
 
        // Remove each service from the list of IServices published by Class Name. 
        String[] clazzes = serviceReg.getClasses(); 
        for (String clazz : clazzes) { 
          List<IServiceRegistration> services = publishedServicesByClass.get(clazz); 
          services.remove(serviceReg); 
        } 
      } 
    } 
  } 
 
  /** 
   * Performs a lookup for IServiceReferences that are bound to this IServiceRegistry. If both clazz and filter are null 
   * then all bound IServiceReferences are returned. 
   *  
   * @param clazz 
   *            A fully qualified class name. All IServiceReferences that reference an object that implement this class 
   *            are returned. May be null. 
   * @param filter 
   *            Used to match against published Services. All IServiceReferences that match the filter are returned. If 
   *            a clazz is specified then all IServiceReferences that match the clazz and the filter parameter are 
   *            returned. May be null. 
   * @return An array of all matching IServiceReferences or null if none exist. 
   */ 
  public IServiceReference[] lookupServiceReferences(String clazz, IServiceFilter filter) { 
    int size; 
    List<IServiceReference> references; 
    List<IServiceRegistration> serviceRegs; 
    if (clazz == null) /* all services */ 
      serviceRegs = allPublishedServices; 
    else 
      /* services registered under the class name */ 
      serviceRegs = publishedServicesByClass.get(clazz); 
 
    if (serviceRegs == null) 
      return InternalConstants.EMPTY_REFERENCE_ARRAY; 
 
    size = serviceRegs.size(); 
 
    if (size == 0) 
      return InternalConstants.EMPTY_REFERENCE_ARRAY; 
 
    references = new ArrayList<IServiceReference>(size); 
    for (int i = 0; i < size; i++) { 
      IServiceRegistration registration = (IServiceRegistration) serviceRegs.get(i); 
 
      IServiceReference reference = registration.getReference(); 
      if ((filter == null) || filter.match(reference)) { 
        references.add(reference); 
      } 
    } 
 
    if (references.size() == 0) { 
      return InternalConstants.EMPTY_REFERENCE_ARRAY; 
    } 
 
    return (IServiceReference[]) references.toArray(new IServiceReference[references.size()]); 
 
  } 
 
  /** 
   * Performs a lookup for IServiceReferences that are bound to this IServiceRegistry using the specified 
   * IBundleContext. 
   *  
   * @param context 
   *            The IBundleContext to lookup the ServiceReferences on. 
   * @return An array of all matching IServiceReferences or null if none exist. 
   */ 
  public IServiceReference[] lookupServiceReferences(ServiceContext context) { 
    int size; 
    List<IServiceReference> references; 
    List<IServiceRegistration> serviceRegs = publishedServicesByContext.get(context); 
 
    if (serviceRegs == null) { 
      return InternalConstants.EMPTY_REFERENCE_ARRAY; 
    } 
 
    size = serviceRegs.size(); 
 
    if (size == 0) { 
      return InternalConstants.EMPTY_REFERENCE_ARRAY; 
    } 
 
    references = new ArrayList<IServiceReference>(size); 
    for (int i = 0; i < size; i++) { 
      IServiceRegistration registration = (IServiceRegistration) serviceRegs.get(i); 
 
      IServiceReference reference = registration.getReference(); 
      references.add(reference); 
    } 
 
    if (references.size() == 0) { 
      return InternalConstants.EMPTY_REFERENCE_ARRAY; 
    } 
 
    return (IServiceReference[]) references.toArray(new IServiceReference[references.size()]); 
  } 
} 
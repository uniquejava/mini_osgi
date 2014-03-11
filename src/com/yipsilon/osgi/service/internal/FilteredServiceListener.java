package com.yipsilon.osgi.service.internal; 
 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IAllServiceListener; 
import com.yipsilon.osgi.service.IServiceEvent; 
import com.yipsilon.osgi.service.IServiceListener; 
import com.yipsilon.osgi.service.IServiceReference; 
 
class FilteredServiceListener implements IServiceListener { 
 
  /** Filter for listener. */ 
  private final ServiceFilter filter; 
 
  /** Real listener. */ 
  private final IServiceListener listener; 
 
  // The bundle context 
  private final ServiceContext context; 
 
  // is this an AllIServiceListener 
  private final boolean allservices; 
 
  // an objectClass required by the filter 
  private final String objectClass; 
 
  /** 
   * Constructor. 
   *  
   * @param filterstring 
   *            filter for this listener. 
   * @param listener 
   *            real listener. 
   * @exception InvalidSyntaxException 
   *                if the filter is invalid. 
   */ 
  public FilteredServiceListener(String filterstring, IServiceListener listener, ServiceContext context) throws InvalidSyntaxException { 
    if (filterstring == null) { 
      this.filter = null; 
      this.objectClass = null; 
    } else { 
      ServiceFilter filter = new ServiceFilter(filterstring); 
      String clazz = filter.getRequiredObjectClass(); 
      if (clazz == null) { 
        this.objectClass = null; 
        this.filter = filter; 
      } else { 
        this.objectClass = clazz.intern(); /* intern the name for future identity comparison */ 
        String objectClassFilter = ServiceFilter.getObjectClassFilterString(this.objectClass); 
        this.filter = (objectClassFilter.equals(filterstring)) ? null : filter; 
      } 
    } 
    this.listener = listener; 
    this.context = context; 
    this.allservices = (listener instanceof IAllServiceListener); 
  } 
 
  /** 
   * Receive notification that a service has had a change occur in it's lifecycle. 
   *  
   * @param event 
   *            The ServiceEvent. 
   */ 
  public void serviceChanged(IServiceEvent event) { 
    IServiceReference reference = event.getServiceReference(); 
 
    // first check if we can short circuit the filter match if the required objectClass does not match the event 
    objectClassCheck: if (objectClass != null) { 
      String[] classes = reference.getRegistration().getClasses(); 
      int size = classes.length; 
      for (int i = 0; i < size; i++) { 
        if (classes[i] == objectClass) // objectClass strings have previously been interned for identity comparison 
          break objectClassCheck; 
      } 
      return; // no class in this event matches a required part of the filter; we do not need to deliver this event 
    } 
 
    if ((filter == null || filter.match(reference)) && (allservices || context.isAssignableTo(reference))) { 
      listener.serviceChanged(event); 
    } 
  } 
 
  /** 
   * Get the filter string used by this Filtered listener. 
   *  
   * @return The filter string used by this listener. 
   */ 
  public String toString() { 
    return filter == null ? listener.toString() : filter.toString(); 
  } 
 
} 
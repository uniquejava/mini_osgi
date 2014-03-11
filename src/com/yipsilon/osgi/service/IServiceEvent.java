package com.yipsilon.osgi.service; 
 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.service.internal.IServiceContext; 
 
/** 
 * An event from the Framework describing a service lifecycle change. 
 * <p> 
 * <code>IServiceEvent</code> objects are delivered to a <code>IServiceListener</code> objects when a change occurs 
 * in this service's lifecycle. A type code is used to identify the event type for future extendability. 
 * <p> 
 * OSGi Alliance reserves the right to extend the set of types. 
 *  
 * @version $Revision: 1.14 $ 
 */ 
public interface IServiceEvent { 
 
  enum TYPE { 
    /** 
     * This service has been registered. 
     * <p> 
     * This event is synchronously delivered <strong>after</strong> the service has been registered with the Framework. 
     * <p> 
     * The value of <code>REGISTERED</code> is 0x00000001. 
     *  
     * @see IBundleContext#registerService(String[],Object,java.util.Map) 
     */ 
    REGISTERED(0x00000001), 
 
    /** 
     * The properties of a registered service have been modified. 
     * <p> 
     * This event is synchronously delivered <strong>after</strong> the service properties have been modified. 
     * <p> 
     * The value of <code>MODIFIED</code> is 0x00000002. 
     *  
     * @see IServiceRegistration#setProperties(java.util.Map) 
     */ 
    MODIFIED(0x00000002), 
 
    /** 
     * This service is in the process of being unregistered. 
     * <p> 
     * This event is synchronously delivered <strong>before</strong> the service has completed unregistering. 
     * <p> 
     * If a bundle is using a service that is <code>UNREGISTERING</code>, the bundle should release its use of the 
     * service when it receives this event. If the bundle does not release its use of the service when it receives this 
     * event, the Framework will automatically release the bundle's use of the service while completing the service 
     * unregistration operation. 
     * <p> 
     * The value of UNREGISTERING is 0x00000004. 
     *  
     * @see IServiceRegistration#unregister() 
     * @see IServiceContext#ungetService(IServiceReference) 
     */ 
    UNREGISTERING(0x00000004); 
 
    private int id; 
 
    TYPE(int id) { 
      this.id = id; 
    } 
 
    public int value() { 
      return id; 
    } 
  } 
 
  /** 
   * Returns a reference to the service that had a change occur in its lifecycle. 
   * <p> 
   * This reference is the source of the event. 
   *  
   * @return Reference to the service that had a lifecycle change. 
   */ 
  public IServiceReference getServiceReference(); 
 
  /** 
   * Returns the type of event. The event type values are: 
   * <ul> 
   * <li>{@link TYPE#REGISTERED} 
   * <li>{@link TYPE#MODIFIED} 
   * <li>{@link TYPE#UNREGISTERING} 
   * </ul> 
   *  
   * @return Type of service lifecycle change. 
   */ 
 
  public TYPE getType(); 
} 
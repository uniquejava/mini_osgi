package com.yipsilon.osgi; 
 
/** 
 * A general event from the Framework. 
 * <p> 
 * <code>IFrameworkEvent</code> is the event class used when notifying listeners of general events occuring within the 
 * OSGI environment. A type code is used to identify the event type for future extendability. 
 * <p> 
 */ 
public interface IFrameworkEvent { 
 
  public enum TYPE { 
 
    /** 
     * The Framework has started. 
     * <p> 
     * This event is fired when the Framework has started after all installed bundles that are marked to be started have 
     * been started and the Framework has reached the intitial start level. 
     * <p> 
     * The value of <code>STARTED</code> is 0x00000001. 
     */ 
    STARTED(0x00000001), 
 
    /** 
     * An error has occurred. 
     * <p> 
     * There was an error associated with a bundle. 
     * <p> 
     * The value of <code>ERROR</code> is 0x00000002. 
     */ 
    ERROR(0x00000002), 
 
    /** 
     * A StartLevel.setStartLevel operation has completed. 
     * <p> 
     * This event is fired when the Framework has completed changing the active start level initiated by a call to the 
     * StartLevel.setStartLevel method. 
     * <p> 
     * The value of <code>STARTLEVEL_CHANGED</code> is 0x00000008. 
     */ 
    STARTLEVEL_CHANGED(0x00000008), 
 
    /** 
     * A warning has occurred. 
     * <p> 
     * There was a warning associated with a bundle. 
     * <p> 
     * The value of <code>WARNING</code> is 0x00000010. 
     */ 
    WARNING(0x00000010), 
 
    /** 
     * An informational event has occurred. 
     * <p> 
     * There was an informational event associated with a bundle. 
     * <p> 
     * The value of <code>INFO</code> is 0x00000020. 
     */ 
    INFO(0x00000020), 
 
    /** 
     * An debug event has occurred. 
     * <p> 
     * There was an debug event associated with a bundle or framework. 
     * <p> 
     * The value of <code>DEBUG</code> is 0x00000030. 
     */ 
    DEBUG(0x00000030), 
 
    /** 
     * A framework stop event has occurred. 
     * <p> 
     * This event is fired when the Framework has shutdown after all bundles uninstalled. 
     * <p> 
     * The value of <code>STOPPED</code> is 0xFFFFFFFF. 
     */ 
    STOPPED(0xFFFFFFFF); 
 
    private int id; 
 
    TYPE(int id) { 
      this.id = id; 
    } 
 
    public int value() { 
      return id; 
    } 
  } 
 
  /** 
   * Returns the type of framework event. 
   * <p> 
   * The type values are: 
   * <ul> 
   * <li>{@link TYPE#STARTED} 
   * <li>{@link TYPE#ERROR} 
   * <li>{@link TYPE#WARNING} 
   * <li>{@link TYPE#INFO} 
   * </ul> 
   *  
   * @return The type of state change. 
   */ 
  public TYPE getType(); 
 
  /** 
   * Returns the message related to this event. 
   *  
   * @return The related message or <code>null</code> if none. 
   */ 
  public String getMessage(); 
 
  /** 
   * Returns the exception related to this event. 
   *  
   * @return The related exception or <code>null</code> if none. 
   */ 
  public Throwable getThrowable(); 
 
  /** 
   * Returns the bundle associated with the event. This bundle is also the source of the event. 
   *  
   * @return The bundle associated with the event. 
   */ 
  public IBundle getBundle(); 
} 
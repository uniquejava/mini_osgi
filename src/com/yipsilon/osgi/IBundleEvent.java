package com.yipsilon.osgi; 
 
/** 
 * An event from the Framework describing a bundle lifecycle change. 
 * <p> 
 * <code>IBundleEvent</code> objects are delivered to <code>IBundleListener</code> objects when a change occurs in a 
 * bundle's lifecycle. A type code is used to identify the event type for future extendability. 
 * <p> 
 */ 
public interface IBundleEvent { 
 
  public enum TYPE { 
    /** 
     * The bundle has been installed. 
     * <p> 
     * The value of <code>INSTALLED</code> is 0x00000001. 
     *  
     * @see IBundleContext#installBundle(String) 
     */ 
    INSTALLED(0x00000001), 
 
    /** 
     * The bundle has been started. 
     * <p> 
     * The value of <code>STARTED</code> is 0x00000002. 
     *  
     * @see IBundle#start 
     */ 
    STARTED(0x00000002), 
 
    /** 
     * The bundle has been stopped. 
     * <p> 
     * The value of <code>STOPPED</code> is 0x00000004. 
     *  
     * @see IBundle#stop 
     */ 
    STOPPED(0x00000004), 
 
    /** 
     * The bundle has been updated. 
     * <p> 
     * The value of <code>UPDATED</code> is 0x00000008. 
     *  
     * @see IBundle#update() 
     */ 
    UPDATED(0x00000008), 
 
    /** 
     * The bundle has been uninstalled. 
     * <p> 
     * The value of <code>UNINSTALLED</code> is 0x00000010. 
     *  
     * @see IBundle#uninstall 
     */ 
    UNINSTALLED(0x00000010), 
 
    /** 
     * The bundle has been resolved. 
     * <p> 
     * The value of <code>RESOLVED</code> is 0x00000020. 
     *  
     * @see IBundle.STATE#RESOLVED 
     */ 
    RESOLVED(0x00000020), 
 
    /** 
     * The bundle has been unresolved. 
     * <p> 
     * The value of <code>UNRESOLVED</code> is 0x00000040. 
     *  
     * @see IBundle.STATE#INSTALLED 
     */ 
    UNRESOLVED(0x00000040), 
 
    /** 
     * The bundle is about to start. 
     * <p> 
     * The value of <code>STARTING</code> is 0x00000080. 
     *  
     * @see IBundle#start() 
     */ 
    STARTING(0x00000080), 
 
    /** 
     * The bundle is about to stop. 
     * <p> 
     * The value of <code>STOPPING</code> is 0x00000100. 
     *  
     * @see IBundle#stop() 
     */ 
    STOPPING(0x00000100); 
 
    private int id; 
 
    TYPE(int id) { 
      this.id = id; 
    } 
 
    public int value() { 
      return id; 
    } 
  } 
 
  /** 
   * Returns the type of lifecyle event. The type values are: 
   * <ul> 
   * <li>{@link TYPE#INSTALLED} 
   * <li>{@link TYPE#RESOLVED} 
   * <li>{@link TYPE#STARTING} 
   * <li>{@link TYPE#STARTED} 
   * <li>{@link TYPE#STOPPING} 
   * <li>{@link TYPE#STOPPED} 
   * <li>{@link TYPE#UPDATED} 
   * <li>{@link TYPE#UNRESOLVED} 
   * <li>{@link TYPE#UNINSTALLED} 
   * </ul> 
   *  
   * @return The type of lifecycle event. 
   */ 
  public TYPE getType(); 
 
  /** 
   * Returns the bundle which had a lifecycle change. This bundle is the source of the event. 
   *  
   * @return The bundle that had a change occur in its lifecycle. 
   */ 
  public IBundle getBundle(); 
} 
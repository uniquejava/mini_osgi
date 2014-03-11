package com.yipsilon.osgi.services; 
 
/** 
 * Auto update bundle service. 
 * <p> 
 * This service can update bundle automatically via local or update location which defined as "Bundle-UpdateLocation" in 
 * bundle's manifest. 
 * </p> 
 *  
 * @author yipsilon 
 * @since 1.0 
 */ 
public interface IAutoUpdate { 
 
  /** 
   * Upate type. 
   *  
   * @author yipsilon 
   * @since 1.0 
   */ 
  public enum TYPE { 
    LOCAL, REMOTE; 
  }; 
 
  /** 
   * Enable or disable update. 
   *  
   * @param enabled 
   *            true if enable update. 
   */ 
  public void setEnabled(boolean enabled); 
 
  /** 
   * Check enable update or not. 
   *  
   * @return true if enable update. 
   */ 
  public boolean getEnabled(); 
 
  /** 
   * Get update type. 
   *  
   * @return update type. 
   * @see TYPE#LOCAL 
   * @see TYPE#REMOTE 
   */ 
  public TYPE getType(); 
 
  /** 
   * Set update type. 
   * <p> 
   * If the type is <code>LOCAL</code>, service will monitor the local bundle file, if the last modified is changed, 
   * then update bundle. 
   * </p> 
   * <p> 
   * If the type is <code>REMOTE</code>, service will check the remove file defined in MANIFEST.MF, if the last 
   * modified is changed, then update bundle. 
   * </p> 
   *  
   * @param type 
   *            update type. 
   */ 
  public void setType(TYPE type); 
} 
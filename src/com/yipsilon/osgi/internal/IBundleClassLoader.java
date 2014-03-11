package com.yipsilon.osgi.internal; 
 
interface IBundleClassLoader extends IClassLoader { 
 
  /** 
   * Get current bundle descriptor. 
   *  
   * @return current bundle descriptor. 
   */ 
  public abstract IBundleDescriptor getDescriptor(); 
 
}
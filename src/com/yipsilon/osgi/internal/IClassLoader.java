package com.yipsilon.osgi.internal; 
 
interface IClassLoader { 
 
  /** 
   * Check the package is exposed or not. 
   *  
   * @param packageName 
   *            package name. 
   * @return true if the package is exposed. 
   */ 
  public boolean isPackageExposed(String packageName); 
 
} 
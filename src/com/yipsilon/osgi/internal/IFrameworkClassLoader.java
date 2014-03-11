package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.IFramework; 
import com.yipsilon.osgi.services.ILibrary; 
 
interface IFrameworkClassLoader extends IClassLoader, ILibrary { 
 
  /** 
   * Get current framework instance. 
   *  
   * @return current framework. 
   */ 
  public abstract IFramework getFramework(); 
}
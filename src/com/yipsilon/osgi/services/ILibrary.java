package com.yipsilon.osgi.services; 
 
/** 
 * Class and library path service. 
 * <p> 
 * This service provides the custom class path and native path features at bundle level. Bundles can add/remove 
 * framework class path and native path manually. 
 * </p> 
 *  
 * @author yipsilon 
 * @since 1.0 
 */ 
public interface ILibrary { 
 
  /** 
   * Add a class path to class loader. 
   *  
   * @param classpath 
   *            class path. Either a directory or jar file. 
   */ 
  public abstract void addClassPath(String classpath); 
 
  /** 
   * Add a native library path. 
   *  
   * @param librarypath 
   *            library path. Must be a directory. 
   */ 
  public abstract void addLibraryPath(String librarypath); 
 
  /** 
   * Remove the exists class path. 
   *  
   * @param classpath 
   *            the exists class path. 
   */ 
  public abstract void removeClassPath(String classpath); 
 
  /** 
   * Remove the exists library path. 
   *  
   * @param librarypath 
   *            the exists library path. 
   */ 
  public abstract void removeLibraryPath(String librarypath); 
} 
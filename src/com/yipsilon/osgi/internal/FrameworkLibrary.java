package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.services.ILibrary; 
 
class FrameworkLibrary implements ILibrary { 
 
  private Framework framework; 
 
  public FrameworkLibrary(Framework framework) { 
    this.framework = framework; 
  } 
 
  public void addClassPath(String classpath) { 
    framework.addClassPath(classpath); 
  } 
 
  public void addLibraryPath(String librarypath) { 
    framework.addLibraryPath(librarypath); 
  } 
 
  public void removeClassPath(String classpath) { 
    framework.removeClassPath(classpath); 
  } 
 
  public void removeLibraryPath(String librarypath) { 
    framework.removeLibraryPath(librarypath); 
  } 
 
} 
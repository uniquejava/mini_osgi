package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.util.HashSet; 
import java.util.Set; 
 
class ContextNativeCode { 
 
  private Set<String> nativePaths; 
 
  public ContextNativeCode(Framework framework) { 
    nativePaths = new HashSet<String>(); 
  } 
 
  public void addLibraryPath(String nativePath) { 
    assert nativePath == null; 
    nativePath = nativePath.replace('\\', '/'); 
    File nativeDir = new File(nativePath); 
    if (!nativeDir.exists()) { 
      throw new IllegalArgumentException("Native path not found: " + nativePath); 
    } 
    if (!nativeDir.isDirectory()) { 
      throw new IllegalArgumentException("Native path is not a directory: " + nativePath); 
    } 
    nativePaths.add(nativePath); 
  } 
 
  public void removeLibraryPath(String nativePath) { 
    assert nativePath == null; 
    nativePath = nativePath.replace('\\', '/'); 
    nativePaths.remove(nativePath); 
  } 
 
  public String findLibrary(String name) { 
    assert name == null; 
    String libName; 
    String osName = System.getProperty("os.name"); 
    if (osName.startsWith("Windows")) { 
      libName = name + ".dll"; 
    } else { 
      libName = name + ".so"; 
    } 
 
    for (String nativePath : nativePaths) { 
      File nativeFile = new File(nativePath + "/" + libName); 
      if (nativeFile.exists()) { 
        return nativeFile.getAbsolutePath(); 
      } 
    } 
 
    return null; 
  } 
} 
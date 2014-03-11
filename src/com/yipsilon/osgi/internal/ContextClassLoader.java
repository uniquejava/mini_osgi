package com.yipsilon.osgi.internal; 
 
import java.io.ByteArrayOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.URL; 
import java.util.Enumeration; 
 
import com.yipsilon.osgi.IFrameworkEvent; 
import com.yipsilon.osgi.services.ILibrary; 
 
class ContextClassLoader extends ClassLoader implements IClassLoader, ILibrary { 
 
  private ContextClassPath classPath; 
 
  private ContextNativeCode nativePath; 
 
  private Framework framework; 
 
  public ContextClassLoader(IFrameworkClassLoader fcl) { 
    super(Thread.currentThread().getContextClassLoader() == null ? ClassLoader.getSystemClassLoader() : Thread.currentThread().getContextClassLoader()); 
    this.framework = (Framework) fcl.getFramework(); 
    this.classPath = new ContextClassPath(this.framework); 
    this.nativePath = new ContextNativeCode(this.framework); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IContextClassLoader#addClassPath(java.lang.String) 
   */ 
  public void addClassPath(String classpath) { 
    classPath.addClasspath(classpath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IContextClassLoader#addLibraryPath(java.lang.String) 
   */ 
  public void addLibraryPath(String librarypath) { 
    nativePath.addLibraryPath(librarypath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IContextClassLoader#removeClassPath(java.lang.String) 
   */ 
  public void removeClassPath(String classpath) { 
    classPath.removeClasspath(classpath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IContextClassLoader#removeLibraryPath(java.lang.String) 
   */ 
  public void removeLibraryPath(String librarypath) { 
    nativePath.removeLibraryPath(librarypath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IClassLoader#isPackageExposed(java.lang.String) 
   */ 
  public boolean isPackageExposed(String packageName) { 
    return classPath.isPackageExposed(packageName); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findClass(java.lang.String) 
   */ 
  @Override 
  protected Class<?> findClass(String name) { 
    String fullName = name.replace('.', '/') + ".class"; 
    URL classURL = findResource(fullName); 
    if (classURL != null) { 
      try { 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096); 
        InputStream inputStream = classURL.openStream(); 
        byte[] cache = new byte[4096]; 
        for (int offset = inputStream.read(cache); offset != -1; offset = inputStream.read(cache)) { 
          outputStream.write(cache, 0, offset); 
        } 
        inputStream.close(); 
        outputStream.close(); 
        cache = null; 
        byte[] classData = outputStream.toByteArray(); 
        return defineClass(name, classData, 0, classData.length); 
      } catch (IOException e) { 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, e); 
        return null; 
      } 
    } 
    return null; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findLibrary(java.lang.String) 
   */ 
  @Override 
  protected String findLibrary(String libname) { 
    return nativePath.findLibrary(libname); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findResource(java.lang.String) 
   */ 
  @Override 
  protected URL findResource(String name) { 
    return classPath.findResource(name); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findResources(java.lang.String) 
   */ 
  @Override 
  protected Enumeration<URL> findResources(String name) throws IOException { 
    return classPath.findResources(name); 
  } 
 
} 
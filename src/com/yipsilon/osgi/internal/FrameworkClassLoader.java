package com.yipsilon.osgi.internal; 
 
import java.io.IOException; 
import java.net.URL; 
import java.util.Enumeration; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.Vector; 
 
import com.yipsilon.osgi.BundleException; 
 
class FrameworkClassLoader extends ClassLoader implements IFrameworkClassLoader { 
 
  private Map<IBundleDescriptor, BundleClassLoader> bcls; 
 
  private ContextClassLoader ccl; 
 
  private Framework framework; 
 
  public FrameworkClassLoader(Framework framework, ClassLoader classLoader) { 
    super(classLoader); 
    this.framework = framework; 
    this.bcls = new HashMap<IBundleDescriptor, BundleClassLoader>(); 
    this.ccl = new ContextClassLoader(this); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IFrameworkClassLoader#getFramework() 
   */ 
  public Framework getFramework() { 
    return framework; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IContextClassLoader#addClassPath(java.lang.String) 
   */ 
  public void addClassPath(String classpath) { 
    ccl.addClassPath(classpath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IContextClassLoader#addLibraryPath(java.lang.String) 
   */ 
  public void addLibraryPath(String librarypath) { 
    ccl.addLibraryPath(librarypath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IContextClassLoader#removeClassPath(java.lang.String) 
   */ 
  public void removeClassPath(String classpath) { 
    ccl.removeClassPath(classpath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IContextClassLoader#removeLibraryPath(java.lang.String) 
   */ 
  public void removeLibraryPath(String librarypath) { 
    ccl.removeLibraryPath(librarypath); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IClassLoader#isPackageExposed(java.lang.String) 
   */ 
  public boolean isPackageExposed(String packageName) { 
 
    if (ccl.isPackageExposed(packageName)) { 
      return true; 
    } 
 
    for (IBundleClassLoader bcl : bcls.values()) { 
      if (bcl.isPackageExposed(packageName)) { 
        return true; 
      } 
    } 
 
    return false; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findClass(java.lang.String) 
   */ 
  @Override 
  protected Class<?> findClass(String name) throws ClassNotFoundException { 
 
    String packageName = name.lastIndexOf('.') > -1 ? name.substring(0, name.lastIndexOf('.')) : ""; 
 
    if (ccl.isPackageExposed(packageName)) { 
      Class<?> c = ccl.loadClass(name); 
      if (c != null) { 
        return c; 
      } 
    } 
 
    for (BundleClassLoader bcl : bcls.values()) { 
      if (bcl.isPackageExposed(packageName)) { 
        Class<?> c = bcl.findClass(name); 
        if (c != null) { 
          return c; 
        } 
      } 
    } 
 
    throw new ClassNotFoundException(name); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findLibrary(java.lang.String) 
   */ 
  @Override 
  protected String findLibrary(String libname) { 
    { 
      String c = ccl.findLibrary(libname); 
      if (c != null) { 
        return c; 
      } 
    } 
 
    for (BundleClassLoader bcl : bcls.values()) { 
      String c = bcl.findLibrary(libname); 
      if (c != null) { 
        return c; 
      } 
    } 
 
    return null; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findResource(java.lang.String) 
   */ 
  @Override 
  protected URL findResource(String name) { 
    { 
      URL c = ccl.findResource(name); 
      if (c != null) { 
        return c; 
      } 
    } 
 
    for (BundleClassLoader bcl : bcls.values()) { 
      URL c = bcl.findResource(name); 
      if (c != null) { 
        return c; 
      } 
    } 
 
    return null; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.ClassLoader#findResources(java.lang.String) 
   */ 
  @Override 
  protected Enumeration<URL> findResources(String name) throws IOException { 
    Vector<URL> urlEnum = new Vector<URL>(); 
 
    { 
      Enumeration<URL> c = ccl.findResources(name); 
      if (c != null) { 
        while (c.hasMoreElements()) { 
          urlEnum.add(c.nextElement()); 
        } 
      } 
    } 
 
    for (BundleClassLoader bcl : bcls.values()) { 
      Enumeration<URL> c = bcl.findResources(name); 
      if (c != null) { 
        while (c.hasMoreElements()) { 
          urlEnum.add(c.nextElement()); 
        } 
      } 
    } 
 
    if (urlEnum.isEmpty()) { 
      return null; 
    } else { 
      return urlEnum.elements(); 
    } 
  } 
 
  BundleClassLoader createBundleClassLoader(BundleDescriptor descriptor) throws BundleException { 
    BundleClassLoader bcl = new BundleClassLoader(this, descriptor); 
    bcls.put(descriptor, bcl); 
    return bcl; 
  } 
 
  void removeBundleClassLoader(BundleDescriptor descriptor) { 
    bcls.remove(descriptor); 
  } 
 
  BundleClassLoader getBundleClassLoader(BundleDescriptor descriptor) { 
    return bcls.get(descriptor); 
  } 
 
  ContextClassLoader getContextClassLoader() { 
    return ccl; 
  } 
} 
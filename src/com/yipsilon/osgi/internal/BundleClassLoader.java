package com.yipsilon.osgi.internal; 
 
import java.io.ByteArrayOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.URL; 
import java.util.Enumeration; 
import java.util.HashSet; 
import java.util.Set; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IFrameworkEvent; 
 
class BundleClassLoader extends ClassLoader implements IBundleClassLoader { 
 
  private BundleClassPath classPath; 
 
  private BundleNativeCode nativePath; 
 
  private Set<String> packages; 
 
  private Framework framework; 
 
  private BundleDescriptor descriptor; 
 
  public BundleClassLoader(FrameworkClassLoader parent, BundleDescriptor descriptor) throws BundleException { 
    super(parent); 
 
    this.framework = parent.getFramework(); 
    this.descriptor = descriptor; 
    this.classPath = new BundleClassPath(framework, descriptor); 
    this.nativePath = new BundleNativeCode(framework, descriptor); 
    this.packages = new HashSet<String>(); 
 
    for (String pkg : descriptor.getExportedPackages()) { 
      this.packages.add(pkg); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IBundleClassLoader#getDescriptor() 
   */ 
  public BundleDescriptor getDescriptor() { 
    return descriptor; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.internal.IClassLoader#isPackageExposed(java.lang.String) 
   */ 
  public boolean isPackageExposed(String packageName) { 
    return packages.contains(packageName); 
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
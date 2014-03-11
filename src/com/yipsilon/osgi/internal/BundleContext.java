package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.util.Map; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.IBundleFilter; 
import com.yipsilon.osgi.IBundleListener; 
import com.yipsilon.osgi.IFrameworkListener; 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IServiceListener; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
import com.yipsilon.osgi.service.internal.IServiceContext; 
 
class BundleContext implements IBundleContext { 
 
  private Framework framework; 
 
  private Bundle bundle; 
 
  private IServiceContext context; 
 
  public BundleContext(Framework framework, Bundle bundle) { 
    this.framework = framework; 
    this.bundle = bundle; 
    this.context = framework.createServiceContext(this); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#addServiceListener(com.yipsilon.osgi.service.IServiceListener, 
   *      java.lang.String) 
   */ 
  public void addServiceListener(IServiceListener listener, String filter) throws InvalidSyntaxException { 
    context.addServiceListener(listener, filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#addServiceListener(com.yipsilon.osgi.service.IServiceListener) 
   */ 
  public void addServiceListener(IServiceListener listener) { 
    context.addServiceListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#createFilter(java.lang.String) 
   */ 
  public IBundleFilter createFilter(String filter) throws InvalidSyntaxException { 
    return context.createFilter(filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getAllServiceReferences(java.lang.String, java.lang.String) 
   */ 
  public IServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException { 
    return context.getAllServiceReferences(clazz, filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getService(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public Object getService(IServiceReference reference) { 
    return context.getService(reference); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getServiceReference(java.lang.String) 
   */ 
  public IServiceReference getServiceReference(String clazz) { 
    return context.getServiceReference(clazz); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#getServiceReferences(java.lang.String, java.lang.String) 
   */ 
  public IServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException { 
    return context.getServiceReferences(clazz, filter); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#registerService(java.lang.String, java.lang.Object, java.util.Map) 
   */ 
  public IServiceRegistration registerService(String clazz, Object service, Map<String, Object> properties) { 
    return context.registerService(clazz, service, properties); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#registerService(java.lang.String[], java.lang.Object, java.util.Map) 
   */ 
  public IServiceRegistration registerService(String[] classes, Object service, Map<String, Object> properties) { 
    return context.registerService(classes, service, properties); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#removeServiceListener(com.yipsilon.osgi.service.IServiceListener) 
   */ 
  public void removeServiceListener(IServiceListener listener) { 
    context.removeServiceListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.IServiceContext#ungetService(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public boolean ungetService(IServiceReference reference) { 
    return context.ungetService(reference); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#addBundleListener(com.yipsilon.osgi.IBundleListener) 
   */ 
  public void addBundleListener(IBundleListener listener) { 
    framework.addBundleListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#addFrameworkListener(com.yipsilon.osgi.IFrameworkListener) 
   */ 
  public void addFrameworkListener(IFrameworkListener listener) { 
    framework.addFrameworkListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getBundle() 
   */ 
  public IBundle getBundle() { 
    return bundle; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getBundle(long) 
   */ 
  public IBundle getBundle(long id) { 
    return framework.getBundle(id); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getBundle(java.lang.String) 
   */ 
  public IBundle getBundle(String symbolicName) { 
    return framework.getBundle(symbolicName); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getBundles() 
   */ 
  public IBundle[] getBundles() { 
    return framework.getBundles(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#installBundle(java.lang.String) 
   */ 
  public IBundle installBundle(String location) throws BundleException { 
    return framework.installBundle(location); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#removeBundleListener(com.yipsilon.osgi.IBundleListener) 
   */ 
  public void removeBundleListener(IBundleListener listener) { 
    framework.removeBundleListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#removeFrameworkListener(com.yipsilon.osgi.IFrameworkListener) 
   */ 
  public void removeFrameworkListener(IFrameworkListener listener) { 
    framework.removeFrameworkListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getDataFile(java.lang.String) 
   */ 
  public File getDataFile(String filename) { 
    // TODO Auto-generated method stub 
    return null; 
  } 
 
  protected Framework getFramework() { 
    return framework; 
  } 
 
  protected IServiceContext getServiceContext() { 
    return context; 
  } 
} 
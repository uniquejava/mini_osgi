package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.io.FileFilter; 
import java.util.Comparator; 
import java.util.HashSet; 
import java.util.Iterator; 
import java.util.Set; 
import java.util.TreeSet; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleContext; 
import com.yipsilon.osgi.IBundleEvent; 
import com.yipsilon.osgi.IBundleListener; 
import com.yipsilon.osgi.IFramework; 
import com.yipsilon.osgi.IFrameworkEvent; 
import com.yipsilon.osgi.IFrameworkListener; 
import com.yipsilon.osgi.IFrameworkLog; 
import com.yipsilon.osgi.ISynchronousBundleListener; 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.IBundle.STATE; 
import com.yipsilon.osgi.service.IAllServiceListener; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.internal.IServiceContext; 
import com.yipsilon.osgi.service.internal.Service; 
import com.yipsilon.osgi.services.ILibrary; 
import com.yipsilon.osgi.services.IStartLevel; 
 
public class Framework implements IFramework, IStartLevel, ILibrary { 
 
  private FrameworkClassLoader classLoader; 
 
  private long bundleSequence; 
 
  private int startLevel; 
 
  private int initialBundleStartLevel; 
 
  private boolean started; 
 
  private Service service; 
 
  private FrameworkLog log; 
 
  private Set<Bundle> bundles; 
 
  private Set<IBundleListener> bundleListeners; 
 
  private Set<IFrameworkListener> frameworkListeners; 
 
  public Framework() { 
    this(Thread.currentThread().getContextClassLoader() == null ? ClassLoader.getSystemClassLoader() : Thread.currentThread().getContextClassLoader()); 
  } 
 
  public Framework(ClassLoader parent) { 
    bundleListeners = new HashSet<IBundleListener>(); 
    frameworkListeners = new HashSet<IFrameworkListener>(); 
    bundles = new HashSet<Bundle>(); 
    startLevel = 6; 
    initialBundleStartLevel = 4; 
    classLoader = new FrameworkClassLoader(this, parent); 
    bundleSequence = 1; 
    started = false; 
    service = new Service(); 
    log = new FrameworkLog(this); 
 
    // Uninstall bundles when system exit. 
    Runtime.getRuntime().addShutdownHook(new Thread() { 
      public void run() { 
        if (started) { 
          shutdown(); 
          unisntallBundles(); 
          started = false; 
        } 
      } 
    }); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#getLog() 
   */ 
  public IFrameworkLog getLog() { 
    return log; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#addServiceListener(com.yipsilon.osgi.service.IServiceListener) 
   */ 
  public void addServiceListener(IAllServiceListener listener) { 
    service.addServiceListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#getAllServiceReferences(java.lang.String, java.lang.String) 
   */ 
  public IServiceReference[] getAllServiceReferences(String clazz, String filterstring) throws InvalidSyntaxException { 
    return service.getAllServiceReferences(clazz, filterstring); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.service.internal.IService#getService(com.yipsilon.osgi.service.IServiceReference) 
   */ 
  public Object getService(IServiceReference reference) { 
    return service.getService(reference); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#removeServiceListener(com.yipsilon.osgi.service.IServiceListener) 
   */ 
  public void removeServiceListener(IAllServiceListener listener) { 
    service.removeServiceListener(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#addBundleListener(com.yipsilon.osgi.IBundleListener) 
   */ 
  public void addBundleListener(IBundleListener listener) { 
    bundleListeners.add(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#removeBundleListener(com.yipsilon.osgi.IBundleListener) 
   */ 
  public void removeBundleListener(IBundleListener listener) { 
    bundleListeners.remove(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#addFrameworkListener(com.yipsilon.osgi.IFrameworkListener) 
   */ 
  public void addFrameworkListener(IFrameworkListener listener) { 
    frameworkListeners.add(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#removeFrameworkListener(com.yipsilon.osgi.IFrameworkListener) 
   */ 
  public void removeFrameworkListener(IFrameworkListener listener) { 
    frameworkListeners.remove(listener); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IClassPathService#addClassPath(java.lang.String) 
   */ 
  public void addClassPath(String path) { 
    classLoader.addClassPath(path); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IClassPathService#addLibraryPath(java.lang.String) 
   */ 
  public void addLibraryPath(String path) { 
    classLoader.addLibraryPath(path); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IClassPathService#removeClassPath(java.lang.String) 
   */ 
  public void removeClassPath(String path) { 
    classLoader.removeClassPath(path); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IClassPathService#removeLibraryPath(java.lang.String) 
   */ 
  public void removeLibraryPath(String path) { 
    classLoader.removeLibraryPath(path); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleStartLevel#getInitialBundleStartLevel() 
   */ 
  public int getInitialBundleStartLevel() { 
    return initialBundleStartLevel; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleStartLevel#setInitialBundleStartLevel(int) 
   */ 
  public void setInitialBundleStartLevel(int initialBundleStartLevel) { 
    assert startLevel < 0 : "Initial bundle start level is less than zero"; 
    if (this.initialBundleStartLevel != initialBundleStartLevel) { 
 
      this.initialBundleStartLevel = initialBundleStartLevel; 
 
      // The sorted bundles: start 
      Set<Bundle> startBundles = createBundleSet(1); 
 
      // The sorted bundles: stop 
      Set<Bundle> stopBundles = createBundleSet(-1); 
 
      // Sort bundles. 
      for (Bundle bundle : bundles) { 
        BundleDescriptor descriptor = bundle.getDescriptor(); 
        IBundle.STATE state = bundle.getState(); 
        if (state == IBundle.STATE.ACTIVE && (descriptor.getStartLevel() > startLevel)) { 
          stopBundles.add(bundle); 
        } else if (state == IBundle.STATE.RESOLVED && (descriptor.getStartLevel() <= startLevel)) { 
          startBundles.add(bundle); 
        } 
      } 
 
      // Stop bundles 
      stopBundleSet(stopBundles); 
 
      // Start bundles 
      startBundleSet(startBundles); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleStartLevel#getStartLevel() 
   */ 
  public int getStartLevel() { 
    return startLevel; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleStartLevel#setStartLevel(int) 
   */ 
  public void setStartLevel(int newStartLevel) { 
    assert newStartLevel < 0 : "Start level is less than zero"; 
    if (this.startLevel != newStartLevel) { 
 
      int oldStartLevel = this.startLevel; 
 
      this.startLevel = newStartLevel; 
 
      // The sorted bundles: start 
      Set<Bundle> startBundles = createBundleSet(1); 
 
      // The sorted bundles: stop 
      Set<Bundle> stopBundles = createBundleSet(-1); 
 
      // Sort bundles. 
      for (Bundle bundle : bundles) { 
        BundleDescriptor descriptor = bundle.getDescriptor(); 
        IBundle.STATE state = bundle.getState(); 
        if (oldStartLevel > newStartLevel) { 
          if (state == IBundle.STATE.ACTIVE && (descriptor.getStartLevel() > newStartLevel)) { 
            stopBundles.add(bundle); 
          } 
        } else { 
          if (state == IBundle.STATE.RESOLVED && (descriptor.getStartLevel() <= newStartLevel)) { 
            startBundles.add(bundle); 
          } 
        } 
      } 
 
      // Stop bundles 
      stopBundleSet(stopBundles); 
 
      // Start bundles 
      startBundleSet(startBundles); 
 
      // Trigger framework event 
      this.fireFrameworkEvent(IFrameworkEvent.TYPE.STARTLEVEL_CHANGED); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#getBundleStartLevel(com.yipsilon.osgi.IBundle) 
   */ 
  public int getBundleStartLevel(IBundle b) { 
    if (bundles.contains(b)) { 
      for (Bundle bundle : bundles) { 
        if (bundle.equals(b)) { 
          IBundleDescriptor descriptor = bundle.getDescriptor(); 
          return descriptor.getStartLevel(); 
        } 
      } 
    } 
    return initialBundleStartLevel; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleStartLevel#setBundleStartLevel(com.yipsilon.osgi.IBundle, int) 
   */ 
  public void setBundleStartLevel(IBundle b, int level) { 
    if (bundles.contains(b)) { 
      for (Bundle bundle : bundles) { 
        if (bundle.equals(b)) { 
 
          BundleDescriptor descriptor = bundle.getDescriptor(); 
 
          descriptor.setStartLevel(level); 
 
          // Start or stop bundle 
          if (level <= startLevel && descriptor.isAutoStart() && bundle.getState() == IBundle.STATE.RESOLVED) { 
            // Three conditions: 
            // 1. bundle start level <= system start level. 
            // 2. bundle is auto start. 
            // 3. bundle state is RESOLVED. 
            try { 
              bundle.start(); 
            } catch (BundleException e) {} 
          } else if (level > startLevel && bundle.getState() == IBundle.STATE.ACTIVE) { 
            // Two conditions: 
            // 1. bundle start level > system start level. 
            // 2. bundle state is ACTIVE. 
            try { 
              bundle.stop(); 
            } catch (BundleException e) {} 
          } 
          return; 
        } 
      } 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleStartLevel#isBundlePersistentlyStarted(com.yipsilon.osgi.IBundle) 
   */ 
  public boolean isBundlePersistentlyStarted(IBundle bundle) { 
    return bundle.getState() == IBundle.STATE.ACTIVE; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getBundle(long) 
   */ 
  public IBundle getBundle(long id) { 
    for (IBundle bundle : bundles) { 
      if (bundle.getBundleId() == id) { 
        return bundle; 
      } 
    } 
    return null; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#getBundle(java.lang.String) 
   */ 
  public IBundle getBundle(String symbolicName) { 
    for (IBundle bundle : bundles) { 
      if (bundle.getSymbolicName().equals(symbolicName)) { 
        return bundle; 
      } 
    } 
    return null; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#getBundles() 
   */ 
  public IBundle[] getBundles() { 
    return bundles.toArray(InternalConstants.EMPTY_BUNDLE_ARRAY); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleContext#installBundle(java.lang.String) 
   */ 
  public synchronized IBundle installBundle(String location) throws BundleException { 
 
    // Create a bundle instance 
    Bundle bundle = new Bundle(this, bundleSequence++, location); 
 
    // Install bundle 
    try { 
      bundle.install(true); 
    } catch (BundleException e) { 
      throw e; 
    } 
 
    // Create bundle class loader 
    classLoader.createBundleClassLoader(bundle.getDescriptor()); 
 
    // Resolve bundle. 
    try { 
      bundle.resolve(true); 
    } catch (BundleException e) { 
 
      // Remove class loader created at install() 
      classLoader.removeBundleClassLoader(bundle.getDescriptor()); 
 
      throw e; 
    } 
 
    // Put to cache 
    addBundle(bundle); 
 
    // Check auto start 
    if (started) { 
      startBundle(bundle); 
    } 
 
    return bundle; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#installBundles(java.lang.String) 
   */ 
  public synchronized IBundle[] installBundles(String location) throws BundleException { 
    File dir = new File(location); 
    if (dir.isDirectory()) { 
 
      File[] files = dir.listFiles(new FileFilter() { 
        public boolean accept(File pathname) { 
          return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar"); 
        } 
      }); 
 
      Set<Bundle> bundles = new HashSet<Bundle>(); 
 
      // Install bundles 
      for (File file : files) { 
        Bundle bundle = new Bundle(this, bundleSequence++, file.getAbsolutePath()); 
 
        // Install bundle 
        try { 
          bundle.install(true); 
        } catch (BundleException e) { 
          continue; 
        } 
 
        // Create bundle class loader 
        try { 
          classLoader.createBundleClassLoader(bundle.getDescriptor()); 
        } catch (BundleException e) { 
          // Trigger framework event 
          this.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not create bundle(ID:" + bundle.getBundleId() + ") class loader", e); 
          continue; 
        } 
 
        bundles.add(bundle); 
 
        // Put to cache 
        addBundle(bundle); 
      } 
 
      // Resolve bundles 
      for (Bundle bundle : bundles) { 
        // Resolve bundle 
        try { 
          bundle.resolve(true); 
        } catch (BundleException e) { 
          // Remove class loader created at install() 
          classLoader.removeBundleClassLoader(bundle.getDescriptor()); 
        } 
      } 
 
      // Start bundles if the "Bundle-AutoStart" attribute is true. 
      if (started) { 
        // Collect the auto-start bundles. 
        startBundles(bundles); 
      } 
 
      return bundles.toArray(InternalConstants.EMPTY_BUNDLE_ARRAY); 
 
    } else { 
      IBundle bundle = installBundle(location); 
      if (bundle != null) { 
        return new IBundle[] { bundle }; 
      } else { 
        return InternalConstants.EMPTY_BUNDLE_ARRAY; 
      } 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#shutdown() 
   */ 
  public void shutdown() { 
    stopBundles(); 
    started = false; 
    this.fireFrameworkEvent(IFrameworkEvent.TYPE.STOPPED); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFramework#startup() 
   */ 
  public void startup() { 
    startBundles(); 
 
    started = true; 
    this.fireFrameworkEvent(IFrameworkEvent.TYPE.STARTED); 
  } 
 
  protected FrameworkClassLoader getClassLoader() { 
    return classLoader; 
  } 
 
  protected IServiceContext createServiceContext(IBundleContext context) { 
    return service.createContext(context); 
  } 
 
  /** 
   * Uninstall bundle from context. 
   * <p> 
   * the operation is to delete the specified bundle from <code>bundles</code> and <code>packages</code>. 
   * </p> 
   *  
   * @param bundle 
   *          the uninstalled bundle. 
   * @param remove 
   *          remove bundle from cache or not. 
   */ 
  protected void uninstallBundle(Bundle bundle, boolean remove) { 
 
    // Remove bundle class loader. 
    classLoader.removeBundleClassLoader(bundle.getDescriptor()); 
 
    // Remove bundle services 
    service.removeContext(bundle.getBundleContext().getServiceContext()); 
 
    // Remove from the cache. 
    if (remove) { 
      delBundle(bundle); 
    } 
  } 
 
  protected void fireBundleChanged(IBundleEvent.TYPE type, IBundle bundle) { 
    IBundleEvent event = new BundleEvent(type, bundle); 
    for (IBundleListener listener : bundleListeners) { 
      switch (type) { 
        case STARTING: 
        case STOPPING: 
          if (listener instanceof ISynchronousBundleListener) { 
            listener.bundleChanged(event); 
          } 
          break; 
        default: 
          listener.bundleChanged(event); 
          break; 
      } 
    } 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, IBundle bundle, String message, Throwable throwable) { 
    IFrameworkEvent event = new FrameworkEvent(type, bundle, message, throwable); 
    for (IFrameworkListener listener : frameworkListeners) { 
      listener.frameworkEvent(event); 
    } 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, IBundle bundle, Throwable throwable) { 
    fireFrameworkEvent(type, bundle, null, throwable); 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, IBundle bundle, String message) { 
    fireFrameworkEvent(type, bundle, message, null); 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, String message, Throwable throwable) { 
    fireFrameworkEvent(type, null, message, throwable); 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, Throwable throwable) { 
    fireFrameworkEvent(type, null, null, throwable); 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, String message) { 
    fireFrameworkEvent(type, null, message, null); 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type, IBundle bundle) { 
    fireFrameworkEvent(type, bundle, null, null); 
  } 
 
  protected void fireFrameworkEvent(IFrameworkEvent.TYPE type) { 
    fireFrameworkEvent(type, null, null, null); 
  } 
 
  private void addBundle(Bundle bundle) { 
    bundles.add(bundle); 
  } 
 
  private void delBundle(Bundle bundle) { 
    // TreeSet's bug? Why the "bundles.remove(bundle)" returns incorrect value? 
    Iterator<Bundle> bundleI = bundles.iterator(); 
    while (bundleI.hasNext()) { 
      Bundle b = bundleI.next(); 
      if (b.equals(bundle)) { 
        bundleI.remove(); 
        return; 
      } 
    } 
  } 
 
  private void startBundle(Bundle bundle) { 
    BundleDescriptor descriptor = bundle.getDescriptor(); 
    if (descriptor.isAutoStart() && descriptor.getStartLevel() <= startLevel) { 
      try { 
        bundle.start(); 
      } catch (BundleException e) {} 
    } 
  } 
 
  private void startBundles(Iterable<Bundle> iterable) { 
    // Start the bundles. 
    for (Bundle bundle : iterable) { 
      IBundleDescriptor descriptor = bundle.getDescriptor(); 
      // Tree conditions: 
      // 1: the state is RESOLVED 
      // 2: the "Bundle-AutoStart" value is true 
      // 3: the bundle start level is less than system start level 
      if (bundle.getState() == IBundle.STATE.RESOLVED && descriptor.isAutoStart() && descriptor.getStartLevel() <= startLevel) { 
        try { 
          bundle.start(); 
        } catch (BundleException e) {} 
      } 
    } 
  } 
 
  private void startBundles(Bundle[] iterable) { 
 
    // The sorted bundles. 
    TreeSet<Bundle> bundles = new TreeSet<Bundle>(new Comparator<Bundle>() { 
      public int compare(Bundle o1, Bundle o2) { 
        BundleDescriptor d1 = o1.getDescriptor(); 
        BundleDescriptor d2 = o2.getDescriptor(); 
        int l1 = d1.getStartLevel(); 
        int l2 = d2.getStartLevel(); 
        if (l1 > l2) { 
          return 1; 
        } else if (l1 < l2) { 
          return -1; 
        } else { 
          return 0; 
        } 
      } 
    }); 
 
    // Sort bundles. 
    for (Bundle bundle : iterable) { 
      IBundleDescriptor descriptor = bundle.getDescriptor(); 
      // Tree conditions: 
      // 1: the state is RESOLVED 
      // 2: the "Bundle-AutoStart" value is true 
      // 3: the bundle start level is less than system start level 
      if (bundle.getState() == IBundle.STATE.RESOLVED && descriptor.isAutoStart() && descriptor.getStartLevel() <= startLevel) { 
        bundles.add(bundle); 
      } 
    } 
 
    // Start sorted bundles 
    for (Bundle bundle : bundles) { 
      try { 
        bundle.start(); 
      } catch (BundleException e) {} 
    } 
  } 
 
  private void startBundles() { 
    Bundle[] bs = bundles.toArray(InternalConstants.EMPTY_BUNDLE_ARRAY); 
    startBundles(bs); 
  } 
 
  private void stopBundles() { 
    // Stop all bundles first. 
    Iterator<Bundle> bundleI = bundles.iterator(); 
    while (bundleI.hasNext()) { 
      Bundle bundle = bundleI.next(); 
      IBundle.STATE state = bundle.getState(); 
      switch (bundle.getState()) { 
        case ACTIVE: 
          try { 
            bundle.stop(); 
          } catch (BundleException e) {} 
          break; 
        case STARTING: 
        case STOPPING: 
          long intervalTime = 200; 
          for (long time = 0; state == STATE.STARTING || state == STATE.STOPPING; time += intervalTime) { 
            try { 
              Thread.sleep(intervalTime); 
            } catch (InterruptedException e) {} 
          } 
          // Check the last state. 
          switch (state) { 
            case ACTIVE: 
              try { 
                bundle.stop(); 
              } catch (BundleException e) {} 
              break; 
            case RESOLVED: 
              return; 
          } 
      } 
    } 
  } 
 
  private void unisntallBundles() { 
    // Stop all bundles first. 
    Iterator<Bundle> bundleI = bundles.iterator(); 
    while (bundleI.hasNext()) { 
      Bundle bundle = bundleI.next(); 
      IBundle.STATE state = bundle.getState(); 
      switch (bundle.getState()) { 
        case ACTIVE: 
          try { 
            bundle.stop(); 
          } catch (BundleException e) { 
            fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not stop bundle(SymbolicName:" + bundle.getSymbolicName() + ")", e); 
          } 
          break; 
        case STARTING: 
        case STOPPING: 
          long intervalTime = 200; 
          for (long time = 0; state == STATE.STARTING || state == STATE.STOPPING; time += intervalTime) { 
            try { 
              Thread.sleep(intervalTime); 
            } catch (InterruptedException e) {} 
          } 
          // Check the last state. 
          switch (state) { 
            case ACTIVE: 
              try { 
                bundle.stop(); 
              } catch (BundleException e) {} 
              break; 
            case RESOLVED: 
              try { 
                bundle.uninstall(true, false); 
              } catch (BundleException e) {} 
              bundleI.remove(); 
              break; 
          } 
        default: 
          try { 
            bundle.uninstall(true, false); 
          } catch (BundleException e) {} 
          bundleI.remove(); 
          break; 
      } 
    } 
 
    // Then, uninstall bundles. 
    bundleI = bundles.iterator(); 
    while (bundleI.hasNext()) { 
      Bundle bundle = bundleI.next(); 
      try { 
        bundle.uninstall(true, false); 
      } catch (BundleException e) {} 
      bundleI.remove(); 
    } 
  } 
 
  private static Set<Bundle> createBundleSet(int op) { 
    if (op > 0) { 
      return new TreeSet<Bundle>(new Comparator<Bundle>() { 
        public int compare(Bundle o1, Bundle o2) { 
          BundleDescriptor d1 = o1.getDescriptor(); 
          BundleDescriptor d2 = o2.getDescriptor(); 
          int l1 = d1.getStartLevel(); 
          int l2 = d2.getStartLevel(); 
          if (l1 > l2) { 
            return 1; 
          } else if (l1 < l2) { 
            return -1; 
          } else { 
            return 0; 
          } 
        } 
      }); 
    } else if (op < 0) { 
      return new TreeSet<Bundle>(new Comparator<Bundle>() { 
        public int compare(Bundle o1, Bundle o2) { 
          BundleDescriptor d1 = o1.getDescriptor(); 
          BundleDescriptor d2 = o2.getDescriptor(); 
          int l1 = d1.getStartLevel(); 
          int l2 = d2.getStartLevel(); 
          if (l1 > l2) { 
            return -1; 
          } else if (l1 < l2) { 
            return 1; 
          } else { 
            return 0; 
          } 
        } 
      }); 
    } else { 
      return new HashSet<Bundle>(); 
    } 
  } 
 
  private static void startBundleSet(Set<Bundle> bundles) { 
    for (Bundle bundle : bundles) { 
      try { 
        bundle.start(); 
      } catch (BundleException e) {} 
    } 
  } 
 
  private static void stopBundleSet(Set<Bundle> bundles) { 
    for (Bundle bundle : bundles) { 
      try { 
        bundle.stop(); 
      } catch (BundleException e) {} 
    } 
  } 
}
package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.util.Enumeration; 
import java.util.Locale; 
import java.util.Map; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleActivator; 
import com.yipsilon.osgi.IBundleConstants; 
import com.yipsilon.osgi.IBundleEvent; 
import com.yipsilon.osgi.IFrameworkEvent; 
 
class Bundle implements IBundle { 
 
  private long id; 
 
  private IBundleActivator activator; 
 
  private STATE state; 
 
  private BundleDescriptor descriptor; 
 
  private String location; 
 
  private Framework framework; 
 
  private BundleContext context; 
 
  public Bundle(Framework framework, long id, String location) { 
    assert location == null : "Bundle location is null"; 
    assert framework == null : "Bundle framework is null"; 
    assert !new File(location).exists() : "Bundle file not found: " + location; 
 
    this.id = id; 
    this.framework = framework; 
    this.location = location; 
    this.context = new BundleContext(framework, this); 
    this.state = STATE.UNINSTALLED; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getBundleId() 
   */ 
  public long getBundleId() { 
    return id; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getEntry(java.lang.String) 
   */ 
  public URL getEntry(String name) { 
    return descriptor.getEntry(name); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getEntryPaths(java.lang.String) 
   */ 
  public Enumeration<URL> getEntryPaths(String path) { 
    return descriptor.getEntryPaths(path); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#findEntries(java.lang.String, java.lang.String, boolean) 
   */ 
  public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) { 
    return descriptor.findEntries(path, filePattern, recurse); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getHeaders() 
   */ 
  public Map<String, String> getHeaders() { 
    return descriptor.getHeaders(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getHeaders(java.util.Locale) 
   */ 
  public Map<String, String> getHeaders(Locale locale) { 
    return descriptor.getHeaders(locale); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getLastModified() 
   */ 
  public long getLastModified() { 
    return descriptor.getLastModified(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getLocation() 
   */ 
  public String getLocation() { 
    return descriptor.getLocation(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getState() 
   */ 
  public STATE getState() { 
    return state; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getSymbolicName() 
   */ 
  public String getSymbolicName() { 
    return descriptor.getSymbolicName(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getResource(java.lang.String) 
   */ 
  public URL getResource(String name) { 
    BundleClassLoader classLoader = framework.getClassLoader().getBundleClassLoader(descriptor); 
    if (classLoader != null) { 
      return classLoader.findResource(name); 
    } else { 
      return null; 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#getResources(java.lang.String) 
   */ 
  public Enumeration<URL> getResources(String name) throws IOException { 
    BundleClassLoader classLoader = framework.getClassLoader().getBundleClassLoader(descriptor); 
    if (classLoader != null) { 
      return classLoader.findResources(name); 
    } else { 
      return null; 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#loadClass(java.lang.String) 
   */ 
  public Class<?> loadClass(String name) throws ClassNotFoundException { 
    BundleClassLoader classLoader = framework.getClassLoader().getBundleClassLoader(descriptor); 
    if (classLoader != null) { 
      if (classLoader.isPackageExposed(name)) { 
        return classLoader.findClass(name); 
      } 
    } 
    throw new ClassNotFoundException(name); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#start() 
   */ 
  public void start() throws BundleException { 
    start(true); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#stop() 
   */ 
  public void stop() throws BundleException { 
    stop(true); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#uninstall() 
   */ 
  public void uninstall() throws BundleException { 
    uninstall(true, true); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#update() 
   */ 
  public synchronized void update() throws BundleException { 
 
    String updateLocation = descriptor.getUpdateLocation(); 
    if (updateLocation == null || "".equals(updateLocation.trim())) { 
      return; 
    } 
 
    try { 
      URL url = new URL(updateLocation); 
      InputStream input = url.openStream(); 
      update(input); 
      input.close(); 
    } catch (MalformedURLException e) { 
      String msg = "Can not update bundle via \"" + IBundleConstants.MANIFEST_BUNDLE_UPDATELOCATION + "\""; 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, msg, e); 
      throw new BundleException(msg, e); 
    } catch (IOException e) { 
      String msg = "Can not update bundle via \"" + IBundleConstants.MANIFEST_BUNDLE_UPDATELOCATION + "\""; 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, msg, e); 
      throw new BundleException(msg, e); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundle#update(java.io.InputStream) 
   */ 
  public void update(InputStream stream) throws BundleException { 
    try { 
      switch (state) { 
        case UNINSTALLED: 
          throw new BundleException("Bundle(" + getBundleId() + ") is not installed"); 
      } 
 
      // Download the new version package 
      File tempFile; 
      try { 
        tempFile = File.createTempFile("bundle", ".jar"); 
        tempFile.deleteOnExit(); 
 
        FileOutputStream output = new FileOutputStream(tempFile); 
        byte[] cache = new byte[4096]; 
        for (int i = stream.read(cache); i != -1; i = stream.read(cache)) { 
          output.write(cache, 0, i); 
        } 
        output.flush(); 
        output.close(); 
      } catch (MalformedURLException e) { 
        throw new BundleException("Can not update bundle(ID:" + id + ")", e); 
      } catch (IOException e) { 
        throw new BundleException("Can not update bundle(ID:" + id + ")", e); 
      } 
 
      // Check the descriptor 
      BundleDescriptor oldDescriptor = descriptor; 
      BundleDescriptor newDescriptor = new BundleDescriptor(framework, tempFile.getAbsolutePath()); 
 
      // Check the symbolic name. 
      if (!oldDescriptor.getSymbolicName().equals(newDescriptor.getSymbolicName())) { 
        throw new BundleException("The updated bundle(ID:" + getBundleId() + ") symbolic name(" + newDescriptor.getSymbolicName() + ") does not equal to original(" + oldDescriptor.getSymbolicName() + ")"); 
      } 
 
      // Check the version. 
      if (newDescriptor.getVersion().compareTo(oldDescriptor.getVersion()) < 1) { 
        // If the new package version is less than old package version, then exit. 
        return; 
      } 
 
      // Now, update bundle entry. 
      { 
        boolean started = (state == STATE.ACTIVE); 
 
        // Stop bundle first. 
        if (started) { 
          stop(false); 
        } 
 
        // Set the state is installed. 
        state = STATE.INSTALLED; 
 
        // Write bundle. 
        try { 
          File bundleFile = new File(location); 
          FileInputStream input = new FileInputStream(tempFile); 
          FileOutputStream output = new FileOutputStream(bundleFile); 
          byte[] cache = new byte[4096]; 
          for (int i = input.read(cache); i != -1; i = input.read(cache)) { 
            output.write(cache, 0, i); 
          } 
          output.flush(); 
          output.close(); 
          input.close(); 
        } catch (IOException e) { 
          throw new BundleException("Can not update bundle(ID:" + id + ")", e); 
        } 
        tempFile.delete(); 
 
        // Install bundle 
        FrameworkClassLoader fcl = framework.getClassLoader(); 
        fcl.removeBundleClassLoader(oldDescriptor); 
        fcl.createBundleClassLoader(newDescriptor); 
 
        // Resolve bundle 
        this.descriptor = newDescriptor; 
        resolve(false); 
 
        // Start bundle if possible. 
        if (started) { 
          start(false); 
        } 
      } 
 
      framework.fireBundleChanged(IBundleEvent.TYPE.UPDATED, this); 
    } catch (BundleException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not update bundle", t); 
      throw t; 
    } catch (RuntimeException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not update bundle", t); 
      throw t; 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#equals(java.lang.Object) 
   */ 
  @Override 
  public boolean equals(Object obj) { 
    if (obj instanceof Bundle) { 
      Bundle bundle = (Bundle) obj; 
      return bundle.getDescriptor().getSymbolicName().equals(descriptor.getSymbolicName()) && bundle.getDescriptor().getVersion().equals(descriptor.getVersion()); 
    } else { 
      return false; 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#hashCode() 
   */ 
  @Override 
  public int hashCode() { 
    return toString().hashCode(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#toString() 
   */ 
  @Override 
  public String toString() { 
    return descriptor.getSymbolicName() + " " + descriptor.getVersion().toString(); 
  } 
 
  protected BundleContext getBundleContext() { 
    return context; 
  } 
 
  protected Framework getFramework() { 
    return framework; 
  } 
 
  protected BundleDescriptor getDescriptor() { 
    return descriptor; 
  } 
 
  /** 
   * Uninstall bundle. 
   *  
   * @param remove 
   *            remove current from framework cache. 
   * @throws BundleException 
   *             Unable to uninstall bundle(ID:[BUNDLE_ID]), the state is [BUNDLE_STATE] 
   */ 
  protected synchronized void uninstall(boolean fireEvent, boolean remove) throws BundleException { 
    try { 
      switch (state) { 
        case UNINSTALLED: 
          return; 
        case STARTING: 
        case STOPPING: 
          long reasonableTime = 10000; 
          long intervalTime = 200; 
          for (long time = 0; state == STATE.STARTING || state == STATE.STOPPING; time += intervalTime) { 
            try { 
              Thread.sleep(intervalTime); 
            } catch (InterruptedException e) {} 
 
            if (time > reasonableTime) { 
              throw new BundleException("Unable to uninstall bundle(ID:" + getBundleId() + "), the state is " + state.name()); 
            } 
          } 
 
          switch (state) { 
            case ACTIVE: 
              // If bundle is active, stop it first. 
              stop(); 
              break; 
          } 
          break; 
        case ACTIVE: 
          // If bundle is active, stop it first. 
          stop(); 
          break; 
      } 
 
      // Remove bundle from framework 
      framework.uninstallBundle(this, remove); 
 
      state = STATE.UNINSTALLED; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.UNINSTALLED, this); 
      } 
    } catch (BundleException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, t); 
      throw t; 
    } catch (RuntimeException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not uninstall bundle(" + id + ":" + getSymbolicName() + ")", t); 
      throw t; 
    } 
  } 
 
  /** 
   * Install this bundle. 
   * <ul> 
   * This method will check the non-dependency attributes: 
   * <li>The symbolic name("Bundle-SymbolicName")</li> 
   * <li>The activator class name("Bundle-Activator")</li> 
   * <li>The exported packages("Export-Package")</li> 
   * <li>The class path("Bundle-ClassPath")</li> 
   * <li>The native code("Bundle-NativeCode")</li> 
   * </ul> 
   * <p> 
   * At last, change the state to <code>INSTALLED</code> and trigger listeners. 
   * </p> 
   *  
   * @throws BundleException 
   *             Bundle([BUNDLE_ID]) has been installed 
   * @throws BundleException 
   *             Required bundle([BUNDLE_SYMBOLIC_NAME]) not found in registry: [BUNDLE_LOCATION] 
   */ 
  protected synchronized void install(boolean fireEvent) throws BundleException { 
    try { 
      // Installed? 
      if (state != STATE.UNINSTALLED) { 
        throw new BundleException("Bundle(" + getSymbolicName() + ") has been installed"); 
      } 
 
      // Create descriptor 
      this.descriptor = new BundleDescriptor(framework, location); 
 
      // Change state 
      state = STATE.INSTALLED; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.INSTALLED, this); 
      } 
    } catch (BundleException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, t); 
      throw t; 
    } catch (RuntimeException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not install bundle(" + id + ":" + getSymbolicName() + ")", t); 
      throw t; 
    } 
  } 
 
  /** 
   * Resolve bundle. 
   * <ul> 
   * This method will check: 
   * <li>The imported packages("Import-Package")</li> 
   * <li>The required bundles("Require-Bundle")</li> 
   * <li>The bundle activator class("Bundle-Activator")</li> 
   * </ul> 
   * <p> 
   * At last, change the bundle state to <code>RESOLVED</code> and trigger listeners. 
   * </p> 
   *  
   * @throws BundleException 
   *             Imported package([IMPORT_PACKAGE]) not found: [BUNDLE_LOCATION] 
   * @throws BundleException 
   *             Required bundle(SymbolicName:[BUNDLE_SYMBOLIC_NAME]) not installed: [BUNDLE_LOCATION] 
   * @throws BundleException 
   *             Bundle(SymbolicName:[BUNDLE_SYMBOLIC_NAME]) not installed 
   * @throws BundleException 
   *             Can not instantiate activator class: [BUNDLE_ACTIVATOR_CLASS] 
   * @throws BundleException 
   *             Can not access activator class: [BUNDLE_ACTIVATOR_CLASS] 
   * @throws BundleException 
   *             Bundle activator class not found: [BUNDLE_ACTIVATOR_CLASS] 
   * @throws BundleException 
   *             Bundle activator class is not a <code>IBundleActivator</code> implementation: 
   *             [BUNDLE_ACTIVATOR_CLASS] 
   */ 
  protected synchronized void resolve(boolean fireEvent) throws BundleException { 
    try { 
      // Resolve imported packages 
      for (String importedPackage : descriptor.getImportedPackages()) { 
        if (!framework.getClassLoader().isPackageExposed(importedPackage)) { 
          throw new BundleException("Imported package(\"" + importedPackage + "\") not found: " + getLocation()); 
        } 
      } 
 
      // Resolve required bundles 
      for (String requiredBundle : descriptor.getRequiredBundles()) { 
        if (framework.getBundle(requiredBundle) == null) { 
          throw new BundleException("Required bundle(SymbolicName:\"" + requiredBundle + "\") not installed: " + getLocation()); 
        } 
      } 
 
      // Check class loader 
      BundleClassLoader classLoader = framework.getClassLoader().getBundleClassLoader(descriptor); 
      if (classLoader == null) { 
        throw new BundleException("Bundle(SymbolicName:" + descriptor.getSymbolicName() + ") not installed"); 
      } 
 
      // Create activator. 
      String activatorClass = descriptor.getActivator(); 
      try { 
        Class<?> c = classLoader.loadClass(activatorClass); 
        if (c == null) { 
          throw new ClassNotFoundException(activatorClass); 
        } 
        activator = (IBundleActivator) c.newInstance(); 
      } catch (InstantiationException e) { 
        throw new BundleException("Can not instantiate activator class: " + activatorClass, e); 
      } catch (IllegalAccessException e) { 
        throw new BundleException("Can not access activator class: " + activatorClass, e); 
      } catch (ClassNotFoundException e) { 
        throw new BundleException("Bundle activator class not found: " + activatorClass); 
      } catch (ClassCastException e) { 
        throw new BundleException("Bundle activator class is not a " + IBundleActivator.class.getName() + " implementation: " + activatorClass); 
      } 
 
      // Change state 
      state = STATE.RESOLVED; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.RESOLVED, this); 
      } 
    } catch (BundleException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, t); 
      throw t; 
    } catch (RuntimeException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not resolve bundle(" + id + ":" + getSymbolicName() + ")", t); 
      throw t; 
    } 
  } 
 
  private synchronized void stop(boolean fireEvent) throws BundleException { 
    try { 
      switch (state) { 
        case INSTALLED: 
          throw new BundleException("Bundle(" + getBundleId() + ") is not resolved"); 
        case UNINSTALLED: 
          throw new BundleException("Bundle(" + getBundleId() + ") is not installed"); 
        case STARTING: 
        case STOPPING: 
          long reasonableTime = 1000; 
          long intervalTime = 200; 
          for (long time = 0; state == STATE.STARTING || state == STATE.STOPPING; time += intervalTime) { 
            try { 
              Thread.sleep(intervalTime); 
            } catch (InterruptedException e) {} 
 
            if (time > reasonableTime) { 
              throw new BundleException("Unable to stop bundle(" + getBundleId() + "), the state is " + state.name()); 
            } 
          } 
          // Check the last state. 
          switch (state) { 
            case ACTIVE: 
              break; 
            case RESOLVED: 
              return; 
          } 
          break; 
        case ACTIVE: 
          break; 
        case RESOLVED: 
          return; 
      } 
 
      state = STATE.STOPPING; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.STOPPING, this); 
      } 
 
      try { 
        activator.stop(context); 
      } catch (Throwable t) { 
        state = STATE.RESOLVED; 
        throw new BundleException("Unable to stop bundle(" + getBundleId() + ") activator: " + activator.getClass().getName(), t); 
      } 
 
      switch (state) { 
        case UNINSTALLED: 
          throw new BundleException("Bundle(" + getBundleId() + ") is not installed"); 
      } 
 
      state = STATE.RESOLVED; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.STOPPED, this); 
      } 
    } catch (BundleException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, t); 
      throw t; 
    } catch (RuntimeException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not stop bundle(" + id + ":" + getSymbolicName() + ")", t); 
      throw t; 
    } 
  } 
 
  private synchronized void start(boolean fireEvent) throws BundleException { 
 
    try { 
      // Check the start level 
      if (descriptor.getStartLevel() > framework.getStartLevel()) { 
        throw new BundleException("Can not start bundle(ID:" + getBundleId() + "), the start level(" + descriptor.getStartLevel() + ") is greater than system start level(" + framework.getStartLevel() + ")"); 
      } 
 
      switch (state) { 
        case INSTALLED: 
          throw new BundleException("Bundle(ID:" + getBundleId() + ") is not resolved"); 
        case UNINSTALLED: 
          throw new BundleException("Bundle(ID:" + getBundleId() + ") is not installed"); 
        case STARTING: 
        case STOPPING: 
 
          // Loop check the state. 
          long reasonableTime = 1000; 
          long intervalTime = 200; 
          for (long time = 0; state == STATE.STARTING || state == STATE.STOPPING; time += intervalTime) { 
            try { 
              Thread.sleep(intervalTime); 
            } catch (InterruptedException e) {} 
 
            if (time > reasonableTime) { 
              throw new BundleException("Unable to start bundle(" + getBundleId() + "), the state is " + state.name()); 
            } 
          } 
 
          // Check the last state. 
          switch (state) { 
            case ACTIVE: 
              return; 
            case RESOLVED: 
              break; 
          } 
          break; 
        case ACTIVE: 
          return; 
        case RESOLVED: 
          break; 
      } 
 
      state = STATE.STARTING; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.STARTING, this); 
      } 
 
      try { 
        activator.start(context); 
      } catch (Throwable t) { 
        state = STATE.RESOLVED; 
        throw new BundleException("Unable to start bundle(" + getBundleId() + ") activator: " + activator.getClass().getName(), t); 
      } 
 
      switch (state) { 
        case UNINSTALLED: 
          throw new BundleException("Bundle(" + getBundleId() + ") is not installed"); 
      } 
 
      state = STATE.ACTIVE; 
      if (fireEvent) { 
        framework.fireBundleChanged(IBundleEvent.TYPE.STARTED, this); 
      } 
    } catch (BundleException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, t); 
      throw t; 
    } catch (RuntimeException t) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not start bundle(" + id + ":" + getSymbolicName() + ")", t); 
      throw t; 
    } 
  } 
} 
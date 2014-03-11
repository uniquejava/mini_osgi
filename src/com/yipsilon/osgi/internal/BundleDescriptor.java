package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Enumeration; 
import java.util.HashMap; 
import java.util.HashSet; 
import java.util.ListResourceBundle; 
import java.util.Locale; 
import java.util.Map; 
import java.util.PropertyResourceBundle; 
import java.util.ResourceBundle; 
import java.util.Set; 
import java.util.StringTokenizer; 
import java.util.Vector; 
import java.util.jar.Attributes; 
import java.util.jar.JarEntry; 
import java.util.jar.JarFile; 
import java.util.jar.Manifest; 
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleConstants; 
import com.yipsilon.osgi.IBundleVersion; 
import com.yipsilon.osgi.IFrameworkEvent; 
 
class BundleDescriptor implements IBundleDescriptor { 
 
  private long lastModified; 
 
  private String location; 
 
  private String[] classPaths; 
 
  private String documentLocation; 
 
  private String[] exportedPackages; 
 
  private String[] importedPackages; 
 
  private String name; 
 
  private String[] nativeCodes; 
 
  private String[] requiredBundles; 
 
  private String updateLocation; 
 
  private String vendor; 
 
  private BundleVersion version; 
 
  private String[] categories; 
 
  private String copyright; 
 
  private String contactAddress; 
 
  private String description; 
 
  private String symbolicName; 
 
  private String activator; 
 
  private boolean autoStart; 
 
  private int startLevel; 
 
  private Map<Locale, Map<String, String>> headers; 
 
  private Map<String, String> entries; 
 
  private Framework framework; 
 
  private double random = Math.random(); 
 
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd"); 
 
  private static final String[] EMPTY_STRING_ARRAY = new String[0]; 
 
  private static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ListResourceBundle() { 
 
    private Object[][] EMPTY = new Object[0][0]; 
 
    /* 
     * (non-Javadoc) 
     *  
     * @see java.util.ListResourceBundle#getContents() 
     */ 
    @Override 
    protected Object[][] getContents() { 
      return EMPTY; 
    } 
  }; 
 
  public BundleDescriptor(Framework framework, String path) throws BundleException { 
    if (framework == null) { 
      throw new BundleException("Framework not found"); 
    } 
 
    if (path == null) { 
      throw new BundleException("Bundle file not found"); 
    } 
 
    File file = new File(path); 
 
    if (path == null) { 
      throw new BundleException("Bundle file not exists"); 
    } 
 
    this.location = path; 
    this.framework = framework; 
    this.lastModified = file.lastModified(); 
    this.headers = new HashMap<Locale, Map<String, String>>(); 
    this.entries = new HashMap<String, String>(); 
 
    try { 
      init(file); 
    } catch (IOException ioe) { 
      throw new BundleException("Can not initialize bundle file: " + file.getAbsolutePath(), ioe); 
    } 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleDescriptor#getLastModified() 
   */ 
  public long getLastModified() { 
    return lastModified; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getClassPaths() 
   */ 
  public String[] getClassPaths() { 
    return classPaths; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getDocumentLocation() 
   */ 
  public String getDocumentLocation() { 
    return documentLocation; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getExportPackages() 
   */ 
  public String[] getExportedPackages() { 
    return exportedPackages; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getImportPackages() 
   */ 
  public String[] getImportedPackages() { 
    return importedPackages; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getName() 
   */ 
  public String getName() { 
    return name; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getNativeCodes() 
   */ 
  public String[] getNativeCodes() { 
    return nativeCodes; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getRequiredExtensions() 
   */ 
  public String[] getRequiredBundles() { 
    return requiredBundles; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getUpdateLocation() 
   */ 
  public String getUpdateLocation() { 
    return updateLocation; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getVendor() 
   */ 
  public String getVendor() { 
    return vendor; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getVersion() 
   */ 
  public IBundleVersion getVersion() { 
    return version; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getCategories() 
   */ 
  public String[] getCategories() { 
    return categories; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getCopyright() 
   */ 
  public String getCopyright() { 
    return copyright; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getContactAddress() 
   */ 
  public String getContactAddress() { 
    return contactAddress; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.xmlx.loader.IExtensionDescriptor#getDescription() 
   */ 
  public String getDescription() { 
    return description; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleDescriptor#isAutoStart() 
   */ 
  public boolean isAutoStart() { 
    return autoStart; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IBundleDescriptor#getStartLevel() 
   */ 
  public int getStartLevel() { 
    return startLevel < 0 ? framework.getInitialBundleStartLevel() : startLevel; 
  } 
 
  double getRandomId() { 
    return random; 
  } 
 
  String getActivator() { 
    return activator; 
  } 
 
  /** 
   * @see IBundle#getSymbolicName() 
   */ 
  String getSymbolicName() { 
    return symbolicName; 
  } 
 
  /** 
   * @see IBundle#getLocation() 
   */ 
  String getLocation() { 
    return location; 
  } 
 
  /** 
   * @see IBundle#getEntry(java.lang.String) 
   */ 
  URL getEntry(String name) { 
    String entry = entries.get(name); 
    if (entry != null) { 
      try { 
        return new URL(entry); 
      } catch (MalformedURLException e) { 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not create entry URL: " + entry, e); 
        return null; 
      } 
    } else { 
      return null; 
    } 
  } 
 
  /** 
   * @see IBundle#getEntryPaths(java.lang.String) 
   */ 
  Enumeration<URL> getEntryPaths(String path) { 
    Vector<URL> entryUrls = new Vector<URL>(); 
    for (String key : entries.keySet()) { 
      String value = entries.get(key); 
      if (key.startsWith(path)) { 
        try { 
          entryUrls.add(new URL(value)); 
        } catch (MalformedURLException e) { 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not create entry URL: " + value, e); 
          continue; 
        } 
      } 
    } 
    return entryUrls.elements(); 
  } 
 
  /** 
   * @see IBundle#findEntries(java.lang.String, java.lang.String, boolean) 
   */ 
  Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) { 
    Vector<URL> entryUrls = new Vector<URL>(); 
    Pattern pattern = Pattern.compile(filePattern); 
    for (String key : entries.keySet()) { 
      if (key.startsWith(path)) { 
        String fileName; 
        if (path.endsWith("/")) { 
          fileName = key.substring(path.length()); 
        } else { 
          fileName = key.substring(path.length() + 1); 
        } 
 
        // Maybe the fileName constains path information. e.g. "hello/world.class" 
        if (fileName.indexOf('/') > -1) { 
          // Match sub-directories 
          if (recurse) { 
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1); 
          } else { 
            continue; 
          } 
        } 
 
        Matcher matcher = pattern.matcher(fileName); 
        if (matcher.find()) { 
          try { 
            entryUrls.add(new URL(entries.get(key))); 
          } catch (MalformedURLException e) { 
            framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not create entry URL: " + entries.get(key), e); 
            continue; 
          } 
        } 
      } 
    } 
    return entryUrls.elements(); 
  } 
 
  /** 
   * @see IBundle#getHeaders() 
   */ 
  Map<String, String> getHeaders() { 
    return getHeaders(Locale.getDefault()); 
  } 
 
  /** 
   * @see IBundle#getHeaders(java.util.Locale) 
   */ 
  Map<String, String> getHeaders(Locale locale) { 
    Map<String, String> h = headers.get(locale); 
    if (h == null) { 
      File file = new File(location); 
      JarFile jarFile = null; 
      try { 
        jarFile = new JarFile(file); 
        Manifest mf = jarFile.getManifest(); 
        Attributes attributes = mf.getMainAttributes(); 
 
        // Get resource bundle. 
        String localization = attributes.getValue(IBundleConstants.MANIFEST_BUNDLE_LOCALIZATION); 
        if (localization == null) { 
          localization = "OSGI-INF/l10n/bundle"; 
        } 
        ResourceBundle resourceBundle = createResourceBundle(file, jarFile, localization, locale); 
 
        // Apply to header values. 
        h = new HashMap<String, String>(); 
        for (Object key : attributes.keySet()) { 
          String value = (String) attributes.get(key); 
 
          // Localize value if "%" identifier found. 
          if (value.indexOf('%') > -1) { 
            Enumeration<String> bundleKeys = resourceBundle.getKeys(); 
            while (bundleKeys.hasMoreElements()) { 
              String bundleKey = bundleKeys.nextElement(); 
              value = replace(value, "%" + bundleKey, resourceBundle.getString(bundleKey)); 
            } 
          } 
 
          h.put(key.toString(), value); 
        } 
 
        headers.put(locale, h); 
      } catch (IOException e) { 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not create localization file: " + location, e); 
        return null; 
      } finally { 
        if (jarFile != null) { 
          try { 
            jarFile.close(); 
          } catch (IOException e) {} 
        } 
      } 
    } 
 
    if (h == null) { 
      h = headers.get(Locale.getDefault()); 
    } 
 
    return h; 
  } 
 
  void setStartLevel(int startLevel) { 
    assert startLevel < 0; 
    this.startLevel = startLevel; 
  } 
 
  /** 
   * Initialize the extension file. 
   *  
   * @param file 
   *            extension file. 
   * @throws IOException 
   *             If some errors occurred when read jar information, throw it. 
   */ 
  private void init(File file) throws IOException, BundleException { 
    JarFile jarFile = new JarFile(file); 
    try { 
      Manifest mf = jarFile.getManifest(); 
      Attributes attributes = mf.getMainAttributes(); 
 
      Map<String, String> headers = getHeaders(); 
 
      symbolicName = headers.get(IBundleConstants.MANIFEST_BUNDLE_SYMBOLICNAME); 
      if (symbolicName == null) { 
        throw new BundleException("Bundle symbolic name not found: " + file.getAbsolutePath()); 
      } 
      if (framework.getBundle(symbolicName) != null) { 
        throw new BundleException("Bundle(SymbolicName: " + symbolicName + ") already exists: " + file.getAbsolutePath()); 
      } 
 
      { // Validate the bundle activator. 
        activator = headers.get(IBundleConstants.MANIFEST_BUNDLE_ACTIVATOR); 
        if (activator == null) { 
          throw new BundleException("Bundle activator definition not found: " + file.getAbsolutePath()); 
        } 
        String activatorPath = activator.replace('.', '/') + ".class"; 
        boolean entryExists = false; 
        Enumeration<JarEntry> entryEnum = jarFile.entries(); 
        while (entryEnum.hasMoreElements()) { 
          JarEntry entry = entryEnum.nextElement(); 
          String entryName = entry.getName(); 
          if (entryName.equals(activatorPath)) { 
            entryExists = true; 
            break; 
          } 
        } 
        if (!entryExists) { 
          throw new BundleException("Bundle activator class(\"" + activator + "\") not found: " + file.getAbsolutePath()); 
        } 
      } 
 
      name = headers.get(IBundleConstants.MANIFEST_BUNDLE_NAME); 
      if (name == null) { 
        String fileName = file.getName(); 
        name = fileName.substring(0, fileName.length() - 4); 
      } 
 
      description = headers.get(IBundleConstants.MANIFEST_BUNDLE_DESCRIPTION); 
      if (description == null) { 
        description = ""; 
      } 
 
      contactAddress = headers.get(IBundleConstants.MANIFEST_BUNDLE_CONTACTADDRESS); 
      if (contactAddress == null) { 
        contactAddress = ""; 
      } 
 
      copyright = headers.get(IBundleConstants.MANIFEST_BUNDLE_COPYRIGHT); 
      if (copyright == null) { 
        copyright = ""; 
      } 
 
      String autostart = headers.get(IBundleConstants.MANIFEST_BUNDLE_AUTOSTART); 
      if (autostart != null) { 
        autoStart = Boolean.valueOf(autostart.trim()); 
      } else { 
        autoStart = false; 
      } 
 
      String startlevel = headers.get(IBundleConstants.MANIFEST_BUNDLE_STARTLEVEL); 
      if (startlevel != null) { 
        try { 
          startLevel = Integer.parseInt(startlevel.trim()); 
          if (startLevel < 0) { 
            startLevel = -1; 
          } 
        } catch (NumberFormatException nfe) { 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not parse bundle(SymbolicName:" + symbolicName + ") start level: " + startlevel, nfe); 
          startLevel = -1; 
        } 
      } else { 
        startLevel = -1; 
      } 
 
      String category = headers.get(IBundleConstants.MANIFEST_BUNDLE_CATEGORY); 
      if (category != null) { 
        StringTokenizer tokenizer = new StringTokenizer(category, ","); 
        categories = new String[tokenizer.countTokens()]; 
        for (int i = 0; tokenizer.hasMoreTokens(); i++) { 
          categories[i] = tokenizer.nextToken().trim(); 
        } 
      } else { 
        categories = EMPTY_STRING_ARRAY; 
      } 
 
      String bundleversion = headers.get(IBundleConstants.MANIFEST_BUNDLE_VERSION); 
      if (bundleversion == null) { 
        bundleversion = "0.0.0." + dateFormat.format(new Date(file.lastModified())); 
      } 
      version = BundleVersion.parseVersion(bundleversion); 
 
      vendor = headers.get(IBundleConstants.MANIFEST_BUNDLE_VENDOR); 
      if (vendor == null) { 
        vendor = ""; 
      } 
 
      updateLocation = headers.get(IBundleConstants.MANIFEST_BUNDLE_UPDATELOCATION); 
      if (updateLocation == null) { 
        updateLocation = "file:" + file.getAbsolutePath(); 
      } 
 
      documentLocation = headers.get(IBundleConstants.MANIFEST_BUNDLE_DOCURL); 
      if (documentLocation == null) { 
        documentLocation = "jar:file:" + file.getAbsolutePath() + "!/docs/index.html"; 
      } 
 
      // Parse classpaths 
      String classpath = attributes.getValue(IBundleConstants.MANIFEST_BUNDLE_CLASSPATH); 
      if (classpath != null) { 
        StringTokenizer tokenizer = new StringTokenizer(classpath, ","); 
        classPaths = new String[tokenizer.countTokens()]; 
        for (int i = 0; tokenizer.hasMoreTokens(); i++) { 
 
          String classPath = tokenizer.nextToken(); 
 
          // Check the classpath entry 
          boolean entryExists = false; 
          Enumeration<JarEntry> entryEnum = jarFile.entries(); 
          searchClasspathEntry: // Search the classpath in entries. 
          while (entryEnum.hasMoreElements()) { 
            JarEntry entry = entryEnum.nextElement(); 
            String entryName = entry.getName(); 
            if (entryName.equals(classPath)) { 
              entryExists = true; 
              break searchClasspathEntry; 
            } 
          } 
 
          if (entryExists) { 
            classPaths[i] = generateURL(classPath); 
          } else { 
            throw new BundleException("Classpath(\"" + classPath + "\") not found: " + file.getAbsolutePath()); 
          } 
        } 
      } else { 
        classPaths = EMPTY_STRING_ARRAY; 
      } 
 
      // Parse export packages 
      String exportpackages = attributes.getValue(IBundleConstants.MANIFEST_EXPORT_PACKAGE); 
      if (exportpackages != null) { 
        StringTokenizer tokenizer = new StringTokenizer(exportpackages, ","); 
        exportedPackages = new String[tokenizer.countTokens()]; 
        for (int i = 0; tokenizer.hasMoreTokens(); i++) { 
 
          String exportPackage = tokenizer.nextToken(); 
 
          // Convert package "hello.world" to "hello/world/". 
          String exportEntryName = exportPackage.replace('.', '/') + '/'; 
 
          // Check the package entry 
          boolean entryExists = false; 
          Enumeration<JarEntry> entryEnum = jarFile.entries(); 
          searchExportPackage: // Search the classpath in entries. 
          while (entryEnum.hasMoreElements()) { 
            JarEntry entry = entryEnum.nextElement(); 
            String entryName = entry.getName(); 
            // The resource always starts with package. e.g. hello/world/haha.class contains "hello.world" package. 
            if (entryName.startsWith(exportEntryName)) { 
              entryExists = true; 
              break searchExportPackage; 
            } 
          } 
 
          if (entryExists) { 
            exportedPackages[i] = exportPackage; 
          } else { 
            throw new BundleException("Exported package(\"" + exportPackage + "\") not found: " + file.getAbsolutePath()); 
          } 
        } 
      } else { 
 
        // If the "Export-Packages" definition not found, then return all packages in this jar. 
        exportedPackages = getAvailablePackages(jarFile).toArray(EMPTY_STRING_ARRAY); 
      } 
 
      // Parse import packages, nee 
      String importpackages = attributes.getValue(IBundleConstants.MANIFEST_IMPORT_PACKAGE); 
      if (importpackages != null) { 
        StringTokenizer tokenizer = new StringTokenizer(importpackages, ","); 
        importedPackages = new String[tokenizer.countTokens()]; 
        for (int i = 0; tokenizer.hasMoreTokens(); i++) { 
          String importPackage = tokenizer.nextToken(); 
          importedPackages[i] = importPackage; 
        } 
      } else { 
        importedPackages = EMPTY_STRING_ARRAY; 
      } 
 
      // Parse native codes 
      String nativecodes = attributes.getValue(IBundleConstants.MANIFEST_BUNDLE_NATIVECODE); 
      if (nativecodes != null) { 
 
        StringTokenizer tokenizer = new StringTokenizer(nativecodes, ","); 
        nativeCodes = new String[tokenizer.countTokens()]; 
        for (int i = 0; tokenizer.hasMoreTokens(); i++) { 
 
          String nativeCode = tokenizer.nextToken(); 
 
          // Check the native code entry 
          boolean entryExists = false; 
          Enumeration<JarEntry> entryEnum = jarFile.entries(); 
          searchNativeCode: // Search the native code path in entries. 
          while (entryEnum.hasMoreElements()) { 
            JarEntry entry = entryEnum.nextElement(); 
            String entryName = entry.getName(); 
            if (entryName.equals(nativeCode)) { 
              entryExists = true; 
              break searchNativeCode; 
            } 
          } 
 
          if (entryExists) { 
            nativeCodes[i] = generateURL(nativeCode); 
          } else { 
            throw new BundleException("Native code(\"" + nativeCode + "\") not found in bundle: " + file.getAbsolutePath()); 
          } 
        } 
      } else { 
        nativeCodes = EMPTY_STRING_ARRAY; 
      } 
 
      // Parse reuqired extensions 
      String requiredbundles = attributes.getValue(IBundleConstants.MANIFEST_REQUIRE_BUNDLE); 
      if (requiredbundles != null) { 
        StringTokenizer tokenizer = new StringTokenizer(requiredbundles, ","); 
        requiredBundles = new String[tokenizer.countTokens()]; 
        for (int i = 0; tokenizer.hasMoreTokens(); i++) { 
          String requiredBundle = tokenizer.nextToken(); 
          requiredBundles[i] = requiredBundle; 
        } 
      } else { 
        requiredBundles = EMPTY_STRING_ARRAY; 
      } 
 
      // Cache the entry 
      Enumeration<JarEntry> entryEnum = jarFile.entries(); 
      while (entryEnum.hasMoreElements()) { 
        JarEntry entry = entryEnum.nextElement(); 
        String entryName = entry.getName(); 
        entries.put(entryName, generateURL(entryName)); 
      } 
 
    } finally { 
      jarFile.close(); 
    } 
  } 
 
  private ResourceBundle createResourceBundle(File file, JarFile jarFile, String localization, Locale locale) throws IOException { 
 
    ResourceBundle localizationBundle = null; 
 
    if (localization != null) { 
 
      JarEntry entry = null; 
      if (locale == null) { 
        locale = Locale.getDefault(); 
      } 
 
      if (entry == null) { 
        String path = localization + "_" + locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant() + ".properties"; 
        entry = jarFile.getJarEntry(path); 
        if (entry != null) { 
          InputStream stream = jarFile.getInputStream(entry); 
          localizationBundle = new PropertyResourceBundle(stream); 
          stream.close(); 
        } 
      } 
 
      if (entry == null) { 
        String path = localization + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties"; 
        entry = jarFile.getJarEntry(path); 
        if (entry != null) { 
          InputStream stream = jarFile.getInputStream(entry); 
          localizationBundle = new PropertyResourceBundle(stream); 
          stream.close(); 
        } 
      } 
 
      if (entry == null) { 
        String path = localization + "_" + locale.getLanguage() + ".properties"; 
        entry = jarFile.getJarEntry(path); 
        if (entry != null) { 
          InputStream stream = jarFile.getInputStream(entry); 
          localizationBundle = new PropertyResourceBundle(stream); 
          stream.close(); 
        } 
      } 
 
      if (entry == null) { 
        String path = localization + ".properties"; 
        entry = jarFile.getJarEntry(path); 
        if (entry != null) { 
          InputStream stream = jarFile.getInputStream(entry); 
          localizationBundle = new PropertyResourceBundle(stream); 
          stream.close(); 
        } 
      } 
    } 
 
    if (localizationBundle == null) { 
      localizationBundle = EMPTY_RESOURCE_BUNDLE; 
    } 
 
    return localizationBundle; 
  } 
 
  /** 
   * Convert specified path to absolute URL path. 
   *  
   * @param path 
   *            specified path. 
   * @return the absolute URL path. 
   */ 
  private String generateURL(String path) { 
    return "jar:file:" + location + "!/" + path; 
  } 
 
  /** 
   * Simple string replacement function. 
   *  
   * @param data 
   *            original data. 
   * @param source 
   *            replace string 
   * @param target 
   *            replaced string 
   * @return the replaced data. 
   */ 
  private static String replace(String data, String source, String target) { 
    StringBuffer buf_data = new StringBuffer(data); 
    int index = data.indexOf(source); 
    while (index != -1) { 
      buf_data = buf_data.replace(index, index + source.length(), target); 
      index = buf_data.toString().indexOf(source, index + target.length()); 
    } 
    return buf_data.toString(); 
  } 
 
  private Set<String> getAvailablePackages(JarFile jarFile) { 
    Set<String> packageSet = new HashSet<String>(); 
    Enumeration<JarEntry> entryEnum = jarFile.entries(); 
    while (entryEnum.hasMoreElements()) { 
      JarEntry entry = entryEnum.nextElement(); 
      String entryName = entry.getName(); 
 
      StringTokenizer tokenizer = new StringTokenizer(entryName, "/"); 
      StringBuffer pathBuffer = new StringBuffer(entryName.length()); 
      while (tokenizer.hasMoreTokens()) { 
        String token = tokenizer.nextToken(); 
 
        pathBuffer.append(token); 
 
        if (!tokenizer.hasMoreTokens()) { 
          if (entry.isDirectory()) { 
            pathBuffer.append("/"); 
          } 
        } else { 
          pathBuffer.append("/"); 
        } 
 
        String packagePath = pathBuffer.toString(); 
        if (packagePath.indexOf('-') < 0 && packagePath.endsWith("/")) { 
          String packageName = packagePath.substring(0, packagePath.length() - 1).replace('/', '.'); 
          if (!packageSet.contains(packageName)) { 
            packageSet.add(packageName); 
          } 
        } 
      } 
    } 
 
    // Add the default package. 
    packageSet.add(""); 
 
    return packageSet; 
  } 
}
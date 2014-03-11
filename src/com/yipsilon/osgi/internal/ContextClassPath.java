package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.io.IOException; 
import java.net.URL; 
import java.util.Enumeration; 
import java.util.HashMap; 
import java.util.HashSet; 
import java.util.Iterator; 
import java.util.Map; 
import java.util.Set; 
import java.util.Vector; 
import java.util.jar.JarEntry; 
import java.util.jar.JarFile; 
 
import com.yipsilon.osgi.IFrameworkEvent; 
 
class ContextClassPath { 
 
  /** 
   * absolute URL -> classpath file(or directory). 
   */ 
  private Map<URL, File> classFiles; 
 
  /** 
   * absolute URL -> relative path. 
   */ 
  private Map<URL, String> classPaths; 
 
  /** 
   * Cache the packages for "Import-Package" definition. 
   */ 
  private Map<String, Set<File>> packages; 
 
  private Framework framework; 
 
  public ContextClassPath(Framework framework) { 
    assert framework == null; 
    this.framework = framework; 
    this.classPaths = new HashMap<URL, String>(); 
    this.classFiles = new HashMap<URL, File>(); 
    this.packages = new HashMap<String, Set<File>>(); 
  } 
 
  public void addClasspath(String classpath) { 
    assert classpath == null; 
 
    File classpathFile = new File(classpath); 
    if (classpathFile.exists()) { 
      addClasspath(classpathFile); 
    } else { 
      throw new IllegalArgumentException("Classpath file not found: " + classpath); 
    } 
  } 
 
  public void removeClasspath(String classpath) { 
    assert classpath == null; 
 
    removeClasspath(new File(classpath)); 
  } 
 
  public URL findResource(String name) { 
    if (name == null) { 
      return null; 
    } 
 
    for (URL key : classPaths.keySet()) { 
      String value = classPaths.get(key); 
      if (value.equals(name)) { 
        return key; 
      } 
    } 
    return null; 
  } 
 
  public Enumeration<URL> findResources(String name) { 
    if (name == null) { 
      return null; 
    } 
 
    Vector<URL> urls = new Vector<URL>(); 
    Iterator<URL> urlI = classPaths.keySet().iterator(); 
    while (urlI.hasNext()) { 
      URL url = urlI.next(); 
      String cName = classPaths.get(url); 
      if (cName.equals(name)) { 
        urls.add(url); 
      } 
    } 
 
    if (urls.isEmpty()) { 
      return null; 
    } else { 
      return urls.elements(); 
    } 
  } 
 
  public boolean isPackageExposed(String packageName) { 
    return packages.containsKey(packageName); 
  } 
 
  /** 
   * Remove cached classpath files. 
   *  
   * @param classpath 
   *            class path to be removed. 
   * @see Classpath#removeClasspath(String) 
   */ 
  private void removeClasspath(File f) { 
    Iterator<URL> urlI = classFiles.keySet().iterator(); 
    while (urlI.hasNext()) { 
      URL url = urlI.next(); 
      File file = classFiles.get(url); 
      if (file.equals(f)) { 
        classPaths.remove(url); 
        urlI.remove(); 
      } 
    } 
    removePackages(f); 
  } 
 
  /** 
   * Cache a classpath file. 
   *  
   * @param f 
   *            file to be cached. 
   * @see Classpath#addClasspath(String) 
   */ 
  private void addClasspath(File f) { 
    try { 
      if (f.isDirectory()) { 
        cacheFromDirectory(f); 
      } else { 
        cacheFromJarFile(f); 
      } 
    } catch (IOException e) { 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, e); 
    } 
  } 
 
  /** 
   * Cache a directory. 
   *  
   * @param dir 
   *            directory to be cached. 
   * @param deep 
   *            use deep load mode? 
   * @see Classpath#addCache(File) 
   */ 
  private void cacheFromDirectory(File dir) throws IOException { 
 
    // Add the root directory as the root package. 
    addPackage(dir, ""); 
 
    // Cache files and directories 
    File[] files = dir.listFiles(); 
    for (File file : files) { 
      if (file.isDirectory()) { 
        cacheFromDirectory(dir, file); 
      } else { 
        cacheFromLocFile(dir, file); 
      } 
    } 
  } 
 
  /** 
   * Cache a classpath directory. 
   *  
   * @param prefix 
   *            classpath prefix. 
   * @param dirFile 
   *            directory to be cached. 
   * @see Classpath#cacheFromDirectory(File, boolean) 
   */ 
  private void cacheFromDirectory(File root, File dirFile) throws IOException { 
    if (dirFile.isDirectory()) { 
 
      String dirPath = dirFile.getAbsolutePath().substring(root.getAbsolutePath().length()).replace('\\', '/'); 
      String dirPackage = dirPath.replace('/', '.'); 
      if (dirPath.startsWith("/")) { 
        // Filtrate "/" prefix. 
        dirPath = dirPath.substring(1); 
        dirPackage = dirPackage.substring(1); 
      } 
      if (!dirPath.endsWith("/")) { 
        // Append the "/" suffix if not exist. 
        dirPath = dirPath + "/"; 
      } 
      if (dirPackage.endsWith(".")) { 
        dirPackage = dirPackage.substring(0, dirPackage.length() - 1); 
      } 
 
      URL dirURL = dirFile.toURL(); 
 
      if (!classPaths.containsKey(dirURL)) { 
 
        // Add directory URL 
        addToCache(dirURL, dirPath); 
 
        // Add classpath file 
        addToCache(dirURL, root); 
 
        // Cache the package name. 
        addPackage(root, dirPackage); 
 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.DEBUG, "Mapping resource[" + dirPath + "] to URL[" + dirURL.toString() + "] in directory[" + root.getAbsolutePath() + "]"); 
      } 
 
      File[] cFiles = dirFile.listFiles(); 
      for (File cFile : cFiles) { 
        cacheFromDirectory(root, cFile); 
      } 
    } else { 
      String fileName = dirFile.getName().toLowerCase(); 
      if (fileName.endsWith(".jar")) { 
        cacheFromJarFile(dirFile); 
      } else { 
        cacheFromLocFile(root, dirFile); 
      } 
    } 
  } 
 
  /** 
   * Cache a classpath file. 
   *  
   * @param prefix 
   *            classpath perfix. 
   * @param locFile 
   *            file to be cached. 
   * @see Classpath#addCache(File) 
   * @see Classpath#cacheFromDirectory(File, boolean) 
   * @see Classpath#cacheFromDirectory(String, File) 
   */ 
  private void cacheFromLocFile(File root, File locFile) throws IOException { 
    String locPath = locFile.getAbsolutePath().substring(root.getAbsolutePath().length() + 1).replace('\\', '/'); 
 
    URL locURL = locFile.toURL(); 
 
    if (!classPaths.containsKey(locURL)) { 
 
      // Add file URL 
      addToCache(locURL, locPath); 
 
      // Add classpath file 
      addToCache(locURL, root); 
 
      framework.fireFrameworkEvent(IFrameworkEvent.TYPE.DEBUG, "Mapping resource[" + locPath + "] to URL[" + locURL.toString() + "] in directory[" + root.getAbsolutePath() + "]"); 
    } 
  } 
 
  /** 
   * Cache a classpath jar file. 
   *  
   * @param root 
   *            jar file to be cached. 
   * @see Classpath#addCache(File) 
   * @see Classpath#cacheFromDirectory(File, boolean) 
   * @see Classpath#cacheFromDirectory(String, File) 
   */ 
  private void cacheFromJarFile(File root) throws IOException { 
    JarFile file = new JarFile(root); 
    try { 
      Enumeration<JarEntry> entries = file.entries(); 
      while (entries.hasMoreElements()) { 
        JarEntry entry = entries.nextElement(); 
        String jarPath = entry.getName(); 
        URL jarURL = new URL("jar:file:" + root.getAbsolutePath() + "!/" + jarPath); 
 
        // Add jar URL 
        addToCache(jarURL, jarPath); 
 
        // Add classpath file 
        addToCache(jarURL, root); 
 
        // If the entry is a directory, cache it as a package. 
        if (entry.isDirectory()) { 
          addPackage(root, jarPath.substring(0, jarPath.length() - 1).replace('/', '.')); 
        } 
 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.DEBUG, "Mapping resource[" + jarPath + "] to URL[" + jarURL.toString() + "] in jar file[" + root.getAbsolutePath() + "]"); 
      } 
    } finally { 
      try { 
        file.close(); 
      } catch (IOException e) {} 
    } 
  } 
 
  private void addToCache(URL url, String path) { 
    classPaths.put(url, path); 
  } 
 
  private void addToCache(URL url, File path) { 
    classFiles.put(url, path); 
  } 
 
  private void addPackage(File path, String pkg) { 
    Set<File> packageFiles = packages.get(pkg); 
    if (packageFiles == null) { 
      packageFiles = new HashSet<File>(); 
    } 
    if (packageFiles.add(path)) { 
      packages.put(pkg, packageFiles); 
    } 
  } 
 
  private void removePackages(File path) { 
    for (String pkg : packages.keySet()) { 
      Set<File> packageFiles = packages.get(pkg); 
      if (packageFiles != null) { 
        if (packageFiles.remove(path)) { 
          if (packageFiles.isEmpty()) { 
            packages.remove(pkg); 
          } else { 
            packages.put(pkg, packageFiles); 
          } 
        } 
      } 
    } 
  } 
} 
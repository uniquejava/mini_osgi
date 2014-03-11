package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.util.Enumeration; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.Vector; 
import java.util.jar.JarEntry; 
import java.util.jar.JarFile; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IFrameworkEvent; 
 
/** 
 * A classpath cache. To cache all resources, so system mustn't search all jar files or class directories every 
 * findResources invoking. 
 *  
 * @author yipsilon 
 * @since 1.0 
 */ 
class BundleClassPath { 
 
  private Map<URL, String> classPaths; 
 
  // private Framework framework; 
 
  public BundleClassPath(Framework framework, BundleDescriptor descriptor) throws BundleException { 
 
    File bundleFile = new File(descriptor.getLocation()); 
    if (!bundleFile.exists()) { 
      throw new BundleException("Bundle file not exists: " + descriptor.getLocation()); 
    } 
 
    // this.framework = framework; 
    this.classPaths = new HashMap<URL, String>(); 
 
    // Cache bundle resources. 
    try { 
      JarFile bundleJarFile = new JarFile(bundleFile); 
      Enumeration<JarEntry> entryEnum = bundleJarFile.entries(); 
      while (entryEnum.hasMoreElements()) { 
        JarEntry bundleEntry = entryEnum.nextElement(); 
        String entryPath = bundleEntry.getName(); 
        URL entryURL = new URL("jar:file:" + bundleFile.getAbsolutePath() + "!/" + entryPath); 
        classPaths.put(entryURL, entryPath); 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.DEBUG, "Mapping resource[" + entryPath + "] to URL[" + entryURL.toString() + "] in bundle file[" + bundleFile.getAbsolutePath() + "]"); 
      } 
    } catch (IOException e) { 
      throw new BundleException("Can not cache bundle file: " + descriptor.getLocation(), e); 
    } 
 
    // Create the temporary directory. 
    File tmpdir = new File(System.getProperty("java.io.tmpdir") + File.separator + "Bundle_" + descriptor.getSymbolicName() + "_" + descriptor.getRandomId()); 
    if (!tmpdir.exists()) { 
      if (tmpdir.mkdir()) { 
        tmpdir.deleteOnExit(); 
      } 
    } else { 
      tmpdir.deleteOnExit(); 
    } 
 
    // Create the classpath directory. 
    File cpdir = new File(tmpdir.getAbsolutePath() + File.separator + "ws"); 
    if (!cpdir.exists()) { 
      if (cpdir.mkdir()) { 
        cpdir.deleteOnExit(); 
      } 
    } else { 
      cpdir.deleteOnExit(); 
    } 
 
    // Cache classpath resources defined by manifest. 
    for (String classpath : descriptor.getClassPaths()) { 
      try { 
 
        // Create the temporary classpath file. 
        String cpFileName = classpath.substring(classpath.lastIndexOf('/') + 1); 
        File cpFile = new File(cpdir.getAbsolutePath() + File.separator + cpFileName); 
        if (!cpFile.exists()) { 
          if (cpFile.createNewFile()) { 
            cpFile.deleteOnExit(); 
          } 
        } else { 
          cpFile.deleteOnExit(); 
        } 
 
        // Write the jar file to temporary classpath. 
        InputStream inputStream = new URL(classpath).openStream(); 
        FileOutputStream outputStream = new FileOutputStream(cpFile); 
        byte[] cache = new byte[4096]; 
        for (int offset = inputStream.read(cache); offset != -1; offset = inputStream.read(cache)) { 
          outputStream.write(cache, 0, offset); 
        } 
        inputStream.close(); 
        outputStream.close(); 
 
        // Cache current jar file. 
        JarFile jarFile = new JarFile(cpFile); 
        Enumeration<JarEntry> jarEnum = jarFile.entries(); 
        while (jarEnum.hasMoreElements()) { 
          JarEntry entry = jarEnum.nextElement(); 
          String entryPath = entry.getName(); 
          URL entryURL = new URL("jar:file:" + cpFile.getAbsolutePath() + "!/" + entryPath); 
          classPaths.put(entryURL, entryPath); 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.DEBUG, "Mapping resource[" + entryPath + "] to URL[" + entryURL.toString() + "] in bundle file[" + bundleFile.getAbsolutePath() + "]"); 
        } 
        jarFile.close(); 
 
      } catch (MalformedURLException e) { 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Can not add class path[" + classpath + "] to bundle[" + bundleFile.getAbsolutePath() + "]", e); 
        continue; 
      } catch (IOException e) { 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Can not add class path[" + classpath + "] to bundle[" + bundleFile.getAbsolutePath() + "]", e); 
        continue; 
      } 
    } 
  } 
 
  public URL findResource(String name) { 
    for (URL key : classPaths.keySet()) { 
      String value = classPaths.get(key); 
      if (value.equals(name)) { 
        return key; 
      } 
    } 
    return null; 
  } 
 
  public Enumeration<URL> findResources(String name) { 
    Vector<URL> resources = new Vector<URL>(); 
    for (URL key : classPaths.keySet()) { 
      String value = classPaths.get(key); 
      if (value.equals(name)) { 
        resources.add(key); 
      } 
    } 
    if (resources.isEmpty()) { 
      return null; 
    } else { 
      return resources.elements(); 
    } 
  } 
} 
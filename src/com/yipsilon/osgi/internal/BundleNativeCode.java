package com.yipsilon.osgi.internal; 
 
import java.io.File; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.URL; 
import java.util.HashSet; 
import java.util.Set; 
 
import com.yipsilon.osgi.IFrameworkEvent; 
 
class BundleNativeCode { 
 
  private Set<String> nativeCodes; 
 
  public BundleNativeCode(Framework framework, BundleDescriptor descriptor) { 
 
    nativeCodes = new HashSet<String>(); 
 
    // Create the temporary directory. 
    File tmpdir = new File(System.getProperty("java.io.tmpdir") + File.separator + "Bundle_" + descriptor.getSymbolicName() + "_" + descriptor.getRandomId()); 
    if (!tmpdir.exists()) { 
      if (tmpdir.mkdir()) { 
        tmpdir.deleteOnExit(); 
      } 
    } else { 
      tmpdir.deleteOnExit(); 
    } 
 
    // Create the library directory. 
    File ncDir = new File(tmpdir.getAbsolutePath() + File.separator + "os"); 
    if (!ncDir.exists()) { 
      if (ncDir.mkdir()) { 
        ncDir.deleteOnExit(); 
      } 
    } else { 
      ncDir.deleteOnExit(); 
    } 
 
    // Cache the native library file. 
    for (String nativeCode : descriptor.getNativeCodes()) { 
      try { 
        String ncFileName = nativeCode.substring(nativeCode.lastIndexOf('/') + 1); 
        File ncFile = new File(ncDir.getAbsolutePath() + File.separator + ncFileName); 
        if (!ncFile.exists()) { 
          if (ncFile.createNewFile()) { 
            ncFile.deleteOnExit(); 
          } 
        } else { 
          ncFile.deleteOnExit(); 
        } 
 
        // Write the native file to temporary library. 
        InputStream inputStream = new URL(nativeCode).openStream(); 
        FileOutputStream outputStream = new FileOutputStream(ncFile); 
        byte[] cache = new byte[4096]; 
        for (int offset = inputStream.read(cache); offset != -1; offset = inputStream.read(cache)) { 
          outputStream.write(cache, 0, offset); 
        } 
        inputStream.close(); 
        outputStream.close(); 
 
        nativeCodes.add(ncFile.getAbsolutePath()); 
 
      } catch (IOException e) { 
        framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not deploy native code[" + nativeCode + "] for bundle[" + descriptor.getSymbolicName() + "]", e); 
        continue; 
      } 
    } 
  } 
 
  public String findLibrary(String name) { 
    String libName; 
    String osName = System.getProperty("os.name"); 
    if (osName.startsWith("Windows")) { 
      libName = name + ".dll"; 
    } else { 
      libName = name + ".so"; 
    } 
 
    for (String nativeCode : nativeCodes) { 
      if (nativeCode.endsWith(libName)) { 
        return nativeCode; 
      } 
    } 
 
    return null; 
  } 
} 
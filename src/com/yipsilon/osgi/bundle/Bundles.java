package com.yipsilon.osgi.bundle; 
 
import java.io.File; 
import java.util.List; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IBundle; 
 
public class Bundles { 
   
  private File cache; 
 
  private List<IBundle> bundles; 
   
  public Bundles(String cacheDir) { 
    setCache(cacheDir); 
  } 
   
  public IBundle installBundle(String location) throws BundleException{ 
    return null; 
  } 
   
  public void startBundle(IBundle bundle) throws BundleException{} 
   
  public void stopBundle(IBundle bundle) throws BundleException{} 
   
  public void uninstallBundle(IBundle bundle) throws BundleException{} 
   
  private void setCache(String dir) throws IllegalArgumentException{ 
    cache = new File(dir); 
    if (!cache.exists()) { 
      if (!cache.mkdir()) { 
        throw new IllegalArgumentException("Can not make cache directory: " + dir); 
      } 
    } 
    if (!cache.isDirectory()) { 
      throw new IllegalArgumentException("Cache path is not a directory: " + dir); 
    } 
  } 
} 
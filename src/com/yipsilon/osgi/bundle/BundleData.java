package com.yipsilon.osgi.bundle; 
 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.Serializable; 
import java.net.URL; 
import java.net.URLConnection; 
import java.util.HashMap; 
import java.util.Locale; 
import java.util.Map; 
 
class BundleData implements Cloneable, Serializable { 
 
  private static final long serialVersionUID = -5703828725707750218L; 
 
  private File tempDir; 
   
  private File dataDir; 
 
  private String location; 
 
  private long lastModified; 
 
  private String symbolicName; 
 
  private Locale locale; 
 
  private Map<String, String> attributes; 
 
  public BundleData(String cacheLocation, String fileLocation, Locale locale) throws IOException { 
    assert fileLocation == null : "Bundle location is null"; 
    this.attributes = new HashMap<String, String>(); 
 
    // 
    setCache(cacheLocation); 
    setLocale(locale); 
    setLocation(fileLocation); 
  } 
 
  public Map<String, String> getHeaders() { 
    return null; 
  } 
 
  public Map<String, String> getHeaders(Locale locale) { 
    return null; 
  } 
 
  public File getDataFile(String filename) { 
    return null; 
  } 
 
  public String getLocation() { 
    return location; 
  } 
 
  public Locale getLocale() { 
    return this.locale; 
  } 
 
  public long getLastModified() { 
    return lastModified; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see java.lang.Object#clone() 
   */ 
  @Override 
  protected Object clone() throws CloneNotSupportedException { 
    BundleData data = (BundleData) super.clone(); 
    data.attributes = new HashMap<String, String>(attributes); 
    return data; 
  } 
 
  private void setLocale(Locale locale) { 
    if (locale == null) { 
      this.locale = Locale.getDefault(); 
    } else { 
      this.locale = locale; 
    } 
  } 
 
  private void setLocation(String location) throws IOException { 
    File file = new File(location); 
    InputStream stream; 
    if (file.exists()) { 
      this.lastModified = file.lastModified(); 
      stream = new FileInputStream(file); 
    } else { 
      URL url = new URL(location); 
      URLConnection conn = url.openConnection(); 
      this.lastModified = conn.getLastModified(); 
      stream = conn.getInputStream(); 
    } 
    install(stream); 
    stream.close(); 
    this.location = location; 
  } 
 
  private void setCache(String dir) throws IOException { 
 
    File cacheDir = new File(dir); 
    if (!cacheDir.exists()) { 
      if (!cacheDir.mkdir()) { 
        throw new IOException("Can not make cache directory: " + dir); 
      } 
    } else if (!cacheDir.isDirectory()) { 
      throw new IOException("Cache path is not a directory: " + dir); 
    } 
 
    File homeDir = new File(cacheDir.getAbsolutePath() + File.separatorChar + symbolicName); 
    if (!homeDir.exists()) { 
      if (!homeDir.mkdir()) { 
        throw new IOException("Can not make bundle directory: " + homeDir.getAbsolutePath()); 
      } 
    } else if (!homeDir.isDirectory()) { 
      throw new IOException("Bundle path is not a directory: " + homeDir.getAbsolutePath()); 
    } 
 
    File tempDir = new File(homeDir.getAbsolutePath() + File.separatorChar + "temp"); 
    if (!tempDir.exists()) { 
      if (!tempDir.mkdir()) { 
        throw new IOException("Can not make temporary directory: " + tempDir.getAbsolutePath()); 
      } 
    } else if (!tempDir.isDirectory()) { 
      throw new IOException("Temporary path is not a directory: " + tempDir.getAbsolutePath()); 
    } 
    this.tempDir = tempDir; 
 
    File dataDir = new File(homeDir.getAbsolutePath() + File.separatorChar + "data"); 
    if (!dataDir.exists()) { 
      if (!dataDir.mkdir()) { 
        throw new IOException("Can not make data directory: " + dataDir.getAbsolutePath()); 
      } 
    } else if (!dataDir.isDirectory()) { 
      throw new IOException("Data path is not a directory: " + dataDir.getAbsolutePath()); 
    } 
    this.dataDir = dataDir; 
     
  } 
 
  /** 
   * This method is invoked while installing bundle. 
   *  
   * @param stream 
   * @throws IOException 
   */ 
  private void install(InputStream stream) throws IOException {} 
 
  /** 
   * This method is invoked while resolving bundle. 
   *  
   * @throws IOException 
   */ 
  private void checkData() throws IOException {} 
 
  /** 
   * This method is invoked while uninstalling bundle. 
   *  
   * @param stream 
   * @throws IOException 
   */ 
  private void uninstall() throws IOException {} 
} 
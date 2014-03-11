package com.yipsilon.osgi.services; 
 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.net.URLConnection; 
import java.util.Map; 
 
import com.yipsilon.osgi.BundleException; 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleConstants; 
import com.yipsilon.osgi.internal.Framework; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
class AutoUpdate implements IAutoUpdate { 
 
  private Framework framework; 
 
  private boolean enabled; 
 
  private TYPE type; 
 
  private Thread monitorThread; 
 
  private BundleMonitor monitor; 
 
  public AutoUpdate(Framework framework) { 
    this.framework = framework; 
    this.enabled = false; 
    this.type = TYPE.LOCAL; 
    this.monitor = new BundleMonitor(); 
    this.monitorThread = new Thread(monitor); 
 
    this.monitorThread.setDaemon(true); 
    this.monitorThread.setPriority(Thread.MIN_PRIORITY); 
    this.monitorThread.start(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IAutoUpdate#getEnabled() 
   */ 
  public boolean getEnabled() { 
    return enabled; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IAutoUpdate#getType() 
   */ 
  public TYPE getType() { 
    return type; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IAutoUpdate#setEnabled(boolean) 
   */ 
  public void setEnabled(boolean enabled) { 
    this.enabled = enabled; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IAutoUpdate#setType(com.yipsilon.osgi.services.IAutoUpdate.TYPE) 
   */ 
  public void setType(TYPE type) { 
    this.type = type; 
  } 
 
  private class BundleMonitor implements Runnable { 
 
    /* 
     * (non-Javadoc) 
     *  
     * @see java.lang.Runnable#run() 
     */ 
    public void run() { 
      while (true) { 
        if (enabled) { 
 
          for (IBundle bundle : framework.getBundles()) { 
 
            // Find the update path. 
            String path = null; 
            switch (type) { 
              case LOCAL: 
                path = "file:/" + bundle.getLocation(); 
                break; 
              case REMOTE: 
                Map<String, String> headers = bundle.getHeaders(); 
                path = headers.get(IBundleConstants.MANIFEST_BUNDLE_UPDATELOCATION); 
                break; 
            } 
 
            if (path == null || path.trim().equals("")) { 
              try { 
                URL url = new URL(path); 
                URLConnection con = url.openConnection(); 
                if (bundle.getLastModified() != con.getLastModified()) { 
                  InputStream input = con.getInputStream(); 
                  bundle.update(input); 
                  input.close(); 
                } 
              } catch (MalformedURLException e) { 
                // framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not resolve bundle update location: " + 
                // path, e); 
                e.printStackTrace(); 
                continue; 
              } catch (IOException e) { 
                // framework.fireFrameworkEvent(IFrameworkEvent.TYPE.ERROR, "Can not update bundle via location: " + 
                // path, e); 
                e.printStackTrace(); 
                continue; 
              } catch (BundleException e) { 
                continue; 
              } 
            } 
          } 
        } 
 
        try { 
          Thread.sleep(1000); 
        } catch (InterruptedException e) { 
          return; 
        } 
      } 
    } 
  } 
} 
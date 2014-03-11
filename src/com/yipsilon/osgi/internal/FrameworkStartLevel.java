package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.services.IStartLevel; 
 
class FrameworkStartLevel implements IStartLevel { 
 
  private Framework framework; 
 
  public FrameworkStartLevel(Framework framework) { 
    this.framework = framework; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#getBundleStartLevel(com.yipsilon.osgi.IBundle) 
   */ 
  public int getBundleStartLevel(IBundle b) { 
    return framework.getBundleStartLevel(b); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#getInitialBundleStartLevel() 
   */ 
  public int getInitialBundleStartLevel() { 
    return framework.getInitialBundleStartLevel(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#getStartLevel() 
   */ 
  public int getStartLevel() { 
    return framework.getStartLevel(); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#isBundlePersistentlyStarted(com.yipsilon.osgi.IBundle) 
   */ 
  public boolean isBundlePersistentlyStarted(IBundle bundle) { 
    return framework.isBundlePersistentlyStarted(bundle); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#setBundleStartLevel(com.yipsilon.osgi.IBundle, int) 
   */ 
  public void setBundleStartLevel(IBundle bundle, int startlevel) { 
    framework.setBundleStartLevel(bundle, startlevel); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#setInitialBundleStartLevel(int) 
   */ 
  public void setInitialBundleStartLevel(int startlevel) { 
    framework.setInitialBundleStartLevel(startlevel); 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.services.IStartLevel#setStartLevel(int) 
   */ 
  public void setStartLevel(int startlevel) { 
    framework.setStartLevel(startlevel); 
  } 
} 
package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IBundleEvent; 
 
class BundleEvent implements IBundleEvent { 
 
  private TYPE type; 
 
  private IBundle bundle; 
 
  public BundleEvent(TYPE type, IBundle bundle) { 
    assert type == null; 
    assert bundle == null; 
    this.type = type; 
    this.bundle = bundle; 
  } 
 
  public IBundle getBundle() { 
    return bundle; 
  } 
 
  public TYPE getType() { 
    return type; 
  } 
 
} 
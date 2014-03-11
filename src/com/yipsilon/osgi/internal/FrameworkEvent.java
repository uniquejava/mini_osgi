package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.IBundle; 
import com.yipsilon.osgi.IFrameworkEvent; 
 
class FrameworkEvent implements IFrameworkEvent { 
 
  private IBundle bundle; 
 
  private Throwable throwable; 
 
  private String message; 
 
  private TYPE type; 
 
  public FrameworkEvent(TYPE type, IBundle bundle) { 
    this(type, bundle, null, null); 
  } 
 
  public FrameworkEvent(TYPE type, String message) { 
    this(type, null, message, null); 
  } 
 
  public FrameworkEvent(TYPE type, Throwable throwable) { 
    this(type, null, null, throwable); 
  } 
 
  public FrameworkEvent(TYPE type, String message, Throwable throwable) { 
    this(type, null, message, throwable); 
  } 
 
  public FrameworkEvent(TYPE type, IBundle bundle, String message) { 
    this(type, bundle, message, null); 
  } 
 
  public FrameworkEvent(TYPE type, IBundle bundle, Throwable throwable) { 
    this(type, bundle, null, throwable); 
  } 
 
  public FrameworkEvent(TYPE type, IBundle bundle, String message, Throwable throwable) { 
    assert type == null; 
    this.type = type; 
    this.bundle = bundle; 
    this.message = message; 
    this.throwable = throwable; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFrameworkEvent#getBundle() 
   */ 
  public IBundle getBundle() { 
    return bundle; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFrameworkEvent#getThrowable() 
   */ 
  public Throwable getThrowable() { 
    return throwable; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFrameworkEvent#getType() 
   */ 
  public TYPE getType() { 
    return type; 
  } 
 
  /* 
   * (non-Javadoc) 
   *  
   * @see com.yipsilon.osgi.IFrameworkEvent#getMessage() 
   */ 
  public String getMessage() { 
    return message; 
  } 
 
} 
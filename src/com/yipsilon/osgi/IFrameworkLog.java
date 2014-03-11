package com.yipsilon.osgi; 
 
public interface IFrameworkLog { 
 
  public abstract void debug(Object message, Throwable t); 
 
  public abstract void debug(Object message); 
 
  public abstract void error(Object message, Throwable t); 
 
  public abstract void error(Object message); 
 
  public abstract void fatal(Object message, Throwable t); 
 
  public abstract void fatal(Object message); 
 
  public abstract void info(Object message, Throwable t); 
 
  public abstract void info(Object message); 
 
  public abstract void trace(Object message, Throwable t); 
 
  public abstract void trace(Object message); 
 
  public abstract void warn(Object message, Throwable t); 
 
  public abstract void warn(Object message); 
 
}
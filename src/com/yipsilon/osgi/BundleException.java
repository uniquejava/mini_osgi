package com.yipsilon.osgi; 
 
/** 
 * A Framework exception used to indicate that a bundle lifecycle problem occurred. 
 * &lt;p> 
 * &lt;code>BundleException&lt;/code> object is created by the Framework to denote an exception condition in the lifecycle 
 * of a bundle. &lt;code>BundleException&lt;/code>s should not be created by bundle developers. 
 * &lt;p> 
 * This exception is updated to conform to the general purpose exception chaining mechanism. 
 */ 
public class BundleException extends Exception { 
 
  private static final long serialVersionUID = 9122050733014890599L; 
 
  public BundleException(String message) { 
    super(message); 
  } 
 
  public BundleException(String message, Throwable cause) { 
    super(message, cause); 
  } 
 
} 
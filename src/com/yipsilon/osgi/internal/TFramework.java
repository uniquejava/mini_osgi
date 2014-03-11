package com.yipsilon.osgi.internal;

import java.net.URL;

import junit.framework.TestCase;

import com.yipsilon.osgi.IFrameworkEvent;
import com.yipsilon.osgi.IFrameworkListener;

public class TFramework extends TestCase {      
     
  private Framework framework;      
     
  public void setUp() {      
    framework = new Framework();      
    framework.addFrameworkListener(new IFrameworkListener() {      
      public void frameworkEvent(IFrameworkEvent event) {      
        if (event.getMessage() != null) {      
          System.out.println(event.getMessage());      
        }      
        if (event.getThrowable() != null) {      
          event.getThrowable().printStackTrace();      
        }      
      }      
    });      
  }      
     
  public void tearDown() {      
    framework = null;      
    System.out.println();      
  }      
     
  public void testBundle() throws Exception {      
     
    framework.addClassPath("C:\\osgi\\ws\\commons-logging.jar");      
    framework.addLibraryPath("C:\\osgi\\os");      
     
    // ----------------------------------------------   
    // Bundle-SymbolicName: com.yipsilon.osgi.test   
    // Bundle-Version: 1.0.0.20061212   
    // Bundle-NativeCode: swt-gdip-win32-3235.dll,swt-awt-win32-3235.dll,swt-wgl-win32-3235.dll,swt-win32-3235.dll   
    // Bundle-Activator: com.yipsilon.osgi.test.Activator   
    // Export-Package: com.yipsilon.osgi.test   
    // Import-Package: org.apache.commons.logging   
    // Bundle-ClassPath: swt.3.2.1.v3235.jar   
    // ----------------------------------------------   
  
    framework.installBundle("C:\\osgi\\test.jar");      
     
    ClassLoader cl = framework.getClassLoader();      
     
    URL explicitURL = cl.getResource("com/yipsilon/osgi/test/Test.class");      
    URL implicitURL = cl.getResource("com/yipsilon/osgi/test1/Hello.class");      
    URL externalURL = cl.getResource("org/apache/commons/logging/LogFactory.class");      
     
    System.out.println("implicitURL: " + (implicitURL != null));  // Returns true      
    System.out.println("explicitURL: " + (explicitURL != null)); // Returns true      
    System.out.println("externalURL: " + (externalURL != null)); // Returns true      
     
    Class explicitClass = cl.loadClass("com.yipsilon.osgi.test.Test");      
    Class implicitClass = cl.loadClass("com.yipsilon.osgi.test1.Hello");      
    Class externalClass = cl.loadClass("org.apache.commons.logging.LogFactory");      
     
    System.out.println("implicitClass: " + (implicitClass != null)); // Returns false      
    System.out.println("explicitClass: " + (explicitClass != null)); // Returns true      
    System.out.println("externalClass: " + (externalClass != null)); // Returns true      
  }      
}  
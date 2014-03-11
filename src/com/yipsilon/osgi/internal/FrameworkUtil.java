package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.IBundle; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
public class FrameworkUtil { 
 
  /** 
   *  
   */ 
  private FrameworkUtil() {} 
   
  public static boolean isAssignableTo(IBundle bundle1, IBundle bundle2, String className) { 
 
    Bundle b1 = null; 
    Bundle b2 = null; 
 
    if (bundle1 instanceof Bundle) { 
      b1 = (Bundle) bundle1; 
    } 
 
    if (bundle2 instanceof Bundle) { 
      b2 = (Bundle) bundle2; 
    } 
 
    if (b1 == null || b2 == null) { 
      return false; 
    } 
 
    String packageName = className.indexOf('.') > 0 ? className.substring(0, className.indexOf('.')) : ""; 
    FrameworkClassLoader fcl = b1.getFramework().getClassLoader(); 
 
    BundleClassLoader bcl1 = fcl.getBundleClassLoader(b1.getDescriptor()); 
    if (!bcl1.isPackageExposed(packageName)) { 
      if (b1.equals(b2)) { 
        return true; 
      } else { 
        return false; 
      } 
    } 
 
    BundleClassLoader bcl2 = fcl.getBundleClassLoader(b2.getDescriptor()); 
    if (bcl2.isPackageExposed(packageName)) { 
      return true; 
    } else { 
      return false; 
    } 
  } 
} 
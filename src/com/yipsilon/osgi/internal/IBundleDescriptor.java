package com.yipsilon.osgi.internal; 
 
import com.yipsilon.osgi.IBundleVersion; 
 
interface IBundleDescriptor { 
 
  public String getName(); 
 
  public IBundleVersion getVersion(); 
 
  public String getVendor(); 
 
  public String[] getClassPaths(); 
 
  public String[] getNativeCodes(); 
 
  public String[] getExportedPackages(); 
 
  public String[] getImportedPackages(); 
 
  public String[] getRequiredBundles(); 
 
  public String getUpdateLocation(); 
 
  public String getDocumentLocation(); 
 
  public String getContactAddress(); 
 
  public String getCopyright(); 
 
  public String getDescription(); 
 
  public String[] getCategories(); 
 
  public boolean isAutoStart(); 
 
  public int getStartLevel(); 
} 
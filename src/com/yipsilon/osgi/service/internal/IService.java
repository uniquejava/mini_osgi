package com.yipsilon.osgi.service.internal; 
 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IAllServiceListener; 
import com.yipsilon.osgi.service.IServiceReference; 
 
public interface IService { 
 
  public abstract void addServiceListener(IAllServiceListener listener); 
 
  public abstract void removeServiceListener(IAllServiceListener listener); 
 
  public abstract IServiceReference[] getAllServiceReferences(String clazz, String filterstring) throws InvalidSyntaxException; 
 
  public abstract Object getService(IServiceReference reference); 
}
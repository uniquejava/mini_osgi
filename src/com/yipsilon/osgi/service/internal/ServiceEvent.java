package com.yipsilon.osgi.service.internal; 
 
import com.yipsilon.osgi.service.IServiceEvent; 
import com.yipsilon.osgi.service.IServiceReference; 
 
class ServiceEvent implements IServiceEvent { 
 
  private TYPE type; 
 
  private IServiceReference reference; 
 
  public ServiceEvent(TYPE type, IServiceReference reference) { 
    this.type = type; 
    this.reference = reference; 
  } 
 
  public IServiceReference getServiceReference() { 
    return reference; 
  } 
 
  public TYPE getType() { 
    return type; 
  } 
 
} 
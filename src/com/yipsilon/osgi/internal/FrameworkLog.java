package com.yipsilon.osgi.internal; 
 
import java.lang.reflect.InvocationTargetException; 
import java.lang.reflect.Method; 
import java.util.ArrayList; 
import java.util.List; 
 
import com.yipsilon.osgi.IFrameworkEvent; 
import com.yipsilon.osgi.IFrameworkLog; 
import com.yipsilon.osgi.InvalidSyntaxException; 
import com.yipsilon.osgi.service.IServiceReference; 
import com.yipsilon.osgi.service.IServiceRegistration; 
import com.yipsilon.osgi.services.ILogger; 
 
/** 
 * @author Administrator 
 * @since 1.0 
 */ 
class FrameworkLog implements IFrameworkLog { 
 
  private Framework framework; 
 
  public FrameworkLog(Framework framework) { 
    this.framework = framework; 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#debug(java.lang.Object, java.lang.Throwable) 
   */ 
  public void debug(Object message, Throwable t) { 
    for (ILogger service : getServices()) { 
      if (service.isDebugEnabled()) { 
        service.debug(message, t); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#debug(java.lang.Object) 
   */ 
  public void debug(Object message) { 
    for (ILogger service : getServices()) { 
      if (service.isDebugEnabled()) { 
        service.debug(message); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#error(java.lang.Object, java.lang.Throwable) 
   */ 
  public void error(Object message, Throwable t) { 
    for (ILogger service : getServices()) { 
      if (service.isErrorEnabled()) { 
        service.error(message, t); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#error(java.lang.Object) 
   */ 
  public void error(Object message) { 
    for (ILogger service : getServices()) { 
      if (service.isErrorEnabled()) { 
        service.error(message); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#fatal(java.lang.Object, java.lang.Throwable) 
   */ 
  public void fatal(Object message, Throwable t) { 
    for (ILogger service : getServices()) { 
      if (service.isFatalEnabled()) { 
        service.fatal(message, t); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#fatal(java.lang.Object) 
   */ 
  public void fatal(Object message) { 
    for (ILogger service : getServices()) { 
      if (service.isFatalEnabled()) { 
        service.fatal(message); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#info(java.lang.Object, java.lang.Throwable) 
   */ 
  public void info(Object message, Throwable t) { 
    for (ILogger service : getServices()) { 
      if (service.isInfoEnabled()) { 
        service.info(message, t); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#info(java.lang.Object) 
   */ 
  public void info(Object message) { 
    for (ILogger service : getServices()) { 
      if (service.isInfoEnabled()) { 
        service.info(message); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#trace(java.lang.Object, java.lang.Throwable) 
   */ 
  public void trace(Object message, Throwable t) { 
    for (ILogger service : getServices()) { 
      if (service.isTraceEnabled()) { 
        service.trace(message, t); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#trace(java.lang.Object) 
   */ 
  public void trace(Object message) { 
    for (ILogger service : getServices()) { 
      if (service.isTraceEnabled()) { 
        service.trace(message); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#warn(java.lang.Object, java.lang.Throwable) 
   */ 
  public void warn(Object message, Throwable t) { 
    for (ILogger service : getServices()) { 
      if (service.isWarnEnabled()) { 
        service.warn(message, t); 
      } 
    } 
  } 
 
  /* (non-Javadoc) 
   * @see com.yipsilon.osgi.internal.IFrameworkLog#warn(java.lang.Object) 
   */ 
  public void warn(Object message) { 
    for (ILogger service : getServices()) { 
      if (service.isWarnEnabled()) { 
        service.warn(message); 
      } 
    } 
  } 
 
  private ILogger[] getServices() { 
    IServiceReference[] references; 
 
    try { 
      references = framework.getAllServiceReferences(ILogger.class.getName(), null); 
    } catch (InvalidSyntaxException e) { 
      references = null; 
    } 
 
    if (references != null) { 
      List<ILogger> serviceList = new ArrayList<ILogger>(references.length); 
      for (int i = 0, len = references.length; i < len; i++) { 
        IServiceRegistration registration = references[i].getRegistration(); 
 
        try { 
          Method method = registration.getClass().getMethod("getService"); 
 
          method.setAccessible(true); 
 
          Object service = method.invoke(registration); 
 
          if (service instanceof ILogger) { 
            serviceList.add((ILogger) service); 
          } else { 
            framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Service object is not a " + ILogger.class.getName() + " implementation: " + service.getClass().getName()); 
          } 
        } catch (SecurityException e) { 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Can not access log service object", e); 
        } catch (NoSuchMethodException e) { 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Can not access log service object", e); 
        } catch (IllegalAccessException e) { 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Can not access log service object", e); 
        } catch (InvocationTargetException e) { 
          framework.fireFrameworkEvent(IFrameworkEvent.TYPE.WARNING, "Can not access log service object", e.getTargetException()); 
        } 
      } 
 
      return serviceList.toArray(ILogger.EMPTY_SERVICE_ARRAY); 
    } else { 
      return ILogger.EMPTY_SERVICE_ARRAY; 
    } 
  } 
} 
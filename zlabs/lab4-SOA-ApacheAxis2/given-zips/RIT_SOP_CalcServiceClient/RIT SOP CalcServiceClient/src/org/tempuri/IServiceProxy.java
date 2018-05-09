package org.tempuri;

public class IServiceProxy implements org.tempuri.IService {
  private String _endpoint = null;
  private org.tempuri.IService iService = null;
  
  public IServiceProxy() {
    _initIServiceProxy();
  }
  
  public IServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIServiceProxy();
  }
  
  private void _initIServiceProxy() {
    try {
      iService = (new org.tempuri.ServiceLocator()).getBasicHttpBinding_IService();
      if (iService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iService != null)
      ((javax.xml.rpc.Stub)iService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.IService getIService() {
    if (iService == null)
      _initIServiceProxy();
    return iService;
  }
  
  public java.lang.Double add(java.lang.Double value1, java.lang.Double value2) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.add(value1, value2);
  }
  
  public java.lang.Double subtract(java.lang.Double value1, java.lang.Double value2) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.subtract(value1, value2);
  }
  
  public java.lang.Double multiple(java.lang.Double value1, java.lang.Double value2) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.multiple(value1, value2);
  }
  
  public java.lang.Double divide(java.lang.Double value1, java.lang.Double value2) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.divide(value1, value2);
  }
  
  
}
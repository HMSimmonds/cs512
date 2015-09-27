package client;

import server.MiddlewareImplService;

import java.net.URL;
import java.net.MalformedURLException;


public class WSClient {

    MiddlewareImplService service;
    
    ResourceManager proxy;

    public WSClient(String serviceName, String serviceHost, int servicePort)
    throws MalformedURLException {
    
        URL wsdlLocation = new URL("http", serviceHost, servicePort, 
                "/" + serviceName + "/service?wsdl");
                
        service = new MiddlewareImplService(wsdlLocation);
        
        proxy = service.getResourceManagerImplPort();
    }

}

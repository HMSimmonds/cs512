package server.ws;

import server.ResourceManagerImpl;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.LifecycleException;
import java.io.File;


public class Main {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 1) {
            System.out.println(
                    "Usage: java Main <service-port>");
            System.exit(-1);
        }

        System.out.println("Creating resource manager with service port : " + Integer.parseInt(args[0]));
        ResourceManagerImpl resourceManager = new ResourceManagerImpl(Integer.parseInt(args[0]));
    }
}
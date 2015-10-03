package server.ws;

import server.ResourceManagerImpl;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.LifecycleException;
import java.io.File;


public class Main {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 3) {
            System.out.println(
                    "Usage: java Main <service-port1> <service-port2> <service-port3>");
            System.exit(-1);
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("Creating resource manager with service port : " + Integer.parseInt(args[i]));
            ResourceManagerImpl resourceManager = new ResourceManagerImpl(Integer.parseInt(args[i]));
        }

    }
}
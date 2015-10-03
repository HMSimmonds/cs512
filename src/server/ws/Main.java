package server.ws;

import server.ResourceManagerImpl;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.LifecycleException;
import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 3) {
            System.out.println(
                    "Usage: java Main <service-port1> <service-port2> <service-port3>");
            System.exit(-1);
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 3; i++) {
            executor.execute(new Thread(new ServerCreator(args[i])));
        }
    }
}


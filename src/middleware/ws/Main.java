package middleware.ws;

import middleware.MiddlewareImpl;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

public class Main {
    public static void main(String[] args)
            throws Exception {

        if (args.length != 7) {
            System.out.println(
                    "Usage: java Main <middleware-port> <rm1-address> <rm1-port> <rm2-address> <rm2-port> <rm3-address> <rm3-port>");
            System.exit(-1);
        }

        int portNum = Integer.parseInt(args[0]);
        String ads1 = args[1];
        int p1 = Integer.parseInt(args[2]);

        String ads2 = args[3];
        int p2 = Integer.parseInt(args[4]);

        String ads3 = args[5];
        int p3 = Integer.parseInt(args[6]);

        MiddlewareImpl middleware = new MiddlewareImpl(portNum, ads1, p1, ads2, p2, ads3, p3);
        //now start up middleware
        middleware.initializeMiddleware();
    }
}
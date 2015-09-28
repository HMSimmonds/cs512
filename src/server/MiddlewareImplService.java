package server;

import java.net.URL;

/**
 * Created by hmsimmonds on 15-09-27.
 */
public class MiddlewareImplService {


    private URL url;
    private MiddlewareImpl middlewareImpl;

    public MiddlewareImplService(URL url) {
        //TODO: Create Connection to Middleware instance - by provided connection

        this.url = url;
    }

    public MiddlewareImpl getMiddlewareImplPort() {
        return this.middlewareImpl;
    }



}

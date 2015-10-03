package server.ws;

import server.ResourceManagerImpl;

/**
 * Created by hmsimmonds on 15-10-03.
 */
public class ServerCreator implements Runnable {
    private int portNum;

    public ServerCreator(String portNum) {
        this.portNum = Integer.parseInt(portNum);
    }

    @Override
    public void run() {
        System.out.println("Creating resource manager with service port : " + portNum);
        ResourceManagerImpl resourceManager = new ResourceManagerImpl(portNum);
    }
}
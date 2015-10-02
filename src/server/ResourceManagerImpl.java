// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package server;

import client.TCPPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ResourceManagerImpl {

    //0 = GET, 1 = ADD, 2 = DELETE, 3 = RESERVE
    private static final int GET = 0;
    private static final int ADD = 1;
    private static final int DELETE = 2;
    private static final int RESERVE = 3;

    //connect for server
    private ServerSocket resourceManagerSocket;

    private HashMap<String, ReservableItem> storage;

    public ResourceManagerImpl(int portNum) {
        storage = new HashMap<>();

        try {
            resourceManagerSocket = new ServerSocket(portNum);
            System.out.println("Created resource manager server socket at port : " +
                    portNum);

            while (true) {
                //this will run while we accept a connection from the middleware
                final Socket middlewareSocket = resourceManagerSocket.accept();

                //this will then process the connection to the middleware
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectOutputStream outputStream = new ObjectOutputStream(middlewareSocket.getOutputStream());
                            ObjectInputStream inputStream = new ObjectInputStream(middlewareSocket.getInputStream());
                            System.out.println("Created connection with middlewareSocket at " + middlewareSocket.getRemoteSocketAddress());

                            while (true) {
                                TCPPacket input = (TCPPacket) inputStream.readObject();
                                TCPPacket response = processRequest(input);
                                outputStream.writeObject(response);
                            }

                        } catch (IOException ex) {
                            System.out.println(ex);
                        } catch (ClassNotFoundException ex) {
                            System.out.println(ex);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                }).run();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /*
    Will process the request and then return
    Need to unpackage packet, and then format to send back
     */
    private TCPPacket processRequest(TCPPacket packet) {
        TCPPacket returnPacket = null;
        boolean isValidResponse = true;


        if (packet.actionType == GET) {
            ReservableItem itm = getItem(packet.id, packet.itemKey);
            if (itm == null) {
                //failed response - invalid
                isValidResponse = false;
            }
            else {

            }

        } else if (packet.actionType == ADD) {

        } else if (packet.actionType == DELETE) {

        } else if (packet.actionType == RESERVE) {

        } else {
            //ERROR
            System.out.println("Error when processing request " + packet);
        }

    }

}

    

    

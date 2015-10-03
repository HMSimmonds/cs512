package middleware;

import client.TCPPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MiddlewareImpl {

    private static final int NUM_OF_RESOURCE_MANAGERS = 3;
    private static final int CAR_INDEX = 0;
    private static final int FLIGHT_INDEX = 1;
    private static final int ROOM_INDEX = 2;

    private static final int HELLO = 0;

    private Socket[] resourceManagerSockets = new Socket[3];
    private ServerSocket middlewareSocket;

    private ObjectOutputStream[] outputStreams = new ObjectOutputStream[3];
    private ObjectInputStream[] inputStreams = new ObjectInputStream[3];

    public MiddlewareImpl(int portNum,
                          String carHostName, int carPortNum,
                          String flightHostName, int flightPortNum,
                          String roomHostName, int roomPortNum) {
        try {
            //create middleware socket
            middlewareSocket = new ServerSocket(portNum);
            System.out.println("Middleware Server Socket Created. Port : " + portNum);

            //create RM sockets
            resourceManagerSockets[CAR_INDEX] = new Socket(carHostName, carPortNum);
            System.out.println("Car Resource Server Socket Created. Port : " + carPortNum);
            resourceManagerSockets[FLIGHT_INDEX] = new Socket(flightHostName, flightPortNum);
            System.out.println("Flight Resource Server Socket Created. Port : " + flightPortNum);
            resourceManagerSockets[ROOM_INDEX] = new Socket(roomHostName, roomPortNum);
            System.out.println("Room Resource Server Socket Created. Port : " + roomPortNum);


            for (int i = 0; i < NUM_OF_RESOURCE_MANAGERS; i++) {
                outputStreams[i] = new ObjectOutputStream(resourceManagerSockets[i].getOutputStream());
                System.out.println("Output Stream created for index " + i);
                inputStreams[i] = new ObjectInputStream(resourceManagerSockets[i].getInputStream());
                System.out.println("Input stream created for index " + i);
            }

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    //start up middleware
    public void initializeMiddleware() {
        try {
            System.out.println("Starting up middleware server");
            while (true) {
                //wait to accept connections
                Socket socket = middlewareSocket.accept();

                new Thread(new MiddlewareWorker(socket)).run();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    //will process request and return appropriate value
    private TCPPacket processRequest(TCPPacket packet) {
        System.out.println("Forwarding request of Type : " + packet.type + " with ItemType: "
                            + packet.itemType + " with ActionType: " + packet.actionType);

        //HELLO - connection trying to be established or redundant command
        if (packet.type == 0) return packet;

        //NOT HELLO -> process
        TCPPacket returnPacket = null;
        try {
            //write object to output stream (server)
            outputStreams[packet.itemType].writeObject(packet);
            System.out.println("Waiting for response from resource manager");
            //read object from server and then return
            returnPacket = (TCPPacket) inputStreams[packet.itemType].readObject();
            System.out.println("Received response back from resource manager");
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnPacket;
        }
    }

    //TO DO : reserve Itinerary


    public class MiddlewareWorker implements Runnable {

        private Socket socket;

        public MiddlewareWorker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

                TCPPacket receipt = new TCPPacket();
                receipt.id = 0;
                receipt.type = 0;
                outputStream.writeObject(receipt);

                //Now wait to get the response
                TCPPacket response = (TCPPacket) inputStream.readObject();

                //If the response is a connection "HELLO"
                if (response.type == HELLO) {
                    outputStream.writeObject(response);
                    System.out.println("Connected created with Middleware");
                }


                while (true) {
                    TCPPacket request = (TCPPacket) inputStream.readObject();
                    //Now process request
                    TCPPacket processedRequest = processRequest(request);
                    //write back to client
                    outputStream.writeObject(processedRequest);
                }
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
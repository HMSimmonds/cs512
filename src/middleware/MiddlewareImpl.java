package middleware;

import client.TCPPacket;
import server.Customer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

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

                ExecutorService executor = Executors.newCachedThreadPool();
                executor.execute(new Thread(new MiddlewareWorker(socket)));
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
        if (packet.itemType == 4) return reserveItinerary(packet);
        //customer - send packet to all three Rm's
        if (packet.itemType == 3) {
            
        }

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

    //reserveItinerary - to forward to all RM's
    private TCPPacket reserveItinerary(TCPPacket packet) {
        boolean isValidResponse = false;
        TCPPacket response = new TCPPacket();

        //If has a car - reserve at location
        if (packet.car) {
            TCPPacket carRequest = new TCPPacket();
            try {
                carRequest.id = packet.id;
                carRequest.type = 1;
                carRequest.actionType = 3;
                carRequest.itemType = 0;
                carRequest.itemKey = packet.itemKey;
                carRequest.customerId = packet.customerId;
                carRequest.totalCount = packet.totalCount;

                //Send Car request
                outputStreams[CAR_INDEX].writeObject(carRequest);

                //wait for response
                TCPPacket carResponse = (TCPPacket) inputStreams[CAR_INDEX].readObject();
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                System.out.println("Reserved car for Itinerary");
            }
        }

        if (packet.room) {
            TCPPacket roomRequest = new TCPPacket();
            try {
                roomRequest.id = packet.id;
                roomRequest.itemKey = packet.itemKey;
                roomRequest.type = 1;
                roomRequest.actionType = 3;
                roomRequest.totalCount = 1;
                roomRequest.customerId = packet.customerId;
                roomRequest.itemType = 2;

                //Send room request
                outputStreams[ROOM_INDEX].writeObject(roomRequest);

                //Wait for response
                TCPPacket roomResponse = (TCPPacket) inputStreams[ROOM_INDEX].readObject();
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                System.out.println("Reserved room for Itinerary");
            }
        }

        //try to book all flights
        if (packet.flights != null) {
            Iterator it = packet.flights.iterator();
            while (it.hasNext()) {
                TCPPacket flightRequest = new TCPPacket();
                try {
                    flightRequest.id = packet.id;
                    flightRequest.type = 1;
                    flightRequest.itemKey = (String)it.next();
                    flightRequest.itemType = 1;
                    flightRequest.actionType = 3;
                    flightRequest.customerId = packet.customerId;

                    //send flights request
                    outputStreams[FLIGHT_INDEX].writeObject(flightRequest);

                    //wait for response
                    TCPPacket flightsResponse = (TCPPacket) inputStreams[FLIGHT_INDEX].readObject();
                    isValidResponse = flightsResponse.isValid;
                } catch (IOException ex) {
                    System.out.println(ex);
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex);
                } catch (Exception ex) {
                    System.out.println(ex);
                } finally {
                    if (isValidResponse) {
                        System.out.println("Reserved flights for Itinerary");
                    }
                }
            }
        }
        response.isValid = isValidResponse;
        System.out.println("Reserved Itinerary returned " + isValidResponse);
        return response;
    }



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
                    System.out.println("Connection created with Middleware");
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
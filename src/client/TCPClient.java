package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by hmsimmonds on 15-10-02.
 *
 *
 * Class is for client to extend - will implement methods using TCP Protocol
 *
 */
public class TCPClient {

    //create streams to receive input and output
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private int numOfReceipts = 0;

    private Socket tcpSocket;

    public TCPClient(String host, int port) {
        try {
            //create socket for client
            tcpSocket = new Socket(host, port);

            //create streams
            outputStream = new ObjectOutputStream(tcpSocket.getOutputStream());
            inputStream = new ObjectInputStream(tcpSocket.getInputStream());

            while (true) {
                //loop while waiting to read input objects
                TCPPacket inputPacket = (TCPPacket) inputStream.readObject();

                //if the response is a receiptResponse for connection
                if (inputPacket.type == 0) {

                    //this is the first receipt for connection
                    if (numOfReceipts < 1) {
                        //Respond to connection with "HELLO" Message
                        TCPPacket responsePacket = new TCPPacket();
                        //assign it to a receipt Type
                        responsePacket.type = 0;
                        outputStream.writeObject(responsePacket);
                        ++numOfReceipts;
                    } else {
                        System.out.println("Connection created with Middleware");
                        //no longer waiting for connection;
                        break;
                    }
                    //the response is a message Response
                }
            }


        } catch (IOException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    //-------------FLIGHT OPERATIONS--------------//

    //ADD FLIGHT //
    public boolean addFlight(int id, int flightNumber, int numSeats, int flightPrice) {
        boolean returnValue = false;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 1;
            request.itemType = 1;

            request.id = id;
            request.itemKey = String.valueOf(flightNumber);
            request.totalCount = numSeats;
            request.itemPrice = flightPrice;

            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;
            System.out.println("AddFlight : id " + id + ": flight number : " + flightNumber +
                    " : seat number " + numSeats + " : flightPrice : " + flightPrice + "Returned : " + returnValue);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //DELETE FLIGHT//
    public boolean deleteFlight(int id, int flightNumber) {
        boolean returnValue = false;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 2;
            request.itemType = 1;

            request.id = id;
            request.itemKey = String.valueOf(flightNumber);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;

            System.out.println("DeleteFlight : id " + id + ": flight number : " + flightNumber +
                    " Returned : " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //QUERY FLIGHT//
    public int queryFlight(int id, int flightNumber) {
        int returnValue = 0;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 0;
            request.itemType = 1;

            request.id = id;
            request.itemKey = String.valueOf(flightNumber);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();

            returnValue = response.totalCount - response.count;

            System.out.println("QueryFlight for id: " + id + " Flight Number: " + flightNumber
                    + " Returned " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //QUERY FLIGHT PRICE//
    public int queryFlightPrice(int id, int flightNumber) {
        int returnPrice = 0;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 0;
            request.itemType = 1;

            request.id = id;
            request.itemKey = String.valueOf(flightNumber);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();

            returnPrice = response.itemPrice;

            System.out.println("QueryFlightPrice for id: " + id + " Flight Number: " + flightNumber
                    + " Returned " + returnPrice);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnPrice;
        }
    }

    //-------------FLIGHT OPERATIONS END--------------//

    //-------------CAR OPERATIONS--------------//

    //ADD CARS//
    public boolean addCars(int id, String location, int numSeats, int carPrice) {
        boolean returnValue = false;

        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 1;
            request.itemType = 0;

            request.id = id;
            request.itemKey = String.valueOf(location);
            request.itemPrice = carPrice;
            request.totalCount = numSeats;
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;

            System.out.println("AddCars : id " + id + ": location : " + location +
                        " : seat number " + numSeats + " : car price : " + carPrice + "RETURNED " + returnValue);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //DELETE CARS//
    public boolean deleteCars(int id, String location) {
        boolean returnValue = false;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 2;
            request.itemType = 0;

            request.id = id;
            request.itemKey = String.valueOf(location);

            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;

            System.out.println("DeleteCars : id " + id + ": location : " + location + "RETURNED " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //QUERY CARS//
    public int queryCars(int id, String location) {
        int returnValue = 0;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 0;
            request.itemType = 0;

            request.id = id;
            request.itemKey = String.valueOf(location);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();

            returnValue = response.totalCount - response.count;

            System.out.println("QueryCars for id: " + id + " location: " + location
                    + " Returned " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //QUERY CAR PRICE//
    public int queryCarsPrice(int id, String location) {
        int returnPrice = 0;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 0;
            request.itemType = 0;

            request.id = id;
            request.itemKey = String.valueOf(location);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();

            returnPrice = response.itemPrice;

            System.out.println("QueryCarsPrice for id: " + id + " Location: " + location
                    + " Returned " + returnPrice);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnPrice;
        }
    }

    //-------------CAR OPERATIONS END--------------//

    //-------------ROOM OPERATIONS----------------//


    //ADD ROOMS//
    public boolean addRooms(int id, String location, int numSeats, int roomPrice) {
        boolean returnValue = false;

        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 1;
            request.itemType = 2;

            request.id = id;
            request.itemKey = String.valueOf(location);
            request.itemPrice = roomPrice;
            request.totalCount = numSeats;
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;

            System.out.println("AddRooms : id " + id + ": location : " + location +
                        " : seat number " + numSeats + " : room price : " + roomPrice + "RETURNED " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //DELETE ROOMS//
    public boolean deleteRooms(int id, String location) {
        boolean returnValue = false;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 2;
            request.itemType = 2;

            request.id = id;
            request.itemKey = String.valueOf(location);

            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;

            System.out.println("DeleteRooms : id " + id + ": location : " + location + " RETURNED " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //QUERY ROOMS//
    public int queryRooms(int id, String location) {
        int returnValue = 0;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 0;
            request.itemType = 2;

            request.id = id;
            request.itemKey = String.valueOf(location);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();

            returnValue = response.totalCount - response.count;

            System.out.println("QueryRooms for id: " + id + " location: " + location
                    + " Returned " + returnValue);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnValue;
        }
    }

    //QUERY ROOM PRICE//
    public int queryRoomsPrice(int id, String location) {
        int returnPrice = 0;
        try {
            TCPPacket request = new TCPPacket();
            request.type = 1;
            request.actionType = 0;
            request.itemType = 2;

            request.id = id;
            request.itemKey = String.valueOf(location);
            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();

            returnPrice = response.itemPrice;

            System.out.println("QueryRoomsPrice for id: " + id + " Location: " + location
                    + " Returned " + returnPrice);

        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            return returnPrice;
        }
    }

    //------------ROOM OPERATIONS END----------------//

    //-----------CUSTOMER OPERATIONS-----------------//

    public int newCustomer(int id) {
        return 0;
    }

    /* Create a new customer with the provided identifier. */
    public boolean newCustomerId(int id, int customerId) {
        return true;
    }

    /* Remove this customer and all their associated reservations. */
    public boolean deleteCustomer(int id, int customerId) {
        return true;
    }

    /* Return a bill. */
    public String queryCustomerInfo(int id, int customerId) {

        return "";
    }

    /* Reserve a seat on this flight. */
    public boolean reserveFlight(int id, int customerId, int flightNumber) {
        return true;
    }

    /* Reserve a car at this location. */
    public boolean reserveCar(int id, int customerId, String location) {
        return true;
    }

    /* Reserve a room at this location. */
    public boolean reserveRoom(int id, int customerId, String location) {
        return true;
    }

    /* Reserve an itinerary. */
    public boolean reserveItinerary(int id, int customerId, Vector<Integer> flightNumbers,
                                    String location, boolean car, boolean room) {
        return true;
    }

}

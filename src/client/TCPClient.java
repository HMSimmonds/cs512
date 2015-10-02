package client;

import sun.tools.java.ClassNotFound;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;

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
                    if (numOfReceipts == 0) {
                        //Respond to connection with "HELLO" Message
                        TCPPacket responsePacket = new TCPPacket();
                        //assign it to a receipt Type
                        responsePacket.type = 0;
                        ++numOfReceipts;
                    }
                    //the response is a message Response
                } else {
                    System.out.println("Connection created with Middleware");
                    //no longer waiting for connection;
                    break;
                }
            }


        } catch (IOException ex) {
            System.out.println(ex);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    //FLIGHT OPERATIONS//

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
            request.count = numSeats;
            request.itemPrice = flightPrice;

            outputStream.writeObject(request);

            //Now wait for response from middleware server
            TCPPacket response = (TCPPacket) inputStream.readObject();
            returnValue = response.isValid;
            if (returnValue) {
                System.out.println("AddFlight : id " + id + ": flight number : " + flightNumber +
                                    " : seat number " + numSeats + " : flightPrice : " + flightPrice + "RETURNED VALID");
            } else {
                System.out.println("AddFlight : id " + id + ": flight number : " + flightNumber +
                " : seat number " + numSeats + " : flightPrice : " + flightPrice + "RETURNED INVALID");
            }

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

            if (returnValue) {
                System.out.println("DeleteFlight : id " + id + ": flight number : " + flightNumber +
                        " RETURNED VALID");
            } else {
                System.out.println("DeleteFlight : id " + id + ": flight number : " + flightNumber +
                        " RETURNED INVALID");
            }

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
        
    }

}

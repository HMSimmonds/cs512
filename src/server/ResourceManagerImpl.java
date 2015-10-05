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

    private static final int ANSWER_TYPE = -1;

    //connect for server
    private ServerSocket resourceManagerSocket;

    //storage to store customers related to the type specific RM
    private HashMap<String, ReservableItem> storage;
    private HashMap<Integer, Customer> customers;

    public ResourceManagerImpl(int portNum) {
        storage = new HashMap<>();
        customers = new HashMap<>();

        try {
            resourceManagerSocket = new ServerSocket(portNum);
            System.out.println("Created resource manager server socket at port : " +
                    portNum);

            while (true) {
                //this will run while we accept a connection from the middleware
                Socket middlewareSocket = resourceManagerSocket.accept();

                //this will then process the connection to the middleware
                new Thread(new ResourceManagerWorker(middlewareSocket)).run();
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
        TCPPacket returnPacket = TCPPacket.makeBlankPacket();
        boolean isValidResponse = true;

        if (packet.actionType == GET) {
            //customer query (info & bill)
            if (packet.itemType == 3) {
                if (customers.get(packet.customerId) == null) {
                    System.out.println("Customer has been deleted");
                    isValidResponse = false;
                } else {
                    returnPacket.bill = customers.get(packet.customerId).printBill();
                    isValidResponse = true;
                }
            } else {
                ReservableItem itm = getItem(packet.id, packet.itemKey);
                if (itm == null) {
                    //failed response - invalid
                    isValidResponse = false;
                }
                //add into return object
                else {
                    Object key = itm.getKey();
                    if (key != null) {
                        returnPacket.itemKey = (String) key;
                        returnPacket.totalCount = itm.getCount();
                        returnPacket.count = itm.getReserved();
                        returnPacket.itemPrice = itm.getPrice();
                    } else {
                        returnPacket.itemKey = "";
                    }
                }
            }
        } else if (packet.actionType == ADD) {
            if (packet.itemType == 3) {
                if (addCustomer(packet.customerId, new Customer(packet.customerId))) {
                    isValidResponse = true;
                }
                returnPacket.customerId = packet.customerId;
            }
            else if (addItem(packet.id, new ReservableItem(packet.itemKey, packet.totalCount, packet.itemPrice))) {
                //the item exists
                System.out.println(packet.totalCount);
                isValidResponse = true;
            }

        } else if (packet.actionType == DELETE) {
            if (packet.itemType == 3) {
                if (deleteCustomer(packet.customerId)) {
                    isValidResponse = true;
                }
            }

            else if (removeItem(packet.id, packet.itemKey)) {
                //item does not exist
                isValidResponse = false;
            }

        } else if (packet.actionType == RESERVE) {
            if (!reserveItem(packet.id, packet.customerId, packet.itemKey, packet.itemKey)) {
                isValidResponse = false;
            }

        } else {
            //ERROR
            System.out.println("Error when processing request " + packet);
        }

        returnPacket.isValid = isValidResponse;
        returnPacket.actionType = packet.actionType;
        returnPacket.itemType = packet.itemType;
        returnPacket.type = ANSWER_TYPE;

        return returnPacket;
    }

    /*
    will retrieve the item from the storage database within the resource manager server
    NOTE: synchronized to prevent multiple access to hashmap
     */
    private synchronized ReservableItem getItem(int id, String key) {
        System.out.println("Retrieving item for id : " + id + " and key: " + key);
        return storage.get(key);
    }

    /*
    Will Add the item to the storage database within the resource manager server
    NOTE: synchronized to prevent multiple access to hashmap.
     */
    private synchronized  boolean addItem(int id, ReservableItem item) {
        boolean doesExist = false;
        String key = item.getKey().toLowerCase();
        System.out.println("Adding item for id : " + id + " and item key : " + key);

        //If the item already exists in the database we must perform an update
        if (storage.containsKey(key)) {
            updateStorage(item, key);
            doesExist = true;
        } else {
            //not in data base
            storage.put(key, item);
        }
        return doesExist;
    }

    private synchronized boolean addCustomer(int customerId, Customer cust) {
        boolean doesExist = false;

        if (customers.containsKey(customerId)) {
            doesExist = true;
        } else {
            customers.put(customerId, cust);
        }
        return doesExist;
    }

    private synchronized boolean deleteCustomer(int customerId) {
        boolean isDeleted = false;
        if (customers.containsKey(customerId)) {
            Customer customer = customers.get(customerId);

            //now remove all reservations
            RMHashtable reservations = customer.getReservations();
            if (reservations == null) {
                System.out.println("Error deleting customer reservations");
            } else {
                //put all reservations back into storage as available
                for (Object obj : reservations.values()) {
                    ReservedItem item = (ReservedItem) obj;
                    updateStorage(item, item.getKey());
                }

                //now remove all entires
                reservations.clear();
            }

            if (customers.remove(customerId) == null) {
                System.out.println("Customer could not be removed");
            }
            isDeleted = true;
        }
        else {
            System.out.println("Customer does not exist");
        }
        return isDeleted;
    }

    private void updateStorage(ReservedItem item, String key) {
        ReservableItem reservableItem = new ReservableItem(item.getLocation(), item.getCount(), item.getPrice());
        updateStorage(reservableItem, key);
    }

    private void updateStorage(ReservableItem item, String key) {
        ReservableItem existing = storage.get(key);

        if (existing == null) {
            System.out.println("Error updating storage, item does not exist in database");
            return;
        }
        existing.setCount(item.getCount() + existing.getCount());
        if (item.getPrice() > 0) {
            existing.setPrice(item.getPrice());
        }
        storage.put(key, existing);
    }

    /*
    Will remove item from the storage database within the resource manager server
    NOTE: synchronized to prevent multiple access to hashmap.
     */
    private synchronized boolean removeItem(int id, String key) {
        System.out.println("Removing item with id : " + id + " Key : " + key);
        return storage.remove(key) == null;
    }

    /*
    Will reserve item within the storage database within the resource manager server
    NOTE: synchronized to prevent multiple access to hashmap.
     */
    private synchronized boolean reserveItem(int id, int customerId, String key, String location) {
        boolean canReserve = false;

        Trace.info("RM::reserveItem(" + id + ", " + customerId + ", "
        + key + ", " + location + ") called.");
        // Read customer object if it exists (and read lock it).
        Customer customer = customers.get(customerId);
        if (customer == null) {
            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
                    + key + ", " + location + ") failed: customer doesn't exist.");
            return false;
        }

        //First we check if the item is available to reserve
        ReservableItem item = getItem(id, key);

        //item does not exist
        if (item == null) {
            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", " + key + ", " + location +
                        ") INVALID - item does not exist in database");
        } else if (item.getCount() == 0) {
            //item is out of stock - none to reserve
            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", " + key + ", " + location +
                        ") INVALID - no stock left");
        } else {
            //we can make reservation
            customer.reserve(key, location, item.getPrice());
            customers.put(customerId, customer);

            System.out.println("Reserving request item : " + item);
            item.setReserved(item.getReserved() + 1);
            canReserve = true;

            Trace.info("RM::reserveItem(" + id + ", " + customerId + ", " + key + ", " + location + ") VALID.");
            System.out.println("Reserved item : " + item);
        }
        return canReserve;
    }

    public class ResourceManagerWorker implements Runnable {

        private Socket socket;

        public ResourceManagerWorker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("Created connection with middlewareSocket at " + socket.getRemoteSocketAddress());

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
    }

}

    

    

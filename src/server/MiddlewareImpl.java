// -------------------------------
// Adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package server;

import server.ws.ResourceManager;

import java.util.*;
import javax.jws.WebService;


@WebService(endpointInterface = "server.ws.Middleware")
public class MiddlewareImpl implements server.ws.Middleware {

    //TODO: Remove HashTable
    //TODO: Synchronize operations (addFlight .. etc)

    protected RMHashtable m_itemHT = new RMHashtable();
    protected HashMap<String, ResourceManager> resourceManagerHash = new HashMap<String, ResourceManager>();

    public final static String FLIGHT_TYPE = "Flight";
    public final static String CAR_TYPE = "Car";
    public final static String ROOM_TYPE = "Room";

    //assigns list of active resourceManagers to middleware
    public MiddlewareImpl() {
        //automatically assigns three RM's to ResourceManagerMap
        resourceManagerHash.put(FLIGHT_TYPE, new ResourceManagerImpl());
        resourceManagerHash.put(CAR_TYPE, new ResourceManagerImpl());
        resourceManagerHash.put(ROOM_TYPE, new ResourceManagerImpl());
    }

    // Basic operations on RMItem //
    
    // Read a data item.
    private ReservableItem readData(ResourceManager rmImpl, int id) {
        synchronized(rmImpl) {
            return rmImpl.getItem(id);
        }
    }

    // Write a data item - returns true if write successful
    private boolean writeData(ResourceManager rmImpl, ReservableItem item, int id) {
        synchronized(rmImpl) {
            return rmImpl.addItem(id, item);
        }
    }

    // Remove the item out of storage.
    protected ReservableItem removeData(ResourceManager rmImpl, int id) {
        synchronized(rmImpl) {
            ReservableItem ret = rmImpl.getItem(id);
            rmImpl.removeItem(id);

            return ret;
        }
    }
    
    
    // Basic operations on ReservableItem //
    
    // Delete the entire item.
//    protected boolean deleteItem(int id, String key) {
//        Trace.info("RM::deleteItem(" + id + ", " + key + ") called.");
//        ReservableItem curObj = (ReservableItem) readData(id, key);
//        // Check if there is such an item in the storage.
//        if (curObj == null) {
//            Trace.warn("RM::deleteItem(" + id + ", " + key + ") failed: "
//                    + " item doesn't exist.");
//            return false;
//        } else {
//            if (curObj.getReserved() == 0) {
//                removeData(id, curObj.getKey());
//                Trace.info("RM::deleteItem(" + id + ", " + key + ") OK.");
//                return true;
//            }
//            else {
//                Trace.info("RM::deleteItem(" + id + ", " + key + ") failed: "
//                        + "some customers have reserved it.");
//                return false;
//            }
//        }
//    }
    
    // Query the number of available seats/rooms/cars.
//    protected int queryNum(int id, String key) {
//        Trace.info("RM::queryNum(" + id + ", " + key + ") called.");
//        ReservableItem curObj = (ReservableItem) readData(id, key);
//        int value = 0;
//        if (curObj != null) {
//            value = curObj.getCount();
//        }
//        Trace.info("RM::queryNum(" + id + ", " + key + ") OK: " + value);
//        return value;
//    }
    
    // Query the price of an item.
//    protected int queryPrice(int id, String key) {
//        Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") called.");
//        ReservableItem curObj = (ReservableItem) readData(id, key);
//        int value = 0;
//        if (curObj != null) {
//            value = curObj.getPrice();
//        }
//        Trace.info("RM::queryCarsPrice(" + id + ", " + key + ") OK: $" + value);
//        return value;
//    }

    // Reserve an item.
//    protected boolean reserveItem(int id, int customerId,
//                                  String key, String location) {
//        Trace.info("RM::reserveItem(" + id + ", " + customerId + ", "
//                + key + ", " + location + ") called.");
//        // Read customer object if it exists (and read lock it).
//        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
//        if (cust == null) {
//            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
//                   + key + ", " + location + ") failed: customer doesn't exist.");
//            return false;
//        }
//
//        // Check if the item is available.
//        ReservableItem item = (ReservableItem) readData(id, key);
//        if (item == null) {
//            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
//                    + key + ", " + location + ") failed: item doesn't exist.");
//            return false;
//        } else if (item.getCount() == 0) {
//            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
//                    + key + ", " + location + ") failed: no more items.");
//            return false;
//        } else {
//            // Do reservation.
//            cust.reserve(key, location, item.getPrice());
//            writeData(id, cust.getKey(), cust);
//
//            // Decrease the number of available items in the storage.
//            item.setCount(item.getCount() - 1);
//            item.setReserved(item.getReserved() + 1);
//
//            Trace.warn("RM::reserveItem(" + id + ", " + customerId + ", "
//                    + key + ", " + location + ") OK.");
//            return true;
//        }
//    }
    
    
    // Flight operations //
    
    // Create a new flight, or add seats to existing flight.
    // Note: if flightPrice <= 0 and the flight already exists, it maintains 
    // its current price.
    @Override
    public boolean addFlight(int id, int flightNumber, 
                             int numSeats, int flightPrice) {
        Trace.info("RM::addFlight(" + id + ", " + flightNumber 
                + ", $" + flightPrice + ", " + numSeats + ") called.");

        //Find appropriate Flight RM and then send the operation
        if (writeData(resourceManagerHash.get(FLIGHT_TYPE), new Flight(flightNumber, numSeats, flightPrice), id)) {
            Trace.info("RM::addFlight(" + id + ", " + flightNumber
                    + ", $" + flightPrice + ", " + numSeats + ") OKAY.");
            return true;
        }

        return(false);
    }

    @Override
    public boolean deleteFlight(int id, int flightNumber) {
        Trace.info("RM::deleteFlight(" + id + ", " + flightNumber + ") called.");

        //if the flight does not already exist
        if (removeData(resourceManagerHash.get(FLIGHT_TYPE), id) != null) {
            Trace.info("RM::deleteFlight(" + id + ", " + flightNumber + ") OKAY.");
            return true;

        }
        //flight does not exist
        Trace.info("RM::deleteFlight(" + id + ", " + flightNumber + ") DOES NOT EXIST.");
        return false;

    }

    // Returns the number of empty seats on this flight.
    @Override
    public int queryFlight(int id, int flightNumber) {
        ReservableItem flight = readData(resourceManagerHash.get(FLIGHT_TYPE), id);

        return flight.getCount() - flight.getReserved();
    }

    // Returns price of this flight.
    public int queryFlightPrice(int id, int flightNumber) {
        return queryItemPrice(id, FLIGHT_TYPE);
    }

    //Generic method for item price
    public int queryItemPrice(int id, String type) {
        return readData(resourceManagerHash.get(type), id).getPrice();
    }

    /*
    // Returns the number of reservations for this flight. 
    public int queryFlightReservations(int id, int flightNumber) {
        Trace.info("RM::queryFlightReservations(" + id 
                + ", #" + flightNumber + ") called.");
        RMInteger numReservations = (RMInteger) readData(id, 
                Flight.getNumReservationsKey(flightNumber));
        if (numReservations == null) {
            numReservations = new RMInteger(0);
       }
        Trace.info("RM::queryFlightReservations(" + id + 
                ", #" + flightNumber + ") = " + numReservations);
        return numReservations.getValue();
    }
    */
    
    /*
    // Frees flight reservation record. Flight reservation records help us 
    // make sure we don't delete a flight if one or more customers are 
    // holding reservations.
    public boolean freeFlightReservation(int id, int flightNumber) {
        Trace.info("RM::freeFlightReservations(" + id + ", " 
                + flightNumber + ") called.");
        RMInteger numReservations = (RMInteger) readData(id, 
                Flight.getNumReservationsKey(flightNumber));
        if (numReservations != null) {
            numReservations = new RMInteger(
                    Math.max(0, numReservations.getValue() - 1));
        }
        writeData(id, Flight.getNumReservationsKey(flightNumber), numReservations);
        Trace.info("RM::freeFlightReservations(" + id + ", " 
                + flightNumber + ") OK: reservations = " + numReservations);
        return true;
    }
    */


    // Car operations //

    // Create a new car location or add cars to an existing location.
    // Note: if price <= 0 and the car location already exists, it maintains 
    // its current price.
    @Override
    public boolean addCars(int id, String location, int numCars, int carPrice) {
        Trace.info("RM::addCars(" + id + ", " + location + ", "
                + numCars + ", $" + carPrice + ") called.");

        //Find appropriate Car RM and then send the operation
        if (writeData(resourceManagerHash.get(CAR_TYPE), new Car(location, numCars, carPrice), id)) {
            Trace.info("RM::addCars(" + id + ", " + location + ", "
                    + numCars + ", $" + carPrice + ") OK.");
            return true;
        }

        return(false);
    }

    // Delete cars from a location.
    @Override
    public boolean deleteCars(int id, String location) {
        Trace.info("RM::deleteCar(" + id + ", " + location + ") called.");

        //if the flight does not already exist
        if (removeData(resourceManagerHash.get(CAR_TYPE), id) != null) {
            Trace.info("RM::deleteCar(" + id + ", " + location + ") OKAY.");
            return true;

        }
        //flight does not exist
        Trace.info("RM::deleteCar(" + id + ", " + location + ") DOES NOT EXIST.");
        return false;
    }

    // Returns the number of cars available at a location.
    @Override
    public int queryCars(int id, String location) {
        ReservableItem car = readData(resourceManagerHash.get(CAR_TYPE), id);

        return car.getCount() - car.getReserved();
    }

    // Returns price of cars at this location.
    @Override
    public int queryCarsPrice(int id, String location) {
        return queryItemPrice(id, CAR_TYPE);
    }
    

    // Room operations //

    // Create a new room location or add rooms to an existing location.
    // Note: if price <= 0 and the room location already exists, it maintains 
    // its current price.
    @Override
    public boolean addRooms(int id, String location, int numRooms, int roomPrice) {
        Trace.info("RM::addRooms(" + id + ", " + location + ", "
                + numRooms + ", $" + roomPrice + ") called.");

        //Find appropriate Room RM and then send the operation
        if (writeData(resourceManagerHash.get(ROOM_TYPE), new Room(location, numRooms, roomPrice), id)) {
            Trace.info("RM::addRooms(" + id + ", " + location + ", "
                    + numRooms + ", $" + roomPrice + ") OK.");
            return true;
        }

        return(false);
    }

    // Delete rooms from a location.
    @Override
    public boolean deleteRooms(int id, String location) {
        Trace.info("RM::deleteRooms(" + id + ", " + location + ") called.");

        //if the flight does not already exist
        if (removeData(resourceManagerHash.get(ROOM_TYPE), id) != null) {
            Trace.info("RM::deleteRooms(" + id + ", " + location + ") OKAY.");
            return true;

        }
        //flight does not exist
        Trace.info("RM::deleteRooms(" + id + ", " + location + ") DOES NOT EXIST.");
        return false;
    }

    // Returns the number of rooms available at a location.
    @Override
    public int queryRooms(int id, String location) {
        ReservableItem room = readData(resourceManagerHash.get(ROOM_TYPE), id);

        return room.getCount() - room.getReserved();
    }
    
    // Returns room price at this location.
    @Override
    public int queryRoomsPrice(int id, String location) {
        return queryItemPrice(id, CAR_TYPE);
    }


    // Customer operations //

    @Override
    public int newCustomer(int id) {
        Trace.info("INFO: RM::newCustomer(" + id + ") called.");
        // Generate a globally unique Id for the new customer.
        int customerId = Integer.parseInt(String.valueOf(id) +
                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                String.valueOf(Math.round(Math.random() * 100 + 1)));
        Customer cust = new Customer(customerId);
        writeData(id, cust.getKey(), cust);
        Trace.info("RM::newCustomer(" + id + ") OK: " + customerId);
        return customerId;
    }

    // This method makes testing easier.
//    @Override
//    public boolean newCustomerId(int id, int customerId) {
//        Trace.info("INFO: RM::newCustomer(" + id + ", " + customerId + ") called.");
//        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
//        if (cust == null) {
//            cust = new Customer(customerId);
//            writeData(id, cust.getKey(), cust);
//            Trace.info("INFO: RM::newCustomer(" + id + ", " + customerId + ") OK.");
//            return true;
//        } else {
//            Trace.info("INFO: RM::newCustomer(" + id + ", " +
//                    customerId + ") failed: customer already exists.");
//            return false;
//        }
//    }

    // Delete customer from the database. 
//    @Override
//    public boolean deleteCustomer(int id, int customerId) {
//        Trace.info("RM::deleteCustomer(" + id + ", " + customerId + ") called.");
//        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
//        if (cust == null) {
//            Trace.warn("RM::deleteCustomer(" + id + ", "
//                    + customerId + ") failed: customer doesn't exist.");
//            return false;
//        } else {
//            // Increase the reserved numbers of all reservable items that
//            // the customer reserved.
//            RMHashtable reservationHT = cust.getReservations();
//            for (Enumeration e = reservationHT.keys(); e.hasMoreElements();) {
//                String reservedKey = (String) (e.nextElement());
//                ReservedItem reservedItem = cust.getReservedItem(reservedKey);
//                Trace.info("RM::deleteCustomer(" + id + ", " + customerId + "): "
//                        + "deleting " + reservedItem.getCount() + " reservations "
//                        + "for item " + reservedItem.getKey());
//                ReservableItem item =
//                        (ReservableItem) readData(id, reservedItem.getKey());
//                item.setReserved(item.getReserved() - reservedItem.getCount());
//                item.setCount(item.getCount() + reservedItem.getCount());
//                Trace.info("RM::deleteCustomer(" + id + ", " + customerId + "): "
//                        + reservedItem.getKey() + " reserved/available = "
//                        + item.getReserved() + "/" + item.getCount());
//            }
//            // Remove the customer from the storage.
//            removeData(id, cust.getKey());
//            Trace.info("RM::deleteCustomer(" + id + ", " + customerId + ") OK.");
//            return true;
//        }
//    }

    // Return data structure containing customer reservation info. 
    // Returns null if the customer doesn't exist. 
    // Returns empty RMHashtable if customer exists but has no reservations.
//    public RMHashtable getCustomerReservations(int id, int customerId) {
//        Trace.info("RM::getCustomerReservations(" + id + ", "
//                + customerId + ") called.");
//        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
//        if (cust == null) {
//            Trace.info("RM::getCustomerReservations(" + id + ", "
//                    + customerId + ") failed: customer doesn't exist.");
//            return null;
//        } else {
//            return cust.getReservations();
//        }
//    }

    // Return a bill.
//    @Override
//    public String queryCustomerInfo(int id, int customerId) {
//        Trace.info("RM::queryCustomerInfo(" + id + ", " + customerId + ") called.");
//        Customer cust = (Customer) readData(id, Customer.getKey(customerId));
//        if (cust == null) {
//            Trace.warn("RM::queryCustomerInfo(" + id + ", "
//                    + customerId + ") failed: customer doesn't exist.");
//            // Returning an empty bill means that the customer doesn't exist.
//            return "";
//        } else {
//            String s = cust.printBill();
//            Trace.info("RM::queryCustomerInfo(" + id + ", " + customerId + "): \n");
//            System.out.println(s);
//            return s;
//        }
//    }

    // Add flight reservation to this customer.  
//    @Override
//    public boolean reserveFlight(int id, int customerId, int flightNumber) {
//        return reserveItem(id, customerId,
//                Flight.getKey(flightNumber), String.valueOf(flightNumber));
//    }

    // Add car reservation to this customer. 
//    @Override
//    public boolean reserveCar(int id, int customerId, String location) {
//        return reserveItem(id, customerId, Car.getKey(location), location);
//    }

    // Add room reservation to this customer. 
//    @Override
//    public boolean reserveRoom(int id, int customerId, String location) {
//        return reserveItem(id, customerId, Room.getKey(location), location);
//    }
    

    // Reserve an itinerary.
//    @Override
//    public boolean reserveItinerary(int id, int customerId, Vector flightNumbers,
//                                    String location, boolean car, boolean room) {
//        return false;
//    }

}

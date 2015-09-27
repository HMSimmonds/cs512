package server;

import server.ws.ResourceManager;

import java.lang.reflect.Array;
import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by hmsimmonds on 15-09-27.
 */
public class ResourceManagerImpl implements ResourceManager {


    HashMap<Integer, ReservableItem> reservableItems;

    public ResourceManagerImpl() {
        reservableItems = new HashMap<Integer, ReservableItem>();

    }

    @Override
    public ReservableItem getItem(int id) {
        if (reservableItems.containsKey(id)) {
            return reservableItems.get(id);
        } else {
            return null;
        }
    }

    @Override
    public boolean removeItem(int id) {

        if (reservableItems.containsKey(id)) {
            reservableItems.remove(id);
            return true;
        }
        return false;
    }

    //returns true if item already existed
    @Override
    public boolean addItem(int id, ReservableItem reservableItem) {
        boolean toReturn = false;
        if (reservableItems.contains(id)) {
            toReturn = true;
        }
        reservableItems.put(id, reservableItem);

        return toReturn;
    }

}
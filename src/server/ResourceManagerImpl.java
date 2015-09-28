package server;

import server.ws.ResourceManager;

import java.util.HashMap;
import javax.jws.WebService;
import java.util.Map;

/*
 * Created by hmsimmonds on 15-09-27.
 */
public class ResourceManagerImpl implements ResourceManager {


    private HashMap<Integer, ReservableItem> reservableItems;

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

        return reservableItems.remove(id) == null;
    }

    //returns true if item already existed
    @Override
    public boolean addItem(int id, ReservableItem reservableItem) {
        ReservableItem returnValue;

        //The database already contains the item, and we update the count
        if (reservableItems.containsKey(id)) {
            ReservableItem tmp = reservableItems.get(id);
            tmp.setCount(reservableItem.getCount() + tmp.getCount());
            if (reservableItem.getPrice() > 0) {
                tmp.setPrice(reservableItem.getPrice());
            }

            returnValue = reservableItems.put(id, tmp);

            //item is not in the database, put into DB
        } else {
            returnValue = reservableItems.put(id, reservableItem);
        }

        return returnValue != null;
    }

}
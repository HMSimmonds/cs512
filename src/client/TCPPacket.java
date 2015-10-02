package client;

import java.io.Serializable;
import server.ReservableItem;

/**
 * Created by hmsimmonds on 15-10-02.
 */
public class TCPPacket {

    public int itemPrice;
    public String itemKey;

    public int customerId;
    public boolean isValid;

    public int type;        //0 = receipt, 1 = message
    public int id;
    public int totalCount;
    public int count;
    public int itemType;    //0 = Car, 1 = Flight, 2 = Room, 3 = Customer, 4 = Itinerary
    public int actionType;  //0 = GET, 1 = ADD, 2 = DELETE, 3 = RESERVE
}

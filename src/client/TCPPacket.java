package client;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by hmsimmonds on 15-10-02.
 */
public class TCPPacket implements Serializable {

    public static TCPPacket makeBlankPacket() {
        TCPPacket returnPacket = new TCPPacket();
        returnPacket.id = 0;
        returnPacket.type = 0;
        returnPacket.itemType = 0;
        returnPacket.actionType = 0;
        returnPacket.itemKey = "";
        returnPacket.totalCount = 0;
        returnPacket.count = 0;
        returnPacket.car = true;
        returnPacket.room = true;
        returnPacket.customerId = 0;
        returnPacket.isValid = false;

        return returnPacket;
    }

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

    //return values
    public boolean car;
    public boolean room;
    public Vector flights;
}

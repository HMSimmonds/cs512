/**
 * Simplified version from CSE 593, University of Washington.
 *
 * A Distributed System in Java using Web Services.
 *
 * Failures should be reported via the return value.  For example, 
 * if an operation fails, you should return either false (boolean), 
 * or some error code like -1 (int).
 *
 * If there is a boolean return value and you're not sure how it 
 * would be used in your implementation, ignore it.  I used boolean
 * return values in the interface generously to allow flexibility in 
 * implementation.  But don't forget to return true when the operation
 * has succeeded.
 */

package server.ws;

import server.ReservableItem;

import java.rmi.server.RemoteServer;
import java.util.*;
import javax.jws.WebService;
import javax.jws.WebMethod;


//@WebService
public interface ResourceManager {

   //Generic implementation for RM's

//    @WebMethod
    ReservableItem getItem(int id);

//    @WebMethod
    boolean removeItem(int id);

//    @WebMethod
    boolean addItem(int id, ReservableItem reservableItem);

}

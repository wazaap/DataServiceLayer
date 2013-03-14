package dataSource;

import java.sql.*;
import java.util.ArrayList;
import domain.*;

//===	keeps track of all changes to domain objects during a business transaction
//	defines system transaction for save  (setAutocommit(false)   >   conn.commit() 
//	2010/hau
public class UnitOfWorkProcessOrder {    

    private ArrayList<Order> newOrders;
    private ArrayList<Order> dirtyOrders;
    private ArrayList<Order> deleteOrder;
    private ArrayList<OrderDetail> newOrderDetails;
    private ArrayList<OrderDetail> updateOrderDetails;
    private ArrayList<OrderDetail> deleteOrderDetails;
    
    public UnitOfWorkProcessOrder() {
        newOrders = new ArrayList<Order>(); // will never exceed 1 element
        dirtyOrders = new ArrayList<Order>(); // will never exceed 1 element
        deleteOrder = new ArrayList<Order>(); // will never exceed 1 element
        newOrderDetails = new ArrayList<OrderDetail>();
        updateOrderDetails = new ArrayList<OrderDetail>();
        deleteOrderDetails = new ArrayList<OrderDetail>();
        
    }
    //====== Methods to register changes ==========================
    
    public void registerNewOrder(Order o) {
        if (!newOrders.contains(o) && // if not allready registered in any list
                !dirtyOrders.contains(o)) {            
            newOrders.add(o);
        }
    }

    public void registerDirtyOrder(Order o) {
        if (!newOrders.contains(o) && // if not allready registered in any list
                !dirtyOrders.contains(o)) {
            dirtyOrders.add(o);
        }
    }

    public void registerNewOrderDetail(OrderDetail od) {
        if (!newOrderDetails.contains(od)) // if not allready registered in list
        {            
            newOrderDetails.add(od);
        }
    }
    
    public void registerDeleteOrder(Order o) {
        if (!newOrders.contains(o) && !dirtyOrders.contains(o) && !deleteOrder.contains(o)) {
            deleteOrder.add(o);
        }
    }
    
    public void registerUpdateOrderDetail(OrderDetail od) {
        if (!newOrderDetails.contains(od) && !updateOrderDetails.contains(od)) {
            updateOrderDetails.add(od);
        }
    }
    
    public void registerDeleteOrderDetail(OrderDetail od) {
        if (!newOrderDetails.contains(od) && !updateOrderDetails.contains(od) && !deleteOrderDetails.contains(od)) {
            deleteOrderDetails.add(od);
        }
    }

    //====== Method to save changes to DB ===============================
    public boolean commit(Connection conn) throws SQLException {        
        boolean status = true;        
        try {
            //=== system transaction - start
            conn.setAutoCommit(false);            
            OrderMapper om = new OrderMapper();
            
            status = status && om.insertOrders(newOrders, conn);
            status = status && om.updateOrders(dirtyOrders, conn);
            status = status && om.insertOrderDetails(newOrderDetails, conn);
            status = status && om.deleteOrder(deleteOrder, conn);
            status = status && om.updateOrderDetail(updateOrderDetails, conn);
            status = status && om.deleteOrderDetail(deleteOrderDetails, conn);
            if (!status) {
                throw new Exception("Business Transaction aborted");
            }
            //=== system transaction - end with success
            conn.commit();            
        } catch (Exception e) {
            System.out.println("fail in UnitOfWork - commit()");
            System.err.println(e);
            //=== system transaction - end with roll back
            conn.rollback();
            status = false;// rettelse
        }
        return status;        
    }

    //====== Methods to read from DB ===================================================
    public Order getOrder(int ono, Connection con) {
        Order o = null;;        
        try {            
            o = new OrderMapper().getOrder(ono, con);            
        } catch (Exception e) {
            System.out.println("fail in UnitOfWork - getOrder()");
            System.err.println(e);
        }
        return o;        
        
    }
}

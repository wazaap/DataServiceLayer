package domain;

import dataSource.DBFacade;

//===	facade to the Domain Layer
//	defines the state of the business transaction
//	perform business logic
//	makes the registration of all objects affected during the business transaction with a Unit of Work object (through the //	DBFacade)
//	2010/hau
public class Controller {

    private boolean processingOrder;	// state of business transaction
    private Order currentOrder;       	// Order in focus
    private DBFacade dbFacade;

    public Controller() {
        processingOrder = false;
        currentOrder = null;
        dbFacade = DBFacade.getInstance();
    }

    public Order getOrder(int ono) {
        if (processingOrder) {
            return null;
        }


        dbFacade.startNewBusinessTransaction();
        processingOrder = true;
        currentOrder = dbFacade.getOrder(ono);
        return currentOrder;
    }

    public Order createNewOrder(int cno, int eno, String rec, String ship) {
        if (processingOrder) {
            return null;
        }

        dbFacade.startNewBusinessTransaction();
        int newOrderNo = dbFacade.getNextOrderNo();// DB-generated unique ID
        if (newOrderNo != 0) {
            processingOrder = true;
            currentOrder = new Order(newOrderNo, cno, eno, rec, ship, 0);
            dbFacade.registerNewOrder(currentOrder);
        } else {
            processingOrder = false;
            currentOrder = null;
        }
        return currentOrder;
    }

    public Order changeCnoForOrder(int cno) {
        if (processingOrder) {
            currentOrder.setCno(cno);
            dbFacade.registerDirtyOrder(currentOrder);
        }
        return currentOrder;
    }

    public Order changeEnoForOrder(int eno) {
        if (processingOrder) {
            currentOrder.setEno(eno);
            dbFacade.registerDirtyOrder(currentOrder);
        }
        return currentOrder;
    }

    public boolean addOrderDetail(int partNo, int qty) {
        boolean status = false;
        if (processingOrder) {
            OrderDetail od = new OrderDetail(currentOrder.getOno(), partNo, qty);
            currentOrder.addDetail(od);
            dbFacade.registerNewOrderDetail(od);
            status = true;
        }
        return status;
    }

    public boolean deleteOrder() {
        boolean status = false;
        if (processingOrder) {
            dbFacade.registerDeleteOrder(currentOrder);
            status = dbFacade.commitBusinessTransaction();
            if (status) {
                resetOrder();
            }
        }
        return status;
    }

    public String getOrderDetailsToString() {
        if (processingOrder) {
            return currentOrder.detailsToString();
        } else {
            return null;
        }
    }

    public boolean saveOrder() {
        boolean status = false;
        if (processingOrder) {
            //== ends ongoing business transaction

            status = dbFacade.commitBusinessTransaction();
            processingOrder = false;
            currentOrder = null;
        }
        return status;
    }

    public void resetOrder() {
        processingOrder = false;
        currentOrder = null;
    }

    public boolean updateOrderDetail(int pno, int qty) {
        boolean status = false;
        if (processingOrder) {
            OrderDetail od = new OrderDetail(currentOrder.getOno(), pno, qty);
            dbFacade.registerDirtyOrder(currentOrder);
            

            
            dbFacade.registerUpdateOrderDetail(od);
            status = dbFacade.commitBusinessTransaction();
        }
        return status;

    }
    
    public boolean deleteOrderDetail (int pno){
       boolean status = false;
       if (processingOrder) {
           OrderDetail od = new OrderDetail (currentOrder.getOno(), pno, 0);
           dbFacade.registerDeleteOrderDetail(od);
           status = dbFacade.commitBusinessTransaction();
       }
       return status;
    }
}

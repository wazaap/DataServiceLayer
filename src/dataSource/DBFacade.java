package dataSource;

import java.sql.*;
import domain.*;

//==	facade to the Data Source Layer
//	encapsulates connection handling
//	implemented as a Singleton to restrict the number of Unit of Work objects to 1.
//	2010/hau
public class DBFacade {

    private UnitOfWorkProcessOrder uow;
    ;
	  
	  //=====	Singleton
	  
	  private static DBFacade instance;

    private DBFacade() {
    }

    public static DBFacade getInstance() {
        if (instance == null) {
            instance = new DBFacade();
        }
        return instance;
    }

    //======	Methods to retrieve data 
    public Order getOrder(int ono) {
        Connection con = null;
        Order o = null;
        try {
            con = getConnection();
            o = new OrderMapper().getOrder(ono, con);
        } finally {
            releaseConnection(con);
        }
        return o;
    }

    public int getNextOrderNo() {
        Connection con = null;
        int nextOno = 0;
        try {
            con = getConnection();
            nextOno = new OrderMapper().getNextOrderNo(con);
        } finally {
            releaseConnection(con);
        }
        return nextOno;
    }

    //=====	Methods to register changes	in UnitOfWork  
    public void registerNewOrder(Order o) {
        if (uow != null) {
            uow.registerNewOrder(o);
        }
    }

    public void registerDirtyOrder(Order o) {
        if (uow != null) {
            uow.registerDirtyOrder(o);
        }
    }

    public void registerNewOrderDetail(OrderDetail od) {
        if (uow != null) {
            uow.registerNewOrderDetail(od);
        }
    }
    
    public void registerDeleteOrder(Order o){
        if (uow != null){
            uow.registerDeleteOrder(o);
        }
    }
    
    public void registerUpdateOrderDetail (OrderDetail od){
        if (uow != null){
            uow.registerUpdateOrderDetail(od);
        } 
    }
    
    public void registerDeleteOrderDetail (OrderDetail od) {
        if (uow != null){
            uow.registerDeleteOrderDetail(od);
        }
    }

    //=== Methods to handle business transactions
    //=====	Ignore changes after last commit
    public void startNewBusinessTransaction() {
        uow = new UnitOfWorkProcessOrder();
    }

    //=====	Save all changes
    public boolean commitBusinessTransaction() {
        boolean status = false;
        if (uow != null) {
            Connection con = null;
            try {
                con = getConnection();
                status = uow.commit(con);
            } catch (Exception e) {
                System.out.println("Fail in DBFacade - commitBusinessTransaction");
                System.err.println(e);
            } finally {
                releaseConnection(con);
            }
            uow = null;
        }
        return status;
    }

    //=== connection specifics
    private Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@delfi.lyngbyes.dk:1521:KNORD", "cphtl12", "cphtl12");
        } catch (Exception e) {
            System.out.println("fejl i DBFacade.getConnection()");
            System.out.println(e);
        }
        return con;
    }

    private void releaseConnection(Connection con) {
        try {
            con.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}

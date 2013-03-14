package dataSource;

import java.sql.*;
import java.util.ArrayList;
import domain.*;

//==== 	encapsulates SQL 
//	maps between classes and tables
//	implements Optimistic Offline Lock (version number attribute in the order table)
//	2010/hau
public class OrderMapper {

    static boolean testRun = false;

    //====== Methods to save to DB =========================================================
    // Insert a list of new orders
    // returns true if all elements were inserted successfully
    public boolean insertOrders(ArrayList<Order> ol, Connection conn) throws SQLException {
        int rowsInserted = 0;
        String SQLString = "insert into v_orders values (?,?,?,?,?,?)";
        PreparedStatement statement = null;
        statement = conn.prepareStatement(SQLString);

        for (int i = 0; i < ol.size(); i++) {
            Order o = ol.get(i);
            statement.setInt(1, o.getOno());
            statement.setInt(2, o.getCno());
            statement.setInt(3, o.getEno());
            statement.setString(4, o.getReceived());
            statement.setString(5, o.getShipped());
            statement.setInt(6, o.getVer());
            rowsInserted += statement.executeUpdate();
        }
        if (testRun) {
            System.out.println("insertOrders(): " + (rowsInserted == ol.size())); // for test
        }
        return (rowsInserted == ol.size());
    }

    // Update a list of orders 
    // using optimistic offline lock (version no)
    // Returns true if any conflict in version number
    public boolean updateOrders(ArrayList<Order> ol, Connection conn) throws SQLException {
        int rowsUpdated = 0;
        String SQLString = "update v_orders "
                + "set cno = ?, eno = ?, received = ?, shipped = ?, ver = ? "
                + "where ono = ? and ver = ?";
        PreparedStatement statement = null;

        statement = conn.prepareStatement(SQLString);
        for (int i = 0; i < ol.size(); i++) {
            Order o = ol.get(i);
            statement.setInt(1, o.getCno());
            statement.setInt(2, o.getEno());
            statement.setString(3, o.getReceived().substring(0, 10));
            statement.setString(4, o.getShipped().substring(0, 10));
            statement.setInt(5, o.getVer() + 1); // next version number
            statement.setInt(6, o.getOno());
            statement.setInt(7, o.getVer());   // old version number
            int tupleUpdated = statement.executeUpdate();
            if (tupleUpdated == 1) {
                o.incrVer();                       // increment version in current OrderObject
            }
            rowsUpdated += tupleUpdated;
        }
        if (testRun) {
            System.out.println("updateOrders: " + (rowsUpdated == ol.size())); // for test
        }
        return (rowsUpdated == ol.size());    // false if any conflict in version number             
    }

    // Insert a list of new order details
    // Returns true if all elements were inserted successfully
    public boolean insertOrderDetails(ArrayList<OrderDetail> odl, Connection conn) throws SQLException {
        String SQLString = "insert into v_odetails values (?,?,?)";
        PreparedStatement statement = null;

        int rowsInserted = 0;
        if (0 < odl.size()) {
            statement = conn.prepareStatement(SQLString);
            for (int i = 0; i < odl.size(); i++) {
                statement.setInt(1, odl.get(i).getOno());
                statement.setInt(2, odl.get(i).getPno());
                statement.setInt(3, odl.get(i).getQty());
                rowsInserted += statement.executeUpdate();
            }
        }
        if (testRun) {
            System.out.println("insertOrderDetails:" + (rowsInserted == odl.size())); // for test
        }
        return rowsInserted == odl.size();
    }

    //======  Methods to read from DB =======================================================
    // Retrieve a specific order and related order details
    // Returns the Order-object
    public Order getOrder(int ono, Connection conn) {
        Order o = null;
        String SQLString1 = // get order
                "select * "
                + "from v_orders "
                + "where ono = ?";
        String SQLString2 = // get order details
                "select * "
                + "from v_odetails od "
                + "where ono = ? ";
        PreparedStatement statement = null;

        try {
            //=== get order
            statement = conn.prepareStatement(SQLString1);
            statement.setInt(1, ono);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                o = new Order(ono,
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6));
            }

            //=== get order details
            statement = conn.prepareStatement(SQLString2);
            statement.setInt(1, ono);
            rs = statement.executeQuery();
            while (rs.next()) {
                o.addDetail(new OrderDetail(
                        ono,
                        rs.getInt(2),
                        rs.getInt(3)));
            }
        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - getOrder");
            System.out.println(e.getMessage());
        }
        if (testRun) {
            System.out.println("Retrieved Order: " + o);
        }
        return o;
    }
    // Retrieves the next unique order number from DB

    public int getNextOrderNo(Connection conn) {
        int nextOno = 0;
        String SQLString = "select v_orderseq.nextval  " + "from dual";
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(SQLString);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                nextOno = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - getNextOrderNo");
            System.out.println(e.getMessage());
        }
        return nextOno;
    }

    public boolean deleteOrder(ArrayList<Order> ol, Connection conn) throws SQLException {
        int rowsUpdated = 0;
        if (ol.size() == 0) {
            return true;
        } else {
            String sql1 = "DELETE FROM v_odetails WHERE ono = ?";
            String sql2 = "DELETE FROM v_orders WHERE ono = ? AND ver = ?";
            PreparedStatement statement1;
            PreparedStatement statement2;

            //Prepares first statement to delete order details.
            statement1 = conn.prepareStatement(sql1);
            statement1.setInt(1, ol.get(0).getOno());
            statement1.executeUpdate();

            //Prepares second statement to delete the order.
            statement2 = conn.prepareStatement(sql2);
            statement2.setInt(1, ol.get(0).getOno());
            statement2.setInt(2, ol.get(0).getVer());
            rowsUpdated += statement2.executeUpdate();
            return rowsUpdated == 1;
        }
    }

    public boolean updateOrderDetail(ArrayList<OrderDetail> odl, Connection conn) throws SQLException {
        int rowsUpdated = 0;
        if (odl.size() == 0) {
            return true;
        } else {
            String sql = "UPDATE v_odetails SET qty = ? WHERE ono = ? AND pno = ?";
            PreparedStatement statement;
            
            //Prepares first statement to update order details.
            statement = conn.prepareStatement(sql);
            statement.setInt(1, odl.get(0).getQty());
            statement.setInt(2, odl.get(0).getOno());
            statement.setInt(3, odl.get(0).getPno());
            rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1;
        }

    }
/**
 * 
 * @param odl
 * @param conn
 * @return
 * @throws SQLException 
 */
    public boolean deleteOrderDetail(ArrayList<OrderDetail> odl, Connection conn) throws SQLException {
        int rowsUpdated = 0;
        if (odl.size() == 0) {
            return true;
        } else {
            String sql = "DELETE FROM v_odetails WHERE ono = ? AND pno = ?";
            PreparedStatement statement = null;
            
            //Prepares the statement to delete a orderdetail
            statement = conn.prepareStatement(sql);
            statement.setInt(1, odl.get(0).getOno());
            statement.setInt(2, odl.get(0).getPno());
            rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1;
        }
        
    }
}

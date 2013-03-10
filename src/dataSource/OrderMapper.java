package dataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.Order;
import domain.OrderDetail;
import java.util.ArrayList;

//=== Maps between objects and tables
//=== Encapsulates SQL-statements
// hau
public class OrderMapper {
    //== load an order and the associated order details

    public Order getOrder(int ono, Connection con) {
        Order o = null;
        String SQLString1 = // get order
                "select * "
                + "from orders "
                + "where ono = ?";
        String SQLString2 = // get order details
                "select od.pno, od.qty "
                + "from odetails od "
                + "where od.ono = ? ";         // foreign key match 
        PreparedStatement statement = null;

        try {
            //=== get order
            statement = con.prepareStatement(SQLString1);
            statement.setInt(1, ono);     // primary key value
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                o = new Order(ono,
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getString(5));
            }

            //=== get order details
            statement = con.prepareStatement(SQLString2);
            statement.setInt(1, ono);          // foreign key value
            rs = statement.executeQuery();
            while (rs.next()) {
                o.addDetail(new OrderDetail(
                        ono,
                        rs.getInt(1),
                        rs.getInt(2)));
            }
        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - getOrder");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - getOrder");
                System.out.println(e.getMessage());
            }
        }
        return o;
    }

    //== Insert new order (tuple)
    public boolean saveNewOrder(Order o, Connection con) {
        int rowsInserted = 0;
        String SQLString1 =
                "select orderseq.nextval  "
                + "from dual";
        String SQLString2 =
                "insert into orders "
                + "values (?,?,?,?,?)";
        PreparedStatement statement = null;

        try {
            //== get unique ono
            statement = con.prepareStatement(SQLString1);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                o.setOno(rs.getInt(1));
            }

            //== insert tuple
            statement = con.prepareStatement(SQLString2);
            statement.setInt(1, o.getOno());
            statement.setInt(2, o.getCustomerNo());
            statement.setInt(3, o.getEmployeeNo());
            statement.setString(4, o.getReceived());
            statement.setString(5, o.getShipped());
            rowsInserted = statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - saveNewOrder");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - saveNewOrder");
                System.out.println(e.getMessage());
            }
        }
        return rowsInserted == 1;
    }

    //== Insert new order detail (tuple)
    public boolean saveNewOrderDetail(OrderDetail od, Connection con) {
        int rowsInserted = 0;
        String SQLString =
                "insert into odetails "
                + "values (?,?,?)";
        PreparedStatement statement = null;

        try {
            //== insert tuple
            statement = con.prepareStatement(SQLString);
            statement.setInt(1, od.getOno());
            statement.setInt(2, od.getPno());
            statement.setInt(3, od.getQty());
            rowsInserted = statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - saveNewOrderDetail");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - saveNewOrderDetail");
                System.out.println(e.getMessage());
            }
        }
        return rowsInserted == 1;
    }

    public boolean updateOrder(Order order, Connection con) {
        String SQLString = "UPDATE orders SET eno = ?, cno = ? WHERE ono = ?";
        PreparedStatement statement = null;
        int rowsInserted = 0;

        try {
            //== insert tuple
            statement = con.prepareStatement(SQLString);
            statement.setInt(1, order.getEmployeeNo());
            statement.setInt(2, order.getCustomerNo());
            statement.setInt(3, order.getOno());
            rowsInserted = statement.executeUpdate();
            Order tmp = getOrder(order.getOno(), con);
            System.out.println(tmp.getOno() + " " + tmp.getCustomerNo());
        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - updateOrderDetail");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
                con.commit();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - updateOrderDetail");
                System.out.println(e.getMessage());
            }
        }
        System.out.println(rowsInserted);
        return rowsInserted == 1;
    }

    public boolean deleteOrder(Order order, Connection con) {
        String SQLString1 = "DELETE FROM odetails WHERE ono = ?";
        String SQLString2 = "DELETE FROM orders WHERE ono = ?";
        PreparedStatement statement = null;
        int rowsInserted = 0;

        try {
            //== delete odetails tuple
            statement = con.prepareStatement(SQLString1);
            statement.setInt(1, order.getOno());
            rowsInserted = statement.executeUpdate();
            //== delete order tuple
            statement = con.prepareStatement(SQLString2);
            statement.setInt(1, order.getOno());
            rowsInserted += statement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - updateOrderDetail");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
                con.commit();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - updateOrderDetail");
                System.out.println(e.getMessage());
            }
        }
        System.out.println(rowsInserted);
        return rowsInserted == 2;
    }

    public boolean updateQty(OrderDetail od, Connection con) {
        String SQLString = "UPDATE odetails SET qty = ? WHERE ono = ? AND pno = ?";
        PreparedStatement statement = null;
        int rowsInserted = 0;

        try {
            //== update oder qty
            statement = con.prepareStatement(SQLString);
            statement.setInt(1, od.getQty());
            statement.setInt(2, od.getOno());
            statement.setInt(3, od.getPno());
            rowsInserted = statement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - updateOrderDetail");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
                con.commit();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - updateOrderDetail");
                System.out.println(e.getMessage());
            }
        }
        System.out.println(rowsInserted);
        return rowsInserted == 1;
    }

    public boolean deleteLine(OrderDetail od, Connection con) {
        String SQLString1 = "DELETE FROM odetails WHERE ono = ? AND pno = ?";
        PreparedStatement statement = null;
        int rowsInserted = 0;

        try {
            //== delete odetails tuple
            statement = con.prepareStatement(SQLString1);
            statement.setInt(1, od.getOno());
            statement.setInt(2, od.getPno());
            rowsInserted = statement.executeUpdate();

        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - updateOrderDetail");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
                con.commit();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - updateOrderDetail");
                System.out.println(e.getMessage());
            }
        }
        System.out.println(rowsInserted);
        return rowsInserted == 2;
    }

    /**
     *Returns a ArrayList of all orders in the database.
     * @param con
     * @return ArrayList<Order>
     */
    public ArrayList getAllOrders(Connection con) {
        ArrayList orderList = new ArrayList();

        Order o = null;
        String SQLString1 = // get order
                "select ono "
                + "from orders ";
        PreparedStatement statement = null;

        try {
            //=== get order
            statement = con.prepareStatement(SQLString1);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                orderList.add(rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("Fail in OrderMapper - getOrder");
            System.out.println(e.getMessage());
        } finally // must close statement
        {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Fail in OrderMapper - getOrder");
                System.out.println(e.getMessage());
            }
        }
        return orderList;
    }
}

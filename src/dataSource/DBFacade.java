package dataSource;

import java.sql.Connection;
import domain.*;
import java.util.ArrayList;

// Encapsulates the Data Source Layer
// Encapsulates connection handling 
// Implemented as a Singleton to provide global access from
// Domain classes and to ensure the use of max one connection
// hau
public class DBFacade {

	  private OrderMapper om; 
	  private Connection con;
	  
	  //== Singleton start
	  private static DBFacade instance;
	 
	  private DBFacade() {
		  om 	= new OrderMapper();
		  con 	= new DBConnector().getConnection();  // the connection will be released upon program 
		  											  // termination by the garbage collector		  
	  }
	  public static DBFacade getInstance()
	  {
		  if(instance == null)
			  instance = new DBFacade();
		  return instance;
	  }
	  //== Singleton end

	  
	  public Order getOrder(int ono) 
	  {
		  return om.getOrder(ono, con);	      
	  }
	  
	  public boolean saveNewOrder(Order o)
	  { 
	    return om.saveNewOrder(o, con);
	  }
	  
	  public boolean saveNewOrderDetail(OrderDetail od)
	  {
	    return om.saveNewOrderDetail(od, con);
	  }
          
          public boolean updateOrder(Order o){
              return om.updateOrder(o, con);
          }
          
          public boolean deleteOrder(Order o){
              return om.deleteOrder(o, con);
          }
          
          public boolean updateQty(OrderDetail od){
              return om.updateQty(od, con);
          }
          
          public boolean deleteLine(OrderDetail od){
              return om.deleteLine(od, con);
          }
          
          public ArrayList getAllOno(){
              return om.getAllOrders(con);
          }
	
}

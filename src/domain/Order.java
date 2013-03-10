package domain;

import java.util.ArrayList;
//=== hau
public class Order
{

	  private int ono; 
	  private int cno; // A FK that should be mapped to a reference
	  private int eno; // A FK that should be mapped to a reference
	  private String received;
	  private String shipped;
	  private ArrayList<OrderDetail> orderDetails;
	
	  public Order(int o, int c, int e, String r, String s)
	  {
	    ono = o;
	    cno = c;
	    eno = e;
	    received = r;
	    shipped = s;
	    orderDetails = new ArrayList<OrderDetail>();
	  }
	  
	  //== accessors
	  public int getOno()
	  {
	    return ono;
	  }
	  public void setOno(int ono)
	  {
	    this.ono = ono;
	  }
	
	  public void setCustomer(int c)
	  {
	    this.cno = c;
	  }
	
	  public int getCustomerNo()
	  {
	    return cno;
	  }
	
	  public void setEmployee(int e)
	  {
	    this.eno = e;
	  }
	
	  public int getEmployeeNo()
	  {
	    return eno;
	  }
	
	  public void setReceived(String received)
	  {
	    this.received = received;
	  }
	
	  public String getReceived()
	  {
	    return received;
	  }
	
	  public void setShipped(String shipped)
	  {
	    this.shipped = shipped;
	  }
	
	  public String getShipped()
	  {
	    return shipped;
	  }
	  public void addDetail(OrderDetail od)
	  {
	    orderDetails.add(od);
	  }
	  public String toString()
	  {
	    return ono + " " + cno + " " + eno + " " + received + " " + shipped;
	  }
	
	  String detailsToString()
	  {
	    String res = "";
	    for (int i = 0; i < orderDetails.size(); i++) 
	    {
	      res += orderDetails.get(i).toString() + "\n";
	    }
	    return res;
	  }
}

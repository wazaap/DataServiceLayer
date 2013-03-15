package domain;

import java.sql.Date;
import java.util.ArrayList;

//======= 2010/hau

public class Order
{
  private int ono;
  private int cno;
  private int eno;
  private Date received;
  private Date shipped;
  private int ver;
  private ArrayList<OrderDetail> orderDetails;

  public Order(int o, int c, int e, Date r, Date s, int v)
  {
    ono = o;
    cno = c;
    eno = e;
    received = r;
    shipped = s;
    ver = v;
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

  public void setCno(int c)
  {
    this.cno = c;
  }

  public int getCno()
  {
    return cno;
  }

  public void setEno(int e)
  {
    this.eno = e;
  }

  public int getEno()
  {
    return eno;
  }

  public void setReceived(Date received)
  {
    this.received = received;
  }

  public Date getReceived()
  {
    return received;
  }

  public void setShipped(Date shipped)
  {
    this.shipped = shipped;
  }

  public Date getShipped()
  {
    return shipped;
  }
  public int getVer()
  {
    return ver;
  }
  public void addDetail(OrderDetail od)
  {
    orderDetails.add(od);
  }
  
  public String toString()
  {
    return "  Ono: " + ono + 
           "  Cno: " + cno + 
           "  Cno: " + received + 
           "  Cno: " + shipped + 
           "  Eno: " + eno;
  }


  public String detailsToString()
  {
    String res = "";
    for (int i = 0; i < orderDetails.size(); i++) 
    {
      res += orderDetails.get(i).toString() + "\n";
    }
    return res;
  }


  public void incrVer()
  {
    ver++;
  }
  
  public boolean equals(Order o)
  {
    return (ono == o.getOno() &&
            cno == o.getCno()&&
            eno == o.getEno()&&
            received.equals(o.getReceived())&&
            shipped.equals(o.getShipped()) &&
            ver == o.getVer()); 
  }
  
  public boolean updateDetail (int pno, int qty){
      boolean result = false; 
      for (int i = 0; i < orderDetails.size(); i++)
      {
          if (orderDetails.get(i).getPno() == pno){
              orderDetails.get(i).setQty(qty);
              result = true;
          } else {
              result = false;
          }
      }
      return result;
  }
}



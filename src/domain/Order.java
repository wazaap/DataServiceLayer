package domain;

import java.util.ArrayList;

//======= 2010/hau

public class Order
{
  private int ono;
  private int cno;
  private int eno;
  private String received;
  private String shipped;
  private int ver;
  private ArrayList<OrderDetail> orderDetails;

  public Order(int o, int c, int e, String r, String s, int v)
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
  
  public void updateDetail (int pno, int qty){
      for (int i = 0; i< orderDetails.size(); i++)
      {
          if (orderDetails.get(i).getPno() == pno){
              orderDetails.get(i).setQty(qty);
          }
      }
  }
}



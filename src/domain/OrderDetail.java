package domain;
//=== hau
public class OrderDetail {
	  private int ono; // ono and pno are FK's but at the same time a constitute the PK!  
	  private int pno; // Therefore they are kept in the object (Identity Map)
	  private int qty;
	  public OrderDetail(int on, int pn, int q)
	  {
	    ono = on;
	    pno = pn;
	    qty = q;
	  }
	  
	  //=== accessors
	  public int getOno()
	  {
	    return ono;
	  }
	  public int getPno()
	  {
	    return pno;
	  }

	  public int getQty()
	  {
	    return qty;
	  }
	  
	  public String toString()
	  {
	    return ono + " " + pno + " " + qty;
	  }

}

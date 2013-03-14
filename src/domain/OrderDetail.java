package domain;

//====== 2010/hau

public class OrderDetail
{
private int ono;
private int pno;
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

    public void setQty(int qty) {
        this.qty = qty;
    }

}


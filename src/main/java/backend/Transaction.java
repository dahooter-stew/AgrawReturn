package backend;

import java.io.Serializable;

public abstract class Transaction implements Serializable
{
	
	String trans_id;
	int amount;
	double price; 
	String date;
  User user;
	
	public Transaction (String trans_id, int amount, double price, String date) {
		this.trans_id = trans_id;
		this.amount = amount;
		this.price = price;
		this.date = date;
	}
	
	public String getId() {
		return this.trans_id;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public String getDate() {
		return this.date;
	}

  public User getUser()
  {
    return this.user;
  }
	
	public void setId(String trans_id) {
		this.trans_id = trans_id;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setPrice(double price) {
		this.price = price;	
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
  public void setUser(User user)
  {
    this.user = user;
  }
	
	public abstract boolean validateTransaction(); // TODO: CHECK IF THE PRODUCT IS PART OF OUR PRODUCT ROSER

  @Override
  public boolean equals (Object o)
  {
    if (o instanceof Transaction other)
      return trans_id.equals(other.getId()) && (price == other.getPrice()) && (amount == other.getAmount()) && date.equals(other.getDate()) && user.equals(other.getUser());
    else
      return false;
  }
}

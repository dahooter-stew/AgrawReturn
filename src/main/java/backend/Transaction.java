package backend;

import java.io.Serializable;

public abstract class Transaction implements Serializable
{
	
  Product product;
  String date;
  int amount;
  double price;
  User user;
		
	public Transaction (Product product, int amount, String date, User user) {
		this.product = product;
		this.amount = amount;
		this.date = date;
    this.user = user;

    price = amount * product.getPrice();
	}
	
	public Product getProduct() {
		return this.product;
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
	
	public void setProduct(Product product) {
		this.product = product;
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
  public String toString()
  {
    return product + "|" + date + "|" + amount + " - " + price + "\n> " + user.toString() + "-" + (this instanceof Purchase ? "Purcahse" : "Sale"); 
  }

  @Override
  public boolean equals (Object o)
  {
    if (o instanceof Transaction other)
      return product.equals(other.getProduct()) && (price == other.getPrice()) && (amount == other.getAmount()) && date.equals(other.getDate()) && user.equals(other.getUser());

    else
      return false;
  }
}

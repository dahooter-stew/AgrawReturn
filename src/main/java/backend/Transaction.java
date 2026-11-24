/*************************************************************************
 *
 * CMSC 22 PROJECT
 * Agriculture Data Management System
 *
 * AgrawReturn()
 *
 * Description:
 * AgrawReturn() is an organization that aims to address local problems regarding 
 * fruit wastes by providing services for a) processing of fruit waste to develop 
 * agricultural-use products and b) distribution of fruit wastes for other 
 * organizations/parties use.
 *
 * By:
 * Bajade, Jann Alexandrei C.
 * Sta Romana, Maria Mikaela C.
 */

package backend;

import java.io.Serializable;

// Transaction base class
// Stores (cash) transactions
//  whether Sale (purchased by customer)
//  or Purschase (purchased by us the company)
public abstract class Transaction implements Serializable
{
  private static final long serialVersionUID = 1L;
	
  // Attributes
  protected Product product; // stores what product was bought
  protected String date;
	protected int amount;
	protected double price; 
  protected User user; // stores who made the transaction, 
                       // for tracking and filtering
	
  // Constructor
	public Transaction (Product product, int amount, String date, User user) {
		this.product = product;
		this.amount = amount;
		this.date = date;
    this.user = user;

    price = amount * product.getPrice();
	}
	
  // Getters
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
	
  // Setters
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
	
  // To be overriden by subclasses
	public abstract boolean validateTransaction(); // TODO: CHECK IF THE PRODUCT IS PART OF OUR PRODUCT ROSER

  // Java Object overrides, for debugging
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

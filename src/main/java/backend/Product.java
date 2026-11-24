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

// simple product class
// essentially just a class with attributes
// + getters & setters
// Just stores Product information, Product objects stored by
// ProductManager are products officially registered by our 
// company (we are selling). However we also use Product to 
// record the company purchasing from other companies.
public class Product implements Serializable 
{
  private static final long serialVersionUID = 1L;

  // Attributes
  private String id;
  private String name;
  private String description;
  private double price;

  // Constructor
  public Product(String id, String name, String description, double price) 
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
  }

  // Getters and Setters
  public String getId() 
  { 
    return id; 
  }

  public void setId(String id) 
  { 
    this.id = id; 
  }

  public String getName() 
  { 
    return name; 
  }

  public void setName(String name) 
  { 
    this.name = name; 
  }

  public String getDescription() 
  { 
    return description; 
  }

  public void setDescription(String description) 
  { 
    this.description = description; 
  }

  public double getPrice() 
  { 
    return price; 
  }

  public void setPrice(double price) 
  { 
    this.price = price; 
  }

  // Java Object Overrides, for debugging
  @Override
  public String toString() 
  {
      return id + "| " + name + " ($" + price + ")" + "\n> " + description;
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof Product other)
      return id.equals(other.getId()) && name.equals(other.getName());
    else
      return false;
  }
}


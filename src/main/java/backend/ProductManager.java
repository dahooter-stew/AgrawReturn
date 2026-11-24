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
import java.util.HashMap;
import java.util.Map;

// Product manager, manages the Products officially registered
// by the company. Validation checks happen here, if a product 
// not recorded by us ever gets attempted to be recorded, it 
// should be blocked
public class ProductManager implements Serializable
{
  private static final long serialVersionUID = 1L;
	
	private HashMap<String, Product> products = new HashMap<>();
	
  // Add
	public Product addProduct(String id, String name, String description, double price) 
  {
		//ADD PROBLEM CASES
		if (id == null || id.isBlank())
			throw new IllegalArgumentException("No product ID!");
		else if (products.containsKey(id))
			throw new IllegalArgumentException("Product with that ID already exists!");
		else if (name == null || name.isBlank())
			throw new IllegalArgumentException("No product name!");
		else if (hasProductByName(name))
			throw new IllegalArgumentException("Product with that name already exists!");
		else if (0 > price)
			throw new IllegalArgumentException("Invalid price!");
		
		Product product = new Product(id, name, description, price);
		products.put(id, product);
		return product;
	}
	
	public HashMap<String, Product> getProducts()
  {
		return new HashMap<>(products); // NOTE THAT ONLY A COPY IS RETURNED
	}
	
  // Find by ID or Name
  
	public Product getProductById(String id) 
  {
		return products.get(id);
	}
	
	public Product getProductByName(String name) 
  {
		for (Product product : products.values()) 
			if (product.getName().equals(name))
				return product;

		return null;
	}

	public boolean removeProduct(String id) 
  {
		if (id == null || !products.containsKey(id))
			return false;

		products.remove(id);
    StoreSystem.instance().inventory.updateInventory();
		return true;
	}

  // Just searches names of products
	public boolean hasProductByName(String name) 
  {
		for (Product product : products.values())
			if (product.getName().equals(name))
				return true;

		return false;
	}

  // Iterate though values
  public boolean hasProduct(Product product)
  {
		for (Product p : products.values())
			if (p.equals(product))
				return true;

		return false;
  }

  // For debugging
  public void printProducts()
  {
    System.out.println("=====Product Manager=====");

    for (Product product : products.values())
    {
      System.out.println(product.toString());
    }

    System.out.println("=========================");
  }
}

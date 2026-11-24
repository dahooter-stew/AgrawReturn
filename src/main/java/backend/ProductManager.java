package backend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProductManager implements Serializable
{
	
	private HashMap<String, Product> products = new HashMap<>();
	
	public Product addProduct(String id, String name, String description, double price) 
  {
		//ADD PROBLEM CASES
		if (id == null || id.isBlank())
			throw new IllegalArgumentException("No product ID!");
		else if (products.containsKey(id))
			throw new IllegalArgumentException("Product with that ID already exists!");
		else if (name == null || name.isBlank())
			throw new IllegalArgumentException("No product name!");
		else if (hasProductByName(name)) // FIXED ADD: hasProductByName is now case-insensitive, so "seeds" and "Seeds" count as duplicates
			throw new IllegalArgumentException("Product with that name already exists!");
		else if (0 > price)
			throw new IllegalArgumentException("Invalid price!");
		
		Product product = new Product(id, name, description, price);
		products.put(id, product);
		return product;
	}
	
	public HashMap<String, Product> getProducts(){
		return new HashMap<>(products); // NOTE THAT ONLY A COPY IS RETURNED
	}
	
	public Product getProductById(String id) 
  {
		return products.get(id);
	}
	
	// NOT CASE SENSITIVE, PARTIAL MATCHES ARE OK TOO (FOR SEARCHING SOON...)
	public Product getProductByName(String name) 
  {
		if (name == null || name.isBlank())
			return null;

		String search = name.trim().toLowerCase();
		for (Product product : products.values())
			if (product.getName().trim().toLowerCase().equals(search))
				return product;

		// settles for just containing that search within that product
		for (Product product : products.values())
			if (product.getName().toLowerCase().contains(search))
				return product;

		return null;
	}

	public boolean removeProduct(String id) {
		if (id == null || !products.containsKey(id)){
			return false;
		}
		products.remove(id);
    	StoreSystem.instance().inventory.updateInventory();
		return true;
	}

	// FOR DUPLICATE CHECKING, FINDS NON CASE SENSITIVE MATCH!
	public boolean hasProductByName(String name) {
		if (name == null){
			return false;
		}
		String search = name.trim().toLowerCase();
		for (Product product : products.values())
			if (product.getName().trim().toLowerCase().equals(search)){
				return true;
			}
		return false;
	}

  public boolean hasProduct(Product product){
		for (Product p : products.values())
			if (p.equals(product))
				return true;

		return false;
  }

  public void printProducts(){
    System.out.println("=====Product Manager=====");
    for (Product product : products.values()) {
      System.out.println(product.toString());
    }
    System.out.println("=========================");
  }
}
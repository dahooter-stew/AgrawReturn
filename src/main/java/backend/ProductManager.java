package backend;

import java.util.HashMap;

public class ProductManager {
	
	private HashMap<String, Product> products = new HashMap<>();
	
	public Product addProduct(String id, String name, String description, double price) {
		//ADD PROBLEM CASES
		
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("No product ID!");
		} else if (products.containsKey(id)) {
			throw new IllegalArgumentException("Product with that ID already exists!");
		} else if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("No product name!");
		} else if (hasProductByName(name)){
			throw new IllegalArgumentException("Product with that name already exists!");
		} else if (0 > price) {
			throw new IllegalArgumentException("Invalid price!");
		}
		
		Product product = new Product(id, name, description, price);
		products.put(id, product);
		return product;
	}
	
	public HashMap<String, Product> getProducts(){
		return new HashMap<>(products); // NOTE THAT ONLY A COPY IS RETURNED
	}
	
	public Product getProductById(String id) {
		return products.get(id);
	}
	
	public Product getProductByName(String name) {
		for (Product product : products.values()) {
			
			if (product.getName().equals(name)) {
				return product;
			}
		}
		return null;
	}

	public boolean removeProduct(String id) {
		if (id == null || !products.containsKey(id)) {
			return false;
		}
		products.remove(id);
		return true;
	}

	public boolean hasProductByName(String name) {
		for (Product product : products.values()) {
			
			if (product.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

}

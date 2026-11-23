package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

// inventory management system
public class Inventory implements Serializable 
{
  // for saving into files in the future
  private static final long serialVersionUID = 3L;

  private Map<Product, Integer> items;

  public Inventory() 
  {
    this.items = new HashMap<>();
  }

  public HashMap<Product, Integer> getItems() 
  {
    return new HashMap<>(items);
  }

  public void addProduct(Product product, int initialQuantity)
  {
    if (product == null || items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return;

    items.put(product, initialQuantity);
  }

  public boolean removeProduct(Product product) 
  {
    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return false;
    
    return items.remove(product) != null;
  }

  public Integer getProductQuantity(Product product)
  {
    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return null;

    return items.get(product);
  }

  private boolean updateStockQuantity(Product product, int newQuantity) 
  {
    if (newQuantity < 0) 
      return false;

    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return false;

    return items.replace(product, newQuantity) != null;
  }

  public boolean restockProduct(Product product, int amount) 
  {
    if (amount < 0)
      return false;

    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return false;
    
    int currentAmount = getProductQuantity(product);
    return updateStockQuantity(product, currentAmount + amount);
  }

  public boolean decreaseProduct(Product product, int amount)
  {
    if (amount < 0)
      return false;

    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return false;
    
    int currentAmount = getProductQuantity(product);
    if (currentAmount - amount < 0)
      return false;
    return updateStockQuantity(product, currentAmount - amount);
  }
}

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
  private ProductManager productManager;

  public Inventory() 
  {
    this.items = new HashMap<>();
  }

  public HashMap<Product, Integer> getItems() 
  {
    return new HashMap<>(items);
  }

  public void addProductById(String id, int initialQuantity)
  {
    Product product = productManager.getProductById(id);

    if (product == null && items.containsKey(product))
      return;

    items.put(product, initialQuantity);
  }

  public boolean removeProduct(String id) 
  {
    Product product = productManager.getProductById(id);

    if (product != null && items.containsKey(product))
      return items.remove(product) != null;
    
    return false;
  }

  public int getProductQuantity(String id)
  {
    Product product = productManager.getProductById(id);

    if (product != null)
      return items.get(product);
    else
      return 0;
  }

  private boolean updateStockQuantity(String id, int newQuantity) 
  {
    if (newQuantity < 0) 
      return false;

    Product product = productManager.getProductById(id);

    if (product == null && !items.containsKey(product))
      return false;

    return items.replace(product, newQuantity) != null;
  }

  public boolean restockProduct(String id, int amount) 
  {
    if (amount < 0)
      return false;

    Product product = productManager.getProductById(id);

    if (product == null && !items.containsKey(product))
      return false;
    
    int currentAmount = getProductQuantity(id);
    return updateStockQuantity(id, currentAmount + amount);
  }

  public boolean decreaseProduct(String id, int amount)
  {
    if (amount < 0)
      return false;

    Product product = productManager.getProductById(id);

    if (product == null && !items.containsKey(product))
      return false;
    
    int currentAmount = getProductQuantity(id);
    if (currentAmount - amount < 0)
      return false;

    return updateStockQuantity(id, currentAmount - amount);
  }
}

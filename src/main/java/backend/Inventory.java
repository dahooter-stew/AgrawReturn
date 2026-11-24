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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

// inventory management system
public class Inventory implements Serializable 
{
  // for saving into files in the future
  private static final long serialVersionUID = 1L;

  // Product and how much left in store
  private Map<Product, Integer> items;

  // Constructor, initializes hashmap
  public Inventory() 
  {
    this.items = new HashMap<>(); }

  // returns list of Products in inventory (without amount)
  public List<Product> getItems() 
  {
    return new ArrayList<>(items.keySet());
  }

  // only adds product if not in inventory yet
  // and product matches the one stored in 
  // product manager
  public void addProduct(Product product, int initialQuantity)
  {
    if (product == null || items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return;

    items.put(product, initialQuantity);
  }

  // removes only if in inventory, and is stored by 
  // product manager
  public boolean removeProduct(Product product) 
  {
    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return false;
    
    return items.remove(product) != null;
  }

  // returns how much is in storage
  public Integer getProductQuantity(Product product)
  {
    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return null;

    return items.get(product);
  }

  // overrides current amount
  private boolean updateStockQuantity(Product product, int newQuantity) 
  {
    if (newQuantity < 0) 
      return false;

    if (product == null || !items.containsKey(product) ||
        !product.equals(StoreSystem.instance().productManager.getProductById(product.getId())))
      return false;

    return items.replace(product, newQuantity) != null;
  }

  // adds to current amount
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

  // decreases amount
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

  // if product is no longer in product manager,
  // remove it
  public void updateInventory()
  {
    for (Product item : items.keySet())
      if (!StoreSystem.instance().productManager.hasProduct(item))
        items.remove(item);
  }

  // for debugging
  public void printInventory()
  {
    System.out.println("=====Inventory=====");

    for (Map.Entry<Product, Integer> pair : items.entrySet())
    {
      System.out.println(pair.getKey().toString() + "|" + pair.getValue().toString());
    }

    System.out.println("===================");
  }
}

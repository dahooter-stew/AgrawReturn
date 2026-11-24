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

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

// StoreSystem | Singleton
// The other systems (UserManager, Inventory, TransactionManager, & ProductManager)
// Aren't singletons themselves but this singleton contains the instances that should 
// be used, at least for prod. 
// Because the classes codepend on each other I decided to implement a single place 
// for classes to communicate to each other to reduce references of each other in code 
// and having to initialize references of each other
// This turns out to be an Anti-Pattern, but I carefully used this to be the barest 
// minimum as possible, and external packages are expected to store copies of the 
// managers and not use StoreSystem singleton
//
// Also implemented as a singleton so Serialization and Persistence is simple,
// only this class saves and all the other objects it stores are recursively saved too
public class StoreSystem implements Serializable
{
  private static final long serialVersionUID = 1L;

  // single instance of StoreSystem
  private static StoreSystem instance = null;

  // manager/system instances
  public final ProductManager productManager = new ProductManager();
  public final TransactionManager transactionManager = new TransactionManager();
  public final UserManager userManager = new UserManager();
  public final Inventory inventory = new Inventory();

  // makes sure manager/systems are initialized
  private StoreSystem() {}

  // The get instance function, singleton is lazy loaded
  public static StoreSystem instance()
  {
    if (instance == null)
      instance = new StoreSystem();

    return instance;
  }

  // Persistence

  public void saveToFile(String filePath) throws IOException 
  {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) 
    {
      out.writeObject(this);
    }
  }

  public void loadFromFile(String filePath) 
  {
    File file = new File(filePath);
    if (!file.exists()) 
    {
      instance = new StoreSystem();
      return;
    }

    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) 
    {
      instance = (StoreSystem) in.readObject();
    } 
    catch (IOException | ClassNotFoundException e) 
    {
      System.err.println("Failed to load save file, starting fresh: " + e.getMessage());
      instance = new StoreSystem();
    }
  }
}

package backend;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class StoreSystem implements Serializable
{
  private static final long serialVersionUID = 1L;

  private static StoreSystem instance = null;

  public final ProductManager productManager = new ProductManager();
  public final TransactionManager transactionManager = new TransactionManager();
  public final UserManager userManager = new UserManager();
  public final Inventory inventory = new Inventory();
  public final BalanceManager balanceManager = new BalanceManager();

  private StoreSystem() {}

  public static StoreSystem instance()
  {
    if (instance == null)
      instance = new StoreSystem();

    return instance;
  }

  public void saveToFile(String filePath) throws IOException 
  {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) 
    {
      out.writeObject(this);
    }
  }

  public static void loadFromFile(String filePath) 
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

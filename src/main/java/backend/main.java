package backend;

import java.io.IOException;
import java.io.Serializable;

public class main
{
  public static void main(String[] args) 
  {
    // StoreSystem.instance().productManager.addProduct("2025-10100", "Milk", "Milky goodness", 1000.0);
    // StoreSystem.instance().productManager.addProduct("2025-20056", "Eggs", "Unfortunate Child", 1920.0);
    // StoreSystem.instance().productManager.addProduct("2025-12039", "Flour", "MMMM bred", 121.0);
    // StoreSystem.instance().productManager.printProducts();
    // System.out.println();
    //
    // StoreSystem.instance().userManager.addUser(new Manager("Jann", "jann"));
    // StoreSystem.instance().userManager.printUsers();
    // System.out.println();
    //
    // // System.out.println(
    // //     StoreSystem.instance().productManager.getProductById("2025-10100").toString()
    // //     );
    // // System.out.println("====");
    //
    // StoreSystem.instance().transactionManager.processTransaction(
    //   new Sale(
    //     StoreSystem.instance().productManager.getProductById("2025-10100"),
    //     10, "11-23-26", StoreSystem.instance().userManager.getUser("Jann")
    //   )
    // );
    // StoreSystem.instance().transactionManager.processTransaction(
    //   new Sale(
    //     StoreSystem.instance().productManager.getProductByName("Eggs"),
    //     90, "11-23-26", StoreSystem.instance().userManager.getUser("Jann")
    //   )
    // );
    // StoreSystem.instance().transactionManager.printTransactions();
    // System.out.println();
    //
    // StoreSystem.instance().inventory.addProduct(
    //     StoreSystem.instance().productManager.getProductByName("Milk"), 10
    // );
    // StoreSystem.instance().inventory.printInventory();
    //
    // try {
    //   StoreSystem.instance().saveToFile("src/main/java/data/hehe.ser");
    // } catch (IOException err) {}
    //
    // System.out.println();
    // System.out.println();
    // System.out.println();
    // System.out.println();
    // System.out.println();
    // System.out.println();

    StoreSystem.instance().loadFromFile("src/main/java/data/hehe.ser");
    StoreSystem.instance().inventory.printInventory();
    System.out.println();
    StoreSystem.instance().userManager.printUsers();
    System.out.println();
    StoreSystem.instance().transactionManager.printTransactions();
    System.out.println();
    StoreSystem.instance().productManager.printProducts();
    System.out.println();
  }
}

package backend;

import java.io.IOException;

public class main
{
  public static void main(String[] args) 
  {
    // ADD PRODUCT
    Product milk = StoreSystem.instance().productManager.addProduct("2025-10100", "Milk", "Milky goodness", 1000.0);
    Product eggs = StoreSystem.instance().productManager.addProduct("2025-20056", "Eggs", "Unfortunate Child", 1920.0);
    Product flour = StoreSystem.instance().productManager.addProduct("2025-12039", "Flour", "MMMM bred", 121.0);
    StoreSystem.instance().productManager.printProducts();
    System.out.println();

    // ADD USER
    Manager jann = new Manager("Jann", "jann");
    Clerk juan = new Clerk("Juan", "juan");
    StoreSystem.instance().userManager.addUser(jann); // manager
    StoreSystem.instance().userManager.addUser(juan); // clerk
    StoreSystem.instance().userManager.printUsers();
    System.out.println();

    // System.out.println(
    //     StoreSystem.instance().productManager.getProductById("2025-10100").toString()
    //     );
    // System.out.println("====");

    // ADD TRANSACTIONS
    StoreSystem.instance().transactionManager.processTransaction(new Sale(milk, 10, "11-23-26", jann));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(eggs, 90, "11-23-26", juan));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(flour, 21, "11-23-26", juan));
    StoreSystem.instance().transactionManager.printTransactions();
    System.out.println();

    StoreSystem.instance().inventory.addProduct(eggs, 1000);
    StoreSystem.instance().inventory.addProduct(milk, 1000);
    StoreSystem.instance().inventory.addProduct(flour, 1000);
    StoreSystem.instance().inventory.printInventory();

    try {
      StoreSystem.instance().saveToFile("src/main/java/data/hehe.ser");
    } catch (IOException err) {}

    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();

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

package backend;

import java.io.IOException;

public class main
{
  public static void main(String[] args) 
  {
    // HARDCODED ITEMS FOR TESTING

     // ADD PRODUCT
    Product biochar = StoreSystem.instance().productManager.addProduct("PROD-001", "FRUIT WASTE BIOCHAR", "500g", 50.0);
    Product repellent = StoreSystem.instance().productManager.addProduct("PROD-002", "PEST REPELLENT", "500ml", 80.0);
    Product fertilizer = StoreSystem.instance().productManager.addProduct("PROD-003", "LIQUID FERTILIZER", "500ml", 50.0);
    Product substrate = StoreSystem.instance().productManager.addProduct("PROD-004", "MUSHROOM GROWTH SUBSTRATE", "1kg", 100.0);
    StoreSystem.instance().productManager.printProducts();
    System.out.println();

    // ADD TO INVENTORY
    StoreSystem.instance().inventory.addProduct(biochar, 200);
    StoreSystem.instance().inventory.addProduct(repellent, 150);
    StoreSystem.instance().inventory.addProduct(fertilizer, 150);
    StoreSystem.instance().inventory.addProduct(substrate, 200);
    StoreSystem.instance().inventory.printInventory();

    // ADD USER
    Manager katniss = new Manager("Katniss", "everdeen");
    Manager jann = new Manager("Jann", "jann");
    Manager mikaela = new Manager("Mikaela", "maria");
    Clerk phylan = new Clerk("Phylan", "matheus");
    Clerk jelen = new Clerk("Jelen", "jelen");
    Clerk jabien = new Clerk("Jabien", "s444chiel");
    Clerk conrad = new Clerk("Conrad", "rafufu");
    Clerk aaron = new Clerk("Aaron", "yusei");

    // manager
    StoreSystem.instance().userManager.addUser(katniss); 
    StoreSystem.instance().userManager.addUser(jann);   
    StoreSystem.instance().userManager.addUser(mikaela); 

    // clerks
    StoreSystem.instance().userManager.addUser(phylan); 
    StoreSystem.instance().userManager.addUser(jelen);   
    StoreSystem.instance().userManager.addUser(jabien); 
    StoreSystem.instance().userManager.addUser(conrad); 
    StoreSystem.instance().userManager.addUser(aaron);   
    StoreSystem.instance().userManager.printUsers();
    System.out.println();

        // ADD SALES
    StoreSystem.instance().transactionManager.processTransaction(new Sale(biochar, 5, "11-15-26", aaron));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(fertilizer, 3, "11-15-26", conrad));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(substrate, 13, "11-15-26", jabien));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(repellent, 6, "11-16-26", jabien));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(biochar, 3, "11-16-26", aaron));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(substrate, 18, "11-16-26", jabien));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(fertilizer, 2, "11-17-26", jelen));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(fertilizer, 1, "11-18-26", aaron));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(substrate, 20, "11-18-26", jabien));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(biochar, 4, "11-18-26", phylan));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(biochar, 5, "11-19-26", aaron));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(repellent, 1, "11-19-26", jelen));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(fertilizer, 1, "11-19-26", jelen));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(substrate, 10, "11-19-26", phylan));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(biochar, 1, "11-20-26", conrad));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(repellent, 1, "11-20-26", jabien));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(repellent, 1, "11-21-26", phylan));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(fertilizer, 2, "11-21-26", jabien));
    StoreSystem.instance().transactionManager.processTransaction(new Sale(biochar, 5, "11-22-26", aaron));

    // ADD PURCHASES // WE SOURCE OUR BIOCHAR ELSE WHERE
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(biochar, 5, "11-08-26", phylan, "Los Baños Wet Market"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(biochar, 10, "11-08-26", jabien, "AllyMango"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(fertilizer, 4, "11-09-26", jelen, "Kuya Jann's Fruit Stand"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(biochar, 15, "11-09-26", aaron, "Hootie Frutti"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(fertilizer, 5, "11-10-26", phylan, "Los Baños Wet Market"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(repellent, 2, "11-10-26", jabien, "Aiona's Orchard"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(repellent, 2, "11-10-26", conrad, "Hootie Frutti"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(biochar, 3, "11-11-26", jelen, "Fruit Trade Center"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(biochar, 10, "11-11-26", aaron, "AllyMango"));
    StoreSystem.instance().transactionManager.processTransaction(new Purchase(fertilizer, 3, "11-11-26", jabien, "Hootie Frutti"));
    StoreSystem.instance().transactionManager.printTransactions();

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

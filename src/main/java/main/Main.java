package main;

import backend.StoreSystem;
import backend.Clerk;
import backend.Inventory;
import backend.Manager;
import backend.Product;
import backend.ProductManager;
import backend.TransactionManager;
import backend.User;
import backend.UserManager;
import frontend.LoginView;
import frontend.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application
{
  private Stage primaryStage;

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;

    StoreSystem.instance().loadFromFile("src/main/java/data/hehe.ser");

    // TransactionManager.Load();
    // productManager = new ProductManager();
    // inventory = new Inventory();

    // products go in the catalog first, then into the inventory
    // Product charcoal = productManager.addProduct("PROD-001", "Charcoal", "bag", 450.00);
    // Product seeds = productManager.addProduct("PROD-002", "Seeds", "1kg high-yield seed pack", 280.00);
    // inventory.addProduct(charcoal, 100);
    // inventory.addProduct(seeds, 50);

    // DEMO ACCOUNTS
    // users = new UserManager();
    // users.addUser(new Manager("jannbajade", "jannbajade"));
    // users.addUser(new Clerk("mikaela", "mikaela"));

    showLoginScreen();
    primaryStage.setTitle("Store Management System");
    primaryStage.show();
  }

  private void showLoginScreen()
  {
    LoginView loginView = new LoginView(StoreSystem.instance().userManager, this::showMainView);
    primaryStage.setScene(new Scene(loginView, 400, 350));
  }

  private void showMainView(User user)
  {
    MainView mainView = new MainView(user, StoreSystem.instance().userManager, StoreSystem.instance().inventory, StoreSystem.instance().productManager, StoreSystem.instance().transactionManager, this::showLoginScreen);   // LOG OUT goes back to login
    primaryStage.setScene(new Scene(mainView, 1000, 600));
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}

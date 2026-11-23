package main;

import backend.StoreSystem;

import java.io.IOException;

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

    try 
    {
      StoreSystem.instance().saveToFile("src/main/java/data/hehe.ser");
    } catch(IOException err) 
    { 
      System.out.println("WHY");
    }
    System.out.println("BYE");
  }
}

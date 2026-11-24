package main;

import backend.StoreSystem;
import backend.User;
import backend.UserManager;
import frontend.LoginView;
import frontend.MainView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main extends Application
{
  private static final String[] CSS_CLASSPATHS = { "/main/resources/style.css", "/style.css" };
  private static final String[] CSS_DISK_PATHS = { "src/main/java/main/resources/style.css", "src/main/resources/style.css" };

  private Stage primaryStage;

  @Override
  public void start(Stage primaryStage) 
  {
    this.primaryStage = primaryStage;

    showLoginScreen();
    primaryStage.setTitle("Store Management System");
    primaryStage.show();
  }

  private void showLoginScreen()
  {
    LoginView loginView = new LoginView(StoreSystem.instance().userManager, this::showMainView);

    // Wrap in a StackPane so LoginView renders as a centered rounded card instead of stretching edge-to-edge
    StackPane wrapper = new StackPane(loginView);
    wrapper.setPadding(new Insets(24));

    Scene scene = new Scene(wrapper, 440, 420);
    applyStylesheet(scene);
    primaryStage.setScene(scene);
  }

  private void showMainView(User user)
  {
    MainView mainView = new MainView(
      user,
      StoreSystem.instance().userManager,
      StoreSystem.instance().inventory,
      StoreSystem.instance().productManager,
      StoreSystem.instance().transactionManager,
      this::showLoginScreen
    );

    Scene scene = new Scene(mainView, 1050, 640);
    applyStylesheet(scene);
    primaryStage.setScene(scene);
  }

  private void applyStylesheet(Scene scene)
  {
    for (String cp : CSS_CLASSPATHS) {
      URL url = getClass().getResource(cp);
      if (url != null) {
        scene.getStylesheets().add(url.toExternalForm());
        return;
      }
    }

    for (String path : CSS_DISK_PATHS) {
      File file = new File(path);
      if (file.exists()) {
        scene.getStylesheets().add(file.toURI().toString());
        return;
      }
    }
  }

  public static void main(String[] args)
  {
    StoreSystem.instance().loadFromFile("src/main/java/data/hehe.ser");

    launch(args);

    try 
    {
      StoreSystem.instance().saveToFile("src/main/java/data/hehe.ser");
    } 
    catch (IOException err) 
    { 
      System.out.println("WHY");
    }
    System.out.println("BYE");
  }
}

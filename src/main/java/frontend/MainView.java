package frontend;

import backend.Inventory;
import backend.ProductManager;
import backend.UserManager;
import backend.TransactionManager;
import backend.User;
import backend.Manager;
import backend.Clerk;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainView extends BorderPane
{
  private static final String SIDEBAR_BG = "-fx-background-color: #cad1da;";
  private static final String NAV_STYLE = "-fx-background-color: transparent; -fx-font-size: 14px;";
  private static final String NAV_ACTIVE = "-fx-background-color: #6b7f96; -fx-text-fill: white; -fx-font-size: 14px;";

  private static final String LOGO_FILE = "/main/resources/LOGO.png";
  private static final String LOGO_DISK_PATH = "src/main/java/main/resources/LOGO.png";

  private final User user;
  private final UserManager users;
  private final Inventory inventory;
  private final ProductManager productManager;
  private final TransactionManager transactionManager;

  private final Runnable onLogout;
  private final boolean isManager;

  private final List<Button> navButtons = new ArrayList<>();

  public MainView(User user, UserManager users, Inventory inventory, ProductManager productManager, TransactionManager transactionManager, Runnable onLogout){
    this.user = user;
    this.users = users;
    this.inventory = inventory;
    this.productManager = productManager;
    this.transactionManager = transactionManager;

    this.onLogout = onLogout;
    this.isManager = user.isManager();

    setLeft(createSidebar());
    showOverview();   // dashboard is the landing page
  }

  // LOADS THE LOGO IMAGE FOR THE TOP OF THE SIDEBAR
  private Node createLogo()
  {
    Image img = null;

    // 1. Try classpath first
    try (InputStream in = getClass().getResourceAsStream(LOGO_FILE)) {
        if (in != null) {
            img = new Image(in);
        }
    } catch (IOException ignored) {}

    // 2. Fallback: load directly from project folder on disk
    if (img == null || img.isError()) {
        File file = new File(LOGO_DISK_PATH);
        if (file.exists()) {
            img = new Image(file.toURI().toString());
        }
    }

    // 3. Text fallback if neither worked
    if (img == null || img.isError()) {
        Label fallback = new Label("AgrawReturn()");
        fallback.setStyle("-fx-font-size: 20px;");
        return fallback;
    }

    ImageView logo = new ImageView(img);
    logo.setFitWidth(160);        // fits cleanly inside the 200px sidebar (20px padding each side)
    logo.setPreserveRatio(true);
    logo.setSmooth(true);

    HBox logoBox = new HBox(logo);
    logoBox.setAlignment(Pos.CENTER);
    return logoBox;
  }

  // SIDEBAR, ALLOWS FOR NAVIGATION TO DIFFERENT SCREENS
  private VBox createSidebar()
  {
    Node logo = createLogo();

    if (user instanceof Manager)
    {
      Button overview = navButton("OVERVIEW", this::showOverview);
      Button products = navButton("PRODUCTS", () -> setContent(new ProductManagementView(productManager, inventory)));
      Button inv = navButton("INVENTORY", () -> setContent(new InventoryManagementView(inventory, productManager)));
      Button transactions = navButton("TRANSACTIONS", () -> setContent(new TransactionView(() -> {}, transactionManager, inventory, user)));
      Button clerks = navButton("CLERKS", () -> setContent(new ClerksView(users)));
      Button extra = navButton("CHAT", () -> setContent(new PlaceholderView("Chat")));

      Button logout = new Button("LOG OUT");
      logout.setStyle(NAV_STYLE);
      logout.setOnAction(e -> onLogout.run());

      Region spacer = new Region();
      VBox.setVgrow(spacer, Priority.ALWAYS);

      VBox sidebar = new VBox(8, logo, overview, products, inv, transactions, clerks, extra, spacer, new Separator(), new Label("ACCOUNT"), logout);
      sidebar.setPadding(new Insets(20));
      sidebar.setPrefWidth(200);
      sidebar.setStyle(SIDEBAR_BG);
      sidebar.setAlignment(Pos.TOP_LEFT);
      return sidebar;
    }
    else if (user instanceof Clerk)
    {
      Button overview = navButton("OVERVIEW", this::showOverview);
      Button inv = navButton("INVENTORY", () -> setContent(new InventoryManagementView(inventory, productManager)));
      Button transactions = navButton("TRANSACTIONS", () -> setContent(new TransactionView(() -> {}, transactionManager, inventory, user)));

      Button logout = new Button("LOG OUT");
      logout.setStyle(NAV_STYLE);
      logout.setOnAction(e -> onLogout.run());

      Region spacer = new Region();
      VBox.setVgrow(spacer, Priority.ALWAYS);

      VBox sidebar = new VBox(8, logo, overview, inv, transactions, spacer, new Separator(), new Label("ACCOUNT"), logout);
      sidebar.setPadding(new Insets(20));
      sidebar.setPrefWidth(200);
      sidebar.setStyle(SIDEBAR_BG);
      sidebar.setAlignment(Pos.TOP_LEFT);
      return sidebar;
    }

    return null;
  }

  private Button navButton(String text, Runnable action){
    Button b = new Button(text);
    b.setMaxWidth(Double.MAX_VALUE);
    b.setAlignment(Pos.CENTER_LEFT);
    b.setStyle(NAV_STYLE);
    b.setOnAction(e -> {
        for (Button other : navButtons){
            other.setStyle(NAV_STYLE);
        }
        ((Button) e.getSource()).setStyle(NAV_ACTIVE);
        action.run();
    });
    navButtons.add(b);
    return b;
  }

  private void showOverview(){
    for (Button b : navButtons){
        b.setStyle(b.getText().equals("OVERVIEW") ? NAV_ACTIVE : NAV_STYLE);
    }
    setContent(new DashboardView(inventory, user, users, transactionManager));
  }

  private void setContent(Node view){
    setCenter(view);
  }
}

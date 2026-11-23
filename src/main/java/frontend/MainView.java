package frontend;

import backend.Inventory;
import backend.ProductManager;
import backend.UserManager;
import backend.TransactionManager;

import backend.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class MainView extends BorderPane
{
  private static final String SIDEBAR_BG = "-fx-background-color: #cad1da;";
  private static final String NAV_STYLE = "-fx-background-color: transparent; -fx-font-size: 14px;";
  private static final String NAV_ACTIVE = "-fx-background-color: #6b7f96; -fx-text-fill: white; -fx-font-size: 14px;"; // TO CHANGE INTO STH MORE AUTUMNAL DOE I LOWKEY TOOK THIS FROM CANVA ROUGH PROTOTYPE

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

  // SIDEBAR, ALLOWS FOR NAVIGATION TO DIFFERENT SCREENS
  private VBox createSidebar()
  {
    Label logo = new Label("LOGOHERE");
    logo.setStyle("-fx-font-size: 20px;");

    Button overview = navButton("OVERVIEW", this::showOverview);
    Button products = navButton("PRODUCTS", () -> setContent(new ProductManagementView(productManager, inventory)));
    Button inv = navButton("INVENTORY", () -> setContent(new InventoryManagementView(inventory, productManager)));
    Button transactions = navButton("TRANSACTIONS", () -> setContent(new TransactionView(() -> {}, transactionManager, inventory, user)));

    // depending on what user options
    Button extra;
    if (isManager){
        extra = navButton("CLERKS", () -> setContent(new ClerksView(users)));
    } else {
        extra = navButton("CHAT", () -> setContent(new PlaceholderView("Chat")));
    }
    

    Button settings = navButton("SETTINGS", () -> setContent(new Settings(user, users))); // PASS THESE Parametera
    Button logout = new Button("LOG OUT");
    logout.setStyle(NAV_STYLE);
    logout.setOnAction(e -> onLogout.run());

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    VBox sidebar = new VBox(8,logo, overview, products, inv, transactions, extra, new Separator(), new Label("ACCOUNT"), settings, logout,spacer);
    sidebar.setPadding(new Insets(20));
    sidebar.setPrefWidth(200);
    sidebar.setStyle(SIDEBAR_BG);
    sidebar.setAlignment(Pos.TOP_LEFT);
    return sidebar;
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
        b.setStyle(NAV_ACTIVE);
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

  // views are rebuilt on every navigation
  private void setContent(Node view){
    setCenter(view);
  }
}

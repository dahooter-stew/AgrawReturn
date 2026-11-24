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

  public MainView(User user, UserManager users, Inventory inventory, ProductManager productManager, TransactionManager transactionManager, Runnable onLogout)
  {
    this.user = user;
    this.users = users;
    this.inventory = inventory;
    this.productManager = productManager;
    this.transactionManager = transactionManager;

    this.onLogout = onLogout;
    this.isManager = user instanceof Manager;

    setLeft(createSidebar());
    showOverview();
  }

  private Node createLogo()
  {
    Image img = null;

    try (InputStream in = getClass().getResourceAsStream(LOGO_FILE)) {
      if (in != null) {
        img = new Image(in);
      }
    } catch (IOException ignored) {}

    if (img == null || img.isError()) {
      File file = new File(LOGO_DISK_PATH);
      if (file.exists()) {
        img = new Image(file.toURI().toString());
      }
    }

    if (img == null || img.isError()) {
      Label fallback = new Label("AgrawReturn()");
      fallback.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #F5F0E6;");
      return fallback;
    }

    ImageView logo = new ImageView(img);
    logo.setFitWidth(155);
    logo.setPreserveRatio(true);
    logo.setSmooth(true);

    HBox logoBox = new HBox(logo);
    logoBox.setAlignment(Pos.CENTER);
    logoBox.setPadding(new Insets(0, 0, 12, 0));
    return logoBox;
  }

  private VBox createSidebar()
  {
    Node logo = createLogo();

    Separator sep = new Separator();
    sep.getStyleClass().add("sidebar-divider");

    Label accountLabel = new Label("ACCOUNT");
    accountLabel.getStyleClass().add("sidebar-section-label");

    Button logout = new Button("LOG OUT");
    logout.setMaxWidth(Double.MAX_VALUE);
    logout.setAlignment(Pos.CENTER_LEFT);
    logout.getStyleClass().add("logout-btn");
    logout.setOnAction(e -> onLogout.run());

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    VBox sidebar = new VBox(8);
    sidebar.setPrefWidth(205);
    sidebar.getStyleClass().add("sidebar");
    sidebar.setAlignment(Pos.TOP_LEFT);

    if (user instanceof Manager)
    {
      Button overview = navButton("OVERVIEW", this::showOverview);
      Button products = navButton("PRODUCTS", () -> setContent(new ProductManagementView(productManager, inventory)));
      Button inv = navButton("INVENTORY", () -> setContent(new InventoryManagementView(inventory, productManager)));
      Button transactions = navButton("TRANSACTIONS", () -> setContent(new TransactionView(() -> {}, transactionManager, inventory, user)));
      Button clerks = navButton("CLERKS", () -> setContent(new ClerksView(users)));
      Button extra = navButton("CHAT", () -> setContent(new PlaceholderView("Chat")));

      sidebar.getChildren().addAll(logo, overview, products, inv, transactions, clerks, extra, spacer, sep, accountLabel, logout);
      return sidebar;
    }
    else if (user instanceof Clerk)
    {
      Button overview = navButton("OVERVIEW", this::showOverview);
      Button inv = navButton("INVENTORY", () -> setContent(new InventoryManagementView(inventory, productManager)));
      Button transactions = navButton("TRANSACTIONS", () -> setContent(new TransactionView(() -> {}, transactionManager, inventory, user)));

      sidebar.getChildren().addAll(logo, overview, inv, transactions, spacer, sep, accountLabel, logout);
      return sidebar;
    }

    return null;
  }

  private Button navButton(String text, Runnable action)
  {
    Button b = new Button(text);
    b.setMaxWidth(Double.MAX_VALUE);
    b.setAlignment(Pos.CENTER_LEFT);
    b.getStyleClass().add("nav-btn");
    b.setOnAction(e -> {
      setActiveNav(b);
      action.run();
    });
    navButtons.add(b);
    return b;
  }

  private void setActiveNav(Button active)
  {
    for (Button b : navButtons) {
      b.getStyleClass().removeAll("nav-btn", "nav-btn-active");
      b.getStyleClass().add(b == active ? "nav-btn-active" : "nav-btn");
    }
  }

  private void showOverview()
  {
    for (Button b : navButtons) {
      if (b.getText().equals("OVERVIEW")) {
        setActiveNav(b);
        break;
      }
    }
    setContent(new DashboardView(inventory, user, users, transactionManager));
  }

  private void setContent(Node view)
  {
    setCenter(view);
  }
}

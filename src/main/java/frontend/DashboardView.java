package frontend;

import java.util.ArrayList;
import java.util.List;

import backend.Clerk;
import backend.Inventory;
import backend.Purchase;
import backend.Product;
import backend.Sale;
import backend.StoreSystem;
import backend.Transaction;
import backend.TransactionManager;
import backend.User;
import backend.UserManager;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class DashboardView extends HBox{ 

  public DashboardView(Inventory inventory, User user, UserManager users, TransactionManager transactionManager)
  {
    setSpacing(15);
    setPadding(new Insets(15));

    // left column: stock card on top, transactions card below
    VBox main = new VBox(15,
      createStockCard(inventory),
      createTransactionsCard(user.isManager(), StoreSystem.instance().transactionManager));   // only managers see profit
    HBox.setHgrow(main, Priority.ALWAYS);          // main column takes the extra width
    getChildren().add(main);

    // adds a right column: clerk list, ONLY IF USER IS A MANAGER DOES THE CLERKS LIST SHOW
    if (user.isManager())
      getChildren().add(createClerksCard(users));
  }

  // TABLE VIEW OF INVENTORY for viewing only
  private VBox createStockCard(Inventory inventory){
    TableView<Product> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);  
    table.setPlaceholder(new Label("No products in inventory.")); // DEFAULT EMPTY
    VBox.setVgrow(table, Priority.ALWAYS);

    // each column pulls its value from the InventoryItem / its Product
    // ReadOnly wrappers = cells can't be edited
    TableColumn<Product, String> nameCol = new TableColumn<>("Product");
    nameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getName()));

    TableColumn<Product, String> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getId()));

    TableColumn<Product, String> priceCol = new TableColumn<>("Price");
    priceCol.setCellValueFactory(c -> new ReadOnlyStringWrapper("Php " + c.getValue().getPrice()));

    TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
    stockCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(inventory.getProductQuantity(c.getValue())));

    table.getColumns().addAll(nameCol, idCol, priceCol, stockCol);

    // fill the table 
    for (Product item : inventory.getItems())
      table.getItems().add(item);

    VBox box = card("Inventory");
    box.getChildren().addAll(new Label("Currently in stock!"), table);
    return box;
  }

  // NEWEST TRANSACTIONS 
  private VBox createTransactionsCard(boolean showProfit, TransactionManager transactionManager)
  {
    // walk the history backwards so the newest ones come first
    List<Transaction> history = transactionManager.getHistoryList();
    List<Transaction> recent = new ArrayList<>();
    for (int i = history.size() - 1; i >= 0 && recent.size() < 10; i--)
      recent.add(history.get(i));

    TableView<Transaction> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPlaceholder(new Label("No transactions yet."));
    VBox.setVgrow(table, Priority.ALWAYS); 

    TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(c -> {
      Transaction t = c.getValue();

      String type;
      if (t instanceof Sale)
        type = "Sale";
      else if (t instanceof Purchase)
        type = "Purchase";
      else
        type = "Other";

      return new ReadOnlyStringWrapper(type);
    });

    TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
    amountCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("Php %.2f", c.getValue().getPrice())));

    table.getColumns().addAll(typeCol, amountCol);
    table.getItems().addAll(recent);

    VBox box = card("Recent Transactions");
    if (showProfit){   // net = sales minus purchases, managers only
      box.getChildren().add(new Label(String.format("Net profit: Php %.2f", transactionManager.calculateProfit())));
    }
    box.getChildren().add(table);
    return box;
  }

  // CLERKS BOX: only managers can see this, usernames of all clerks
  private VBox createClerksCard(UserManager users)
  {
    ListView<String> clerks = new ListView<>();
    for (Clerk clerk : users.getClerks())
      clerks.getItems().add(clerk.getUsername());

    VBox box = card("Clerks");
    box.getChildren().add(clerks);
    box.setPrefWidth(220);
    return box;
  }

  // each card adds its own contents after calling this
  private VBox card(String title)
  {
    Label t = new Label(title);
    t.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    VBox box = new VBox(8, t);
    box.setPadding(new Insets(15));
    VBox.setVgrow(box, Priority.ALWAYS);   // cards share the column height
    return box;
  }
}

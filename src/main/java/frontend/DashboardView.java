package frontend;

import java.util.ArrayList;
import java.util.List;

import backend.Clerk;
import backend.Manager;
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
import javafx.scene.control.*;
import javafx.scene.layout.*;

// THE DASHBOARD VIEW IS WHAT U SEE WHEN U OPEN
// THIS LOWKEY LOOKS DIFFERENT DEPENDING IF YOU LOGGED IN AS MANAGER OR CLERK 

public class DashboardView extends VBox
{
  public DashboardView(Inventory inventory, User user, UserManager users, TransactionManager transactionManager){
    getStyleClass().add("page-container");

    VBox shell = new VBox(16);
    shell.getStyleClass().add("warm-shell");
    VBox.setVgrow(shell, Priority.ALWAYS);

    Label welcome = new Label("Welcome,\n" + user.getUsername() + "!");
    welcome.getStyleClass().add("welcome-title");

    HBox columns = new HBox(16);
    VBox.setVgrow(columns, Priority.ALWAYS);

    VBox main = new VBox(16,
      createStockCard(inventory),
      createTransactionsCard(user instanceof Manager, StoreSystem.instance().transactionManager)
    );
    HBox.setHgrow(main, Priority.ALWAYS);
    columns.getChildren().add(main);

    if (user instanceof Manager) {
      columns.getChildren().add(createClerksCard(users)); // MANAGERS HAVE A CLERK CARD IN THE DASHBOARD
    }
    shell.getChildren().addAll(welcome, columns);
    getChildren().add(shell);
  }

  // inventory is seen in the dashboard

  private VBox createStockCard(Inventory inventory) {
    TableView<Product> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPlaceholder(new Label("No products in inventory."));
    VBox.setVgrow(table, Priority.ALWAYS);

    TableColumn<Product, String> nameCol = new TableColumn<>("Product");
    nameCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getName()));

    TableColumn<Product, String> idCol = new TableColumn<>("ID");
    idCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getId()));

    TableColumn<Product, String> priceCol = new TableColumn<>("Price");
    priceCol.setCellValueFactory(c -> new ReadOnlyStringWrapper("Php " + c.getValue().getPrice()));

    TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
    stockCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(inventory.getProductQuantity(c.getValue())));

    table.getColumns().addAll(nameCol, idCol, priceCol, stockCol);

    for (Product item : inventory.getItems()) {
      table.getItems().add(item);
    }

    Label subtitle = new Label("Currently in stock!");
    subtitle.getStyleClass().add("muted-label");

    VBox box = card("Inventory");
    box.getChildren().addAll(subtitle, table);
    return box;
  }

  // recent transaxctions are seen in the dashboard

  private VBox createTransactionsCard(boolean showProfit, TransactionManager transactionManager) {
    List<Transaction> history = transactionManager.getHistoryList();
    List<Transaction> recent = new ArrayList<>();
    for (int i = history.size() - 1; i >= 0 && recent.size() < 10; i--) { // recent ones are shown...
      recent.add(history.get(i));
    }

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
    if (showProfit) {
      Label profit = new Label(String.format("Net profit: Php %.2f", transactionManager.calculateProfit())); // shows da profit as well
      profit.getStyleClass().add("row-tag");
      box.getChildren().add(profit);
    }
    box.getChildren().add(table);
    return box;
  }

  private VBox createClerksCard(UserManager users){ // the clerk card special for manager
    ListView<String> clerks = new ListView<>();
    VBox.setVgrow(clerks, Priority.ALWAYS);
    for (Clerk clerk : users.getClerks()) {
      clerks.getItems().add(clerk.getUsername());
    }
    VBox box = card("Clerks");
    box.getChildren().add(clerks);
    box.setPrefWidth(220);
    return box;
  }

  private VBox card(String title){
    Label t = new Label(title);
    t.getStyleClass().add("card-title");

    VBox box = new VBox(8, t);
    box.getStyleClass().add("sheet-card");
    VBox.setVgrow(box, Priority.ALWAYS);
    return box;
  }
}

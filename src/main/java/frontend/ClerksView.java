package frontend;

import backend.Clerk;
import backend.Sale;
import backend.StoreSystem;
import backend.Transaction;
import backend.TransactionManager;
import backend.UserManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ClerksView extends HBox
{
  private final UserManager users;
  private final TransactionManager transactionManager;

  private final ListView<String> clerks = new ListView<>();
  private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
  private final TableView<Transaction> transactionTable = new TableView<>(transactionData);
  private final Label transactionsTitle = new Label("Clerk Transactions");

  public ClerksView(UserManager users) 
  {
    this(users, StoreSystem.instance().transactionManager);
  }

  public ClerksView(UserManager users, TransactionManager transactionManager) 
  {
    this.users = users;
    this.transactionManager = transactionManager;

    setSpacing(18);
    getStyleClass().add("page-container");

    VBox leftColumn = createClerkListSection();
    VBox rightColumn = createTransactionsSection();
    HBox.setHgrow(rightColumn, Priority.ALWAYS);

    getChildren().addAll(leftColumn, rightColumn);

    clerks.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedUsername) -> {
      loadTransactionsForClerk(selectedUsername);
    });

    refresh();
  }

  private VBox createClerkListSection() 
  {
    VBox left = new VBox(10);
    left.setPrefWidth(320);
    left.setMinWidth(280);
    left.getStyleClass().add("sheet-card");

    Label title = new Label("Clerks");
    title.getStyleClass().add("page-title");

    VBox.setVgrow(clerks, Priority.ALWAYS);

    TextField nameField = new TextField();
    nameField.setPromptText("Username");
    HBox.setHgrow(nameField, Priority.ALWAYS);

    PasswordField passField = new PasswordField();
    passField.setPromptText("Password");
    HBox.setHgrow(passField, Priority.ALWAYS);

    Button add = new Button("Add");
    add.getStyleClass().add("btn-olive-pill");
    add.setOnAction(e -> {
      try {
        users.addUser(new Clerk(nameField.getText().trim(), passField.getText()));
        nameField.clear();
        passField.clear();
        refresh();
      } catch (IllegalArgumentException ex) {
        showAlert(ex.getMessage());
      }
    });

    Button remove = new Button("Remove Selected");
    remove.setMaxWidth(Double.MAX_VALUE);
    remove.getStyleClass().add("btn-danger-pill");
    remove.setOnAction(e -> {
      String selectedName = clerks.getSelectionModel().getSelectedItem();

      if (selectedName != null) {
        for (Clerk clerk : users.getClerks()) {
          if (clerk.getUsername().equals(selectedName)) {
            users.removeUser(clerk);
            break;
          }
        }
        refresh();
      }
    });

    left.getChildren().addAll(title, clerks, new HBox(8, nameField, passField, add), remove);
    return left;
  }

  private VBox createTransactionsSection() 
  {
    VBox right = new VBox(10);
    right.getStyleClass().add("sheet-card");

    transactionsTitle.getStyleClass().add("page-title");

    transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    transactionTable.setPlaceholder(new Label("Select a clerk to view their transactions."));
    VBox.setVgrow(transactionTable, Priority.ALWAYS);

    TableColumn<Transaction, String> productCol = new TableColumn<>("Product");
    productCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduct().getName()));

    TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(cell -> new SimpleStringProperty(
      cell.getValue() instanceof Sale ? "Sale" : "Purchase"
    ));

    TableColumn<Transaction, Integer> amountCol = new TableColumn<>("Amount");
    amountCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAmount()).asObject());

    TableColumn<Transaction, Double> priceCol = new TableColumn<>("Price (₱)");
    priceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());

    TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));

    transactionTable.getColumns().addAll(productCol, typeCol, amountCol, priceCol, dateCol);

    right.getChildren().addAll(transactionsTitle, transactionTable);
    return right;
  }

  private void loadTransactionsForClerk(String username) 
  {
    if (username == null) {
      transactionsTitle.setText("Clerk Transactions");
      transactionData.clear();
      transactionTable.setPlaceholder(new Label("Select a clerk to view their transactions."));
      return;
    }

    Clerk selectedClerk = null;
    for (Clerk clerk : users.getClerks()) {
      if (clerk.getUsername().equals(username)) {
        selectedClerk = clerk;
        break;
      }
    }

    if (selectedClerk == null || transactionManager == null) {
      transactionData.clear();
      return;
    }

    transactionsTitle.setText("Transactions — " + selectedClerk.getUsername());
    List<Transaction> clerkTransactions = transactionManager.filterByUser(selectedClerk);
    transactionData.setAll(clerkTransactions);
    transactionTable.setPlaceholder(new Label("No transactions recorded for " + selectedClerk.getUsername() + "."));
  }

  private void refresh() 
  {
    clerks.getItems().clear();
    for (Clerk clerk : users.getClerks()) {
      clerks.getItems().add(clerk.getUsername());
    }
  }

  private void showAlert(String msg) 
  {
    Alert a = new Alert(Alert.AlertType.ERROR);
    a.setHeaderText(null);
    if (getScene() != null) {
      a.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
    }
    a.setContentText(msg);
    a.showAndWait();
  }
}

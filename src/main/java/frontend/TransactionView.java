package frontend;

import backend.Inventory;
import backend.Product;
import backend.Purchase;
import backend.Sale;
import backend.Transaction;
import backend.TransactionManager;
import backend.User;
import backend.Clerk;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class TransactionView extends BorderPane 
{
  // Attributes
  private final TransactionManager transactionManager;
  private final Inventory inventory;
  private final User user;

  private final ObservableList<Transaction> masterData;
  private Label profitLabel;

  private ComboBox<String> typeBox;

  private VBox saleProductBox;
  private ComboBox<Product> productBox;

  private VBox purchaseProductBox;
  private TextField idField;
  private TextField nameField;
  private TextField descriptionField;
  private TextField priceField;

  private VBox sourceBox;
  private TextField sourceField;

  private TextField amountField;
  private TextField dateField;
  private Label totalValueLabel;

  // init
  public TransactionView(Runnable onLogout, TransactionManager transactionManager, Inventory inventory, User user) 
  {
    this.transactionManager = transactionManager;
    this.inventory = inventory;
    this.user = user;

    getStyleClass().add("page-container");

    masterData = FXCollections.observableArrayList(transactionManager.getHistoryList());

    setCenter(createLeftFinanceSection());
    setRight(createFormSection());

    handleCalculateProfit();
  }

  // the table and the balance on top
  private VBox createLeftFinanceSection() 
  {
    VBox container = new VBox(16);
    container.getStyleClass().add("warm-shell");

    Label titleLabel = new Label("Finances & Transaction");
    titleLabel.getStyleClass().add("page-title");

    // balance
    HBox balanceCard = new HBox(15);
    balanceCard.setAlignment(Pos.CENTER_LEFT);
    balanceCard.getStyleClass().add("balance-card");

    VBox balanceTextBox = new VBox(4);
    Label balanceTitle = new Label("Net Profit / Current Balance");
    balanceTitle.getStyleClass().add("muted-label");

    profitLabel = new Label("PHP 0.00");
    profitLabel.getStyleClass().add("balance-amount");
    balanceTextBox.getChildren().addAll(balanceTitle, profitLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button btnCalcProfit = new Button("Calculate Total Profit");
    btnCalcProfit.getStyleClass().add("btn-olive-pill");
    btnCalcProfit.setOnAction(e -> handleCalculateProfit());

    balanceCard.getChildren().addAll(balanceTextBox, spacer, btnCalcProfit);

    // the transaction history
    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    VBox.setVgrow(tabPane, Priority.ALWAYS);

    // filter by sale or purchase
    // if manager all transactions are visible
    // if clerk only own transactions are available
    FilteredList<Transaction> allFiltered;
    FilteredList<Transaction> salesFiltered;
    FilteredList<Transaction> purchasesFiltered;
    if (user instanceof Clerk)
    {
      allFiltered = new FilteredList<>(masterData, t -> t.getUser().equals(user));
      salesFiltered = new FilteredList<>(masterData, t -> t instanceof Sale && t.getUser() == user);
      purchasesFiltered = new FilteredList<>(masterData, t -> t instanceof Purchase && t.getUser() == user);
    }
    else
    {
      allFiltered = new FilteredList<>(masterData, t -> true);
      salesFiltered = new FilteredList<>(masterData, t -> t instanceof Sale);
      purchasesFiltered = new FilteredList<>(masterData, t -> t instanceof Purchase);
    }

    TableView<Transaction> allTable = buildTransactionTable(allFiltered, true, true);
    TableView<Transaction> salesTable = buildTransactionTable(salesFiltered, false, false);
    TableView<Transaction> purchasesTable = buildTransactionTable(purchasesFiltered, false, true);

    Tab allTab = new Tab("All Transactions", allTable);
    Tab salesTab = new Tab("Sales", salesTable);
    Tab purchasesTab = new Tab("Purchases", purchasesTable);

    tabPane.getTabs().addAll(allTab, salesTab, purchasesTab);

    // remove selected transaction button
    Button btnRemoveTxn = new Button("Remove Selected Transaction");
    btnRemoveTxn.setMaxWidth(Double.MAX_VALUE);
    btnRemoveTxn.getStyleClass().add("btn-danger-pill");
    btnRemoveTxn.disableProperty().bind(Bindings.createBooleanBinding(() -> {
      Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
      if (selectedTab != null && selectedTab.getContent() instanceof TableView<?> table)
      {
        return table.getSelectionModel().getSelectedItem() == null;
      }
      return true;
    },
      tabPane.getSelectionModel().selectedItemProperty(),
      allTable.getSelectionModel().selectedItemProperty(),
      salesTable.getSelectionModel().selectedItemProperty(),
      purchasesTable.getSelectionModel().selectedItemProperty()
    ));

    btnRemoveTxn.setOnAction(e -> {
      Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
      if (selectedTab != null && selectedTab.getContent() instanceof TableView<?> table)
      {
        Object selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem instanceof Transaction selectedTxn)
        {
          transactionManager.removeTransaction(selectedTxn);
          refreshTables();
          refreshProductDropdown();
          handleCalculateProfit();
        }
      }
    });

    container.getChildren().addAll(titleLabel, balanceCard, tabPane, btnRemoveTxn);
    return container;
  }

  // the forms for recording new transactions
  private TableView<Transaction> buildTransactionTable(FilteredList<Transaction> data, boolean showTypeCol, boolean showSourceCol) 
  {
    TableView<Transaction> table = new TableView<>(data);
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<Transaction, String> productCol = new TableColumn<>("Product");
    productCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduct().getName()));
    table.getColumns().add(productCol);

    if (showTypeCol) 
    {
      TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
      typeCol.setCellValueFactory(cell -> new SimpleStringProperty(
        cell.getValue() instanceof Sale ? "Sale" : "Purchase"
      ));
      table.getColumns().add(typeCol);
    }

    if (showSourceCol) 
    {
      TableColumn<Transaction, String> sourceCol = new TableColumn<>("Source");
      sourceCol.setCellValueFactory(cell -> {
        Transaction txn = cell.getValue();
        if (txn instanceof Purchase purchase) 
        {
          String src = purchase.getSource();
          return new SimpleStringProperty(src != null && !src.isBlank() ? src : "-");
        }
        return new SimpleStringProperty("-");
      });
      table.getColumns().add(sourceCol);
    }

    TableColumn<Transaction, Integer> amountCol = new TableColumn<>("Amount");
    amountCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAmount()).asObject());

    TableColumn<Transaction, Double> priceCol = new TableColumn<>("Price (₱)");
    priceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrice()).asObject());

    TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate()));

    TableColumn<Transaction, String> userCol = new TableColumn<>("User");
    userCol.setCellValueFactory(cell -> {
      User txnUser = cell.getValue().getUser();
      return new SimpleStringProperty(txnUser != null ? txnUser.getUsername() : "-");
    });

    table.getColumns().addAll(amountCol, priceCol, dateCol, userCol);
    return table;
  }

  // for recording transactions
  private VBox createFormSection() 
  {
    VBox form = new VBox(9);
    form.setPadding(new Insets(4, 0, 4, 14));
    form.setPrefWidth(235);
    form.setMinWidth(220);
    form.setMaxWidth(240);

    Label formTitle = new Label("Record Transaction");
    formTitle.getStyleClass().add("card-title");

    typeBox = new ComboBox<>();
    typeBox.getItems().addAll("Sale", "Purchase");
    typeBox.setValue("Sale");
    typeBox.setMaxWidth(Double.MAX_VALUE);
    VBox typeGroup = createLabeledField("Transaction Type", typeBox);

    productBox = new ComboBox<>();
    productBox.setMaxWidth(Double.MAX_VALUE);
    productBox.setPromptText("Select product...");
    productBox.setConverter(new StringConverter<>() 
    {
      @Override
      public String toString(Product product) 
      {
        if (product == null) return "";
        Integer stock = inventory.getProductQuantity(product);
        int qty = (stock != null) ? stock : 0;
        return String.format("%s (%d) - ₱%.2f", product.getName(), qty, product.getPrice());
      }

      @Override
      public Product fromString(String string) 
      {
        return null;
      }
    });
    refreshProductDropdown();
    saleProductBox = createLabeledField("Product", productBox);

    Label productCardHeader = new Label("Product Details");
    productCardHeader.getStyleClass().add("field-label");

    idField = new TextField();
    idField.setPromptText("2026-101");

    nameField = new TextField();
    nameField.setPromptText("Rice");

    HBox idAndNameRow = new HBox(
      6,
      createLabeledField("ID", idField),
      createLabeledField("Name", nameField)
    );
    HBox.setHgrow(idAndNameRow.getChildren().get(0), Priority.ALWAYS);
    HBox.setHgrow(idAndNameRow.getChildren().get(1), Priority.ALWAYS);

    descriptionField = new TextField();
    descriptionField.setPromptText("e.g., 5kg sack");
    VBox descGroup = createLabeledField("Description", descriptionField);

    priceField = new TextField();
    priceField.setPromptText("e.g., 250.00");
    VBox priceGroup = createLabeledField("Price (₱)", priceField);

    purchaseProductBox = new VBox(6, productCardHeader, idAndNameRow, descGroup, priceGroup);
    purchaseProductBox.getStyleClass().add("sub-card");
    purchaseProductBox.setVisible(false);
    purchaseProductBox.setManaged(false);

    sourceField = new TextField();
    sourceField.setPromptText("e.g., Fresh Farms");
    sourceBox = createLabeledField("Source", sourceField);
    sourceBox.setVisible(false);
    sourceBox.setManaged(false);

    amountField = new TextField();
    amountField.setPromptText("5");

    dateField = new TextField();
    dateField.setPromptText(LocalDate.now().toString());

    HBox amountAndDateRow = new HBox(
      6,
      createLabeledField("Amount", amountField),
      createLabeledField("Date", dateField)
    );
    HBox.setHgrow(amountAndDateRow.getChildren().get(0), Priority.ALWAYS);
    HBox.setHgrow(amountAndDateRow.getChildren().get(1), Priority.ALWAYS);

    Label totalTitleLabel = new Label("Total:");
    totalTitleLabel.getStyleClass().add("field-label");

    totalValueLabel = new Label("₱0.00");
    totalValueLabel.getStyleClass().add("row-title");

    Region totalSpacer = new Region();
    HBox.setHgrow(totalSpacer, Priority.ALWAYS);

    HBox totalSummaryBox = new HBox(totalTitleLabel, totalSpacer, totalValueLabel);
    totalSummaryBox.setAlignment(Pos.CENTER_LEFT);
    totalSummaryBox.getStyleClass().add("total-bar");

    // Just sets if it's Sale or Purchase transaction type
    typeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      boolean isPurchase = "Purchase".equalsIgnoreCase(newVal);

      saleProductBox.setVisible(!isPurchase);
      saleProductBox.setManaged(!isPurchase);

      purchaseProductBox.setVisible(isPurchase);
      purchaseProductBox.setManaged(isPurchase);

      sourceBox.setVisible(isPurchase);
      sourceBox.setManaged(isPurchase);

      updateTotalPreview();
    });

    productBox.valueProperty().addListener((obs, o, n) -> updateTotalPreview());
    priceField.textProperty().addListener((obs, o, n) -> updateTotalPreview());
    amountField.textProperty().addListener((obs, o, n) -> updateTotalPreview());

    Button btnProcess = new Button("Process Transaction");
    btnProcess.setMaxWidth(Double.MAX_VALUE);
    btnProcess.getStyleClass().add("btn-forest");
    btnProcess.setOnAction(e -> handleProcessTransaction());

    form.getChildren().addAll(
      formTitle,
      typeGroup,
      saleProductBox,
      purchaseProductBox,
      sourceBox,
      amountAndDateRow,
      totalSummaryBox,
      btnProcess
    );

    return form;
  }

  // utility, label and field sidebyside
  private VBox createLabeledField(String labelText, Control inputControl) 
  {
    Label label = new Label(labelText);
    label.getStyleClass().add("field-label");
    return new VBox(3, label, inputControl);
  }

  // previews calculated price
  private void updateTotalPreview() 
  {
    try 
    {
      int qty = Integer.parseInt(amountField.getText().trim());
      if (qty <= 0) 
      {
        totalValueLabel.setText("₱0.00");
        return;
      }

      double price;
      if ("Purchase".equalsIgnoreCase(typeBox.getValue())) 
      {
        price = Double.parseDouble(priceField.getText().trim());
      } 
      else 
      {
        Product selected = productBox.getValue();
        if (selected == null) 
        {
          totalValueLabel.setText("₱0.00");
          return;
        }
        price = selected.getPrice();
      }

      double total = qty * Math.max(0, price);
      totalValueLabel.setText(String.format("₱%,.2f", total));
    } 
    catch (NumberFormatException e) 
    {
      totalValueLabel.setText("₱0.00");
    }
  }

  // wrapper for TransactionManager
  private void handleProcessTransaction() 
  {
    try 
    {
      String type = typeBox.getValue();
      String date = dateField.getText().trim();

      if (date.isEmpty()) 
      {
        showAlert(Alert.AlertType.ERROR, "Validation Error", "Date field cannot be empty.");
        return;
      }

      int amount = Integer.parseInt(amountField.getText().trim());
      if (amount <= 0) 
      {
        showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be greater than 0.");
        return;
      }

      Transaction txn;
      if ("Sale".equalsIgnoreCase(type)) 
      {
        Product selectedProduct = productBox.getValue();
        if (selectedProduct == null) 
        {
          showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a product from inventory.");
          return;
        }

        Integer available = inventory.getProductQuantity(selectedProduct);
        if (available == null || amount > available) 
        {
          showAlert(Alert.AlertType.ERROR, "Insufficient Stock", "Only " + (available != null ? available : 0) + " units available in inventory.");
          return;
        }

        txn = new Sale(selectedProduct, amount, date, user);
      } 
      else 
      {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String source = sourceField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || source.isEmpty()) 
        {
          showAlert(Alert.AlertType.ERROR, "Validation Error", "ID, Name, and Source cannot be empty.");
          return;
        }

        double price = Double.parseDouble(priceField.getText().trim());
        if (price < 0) 
        {
          showAlert(Alert.AlertType.ERROR, "Validation Error", "Price cannot be negative.");
          return;
        }

        Product customProduct = new Product(id, name, description, price);
        txn = new Purchase(customProduct, amount, date, user, source);
      }

      transactionManager.processTransaction(txn);
      refreshTables();
      refreshProductDropdown();
      clearForm();
      handleCalculateProfit();
      showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction recorded successfully!");
    } 
    catch (NumberFormatException ex) 
    {
      showAlert(Alert.AlertType.ERROR, "Input Error", "Amount and Price must be valid numbers.");
    }
  }

  private void handleCalculateProfit() 
  {
    double profit = transactionManager.calculateProfit();
    profitLabel.setText(String.format("PHP %,.2f", profit));
  }

  // clear 
  private void refreshTables() 
  {
    masterData.setAll(transactionManager.getHistoryList());
  }

  private void refreshProductDropdown() 
  {
    Product current = productBox.getValue();
    List<Product> items = inventory.getItems();

    productBox.setItems(FXCollections.observableArrayList(items));
    if (current != null && items.contains(current)) 
    {
      productBox.setValue(current);
    }
  }

  private void clearForm() 
  {
    productBox.setValue(null);
    idField.clear();
    nameField.clear();
    descriptionField.clear();
    priceField.clear();
    sourceField.clear();
    amountField.clear();
    dateField.clear();
    totalValueLabel.setText("₱0.00");
  }

  // For popups when something is awry
  private void showAlert(Alert.AlertType type, String title, String content) 
  {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    if (getScene() != null) {
      alert.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
    }
    alert.setContentText(content);
    alert.showAndWait();
  }
}

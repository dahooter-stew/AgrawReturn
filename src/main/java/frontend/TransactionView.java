package frontend;

import backend.Inventory;
import backend.Product;
import backend.Purchase;
import backend.Sale;
import backend.Transaction;
import backend.TransactionManager;
import backend.User;

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
  private final TransactionManager transactionManager;
  private final Inventory inventory;
  private final User user;

  private final ObservableList<Transaction> masterData;
  private Label profitLabel;

  private ComboBox<String> typeBox;

  // Sale-specific product dropdown (from Inventory)
  private VBox saleProductContainer;
  private ComboBox<Product> productBox;

  // Purchase-specific custom product & source fields
  private VBox purchaseFieldsContainer;
  private TextField prodIdField;
  private TextField prodNameField;
  private TextField prodDescField;
  private TextField unitPriceField;
  private TextField sourceField;

  // Shared transaction fields
  private TextField amountField;
  private Label calculatedTotalLabel;
  private TextField dateField;

  public TransactionView(Runnable onLogout, TransactionManager transactionManager, Inventory inventory, User user) 
  {
    this.transactionManager = transactionManager;
    this.inventory = inventory;
    this.user = user;

    setPadding(new Insets(18));
    setStyle("-fx-background-color: #f8fafc;");

    masterData = FXCollections.observableArrayList(transactionManager.getHistoryList());

    setCenter(createLeftFinanceSection());
    setRight(createFormSection());

    handleCalculateProfit();
  }

  private VBox createLeftFinanceSection() 
  {
    VBox container = new VBox(16);
    container.setPadding(new Insets(22));
    container.setStyle(
      "-fx-background-color: #cbd5dc;" +
      "-fx-background-radius: 20;"
    );

    Label titleLabel = new Label("Finances & Transaction");
    titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

    // White rounded Balance / Net Profit card (matching 1st mockup)
    HBox balanceCard = new HBox(15);
    balanceCard.setAlignment(Pos.CENTER_LEFT);
    balanceCard.setPadding(new Insets(18, 22, 18, 22));
    balanceCard.setStyle(
      "-fx-background-color: white;" +
      "-fx-background-radius: 16;" +
      "-fx-border-color: #64748b;" +
      "-fx-border-radius: 16;" +
      "-fx-border-width: 1;"
    );

    VBox balanceTextBox = new VBox(4);
    Label balanceTitle = new Label("Net Profit / Current Balance");
    balanceTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

    profitLabel = new Label("PHP 0.00");
    profitLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
    balanceTextBox.getChildren().addAll(balanceTitle, profitLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button btnCalcProfit = new Button("Calculate Total Profit");
    btnCalcProfit.setStyle(
      "-fx-background-color: #6c7c8c;" +
      "-fx-text-fill: white;" +
      "-fx-font-weight: bold;" +
      "-fx-background-radius: 20;" +
      "-fx-padding: 8 18;" +
      "-fx-cursor: hand;"
    );
    btnCalcProfit.setOnAction(e -> handleCalculateProfit());

    balanceCard.getChildren().addAll(balanceTextBox, spacer, btnCalcProfit);

    // Tabs for All, Sale, and Purchase transactions
    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    VBox.setVgrow(tabPane, Priority.ALWAYS);

    FilteredList<Transaction> allFiltered = new FilteredList<>(masterData, t -> true);
    FilteredList<Transaction> salesFiltered = new FilteredList<>(masterData, t -> t instanceof Sale);
    FilteredList<Transaction> purchasesFiltered = new FilteredList<>(masterData, t -> t instanceof Purchase);

    Tab allTab = new Tab("All Transactions", buildTransactionTable(allFiltered, true, true));
    Tab salesTab = new Tab("Sales", buildTransactionTable(salesFiltered, false, false));
    Tab purchasesTab = new Tab("Purchases", buildTransactionTable(purchasesFiltered, false, true));

    tabPane.getTabs().addAll(allTab, salesTab, purchasesTab);

    container.getChildren().addAll(titleLabel, balanceCard, tabPane);
    return container;
  }

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

  private ScrollPane createFormSection() 
  {
    VBox form = new VBox(8);
    form.setPadding(new Insets(6, 6, 10, 18));

    Label formTitle = new Label("Record New Transaction");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e293b;");

    // Transaction Type Dropdown
    typeBox = new ComboBox<>();
    typeBox.getItems().addAll("Sale", "Purchase");
    typeBox.setValue("Sale");
    typeBox.setMaxWidth(Double.MAX_VALUE);

    // 1. Sale Mode: Product Dropdown populated from Inventory
    productBox = new ComboBox<>();
    productBox.setMaxWidth(Double.MAX_VALUE);
    productBox.setPromptText("Select a product...");
    productBox.setConverter(new StringConverter<>() 
    {
      @Override
      public String toString(Product product) 
      {
        if (product == null) return "";
        Integer stock = inventory.getProductQuantity(product);
        int qty = (stock != null) ? stock : 0;
        return String.format("%s (Stock: %d) - ₱%.2f", product.getName(), qty, product.getPrice());
      }

      @Override
      public Product fromString(String string) 
      {
        return null;
      }
    });
    refreshProductDropdown();

    saleProductContainer = new VBox(5, new Label("Product:"), productBox);

    // 2. Purchase Mode: Custom Product Text Fields + Source Field
    prodIdField = new TextField();
    prodIdField.setPromptText("e.g., 2026-10105");

    prodNameField = new TextField();
    prodNameField.setPromptText("e.g., Brown Rice");

    prodDescField = new TextField();
    prodDescField.setPromptText("e.g., 5kg sack");

    unitPriceField = new TextField();
    unitPriceField.setPromptText("e.g., 250.00");

    sourceField = new TextField();
    sourceField.setPromptText("e.g., Fresh Farms Supplier");

    purchaseFieldsContainer = new VBox(
      6,
      new Label("Product ID:"), prodIdField,
      new Label("Product Name:"), prodNameField,
      new Label("Description:"), prodDescField,
      new Label("Unit Price (₱):"), unitPriceField,
      new Label("Source:"), sourceField
    );
    purchaseFieldsContainer.setVisible(false);
    purchaseFieldsContainer.setManaged(false);

    // Toggle between Sale dropdown and Purchase custom fields
    typeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      boolean isPurchase = "Purchase".equalsIgnoreCase(newVal);

      saleProductContainer.setVisible(!isPurchase);
      saleProductContainer.setManaged(!isPurchase);

      purchaseFieldsContainer.setVisible(isPurchase);
      purchaseFieldsContainer.setManaged(isPurchase);

      updateEstimatedTotal();
    });

    // Amount Field
    amountField = new TextField();
    amountField.setPromptText("e.g., 5");

    // Auto-calculated Total Price preview
    calculatedTotalLabel = new Label("Estimated Total: ₱0.00");
    calculatedTotalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");

    productBox.valueProperty().addListener((obs, o, n) -> updateEstimatedTotal());
    unitPriceField.textProperty().addListener((obs, o, n) -> updateEstimatedTotal());
    amountField.textProperty().addListener((obs, o, n) -> updateEstimatedTotal());

    // Date Field
    dateField = new TextField();
    dateField.setPromptText("e.g., " + LocalDate.now());

    Button btnProcess = new Button("Process Transaction");
    btnProcess.setMaxWidth(Double.MAX_VALUE);
    btnProcess.setStyle(
      "-fx-background-color: #27ae60;" +
      "-fx-text-fill: white;" +
      "-fx-font-weight: bold;" +
      "-fx-padding: 9 14;" +
      "-fx-cursor: hand;"
    );
    btnProcess.setOnAction(e -> handleProcessTransaction());

    form.getChildren().addAll(
      formTitle,
      new Label("Transaction Type:"), typeBox,
      saleProductContainer,
      purchaseFieldsContainer,
      new Label("Amount / Quantity:"), amountField,
      calculatedTotalLabel,
      new Label("Date:"), dateField,
      new Separator(),
      btnProcess
    );

    ScrollPane formScroll = new ScrollPane(form);
    formScroll.setFitToWidth(true);
    formScroll.setPrefWidth(295);
    formScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
    return formScroll;
  }

  private void updateEstimatedTotal() 
  {
    try 
    {
      int qty = Integer.parseInt(amountField.getText().trim());
      if (qty <= 0) 
      {
        calculatedTotalLabel.setText("Estimated Total: ₱0.00");
        return;
      }

      double unitPrice;
      if ("Purchase".equalsIgnoreCase(typeBox.getValue())) 
      {
        unitPrice = Double.parseDouble(unitPriceField.getText().trim());
      } 
      else 
      {
        Product selected = productBox.getValue();
        if (selected == null) 
        {
          calculatedTotalLabel.setText("Estimated Total: ₱0.00");
          return;
        }
        unitPrice = selected.getPrice();
      }

      double total = qty * Math.max(0, unitPrice);
      calculatedTotalLabel.setText(String.format("Estimated Total: ₱%,.2f", total));
    } 
    catch (NumberFormatException e) 
    {
      calculatedTotalLabel.setText("Estimated Total: ₱0.00");
    }
  }

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
        String prodId = prodIdField.getText().trim();
        String prodName = prodNameField.getText().trim();
        String prodDesc = prodDescField.getText().trim();
        String source = sourceField.getText().trim();

        if (prodId.isEmpty() || prodName.isEmpty() || source.isEmpty()) 
        {
          showAlert(Alert.AlertType.ERROR, "Validation Error", "Product ID, Product Name, and Source cannot be empty.");
          return;
        }

        double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
        if (unitPrice < 0) 
        {
          showAlert(Alert.AlertType.ERROR, "Validation Error", "Unit price cannot be negative.");
          return;
        }

        Product customProduct = new Product(prodId, prodName, prodDesc, unitPrice);
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
      showAlert(Alert.AlertType.ERROR, "Input Error", "Amount and Unit Price must be valid numbers.");
    }
  }

  private void handleCalculateProfit() 
  {
    double profit = transactionManager.calculateProfit();
    profitLabel.setText(String.format("PHP %,.2f", profit));
  }

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
    prodIdField.clear();
    prodNameField.clear();
    prodDescField.clear();
    unitPriceField.clear();
    sourceField.clear();
    amountField.clear();
    dateField.clear();
    calculatedTotalLabel.setText("Estimated Total: ₱0.00");
  }

  private void showAlert(Alert.AlertType type, String title, String content) 
  {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}

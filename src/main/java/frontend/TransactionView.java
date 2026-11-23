package frontend;

import backend.Inventory;
import backend.Product;
import backend.Purchase;
import backend.Sale;
import backend.Transaction;
import backend.TransactionManager;
import backend.User;
import backend.Clerk;
import backend.Manager;

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

  // Sale product selector (from Inventory)
  private VBox saleProductBox;
  private ComboBox<Product> productBox;

  // Purchase product fields (matches Product(id, name, description, price))
  private VBox purchaseProductBox;
  private TextField idField;
  private TextField nameField;
  private TextField descriptionField;
  private TextField priceField;

  // Purchase-only transaction field
  private VBox sourceBox;
  private TextField sourceField;

  // Shared transaction fields
  private TextField amountField;
  private TextField dateField;
  private Label totalValueLabel;

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

    // Top Balance / Net Profit card
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

    // Filtered Tabs for All, Sales, and Purchases
    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    VBox.setVgrow(tabPane, Priority.ALWAYS);

    FilteredList<Transaction> allFiltered;
    if (user instanceof Clerk)
      allFiltered = new FilteredList<>(masterData, t -> t.getUser().equals(user));
    else
      allFiltered = new FilteredList<>(masterData, t -> true);
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

  private VBox createFormSection() 
  {
    VBox form = new VBox(12);
    form.setPadding(new Insets(6, 0, 6, 18));
    form.setPrefWidth(310);

    Label formTitle = new Label("Record New Transaction");
    formTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e293b;");

    // 1. Transaction Type
    typeBox = new ComboBox<>();
    typeBox.getItems().addAll("Sale", "Purchase");
    typeBox.setValue("Sale");
    typeBox.setMaxWidth(Double.MAX_VALUE);
    VBox typeGroup = createLabeledField("Transaction Type", typeBox);

    // 2A. Sale Product Dropdown (from Inventory)
    productBox = new ComboBox<>();
    productBox.setMaxWidth(Double.MAX_VALUE);
    productBox.setPromptText("Select product from inventory...");
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
    saleProductBox = createLabeledField("Product", productBox);

    // 2B. Purchase Product Card: strictly ordered (id, name, description, price)
    Label productCardHeader = new Label("Product Details");
    productCardHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #334155;");

    idField = new TextField();
    idField.setPromptText("e.g., 2026-10105");

    nameField = new TextField();
    nameField.setPromptText("e.g., Brown Rice");

    HBox idAndNameRow = new HBox(
      8,
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

    purchaseProductBox = new VBox(8, productCardHeader, idAndNameRow, descGroup, priceGroup);
    purchaseProductBox.setPadding(new Insets(10));
    purchaseProductBox.setStyle(
      "-fx-background-color: #eef2f6;" +
      "-fx-background-radius: 10;" +
      "-fx-border-color: #cbd5e1;" +
      "-fx-border-radius: 10;"
    );
    purchaseProductBox.setVisible(false);
    purchaseProductBox.setManaged(false);

    // 3. Purchase-only Source Field
    sourceField = new TextField();
    sourceField.setPromptText("e.g., Fresh Farms Supplier");
    sourceBox = createLabeledField("Source", sourceField);
    sourceBox.setVisible(false);
    sourceBox.setManaged(false);

    // 4. Shared Amount & Date Row
    amountField = new TextField();
    amountField.setPromptText("e.g., 5");

    dateField = new TextField();
    dateField.setPromptText(LocalDate.now().toString());

    HBox amountAndDateRow = new HBox(
      8,
      createLabeledField("Amount", amountField),
      createLabeledField("Date", dateField)
    );
    HBox.setHgrow(amountAndDateRow.getChildren().get(0), Priority.ALWAYS);
    HBox.setHgrow(amountAndDateRow.getChildren().get(1), Priority.ALWAYS);

    // 5. Total Price Summary Row
    Label totalTitleLabel = new Label("Total Price:");
    totalTitleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #475569;");

    totalValueLabel = new Label("₱0.00");
    totalValueLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

    Region totalSpacer = new Region();
    HBox.setHgrow(totalSpacer, Priority.ALWAYS);

    HBox totalSummaryBox = new HBox(totalTitleLabel, totalSpacer, totalValueLabel);
    totalSummaryBox.setAlignment(Pos.CENTER_LEFT);
    totalSummaryBox.setPadding(new Insets(8, 12, 8, 12));
    totalSummaryBox.setStyle(
      "-fx-background-color: #e2e8f0;" +
      "-fx-background-radius: 8;"
    );

    // Toggle Sale vs Purchase inputs
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
    btnProcess.setStyle(
      "-fx-background-color: #27ae60;" +
      "-fx-text-fill: white;" +
      "-fx-font-weight: bold;" +
      "-fx-font-size: 13px;" +
      "-fx-background-radius: 8;" +
      "-fx-padding: 10 14;" +
      "-fx-cursor: hand;"
    );
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

  private VBox createLabeledField(String labelText, Control inputControl) 
  {
    Label label = new Label(labelText);
    label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #334155;");
    VBox group = new VBox(4, label, inputControl);
    return group;
  }

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

  private void showAlert(Alert.AlertType type, String title, String content) 
  {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}

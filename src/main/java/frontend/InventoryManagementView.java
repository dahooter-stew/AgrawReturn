package frontend;

import backend.Inventory;
import backend.Product;
import backend.ProductManager;

import javafx.application.Platform; // FIXEDHERE
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagementView extends BorderPane 
{
  private Inventory inventory;

  // static so that availability status does not reset when after moving to diff sidebars and back
  private static final Map<String, String> availability = new HashMap<>();

  private ProductManager productManager;
  private Product foundProduct;

  private VBox rowsBox;
  private List<Product> checkedItems = new ArrayList<>();

  private TextField searchField;
  private Button addOrDeleteBtn;

  public InventoryManagementView(Inventory inventory, ProductManager productManager){
    this.inventory = inventory;
    this.productManager = productManager;
    getStyleClass().add("page-container");
    setCenter(createMainSection());
    refreshTable();
  }

  private VBox createMainSection() {
    searchField = new TextField();
    searchField.setPromptText("Search by name or id...");
    searchField.getStyleClass().add("search-pill");
    searchField.textProperty().addListener((obs, oldText, newText) -> refreshTable());

    Label title = new Label("Inventory");
    title.getStyleClass().add("page-title");

    addOrDeleteBtn = new Button("ADD +");
    addOrDeleteBtn.getStyleClass().add("btn-olive-pill");
    addOrDeleteBtn.setOnAction(e -> handleHeaderButton());

    BorderPane header = new BorderPane();
    header.setPadding(new Insets(0, 0, 14, 0));
    header.setLeft(searchField);
    header.setCenter(title);
    header.setRight(addOrDeleteBtn);
    BorderPane.setAlignment(searchField, Pos.CENTER_LEFT);
    BorderPane.setAlignment(addOrDeleteBtn, Pos.CENTER_RIGHT);

    rowsBox = new VBox(0);

    ScrollPane scroll = new ScrollPane(rowsBox);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("clean-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);

    VBox sheet = new VBox(8, header, scroll);
    sheet.getStyleClass().add("sheet-card");
    VBox.setVgrow(sheet, Priority.ALWAYS);
    return sheet;
  }

  private int getQty(Product item){
    Integer q = inventory.getProductQuantity(item);
    return q == null ? 0 : q;
  }

  private HBox createRow(Product item) {
    CheckBox check = new CheckBox();
    check.setOnAction(e -> {
      if (check.isSelected()){
        checkedItems.add(item);
      } else {
        checkedItems.remove(item);
      }
      updateHeaderButton();
    });

    Label name = new Label(item.getName());
    name.getStyleClass().add("row-title");

    Label info = new Label(String.format("Php %.2f  |  Stock: %d", item.getPrice(), getQty(item)));
    info.getStyleClass().add("row-subtitle");

    VBox textCol = new VBox(2, name, info);
    textCol.setPrefWidth(210);

    Label desc = new Label(item.getDescription());
    desc.getStyleClass().add("row-tag");

    Button editBtn = new Button("Edit");
    editBtn.getStyleClass().add("btn-olive-pill");
    editBtn.setOnAction(e -> showEditDialog(item));

    Button minusStock = new Button("-");
    minusStock.getStyleClass().add("stepper-btn");
    minusStock.setOnAction(e -> handleChangeStock(item, -1));

    // QTY CAN BE TYPES AS A TEXT FIELD
    TextField qty = new TextField(String.valueOf(getQty(item)));
    qty.getStyleClass().add("stepper-qty");
    qty.setPrefWidth(60);
    qty.setOnAction(e -> handleSetStockFromField(item, qty));   // runs when you press Enter

    Button addStock = new Button("+");
    addStock.getStyleClass().add("stepper-btn");
    addStock.setOnAction(e -> handleChangeStock(item, 1));

    HBox stepper = new HBox(4, minusStock, qty, addStock);
    stepper.setAlignment(Pos.CENTER);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(14, check, textCol, desc, spacer, editBtn, stepper);
    row.getStyleClass().add("row-item");
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private void handleHeaderButton() { // IF NO ITEMS SELECTED, THE HEADER BUTTON CAN ADD ELSE THEN ITEMS ARE SELECTED THE BUTTON IN THE HEADER DOES DELETE
    if (checkedItems.isEmpty()){
      showAddDialog();
    }
    else{
      handleDeleteProduct();
    }
  }

  private void updateHeaderButton() {
    addOrDeleteBtn.getStyleClass().removeAll("btn-olive-pill", "btn-danger-pill");
    if (checkedItems.isEmpty()) {
      addOrDeleteBtn.setText("ADD +");
      addOrDeleteBtn.getStyleClass().add("btn-olive-pill");
    } else {
      addOrDeleteBtn.setText("Delete (" + checkedItems.size() + ")");
      addOrDeleteBtn.getStyleClass().add("btn-danger-pill");
    }
  }

  // FIXEDHERE: goes through setStock(), which uses restockProduct/decreaseProduct correctly
  private void handleChangeStock(Product item, int change) 
  {
    int newQty = getQty(item) + change;
    if (newQty < 0)
      return;

    if (!setStock(item, newQty))
      showAlert(Alert.AlertType.ERROR, "Error", "Could not update the stock for this product.");
    refreshTable();
  }

  private void handleSetStockFromField(Product item, TextField field){
  int current = getQty(item);

  try {
    int newQty = Integer.parseInt(field.getText().trim());
    if (newQty < 0)
      throw new NumberFormatException();
    if (newQty == 0) {
      inventory.removeProduct(item);
      availability.remove(item.getId());
      refreshTable();
      return;
    }
    if (newQty == current)
      return;
    if (!setStock(item, newQty)) {
      field.setText(String.valueOf(current));
      showAlert(Alert.AlertType.ERROR, "Error", "Could not update the stock for this product.");
      return;
    }
    refreshTable();
  } catch (NumberFormatException ex) {
    field.setText(String.valueOf(current)); // revert
    showAlert(Alert.AlertType.ERROR, "Input Error", "Stock must be a whole number of 0 or more.");
  }
}

  private boolean setStock(Product item, int newQty){
    int diff = newQty - getQty(item);

    if (diff > 0)
      return inventory.restockProduct(item, diff);
    if (diff < 0)
      return inventory.decreaseProduct(item, -diff);
    return true; // no change
  }

  private void showAddDialog() { //ADD POPUP
    foundProduct = null;

    TextField nameField = new TextField();
    nameField.setPromptText("e.g., Seeds");
    Button btnSearch = new Button("Search");
    btnSearch.getStyleClass().add("btn-olive-pill");

    Label nameLabel = new Label("Product Name: ...");
    Label idLabel = new Label("Product ID: ...");
    Label descLabel = new Label("Description: ...");
    Label priceLabel = new Label("Standard Price: ...");

    btnSearch.setOnAction(e -> {
      String text = nameField.getText().trim();
      foundProduct = productManager.getProductByName(text); 
      if (foundProduct == null){
        foundProduct = productManager.getProductById(text); // ACCEPTS ID TOO
      }

      if (foundProduct == null) { // NO PRODUCT MATCH
        nameLabel.setText("Product Name: ...");
        idLabel.setText("Product ID: ...");
        descLabel.setText("Description: ...");
        priceLabel.setText("Standard Price: ...");
        showAlert(Alert.AlertType.ERROR, "Not Found", "No such product available in product catalog!");
      } else { // shows the deets of the found product
        nameLabel.setText("Product Name: " + foundProduct.getName());
        idLabel.setText("Product ID: " + foundProduct.getId());
        descLabel.setText("Description: " + foundProduct.getDescription());
        priceLabel.setText("Standard Price: Php " + foundProduct.getPrice());
      }
    });

    TextField quantityField = new TextField();
    quantityField.setPromptText("e.g., 20");
    VBox avaiBox = createAvaiBox("Available");

    Label searchLbl = new Label("Product Name:");
    searchLbl.getStyleClass().add("field-label");
    Label qtyLbl = new Label("Product Quantity:");
    qtyLbl.getStyleClass().add("field-label");

    VBox left = new VBox(8, searchLbl, new HBox(8, nameField, btnSearch), nameLabel, idLabel, descLabel, priceLabel, qtyLbl, quantityField);
    HBox content = new HBox(24, left, avaiBox);

    if (!showDialog("Add Product to Inventory", content)) {
      return;
    }

    if (foundProduct == null) {
      showAlert(Alert.AlertType.ERROR, "Validation Error", "Search for a product first.");
      return;
    }

    for (Product item : inventory.getItems()) {
      if (item.getId().equals(foundProduct.getId())) {
        showAlert(Alert.AlertType.ERROR, "Error", "That product is already in the inventory. Try Restocking?");
        return;
      }
    }
    try {
      int qty = Integer.parseInt(quantityField.getText().trim());
      if (qty < 0){
        throw new NumberFormatException();
      }
      if (!getStatus(avaiBox).equals("Available")) {
        showAlert(Alert.AlertType.ERROR, "Not Added", "Only products marked Available can be added to the inventory.");
        return;
      }
      inventory.addProduct(foundProduct, qty);
      availability.put(foundProduct.getId(), "Available");
      refreshTable();
    } catch (NumberFormatException ex) {
      showAlert(Alert.AlertType.ERROR, "Input Error", "Quantity must be a whole number of 0 or more.");
    }
  }

  private void showEditDialog(Product item) { // edit popUP
    Label stockLabel = new Label("Product Quantity: " + getQty(item));
    VBox left = new VBox(8,
      new Label("Product Name: " + item.getName()),
      stockLabel,
      new Label("Standard Price: Php " + item.getPrice()),
      new Label("Product ID: " + item.getId()),
      new Label("Description: " + item.getDescription())
    );

    TextField stockField = new TextField();
    stockField.setPromptText("Qty to add");
    Button btnRestock = new Button("Restock");
    btnRestock.getStyleClass().add("btn-olive-pill");
    btnRestock.setOnAction(e -> handleRestockProduct(item, stockField, stockLabel));

    TextField priceField = new TextField(String.valueOf(item.getPrice()));
    VBox avaiBox = createAvaiBox(getAvailability(item.getId()));

    Label stockLbl = new Label("Stock:");
    stockLbl.getStyleClass().add("field-label");
    Label priceLbl = new Label("Updated Price:");
    priceLbl.getStyleClass().add("field-label");

    VBox right = new VBox(8,
      stockLbl, new HBox(8, stockField, btnRestock),
      priceLbl, priceField,
      avaiBox
    );

    if (!showDialog("Edit Product Availability", new HBox(24, left, right))) {
      return;
    }

    try {
      item.setPrice(Double.parseDouble(priceField.getText().trim()));

      // Unavailable/Unlisted products are no longer in inventory
      String status = getStatus(avaiBox);
      if (!status.equals("Available")) {
        inventory.removeProduct(item);
        availability.remove(item.getId());
      } else {
        availability.put(item.getId(), status);
      }

      refreshTable();
    } catch (NumberFormatException ex) {
      showAlert(Alert.AlertType.ERROR, "Input Error", "Price must be a valid number.");
    }
  }

  private void handleRestockProduct(Product item, TextField stockField, Label stockLabel) 
  {
    try {
      int addQty = Integer.parseInt(stockField.getText().trim());
      if (addQty < 0) {
        showAlert(Alert.AlertType.ERROR, "Input Error", "Restock amount cannot be negative.");
        return;
      }

      if (inventory.restockProduct(item, addQty)) {
        stockLabel.setText("Product Quantity: " + getQty(item)); 
        stockField.clear();
        refreshTable();
      } else {
        showAlert(Alert.AlertType.ERROR, "Error", "Product not found in inventory.");
      }
    } catch (NumberFormatException ex) {
      showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid stock quantity to add.");
    }
  }

  private void handleDeleteProduct() {
    for (Product item : new ArrayList<>(checkedItems)) {
      inventory.removeProduct(item);
      availability.remove(item.getId());
    }
    refreshTable();
  }

  private boolean showDialog(String title, HBox content) {
    content.setPadding(new Insets(14));
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle(title);
    if (getScene() != null) {
      dialog.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
    }
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    return dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
  }

  private VBox createAvaiBox(String availability) {
    Label statusHdr = new Label("Status:");
    statusHdr.getStyleClass().add("field-label");
    VBox box = new VBox(8, statusHdr);

    for (String status : new String[] { "Available", "Unavailable", "Unlisted" }) {
      CheckBox check = new CheckBox(status);
      check.setSelected(status.equals(availability));

      check.setOnAction(e -> {
        if (check.isSelected()) {
          for (javafx.scene.Node node : box.getChildren()) {
            if (node instanceof CheckBox cb && cb != check) {
              cb.setSelected(false);
            }
          }
        } else {
          check.setSelected(true);
        }
      });

      box.getChildren().add(check);
    }

    return box;
  }

  // NOTE IT IS STATIC SO OTHER TABS MAY HIDE UNAVAILABLE / UNLISTED ITEMS
  public static String getAvailability(String id) 
  {
    return availability.getOrDefault(id, "Available");
  }

  private String getStatus(VBox avaiBox) 
  {
    for (javafx.scene.Node node : avaiBox.getChildren()) {
      if (node instanceof CheckBox check && check.isSelected()) {
        return check.getText();
      }
    }
    return "Available";
  }

  public void refreshTable() {
    String search = searchField.getText().trim().toLowerCase();
    List<Product> matches = new ArrayList<>();

    for (Product item : inventory.getItems()) {
      if (!getAvailability(item.getId()).equals("Available"))
        continue;
      if (item.getName().toLowerCase().contains(search) || item.getId().toLowerCase().contains(search))
        matches.add(item);
    }

    rowsBox.getChildren().clear();
    checkedItems.clear();
    for (Product item : matches) {
      rowsBox.getChildren().add(createRow(item));
    }

    updateHeaderButton();
  }

  private void showAlert(Alert.AlertType type, String title, String content) {
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
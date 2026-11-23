package frontend;

import backend.Inventory;
import backend.Product;
import backend.ProductManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementView extends BorderPane
{
  private ProductManager productManager;
  private Inventory inventory;

  private VBox rowsBox;
  private List<Product> checkedProducts = new ArrayList<>();

  private TextField searchField;
  private Button addOrDeleteBtn;

  public ProductManagementView(ProductManager productManager, Inventory inventory)
  {
    this.productManager = productManager;
    this.inventory = inventory;

    getStyleClass().add("page-container");
    setCenter(createMainSection());

    refreshTable();
  }

  private VBox createMainSection() 
  {
    searchField = new TextField();
    searchField.setPromptText("Search by name or id...");
    searchField.getStyleClass().add("search-pill");
    searchField.textProperty().addListener((obs, oldText, newText) -> refreshTable());

    Label title = new Label("Products");
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

  private HBox createRow(Product p) 
  {
    CheckBox check = new CheckBox();
    check.setOnAction(e -> {
      if (check.isSelected())
        checkedProducts.add(p);
      else
        checkedProducts.remove(p);
      updateHeaderButton();
    });

    Label name = new Label(p.getName());
    name.getStyleClass().add("row-title");

    Label info = new Label(String.format("Php %.2f  |  ID: %s", p.getPrice(), p.getId()));
    info.getStyleClass().add("row-subtitle");

    VBox textCol = new VBox(2, name, info);
    textCol.setPrefWidth(210);

    Label desc = new Label(p.getDescription());
    desc.getStyleClass().add("row-tag");

    Button editBtn = new Button("Edit");
    editBtn.getStyleClass().add("btn-olive-pill");
    editBtn.setOnAction(e -> showEditDialog(p));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(14, check, textCol, desc, spacer, editBtn);
    row.getStyleClass().add("row-item");
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private void handleHeaderButton() 
  {
    if (checkedProducts.isEmpty())
      showAddDialog();
    else
      handleDeleteProduct();
  }

  private void updateHeaderButton() 
  {
    addOrDeleteBtn.getStyleClass().removeAll("btn-olive-pill", "btn-danger-pill");
    if (checkedProducts.isEmpty()) {
      addOrDeleteBtn.setText("ADD +");
      addOrDeleteBtn.getStyleClass().add("btn-olive-pill");
    } else {
      addOrDeleteBtn.setText("Delete (" + checkedProducts.size() + ")");
      addOrDeleteBtn.getStyleClass().add("btn-danger-pill");
    }
  }

  private void showAddDialog() 
  {
    TextField nameField = new TextField();
    TextField qtyField = new TextField("0");
    TextField priceField = new TextField();
    TextField idField = new TextField();
    TextField descField = new TextField();

    VBox content = new VBox(6,
      labeled("Product Name:"), nameField,
      labeled("Initial Quantity:"), qtyField,
      labeled("Price:"), priceField,
      labeled("Product ID:"), idField,
      labeled("Description:"), descField
    );
    content.setPrefWidth(320);

    if (!showDialog("Add New Product", content))
      return;

    try {
      double price = Double.parseDouble(priceField.getText().trim());
      int qty = Integer.parseInt(qtyField.getText().trim());

      if (qty < 0) {
        showAlert("Initial quantity can't be negative.");
        return;
      }

      Product product = productManager.addProduct(
        idField.getText().trim(), nameField.getText().trim(), descField.getText().trim(), price);

      if (qty > 0)
        inventory.addProduct(product, qty);

      refreshTable();
    } catch (NumberFormatException ex) {
      showAlert("Price and Initial Quantity must be valid numbers.");
    } catch (IllegalArgumentException ex) {
      showAlert(ex.getMessage());
    }
  }

  private void showEditDialog(Product p) 
  {
    TextField nameField = new TextField(p.getName());
    TextField priceField = new TextField(String.valueOf(p.getPrice()));
    TextField descField = new TextField(p.getDescription());

    Label idInfo = new Label("Product ID: " + p.getId());
    idInfo.getStyleClass().add("muted-label");

    VBox content = new VBox(6,
      labeled("Product Name:"), nameField,
      labeled("Price:"), priceField,
      idInfo,
      labeled("Description:"), descField
    );
    content.setPrefWidth(320);

    if (!showDialog("Edit Product Details", content)) {
      return;
    }

    try {
      String name = nameField.getText().trim();
      double price = Double.parseDouble(priceField.getText().trim());
      Product sameName = productManager.getProductByName(name);

      if (name.isEmpty() || price < 0) {
        showAlert("Name can't be empty and price can't be negative.");
      } else if (sameName != null && sameName != p) {
        showAlert("Another product already has that name.");
      } else {
        p.setName(name);
        p.setPrice(price);
        p.setDescription(descField.getText().trim());
        refreshTable();
      }
    } catch (NumberFormatException ex) {
      showAlert("Price must be a valid number.");
    }
  }

  private Label labeled(String text) 
  {
    Label l = new Label(text);
    l.getStyleClass().add("field-label");
    return l;
  }

  private void handleDeleteProduct() 
  {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + checkedProducts.size() + " product(s)? They will also be removed from the inventory.", ButtonType.OK, ButtonType.CANCEL);
    confirm.setHeaderText(null);
    if (getScene() != null) {
      confirm.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
    }
    if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
      return;
    }

    for (Product p : new ArrayList<>(checkedProducts)) {
      productManager.removeProduct(p.getId());
      inventory.removeProduct(p);
    }
    refreshTable();
  }

  public void refreshTable() 
  {
    String search = searchField.getText().trim().toLowerCase();
    List<Product> matches = new ArrayList<>();

    for (Product p : productManager.getProducts().values()) {
      if (p.getName().toLowerCase().contains(search) || p.getId().toLowerCase().contains(search))
        matches.add(p);
    }
    rowsBox.getChildren().clear();
    checkedProducts.clear();
    for (Product p : matches) {
      rowsBox.getChildren().add(createRow(p));
    }

    updateHeaderButton();
  }

  private boolean showDialog(String title, VBox content) 
  {
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

  private void showAlert(String content) 
  {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setHeaderText(null);
    if (getScene() != null) {
      alert.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
    }
    alert.setContentText(content);
    alert.showAndWait();
  }
}

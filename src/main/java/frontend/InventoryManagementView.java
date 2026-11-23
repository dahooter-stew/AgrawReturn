package frontend;

import backend.Inventory;
import backend.Product;
import backend.ProductManager;

import javafx.geometry.Pos; // row contents alignment in inventory table
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * v e r y  rough na hahahaha ui
 */

public class InventoryManagementView extends BorderPane 
{
  // inventory stuff
  private Inventory inventory;
  // private TableView<InventoryItem> table;
  // private ObservableList<InventoryItem> tableData;
  private Map<String, String> availability = new HashMap<>(); // AVAILABILITY NOT IN PRODUCT

  // PRODUCT MANAGER
  private ProductManager productManager;
  private Product foundProduct; // adding products to inventory from product manager cataglog

  // inventory table in this vbox
  private VBox rowsBox;
  private List<Product> checkedItems = new ArrayList<>();
 
  // inputs and labels
  private TextField searchField;
  private Button addOrDeleteBtn;

  // constructor
  public InventoryManagementView(Inventory inventory, ProductManager productManager) {
    this.inventory = inventory;
    this.productManager = productManager;
    setPadding(new Insets(15));
    setCenter(createMainSection());   // no setLeft(createSideBar()), MainView owns the sidebar
    refreshTable();
  }

  private VBox createMainSection(){

    // buttons IN THE HEADER
    searchField = new TextField();
    searchField.setPromptText("Search by name or id..");
    searchField.textProperty().addListener((obs, oldText, newText) -> refreshTable());

    Label title = new Label("Inventory");
    title.setStyle("-fx-font-size: 20px;");


    addOrDeleteBtn = new Button("Add +");
    addOrDeleteBtn.setOnAction(e -> handleHeaderButton());

    BorderPane header = new BorderPane();
    header.setLeft(searchField);
    header.setCenter(title);
    header.setRight(new HBox(20, addOrDeleteBtn));

    rowsBox = new VBox(5);

    // with fancy scroll feature
    ScrollPane scroll = new ScrollPane(rowsBox);
    scroll.setFitToWidth(true);
    VBox.setVgrow(scroll, Priority.ALWAYS);

    return new VBox(10, header, scroll);

  }

  // CREATE THE ROWS IN THE MAIN AREA WITH THE INVENTORY ROWS

  private HBox createRow(Product item){

    CheckBox check = new CheckBox();
    check.setOnAction(e -> {
      if (check.isSelected())
        checkedItems.add(item);
      else
        checkedItems.remove(item);
      updateHeaderButton();
    });

    Label name = new Label(item.getName());
    name.setStyle("-fx-font-weight: bold;"); // BOLDEN IT
    Label info = new Label("Php " + item.getPrice() + " | Stock: " + inventory.getProductQuantity(item));
    Label desc = new Label(item.getDescription());     

    Button editBtn = new Button("Edit");
    editBtn.setOnAction(e -> showEditDialog(item));

    //MODIFY STOCK AS MANAGER feature
    Button minusStock = new Button("-");
    minusStock.setOnAction(e -> handleChangeStock(item, -1));
    Button addStock = new Button("+");
    addStock.setOnAction(e -> handleChangeStock(item, 1));
    Label qty = new Label(String.valueOf(inventory.getProductQuantity(item))); // display qty as well

    Region spacer = new Region();                   // pushes Edit and - + to the right
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(10, check, new VBox(name, info), desc, spacer, editBtn, minusStock, qty, addStock);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private void handleHeaderButton(){
    if (checkedItems.isEmpty())
      showAddDialog();
    else
      handleDeleteProduct();
  }

  private void updateHeaderButton() {
    addOrDeleteBtn.setText(checkedItems.isEmpty() ? "Add +" : "Delete");
  }

  private void handleChangeStock(Product item, int change){
    if (inventory.getProductQuantity(item) + change < 0)
      return;

    inventory.restockProduct(item, change);
    refreshTable();
  }


private void showAddDialog(){
    foundProduct = null;

    TextField nameField = new TextField();
    nameField.setPromptText("e.g., Seeds");
    Button btnSearch = new Button("Search");

    // READ ONLY PRODUCT DETAILS DEFAULT // updates when a product to be added is found!
    Label nameLabel = new Label("Product Name: ...");
    Label idLabel = new Label("Product ID: ...");
    Label descLabel = new Label("Description: ...");
    Label priceLabel = new Label("Standard Price: ...");

    // search product manager by id , only products available in product catalog can be added to inventory
    btnSearch.setOnAction(e -> {
      foundProduct = productManager.getProductByName(nameField.getText().trim());

      if (foundProduct == null) {
        nameLabel.setText("Product Name: ...");
        idLabel.setText("Product ID: ...");
        descLabel.setText("Description: ...");
        priceLabel.setText("Standard Price: ...");
        showAlert(Alert.AlertType.ERROR, "Not Found", "No such product available in product catalog!");
      } 
      else {
        nameLabel.setText("Product Name: " + foundProduct.getName());
        idLabel.setText("Product ID: " + foundProduct.getId());
        descLabel.setText("Description: " + foundProduct.getDescription());
        priceLabel.setText("Standard Price: " + foundProduct.getPrice());
      }
    });

    TextField quantityField = new TextField();
    VBox avaiBox = createAvaiBox("Available");

    VBox left = new VBox(5,new Label("Product Name:"), new HBox(5, nameField, btnSearch), nameLabel, idLabel, descLabel, priceLabel, new Label("Product Quantity:"), quantityField);
    HBox content = new HBox(20, left, avaiBox);


    if (!showDialog("Add Product to Inventory", content)){
      return;
    }

    // no product was even found
    if (foundProduct == null) {
      showAlert(Alert.AlertType.ERROR, "Validation Error", "Search for a product ID first.");
      return;
    }
    // avoid repeat by checking existing inventory items
    for (Product item : inventory.getItems()) {
      if (item.getId().equals(foundProduct.getId())) 
      {
        showAlert(Alert.AlertType.ERROR, "Error", "That product is already in the inventory. Try Restocking?");
        return;
      }
    }

    try {
      int qty = Integer.parseInt(quantityField.getText().trim());
      inventory.addProduct(foundProduct, qty);   // CHANGE: uses the product from ProductManager
      availability.put(foundProduct.getId(), getStatus(avaiBox));
      refreshTable();
    } 
    catch (NumberFormatException ex) {
      showAlert(Alert.AlertType.ERROR, "Input Error", "Quantity must be a valid number.");
    }
  }

  // EDIT INVENTORY
private void showEditDialog(Product item) {
  // LEFT SIDE CONTAINS UNEDITABLE PRODUCT DETAILS
  Label stockLabel = new Label("Product Quantity: " + inventory.getProductQuantity(item));
  VBox left = new VBox(5,
    new Label("Product Name: " + item.getName()),
    stockLabel,
    new Label("Standard Price: " + item.getPrice()),
    new Label("Product ID: " + item.getId()),
    new Label("Description: " + item.getDescription())
  );

  // RIGHT SIDE CONTAINS EDITABLE INVENTORY PRODUCT DETAILS
  TextField stockField = new TextField();
  stockField.setPromptText("Qty to add");
  Button btnRestock = new Button("Restock");
  btnRestock.setOnAction(e -> handleRestockProduct(item, stockField, stockLabel));

  TextField priceField = new TextField(String.valueOf(item.getPrice()));
  VBox avaiBox = createAvaiBox(getAvailability(item.getId())); 

  VBox right = new VBox(5,
    new Label("Stock:"), new HBox(5, stockField, btnRestock),
    new Label("Updated Price:"), priceField,
    avaiBox                                                   
  );

  if (!showDialog("Edit Product Availability", new HBox(20, left, right))){
    return;
  }

  try {
    item.setPrice(Double.parseDouble(priceField.getText().trim()));
    availability.put(item.getId(), getStatus(avaiBox));         
    // hides upon refresh if status is NOT available
    refreshTable();                                           
  } 
  catch (NumberFormatException ex) {
    showAlert(Alert.AlertType.ERROR, "Input Error", "Price must be a valid number.");
  }
}

  private void handleRestockProduct(Product item, TextField stockField, Label stockLabel){
    try{
      int addQty = Integer.parseInt(stockField.getText().trim());

      if (inventory.restockProduct(item, addQty)) {
        stockLabel.setText("Product Quantity: " + inventory.getProductQuantity(item));
        stockField.clear();
        refreshTable();
      } 
      else 
        showAlert(Alert.AlertType.ERROR, "Error", "Product ID not found.");
    } 
    catch (NumberFormatException ex) 
    {
      showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid stock quantity to add.");
    }
  }

  private void handleDeleteProduct() {
    for (Product item : new ArrayList<>(checkedItems)){
      inventory.removeProduct(item);
      availability.remove(item.getId());
    }
    refreshTable();
  }

  private boolean showDialog(String title, HBox content){ // HELPER FOR EXITING POPUPS (like when OK / CANCELLED is pressed)
    content.setPadding(new Insets(10)); 
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    return dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
  }


  private VBox createAvaiBox(String availability){
    VBox box = new VBox(5);
    for (String status : new String[] { "Available", "Unavailable", "Unlisted" }) 
    {
      CheckBox check = new CheckBox(status);
      check.setSelected(status.equals(availability));

      check.setOnAction(e -> {
        if (check.isSelected()) 
        {
          // untick every other checkbox in the box
          for (javafx.scene.Node node : box.getChildren())
            if (node != check)
              ((CheckBox) node).setSelected(false);
        } 
        else
          check.setSelected(true);   // a product always needs a status
      });

      box.getChildren().add(check);
    }

    return box;
  }

  private String getAvailability(String id){ // GETS AVAILABILITY BASED ON HASHMAP
    return availability.getOrDefault(id, "Available");
  }

  private String getStatus(VBox avaiBox) { // SHOWS WHAT IS CURRENTLY BEING SELECTED IN CHECKBOX
    for (javafx.scene.Node node : avaiBox.getChildren()) 
    {
      CheckBox check = (CheckBox) node;
      if (check.isSelected()){
        return check.getText();
      }
       
    }
    return "Available";
  }

  // KEEPS IN MIND WHEN THE TABLE REFRESHES WHEN SOMETHING IS SEARCHED
  // hides unavailable/unlisted products when updated that way
  public void refreshTable() {
    String search = searchField.getText().trim().toLowerCase();
    List<Product> matches = new ArrayList<>();

    for (Product item : inventory.getItems()){

      // only available allowed
      if (!getAvailability(item.getId()).equals("Available"))
        continue;
      // matches the search
      if (item.getName().toLowerCase().contains(search) || item.getId().toLowerCase().contains(search))
        matches.add(item);
    }

    // one row per product
    rowsBox.getChildren().clear();
    checkedItems.clear();
    for (Product item : matches) {
      rowsBox.getChildren().add(createRow(item));
      rowsBox.getChildren().add(new Separator());
    }

    updateHeaderButton();
  }

  private void showAlert(Alert.AlertType type, String title, String content){
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

}

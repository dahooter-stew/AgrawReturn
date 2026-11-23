package frontend;


import backend.Inventory;
import backend.Product;
import backend.ProductManager;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// NOTE: THIS VIEW IS SIMILAR TO INV MANAGEMENT VIEW JUST THAT THIS CONTROLS THE PRODUCT CATALOG ONLY, STOCKS CANNOT BE ADJUSTED HERE


public class ProductManagementView extends BorderPane
{
 // backend stuff
 private ProductManager productManager;
 private Inventory inventory;   // for the initial quantity, and to remove deleted products from stock


 // products table in this vbox
 private VBox rowsBox;
 private List<Product> checkedProducts = new ArrayList<>();


 // inputs and labels
 private TextField searchField;
 private Button addOrDeleteBtn;


 // constructor
 public ProductManagementView(ProductManager productManager, Inventory inventory)
 {
   this.productManager = productManager;
   this.inventory = inventory;

   setPadding(new Insets(15));
   setCenter(createMainSection());

   refreshTable();
 }


 // HEADER
 private VBox createMainSection(){
   searchField = new TextField();
   searchField.setPromptText("Search by name or id..");
   searchField.textProperty().addListener((obs, oldText, newText) -> refreshTable());


   Label title = new Label("Products");
   title.setStyle("-fx-font-size: 20px;");


   addOrDeleteBtn = new Button("Add +");
   addOrDeleteBtn.setOnAction(e -> handleHeaderButton());


   BorderPane header = new BorderPane();
   header.setLeft(searchField);
   header.setCenter(title);
   header.setRight(new HBox(10, addOrDeleteBtn));


   rowsBox = new VBox(5);
   ScrollPane scroll = new ScrollPane(rowsBox);
   scroll.setFitToWidth(true);
   VBox.setVgrow(scroll, Priority.ALWAYS);


   return new VBox(10, header, scroll);
 }


 // PRODUCT ROW
 private HBox createRow(Product p) {
   CheckBox check = new CheckBox();
   check.setOnAction(e -> {
     if (check.isSelected())
       checkedProducts.add(p);
     else
       checkedProducts.remove(p);
     updateHeaderButton();
   });


   Label name = new Label(p.getName());
   name.setStyle("-fx-font-weight: bold;");
   Label info = new Label("Php " + p.getPrice() + " | ID: " + p.getId());
   Label desc = new Label(p.getDescription());


   Button editBtn = new Button("Edit");
   editBtn.setOnAction(e -> showEditDialog(p));

   Region spacer = new Region();
   HBox.setHgrow(spacer, Priority.ALWAYS);


   HBox row = new HBox(10, check, new VBox(name, info), desc, spacer, editBtn);
   row.setAlignment(Pos.CENTER_LEFT);
   return row;
 }


 private void handleHeaderButton() { // header btn either can add or delete depending if product(s) ARE selected
   if (checkedProducts.isEmpty())
     showAddDialog();
   else
     handleDeleteProduct();
 }


 private void updateHeaderButton() {
   addOrDeleteBtn.setText(checkedProducts.isEmpty() ? "Add +" : "Delete");
 }


 private void showAddDialog() {
   TextField nameField = new TextField();
   TextField qtyField = new TextField("0");
   TextField priceField = new TextField();
   TextField idField = new TextField();
   TextField descField = new TextField();

   VBox content = new VBox(5,
     new Label("Product Name:"), nameField,
     new Label("Initial Quantity:"), qtyField,
     new Label("Price:"), priceField,
     new Label("Product ID:"), idField,
     new Label("Description:"), descField
   );

   if (!showDialog("Add New Product", content))
     return;

   try
   {
     double price = Double.parseDouble(priceField.getText().trim());
     int qty = Integer.parseInt(qtyField.getText().trim());

     if (qty < 0)
     {
       showAlert("Initial quantity can't be negative.");
       return;
     }

     // ProductManager checks the id, name and price
     Product product = productManager.addProduct(
       idField.getText().trim(), nameField.getText().trim(), descField.getText().trim(), price);

     // a positive initial quantity also puts the product in the inventory (otherwise it just exists in the catalog)
     if (qty > 0)
       inventory.addProduct(product, qty);


     refreshTable();
   }
   catch (NumberFormatException ex)
   {
     showAlert("Price and Initial Quantity must be valid numbers.");
   }
   catch (IllegalArgumentException ex)   // from ProductManager.addProduct
   {
     showAlert(ex.getMessage());
   }
 }


 // EDIT PRODUCT DETAILS
 private void showEditDialog(Product p) {
   TextField nameField = new TextField(p.getName());
   TextField priceField = new TextField(String.valueOf(p.getPrice()));
   TextField descField = new TextField(p.getDescription());


   VBox content = new VBox(5,
     new Label("Product Name:"), nameField,
     new Label("Price:"), priceField,
     new Label("Product ID: " + p.getId()),
     new Label("Description:"), descField
   );
   if (!showDialog("Edit Product Details", content)){
     return;
   }


   try  {
     String name = nameField.getText().trim();
     double price = Double.parseDouble(priceField.getText().trim());
     Product sameName = productManager.getProductByName(name);


     if (name.isEmpty() || price < 0) {
       showAlert("Name can't be empty and price can't be negative.");
     }
     else if (sameName != null && sameName != p) { // names must stay unique
       showAlert("Another product already has that name.");
     }  else {
       p.setName(name);
       p.setPrice(price);
       p.setDescription(descField.getText().trim());
       refreshTable();
     }
   }
   catch (NumberFormatException ex) {
     showAlert("Price must be a valid number.");
   }
 }


 // DELETES THE CHECKED PRODUCTS FROM THE CATALOG AND FROM THE INVENTORY
 private void handleDeleteProduct()  {
   Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + checkedProducts.size() + " product(s)? They will also be removed from the inventory.", ButtonType.OK, ButtonType.CANCEL);
   confirm.setHeaderText(null);
   if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK){
    return;
   }

   for (Product p : new ArrayList<>(checkedProducts)){
     productManager.removeProduct(p.getId());
     inventory.removeProduct(p);
   }
   refreshTable();
 }


 public void refreshTable()  { // REFRESH IN PRODUCT VIEW only shows what matches the search (by default, its all products in catalog)
   String search = searchField.getText().trim().toLowerCase();
   List<Product> matches = new ArrayList<>();

   for (Product p : productManager.getProducts().values()){
     if (p.getName().toLowerCase().contains(search) || p.getId().toLowerCase().contains(search))
       matches.add(p);
   }
   rowsBox.getChildren().clear();
   checkedProducts.clear();
   for (Product p : matches)
   {
     rowsBox.getChildren().add(createRow(p));
     rowsBox.getChildren().add(new Separator());
   }

   updateHeaderButton();
 }


 // POPUP WITH OK / CANCEL
 private boolean showDialog(String title, VBox content){
   content.setPadding(new Insets(10));

   Dialog<ButtonType> dialog = new Dialog<>();
   dialog.setTitle(title);
   dialog.getDialogPane().setContent(content);
   dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


   return dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
 }


 private void showAlert(String content) {
   Alert alert = new Alert(Alert.AlertType.ERROR);
   alert.setHeaderText(null);
   alert.setContentText(content);
   alert.showAndWait();
 }
}


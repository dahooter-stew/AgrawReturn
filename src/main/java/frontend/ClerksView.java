package frontend;

import backend.Clerk;
import backend.UserManager;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

// THIS IS WHERE THE MANAGER ADDS AND REMOVES CLERKS, EXTRA FEATURE IN MANAGER SIDE BAR
public class ClerksView extends VBox
{
  private final UserManager users;
  private final ListView<String> clerks = new ListView<>(); // has the clerk usernames

  public ClerksView(UserManager users){
    this.users = users;
    setSpacing(10);
    setPadding(new Insets(15));

    Label title = new Label("Clerks");
    title.setStyle("-fx-font-size: 20px;");

    VBox.setVgrow(clerks, Priority.ALWAYS);

    TextField nameField = new TextField();
    nameField.setPromptText("Username");
    PasswordField passField = new PasswordField();
    passField.setPromptText("Password");

    // ADD UDSER
    Button add = new Button("Add Clerk");
    add.setOnAction(e -> {
      try {
        users.addUser(new Clerk(nameField.getText().trim(), passField.getText()));
        nameField.clear();
        passField.clear();
        refresh();
      }
      catch (IllegalArgumentException ex) {
        showAlert(ex.getMessage());
      }
    });

    // DELETE SELECTED
    Button remove = new Button("Remove Selected");
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
    getChildren().addAll(title, clerks, new HBox(10, nameField, passField, add), remove);
    refresh(); 
  }

  // reloads the usernames of the clerks currently
  private void refresh() {
    clerks.getItems().clear();
    for (Clerk clerk : users.getClerks()) {
      clerks.getItems().add(clerk.getUsername());
    }
  }

  private void showAlert(String msg) { // WHEN ADDING FAILS...
    Alert a = new Alert(Alert.AlertType.ERROR);
    a.setHeaderText(null);
    a.setContentText(msg);
    a.showAndWait();
  }
}
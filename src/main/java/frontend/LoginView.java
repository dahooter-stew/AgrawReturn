package frontend;

import backend.User;
import backend.Manager;
import backend.UserManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

// just a simple login screen
public class LoginView extends VBox
{
  private final UserManager users;
  private final Consumer<User> onLogin;

  private TextField userField;
  private PasswordField passField;
  private Label errorLabel;

  public LoginView(UserManager users, Consumer<User> onLogin)
  {
    this.users = users;
    this.onLogin = onLogin;

    setSpacing(12);
    setPadding(new Insets(32));
    setAlignment(Pos.CENTER_LEFT);
    setMaxWidth(360);
    getStyleClass().add("sheet-card");

    Label titleLabel = new Label("AgrawReturn()");
    titleLabel.getStyleClass().add("page-title");

    Label userLbl = new Label("Username:");
    userLbl.getStyleClass().add("field-label");
    userField = new TextField();
    userField.setPromptText("Username");

    Label passLbl = new Label("Password:");
    passLbl.getStyleClass().add("field-label");
    passField = new PasswordField();
    passField.setPromptText("Password");

    errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: #9E3B33; -fx-font-weight: bold;");

    // DIFFERENT ACTIONS DEPENDING ON WHO THE USER IS LOGGING IN AS

    Button btnManager = new Button("Login as Manager");
    btnManager.setMaxWidth(Double.MAX_VALUE);
    btnManager.getStyleClass().add("btn-forest");
    btnManager.setOnAction(e -> attemptLogin(true));

    Button btnClerk = new Button("Login as Clerk");
    btnClerk.setMaxWidth(Double.MAX_VALUE);
    btnClerk.getStyleClass().add("btn-olive-pill");
    btnClerk.setOnAction(e -> attemptLogin(false));

    getChildren().addAll(titleLabel, userLbl, userField, passLbl, passField, errorLabel, btnManager, btnClerk);
  }

  private void attemptLogin(boolean asManager)
  {
    User user = users.login(userField.getText().trim(), passField.getText());

    if (user == null) {
      errorLabel.setText("Wrong username or password.");
      passField.clear();
    } else if (user instanceof Manager != asManager) {
      errorLabel.setText("Wrong log-in for account type!");
      passField.clear();
    } else {
      onLogin.accept(user);
    }
  }
}

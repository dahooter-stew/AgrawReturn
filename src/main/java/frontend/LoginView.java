package frontend;

import backend.User;
import backend.UserManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

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

    setSpacing(15);
    setPadding(new Insets(30));
    setAlignment(Pos.CENTER);
    setMaxWidth(350);

    Label titleLabel = new Label("System Login");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

    userField = new TextField();
    userField.setPromptText("Username");

    passField = new PasswordField();
    passField.setPromptText("Password");

    errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: red;");

    // FOR DA DIFFERENT VIEWS
    Button btnManager = new Button("Login as Manager");
    btnManager.setMaxWidth(Double.MAX_VALUE);
    btnManager.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
    btnManager.setOnAction(e -> attemptLogin(true)); // im logging in as manager!

    Button btnClerk = new Button("Login as Clerk");
    btnClerk.setMaxWidth(Double.MAX_VALUE);
    btnClerk.setStyle("-fx-background-color: #00ff00; -fx-text-fill: white;");
    btnClerk.setOnAction(e -> attemptLogin(false)); // im logging in as clerk!

    // PUT ON SCREEN
    getChildren().addAll(titleLabel, new Label("Username:"), userField, new Label("Password:"), passField, errorLabel, btnManager, btnClerk);

  }
  // login shenanigans help
  private void attemptLogin(boolean asManager){
    User user = users.login(userField.getText().trim(), passField.getText()); // see who matches!

    if (user == null) {
      errorLabel.setText("Wrong username or password.");
      passField.clear();
    }
    else if (user.isManager() != asManager) {
      errorLabel.setText("Wrong log-in for account type!");
      passField.clear();
    }
    else {
      onLogin.accept(user); // OPEN MAIN VIEW
    }
  }
}

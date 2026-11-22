package frontend;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

// LOWKEY JUST A BLANK SCREEN FOR NOW
public class PlaceholderView extends BorderPane
{
  public PlaceholderView(String title)
  {
    setPadding(new Insets(15));
    Label l = new Label(title);
    l.setStyle("-fx-font-size: 20px;");
    setTop(l);
  }
}
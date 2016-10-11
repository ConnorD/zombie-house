package gamePackage.mainPackage.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * This class manages the dialog that is shown before the user begins playing the game.
 *
 * @author Connor Denman
 */
public class StartDialog extends Alert
{
  public static final ButtonType START_BUTTON_TYPE = new ButtonType("Start Game");

  public StartDialog()
  {
    super(AlertType.INFORMATION);
    setTitle("Welcome to Zombie House");
    setContentText("The zombies are out to eat your brains! Navigate your way to the exit before all your health runs out!");
    getButtonTypes().setAll(START_BUTTON_TYPE);
  }

}

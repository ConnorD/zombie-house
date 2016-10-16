package gamePackage.mainPackage.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * @author Connor Denman
 */
public class GameOverDialog extends Alert
{
  public static final ButtonType REPLAY_BUTTON_TYPE = new ButtonType("Replay");

  public GameOverDialog()
  {
    super(AlertType.ERROR);
    setTitle("Game Over");
    setContentText("The zombies have eaten your brains!");
    getButtonTypes().setAll(REPLAY_BUTTON_TYPE);
  }
}

package gamePackage.mainPackage;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * This class
 *
 * @author Connor Denman
 */
public class PauseDialog extends Alert
{

  public static final ButtonType RESUME_BUTTON_TYPE = new ButtonType("Resume");
  public static final ButtonType RESTART_BUTTON_TYPE = new ButtonType("Restart");

  public PauseDialog()
  {
    super(AlertType.INFORMATION);
    setTitle("Game Paused");
    setContentText("What do you want to do?");
    getButtonTypes().setAll(RESUME_BUTTON_TYPE, RESTART_BUTTON_TYPE);
  }

//  public final Optional<ButtonType> showAndWait()
//  {
//    Optional<ButtonType> result = super();
//
//    switch (result.get())
//    {
//      case resumeButtonType:
//        close();
//        break;
//      case restartButtonType:
//        close();
//        break;
//    }
//  }
}

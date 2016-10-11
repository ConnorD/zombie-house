package gamePackage.mainPackage.ui;

import gamePackage.common.PlayerData;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

/**
 * This class manages the toolbar showing the status of the game.
 * Player health, stamina, FPS, etc.
 *
 * @author Connor Denman
 */
public class HUD extends HBox
{
  private ProgressBar healthPB;
  private ProgressBar staminaPB;

  public HUD()
  {
    super(4);
    healthPB = new ProgressBar((float) (PlayerData.health / PlayerData.maxHealth));
    healthPB.setStyle("-fx-accent: green;");
    staminaPB = new ProgressBar((float) (PlayerData.stamina / PlayerData.maxStamina));
    staminaPB.setStyle("-fx-accent: green;");
    getChildren().addAll(new Label("Health: "), healthPB, new Label("Stamina: "), staminaPB);
//    getItems().addAll(healthLabel, staminaLabel);
  }

  public void update()
  {
    float healthRatio = (float) (PlayerData.health / PlayerData.maxHealth);
    float staminaRatio = (float) (PlayerData.stamina / PlayerData.maxStamina);

    healthPB.setProgress(healthRatio);
    staminaPB.setProgress(staminaRatio);

//    determine the color of health bar
    if (healthRatio >= 0.7f)
    {
      healthPB.setStyle("-fx-accent: green;");
    } else if ((healthRatio >= 0.33f) && (healthRatio < 0.7))
    {
      healthPB.setStyle("-fx-accent: yellow;");
    } else
    {
      healthPB.setStyle("-fx-accent: red;");
    }

    //    determine the color of stamina bar
    if (staminaRatio >= 0.7f)
    {
      staminaPB.setStyle("-fx-accent: green;");
    } else if ((staminaRatio >= 0.33f) && (staminaRatio < 0.7))
    {
      staminaPB.setStyle("-fx-accent: yellow;");
    } else
    {
      staminaPB.setStyle("-fx-accent: red;");
    }
  }
}

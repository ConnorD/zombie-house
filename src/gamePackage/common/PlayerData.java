package gamePackage.common;

import gamePackage.levelGenerator.player.PastPlayer;

import java.util.LinkedList;

/******************************************************************************
 * Ederin Igharoro
 *
 * Class to hold player positioning/stamina data.
 *
 * Modifications: Added new data for the player such as health, dps
 * and a health bar for the player located in the top of the main screen
 * original author Maxwell Sanchez
 * @author Ederin Igharoro
 *****************************************************************************/
public class PlayerData
{
  public static double xPosition = 1.0;
  public static double yPosition = 1.0;

  public static double stamina = 5.0;
  public static double staminaRegen = 0.20;
  public static double maxStamina = 5.0;

  public static double health = 100.0;
  public static double maxHealth = 100.0;

  public static double dps = 25.0;

  public static double playerSpeed = 2.0;

  public static int playerSightRange = 7;

  public static PastPlayer past = new PastPlayer();

  public static void restart()
  {
    xPosition = 1.0;
    yPosition = 1.0;
    stamina = maxStamina;
    health = maxHealth;
  }
}

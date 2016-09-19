package gamePackage.common;

/******************************************************************************
 * Ederin Igharoro
 *
 * Class to hold player positioning/stamina data.
 *
 * Modifications: Added new data for the player such as health and dps
 * original author Maxwell Sanchez
 * @author Ederin Igharoro
 *****************************************************************************/
public class Player
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
}

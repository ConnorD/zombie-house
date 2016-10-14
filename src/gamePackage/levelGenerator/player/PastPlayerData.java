package gamePackage.levelGenerator.player;

/******************************************************************************
 * Connor Denman
 *
 * Class to past player's state at each time interval.
 *
 * @author Connor Denman
 *****************************************************************************/

public class PastPlayerData
{
  public double xPosition = 1.0;
  public double yPosition = 1.0;

  public double stamina = 5.0;
  public double staminaRegen = 0.20;
  public double maxStamina = 5.0;

  public double health = 100.0;
  public double maxHealth = 100.0;

  public double dps = 25.0;

  public double playerSpeed = 2.0;

  public int playerSightRange = 7;

  public PastPlayerData(double x, double y)
  {
    xPosition = x;
    yPosition = y;
  }
}

package gamePackage.util;

import gamePackage.common.PlayerData;
import gamePackage.common.ZombieData;

/**
 *
 *
 * @author Ederin Igharoro
 * Created by Ultimate Ediri on 9/19/2016.
 */
public class CombatSystem
{
  private boolean playerAlive;
  private boolean pastSelfPresent;

  private StatusBar vitalStatus;

  public boolean isPlayerAlive()
  {
    return playerAlive;
  }

  public boolean isPastSelfPresent()
  {
    return pastSelfPresent;
  }

  public CombatSystem(boolean playerAlive , boolean pastSelfPresent)
  {
    this.playerAlive = playerAlive;
    this.pastSelfPresent = pastSelfPresent;

    PlayerData.health = PlayerData.maxHealth;
  }


  public void zombieAttack()
  {

    if(PlayerData.health > 0.0)
    {
      System.out.print("Player got hit, health -5 ");
      PlayerData.health+= -ZombieData.dps;
      System.out.println("Health Remaining: "+ PlayerData.health);
    }
    else
    {
      playerAlive = false;
    }
  }

  public void playerAttack()
  {
    if(ZombieData.health > 0.0)
    {
      System.out.print("Zombie got hit, health  "+-PlayerData.dps);
      ZombieData.health += -PlayerData.dps;
      System.out.println("Health Remaining: "+ ZombieData.health);

    }
  }
}

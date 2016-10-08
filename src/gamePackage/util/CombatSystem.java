package gamePackage.util;

import gamePackage.audio.AudioFiles;
import gamePackage.audio.DirectionalPlayer;
import gamePackage.common.InputContainer;
import gamePackage.common.LevelVar;
import gamePackage.common.PlayerData;
import gamePackage.common.ZombieData;
import gamePackage.levelGenerator.house.Wall;
import gamePackage.levelGenerator.zombies.Zombie;

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

  public double distance, distanceX, distanceY, totalDistance;

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

  public void setTargetForZombie(Zombie zombie, double percentOfSecond, double playerDirectionVectorX, double playerDirectionVectorY)
  {
    if (!isPastSelfPresent())
    {
      // Move and rotate the zombie. A* doesn't currently work, so this allows zombies to move towards player. Ugly.
      distance = Math.sqrt(Math.abs(zombie.positionX - PlayerData.xPosition) * Math.abs(zombie.positionX - PlayerData.xPosition) +
              Math.abs(zombie.positionY - PlayerData.yPosition) * Math.abs(zombie.positionY - PlayerData.yPosition));
    }

    else if(isPastSelfPresent())
    {

    }


    if (distance < GameData.ZOMBIE_ACTIVATION_DISTANCE)
    {
      // Animate 3D zombie and move it to its parent zombie location
      zombie.zombie3D.nextFrame();
      distanceX = (zombie.positionX - PlayerData.xPosition);
      distanceY = (zombie.positionY - PlayerData.yPosition);
      totalDistance = Math.abs(distanceX) + Math.abs(distanceY);

      // Player collides with zombie, Deduct health
      targetCollision(zombie);
      checkWallCollisionForZombies(zombie, percentOfSecond, playerDirectionVectorX, playerDirectionVectorY);

    }
  }

  /**
   * Rounds the provided number up if decimal component >= 0.5, otherwise down.
   *
   * @param toRound Double to round
   * @return int Rounded number
   */
  private int round(double toRound)
  {
    if (toRound - ((int) toRound) < 0.5)
    {
      return (int) toRound;
    } else
    {
      return (int) toRound + 1;
    }
  }

  /**
   * Calculates the angle between two vectors, useful in directional sound calculation.
   *
   * @param x1 X component of vector 1
   * @param y1 Y component of vector 1
   * @param x2 X component of vector 2
   * @param y2 Y component of vector 2
   * @return double Angle, in degrees, between the provided vectors
   */
  public double angleBetweenVectors(double x1, double y1, double x2, double y2)
  {
    return Math.toDegrees(Math.atan2(x1 * y2 - x2 * y1, x1 * x2 + y1 * y2));
  }


  public void checkWallCollisionForPlayer()
  {

  }

  public void checkWallCollisionForZombies(Zombie zombie , double percentOfSecond, double playerDirectionVectorX, double playerDirectionVectorY)
  {
    double desiredPositionX = zombie.positionX - (distanceX / totalDistance * LevelVar.zombieSpeed * percentOfSecond);
    double desiredPositionY = zombie.positionY - (distanceY / totalDistance * LevelVar.zombieSpeed * percentOfSecond);

    // Check for wall collisions
    if (!(LevelVar.house[round(desiredPositionX + GameData.WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall) &&
            !(LevelVar.house[round(desiredPositionX - GameData.WALL_COLLISION_OFFSET)][round(zombie.positionY)] instanceof Wall))
    {
      zombie.positionX = desiredPositionX;
    }
    if (!(LevelVar.house[round(zombie.positionX)][round(desiredPositionY + GameData.WALL_COLLISION_OFFSET)] instanceof Wall) &&
            !(LevelVar.house[round(zombie.positionX)][round(desiredPositionY - GameData.WALL_COLLISION_OFFSET)] instanceof Wall))
    {
      zombie.positionY = desiredPositionY;
    }

    double zombieVectorX = zombie.positionX - PlayerData.xPosition;
    double zombieVectorY = zombie.positionY - PlayerData.yPosition;

    // Accomodate all four quadrants of the unit circle, rotate to face the user
    if (distanceX < 0)
    {
      if (distanceY < 0)
      {
        double angle = 180 + Math.toDegrees(Math.atan((zombie.positionX - PlayerData.xPosition) / (zombie.positionY - PlayerData.yPosition)));
        zombie.zombie3D.setRotate(angle);
      } else
      {
        double angle = 360 + Math.toDegrees(Math.atan((zombie.positionX - PlayerData.xPosition) / (zombie.positionY - PlayerData.yPosition)));
        zombie.zombie3D.setRotate(angle);
      }
    }

    else if (distanceY < 0)
    {
      double angle = 180 + Math.toDegrees(Math.atan((zombie.positionX - PlayerData.xPosition) / (zombie.positionY - PlayerData.yPosition)));
      zombie.zombie3D.setRotate(angle);

    }

    else
    {
      double angle = Math.toDegrees(Math.atan((zombie.positionX - PlayerData.xPosition) / (zombie.positionY - PlayerData.yPosition)));
      zombie.zombie3D.setRotate(angle);
    }

    if (Math.random() > 0.98)
    {
      DirectionalPlayer.playSound(AudioFiles.randomZombieSound(), angleBetweenVectors(playerDirectionVectorX, playerDirectionVectorY, zombieVectorX, zombieVectorY), distance);
    }



  }

  private void targetCollision(Zombie zombie)
  {
    if (totalDistance < 0.3)
    {
      System.out.println("Im Called");

      //Zombie starts to attack
      zombieAttack();

      //If Player left clicks mouse, Player attacks
      if (InputContainer.useWeapon)
      {
        playerAttack(zombie);
      }
    }
  }


  public void zombieAttack()
  {

    if(PlayerData.health > 0.0)
    {
      System.out.print("Player got hit, health - " + ZombieData.dps);
      PlayerData.health+= -ZombieData.dps;
      System.out.println("Health Remaining: "+ PlayerData.health);
    }
    else
    {
      playerAlive = false;
    }
  }

  public void playerAttack(Zombie zombie)
  {
    if(ZombieData.health > 0.0)
    {
      System.out.print("Zombie got hit, health  "+-PlayerData.dps);
      ZombieData.health += -PlayerData.dps;
      zombie.zombie3D.zombieVitails.decrementHealth("Zombie", PlayerData.dps); //THIS WORKS
      System.out.println("Health Remaining: "+ ZombieData.health);

    }
  }
}
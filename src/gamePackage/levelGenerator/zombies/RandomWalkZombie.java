package gamePackage.levelGenerator.zombies;


import gamePackage.common.LevelVar;
import gamePackage.levelGenerator.house.Tile;

/**
 * RandomWalkZombie class contains the behavior for a
 * RandomWalkZombie
 *
 * @author Stephen Sagartz
 * @since 2016-03-05
 */
public class RandomWalkZombie extends Zombie
{
  /**
   * Creates a RandomWalkZombie that behaves uniquely
   *
   * @param heading   this RandomWalkZombie's heading
   * @param positionX this RandomWalkZombie's positionX
   * @param positionY this RandomWalkZombie's positionY
   * @param curTile   this RandomWalkZombie's curTile
   */
  public RandomWalkZombie(double heading, double positionX, double positionY, Tile curTile, int id, double health, double dps)
  {
    super(heading, positionX, positionY, curTile, id, health, dps);
  }

  /**
   * Updates and sets this Zombie's heading every zombie_Decision_Rate milliseconds
   * and adjusts the behavior according to the ZombieHouse Project specifications.
   */
  @Override
  public void makeDecision()
  {
    if(super.hasLife())
    {
      super.setHealth(100);
      super.setDPS(2.5);
      System.out.println("This zombie ID is " + zombieID);

      if (super.scentDetection(super.getZombieSmell(), LevelVar.house) || this.getSmell())
      {
        super.setSmell(true);
        super.calcPath(LevelVar.house);
      } else
      {
        super.setSmell(false);
        if (super.getCollide())
        {
          double curHeading = super.getHeading();
          super.setHeading((180 + curHeading) % 360);
          super.setCollided(false);
        } else
        {
          super.setHeading(Math.random() * 360);
          super.setCollided(false);
        }
      }
    }

  }
}
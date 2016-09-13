package gamePackage.levelGenerator.zombies;

import gamePackage.common.LevelVar;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class updates zombie locations periodically. Run every two seconds in code.
 *
 * @author All
 */
public class ZTimer
{
  public ZUpdate myUpdate = new ZUpdate();
  public Timer zUpdateTimer = new Timer();

  class ZUpdate extends TimerTask
  {
    @Override
    public void run()
    {
      try
      {
        if (LevelVar.zombieCollection != null)
          for (Zombie z : LevelVar.zombieCollection)
          {
            if (z != null)
              z.makeDecision();
          }
      } catch (Exception e)
      {
      }
    }
  }
}

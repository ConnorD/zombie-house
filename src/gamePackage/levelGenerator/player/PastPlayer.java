package gamePackage.levelGenerator.player;

import gamePackage.common.PlayerData;
import javafx.scene.Group;
import javafx.scene.shape.Box;

import java.util.LinkedList;

/******************************************************************************
 * Connor Denman
 *
 * Class to past player's state at each time interval.
 *
 * @author Connor Denman
 *****************************************************************************/

public class PastPlayer extends Box
{
  public static LinkedList<PlayerData> states = new LinkedList<>();
}

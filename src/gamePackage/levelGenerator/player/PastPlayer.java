package gamePackage.levelGenerator.player;

import gamePackage.common.PlayerData;
import gamePackage.util.GameData;
import javafx.scene.Group;
import javafx.scene.shape.Box;

import java.util.LinkedList;

public class PastPlayer extends Box
{
  public static LinkedList<PastPlayerData> states = new LinkedList<>();
  private int currentIndex;

  public PastPlayer()
  {
//    super(GameData.WALL_HEIGHT/2, GameData.WALL_HEIGHT/2, GameData.WALL_HEIGHT);
    super(GameData.TILE_WIDTH_AND_HEIGHT, GameData.WALL_HEIGHT, GameData.TILE_WIDTH_AND_HEIGHT);
    currentIndex = 0;
//    setMaterial(G);
  }

  public void recordPlayerState()
  {
    PastPlayerData currentState = new PastPlayerData(PlayerData.xPosition, PlayerData.yPosition);

    states.add(currentState);
  }

  public PastPlayerData nextState()
  {
    PastPlayerData currentPastPlayerData = states.get(currentIndex);
    currentIndex++;

    return currentPastPlayerData;
  }
}

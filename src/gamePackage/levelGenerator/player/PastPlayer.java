package gamePackage.levelGenerator.player;

import gamePackage.common.PlayerData;
import gamePackage.util.CombatSystem;
import gamePackage.util.GameData;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.LinkedList;

/**
 * Manages the bifurcated form of the player.
 *
 * @author Connor Denman
 */

public class PastPlayer extends Box
{
  public static LinkedList<PastPlayerData> states = new LinkedList<>();

//  when the game is played initially, past player is in recording mode
//  when the player is restarting, not in recording mode

  /**
   * Constructor for PastPlayer that sets the dimensions, rotation axis, and material of the 3D object.
   */
  public PastPlayer()
  {
//    super(GameData.WALL_HEIGHT/2, GameData.WALL_HEIGHT/2, GameData.WALL_HEIGHT);
    super(GameData.TILE_WIDTH_AND_HEIGHT, GameData.WALL_HEIGHT, GameData.TILE_WIDTH_AND_HEIGHT);
    setRotationAxis(Rotate.Y_AXIS);
    setMaterial(new PhongMaterial(Color.WHITE));
  }

//  public void update(double cameraRotation)
//  {
//    if ()
//  }

  /**
   * Add a "snapshot" of the current player's state into the linked list for use later.
   *
   * @param cameraRotation - the double value used for rotating the camera.
   */
  public void recordPlayerState(double cameraRotation)
  {
    PastPlayerData currentState = new PastPlayerData(PlayerData.xPosition, PlayerData.yPosition, cameraRotation, PlayerData.health);

    states.add(currentState);
  }

  /**
   * Step to the next saved state of the past player. Remove elements from the linked list of states as we go.
   *
   * @return the next past player state in the linked list.
   */
  public PastPlayerData nextState()
  {
    PastPlayerData nextPastState = states.remove();

    setTranslateX(nextPastState.xPosition * GameData.TILE_WIDTH_AND_HEIGHT);
    setTranslateZ(nextPastState.yPosition * GameData.TILE_WIDTH_AND_HEIGHT);
    setRotate(nextPastState.yRotation);

    return nextPastState;
  }
}

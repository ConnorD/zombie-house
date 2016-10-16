package gamePackage.mainPackage;

import gamePackage.audio.AudioFiles;
import gamePackage.common.InputContainer;
import gamePackage.common.LevelVar;
import gamePackage.common.PlayerData;
import gamePackage.levelGenerator.house.Exit;
import gamePackage.levelGenerator.house.Tile;
import gamePackage.levelGenerator.player.PastPlayer;
import gamePackage.levelGenerator.player.PastPlayerData;
import gamePackage.levelGenerator.zombies.Zombie;
import gamePackage.mainPackage.MainApplication;
import gamePackage.mainPackage.Zombie3D;
import gamePackage.mainPackage.ui.GameOverDialog;
import gamePackage.util.CombatSystem;
import gamePackage.util.GameData;
import javafx.animation.AnimationTimer;
import javafx.scene.control.ButtonType;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

import java.util.Optional;

/**
 * This handles the primary game animation frame timing. and uses the
 * Main App to handle the scene / camera changes
 *
 * @author Ederin Igharoro
 * Created by Ultimate Ediri on 10/12/2016.
 */
public class GameEngine extends AnimationTimer
{


  private MainApplication main;
  private CombatSystem combatSystem;

  public GameEngine(MainApplication main, CombatSystem combatSystem)
  {
    this.main = main;
    this.combatSystem = combatSystem;
  }

  /**
   * Moves the player, if possible (no wall collisions) in the direction(s) requested by the user
   * with keyboard input, given the current angle determined by previous mouse input.
   */
  public void movePlayerIfRequested(double percentOfSecond)
  {
    double desiredZDisplacement = 0;

    // Calculate information for horizontal and vertical player movement based on direction
    double cos = Math.cos(main.cameraYRotation / 180.0 * 3.1415);
    double sin = Math.sin(main.cameraYRotation / 180.0 * 3.1415);

    // Include all user input (including those which cancel out) to determine z offset
    desiredZDisplacement += (InputContainer.forward) ? (cos) : 0;
    desiredZDisplacement -= (InputContainer.backward) ? (cos) : 0;
    desiredZDisplacement += (InputContainer.left) ? (sin) : 0;
    desiredZDisplacement -= (InputContainer.right) ? (sin) : 0;

    // Include all user input (including those which cancel out) to determine x offset
    double desiredXDisplacement = 0;
    desiredXDisplacement += (InputContainer.forward) ? (sin) : 0;
    desiredXDisplacement -= (InputContainer.backward) ? (sin) : 0;
    desiredXDisplacement -= (InputContainer.left) ? (cos) : 0;
    desiredXDisplacement += (InputContainer.right) ? (cos) : 0;

    // Prevent diagonal move speed-boost
    double displacementMagnitude = Math.abs(desiredZDisplacement) + Math.abs(desiredXDisplacement);
    double displacementScaleFactor = 1 / displacementMagnitude;

    boolean isRunning = false;

    if (Double.isInfinite(displacementScaleFactor)) displacementScaleFactor = 1;
    if (InputContainer.run && PlayerData.stamina > 0)
    {
      displacementScaleFactor *= 2;
      PlayerData.stamina -= 1.0 / GameData.TARGET_FRAMES_PER_SECOND;
      isRunning = true;
    }

    // PlayerData out of stamina
    else if (PlayerData.stamina <= 0)
    {
      InputContainer.run = false;
    }

    // PlayerData is not *trying* to run, so allow stamina regeneration
    if (!InputContainer.run)
    {
      PlayerData.stamina += PlayerData.staminaRegen / GameData.TARGET_FRAMES_PER_SECOND;
      if (PlayerData.stamina > PlayerData.maxStamina) PlayerData.stamina = PlayerData.maxStamina;
    }

    // How often to play the stepping noise (walking vs running)
    int stepFrequency = isRunning ? 20 : 40;

    // Play walking noises if player is moving
    if (desiredXDisplacement != 0 || desiredZDisplacement != 0)
    {
      if (frame % stepFrequency == 0)
      {
        // Alternate step clips
        if (lastClip == 2)
        {
          AudioFiles.userStep1.setVolume(isRunning ? 0.4 : 0.25);
          AudioFiles.userStep1.play();
          lastClip = 1;
        } else if (lastClip == 1)
        {
          AudioFiles.userStep2.setVolume(isRunning ? 0.4 : 0.25);
          AudioFiles.userStep2.play();
          lastClip = 2;
        }
      }
    }
    desiredXDisplacement *= displacementScaleFactor;
    desiredZDisplacement *= displacementScaleFactor;

    // If possible, the position the player indicated they wanted to move to
    double desiredPlayerXPosition = PlayerData.xPosition + (desiredXDisplacement * (percentOfSecond * PlayerData.playerSpeed));
    double desiredPlayerYPosition = PlayerData.yPosition + (desiredZDisplacement * (percentOfSecond * PlayerData.playerSpeed));

    // PlayerData reached the exit
    if (LevelVar.house[(int) desiredPlayerXPosition][(int) desiredPlayerYPosition] instanceof Exit)
    {
      System.out.println("next level...");
      main.level.nextLevel();
      main.stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
      main.rebuildLevel();
    }

    // "Unstick" player
    while (!(LevelVar.house[round(PlayerData.xPosition)][round(PlayerData.yPosition)] instanceof Tile))
    {
      if (PlayerData.xPosition < 5)
      {
        PlayerData.xPosition += 1;
      } else
      {
        PlayerData.xPosition -= 1;
      }
    }

    // Check for wall collisions for player
    combatSystem.checkWallCollisionForPlayer(desiredPlayerXPosition, desiredPlayerYPosition, desiredXDisplacement, desiredZDisplacement, percentOfSecond);


    // Calculate camera displacement
    main.cameraXDisplacement = PlayerData.xPosition * GameData.TILE_WIDTH_AND_HEIGHT;
    main.cameraZDisplacement = PlayerData.yPosition * GameData.TILE_WIDTH_AND_HEIGHT;

    // Move the point light with the light
    main.light.setTranslateX(main.cameraXDisplacement);
    main.light.setTranslateZ(main.cameraZDisplacement);

    // Calculate camera rotation
    main.cameraYRotation += GameData.PLAYER_TURN_SMOOTHING * InputContainer.remainingCameraPan;

    // Displace camera
    main.camera.setTranslateX(main.cameraXDisplacement);
    main.camera.setTranslateZ(main.cameraZDisplacement);

    // Rotate the camera
    main.camera.setRotate(main.cameraYRotation);

    // Used for movement and swivel smoothing
    InputContainer.remainingCameraPan -= GameData.PLAYER_TURN_SMOOTHING * InputContainer.remainingCameraPan;
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

  // Used for timing events that don't happen every frame
  int frame = 0;

  // The last-used user walking clip
  int lastClip = 1;
  long lastFrame = System.nanoTime();


  /**
   * Called for every frame of the game. Moves the player, nearby zombies, and determiens win/loss conditions.
   */
  @Override
  public void handle(long time)
  {

    if (frame == 0) lastFrame = time;
    frame++;
    double percentOfSecond = ((double) time - (double) lastFrame) / 2000000000;
    movePlayerIfRequested(percentOfSecond);

    double playerDirectionVectorX = Math.toDegrees(Math.cos(main.cameraYRotation));
    double playerDirectionVectorY = Math.toDegrees(Math.sin(main.cameraYRotation));

    // Animate zombies every four frames to reduce computational load
    if (frame % 4 == 0)
    {
      for (Zombie zombie : LevelVar.zombieCollection)
      {
        Zombie3D zombie3D = zombie.zombie3D;
        zombie3D.setTranslateX(zombie.positionX * GameData.TILE_WIDTH_AND_HEIGHT);
        zombie3D.setTranslateZ(zombie.positionY * GameData.TILE_WIDTH_AND_HEIGHT);

        combatSystem.setTargetForZombie(zombie, percentOfSecond, playerDirectionVectorX, playerDirectionVectorY);

        //Checking if Player gets Killed
        if(!combatSystem.isPlayerAlive())
        {
          GameOverDialog gameOverAlert = new GameOverDialog();
          Optional<ButtonType> chosenOption = gameOverAlert.showAndWait();

          if (chosenOption.isPresent())
          {
            System.out.println("Restarting due to death!!");
            main.level.restartLevel();
            main.rebuildLevel();
          }
        }

        //Checking if a Zombie gets killed
        if(!zombie.hasLife())
        {
          main.sceneRoot.getChildren().remove(zombie.zombie3D); //First, Remove the physical zombie obj
          LevelVar.zombieCollection.remove(zombie); //Now remove the instance of the obj (that certain zombie)
          break; // Get out of the current loop and restart
        }
      }

      lastFrame = time;
    }

    // Rebuild level if requested. Done here to occur on graphics thread to avoid concurrent modification exceptions.
    if (main.shouldRebuildLevel)
    {

      for (int i = 0; i < main.sceneRoot.getChildren().size(); i++)
      {
        if (main.sceneRoot.getChildren().get(i) instanceof Box || main.sceneRoot.getChildren().get(i) instanceof Zombie3D)
        {
          main.sceneRoot.getChildren().remove(main.sceneRoot.getChildren().get(i));
          i--;
        }
      }
      main.setupLevel();
      main.shouldRebuildLevel = false;
    }

    PlayerData.past.recordPlayerState(main.cameraYRotation);
    if (frame > 500)
    {
//      move past player
      PlayerData.past.nextState();
    }

//    PlayerData.past.recordPlayerState();
//      update the HUD
    main.dataHUD.update();
  }
}

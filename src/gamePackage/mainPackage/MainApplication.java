package gamePackage.mainPackage;

import gamePackage.audio.AudioFiles;
import gamePackage.audio.DirectionalPlayer;
import gamePackage.common.*;
import gamePackage.levelGenerator.house.Exit;
import gamePackage.levelGenerator.house.Level;
import gamePackage.levelGenerator.house.Tile;
import gamePackage.levelGenerator.house.Wall;
import gamePackage.levelGenerator.zombies.ZTimer;
import gamePackage.levelGenerator.zombies.Zombie;
import gamePackage.util.CombatSystem;
import gamePackage.util.GameData;
import gamePackage.util.StatusBar;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.*;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Optional;


/**
 * This class manages all 3D rendering, sets up key listeners,
 * and responds to key events.
 * <p>
 * WASD used for traditional movement, mouse swivels the camera.
 * <p>
 * PlayerData cannot move through walls, and zombie collisions trigger
 * a level reset.
 *
 * @author Connor Denman & Ederin Igharoro
 */
public class MainApplication extends Application
{

  private double cameraXDisplacement = 0;
  private double cameraYDisplacement = -375;
  private double cameraZDisplacement = 0;

  private double cameraYRotation = 0;



  private Level level;
  private Stage stage;
  private GameLoop mainGameLoop;

  private PointLight pl;
  private PerspectiveCamera camera;
  private Group sceneRoot;

  private StatusBar playerVitals;
  private CombatSystem combatSystem;

  private boolean isRunning = false;

  /**
   * Create a robot to reset the mouse to the middle of the screen.
   */
  private Robot robot;

  {
    try
    {
      robot = new Robot();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }



  /**
   * Called on initial application startup. Setup the camera, point light,
   * scene, key listeners, and materials, as well as starting the
   * primary game loop.
   *
   * @param stage The stage to set up the 3D graphics on
   */
  @Override
  public void start(Stage stage) throws Exception
  {
    stage.setOnCloseRequest(event -> System.exit(0));
    this.stage = stage;

    // Create group to hold 3D objects
    sceneRoot = new Group();
    Scene scene = new Scene(sceneRoot, GameData.WINDOW_WIDTH, GameData.WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
    scene.setFill(Color.BLACK);

    Label fpsLabel = new Label("FPS");
//    sceneRoot.getChildren().add(fpsLabel);

    // Hide the cursor
    scene.setCursor(Cursor.NONE);

    // Spawn the first level
    LevelVar.zombie3D = true;
    level = new Level();
    level.nextLevel();
    level.fullGen();

    // Create a "lantern" for the user
    pl = new PointLight(Color.WHITE);
    pl.setDepthTest(DepthTest.ENABLE);
    pl.setTranslateY(cameraYDisplacement);

    sceneRoot.getChildren().add(pl);

    // Create the camera, set it to view far enough for any reasonably-sized map
    camera = new PerspectiveCamera(true);
    camera.setNearClip(0.1);
    camera.setFarClip(20000.0);
    camera.setFieldOfView(62.5);

    // Rotate camera on the y-axis for swivel in response to mouse
    camera.setVerticalFieldOfView(true);
    camera.setTranslateZ(cameraZDisplacement);
    camera.setTranslateY(cameraYDisplacement);
    camera.setRotationAxis(Rotate.Y_AXIS);
    camera.setDepthTest(DepthTest.ENABLE);
    scene.setCamera(camera);

    //Setting up player health and stamina bar
    playerVitals = new StatusBar(PlayerData.health, PlayerData.stamina, PlayerData.xPosition,PlayerData.yPosition, 10, 0, GameData.WINDOW_WIDTH, GameData.WINDOW_HEIGHT);
    sceneRoot.getChildren().add(playerVitals);

    // Set up key listeners for WASD (movement), F1/F2 (full screen toggle), Shift (run), Escape (exit), F3 (cheat)
    scene.setOnKeyPressed(event ->
    {
      KeyCode keycode = event.getCode();
      if (keycode == KeyCode.W)
      {
        InputContainer.forward = true;
      }

      else if (keycode == KeyCode.S)
      {
        InputContainer.backward = true;
      }

      else if (keycode == KeyCode.A)
      {
        InputContainer.left = true;
      }

      else if (keycode == KeyCode.D)
      {
        InputContainer.right = true;
      }

      else if (keycode == KeyCode.F1)
      {
        stage.setFullScreen(true);
      }

      else if (keycode == KeyCode.F2)
      {
        stage.setFullScreen(false);
      }

      else if (keycode == KeyCode.SHIFT)
      {
        InputContainer.run = true;
      }

      else if (keycode == KeyCode.ESCAPE)
      {
        if (isRunning == true)
        {
          isRunning = false;
          mainGameLoop.stop();

          PauseDialog pd = new PauseDialog();
          Optional<ButtonType> chosenOption = pd.showAndWait();

          if (chosenOption.isPresent())
          {
            if (chosenOption.get() == PauseDialog.RESUME_BUTTON_TYPE)
            {
              mainGameLoop.start();
              isRunning = true;
            }

            else
            {
              mainGameLoop.stop();
              isRunning = false;
              rebuildLevel();
            }
          }
        }

        else
        {
          isRunning = true;
          mainGameLoop.start();
        }
      }

      else if (keycode == KeyCode.F3) /* Cheat key to advance levels */
      {
        level.nextLevel();
        rebuildLevel();
        stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
      }
    });

    scene.setOnKeyReleased(event ->
    {
      KeyCode keycode = event.getCode();
      if (keycode == KeyCode.W)
      {
        InputContainer.forward = false;
      } else if (keycode == KeyCode.S)
      {
        InputContainer.backward = false;
      } else if (keycode == KeyCode.A)
      {
        InputContainer.left = false;
      } else if (keycode == KeyCode.D)
      {
        InputContainer.right = false;
      } else if (keycode == KeyCode.SHIFT)
      {
        InputContainer.run = false;
      }
    });

    // Add mouse listener
    scene.addEventHandler(MouseEvent.MOUSE_MOVED, event ->
    {
      double rotateAmountY = event.getScreenX() - InputContainer.lastMouseX;
      rotateAmountY *= GameData.PLAYER_TURN_SPEED;

      // Smooth inertia swivel
      InputContainer.remainingCameraPan += rotateAmountY;

      try
      {
        double topX = event.getScreenX() - event.getSceneX();
        double topY = event.getScreenY() - event.getSceneY();

        // Reset mouse to middle of screen
        robot.mouseMove((int) topX + (int) scene.getWidth() / 2, (int) topY + (int) scene.getHeight() / 2);

        InputContainer.lastMouseX = topX + scene.getWidth() / 2;
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    });

    //Add Mouse Pressed and Released for attack system
    scene.setOnMousePressed(event ->
  {
    MouseButton mouse = event.getButton();

    if(mouse == MouseButton.PRIMARY) // if Left Click for player attack
    {
      InputContainer.useWeapon = true;
      System.out.println("Mouse Pressed, use Weapon = "+ InputContainer.useWeapon);
    }
  });

    scene.setOnMouseReleased(event ->
    {
      MouseButton mouse = event.getButton();

      if(mouse == MouseButton.PRIMARY) // if Left Click for player attack
      {
        InputContainer.useWeapon = false;
        System.out.println("Mouse Released, use Weapon = "+ InputContainer.useWeapon);
      }
    });

    stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
    stage.setScene(scene);
    stage.show();
    stage.toFront();

    // Load textures from files to use for floor, walls, and ceiling
    GameData.floorMaterial1.setDiffuseColor(Color.WHITE);
    GameData.floorMaterial1.setSpecularColor(Color.WHITE.darker());
    GameData.floorMaterial1.setSpecularPower(128);
    GameData.floorMaterial1.setDiffuseMap(new Image(getClass().getResource("/resources/floor1.png").toExternalForm()));

    GameData.floorMaterial2.setDiffuseColor(Color.WHITE);
    GameData.floorMaterial2.setSpecularColor(Color.WHITE.darker());
    GameData.floorMaterial2.setSpecularPower(128);
    GameData.floorMaterial2.setDiffuseMap(new Image(getClass().getResource("/resources/floor2.png").toExternalForm()));

    GameData.floorMaterial3.setDiffuseColor(Color.WHITE);
    GameData.floorMaterial3.setSpecularColor(Color.WHITE.darker());
    GameData.floorMaterial3.setSpecularPower(128);
    GameData.floorMaterial3.setDiffuseMap(new Image(getClass().getResource("/resources/floor3.png").toExternalForm()));

    GameData.floorMaterial4.setDiffuseColor(Color.WHITE);
    GameData.floorMaterial4.setSpecularColor(Color.WHITE.darker());
    GameData.floorMaterial4.setSpecularPower(128);
    GameData.floorMaterial4.setDiffuseMap(new Image(getClass().getResource("/resources/floor0.png").toExternalForm()));


    GameData.ceilingMaterial.setDiffuseColor(Color.WHITE);
    GameData.ceilingMaterial.setSpecularColor(Color.BLACK.darker().darker().darker().darker());
    GameData.ceilingMaterial.setSpecularPower(25);
    GameData.ceilingMaterial.setDiffuseMap(new Image(getClass().getResource("/resources/floor3.png").toExternalForm()));

    GameData.wallMaterial.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0));
    GameData.wallMaterial.setSpecularColor(Color.BLACK);
    GameData.wallMaterial.setSpecularPower(256);
    GameData.wallMaterial.setDiffuseMap(new Image(getClass().getResource("/resources/wall.png").toExternalForm()));

    GameData.exitMaterial.setDiffuseColor(Color.WHITE);
    GameData.exitMaterial.setSpecularColor(Color.WHITE.darker());
    GameData.exitMaterial.setSpecularPower(128);
    //GameData.exitMaterial.setDiffuseMap(new Image(getClass().getResource("/resources/exitDoor.png").toExternalForm()));

    setupLevel();

    mainGameLoop = new GameLoop();
  }

  // Stores requests to rebuild the level graphically, so that rebuilding is done in a thread-safe manner
  boolean shouldRebuildLevel = false;

  /**
   * Informs the program that a level rebuild has been requested.
   */
  public void rebuildLevel()
  {
    shouldRebuildLevel = true;
  }

  /**
   * Sets up the 3D objects to represent a 2D Tile[][] house in a 3D world.
   */
  public void setupLevel()
  {
    combatSystem = new CombatSystem(true, false);

    Tile[][] house = LevelVar.house;
    // Loop through all tiles
    for (int x = 0; x < house.length; x++)
    {
      for (int z = 0; z < house[0].length; z++)
      {
        // Always have a floor and ceiling
        Box floor = new Box(GameData.TILE_WIDTH_AND_HEIGHT, 10, GameData.TILE_WIDTH_AND_HEIGHT);
        if (house[x][z].zone == 0)
        {
          floor.setMaterial(GameData.floorMaterial1);
        }
        if (house[x][z].zone == 1)
        {
          floor.setMaterial(GameData.floorMaterial2);
        }
        if (house[x][z].zone == 2)
        {
          floor.setMaterial(GameData.floorMaterial3);
        } else
        {
          floor.setMaterial(GameData.floorMaterial4);
        }

        floor.setTranslateY(GameData.FLOOR_Y_DISPLACEMENT);
        floor.setTranslateX(x * GameData.TILE_WIDTH_AND_HEIGHT);
        floor.setTranslateZ(z * GameData.TILE_WIDTH_AND_HEIGHT);
        sceneRoot.getChildren().add(floor);

        Box ceiling = new Box(GameData.TILE_WIDTH_AND_HEIGHT, 10, GameData.TILE_WIDTH_AND_HEIGHT);
        ceiling.setMaterial(GameData.ceilingMaterial);
        ceiling.setTranslateY(GameData.CEILING_Y_DISPLACEMENT);
        ceiling.setTranslateX(x * GameData.TILE_WIDTH_AND_HEIGHT);
        ceiling.setTranslateZ(z * GameData.TILE_WIDTH_AND_HEIGHT);
        sceneRoot.getChildren().add(ceiling);

        // If wall, place a ground-to-ceiling wall box
        if (house[x][z] instanceof Wall)
        {
          Box wall = new Box(GameData.TILE_WIDTH_AND_HEIGHT, GameData.WALL_HEIGHT, GameData.TILE_WIDTH_AND_HEIGHT);
          wall.setMaterial(GameData.wallMaterial);
          wall.setTranslateY(-GameData.WALL_HEIGHT / 2);
          wall.setTranslateX(x * GameData.TILE_WIDTH_AND_HEIGHT);
          wall.setTranslateZ(z * GameData.TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(wall);
        }

        // If exit, place a ground-to-ceiling exit box
        else if (house[x][z] instanceof Exit)
        {
          Box exit = new Box(GameData.TILE_WIDTH_AND_HEIGHT, GameData.WALL_HEIGHT, GameData.TILE_WIDTH_AND_HEIGHT);
          exit.setMaterial(GameData.exitMaterial);
          exit.setTranslateY(-GameData.WALL_HEIGHT / 2);
          exit.setTranslateX(x * GameData.TILE_WIDTH_AND_HEIGHT);
          exit.setTranslateZ(z * GameData.TILE_WIDTH_AND_HEIGHT);
          sceneRoot.getChildren().add(exit);
        }
      }
    }

    // Add all of the 3D zombie objects
    for (Zombie zombie : LevelVar.zombieCollection)
    {
      sceneRoot.getChildren().add(zombie.zombie3D);
    }


    // Create a zombie update timer
    ZTimer zMoves = new ZTimer();
    zMoves.zUpdateTimer.schedule(zMoves.myUpdate, Zombie.getDecisionRate(), Zombie.getDecisionRate());
  }

  /**
   * @author Maxwell Sanchez
   *         <p>
   *         GameLoop handles the primary game animation frame timing.
   */
  class GameLoop extends AnimationTimer
  {

    /**
     * Moves the player, if possible (no wall collisions) in the direction(s) requested by the user
     * with keyboard input, given the current angle determined by previous mouse input.
     */
    public void movePlayerIfRequested(double percentOfSecond)
    {
      double desiredZDisplacement = 0;

      // Calculate information for horizontal and vertical player movement based on direction
      double cos = Math.cos(cameraYRotation / 180.0 * 3.1415);
      double sin = Math.sin(cameraYRotation / 180.0 * 3.1415);

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
        level.nextLevel();
        stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
        rebuildLevel();
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

      // Check for wall collisions
      if (!(LevelVar.house[round(desiredPlayerXPosition + GameData.WALL_COLLISION_OFFSET)][round(PlayerData.yPosition)] instanceof Wall) &&
              !(LevelVar.house[round(desiredPlayerXPosition - GameData.WALL_COLLISION_OFFSET)][round(PlayerData.yPosition)] instanceof Wall))
      {
        PlayerData.xPosition += desiredXDisplacement * (percentOfSecond * PlayerData.playerSpeed);
      }
      if (!(LevelVar.house[round(PlayerData.xPosition)][round(desiredPlayerYPosition + GameData.WALL_COLLISION_OFFSET)] instanceof Wall) &&
              !(LevelVar.house[round(PlayerData.xPosition)][round(desiredPlayerYPosition - GameData.WALL_COLLISION_OFFSET)] instanceof Wall))
      {
        PlayerData.yPosition += desiredZDisplacement * (percentOfSecond * PlayerData.playerSpeed);
      }

      // Calculate camera displacement
      cameraXDisplacement = PlayerData.xPosition * GameData.TILE_WIDTH_AND_HEIGHT;
      cameraZDisplacement = PlayerData.yPosition * GameData.TILE_WIDTH_AND_HEIGHT;

      // Move the point light with the light
      pl.setTranslateX(cameraXDisplacement);
      pl.setTranslateZ(cameraZDisplacement);

      // Calculate camera rotation
      cameraYRotation += GameData.PLAYER_TURN_SMOOTHING * InputContainer.remainingCameraPan;

      // Displace camera
      camera.setTranslateX(cameraXDisplacement);
      camera.setTranslateZ(cameraZDisplacement);

      // Rotate the camera
      camera.setRotate(cameraYRotation);

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

      double playerDirectionVectorX = Math.toDegrees(Math.cos(cameraYRotation));
      double playerDirectionVectorY = Math.toDegrees(Math.sin(cameraYRotation));

      // Animate zombies every four frames to reduce computational load
      if (frame % 4 == 0)
      {
        for (Zombie zombie : LevelVar.zombieCollection)
        {
          Zombie3D zombie3D = zombie.zombie3D;
          zombie3D.setTranslateX(zombie.positionX * GameData.TILE_WIDTH_AND_HEIGHT);
          zombie3D.setTranslateZ(zombie.positionY * GameData.TILE_WIDTH_AND_HEIGHT);

          combatSystem.setTargetForZombie(zombie, percentOfSecond, playerDirectionVectorX, playerDirectionVectorY);

          if(!combatSystem.isPlayerAlive())
          {
            System.out.println("Restarting due to death!!");
            level.restartLevel();
            rebuildLevel();
          }
        }

        lastFrame = time;
      }

      // Rebuild level if requested. Done here to occur on graphics thread to avoid concurrent modification exceptions.
      if (shouldRebuildLevel)
      {

        for (int i = 0; i < sceneRoot.getChildren().size(); i++)
        {
          if (sceneRoot.getChildren().get(i) instanceof Box || sceneRoot.getChildren().get(i) instanceof Zombie3D)
          {
            sceneRoot.getChildren().remove(sceneRoot.getChildren().get(i));
            i--;
          }
        }
        setupLevel();
        shouldRebuildLevel = false;
      }
    }

  }

  /**
   * Main kept for legacy applications.
   *
   * @param args Unused command-line arguments
   */
  public static void main(String[] args)
  {
    launch(args);

//    LauncherImpl.launchApplication(this, GamePreloader.class, args);
  }
}

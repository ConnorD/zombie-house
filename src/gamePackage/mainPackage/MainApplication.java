package gamePackage.mainPackage;

import gamePackage.audio.AudioFiles;
import gamePackage.common.*;
import gamePackage.levelGenerator.house.Exit;
import gamePackage.levelGenerator.house.Level;
import gamePackage.levelGenerator.house.Tile;
import gamePackage.levelGenerator.house.Wall;
import gamePackage.levelGenerator.zombies.ZTimer;
import gamePackage.levelGenerator.zombies.Zombie;
import gamePackage.mainPackage.ui.GameOverDialog;
import gamePackage.mainPackage.ui.HUD;
import gamePackage.mainPackage.ui.PauseDialog;
import gamePackage.mainPackage.ui.StartDialog;
import gamePackage.util.CombatSystem;
import gamePackage.util.GameData;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
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

  public double cameraXDisplacement = 0;
  public double cameraYDisplacement = -375;
  public double cameraZDisplacement = 0;

  public double cameraYRotation = 0;

  public Level level;
  public Stage stage;
  private GameEngine gameEngine;

  public static PointLight light;
  public PerspectiveCamera camera;
  public Group sceneRoot;

  public HUD dataHUD;

  public CombatSystem combatSystem;

  public boolean isRunning = false;

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

    SubScene scene = new SubScene(sceneRoot, GameData.WINDOW_WIDTH, GameData.WINDOW_HEIGHT, true, SceneAntialiasing.BALANCED);
    scene.setFill(Color.BLACK);


    // 2D
    BorderPane pane = new BorderPane();
    pane.setCenter(scene);
    Scene fullScene = new Scene(pane);

    dataHUD = new HUD();
    pane.setTop(dataHUD);
    pane.setPrefSize(300,300);

    // Spawn the first level
    LevelVar.zombie3D = true;
    level = new Level();
    level.nextLevel();
    level.fullGen();

    // Set up key listeners for WASD (movement), F1/F2 (full screen toggle), Shift (run), Escape (exit), F3 (cheat)
    fullScene.setOnKeyPressed(event ->
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
        InputContainer.run = InputContainer.isMoving();
      }

      else if (keycode == KeyCode.ESCAPE)
      {
        if (isRunning == true)
        {
          isRunning = false;
          gameEngine.stop();

          PauseDialog pd = new PauseDialog();
          Optional<ButtonType> chosenOption = pd.showAndWait();

          if (chosenOption.isPresent())
          {
            if (chosenOption.get() == PauseDialog.RESUME_BUTTON_TYPE)
            {
//              user wants to resume
              gameEngine.start();
              isRunning = true;
            }
            else
            {
//              user wants to restart
              gameEngine.stop();
              isRunning = false;
              level.restartLevel();
              rebuildLevel();
            }
          }
        }
        else
        {
          isRunning = true;
          gameEngine.start();
        }
      }
      else if (keycode == KeyCode.F3) /* Cheat key to advance levels */
      {
        level.nextLevel();
        rebuildLevel();
        stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
      }
    });

    fullScene.setOnKeyReleased(event ->
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

      InputContainer.run = InputContainer.run && InputContainer.isMoving();
    });

    // Add mouse listener
    fullScene.addEventHandler(MouseEvent.MOUSE_MOVED, event ->
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
    fullScene.setOnMousePressed(event ->
    {
      MouseButton mouse = event.getButton();
      AudioFiles.userSwing.play();

      if(mouse == MouseButton.PRIMARY) // if Left Click for player attack
      {
        InputContainer.useWeapon = true;
        System.out.println("Mouse Pressed, use Weapon = "+ InputContainer.useWeapon);
      }
    });

    fullScene.setOnMouseReleased(event ->
    {
      MouseButton mouse = event.getButton();

      if(mouse == MouseButton.PRIMARY) // if Left Click for player attack
      {
        InputContainer.useWeapon = false;
        System.out.println("Mouse Released, use Weapon = "+ InputContainer.useWeapon);
      }
    });

    stage.setTitle("Zombie House: Level " + (LevelVar.levelNum + 1));
    stage.setScene(fullScene);
    stage.show();
    stage.toFront();

    // Load textures from files to use for floor, walls, and ceiling
    GameData.floorMaterial1.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0).darker());
    GameData.floorMaterial1.setSpecularColor(Color.BLACK);
    GameData.floorMaterial1.setSpecularPower(128);
    GameData.floorMaterial1.setDiffuseMap(new Image(getClass().getResource("/resources/floor1.png").toExternalForm()));

    GameData.floorMaterial2.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0).darker());
    GameData.floorMaterial2.setSpecularColor(Color.BLACK);
    GameData.floorMaterial2.setSpecularPower(128);
    GameData.floorMaterial2.setDiffuseMap(new Image(getClass().getResource("/resources/floor2.png").toExternalForm()));

    GameData.floorMaterial3.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0).darker());
    GameData.floorMaterial3.setSpecularColor(Color.BLACK);
    GameData.floorMaterial3.setSpecularPower(128);
    GameData.floorMaterial3.setDiffuseMap(new Image(getClass().getResource("/resources/floor3.png").toExternalForm()));

    GameData.floorMaterial4.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0).darker());
    GameData.floorMaterial4.setSpecularColor(Color.BLACK);
    GameData.floorMaterial4.setSpecularPower(128);
    GameData.floorMaterial4.setDiffuseMap(new Image(getClass().getResource("/resources/floor0.png").toExternalForm()));


    GameData.ceilingMaterial.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0).darker());
    GameData.ceilingMaterial.setSpecularColor(Color.BLACK);
    GameData.ceilingMaterial.setSpecularPower(128);
    GameData.ceilingMaterial.setDiffuseMap(new Image(getClass().getResource("/resources/floor3.png").toExternalForm()));

    GameData.wallMaterial.setDiffuseColor(new Color(0.45, 0.45, 0.45, 1.0).darker());
    //GameData.wallMaterial.setSpecularColor(Color.BLACK);
    GameData.wallMaterial.setSpecularPower(128);
    GameData.wallMaterial.setDiffuseMap(new Image(getClass().getResource("/resources/wall.png").toExternalForm()));

    //GameData.exitMaterial.setDiffuseColor(Color.WHITE);
    GameData.exitMaterial.setSpecularColor(Color.WHITE.darker());
    GameData.exitMaterial.setSpecularPower(128);
    //GameData.exitMaterial.setDiffuseMap(new Image(getClass().getResource("/resources/exitDoor.png").toExternalForm()));

    // Create the camera, set it to view far enough for any reasonably-sized map
    camera = new PerspectiveCamera(true);
    camera.setNearClip(0.1);
    camera.setFarClip(3500.0);
    camera.setFieldOfView(50);

    // Rotate camera on the y-axis for swivel in response to mouse
    camera.setVerticalFieldOfView(true);
    camera.setTranslateZ(cameraZDisplacement);
    camera.setTranslateY(cameraYDisplacement);
    camera.setRotationAxis(Rotate.Y_AXIS);
    camera.setDepthTest(DepthTest.ENABLE);

    scene.setCamera(camera);

    // Create a "lantern" for the user
    light = new PointLight();
    light.setLightOn(true);

    light.setDepthTest(DepthTest.ENABLE);

    light.setColor(Color.rgb(255, 255, 255, 1).brighter().brighter().brighter());
//    light.setScaleZ(250000);

    light.setLayoutX(-50); // This sets the light floor distance between you(Player/camera) and the in front of you
    light.setLayoutY(camera.getLayoutY());

    light.setTranslateX(camera.getTranslateX());
    light.setTranslateY(camera.getTranslateY());
    light.setTranslateZ(camera.getTranslateZ());

    sceneRoot.getChildren().add(light);

    double distanceModifier = 0.0;

    double dx = camera.getLayoutX();
    double dy = camera.getLayoutY();
    double roughDistance = dx * dx + dy * dy;
    distanceModifier = 1.0 - roughDistance / (camera.getFarClip() * camera.getFarClip());
    if (distanceModifier < 0.0) distanceModifier = 0.0;

    setupLevel();

    gameEngine = new GameEngine(this, combatSystem);
    AudioFiles.backgroundMusic.play(0.4f);

    //    show startup menu
    StartDialog sd = new StartDialog();
    Optional<ButtonType> chosenOption = sd.showAndWait();

    if (chosenOption.isPresent())
    {
      if (chosenOption.get() == StartDialog.START_BUTTON_TYPE)
      {
        // Hide the cursor
        scene.setCursor(Cursor.NONE);

        gameEngine.start();
        isRunning = true;
      }
    }
  }

  // Stores requests to rebuild the level graphically, so that rebuilding is done in a thread-safe manner
  public boolean shouldRebuildLevel = false;

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
      zombie.isAlive(true);
    }

    // Create a zombie update timer
    ZTimer zMoves = new ZTimer();
    zMoves.zUpdateTimer.schedule(zMoves.myUpdate, Zombie.getDecisionRate(), Zombie.getDecisionRate());
  }

  /**
   * Don't regenerate the house but place the player at the beginning along with the past player.
   *
   */
  public void respawnAfterDeath()
  {
    gameEngine.stop();

    System.out.println("RESPAWN");

    GameOverDialog gameOverAlert = new GameOverDialog();
    gameOverAlert.show();

    gameOverAlert.showingProperty().addListener((observable, oldValue, newValue)->
    {
      if (!newValue)
      {
        try {
          //    reset camera and light
          cameraXDisplacement = 0;
          cameraYDisplacement = -375;
          cameraZDisplacement = 0;
          camera.setTranslateZ(cameraZDisplacement);
          camera.setTranslateY(cameraYDisplacement);

          light.setTranslateX(camera.getTranslateX());
          light.setTranslateY(camera.getTranslateY());
          light.setTranslateZ(camera.getTranslateZ());

          sceneRoot.getChildren().add(PlayerData.past);
          PlayerData.past.setTranslateX(PlayerData.xPosition * GameData.TILE_WIDTH_AND_HEIGHT);
          PlayerData.past.setTranslateZ(PlayerData.yPosition * GameData.TILE_WIDTH_AND_HEIGHT);
          PlayerData.past.setTranslateY(-GameData.WALL_HEIGHT / 2);

          gameEngine.start();
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Setup the zombies and player (camera).
   *
   */
  public void placeZombies()
  {
    // Add all of the 3D zombie objects
    for (Zombie zombie : LevelVar.zombieCollection)
    {
      sceneRoot.getChildren().add(zombie.zombie3D);
      zombie.isAlive(true);
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

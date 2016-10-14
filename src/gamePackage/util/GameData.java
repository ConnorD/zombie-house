package gamePackage.util;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * A simple storage class to hold all the static variables used in the game
 *
 * @author Ederin Igharoro
 * Created by Ultimate Ediri(Ederin) on 10/7/2016.
 */
public class GameData
{
  public static final double TARGET_FRAMES_PER_SECOND = 60;

  public static final double PLAYER_TURN_SPEED = 0.07;
  public static final double PLAYER_TURN_SMOOTHING = 0.36;

  public static final double FLOOR_Y_DISPLACEMENT = -10;
  public static final double CEILING_Y_DISPLACEMENT = -600;
  public static final double WALL_HEIGHT = 600;
  public static final double TILE_WIDTH_AND_HEIGHT = 400;
  public static final double WALL_COLLISION_OFFSET = 0.25;

  public static final int WINDOW_WIDTH = 1260;
  public static final int WINDOW_HEIGHT = 900;

  public static final int ZOMBIE_ACTIVATION_DISTANCE = 14;

  public static final PhongMaterial floorMaterial1 = new PhongMaterial(Color.WHITE);
  public static final PhongMaterial floorMaterial2 = new PhongMaterial(Color.WHITE);
  public static final PhongMaterial floorMaterial3 = new PhongMaterial(Color.WHITE);
  public static final PhongMaterial floorMaterial4 = new PhongMaterial(Color.WHITE);
  public static final PhongMaterial ceilingMaterial = new PhongMaterial(Color.WHITE);
  public static final PhongMaterial wallMaterial = new PhongMaterial(Color.WHITE);
  public static final PhongMaterial exitMaterial = new PhongMaterial(Color.WHITE);
}

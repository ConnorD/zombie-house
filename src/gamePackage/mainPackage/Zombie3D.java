package gamePackage.mainPackage;

import gamePackage.util.StatusBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

import java.util.Random;



/**
 * Zombie3D holds zombie animation frames that I animated in Blender.
 * Zombie model originally from http://www.blendswap.com/blends/view/4807.
 * Texturing and movement by me.
 * <p>
 * Each Zombie3D is tied to a traditional Zombie object.
 *
 * @author Maxwell Sanchez
 */
public class Zombie3D extends Group
{

  private static final int MAXIMUM_FRAME = 8;
  private static final int LARGEST_FRAME = 30;
  private int currentFrame = 0;
  private int frameDirection = 1;
  private static Random random = new Random();
  public StatusBar zombieVitails;

  private static final String MESH_FILENAME = "resources/Zombie.stl";

  /**
   * Create a Zombie3D by loading in 8 random, contiguous frames,
   * setting the mesh group's scale and Y translation, and preparing
   * the model to rotate on the Y axis.
   */
  public Zombie3D()
  {
    // Give each zombie 8 random, continuous frames to work with, so they aren't all alike
    int randomStart = random.nextInt(LARGEST_FRAME - 8);
    for (int i = randomStart; i <= randomStart + 8; i++)
    {
      try
      {
        // Load in zombie meshes
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/" + i + ".fxml"));
        Group zombieModel = fxmlLoader.load();
        zombieModel.setStyle("-fx-background-color: -fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border),\n" +
                "        linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%);");
        zombieModel.setVisible(false);
        getChildren().add(zombieModel);



      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    setScaleX(25);
    setScaleY(25);
    setScaleZ(25);
    setTranslateY(-235);

    setRotationAxis(Rotate.Y_AXIS);

    // Make sure zombies are on different frames to avoid "synchronized" movement
    getChildren().get(random.nextInt(MAXIMUM_FRAME)).setVisible(true);

    zombieVitails = new StatusBar(true, false, -5, -7, 0, 0, 10, 1);
    getChildren().add(zombieVitails);
  }

  /**
   * Change the current animation frame to the next frame.
   */
  public void nextFrame()
  {
    getChildren().get(currentFrame).setVisible(false);
    currentFrame += frameDirection;
    if (currentFrame >= MAXIMUM_FRAME)
    {
      currentFrame = MAXIMUM_FRAME - 1;
      frameDirection = -1;
    } else if (currentFrame < 0)
    {
      currentFrame = 1;
      frameDirection = 1;
    }

    getChildren().get(currentFrame).setVisible(true);
  }
}

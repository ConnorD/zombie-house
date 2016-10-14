package gamePackage.common;

/**
 * A class to hold information about user interaction.
 * Holds mouseX information for calculating distance mouse was moved.
 *
 * @author Maxwell Sanchez
 */
public class InputContainer
{
  // Directional and speed information
  public static boolean forward = false;
  public static boolean backward = false;
  public static boolean left = false;
  public static boolean right = false;
  public static boolean run = false;

  public static boolean useWeapon = false;

  // The last X location of the mouse, used for calculating how far the user moved the mouse in a mouse event
  public static double lastMouseX = 0.0;

  // THe remaining distance to pan the camera
  public static double remainingCameraPan = 0.0;

  public static boolean isMoving()
  {
    if (forward == false && backward == false && left == false && right == false)
    {
      return false;
    }

    return true;
  }
}

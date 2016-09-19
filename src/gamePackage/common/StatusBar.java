package gamePackage.common;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Ultimate Ediri on 9/18/2016.
 */
public class StatusBar extends Group
{
  private double health , initialHealth, stamina, initialStamina;
  private Rectangle healthGreenBar, healthRedBar;
  private Rectangle staminaYellowBar, staminaGreyBar;
  private double widthBar;

  public double getHealth()
  {
    return health;
  }

  public double getStamina()
  {
    return stamina;
  }

  public StatusBar(double health, double stamina, double healthX, double healthY, double staminaX, double staminaY, double width, double height)
  {
    super();
    this.health = health;
    this.stamina = stamina;
    this.initialHealth = health;
    this.initialStamina = stamina;

    if(this.stamina == 0){this.initHealthRectangles(healthX, healthY, width, height);}

    else
      {
        this.initHealthRectangles(healthX, healthY, width, height);
        this.initStaminaRectangles(staminaX, staminaY, width, height);
      }

  }

  private void initHealthRectangles(double x, double y, double width, double height)
  {
    healthRedBar = new Rectangle();
    healthRedBar.setFill(Color.RED);
    healthRedBar.setTranslateX(x);
    healthRedBar.setTranslateY(y);
    healthRedBar.setHeight(height);
    healthRedBar.setWidth(width);


    healthGreenBar = new Rectangle();
    healthGreenBar.setFill(Color.GREEN);
    healthGreenBar.setTranslateX(x);
    healthGreenBar.setTranslateY(y);
    healthGreenBar.setHeight(height);
    healthGreenBar.setWidth(width);

    this.getChildren().add(healthRedBar);
    this.getChildren().add(healthGreenBar);
  }

  private void initStaminaRectangles(double x, double y, double width, double height)
  {
    staminaGreyBar = new Rectangle();
    staminaGreyBar.setFill(Color.GREY);
    staminaGreyBar.setTranslateX(x);
    staminaGreyBar.setTranslateY(y);
    staminaGreyBar.setHeight(height);
    staminaGreyBar.setWidth(width);

    staminaYellowBar = new Rectangle();
    staminaYellowBar.setFill(Color.YELLOW);
    staminaYellowBar.setTranslateX(x);
    staminaYellowBar.setTranslateY(y);
    staminaYellowBar.setHeight(height);
    staminaYellowBar.setWidth(width);

    this.getChildren().add(staminaGreyBar);
    this.getChildren().add(staminaYellowBar);
  }

  public void decrementHealth(double amount)
  {

  }

  private void reAdjustSizeOfVitals(Rectangle selectedVital, double amount)
  {
    double percent;
  }
}

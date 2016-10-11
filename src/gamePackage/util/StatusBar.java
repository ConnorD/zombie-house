package gamePackage.util;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Ederin Igharoro
 * Created by Ultimate Ediri(Ederin) on 9/18/2016.
 */
public class StatusBar extends Group
{
  private Rectangle healthGreenBar;
  private Rectangle staminaYellowBar;
  private double widthBar;


  public StatusBar(boolean hasHealth, boolean hasStamina, double healthX, double healthY, double staminaX, double staminaY, double width, double height)
  {
    super();

    this.widthBar = width;
    if(hasHealth)
    {
      if(!hasStamina){this.initHealthRectangles(healthX, healthY, width, height);}

      else
      {
        this.initHealthRectangles(healthX, healthY, width, height);
        this.initStaminaRectangles(staminaX, staminaY, width, height);
      }
    }


  }

  private void initHealthRectangles(double x, double y, double width, double height)
  {
    Rectangle healthRedBar = new Rectangle();

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
    Rectangle staminaGreyBar = new Rectangle();

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

  public void reduceHealthBar(String damageDoneTo , double amount)
  {
    if(damageDoneTo.equals("Player"))
    {
      //PlayerData.health -= amount;
    }


    else if(damageDoneTo.equals("Zombie"))
    {
      widthBar -= ((amount/10));
      healthGreenBar.setWidth(widthBar);
    }
  }

  public void reduceStaminaBar(double amount)
  {
    widthBar -= (amount/10);

    staminaYellowBar.setWidth(widthBar);
  }

  public void staminaRegen(double amount)
  {
    widthBar += (amount/10);
    staminaYellowBar.setWidth(widthBar);
  }

}

package gamePackage.levelGenerator.house;

import gamePackage.common.LevelVar;
import gamePackage.common.Player;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * @author Rob
 *         <p>
 *         Tile is the super-class for all the 'space' elements of a level (Floor, Wall, Exit)
 *         Contains all the common elements of Floor, Wall, Exit
 *         In theory Tile should not be initialized
 */
public class Tile
{
  public boolean visited = false; // only concerned if visited was used in generation
  public int xCord, yCord;
  public int zone;
  public ArrayList<Tile> neighbors = new ArrayList<>();
  public Tile ancestor;
  public int cost;
  public boolean toRemove = false;
  public boolean hasBeenSeen = false;

  /**
   * Simple constructor
   *
   * @param xCord the x-coordinate (index) on LevelVar.house
   * @param yCord the y-coordinate (index) on LevelVar.house
   * @param zone  the zone ID for this tile
   */
  public Tile(int xCord, int yCord, int zone)
  {
    this.xCord = xCord;
    this.yCord = yCord;
    this.zone = zone;
  }


  public void setNeighbors(Tile[][] house)
  {
    if (xCord + 1 < house.length)
    {
      if (house[xCord + 1][yCord].isFloor())
      {
        neighbors.add(house[xCord + 1][yCord]);
      }
    }
    if (yCord + 1 < house[0].length)
    {
      if (house[xCord][yCord + 1].isFloor())
      {
        neighbors.add(house[xCord][yCord + 1]);
      }
    }
    if (yCord - 1 >= 0)
    {
      if (house[xCord][yCord - 1].isFloor())
      {
        neighbors.add(house[xCord][yCord - 1]);
      }
    }
    if (xCord - 1 >= 0)
    {
      if (house[xCord - 1][yCord].isFloor())
      {
        neighbors.add(house[xCord - 1][yCord]);
      }
    }
  }

  public void addNeighbor(Tile nextTile)
  {
    neighbors.add(nextTile);
  }

  public ArrayList<Tile> getNeighbors()
  {
    return neighbors;
  }

  public void setAncestor(Tile parentTile)
  {
    ancestor = parentTile;
  }

  public void setVisited(boolean value)
  {
    visited = value;
  }

  public void setCost(int cost)
  {
    this.cost = cost;
  }

  public void setToRemove(boolean value)
  {
    this.toRemove = value;
  }

  public double getXCor()
  {
    return xCord;
  }

  public double getYCor()
  {
    return yCord;
  }

  public void isUsed()
  {
  }

  public char getChar()
  {
    return 'f';
  }

  public Color getColor()
  {
    if (LevelVar.WITH_SIGHT && !hasBeenSeen)
    {
      return Color.BLACK;
    }
    return Color.WHITE;
  }

  public boolean isEmpty()
  {
    return false;
  }

  public boolean isFloor()
  {
    return false;
  }

  public void isSeen()
  {
    if (hasBeenSeen)
    {
      return;
    }
    double distFromPlayer = Math.abs(Player.xPosition - xCord) + Math.abs(Player.yPosition - yCord);
    if (distFromPlayer <= Player.playerSightRange)
    {
      hasBeenSeen = true;
    }
  }

}
package utils;

import bookClasses.Picture;
import bookClasses.Turtle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-29
 */
public class PaintBg {

  private final static String BG_DIR_PATH = "art/bg/";
  private static BufferedImage backgroundImage;
  private Color red = new Color(255, 120, 120);
  private Color darkred = new Color(180, 0, 0);
  private Color green = new Color(120, 255, 120);
  private Color blue = new Color(120, 120, 255);
  private Color yellow = new Color(255, 255, 120);
  private Color black = new Color(0, 0, 0);
  private Color gold = new Color(212, 175, 55);
  private Color grid = new Color(230, 230, 230);
  private int pictureWidth = 1200;
  private int pictureHeight= 835;

  public static void main(String[] args) {
    String fileName = "world_of_tanks.jpg";
    try {
      backgroundImage = ImageIO.read(new File(BG_DIR_PATH + fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }

    new PaintBg(backgroundImage, "world_of_tanks_custom.jpg");
  }

  public PaintBg(BufferedImage background, String outputFileName) {
    Picture bg = new Picture(background);
    Turtle turtle1 = new Turtle(bg);

    // Color Switch
    boolean moveRed = true;
    boolean moveGreen = false;
    boolean moveBlue = false;
    boolean moveYellow= false;

    int starsCount = 250;
    // Create small stars at random locations within the World
    for (int i=0; i<=starsCount; i++)
    {
      // Cycle colors
      if (moveRed)
      {
        moveRed = false;
        moveGreen = true;
        moveBlue = false;
        moveYellow = false;
        turtle1.setColor(green);
      }
      else if (moveGreen)
      {
        moveRed = false;
        moveGreen = false;
        moveBlue = true;
        moveYellow = false;
        turtle1.setColor(blue);
      }
      else if (moveBlue)
      {
        moveRed = false;
        moveGreen = false;
        moveBlue = false;
        moveYellow = true;
        turtle1.setColor(red);
      }
      else if (moveYellow)
      {
        moveRed = true;
        moveGreen = false;
        moveBlue = false;
        moveYellow = false;
        turtle1.setColor(yellow);
      }

      // Random number generator with Java Math class for spiral coordinates
      int x = (int)((Math.random() * pictureWidth) + 0);
      int y = (int)((Math.random() * pictureHeight) + 0);
      turtle1.penUp();
      turtle1.moveTo(x, y);
      turtle1.penDown();

      // Draw small stars
      for (int j=0; j<=20; j++)
      {
        turtle1.turn(45);
        turtle1.forward(2);
        turtle1.turn(-90);
        turtle1.forward(-2);
      }

      turtle1.penUp();
    }

    bg.show();
    bg.write("art/bg/" + outputFileName);
  }

}

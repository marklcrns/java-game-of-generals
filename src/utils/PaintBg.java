package utils;

import bookClasses.Picture;
import bookClasses.Turtle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class that paints a passed in image different colored stars at random
 * locations, and outputs the painted image to the specified directory. Output
 * file will serve as background image of the gui MainMenuPanel class.
 *
 * Uses Turtle and Picture class from bookClasses.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-29
 */
public class PaintBg {

  /** Background image directory path */
  private final static String BG_DIR_PATH = "art/bg/";

  /** Stores background image to be painted */
  private static BufferedImage backgroundImage;

  /** Colors of the start */
  private final Color red = new Color(180, 0, 0);
  private final Color yellow = new Color(255, 255, 120);
  private final Color gold = new Color(212, 175, 55);
  private final Color gray = new Color(150, 150, 150);

  /** Output image dimensions  */
  private final int pictureWidth = 1200;
  private final int pictureHeight= 835;

  /** Name of the file to modify */
  private final static String fileName = "tank_1200x.jpg";

  /** Output filename  */
  private final static String outputFileName =
    fileName.replace(".jpg", "_painted.jpg");

  public static void main(final String[] args) {
    // Load background image
    try {
      backgroundImage = ImageIO.read(new File(BG_DIR_PATH + fileName));
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // Modify loaded background image
    new PaintBg(backgroundImage);
  }

  /**
   * Constructor method that takes in BufferedImage to be painted with stars.
   * @param image BufferedImage to be painted.
   */
  public PaintBg(final BufferedImage image) {
    final Picture bg = new Picture(image);
    final Turtle turtle = new Turtle(bg);

    // Color Switch
    boolean color1 = true;
    boolean color2 = false;
    boolean color3 = false;
    boolean color4= false;

    final int starsCount = 250;
    final int startSize = 8;
    // Create small stars at random locations within the World
    for (int i = 0; i <= starsCount; i++) {
      // Cycle colors
      if (color1) {
        color1 = false;
        color2 = true;
        color3 = false;
        color4 = false;
        turtle.setColor(red);
      }
      else if (color2) {
        color1 = false;
        color2 = false;
        color3 = true;
        color4 = false;
        turtle.setColor(yellow);
      }
      else if (color3) {
        color1 = false;
        color2 = false;
        color3 = false;
        color4 = true;
        turtle.setColor(gold);
      }
      else if (color4) {
        color1 = true;
        color2 = false;
        color3 = false;
        color4 = false;
        turtle.setColor(gray);
      }

      // Random number generator with Java Math class for spiral coordinates
      final int x = (int)((Math.random() * pictureWidth) + 0);
      final int y = (int)((Math.random() * pictureHeight) + 0);
      turtle.penUp();
      turtle.moveTo(x, y);
      turtle.penDown();

      // Draw small stars
      for (int j=0; j<=20; j++) {
        turtle.turn(45);
        turtle.forward(startSize);
        turtle.turn(-90);
        turtle.forward(-startSize);
      }

      turtle.penUp();
    }

    // show resulting image
    bg.show();
    // writes the resulting image to bg directory
    bg.write(BG_DIR_PATH + outputFileName);
  }

} // PaintImage

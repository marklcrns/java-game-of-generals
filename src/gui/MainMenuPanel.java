package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Main menu JPanel that displays game main menu.
 *
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
public class MainMenuPanel extends JPanel {

  /** Background image directory */
  private final static String BG_DIR_PATH = "art/bg/";

  /** Main menu options buttons */
  private final JButton start, load, howToPlay, quit;

  /** Main menu JButton size */
  private final Dimension MAIN_MENU_BUTTON_SIZE = new Dimension(200, 25);

  /** Main menu JButton font */
  private final Font MAIN_MENU_FONT = new Font("TimesRoman", Font.BOLD, 16);

  /** Background image */
  private BufferedImage backgroundImage;

  /**
   * No argument constructor that creates the main menu JPanel.
   */
  public MainMenuPanel() {
    // Load background image.
    try {
      backgroundImage = ImageIO.read(new File(BG_DIR_PATH + "tank_1200x_painted.jpg"));
    } catch (final IOException e) {
      e.printStackTrace();
    }
    this.setLayout(new GridBagLayout());
    this.setBackground(Color.GRAY);
    this.setVisible(true);

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Add main menu label
    final JLabel title = new JLabel("Game of the Generals");
    title.setFont(new Font("TimesRoman", Font.BOLD, 70));
    title.setForeground(new Color(219, 243, 250));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    this.add(title, gbc);

    // Add main menu buttons
    start = new JButton("START GAME");
    start.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    start.setFont(MAIN_MENU_FONT);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    this.add(start, gbc);
    load  = new JButton("LOAD GAME");
    load.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    load.setFont(MAIN_MENU_FONT);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    this.add(load, gbc);
    howToPlay = new JButton("HOW TO PLAY");
    howToPlay.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    howToPlay.setFont(MAIN_MENU_FONT);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    this.add(howToPlay, gbc);
    quit = new JButton("QUIT");
    quit.setPreferredSize(MAIN_MENU_BUTTON_SIZE);
    quit.setFont(MAIN_MENU_FONT);
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridheight = 1;
    this.add(quit, gbc);
    final JLabel author = new JLabel("by Mark Lucernas");
    author.setFont(new Font("TimesRoman", Font.BOLD, 24));
    author.setForeground(new Color(219, 243, 250));
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.gridheight = 1;
    this.add(author, gbc);

  }

  /**
   * Paints main menu JPanel background with image.
   */
  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);
    g.drawImage(backgroundImage, 0, 0, null);
  }

  /**
   * Gets the main menu start button.
   * @return JButton start field.
   */
  public JButton getStartBtn() {
    return this.start;
  }

  /**
   * Gets the main menu load button.
   * @return JButton load field.
   */
  public JButton getLoadBtn() {
    return this.load;
  }

  /**
   * Gets the main menu how to play button.
   * @return JButton howToPlay field.
   */
  public JButton getHowToPlayBtn() {
    return this.howToPlay;
  }

  /**
   * Gets the main menu quit button.
   * @return JButton quit field.
   */
  public JButton getQuitBtn() {
    return this.quit;
  }

} // MainMenuPanel

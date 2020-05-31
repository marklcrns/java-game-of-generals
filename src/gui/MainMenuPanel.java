package gui;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;

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

  /** Background image */
  private BufferedImage backgroundImage;

  /**
   * No argument constructor that creates the main menu JPanel.
   */
  public MainMenuPanel() {
    // Load background image.
    try {
      backgroundImage = ImageIO.read(new File(BG_DIR_PATH + "tank_1200x.jpg"));
    } catch (final IOException e) {
      e.printStackTrace();
    }
    this.setLayout(new GridBagLayout());
    // this.setBackground(Color.GRAY);
    this.setVisible(true);

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    // Add main menu label
    final JLabel title = new JLabel("Game of the Generals");
    title.setFont(new Font("TimesRoman", Font.BOLD, 70));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    this.add(title, gbc);

    // Add main menu buttons
    start = new JButton("Start Game");
    start.setPreferredSize(new Dimension(200, 30));
    start.setSize(100, 100);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    this.add(start, gbc);
    load  = new JButton("Load Game");
    load.setPreferredSize(new Dimension(200, 30));
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    this.add(load, gbc);
    howToPlay = new JButton("How To Play");
    howToPlay.setPreferredSize(new Dimension(200, 30));
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    this.add(howToPlay, gbc);
    quit = new JButton("Quit");
    quit.setPreferredSize(new Dimension(200, 30));
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridheight = 1;
    this.add(quit, gbc);
  }

  /**
   * Paints main menu JPanel background with image.
   */
  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);

    // TODO: Fix
    // Centers image inside JPanel bounds
    final Graphics2D g2 = (Graphics2D) g;
    final int x = (this.getWidth() - this.backgroundImage.getWidth(null)) / 2;
    final int y = (this.getHeight() - this.backgroundImage.getWidth(null)) / 2;
    g2.drawImage(backgroundImage, x, y, null);
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

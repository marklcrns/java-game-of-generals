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
import java.awt.GridBagConstraints;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
// Tutorial: https://www.youtube.com/watch?v=g2vDARb7gx8&t=81s
public class MainMenuPanel extends JPanel {

  private final static String BG_DIR_PATH = "art/bg/";
  private JButton start, load, howToPlay, quit;
  private BufferedImage backgroundImage;

  MainMenuPanel() {
    try {
      backgroundImage = ImageIO.read(new File(BG_DIR_PATH + "world_of_tanks.jpg"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.setLayout(new GridBagLayout());
    // this.setBackground(Color.GRAY);
    this.setVisible(true);

    GridBagConstraints gbc = new GridBagConstraints();

    gbc.insets = new Insets(5, 5, 5, 5);

    JLabel title = new JLabel("Game of the Generals");
    title.setFont(new Font("TimesRoman", Font.BOLD, 70));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    this.add(title, gbc);

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

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(backgroundImage, 0, 0, null);
  }

  public JButton getStartBtn() {
    return this.start;
  }

  public JButton getLoadBtn() {
    return this.load;
  }

  public JButton getHowToPlayBtn() {
    return this.howToPlay;
  }

  public JButton getQuitBtn() {
    return this.quit;
  }
}

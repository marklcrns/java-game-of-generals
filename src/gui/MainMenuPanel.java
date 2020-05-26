package gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.GridBagConstraints;

/**
 * Author: Mark Lucernas
 * Date: 2020-05-25
 */
// Tutorial: https://www.youtube.com/watch?v=g2vDARb7gx8&t=81s
public class MainMenuPanel extends JPanel {

  private JButton start, load, howToPlay, quit;

  MainMenuPanel() {
    this.setLayout(new GridBagLayout());
    this.setBackground(Color.GRAY);
    this.setVisible(true);

    GridBagConstraints gbc = new GridBagConstraints();

    gbc.insets = new Insets(5, 5, 5, 5);

    JLabel title = new JLabel("Game of Generals");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridheight = 1;
    this.add(title, gbc);

    start = new JButton("Start Game");
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridheight = 1;
    this.add(start, gbc);

    load  = new JButton("Load Game");
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridheight = 1;
    this.add(load, gbc);

    howToPlay = new JButton("How To Play");
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 1;
    this.add(howToPlay, gbc);

    quit = new JButton("Quit");
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridheight = 1;
    this.add(quit, gbc);
  }

  public JButton getStartButton() {
    return this.start;
  }

  public JButton getLoadButton() {
    return this.load;
  }

  public JButton getHowToPlayButton() {
    return this.howToPlay;
  }

  public JButton getQuitButton() {
    return this.quit;
  }
}

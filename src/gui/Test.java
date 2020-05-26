package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Test {

  boolean popupShown;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        new Test().makeUI();
      }
    });
  }

  public void makeUI() {
    JMenuItem menu1 = new JMenuItem("One");
    JMenuItem menu2 = new JMenuItem("Two");
    final JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(menu1);
    popupMenu.add(menu2);

    popupMenu.addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        popupShown = true;
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            popupShown = false;
          }
        });
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });

    final JButton button = new JButton("Click");
    button.addMouseListener(new MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent e) {
        final boolean shown = popupShown;
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            popupShown = shown;
          }
        });
      }
    });
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (popupShown) {
          popupMenu.setVisible(false);
          popupShown = false;
        } else {
          popupMenu.show(button, 0, button.getHeight());
        }
      }
    });

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 400);
    frame.add(button, BorderLayout.NORTH);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

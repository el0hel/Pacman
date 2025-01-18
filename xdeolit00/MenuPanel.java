package xdeolit00;

import javax.swing.*;
import java.awt.*;

/**
 * this class represents the menu section of the game window.
 * it provides buttons for selecting different levels and a slider to control the speed of the game.
 */
class MenuPanel extends JPanel {
    /**
     * constructs a new MenuPanel for the Pacman game.
     * initialises buttons for selecting the level and a slider for controlling Pacman’s speed.
     *
     * @param mainFrame the main game window frame
     */
    public MenuPanel(MainFrame mainFrame) {
        setLayout(new FlowLayout());

        // button for easy level
        JButton level1Button = new JButton("Easy");
        level1Button.setFocusable(false);
        level1Button.addActionListener(e ->
                mainFrame.loadLevel(getClass().getClassLoader().getResource("levels/level1.txt").getPath()));
        add(level1Button);

        // button for medium level
        JButton level2Button = new JButton("Medium");
        level2Button.setFocusable(false);
        level2Button.addActionListener(e ->
                mainFrame.loadLevel(getClass().getClassLoader().getResource("levels/level2.txt").getPath()));
        add(level2Button);

        // button for hard level
        JButton level3Button = new JButton("Hard");
        level3Button.setFocusable(false);
        level3Button.addActionListener(e ->
                mainFrame.loadLevel(getClass().getClassLoader().getResource("levels/level3.txt").getPath()));
        add(level3Button);

        // label for pacman's speed slider
        JLabel speedLabel = new JLabel("Pacman Speed:");
        add(speedLabel);

        // speed slider ranging from 50 to 3000, inverted to make more intuitive
        JSlider speedSlider = new JSlider(50, 300, 300);
        speedSlider.setFocusable(false);
        speedSlider.setInverted(true);
        speedSlider.addChangeListener(e -> GamePanel.setGameSpeed(speedSlider.getValue()));
        add(speedSlider);
    }
}
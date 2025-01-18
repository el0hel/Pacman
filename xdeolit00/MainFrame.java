package xdeolit00;

import javax.swing.*;
import java.awt.*;

/**
 * this class is responsible for setting up and displaying the main window of the game.
 * it contains both the menu panel and the game panel, managing the layout and game flow.
 */
class MainFrame extends JFrame {
    private final GamePanel gamePanel;

    /**
     * constructs a new MainFrame for the game.
     * sets up the title, layout, and the menu and game panels.
     * initialises the game panel and makes the frame visible to the user.
     */
    public MainFrame() {
        setTitle("Pacman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // create menu
        MenuPanel menuPanel = new MenuPanel(this);
        add(menuPanel, BorderLayout.NORTH);

        // create game
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null); // centers the frame on the screen
        setVisible(true);

        gamePanel.requestFocusInWindow(); // ensures game panel has focus to receive user inputs
    }

    /**
     * loads a level by reading the level file and initialising the game state.
     *
     * @param filePath the path to the level file
     */
    public void loadLevel(String filePath) {
        gamePanel.loadLevel(filePath);
    }
}

/**
 * the entry point for the game.
 * this class is responsible for starting the application by launching the main frame.
 */
package xdeolit00;

import javax.swing.*;

/**
 * the PacmanGame class contains the main method that runs the game.
 * it uses the SwingUtilities to ensure the creation of the main frame is done on the Event Dispatch Thread.
 */
public class PacmanGame {

    /**
     * the main method is the entry point of the game.
     * it initialises the game by creating and showing the main frame.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
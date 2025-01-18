package xdeolit00;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this class handles the game logic and rendering for the game.
 * it controls the game flow, Pacman and ghost movement, score, collision detection,
 * and the rendering of the game board and its elements.
 */
class GamePanel extends JPanel {
    // game setup variables
    private static int gameSpeed = 300; // executes stepGame every 300ms
    private static final int GHOST_MOVE_SPEED = 400; // executes moveGHosts every 500ms
    private char[][] board;
    private int rows, cols;
    private Timer gameTimer;

    private Image pacman;
    private final Image initialPacman, ghostImg, key, gate, dead, wall;

    // pacman control variables
    private int playerRow, playerCol;
    private int directionRow = 0, directionCol = 0;
    private boolean hasKey = false;
    private int score = 0;

    private final List<Ghost> ghosts;  // list to store ghosts
    private Timer ghostTimer;

    /**
     * constructs the GamePanel and initialises necessary resources.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.BLACK);
        initialPacman = new ImageIcon(getClass().getClassLoader().getResource("images/pacman.png")).getImage();
        ghostImg = new ImageIcon(getClass().getClassLoader().getResource("images/ghost.gif")).getImage();
        key = new ImageIcon(getClass().getClassLoader().getResource("images/key.png")).getImage();
        gate = new ImageIcon(getClass().getClassLoader().getResource("images/gate.png")).getImage();
        dead = new ImageIcon(getClass().getClassLoader().getResource("images/dead.png")).getImage();
        wall = new ImageIcon(getClass().getClassLoader().getResource("images/wall.png")).getImage();
        pacman = initialPacman;
        ghosts = new ArrayList<>();

        // initialise timer for ghost movement (this is to separate pacman movement from ghost movement)
        ghostTimer = new Timer(GHOST_MOVE_SPEED, e -> moveGhosts());
        ghostTimer.start();

        // listener for user commands (up down left right arrow keys)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        directionRow = -1;
                        directionCol = 0; break;
                    case KeyEvent.VK_DOWN:
                        directionRow = 1;
                        directionCol = 0; break;
                    case KeyEvent.VK_LEFT:
                        directionRow = 0;
                        directionCol = -1; break;
                    case KeyEvent.VK_RIGHT:
                        directionRow = 0;
                        directionCol = 1; break;
                }
            }
        });

        // listener to support mouse movement
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // check if the board is initialised properly
                if (rows == 0 || cols == 0 || getWidth() == 0 || getHeight() == 0) {
                    return; // avoid division by zero
                }

                // calculate the cell width and height based on the panel's size
                int cellWidth = getWidth() / cols;
                int cellHeight = getHeight() / rows;

                // get the mouse position relative to the panel
                int mouseX = e.getX();
                int mouseY = e.getY();

                // determine the row and column of the mouse's position
                int targetRow = mouseY / cellHeight;
                int targetCol = mouseX / cellWidth;

                // check if the mouse is within bounds of the board
                if (targetRow >= 0 && targetRow < rows && targetCol >= 0 && targetCol < cols) {
                    // calculate the direction to move towards the target cell
                    int deltaRow = targetRow - playerRow;
                    int deltaCol = targetCol - playerCol;

                    // normalise the direction to have a maximum step size of 1
                    if (Math.abs(deltaRow) > Math.abs(deltaCol)) {
                        directionRow = deltaRow > 0 ? 1 : -1;
                        directionCol = 0; // move vertically
                    } else if (Math.abs(deltaCol) > Math.abs(deltaRow)) {
                        directionCol = deltaCol > 0 ? 1 : -1;
                        directionRow = 0; // move horizontally
                    } else {
                        // move diagonally
                        directionRow = deltaRow > 0 ? 1 : -1;
                        directionCol = deltaCol > 0 ? 1 : -1;
                    }
                }
            }
        });
    }

    /**
     * loads the game level by reading the specified file.
     * parses the level layout and initialises Pacman, ghosts, and other elements.
     *
     * @param filePath The path to the level file.
     */public void loadLevel(String filePath) {
        // parse file's information
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] dimensions = reader.readLine().trim().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            board = new char[rows][cols];

            resetGame(); // ensures when a level button is pressed all variables are reset and window is repainted

            // render pacman and ghosts
            for (int i = 0; i < rows; i++) {
                String line = reader.readLine().trim();
                board[i] = line.toCharArray();
                for (int j = 0; j < cols; j++) {
                    switch (board[i][j]){
                        case 'P':
                            // set Pacman's initial position
                            playerRow = i;
                            playerCol = j; break;
                        case 'C':
                            ghosts.add(new Ghost(i, j, board, this)); break; // add each encountered ghost to the list
                    }
                }
            }
            // create a new timer
            if (gameTimer != null) {
                gameTimer.stop();
            }
            gameTimer = new Timer(gameSpeed, e -> stepGame());
            gameTimer.start();

            // create a new ghost timer
            if (ghostTimer != null) {
                ghostTimer.stop();
            }
            ghostTimer = new Timer(GHOST_MOVE_SPEED, e -> moveGhosts());
            ghostTimer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to dynamically change game speed based on user input (via slider)
    /**
     * sets the game speed, which controls how often Pacman's stepGame method is executed.
     *
     * @param speed the new game speed in milliseconds
     */
    public static void setGameSpeed(int speed) {
        gameSpeed = speed;
    }

    /**
     * handles Pacman's movement and interactions within the game.
     * it updates Pacman's position based on the current direction and handles special objects like keys, gates, and points.
     */
    private void stepGame() {
        // calculate the cell that Pacman wants to move to
        if (directionRow == 0 && directionCol == 0) return;
        int newRow = playerRow + directionRow;
        int newCol = playerCol + directionCol;

        // check that move is within boundaries of board
        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
            // get the content of the cell that Pacman wants to move to
            char targetCell = board[newRow][newCol];

            // if it is a wall or gate and key has not been acquired, block the move
            if (targetCell == 'W' || (targetCell == 'G' && !hasKey)) return;

            // handle interactions with special objects
            switch (targetCell) {
                case 'o':
                    // collect a point
                    score++;
                    break;
                case 'K':
                    // collect key
                    hasKey = true;
                    break;
                case 'G':
                    // winning condition: pacman reached gate with the key
                    pacman = null; // pacman disappears
                    repaint();
                    ghostTimer.stop();
                    gameTimer.stop(); // stop the game loop
                    JOptionPane.showMessageDialog(this, "You win!");
                    return;
            }

            // move Pacman to new position
            board[playerRow][playerCol] = '.';  // clear old position
            playerRow = newRow;
            playerCol = newCol;
            board[playerRow][playerCol] = 'P'; // update Pacman position
            repaint();

        }
    }

    /**
     * checks if Pacman has collided with any ghosts.
     * if a collision is detected, it ends the game.
     *
     * @param row the row position to check for collisions
     * @param col the column position to check for collisions
     */
    public void checkCollision(int row, int col) {
        for (Ghost ghost : ghosts) {
            if (ghost.getRow() == row && ghost.getCol() == col) {
                pacman = dead; // display dead pacman image
                playerRow = playerCol = -1; // reset position
                directionCol = directionRow = 0; // stop movement
                JOptionPane.showMessageDialog(this, "You died!");
                return;
            }

        }

    }

    /**
     * moves the ghosts based on their logic defined in Ghost class
     */
        private void moveGhosts() {
        for (Ghost ghost : ghosts) {
            ghost.move(playerRow, playerCol, ghosts); // pass Pacman's position to the ghost
        }
        repaint();  // redraw the screen after the ghosts move
    }





    /**
     * paints the game components to the screen, including Pacman, ghosts, walls, and points.
     *
     * @param g the Graphics object used to render the game components
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (board != null) { // check if board is initialised
            // determine size of each cell based on panel's dimensions and board size
            int fieldSize = Math.min(getWidth() / cols, getHeight() / rows);

            // vars for image rendering
            int imageSize, imageX, imageY;

            // iterate through each cell of the game board
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    char field = board[row][col]; // get content of cell
                    // render each cell based on its contents
                    switch (field) {
                        case 'W': // wall
                            imageSize = fieldSize;
                            imageX = col * fieldSize;
                            imageY = row * fieldSize;
                            g.drawImage(wall, imageX, imageY, imageSize, imageSize, this);
                            break;
                        case 'G': // gate
                            imageSize = fieldSize;
                            imageX = col * fieldSize;
                            imageY = row * fieldSize;
                            g.drawImage(gate, imageX, imageY, imageSize, imageSize, this);
                            break;
                        case '.': // empty field
                            g.setColor(Color.BLACK);
                            g.fillRect(col * fieldSize, row * fieldSize, fieldSize, fieldSize);
                            break;
                        case 'P': // pacman
                            imageSize = (int) (fieldSize * 0.5);
                            imageX = col * fieldSize + (fieldSize - imageSize) / 2;
                            imageY = row * fieldSize + (fieldSize - imageSize) / 2;
                            g.drawImage(pacman, imageX, imageY, imageSize, imageSize, this);
                            break;
                        case 'K': // key
                            imageSize = (int) (fieldSize * 0.5);
                            imageX = col * fieldSize + (fieldSize - imageSize) / 2;
                            imageY = row * fieldSize + (fieldSize - imageSize) / 2;
                            g.drawImage(key, imageX, imageY, imageSize, imageSize, this);
                            break;
                        case 'o': // point
                            g.setColor(Color.WHITE);
                            int pointSize = fieldSize / 10; //
                            int pointX = col * fieldSize + (fieldSize - pointSize) / 2;
                            int pointY = row * fieldSize + (fieldSize - pointSize) / 2;
                            g.fillOval(pointX, pointY, pointSize, pointSize);
                            break;
                        }
                }
            }

            // render all ghosts based on their current positions
            for (Ghost ghost : ghosts) {
                imageSize = (int) (fieldSize * 0.5);
                imageX = ghost.getCol() * fieldSize + (fieldSize - imageSize) / 2;
                imageY = ghost.getRow() * fieldSize + (fieldSize - imageSize) / 2;
                g.drawImage(ghostImg, imageX, imageY, imageSize, imageSize, this);
            }
        }

        // draw the score on top of the board
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Comic Sans", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
    }

    /**
     * Resets the game by clearing the game state.
     */
    private void resetGame() {
        hasKey = false;
        playerRow = playerCol = -1;
        directionCol = directionRow = score = 0;
        pacman = initialPacman;
        ghosts.clear();
    }
}
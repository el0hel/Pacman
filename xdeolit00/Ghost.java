package xdeolit00;
import java.util.List;

/**
 * this class defines the movement logic of the ghosts in the Pacman game.
 * the ghosts chase Pacman and move in a way to avoid collisions with walls and other ghosts.
 */
public class Ghost {
    private int row, col;
    private final char[][] board;
    private final GamePanel gamePanel;

    /**
     * constructs a ghost at a specific position on the board.
     *
     * @param row the initial row position of the ghost
     * @param col the initial column position of the ghost
     * @param board the game board containing the layout
     * @param gamePanel the panel responsible for managing the game state and rendering
     */
    public Ghost(int row, int col, char[][] board, GamePanel gamePanel) {
        this.row = row;
        this.col = col;
        this.board = board;
        this.gamePanel = gamePanel;
    }

    /**
     * moves the ghost to chase Pacman based on its current position and Pacman's position.
     * if the primary movement direction is blocked, the ghost attempts to move randomly to free itself.
     *
     * @param pacmanRow the row position of Pacman
     * @param pacmanCol the column position of Pacman
     * @param ghosts the list of all ghosts in the game
     */
    public void move(int pacmanRow, int pacmanCol, List<Ghost> ghosts) {
        int bestRowDir = 0;
        int bestColDir = 0;

        // determine the primary direction towards Pacman (how far ghosts are vert. and hor.)
        int rowDiff = pacmanRow - row;
        int colDiff = pacmanCol - col;

        // prioritise movement in direction where distance is greater
        if (Math.abs(rowDiff) > Math.abs(colDiff)) {
            bestRowDir = Integer.signum(rowDiff); // -1, 0, or 1 to move up, no move or down
        } else {
            bestColDir = Integer.signum(colDiff); // -1, 0, or 1 to move left, no move, or right
        }

        // attempt to move in the best direction
        if (tryMove(row + bestRowDir, col + bestColDir, pacmanRow, pacmanCol, ghosts)) {
            return;
        }

        // if blocked, try to move up, down, left or right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            if (tryMove(row + dir[0], col + dir[1], pacmanRow, pacmanCol, ghosts)) {
                return;
            }
        }
    }

    /**
     * attempts to move the ghost to a new position and checks if the move is valid.
     * if the move is successful, the ghost's position is updated and any collisions are checked.
     *
     * @param newRow the new row position the ghost is trying to move to
     * @param newCol the new column position the ghost is trying to move to
     * @param pacmanRow the row position of Pacman, for collision detection
     * @param pacmanCol the column position of Pacman, for collision detection
     * @param ghosts the list of all ghosts in the game
     * @return true if the move is successful, false otherwise
     */
    private boolean tryMove(int newRow, int newCol, int pacmanRow, int pacmanCol, List<Ghost> ghosts) {
        if (isValidMove(newRow, newCol, pacmanRow, pacmanCol, ghosts)) {
            row = newRow;
            col = newCol;
            gamePanel.checkCollision(pacmanRow, pacmanCol);
            return true;
        }
        return false;
    }

    /**
     * checks if a move to a new position is valid.
     * a move is valid if it does not go out of bounds, collide with walls, or overlap with other ghosts.
     *
     * @param newRow the new row position the ghost is trying to move to
     * @param newCol the new column position the ghost is trying to move to
     * @param pacmanRow the row position of Pacman, for collision detection
     * @param pacmanCol the column position of Pacman, for collision detection
     * @param ghosts the list of all ghosts in the game, to check for overlap with other ghosts
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(int newRow, int newCol, int pacmanRow, int pacmanCol, List<Ghost> ghosts) {
        // check for game panel's boundaries
        if (newRow < 0 || newRow >= board.length || newCol < 0 || newCol >= board[0].length) {
            return false;
        }
        // check for walls
        if (board[newRow][newCol] == 'W') {
            return false;
        }

        // check for collision with other ghosts to avoid overlap
        for (Ghost ghost : ghosts) {
            if (ghost != this && ghost.row == newRow && ghost.col == newCol) {
                return false; // square is occupied by another ghost
            }
        }
        return true;
    }

    /**
     * gets the current row position of the ghost.
     *
     * @return the current row position of the ghost
     */
    public int getRow() {
        return row;
    }
    /**
     * gets the current column position of the ghost.
     *
     * @return the current column position of the ghost
     */
    public int getCol() {
        return col;
    }
}

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
class Maze {
    private char[][] maze;
    private int rows, cols;
    private int dotCount;
    Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        maze = new char[rows][cols];
        initializeMaze();
    }
    private void initializeMaze() {
        dotCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    maze[i][j] = '#';
                } else {
                    maze[i][j] = '.';
                    dotCount++;
                }
            }
        }
        addHurdles();
    }
    private void addHurdles() {
        int[][] hurdles = {
                {2, 5}, {2, 6}, {3, 5}, {4, 4}, {4, 5}, {4, 6},
                {6, 10}, {7, 10}, {8, 10}, {1, 8}, {1, 9}, {2, 9},
                {3, 2}, {3, 3}, {3, 4}, {5, 1}, {5, 2}, {5, 3},
                {6, 1}, {6, 2}, {6, 3}, {7, 1}, {7, 2}, {7, 3},
                {8, 5}, {8, 6}, {9, 5}, {4, 8}, {4, 9}, {5, 8},
                {1, 15}, {1, 16}, {2, 15}, {5, 15}, {6, 15}, {7, 15},
                {7, 12}, {8, 12}, {9, 12}, {10,14},{10,15},{13,5},{13,19},
                {5, 13}, {6, 13}, {7, 13}, {15,14},{15,15},{13,18},{1,19},
                {11,12},{11,13},{11,14},{11,15}, {12,12},{12,13},{12,14},{12,15}
        };
        for (int[] hurdle : hurdles) {
            int x = hurdle[0];
            int y = hurdle[1];
            if (isWithinBounds(x, y)) {
                maze[x][y] = '#';
                dotCount--;
            }
        }
    }
    public void updatePosition(int old_x, int old_y, int new_x, int new_y, char symbol) {
        if (isWithinBounds(old_x, old_y) && maze[old_x][old_y] != '#') {
            maze[old_x][old_y] = ' ';
        }
        if (isWithinBounds(new_x, new_y)) {
            maze[new_x][new_y] = symbol;
        }
    }
    public void GhostUpdatePosition(int old_x, int old_y, int new_x, int new_y, char symbol) {
        if (isWithinBounds(old_x, old_y) && maze[old_x][old_y] == symbol) {
            if (maze[old_x][old_y] == symbol && maze[new_x][new_y] == '.') {
                maze[old_x][old_y] = '.';
            } else {
                maze[old_x][old_y] = ' ';
            }
        }
        if (isWithinBounds(new_x, new_y)) {
            maze[new_x][new_y] = symbol;
        }
    }

    public void eatEatable(int x, int y) {
        if (maze[x][y] == '.') {
            maze[x][y] = ' ';
            dotCount--;
            PacManGame.addScore(10);
        }
    }
    public int getDotCount() {
        return dotCount;
    }
    public char getPosition(int x, int y) {
        return maze[x][y];
    }
    public void draw() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }
}
class Walker {
    private int x, y;
    private char symbol;
    Walker(int x, int y, char symbol) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
    }
    public int getX() {

        return x;
    }
    public int getY() {
        return y;
    }
    public char getSymbol() {
        return symbol;
    }
    public void movePacman(String direction, Maze board) {
        int new_x = x;
        int new_y = y;
        switch (direction) {
            case "^":
                new_x = x - 1;
                break;
            case "v" :
                new_x = x + 1;
                break;
            case "<":
                new_y = y - 1;
                break;
            case ">":
                new_y = y + 1;
                break;
            default : {
                System.out.println("Invalid direction!");
                return;
            }
        }
        if (board.getPosition(new_x, new_y) != '#') {
            board.eatEatable(new_x, new_y);
            board.updatePosition(x, y, new_x, new_y, symbol);
            x = new_x;
            y = new_y;
        } else {
            System.out.println("Pacman hit a wall! Try a different direction.");
        }
    }
    public void moveRandom(Maze board) {
        Random random = new Random();
        int new_x, new_y, dx, dy, maxAttempts = 0;
        do {
            dx = random.nextInt(3) - 1;
            dy = random.nextInt(3) - 1;
            new_x = x + dx;
            new_y = y + dy;
            maxAttempts++;
        } while (maxAttempts < 10 &&(new_x < 0 || new_x >= board.getRows() || new_y < 0 || new_y >= board.getCols() ||
                board.getPosition(new_x, new_y) == '#' || board.getPosition(new_x, new_y) == 'P'));
        if (maxAttempts < 10) {
            board.GhostUpdatePosition(x, y, new_x, new_y, symbol);
            x = new_x;
            y = new_y;
        }
    }
}
public class PacManGame {
    private static int score = 0;
    public static void main(String[] args) {
        System.out.println("Welcome to PAC MAN inspired game! ");
        System.out.println("Eating one . would give you 10 points.");
        Maze board = new Maze(15, 20);
        Walker pacman = new Walker(1, 1, 'P');
        Walker ghost1 = new Walker(5, 5, 'G');
        Walker ghost2 = new Walker(2, 2, 'G');
        Scanner scanner = new Scanner(System.in);
        int lives = 2;
        board.updatePosition(1, 1, pacman.getX(), pacman.getY(), pacman.getSymbol());
        board.GhostUpdatePosition(5, 5, ghost1.getX(), ghost1.getY(), ghost1.getSymbol());
        board.GhostUpdatePosition(2, 2, ghost2.getX(), ghost2.getY(), ghost2.getSymbol());
        List<Walker> ghosts = Arrays.asList(ghost1, ghost2);
        while (board.getDotCount() > 0 && lives > 0) {
            System.out.println("Current Score: " + score);
            System.out.println("Lives remaining: " + lives);
            board.draw();
            System.out.println("Enter direction <, >, ^, v: ");
            String direction;
            do {
                direction = scanner.nextLine();
                if (!List.of("<", ">", "^", "v").contains(direction)) {
                    System.out.println("Invalid direction! Enter again: ");
                }
            } while (!List.of("<", ">", "^", "v").contains(direction));
            pacman.movePacman(direction, board);
            if (isPacmanCaught(pacman, ghosts)) {
                lives--;
                System.out.println("Pacman was caught by a ghost! Lives left: " + lives);
                pacman = new Walker(1, 1, 'P');
                continue;
            }
            ghost1.moveRandom(board);
            ghost2.moveRandom(board);
            if (isPacmanCaught(pacman, ghosts)) {
                lives--;
                System.out.println("Pacman was caught by a ghost! Lives left: " + lives);
                pacman = new Walker(1, 1, 'P');
                continue;
            }
        }
        if (lives == 0) {
            System.out.println("Game Over! Pacman has no lives left.");
        } else if (board.getDotCount() == 0) {
            System.out.println("Congratulations! You've cleared the board.");
        }
        scanner.close();
    }
    private static boolean isPacmanCaught(Walker pacman, List<Walker> ghosts) {
        for (Walker ghost : ghosts) {
            if (pacman.getX() == ghost.getX() && pacman.getY() == ghost.getY()) {
                return true;
            }
        }
        return false;
    }
    public static void addScore(int points) {
        score = score + points;
    }
}
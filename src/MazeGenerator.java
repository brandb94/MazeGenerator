import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

/**
 * Maze Generator. Uses the depth-first-search algorithm to create a
 * maze inside of a 2D char array.
 *@author Brandon Bell
 *@version 2.0.1
 */
public class MazeGenerator {

    /** Random object used throughout the program. */
    private static final Random RAND = new Random();

    /** The number of rows. */
    private final int rows;

    /** The number of columns. */
    private final int cols;

    /** The maze. */
    private final char maze[][];

    /** The backtracking stack. */
    private final ArrayDeque<Cell> myBacktrackStack;

    /** The solution stack. */
    private final ArrayDeque<Cell> solutionStack;

    /** The solution cell. */
    private final Cell solutionCell;

    /** The debug flag. */
    private final boolean debugEnabled;

    /** The flag used to determine when to stop pushing to the
     * solution stack. */
    private boolean isSolved;

    /**
     * Constructor, instantiates a new maze.
     *
     * @param height the height
     * @param width the width
     * @param debug the debug flag
     */
    public MazeGenerator(final int width, final int height, final boolean debug) {
        rows = height;
        solutionCell = new Cell(height * 2 - 1, width * 2 - 1);
        cols = width;
        maze = new char[height * 2 + 1][width * 2 + 1];
        isSolved = false;
        debugEnabled = debug;
        myBacktrackStack = new ArrayDeque<>();
        solutionStack = new ArrayDeque<>();

        buildMazeShell();
        generateMaze();
        showSolution();

    }

    /**
     * Builds the maze initially.
     *
     * Runtime: O(n) - must go over every cell of the maze
     */
    private final void buildMazeShell() {
        for (int i = 0; i < rows * 2 + 1 ; i++) {

            for (int k = 0; k < cols * 2 + 1; k++) {
                //even row - will always be Xs
                if (i % 2 == 0) maze[i][k] = 'X'; //place border

                if (i % 2 == 1 && k % 2 == 1) {
                    //odd row, odd col
                    maze[i][k] = ' '; // place cell
                }
                if (i % 2 == 1 && k % 2 == 0) {
                    //odd row, even col
                    maze[i][k] = 'X';
                }
            }
        }

        maze[0][1] = ' '; //entrance

        maze[rows * 2][cols * 2 - 1] = ' '; //exit

    }

    /**
     * Generates the maze.
     * Runtime:O(n) must visit every cell
     *
     */
    private final void generateMaze() {
        int totalCells = rows * cols;
        int visitedCells = 1; //First cell is always visited

        //top left corner of the maze
        Cell currCell = new Cell(1, 1); //generateRandomCell(); // starting cell

        //Mark starting point as visited
        maze[currCell.getRow()][currCell.getCol()] = 'V';

		/*Depth First search algorithm*/
        while (visitedCells < totalCells) {
            ArrayList<Cell> neighbors = getUnvisitedNeighbors(currCell);

            if (!neighbors.isEmpty()) {
                //current cell has unvisited/legal neighbors

                //push current to backtracking stack
                myBacktrackStack.push(currCell);

                //save solution path locations, stop when solution found
                if (!isSolved) solutionStack.push(currCell);
                // choose random neighbor
                int randDir = RAND.nextInt(neighbors.size());

                //break down the wall between cells
                formPath(currCell, neighbors.get(randDir));

                //reassign current to freshly opened cell
                currCell = neighbors.get(randDir);
                //mark cell as visited
                visitedCells++;

            } else if (!myBacktrackStack.isEmpty()) {
                //we are able to step backward
                currCell = myBacktrackStack.pop();

                if (!isSolved) solutionStack.pop();

            }
            if (debugEnabled) display(); //display each step if debug enabled
            if (solutionCell.isVisited()) isSolved = true; //stop adding/removing from solution stack
        }

    }

    /**
     * Display the solution to the maze with * characters for the path.
     *
     * runtime: O(n) - must iterate over every cell of the maze
     */
    private final void showSolution() {
        for (int i = 0; i < rows * 2 + 1; i++) {
            for (int k = 0; k < cols * 2 + 1; k++) {
                if (maze[i][k] == 'V') maze[i][k] = ' ';
            }
        }
        while (!solutionStack.isEmpty()) {
            Cell curr = solutionStack.pop();
            maze[curr.getRow()][curr.getCol()] = '*';
        }
        //place star by exit
        maze[rows * 2 - 1][cols * 2 - 1] = '*';

    }

    /**
     * Form path between two adjacent cells.
     *
     * Runtime: O(1)
     * @param first the first cell
     * @param second the second cell
     */
    private final void formPath(Cell first, Cell second) {

        if (first.getRow() == second.getRow() && first.getCol() < second.getCol()) {
            //right
            maze[first.getRow()][first.getCol() + 1] = ' ';
            maze[first.getRow()][first.getCol() + 2] = 'V';
        } else if (first.getRow() == second.getRow() && first.getCol() > second.getCol()) {
            //left
            maze[first.getRow()][first.getCol() - 1] = ' ';
            maze[first.getRow()][first.getCol() - 2] = 'V';
        }
        if (first.getCol() == second.getCol() && first.getRow() < second.getRow()) {
            //down
            maze[first.getRow() + 1][first.getCol()] = ' ';
            maze[first.getRow() + 2][first.getCol()] = 'V';
        } else if (first.getCol() == second.getCol() && first.getRow() > second.getRow()) {
            //up
            maze[first.getRow() - 1][first.getCol()] = ' ';
            maze[first.getRow() - 2][first.getCol()] = 'V';
        }

    }

    /**
     * Gets the unvisited neighbors of a given cell.
     * Runtime: O(1)
     *
     * @param ref the reference cell
     * @return the unvisited neighbors
     */
    private final ArrayList<Cell> getUnvisitedNeighbors(Cell ref) {
        ArrayList<Cell> result = new ArrayList<Cell>();

        Cell left = new Cell(ref.getRow(), ref.getCol() - 2);
        Cell right = new Cell(ref.getRow(), ref.getCol() + 2);
        Cell up = new Cell(ref.getRow() - 2, ref.getCol());
        Cell down = new Cell(ref.getRow() + 2, ref.getCol());

        //Only add legal cells to the list
        if (left.checkLegalBounds()&& !left.isVisited()) result.add(left);
        if (right.checkLegalBounds() && !right.isVisited()) result.add(right);
        if (up.checkLegalBounds() && !up.isVisited()) result.add(up);
        if (down.checkLegalBounds() && !down.isVisited()) result.add(down);

        return result;
    }

    /**
     * Display the maze to the console.
     * Runtime: O(n)- must visit every cell
     *
     */
    public final void display() {

        for (int i = 0; i < rows * 2 + 1; i++) {
            for (int k = 0; k < cols * 2 + 1; k++) {
                System.out.print(maze[i][k] + " ");
            }
            System.out.println();
        }
        System.out.println();

    }


    /**
     * Class representing a Cell object.
     * Used to store the location of a given cell in a maze.
     * While used to represent a cell, the Cell object itself is not stored
     * in the maze directly.
     *
     */
    private final class Cell {

        /** The row. */
        private final int row;

        /** The col. */
        private final int col;

        /**
         * Instantiates a new cell.
         * O(1)
         *
         * @param theRow the the row
         * @param theCol the the col
         */
        public Cell(final int theRow, final int theCol) {
            row = theRow;
            col = theCol;

        }

        /**
         * Checks if the cell has been visited yet.
         *
         * Runtime: O(1)
         *
         * @return true, if is visited
         */
        public final boolean isVisited() {
            return maze[row][col] == 'V';
        }

        /**
         * Check legal bounds if the cell has been made within the bounds of the board.
         * Runtime: O(1)
         * @return true, if bounds are legal, false otherwise
         */
        public final boolean checkLegalBounds() {
            return (row >= 1 && col >= 1 && row < rows * 2 && col < cols * 2);

        }

        /**
         * Gets the row.
         * Runtime: O(1)
         * @return the row
         */
        public final int getRow() { return row; }

        /**
         * Gets the column.
         * Runtime: O(1)
         * @return the col
         */
        public final int getCol() { return col; }
        /**
         * Returns a string representation of the cell.
         * Runtime: O(1)
         *
         * @return the string.
         */
        @Override
        public String toString() {
            return "(" + row + ", " + col + ")";
        }

    }

}

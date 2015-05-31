/**
 * Main class. Use to create and display mazes of different sizes.
 * @author Brandon Bell
 * @version 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        // generate a maze with n by m cells


		/*NOTE: DFS algorithm used. Big mazes look best, be generous. */
        /*NOTE: Setting the third parameter to true will display
        *       the maze at every step during its creation.           */
        MazeGenerator maze = new MazeGenerator(5, 5, true);
        maze.display();
        maze = new MazeGenerator(5, 5, false);
        maze.display();
        maze = new MazeGenerator(10, 15, false);
        maze.display();


        maze = new MazeGenerator(30, 20, false);

        maze.display();
    }



}

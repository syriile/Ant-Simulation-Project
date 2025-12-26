import java.io.*;
import java.util.*;

public class Main {
    // -------------------------------------------------------------//
    // Static Varaibles
    // -------------------------------------------------------------//
    private static String filename;
    private static char ant;
    private static char beetle;
    private static int numOfTurns;

    // -------------------------------------------------------------//
    // Main Method
    // -------------------------------------------------------------//
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        readUserInput(sc);
        Creature[][] grid = new Creature[10][10];
        fillGrid(grid);
        runGame(grid);
        sc.close();
    }

    // -------------------------------------------------------------//
    // Reading user input method
    // -------------------------------------------------------------//
    public static void readUserInput(Scanner sc) {
        System.out.print("Enter a filename: ");
        filename = sc.next();
        System.out.print("Enter a character for Ant: ");
        ant = sc.next().charAt(0);
        System.out.print("Enter a character for Beetle: ");
        beetle = sc.next().charAt(0);
        System.out.print("Enter an integer for Number of Turns: ");
        numOfTurns = sc.nextInt();
    }

    // -------------------------------------------------------------//
    // Loading in Grid Method
    // -------------------------------------------------------------//
    public static void fillGrid(Creature[][] grid) {
        Character symbol1 = null, symbol2 = null;
        int count1 = 0, count2 = 0;
        List<String> lines = new ArrayList<>();
        try (Scanner fs = new Scanner(new File(filename))) { // Try to open file and read
            while (fs.hasNextLine()) {
                String line = fs.nextLine();
                lines.add(line);
                for (char x : line.toCharArray()) {
                    if (x == ' ') continue;
                    if (symbol1 == null) symbol1 = x;
                    else if (symbol2 == null && x != symbol1) symbol2 = x;

                    if (x == symbol1) count1++;
                    else if (x == symbol2) count2++;
                }
            }

            Character[] fileSymbols = AntOrBeetle(symbol1, symbol2, count1, count2);
            Character fileAntSymbol = fileSymbols[0];
            Character fileBeetleSymbol = fileSymbols[1];

            for (int row = 0; row < 10 && row < lines.size(); row++) {
                String line = lines.get(row);
                for (int col = 0; col < 10 && col < line.length(); col++) {
                    char x = line.charAt(col);
                    if (x == ' ') grid[row][col] = null;
                    else if (x == fileAntSymbol) grid[row][col] = new Ant(row, col, ant);
                    else if (x == fileBeetleSymbol) grid[row][col] = new Beetle(row, col, beetle);
                    else grid[row][col] = null;
                }
            }

        } catch (FileNotFoundException e) { // Catch File Exception
            System.out.println("Error: File not found (" + filename + ")");
            System.exit(0);
        }
    }

    // -------------------------------------------------------------//
    // AntOrBeetle method to determine whether or not the cell is an ant or beetle
    // -------------------------------------------------------------//
    private static Character[] AntOrBeetle(Character symbol1, Character symbol2 ,int count1, int count2) {
        Character fileAntSymbol, fileBeetleSymbol;
        if (count1 >= count2) {
            fileAntSymbol = symbol1;
            fileBeetleSymbol = symbol2;
        } else {
            fileAntSymbol = symbol2;
            fileBeetleSymbol = symbol1;
        }
        return new Character[]{fileAntSymbol, fileBeetleSymbol};
    }

    // -------------------------------------------------------------//
    // Method for Entire Game, using helper methods
    // -------------------------------------------------------------//
    public static void runGame(Creature[][] grid) {
        for (int turn = 0; turn < numOfTurns; turn++) {
            System.out.println("TURN " + (turn + 1));
            moveCreature(grid, Beetle.class);
            moveCreature(grid, Ant.class);
            starveBeetle(grid);
            breedCreature(grid, Ant.class);
            breedCreature(grid, Beetle.class);
            printGrid(grid);
            System.out.println();
        }
    }

    // -------------------------------------------------------------//
    // printGrid helper method to print entire 10x10 grid
    // -------------------------------------------------------------//
    public static void printGrid(Creature[][] grid) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (grid[row][col] instanceof Ant) System.out.print(ant); // print ant char if cell is an ant object
                else if (grid[row][col] instanceof Beetle) System.out.print(beetle); // print beetle char if cell is a beetle object
                else System.out.print(' '); // print white space if all other conditions are false
            }
            System.out.println(); // print new line after each grid
        }
    }

    // -------------------------------------------------------------//
    // moveCreature generic method for beetle and ant class
    // -------------------------------------------------------------//
    public static void moveCreature(Creature[][] grid, Class<?> creatureType) {
        // tracks which creatures have moved
        boolean[][] moved = new boolean[10][10];

        for (int col = 0; col < 10; col++) { // column first traversal for all move, breed, and starve
            for (int row = 0; row < 10; row++) {
                Creature c = grid[row][col]; 
                // skip if already moved, wrong creaturetype, or any empty cell
                if (c == null || !creatureType.isInstance(c) || moved[row][col]) continue;
                c.incrementSteps();

                // beetle logic 
                if (creatureType == Beetle.class) {
                    Beetle b = (Beetle) c;
                    boolean ate = false;
                    int[] newLocation = c.move(grid); // determines which direction to move

                    if (newLocation != null) { 
                        int row2 = newLocation[0], col2 = newLocation[1];
                        if (row2 >= 0 && row2 < 10 && col2 >= 0 && col2 < 10) { // boundary check 
                            if (grid[row2][col2] instanceof Ant) { // if the target is a cell with an ant, then eat
                                b.ateAnt();
                                ate = true;
                                grid[row2][col2] = c;
                                grid[row][col] = null;
                                c.setPosition(row2, col2);
                                moved[row2][col2] = true;
                            } else if (grid[row2][col2] == null) { // otherwise if the cell is empty, then move 
                                grid[row2][col2] = c;
                                grid[row][col] = null;
                                c.setPosition(row2, col2);
                                moved[row2][col2] = true;
                            }
                        }
                    }

                    if (!ate) b.incrementStarve(); // if no ate was eaten, increment the starve counter
                } 
                else { // ant logic 
                    int[] newLocation = c.move(grid);
                    if (newLocation == null) continue;
                    int row2 = newLocation[0], col2 = newLocation[1];
                    if (row2 < 0 || row2 >= 10 || col2 < 0 || col2 >= 10) continue; // boundary check
                    if (grid[row2][col2] != null) continue;
                    // move ant to the new location
                    grid[row2][col2] = c;
                    grid[row][col] = null;
                    c.setPosition(row2, col2);
                    moved[row2][col2] = true;
                }
            }
        }
    }
    
    // -------------------------------------------------------------//
    // breedCreature generic method for ant and beetle
    // -------------------------------------------------------------//
    public static void breedCreature(Creature[][] grid, Class<?> creatureType) {
        // creating a copy of the grid so breeding is based on the current turn, instead of the new born creatures in the same turn
        Creature[][] copy = new Creature[10][10]; 
        for (int r = 0; r < 10; r++)
            for (int c = 0; c < 10; c++)
                copy[r][c] = grid[r][c];

        for (int col = 0; col < 10; col++) {             
            for (int row = 0; row < 10; row++) {
                Creature c = copy[row][col];
                if (c == null || !creatureType.isInstance(c)) continue; // skip wrong creature type and if an empty cell

                int[] spot = c.breed(grid); // determine which spot to breed
                if (spot == null) continue; // skip if breed method was invalid 

                int r2 = spot[0], c2 = spot[1]; 
                if (r2 < 0 || r2 >= 10 || c2 < 0 || c2 >= 10) continue; // check if new location for breeding is valid
                if (grid[r2][c2] != null) continue;

                // breed if the creatureType that was called matches
                if (creatureType == Ant.class) grid[r2][c2] = new Ant(r2, c2, ant);
                else grid[r2][c2] = new Beetle(r2, c2, beetle);

                c.resetTurns(); // reset turns after breed is complete
            }
        }
    }

    // -------------------------------------------------------------//
    // starveBeetle method
    // -------------------------------------------------------------//
    public static void starveBeetle(Creature[][] grid) {
        for (int col = 0; col < 10; col++) {             
            for (int row = 0; row < 10; row++) {
                Creature c = grid[row][col];
                if (c instanceof Beetle && ((Beetle) c).starve()) { // type cast creature to a beetle
                    grid[row][col] = null; // if starve method was valid and the cell was a Beetle, then delete creature to empty cell
                }
            }
        }
    }
}

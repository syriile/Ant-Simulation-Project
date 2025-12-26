import java.util.ArrayList;
import java.util.List;

public abstract class Creature {
    // -------------------------------------------------------------//
    // Data Fields
    // -------------------------------------------------------------//
    protected char symbol;
    protected int row;
    protected int col;
    protected int turnsSurvived;

    // -------------------------------------------------------------//
    // Constructor
    // -------------------------------------------------------------//
    public Creature(int row, int col, char symbol) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
        this.turnsSurvived = 0;
    }

    // -------------------------------------------------------------//
    // Accessors
    // -------------------------------------------------------------//
    public int getRow() {return row;}
    public int getCol() {return col;}
    public char getSymbol() {return symbol;}
    public int getTurnsSurvived() {return turnsSurvived;}
    
    // -------------------------------------------------------------//
    // Mutators
    // -------------------------------------------------------------//
    public void setPosition(int row2, int col2) {
        this.row = row2;
        this.col = col2;
    }
    public void incrementSteps() {turnsSurvived++;}
    public void resetTurns() {turnsSurvived = 0;}

    // -------------------------------------------------------------//
    // Abstract Methods
    // -------------------------------------------------------------//
    public abstract int[] move(Creature[][] grid);
    public abstract int[] breed(Creature[][] grid);

    // -------------------------------------------------------------//
    // findNearest helper method for both beetle and ant class
    // -------------------------------------------------------------//
    protected int findNearest(Creature[][] grid, Class<?> target, char direction) {
        int n = grid.length, distance = Integer.MAX_VALUE;
        switch (direction) {
            case 'N':
                for (int i = row - 1; i >= 0; i--)
                    if (grid[i][col] != null && target.isInstance(grid[i][col])) { 
                        distance = row - i; break; 
                    }
                break;
            case 'S':
                for (int i = row + 1; i < n; i++)
                    if (grid[i][col] != null && target.isInstance(grid[i][col])) { 
                        distance = i - row; break; 
                    }
                break;
            case 'W':
                for (int i = col - 1; i >= 0; i--)
                    if (grid[row][i] != null && target.isInstance(grid[row][i])) { 
                        distance = col - i; break;
                    }
                break;
            case 'E':
                for (int i = col + 1; i < n; i++)
                    if (grid[row][i] != null && target.isInstance(grid[row][i])) { 
                        distance = i - col; break; 
                    }
                break;
        }
        return distance;
    }

    // -------------------------------------------------------------//
    // priorityDirection helper method to choose direction by priority
    // -------------------------------------------------------------//
    protected char priorityDirection(List<Character> result) {
        char[] priority = {'N', 'E', 'S', 'W'};
        for(int i = 0; i < 4; i++) {
            char direction = priority[i]; 
            if(result.contains(direction)) {
                return direction;
            }
        }
        return ' ';
    }

    // -------------------------------------------------------------//
    // collectNearestDirections helper method to store all nearest ant directions
    // -------------------------------------------------------------//
    protected List<Character> collectNearestDirections(int north, int east, int south, int west) {
        int minimum = Math.min(Math.min(north, east), Math.min(south, west));
        char[] direction = {'N', 'E', 'S', 'W'};
        int[] distances = {north, east, south, west};
        List<Character> result = new ArrayList<>();

        for(int i = 0; i < 4; i++)
            if(distances[i] == minimum) result.add(direction[i]);
        return result;
    }
}

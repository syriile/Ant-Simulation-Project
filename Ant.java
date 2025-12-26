import java.util.*;
public class Ant extends Creature {
    // -------------------------------------------------------------//
    // Constructor
    // -------------------------------------------------------------//
    public Ant(int row, int col, char symbol) {
        super(row, col, symbol);
    }

    // -------------------------------------------------------------//
    // Override move method from base class
    // -------------------------------------------------------------//
    @Override
    public int[] move(Creature[][] grid) {
        char direction = nearestDirection(grid);
        if (direction == ' ') return null;
        int[] newLocation = moveAway(direction);
        int row2 = newLocation[0], col2 = newLocation[1];
        if (row2 < 0 || row2 >= 10 || col2 < 0 || col2 >= 10) return null;
        if (grid[row2][col2] != null) return null;
        return newLocation;
    }

    // -------------------------------------------------------------//
    // nearestDirection helper method to find the nearest beetle
    // -------------------------------------------------------------//
    private char nearestDirection(Creature[][] grid) {
        int north = findNearest(grid, Beetle.class, 'N');
        int south = findNearest(grid, Beetle.class, 'S');
        int east = findNearest(grid, Beetle.class, 'E');
        int west = findNearest(grid, Beetle.class, 'W');
        int max = Integer.MAX_VALUE;

        // if no beetles, don't move
        if (north == max && south == max && east == max && west == max) return ' ';

        // find all directions of which that have no beetles.
        List<Character> nearestBeetle = collectNearestDirections(north, east, south, west);

        if (nearestBeetle.size() > 1) {
            List<Character> noBeetle = collectNoBeetleDirections(north, east, south, west);
            if (!noBeetle.isEmpty()) return priorityDirection(noBeetle);
            List<Character> farthestBeetle = collectFarthestDistances(north, east, south, west);
            if (!farthestBeetle.isEmpty()) return priorityDirection(farthestBeetle);
        }

        return priorityDirection(nearestBeetle);
    }

    // -------------------------------------------------------------//
    // collectNoBeetleDirections helper method to store all the directions with no beetles
    // -------------------------------------------------------------//
    private List<Character> collectNoBeetleDirections(int north, int east, int south, int west) {
        int maximum = Integer.MAX_VALUE;
        char[] direction = {'N', 'E', 'S', 'W'};
        int[] distances = {north, east, south, west};
        List<Character> result = new ArrayList<>();

        for(int i = 0; i < 4; i++)
            if(distances[i] == maximum) result.add(direction[i]);
        return result;
    }
    
    // -------------------------------------------------------------//
    // collectFarthestDistances helper method to store all directions with farthest beetle
    // -------------------------------------------------------------//
    private List<Character> collectFarthestDistances(int north, int east, int south, int west) { 
        int farthest = Math.max(Math.max(north, east), Math.max(south, west));
        char[] direction = {'N', 'E', 'S', 'W'};
        int[] distances = {north, east, south, west};
        List<Character> result = new ArrayList<>();

        for(int i = 0; i < 4; i++)
            if(distances[i] == farthest) result.add(direction[i]);
        return result;
    }

    // -------------------------------------------------------------//
    // moveAway helper method to move the ant
    // -------------------------------------------------------------//
    private int[] moveAway(char direction) {
        int row2 = row, col2 = col;
        switch (direction) {
            case 'N': row2 = row + 1; break;
            case 'S': row2 = row - 1; break;
            case 'E': col2 = col - 1; break;
            case 'W': col2 = col + 1; break;
        }
        return new int[]{row2, col2};
    }

    // -------------------------------------------------------------//
    // Override breed method from base class
    // -------------------------------------------------------------//
    @Override
    public int[] breed(Creature[][] grid) {
        // only breed every 3 turns
        if (getTurnsSurvived() % 3 == 0 && getTurnsSurvived() != 0) {
            int[][] directions = {{-1,0},{0,1},{1,0},{0,-1}};
            for (int[] direction : directions) {
                int row2 = row + direction[0], col2 = col + direction[1];
                if (row2 >= 0 && row2 < 10 && col2 >= 0 && col2 < 10 && grid[row2][col2] == null)
                    return new int[]{row2, col2};
            }
        }
        return null;
    }
}

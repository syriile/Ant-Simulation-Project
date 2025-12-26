import java.util.ArrayList;
import java.util.List;

public class Beetle extends Creature {
     // starveCounter for final submission
     private int starveCounter;
    
    // -------------------------------------------------------------//
    // Constructor
    // -------------------------------------------------------------//
    public Beetle(int row, int col, char symbol) {
        super(row, col, symbol);
        this.starveCounter = 0;
    }

    // -------------------------------------------------------------//
    // Override move method from base class
    // -------------------------------------------------------------//
    @Override
    public int[] move(Creature[][] grid) {
        char direction = nearestDirection(grid);
        if (direction == ' ')
            direction = farthestEdge();
        int[] newLocation = moveToward(direction);
        int row2 = newLocation[0], col2 = newLocation[1];

        if (row == 0 && direction == 'N') return null;
        if (row == 9 && direction == 'S') return null;
        if (col == 0 && direction == 'W') return null;
        if (col == 9 && direction == 'E') return null;
        if (row2 < 0 || row2 >= 10 || col2 < 0 || col2 >= 10) return null;
        if (grid[row2][col2] instanceof Beetle) return null;

        return newLocation;
    }


    // -------------------------------------------------------------//
    // nearestDirection helper method to find the nearest direction
    // -------------------------------------------------------------//
    private char nearestDirection(Creature[][] grid) {
        int north = findNearest(grid, Ant.class, 'N');
        int south = findNearest(grid, Ant.class, 'S');
        int east = findNearest(grid, Ant.class, 'E');
        int west = findNearest(grid, Ant.class, 'W');
        int max = Integer.MAX_VALUE;

        // if there are no ants in any direction, move toward the farthest edge
        if (north == max && south == max && east == max && west == max) return farthestEdge();
        List<Character> nearestAnts = collectNearestDirections(north, east, south, west);

        if(nearestAnts.size() > 1) {
            nearestAnts = collectAntNeighbors(grid, nearestAnts);
        }

        return priorityDirection(nearestAnts);
    }

    // -------------------------------------------------------------//
    // farthestEdge helper method to return the farthest edge direction
    // -------------------------------------------------------------//
    private char farthestEdge() {
        int top = row;
        int bottom = 9-row;
        int left = col;
        int right = 9-col;

        // find the farthest direction
        int farthest = Math.max(Math.max(top,bottom), Math.max(left,right));

    if(top == farthest) return 'N';
    else if(right == farthest) return 'E';
    else if(bottom == farthest) return 'S';
    else return 'W'; // return west direction if all other conditions fail
    }

    // -------------------------------------------------------------//
    // collectAntNeighbors helper method to store all the directions with most ant neighbors
    // -------------------------------------------------------------//
    private List<Character> collectAntNeighbors(Creature[][] grid, List<Character> result) {
        int[] counts = getAntNeighborCounts(grid, result);
        int max = getMaxNeighbors(counts);
        return collectMaxDirections(counts, result, max);
    }

    // -------------------------------------------------------------//
    // getAntNeighorCounts method
    // -------------------------------------------------------------//
    private int[] getAntNeighborCounts(Creature[][] grid, List<Character> result) {
        int[] counts = new int[4];
        char[] direction = {'N','E','S','W'};
        int[][] delta = {{-1,0},{0,1},{1,0},{0,-1}};

        for (int i = 0; i < 4; i++) {
            if (!result.contains(direction[i])) continue;
            int r = row + delta[i][0], c = col + delta[i][1];
            int[] location = findFirstAnt(grid, r, c, delta[i]);
            if (location != null) counts[i] = countAntNeighbors(grid, location[0], location[1]);
        }
        return counts;
    }

    // -------------------------------------------------------------//
    // collectMaxDirections method
    // -------------------------------------------------------------//
    private List<Character> collectMaxDirections(int[] counts, List<Character> results, int max) {
        List<Character> maxDirections = new ArrayList<>();
        char[] direction = {'N','E','S','W'};
        for(int i = 0; i < 4; i++) {
            if(counts[i] == max && results.contains(direction[i]))
                maxDirections.add(direction[i]);
        }
        return maxDirections;
    }

    // -------------------------------------------------------------//
    // findFirstAnt method
    // -------------------------------------------------------------//
    private int[] findFirstAnt(Creature[][] grid, int r, int c, int[] delta) {
        while (r >= 0 && r < 10 && c >= 0 && c < 10) {
            if (grid[r][c] instanceof Ant) return new int[]{r, c};
            r += delta[0];
            c += delta[1];
        }
        return null;
    }

    // -------------------------------------------------------------//
    // countAntNeighbors
    // -------------------------------------------------------------//
    private int countAntNeighbors(Creature[][] grid, int r, int c) {
        int count = 0;
        for (int row = -1; row <= 1; row++)
            for (int col = -1; col <= 1; col++) {
                if (row == 0 && col == 0) continue;

                int row2 = r + row, col2 = c + col;
                if (row2 >= 0 && row2 < 10 && col2 >= 0 && col2 < 10 && grid[row2][col2] instanceof Ant)
                    count++;
            }
        return count;
    }

    // -------------------------------------------------------------//
    // getMaxNeighborsMethod
    // -------------------------------------------------------------//
    private int getMaxNeighbors(int[] counts) {
        int max = 0;
        for(int v : counts) {
            if (v > max) 
                max = v;
        }
        return max;
    }

    // -------------------------------------------------------------//
    // moveToward helper method for beetle to move towards the closest ant
    // -------------------------------------------------------------//
    private int[] moveToward(char direction) {
        int row2 = row, col2 = col;
        switch (direction) {
            case 'N': row2 = row - 1; break;
            case 'S': row2 = row + 1; break;
            case 'E': col2 = col + 1; break;
            case 'W': col2 = col - 1; break;
        }
        return new int[]{row2, col2};
    }

    // -------------------------------------------------------------//
    // Override breed method from base class
    // -------------------------------------------------------------//
    @Override
    public int[] breed(Creature[][] grid) {
        if (getTurnsSurvived() % 8 == 0 && getTurnsSurvived() != 0) {
            int[][] directions = {{-1,0},{0,1},{1,0},{0,-1}};
            for (int[] direction : directions) {
                int row2 = row + direction[0], col2 = col + direction[1];
                if (row2 >= 0 && row2 < 10 && col2 >= 0 && col2 < 10 && grid[row2][col2] == null)
                    return new int[]{row2, col2};
            }
        }
        return null;
    }

    // -------------------------------------------------------------//
    // Starve method
    // -------------------------------------------------------------//
    public boolean starve() {return starveCounter >= 5;}
    public void ateAnt() {starveCounter = 0;}
    public void incrementStarve() {starveCounter++;}
}

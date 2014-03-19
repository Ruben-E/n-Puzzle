package nl.rubenernst.han.mad.android.puzzle.domain;

import java.util.*;

/**
 * Created by rubenernst on 18-03-14.
 */
public class Game {
    private List<CurrentPosition> currentPositions = new ArrayList<CurrentPosition>();
    private List<Turn> turns = new ArrayList<Turn>();

    private Integer gridSize;

    public void addCurrentPosition(CurrentPosition currentPosition) {
        currentPositions.add(currentPosition);
    }

    public List<CurrentPosition> getCurrentPositions() {
        Collections.sort(currentPositions);
        return currentPositions;
    }

    public CurrentPosition getCurrentPositionAt(Integer index) {
        return currentPositions.get(index);
    }

    public void addTurn(Turn turn) {
        turns.add(turn);
    }

    public List<Turn> getTurns() {
        return turns;
    }

    public Integer getGridSize() {
        return gridSize;
    }

    public void setGridSize(Integer gridSize) {
        this.gridSize = gridSize;
    }

    public HashMap<Integer, CurrentPosition> getCurrentGrid() {
        HashMap<Integer, CurrentPosition> grid = new HashMap<Integer, CurrentPosition>();

        for (CurrentPosition currentPosition : getCurrentPositions()) {
            grid.put(currentPosition.getPosition(), currentPosition);
        }

        grid.put(getFreeTileNumber(), null);

        return grid;
    }

    public Integer numberOfTiles(){
        return (int) Math.pow(getGridSize(), 2);
    }

    public Integer getFreeTileNumber() {
        List<Integer> tileNumbers = new ArrayList<Integer>();
        for (Position position : currentPositions) {
            tileNumbers.add(position.getPosition());
        }

        for (int i = 0; i < numberOfTiles(); i++) {
            if(!tileNumbers.contains(i)) {
                return i;
            }
        }

        return 0;
    }

    public Boolean allPositionsCorrect() {
        for (CurrentPosition position : currentPositions) {
            if (!position.isCorrectPosition()) {
                return false;
            }
        }

        return true;
    }
}

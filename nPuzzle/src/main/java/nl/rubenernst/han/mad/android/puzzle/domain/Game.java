package nl.rubenernst.han.mad.android.puzzle.domain;

import nl.rubenernst.han.mad.android.puzzle.utils.Constants;
import nl.rubenernst.han.mad.android.puzzle.utils.Difficulty;

import java.util.*;

/**
 * Created by rubenernst on 18-03-14.
 */
public class Game {
    private static final int SHUFFLE_MULTIPLIER = 40;

    private List<CurrentPosition> currentPositions = new ArrayList<CurrentPosition>();
    private List<Turn> turns = new ArrayList<Turn>();

    private Location location;

    private Integer gridSize;

    private Constants.GameState gameState;

    private int puzzleId;

    public Game() {
        setGameState(Constants.GameState.INITIALIZING);
    }

    public void addCurrentPosition(CurrentPosition currentPosition) {
        currentPositions.add(currentPosition);
    }

    public List<CurrentPosition> getCurrentPositions() {
        Collections.sort(currentPositions);
        return currentPositions;
    }

    public CurrentPosition getCurrentPositionAt(Integer index) {
        HashMap<Integer, CurrentPosition> grid = getCurrentGrid();
        return grid.get(index);
    }

    public void addTurn(Turn turn) {
        turns.add(turn);
    }

    public void createTurn() {
        Turn turn = new Turn();
        addTurn(turn);
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

    public Integer numberOfTiles() {
        return (int) Math.pow(getGridSize(), 2);
    }

    public Integer getFreeTileNumber() {
        List<Integer> tileNumbers = new ArrayList<Integer>();
        for (Position position : currentPositions) {
            tileNumbers.add(position.getPosition());
        }

        for (int i = 0; i < numberOfTiles(); i++) {
            if (!tileNumbers.contains(i)) {
                return i;
            }
        }

        return 0;
    }

    public Boolean allPositionsCorrect() {
        for (CurrentPosition position : currentPositions) {
            if (!position.isAtCorrectPosition()) {
                return false;
            }
        }

        setGameState(Constants.GameState.FINISHED);
        return true;
    }

    public List<CurrentPosition> getPositionsAroundFreeTile() {
        List<CurrentPosition> positionsAroundFreeTile = new ArrayList<CurrentPosition>();
        Integer freeTileNumber = getFreeTileNumber();

        for (CurrentPosition currentPosition : getCurrentPositions()) {
            if(currentPosition.isPositionInRange(freeTileNumber)) {
                positionsAroundFreeTile.add(currentPosition);
            }
        }

        return positionsAroundFreeTile;
    }

    public void randomize() {
        for (int i = 0; i < (getPuzzleShuffleNumber()); i++) {
            List<CurrentPosition> positionsAroundFreeTile = getPositionsAroundFreeTile();

            Random rand = new Random();
            CurrentPosition randomPosition = positionsAroundFreeTile.get(rand.nextInt(positionsAroundFreeTile.size()));

            randomPosition.move();
        }
    }

    public int getScore() {
        return getTurns().size();
    }

    public Difficulty getDifficulty() {
        return Difficulty.fromGridSize(gridSize);
    }

    private Integer getPuzzleShuffleNumber() {
        return getGridSize() * SHUFFLE_MULTIPLIER;
    }

    public Constants.GameState getGameState() {
        return gameState;
    }

    public void setGameState(Constants.GameState gameState) {
        this.gameState = gameState;
    }

    public Boolean isPlayable() {
        return getGameState() == Constants.GameState.PLAYABLE;
    }

    public void startGame() {
        setGameState(Constants.GameState.PLAYABLE);
        getTurns().clear();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public int getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(int puzzleId) {
        this.puzzleId = puzzleId;
    }
}

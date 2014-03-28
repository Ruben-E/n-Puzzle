package nl.rubenernst.han.mad.android.puzzle.domain;

import java.util.List;

/**
 * Created by rubenernst on 18-03-14.
 */
public class CurrentPosition extends Position {
    private Image image;
    private CorrectPosition correctPosition;

    public void move() {
        Integer toTile = getGame().getFreeTileNumber();
        if (canMoveToPosition(toTile)) {
            this.setPosition(toTile);
        }
    }

    public void moveToDirection(Directions direction) {
        Integer newPosition = -1;
        switch (direction) {
            case TOP:
                newPosition = getPosition() - getGame().getGridSize();
                break;
            case RIGHT:
                newPosition = getPosition() + 1;
                break;
            case BOTTOM:
                newPosition = getPosition() + getGame().getGridSize();
                break;
            case LEFT:
                newPosition = getPosition() - 1;
                break;
        }

        if (canMoveToPosition(newPosition)) {
            this.setPosition(newPosition);
        }
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Boolean isAtCorrectPosition() {
        return (getPosition() == getCorrectPosition().getPosition());
    }

    public CorrectPosition getCorrectPosition() {
        return correctPosition;
    }

    public void setCorrectPosition(CorrectPosition correctPosition) {
        this.correctPosition = correctPosition;
    }

    public boolean isFreePosition(Integer position) {
        List<CurrentPosition> currentPositions = getGame().getCurrentPositions();

        for (CurrentPosition currentPosition : currentPositions) {
            if (currentPosition.getPosition() == position) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidPosition(Integer position) {
        return ((position >= 0) && (position < getGame().numberOfTiles()));
    }

    public Boolean isPositionInRange(Integer position) {
        if (isAtLeftTopCorner()) {
            if (position == (getPosition() + 1)) {
                return true;
            }

            if (position == (getPosition() + getGame().getGridSize())) {
                return true;
            }
        } else if (isAtRightTopCorner()) {
            if (position == (getPosition() - 1)) {
                return true;
            }

            if (position == (getPosition() + getGame().getGridSize())) {
                return true;
            }
        } else if (isAtLeftBottomCorner()) {
            if (position == (getPosition() + 1)) {
                return true;
            }

            if (position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        } else if (isAtRightBottomCorner()) {
            if (position == (getPosition() - 1)) {
                return true;
            }

            if (position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        } else if (isAtLeftBorder()) {
            if (position == (getPosition() - getGame().getGridSize())) {
                return true;
            }

            if (position == (getPosition() + getGame().getGridSize())) {
                return true;
            }

            if (position == (getPosition() + 1)) {
                return true;
            }
        } else if (isAtRightBorder()) {
            if (position == (getPosition() - getGame().getGridSize())) {
                return true;
            }

            if (position == (getPosition() + getGame().getGridSize())) {
                return true;
            }

            if (position == (getPosition() - 1)) {
                return true;
            }
        } else if (isAtTopBorder()) {
            if (position == (getPosition() - 1)) {
                return true;
            }

            if (position == (getPosition() + 1)) {
                return true;
            }

            if (position == (getPosition() + getGame().getGridSize())) {
                return true;
            }
        } else if (isAtBottomBorder()) {
            if (position == (getPosition() - 1)) {
                return true;
            }

            if (position == (getPosition() + 1)) {
                return true;
            }

            if (position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        } else {
            if (position == (getPosition() - 1)) {
                return true;
            }

            if (position == (getPosition() + 1)) {
                return true;
            }

            if (position == (getPosition() + getGame().getGridSize())) {
                return true;
            }

            if (position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        }

        return false;
    }

    public Boolean canMoveToPosition(Integer position) {
        return (isValidPosition(position) && isFreePosition(position) && isPositionInRange(position));
    }

    @Override
    public void setPosition(Integer position) {
        super.setPosition(position);

        getGame().createTurn();
    }
}

package nl.rubenernst.han.mad.android.puzzle.domain;

/**
 * Created by rubenernst on 18-03-14.
 */
public class Position implements Comparable {
    protected Game game;
    private Integer position;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public int compareTo(Object o) {
        Position otherPosition = (Position) o;
        return this.getPosition() - otherPosition.getPosition();
    }

    public Boolean canMoveToPosition(Integer position) {
        if(isAtLeftTopCorner()) {
            if(position == (getPosition() + 1)) {
                return true;
            }

            if(position == (getPosition() + getGame().getGridSize())) {
                return true;
            }
        } else if (isAtRightTopCorner()) {
            if(position == (getPosition() - 1)) {
                return true;
            }

            if(position == (getPosition() + getGame().getGridSize())) {
                return true;
            }
        } else if (isAtLeftBottomCorner()) {
            if(position == (getPosition() + 1)) {
                return true;
            }

            if(position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        } else if (isAtRightBottomCorner()) {
            if(position == (getPosition() - 1)) {
                return true;
            }

            if(position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        } else if (isAtLeftBorder()) {
            if(position == (getPosition() - getGame().getGridSize())) {
                return true;
            }

            if(position == (getPosition() + getGame().getGridSize())) {
                return true;
            }

            if(position == (getPosition() + 1)) {
                return true;
            }
        } else if (isAtRightBorder()) {
            if(position == (getPosition() - getGame().getGridSize())) {
                return true;
            }

            if(position == (getPosition() + getGame().getGridSize())) {
                return true;
            }

            if(position == (getPosition() - 1)) {
                return true;
            }
        } else if (isAtTopBorder()) {
            if(position == (getPosition() - 1)) {
                return true;
            }

            if(position == (getPosition() + 1)) {
                return true;
            }

            if(position == (getPosition() + getGame().getGridSize())) {
                return true;
            }
        } else if (isAtBottomBorder()) {
            if(position == (getPosition() - 1)) {
                return true;
            }

            if(position == (getPosition() + 1)) {
                return true;
            }

            if(position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        } else {
            if(position == (getPosition() - 1)) {
                return true;
            }

            if(position == (getPosition() + 1)) {
                return true;
            }

            if(position == (getPosition() + getGame().getGridSize())) {
                return true;
            }

            if(position == (getPosition() - getGame().getGridSize())) {
                return true;
            }
        }

        return false;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getRowNumber() {
        return (int) Math.ceil((double) this.getPosition() / (double) getGame().getGridSize());
    }

    public Boolean isAtBorder() {
        if(isAtBottomBorder() || isAtLeftBorder() || isAtRightBorder() || isAtTopBorder()) {
            return true;
        }

        return false;
    }

    public Boolean isAtTopBorder() {
        if (getRowNumber() == 0) {
            return true;
        }

        return false;
    }

    public Boolean isAtLeftBorder() {
        double calculation = this.getPosition() / (double) getGame().getGridSize();
        if (calculation % getGame().getGridSize() == 0) {
            return true;
        }

        return false;
    }

    public Boolean isAtBottomBorder() {
        if (getRowNumber() == getGame().getGridSize()) {
            return true;
        }

        return false;
    }

    public Boolean isAtRightBorder() {
        double calculation = (this.getPosition() + 1) / (double) getGame().getGridSize();
        if (calculation % getGame().getGridSize() == 0) {
            return true;
        }

        return false;
    }

    public Boolean isAtLeftTopCorner() {
        return (isAtTopBorder() && isAtLeftBorder());
    }

    public Boolean isAtRightTopCorner() {
        return (isAtTopBorder() && isAtRightBorder());
    }

    public Boolean isAtLeftBottomCorner() {
        return (isAtBottomBorder() && isAtLeftBorder());
    }

    public Boolean isAtRightBottomCorner() {
        return (isAtBottomBorder() && isAtRightBorder());
    }
}

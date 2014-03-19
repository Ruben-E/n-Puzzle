package nl.rubenernst.han.mad.android.puzzle.domain;

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
}

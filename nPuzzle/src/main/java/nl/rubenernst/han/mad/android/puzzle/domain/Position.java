package nl.rubenernst.han.mad.android.puzzle.domain;

/**
 * Created by rubenernst on 18-03-14.
 */
public class Position implements Comparable {
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
}

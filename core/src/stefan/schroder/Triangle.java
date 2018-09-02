package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class Triangle extends Piece{
    private boolean[][] shape;
    private int[] center;
    //private boolean[][] shape;

    public Triangle() {
        setColour(Color.PURPLE);

        boolean t = true;
        boolean f = false;
        shape = new boolean[][]{{f, f, f},
                                {t, t, t},
                                {f, t, f}};//this view in inverted
        setShape(shape);
        center = new int[]{1, 1};
        setCenter(center);
    }
}


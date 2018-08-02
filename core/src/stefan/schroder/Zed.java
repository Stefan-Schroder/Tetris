package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class Zed extends Piece{
    private Color colour;
    private boolean[][] shape;
    private int[] center;

    public Zed() {
        setColour(Color.RED);

        boolean t = true;
        boolean f = false;
        shape = new boolean[][]{{f, f, f},
                                {f, t, t},
                                {t, t, f}};//this view in inverted
        setShape(shape);
        center = new int[]{1, 1};
        setCenter(center);
    }

}


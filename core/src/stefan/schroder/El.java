package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class El extends Piece{
    private boolean[][] shape;
    private int[] center;

    public El() {
        setColour(Color.ORANGE);

        boolean t = true;
        boolean f = false;
        shape = new boolean[][]{{f, f, f},
                                {t, t, t},
                                {f, f, t}};//this view in inverted
        setShape(shape);
        center = new int[]{1, 1};
        setCenter(center);
    }

}


package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class Es extends Piece{
    private boolean[][] shape;
    private int[] center;

    public Es() {
        setColour(Color.GREEN);

        boolean t = true;
        boolean f = false;
        shape = new boolean[][]{{f, f, f},
                                {t, t, f},
                                {f, t, t}};//this view in inverted
        setShape(shape);
        center = new int[]{1, 1};
        setCenter(center);
    }

}


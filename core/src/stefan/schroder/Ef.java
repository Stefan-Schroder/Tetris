package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class Ef extends Piece{
    private Color colour;
    private boolean[][] shape;
    private int[] center;

    public Ef(){
        setColour(Color.BLUE);

        boolean t = true;
        boolean f = false;
        shape = new boolean[][]{{f, f, f},
                                {t, t, t},
                                {t, f, f}};//this view in inverted
        setShape(shape);
        center = new int[]{1, 1};
        setCenter(center);
    }

}


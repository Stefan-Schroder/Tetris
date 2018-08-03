package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class Square extends Piece {
    private Color colour;
    private boolean[][] shape;
    private int[] center;

    public Square() {
        setColour(Color.YELLOW);

        boolean t = true;
        boolean f = false;
        boolean[][] shape = {	{f,f,f},
                                {f,t,t},
                                {f,t,t}};//this view in inverted
        setShape(shape);
        center = new int[]{1, 1};
        setCenter(center);
    }

    public void Rotate(){}

}

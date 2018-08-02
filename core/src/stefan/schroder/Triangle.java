package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public class Triangle extends Piece{
    private Color colour;
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

    public void Rotate(){
        //copy array
        boolean[][] placeholder = new boolean[shape.length][shape[0].length];
        for(int i=0;i<shape.length;i++){
            for(int j=0;j<shape[i].length;j++){
                placeholder[i][j] = shape[i][j];
            }
        }
        //rotate
        for(int i=0;i<shape.length;i++){
            for(int j=0;j<shape[i].length;j++){
                shape[i][j] = placeholder[j][shape.length-i-1];
            }
        }
    }
}


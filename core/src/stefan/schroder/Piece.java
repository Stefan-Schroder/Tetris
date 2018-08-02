package stefan.schroder;

import com.badlogic.gdx.graphics.Color;

public abstract class Piece {
    private Color colour;
    private boolean[][] shape;
    private int[] center;
    private int[] position;

    public Piece(){
        setPosition(new int[]{5,20});
    }

    public void setCenter(int[] center){
        this.center = center;
    }

    public int[] getCenter(){
        return center;
    }

    public void setPosition(int[] position){
       this.position = position;
    }

    public int[] getPosition(){ return position; }

    public void setShape(boolean[][] shape){
        this.shape = shape;
    }

    public boolean[][] getShape(){
        return shape;
    }

    public void setColour(Color colour){
        this.colour = colour;
    }

    public Color getColour(){
        return colour;
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

    public void moveRight(){
        position[0]++;
        if(position[0]>9){
            position[0]=0;
        }
    }

    public void moveLeft(){
        position[0]--;
        if(position[0]<0){
            position[0]=9;
        }
    }

    public void moveDown(){
        position[1]--;
    }
}

package stefan.schroder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.Random;

public class ProcessThread extends Thread{
    private Thread thread;
    private boolean running;

    private Piece currentPiece;
    private Piece nextPiece;

    private boolean[][] map;

    private long lastDropTime;
    private long lastPressTime;

    private Random random = new Random();

    private int score;

    private Sound blockDropSound;
    private Sound blockBreakSound;
    private Sound blockWooshSound;
    private Sound moveClickSound;
    private Music music;

    private long nanoTime;
    private boolean nextBlock;

    public ProcessThread(){
        running = true;

        blockDropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Blockland.wav"));
        blockBreakSound = Gdx.audio.newSound(Gdx.files.internal("sounds/poof3.wav"));
        blockWooshSound = Gdx.audio.newSound(Gdx.files.internal("sounds/woosh.wav"));
        moveClickSound = Gdx.audio.newSound((Gdx.files.internal("sounds/click.wav")));

        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Music.mp3"));
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        currentPiece = new El();
        nextPiece = new El();

        randomBlock();
        randomBlock();

        map = new boolean[21][10];
        lastDropTime = 0l;

        score = 0;
        nextBlock = true;
    }

    private boolean CheckSideCollision(boolean checkLeft){
        int check = 1;
        if(checkLeft) check = -1;

        int[] pos = currentPiece.getPosition();
        int[] centerpos = currentPiece.getCenter();
        boolean[][] shape = currentPiece.getShape();

        //collision with other blocks
        for(int y=0; y<shape.length; y++){
            for(int x=0; x<shape[y].length; x++){
                if(shape[y][x]){
                    int posx = x+pos[0]-centerpos[0]+check;
                    //wrapping
                    if(posx<0){
                        posx+=10;
                    }
                    if(posx>9){
                        posx-=10;
                    }
                    int posy = y+pos[1]-centerpos[1];
                    if(posy<20 && map[posy][posx]){
                        return true;
                    }
                }
            }
        }
        return false;

    }

    private boolean CheckCollisions(){
        boolean collision = false;
        int[] pos = currentPiece.getPosition();
        int[] centerpos = currentPiece.getCenter();
        boolean[][] shape = currentPiece.getShape();
        //check floor
        int lowestPos = 0;
        outer:
        for(int i=0;i<shape.length;i++){
            for(int j=0;j<shape[i].length;j++){
                if(shape[i][j]){
                    lowestPos=i;
                    break outer;
                }
            }
        }
        lowestPos = pos[1]+lowestPos;
        if(lowestPos<=1){
            collision = true;
        }

        //collision with other blocks
        if(!collision){
            for(int y=0; y<shape.length; y++){
                for(int x=0; x<shape[y].length; x++){
                    if(shape[y][x]){
                        int posx = x+pos[0]-centerpos[0];
                        //wrapping
                        if(posx<0){
                            posx+=10;
                        }
                        if(posx>9){
                            posx-=10;
                        }
                        int posy = y+pos[1]-centerpos[1];
                        if(posy-1<=20 && map[posy-1][posx]){
                            collision = true;
                        }
                    }
                }
            }
        }
        //make stone
        if(collision){
            return true;
        }
        return false;

    }

    private void TurnToStone() {
        blockDropSound.play();
        int[] pos = currentPiece.getPosition();
        int[] centerpos = currentPiece.getCenter();
        boolean[][] shape = currentPiece.getShape();

        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x]) {
                    int xposition = (pos[0] - centerpos[0] + x);
                    //wrapping
                    if (xposition < 0) {
                        xposition += 10;
                    }
                    if (xposition > 9) {
                        xposition -= 10;
                    }
                    int yposition = pos[1] - centerpos[1] + y;
                    map[yposition][xposition] = true;
                }
            }
        }
    }

    private void BreakLine(int line){
        blockDropSound.stop();
        blockBreakSound.play();
        for(int y=line; y<map.length-1; y++){
            for(int x=0; x<map[y].length; x++){
                map[y][x] = map[y+1][x];
            }
        }

    }

    private void randomBlock(){
        currentPiece = nextPiece;

        switch (random.nextInt(6)) {
            case 0:
                nextPiece = new Ef();
                break;
            case 1:
                nextPiece = new El();
                break;
            case 2:
                nextPiece = new Es();
                break;
            case 3:
                nextPiece = new Line();
                break;
            case 4:
                nextPiece = new Square();
                break;
            case 5:
                nextPiece = new Triangle();
                break;
            case 6:
                nextPiece = new Zed();
                break;
            default:
                nextPiece = new Ef();

        }

    }

    public void run(){
        while(running) {
            if(currentPiece!=null) {

                //input
                if (Gdx.input.isKeyPressed(Input.Keys.UP) && nanoTime - lastPressTime > 150000000l) {
                    lastPressTime = nanoTime;
                    moveClickSound.play(0.5f);
                    currentPiece.Rotate();
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && nanoTime - lastPressTime > 100000000l) {
                    lastPressTime = nanoTime;
                    moveClickSound.play(0.5f);
                    if (!CheckCollisions()) currentPiece.moveDown();
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && nanoTime - lastPressTime > 150000000l) {
                    lastPressTime = nanoTime;
                    moveClickSound.play(0.5f);
                    if (!CheckSideCollision(false)) currentPiece.moveRight();
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && nanoTime - lastPressTime > 150000000l) {
                    lastPressTime = nanoTime;
                    moveClickSound.play(0.5f);
                    if (!CheckSideCollision(true)) currentPiece.moveLeft();
                }

                //yeet
                else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    if (CheckSideCollision(true)) {
                        blockWooshSound.play(0.8f);
                        TurnToStone();
                        nextBlock = true;
                        currentPiece = null;
                    } else {
                        currentPiece.moveLeft();
                    }
                }
                else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    if (CheckSideCollision(false)) {
                        blockWooshSound.play(0.8f);
                        TurnToStone();
                        nextBlock = true;
                        currentPiece = null;

                    } else {
                        currentPiece.moveRight();
                    }
                }
                else if (currentPiece.getPosition()[1]<=19 && Gdx.input.isKeyPressed(Input.Keys.S)) {
                    if (CheckCollisions()) {
                        blockWooshSound.play(0.8f);
                        TurnToStone();
                        nextBlock = true;
                        currentPiece = null;
                    }else{
                        currentPiece.moveDown();
                    }
                }
            }

            //drop
            if (nanoTime - lastDropTime > 200000000l) {
                lastDropTime = nanoTime;
                if(nextBlock){
                    nextBlock = false;
                    randomBlock();
                }
                if (CheckCollisions()) {
                    TurnToStone();
                    nextBlock = true;
                    currentPiece = null;
                } else {
                    currentPiece.moveDown();
                }
            }

            //check if full line
            int lineNumbers = 0;
            for (int y = 0; y < map.length; y++) {
                boolean fullLine = true;
                for (int x = 0; x < map[y].length; x++) {
                    //check loss
                    if (y == 21 && map[y][x]) {
                        System.out.println("lost");
                    }
                    //check line
                    if (!map[y][x]) {
                        fullLine = false;
                        break;
                    }
                }
                if (fullLine) {
                    BreakLine(y);
                    lineNumbers++;
                }
            }
            score += lineNumbers * lineNumbers * lineNumbers;
        }
    }

    public void tickTime(long time){
        this.nanoTime = time;
    }

    public Piece getCurrentPiece(){
        return currentPiece;
    }

    public Piece getNextPiece(){
        return nextPiece;
    }

    public boolean[][] getMap(){
        return map;
    }

    public int getScore(){
        return score;
    }

    public void dispose(){
        blockBreakSound.dispose();
        blockDropSound.dispose();
        moveClickSound.dispose();
        running = false;
    }

    public void startThread(){
        if(thread==null){
            System.out.print("Starting Thread");
            thread = new Thread(this, "Process Thread");
            thread.start();
        }

    }

}

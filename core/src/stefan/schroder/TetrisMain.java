package stefan.schroder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;
import java.sql.Time;
import java.util.Random;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;

public class TetrisMain extends ApplicationAdapter {
    private Texture blockImage;

    private Sound blockDropSound;
	private Sound blockBreakSound;
	private Sound moveClickSound;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Rectangle block;


    //define blocks
	private Piece currentPiece;

    private boolean[][] map;

    private long lastDropTime;
	private long lastPressTime;

    private Random random = new Random();

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private int score;

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

    public boolean CheckCollisions(){
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
						if(map[posy-1][posx]){
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

	@Override
	public void create () {
    	shapeRenderer = new ShapeRenderer();
	    blockImage = new Texture(Gdx.files.internal("sprites/Block.png"));

	    blockDropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Blockland.wav"));
	    blockBreakSound = Gdx.audio.newSound(Gdx.files.internal("sounds/poof3.wav"));
	    moveClickSound = Gdx.audio.newSound((Gdx.files.internal("sounds/click.wav")));

	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 120, 215);

	    batch = new SpriteBatch();


	    //setting up object
		block = new Rectangle();
		block.x = 50;
		block.y = 100;
		block.width = 10;
		block.height = 10;

		currentPiece = new El();

		map = new boolean[21][10];
		lastDropTime = 0l;

		font = new BitmapFont();
		score = 0;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		//shape draw
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0.5f,0.5f,1f,0.8f);
		//shapeRenderer.line(x, y, x2, y2);
		shapeRenderer.rect(0, 0, 10, 210);
		shapeRenderer.rect(110, 0, 10, 210);
		shapeRenderer.end();

		//batch draw
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//borders
		//draw square
		boolean [][] shape = currentPiece.getShape();
		int[] shapeCenter = currentPiece.getCenter();
		int[] shapePosition = currentPiece.getPosition();
		for(int y=0;y<shape.length;y++){
			for(int x=0;x<shape[y].length;x++){
				if(shape[y][x]){
					//batch.setColor(Color.PINK);
					batch.setColor(currentPiece.getColour());
					int yposition = (shapePosition[1]-shapeCenter[1]+y);
					int xposition = (shapePosition[0]-shapeCenter[0]+x);
					//wrapping
					if(xposition<0){
						batch.draw(blockImage,xposition*10+10,yposition*10, 10f, 10f);
						xposition+=10;
					}
					if(xposition>9){
						batch.draw(blockImage,xposition*10+10,yposition*10, 10f, 10f);
						xposition-=10;
					}
					if(xposition==0){
						batch.draw(blockImage, xposition*10+110, yposition*10, 10f, 10f);
					}
					if(xposition==9){
						batch.draw(blockImage, 0, yposition*10, 10f, 10f);
					}

					batch.setColor(currentPiece.getColour());
					batch.draw(blockImage,xposition*10+10,yposition*10, 10f, 10f);
				}
			}
		}
		batch.setColor(Color.GRAY);
		for(int y=0; y<map.length; y++){
			for(int x=0; x<map[y].length; x++){
				if(map[y][x]) batch.draw(blockImage, x*10+10, y*10, 10f, 10f);
			}
		}
		batch.setColor(Color.PINK);
		//batch.setColor(currentPiece.getColour());
		for(int y=0; y<map.length; y++){
            if(map[y][0]) batch.draw(blockImage, 10*10+10, y*10, 10f, 10f);
			if(map[y][9]) batch.draw(blockImage, 0, y*10, 10f, 10f);
		}
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0.1f,0.1f,0.1f,1);
		shapeRenderer.rect(0, 205, 120, 10);
		shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Score: "+String.valueOf(score), 5f, 212f);
		font.getData().setScale(.5f);
		batch.end();
		//input
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP) || (Gdx.input.isKeyPressed(Input.Keys.UP) && TimeUtils.nanoTime()-lastPressTime>150000000l)) {
			lastPressTime=TimeUtils.nanoTime();
			moveClickSound.play(0.5f);
			currentPiece.Rotate();
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || (Gdx.input.isKeyPressed(Input.Keys.DOWN) && TimeUtils.nanoTime()-lastPressTime>100000000l)) {
			lastPressTime=TimeUtils.nanoTime();
			moveClickSound.play(0.5f);
			if(!CheckCollisions()) currentPiece.moveDown();
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && TimeUtils.nanoTime()-lastPressTime>120000000l)) {
			lastPressTime=TimeUtils.nanoTime();
			moveClickSound.play(0.5f);
			if(!CheckSideCollision(false)) currentPiece.moveRight();
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || (Gdx.input.isKeyPressed(Input.Keys.LEFT) && TimeUtils.nanoTime()-lastPressTime>120000000l)) {
			lastPressTime=TimeUtils.nanoTime();
			moveClickSound.play(0.5f);
			if(!CheckSideCollision(true)) currentPiece.moveLeft();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			if (CheckSideCollision(true)) {
				//turn to stone
				TurnToStone();
			} else {
				currentPiece.moveLeft();
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (CheckSideCollision(false)) {
				//turn to stone
				TurnToStone();
			} else {
				currentPiece.moveRight();
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			if (!CheckCollisions()) currentPiece.moveDown();
		}



		if(TimeUtils.nanoTime() - lastDropTime > 200000000l) {
			lastDropTime = TimeUtils.nanoTime();
			if (CheckCollisions()) {
				TurnToStone();
				switch (random.nextInt(6)) {
					case 0:
						currentPiece = new Ef();
						break;
					case 1:
						currentPiece = new El();
						break;
					case 2:
						currentPiece = new Es();
						break;
					case 3:
						currentPiece = new Line();
						break;
					case 4:
						currentPiece = new Square();
						break;
					case 5:
						currentPiece = new Triangle();
						break;
					case 6:
						currentPiece = new Zed();
						break;
					default:
						currentPiece = new Ef();

				}


			} else {
				currentPiece.moveDown();
			}
		}

		//check if full line
        int lineNumbers = 0;
		for(int y=0;y<map.length;y++){
			boolean fullLine = true;
			for(int x=0;x<map[y].length;x++){
			    //check loss
				if(y==21 && map[y][x]){
					System.out.println("lost");
				}
				//check line
				if(!map[y][x]){
					fullLine = false;
					break;
				}
			}
			if(fullLine){
				BreakLine(y);
				lineNumbers++;
			}
		}
		score+= lineNumbers*lineNumbers*lineNumbers;
	}

	@Override
	public void dispose () {
    	batch.dispose();
    	blockImage.dispose();
    	shapeRenderer.dispose();
	}
}

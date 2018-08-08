package stefan.schroder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;

public class TetrisMain extends ApplicationAdapter {
    private Texture blockImage;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Rectangle block;

	private ProcessThread process;
    //define blocks

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

	@Override
	public void create () {
    	shapeRenderer = new ShapeRenderer();
	    blockImage = new Texture(Gdx.files.internal("sprites/Block.png"));

	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 120, 215);

	    batch = new SpriteBatch();

	    //setting up object
		block = new Rectangle();
		block.x = 50;
		block.y = 100;
		block.width = 10;
		block.height = 10;

		font = new BitmapFont();

		process = new ProcessThread();
		process.startThread();
	}

	@Override
	public void render () {
	    process.tickTime(TimeUtils.nanoTime());
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();

		//shape draw
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0.8f,0.8f,0.8f,0.3f);
		shapeRenderer.rect(0, 0, 10, 210);
		shapeRenderer.rect(110, 0, 10, 210);
		shapeRenderer.end();

		//batch draw
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//borders
		//draw square
		Piece currentPiece = process.getCurrentPiece();
        if(currentPiece!=null) {
			boolean[][] shape = currentPiece.getShape();
			int[] shapeCenter = currentPiece.getCenter();
			int[] shapePosition = currentPiece.getPosition();

			for (int y = 0; y < shape.length; y++) {
				for (int x = 0; x < shape[y].length; x++) {
					if (shape[y][x]) {
						batch.setColor(currentPiece.getColour());

						int yposition = (shapePosition[1] - shapeCenter[1] + y);
						int xposition = (shapePosition[0] - shapeCenter[0] + x);
						//wrapping
						if (xposition < 0) {
							batch.draw(blockImage, xposition * 10 + 10, yposition * 10, 10f, 10f);
							xposition += 10;
						}
						if (xposition > 9) {
							batch.draw(blockImage, xposition * 10 + 10, yposition * 10, 10f, 10f);
							xposition -= 10;
						}
						if (xposition == 0) {
							batch.draw(blockImage, xposition * 10 + 110, yposition * 10, 10f, 10f);
						}
						if (xposition == 9) {
							batch.draw(blockImage, 0, yposition * 10, 10f, 10f);
						}

						if (yposition<=20){
							batch.draw(blockImage, xposition * 10 + 10, yposition * 10, 10f, 10f);
						}
					}
				}
			}
		}
		//solid blocks
		batch.setColor(Color.GRAY);
		for(int y=0; y<process.getMap().length; y++){
			for(int x=0; x<process.getMap()[y].length; x++){
				if(process.getMap()[y][x]) batch.draw(blockImage, x*10+10, y*10, 10f, 10f);
			}
		}
		//mirror solids
		batch.setColor(Color.PINK);
		for(int y=0; y<process.getMap().length; y++){
            if(process.getMap()[y][0]) batch.draw(blockImage, 10*10+10, y*10, 10f, 10f);
			if(process.getMap()[y][9]) batch.draw(blockImage, 0, y*10, 10f, 10f);
		}
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0.1f,0.1f,0.1f,1);
		shapeRenderer.rect(0, 205, 120, 10);
		shapeRenderer.end();

        batch.begin();
		//next shape
		boolean [][] nextShape = process.getNextPiece().getShape();
		int[] nextShapeCenter = process.getNextPiece().getCenter();

		for(int y=0;y<nextShape.length;y++){
			for(int x=0;x<nextShape[y].length;x++){
				if(nextShape[y][x]){
				    float scale = 4;
					batch.setColor(process.getNextPiece().getColour());

					int yposition = (-nextShapeCenter[1]+y);
					int xposition = (-nextShapeCenter[0]+x);

					batch.draw(blockImage,100+xposition*scale,205+yposition*scale, scale, scale);
				}
			}
		}

		if(!process.getRunning()){
			font.getData().setScale(1f);
			font.draw(batch, "You Win't!", 25f, 150f);
			font.getData().setScale(.5f);
			font.draw(batch, "Score: "+String.valueOf(process.getScore())+"\nPress R to restart.", 25f, 130f);
			if(Gdx.input.isKeyPressed(Input.Keys.R)){
			    process.dispose();
				process = new ProcessThread();
				process.startThread();
			}

		}else{
			font.getData().setScale(.5f);
			font.draw(batch, "Score: "+String.valueOf(process.getScore()), 5f, 212f);
		}
		batch.end();


	}

	@Override
	public void dispose () {
    	batch.dispose();
    	blockImage.dispose();
    	shapeRenderer.dispose();
    	process.dispose();
	}
}

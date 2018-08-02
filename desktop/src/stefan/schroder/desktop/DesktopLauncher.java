package stefan.schroder.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import stefan.schroder.TetrisMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tetris";
		config.width = 240;
		config.height = 425;
		new LwjglApplication(new TetrisMain(), config);
	}
}

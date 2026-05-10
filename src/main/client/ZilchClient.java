package client;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ui.gdx.ZilchGdxGame;

import java.util.Arrays;


public class ZilchClient
{
	/**
	 * Entry point for the visual desktop game. Pass {@code cli} to run the
	 * preserved console launcher from this branch.
	 *
	 * @param args Optional mode selector: {@code cli [readConfig|writeConfig]}.
	 */
	public static void main(String[] args) {
		if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
			ZilchCliClient.main(Arrays.copyOfRange(args, 1, args.length));
			return;
		}

		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("Zilch Basic");
		configuration.setWindowedMode(1180, 760);
		configuration.setWindowSizeLimits(920, 620, -1, -1);
		configuration.setResizable(true);
		configuration.useVsync(true);
		configuration.setForegroundFPS(60);

		new Lwjgl3Application(new ZilchGdxGame(), configuration);
	}
}

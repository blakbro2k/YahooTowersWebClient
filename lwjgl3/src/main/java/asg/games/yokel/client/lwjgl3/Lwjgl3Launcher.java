package asg.games.yokel.client.lwjgl3;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALSound;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.github.czyzby.autumn.fcs.scanner.DesktopClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.czyzby.websocket.CommonWebSockets;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.YahooTowersClient;
import asg.games.yokel.client.configuration.preferences.BootstrapConfig;
import asg.games.yokel.client.managers.GameNetFactory;
import asg.games.yokel.client.utils.SoundUtil;
import asg.games.yokel.client.utils.UIUtil;


/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
	//private static final Logger logger = LoggerFactory.getLogger(Lwjgl3Launcher.class);
    public static final String DEBUG_KEY = "debugMode";

	public static void main(String[] args) {
		if (args != null) {
			for (String arg : args) {
				if ("texturepacker".equalsIgnoreCase((arg))) {
					// Create two run configurations
					// 1. For texture packing. Pass 'texturepacker' as argument and use desktop/src
					//    as working directory
					// 2. For playing game with android/assets as working directory
					TexturePacker.Settings settings = new TexturePacker.Settings();
					settings.maxWidth = GlobalConstants.MAX_WIDTH;
					settings.maxHeight = GlobalConstants.MAX_HEIGHT;
					TexturePacker.process(settings, GlobalConstants.SOURCE_ASSETS_FOLDER_PATH,
							"U:\\YahooTowersWebClient\\assets\\ui\\game", GlobalConstants.GAME_ATLAS_NAME);

					System.exit(0);
				}

				// Set debug mode as system property
				if ("-debug".equalsIgnoreCase(arg)) {
                    System.out.println("Setting Debug Setting");
                    BootstrapConfig.setDebugMode(true);
				}

				// Parse JWT from -jwt=...
				if (arg.startsWith("-jwt=")) {
					String jwt = arg.substring("-jwt=".length());
                    BootstrapConfig.setJwtToken(jwt);
				}

            }
		}

		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		SoundUtil lwjgl3SoundUtil = new Lwjgl3SoundUtil();
		GameNetFactory.registerClientManager(new KryoNetworkManager(5000, "localhost", 8080, 28080));
		GameNetFactory.registerWebClientManager(new Lwjgl3WebSocketNetworkManager("localhost"));
		UIUtil.getInstance().setSoundUtil(lwjgl3SoundUtil);
		CommonWebSockets.initiate();
		return new Lwjgl3Application(new AutumnApplication(new DesktopClassScanner(), YahooTowersClient.class),
				getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("YahooTowersClient");
		configuration.setWindowedMode(YahooTowersClient.WIDTH, YahooTowersClient.HEIGHT);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		return configuration;
	}

	private static class Lwjgl3SoundUtil extends SoundUtil {
		@Override
		protected float getDuration(Sound soundFile) {
			float duration = -1;
			if(soundFile instanceof OpenALSound){
                duration = ((OpenALSound) soundFile).duration();
			}
            return duration;
		}
	}
}
package asg.games.yokel.client.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPISound;
import com.github.czyzby.autumn.gwt.scanner.GwtClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.czyzby.websocket.GwtWebSockets;

import asg.games.yokel.client.YahooTowersClient;
import asg.games.yokel.client.utils.SoundUtil;
import asg.games.yokel.client.utils.UIUtil;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig() {
		GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(YahooTowersClient.WIDTH, YahooTowersClient.HEIGHT);
		return configuration;
	}

	@Override
	public ApplicationListener createApplicationListener() {
		SoundUtil gwtSoundUtil = new GwtSoundUtil();
		UIUtil.getInstance().setSoundUtil(gwtSoundUtil);
		GwtWebSockets.initiate();
		return new AutumnApplication(new GwtClassScanner(), YahooTowersClient.class);
	}

	private static class GwtSoundUtil extends SoundUtil {
		@Override
		protected float getDuration(Sound soundFile) {
			if (soundFile instanceof WebAudioAPISound) {
				Gdx.app.log("SoundFXService", "Playing  " + soundFile.getClass());
				return getDurationFromJSFile((WebAudioAPISound) soundFile);
			} else {
				return -1;
			}
		}

		private native int getDurationFromJSFile(WebAudioAPISound sound) /*{
			//Console.log("getDurationFromJSFile");
			//Console.log("audioBuffer" + audioBuffer);
			//var audioBuffer = sound::audioBuffer;

			var duration = -10
			//if(audioBuffer != null) {
				//duration = audioBuffer.duration;
			//}
			return duration;
		}*/;
	}
}
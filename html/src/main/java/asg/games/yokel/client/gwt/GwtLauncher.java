package asg.games.yokel.client.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
		return new GwtApplicationConfiguration(YahooTowersClient.WIDTH, YahooTowersClient.HEIGHT);
	}

	@Override
	public ApplicationListener createApplicationListener() {
		SoundUtil gwtSoundUtil = new GwtSoundUtil();
		UIUtil.getInstance().setSoundUtil(gwtSoundUtil);
		GwtWebSockets.initiate();
		Gdx.input.setCatchKey(Input.Keys.SPACE, true);
		Gdx.input.setCatchKey(Input.Keys.DOWN, true);
		Gdx.input.setCatchKey(Input.Keys.UP, true);
		return new AutumnApplication(new GwtClassScanner(), YahooTowersClient.class);
	}

	private static class GwtSoundUtil extends SoundUtil {
		@Override
		protected float getDuration(Sound soundFile) {
			int duration = -1;
			if (soundFile instanceof WebAudioAPISound) {
				try {
					duration = getDurationFromJSAudio((WebAudioAPISound) soundFile);
				} catch (Exception e) {
					Gdx.app.log("SoundFXService", "Logging unknown JS exception  " + e.getMessage());
					Gdx.app.log("SoundFXService", "Logging unknown JS exception  " + e.getCause());
				}
			}
			return duration;
		}

		public native int getDurationFromJSAudio(WebAudioAPISound sound) /*-{
			// Get the Java values here, for readability
			var audioContext = sound.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPISound::audioContext;
			var audioBuffer = sound.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPISound::audioBuffer;

			var duration = -1
			if(audioBuffer != null) {
				duration = audioBuffer.duration;
			}
			return duration;
		}-*/;
	}
}
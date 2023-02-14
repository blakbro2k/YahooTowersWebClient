package asg.games.yokel.client.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager;
import com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPISound;
import com.github.czyzby.autumn.gwt.scanner.GwtClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.czyzby.websocket.GwtWebSockets;
import com.google.gwt.core.client.JavaScriptObject;

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
			if(soundFile instanceof WebAudioAPISound){
				WebAudioAPIManager manager = new WebAudioAPIManager();
				//JavaScriptObject context = manager.getAudioContext();

				//getDurationFromJSFile(soundFile);
				Gdx.app.log("SoundFXService", "Playing  " + soundFile.getClass());
				return -1;
			} else {
				return -1;
			}
		}

		private native void getDurationFromJSFile(String filePath) /*{
			function getDuration(src) {
			return new Promise(function(resolve) {
				var audio = new Audio();
				$(audio).on("loadedmetadata", function(){
					resolve(audio.duration);
				});
				audio.src = src;
				});
			}
			getDuration(filePath)
					.then(function(length) {
				console.log('I got length ' + length);
				document.getElementById("duration").textContent = length;
			});
		}*/;
	}
}
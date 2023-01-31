package asg.games.yokel.client.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.czyzby.autumn.gwt.scanner.GwtClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import asg.games.yokel.client.YahooTowersClient;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig() {
		GwtApplicationConfiguration configuration = new GwtApplicationConfiguration(YahooTowersClient.WIDTH, YahooTowersClient.HEIGHT);
		return configuration;
	}

	@Override
	public ApplicationListener createApplicationListener() {
		return new AutumnApplication(new GwtClassScanner(), YahooTowersClient.class);
	}
}
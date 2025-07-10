package asg.games.yokel.client.configuration.preferences;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference;
import com.github.czyzby.autumn.mvc.stereotype.preference.Property;

@Property("jwtToken")
public class JwtTokenPreference extends AbstractPreference<String> {
    @Override
    public String getDefault() {
        return "";
    }

    @Override
    public String extractFromActor(Actor actor) {
        return actor.getName();
    }

    @Override
    protected String convert(String rawPreference) {
        return rawPreference;
    }

    @Override
    protected String serialize(String preference) {
        return preference;
    }
}

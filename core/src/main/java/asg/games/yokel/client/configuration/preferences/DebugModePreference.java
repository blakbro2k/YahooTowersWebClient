package asg.games.yokel.client.configuration.preferences;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference;
import com.github.czyzby.autumn.mvc.stereotype.preference.Property;

@Property("debugMode")
public class DebugModePreference extends AbstractPreference<Boolean> {
    @Override
    public Boolean getDefault() {
        return false;
    }

    @Override
    public Boolean extractFromActor(Actor actor) {
        // Could be tied to a UI toggle
        return Boolean.valueOf(actor.getName());
    }

    @Override
    protected Boolean convert(String rawPreference) {
        return Boolean.parseBoolean(rawPreference);
    }

    @Override
    protected String serialize(Boolean preference) {
        return Boolean.toString(preference);
    }
}

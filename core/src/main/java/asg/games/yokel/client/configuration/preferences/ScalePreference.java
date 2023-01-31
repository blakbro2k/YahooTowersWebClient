package asg.games.yokel.client.configuration.preferences;

	import com.badlogic.gdx.scenes.scene2d.Actor;
	import com.github.czyzby.autumn.mvc.component.preferences.dto.AbstractPreference;
	import com.github.czyzby.autumn.mvc.stereotype.preference.Property;
	import com.github.czyzby.lml.util.LmlUtilities;
	import com.kotcrab.vis.ui.VisUI.SkinScale;

	/** Thanks to the Property annotation, this class will be automatically found and initiated.
	 *
	 * This class manages VisUI scale preference. */
	@Property("Scale")
	public class ScalePreference extends AbstractPreference<SkinScale> {
		@Override
		public SkinScale getDefault() {
			return SkinScale.X2;
		}

		@Override
		public SkinScale extractFromActor(final Actor actor) {
			return convert(LmlUtilities.getActorId(actor));
		}

		@Override
		protected SkinScale convert(final String rawPreference) {
			return SkinScale.valueOf(rawPreference);
		}

		@Override
		protected String serialize(final SkinScale preference) {
			return preference.name();
		}
	}
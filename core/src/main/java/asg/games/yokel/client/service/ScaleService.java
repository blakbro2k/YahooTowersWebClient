package asg.games.yokel.client.service;

import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.SkinService;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;

import asg.games.yokel.client.configuration.preferences.ScalePreference;

	/** Thanks to the ViewActionContainer annotation, this class will be automatically found and processed.
	 *
	 * This service handles GUI scale. */
	@Component
	public class ScaleService {
		// @Inject-annotated fields will be automatically filled by the context initializer.
		@Inject private ScalePreference preference;
		@Inject private InterfaceService interfaceService;
		@Inject private SkinService skinService;

		/** @return current GUI scale. */
		public SkinScale getScale() {
			return preference.get();
		}

		/** @return all scales supported by the application. */
		public SkinScale[] getScales() {
			return SkinScale.values();
		}

		/** @return scale property, which is saved in application's preferences. */
		public ScalePreference getPreference() {
			return preference;
		}

		/** @param scale the new application's scale. */
		public void changeScale(final SkinScale scale) {
			if (preference.get() == scale) {
				return; // This is the current scale.
			}
			preference.set(scale);
			// Changing GUI skin, reloading all screens:
			interfaceService.reload(new Runnable() {
				@Override
				public void run() {
					// Removing previous skin resources:
					VisUI.dispose();
					// Loading new skin:
					VisUI.load(scale);
					// Replacing the previously default skin:
					skinService.clear();
					skinService.addSkin("default", VisUI.getSkin());
				}
			});
		}
	}
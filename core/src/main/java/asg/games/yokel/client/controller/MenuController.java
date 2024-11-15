package asg.games.yokel.client.controller;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.Asset;
import com.github.czyzby.autumn.mvc.stereotype.View;

	/** Thanks to View annotation, this class will be automatically found and initiated.
	 *
	 * This is application's main view, displaying a menu with several options. */
	@View(id = "menu", value = "ui/templates/menu.lml", themes = "music/theme.ogg")
	public class MenuController implements ViewRenderer {
		/** Asset-annotated files will be found and automatically loaded by the AssetsService. */
		@Asset("images/libgdx.png") private Texture logo;

		@Override
		public void render(final Stage stage, final float delta) {
			// As a proof of concept that you can pair custom logic with Autumn MVC views, this class implements
			// ViewRenderer and handles view rendering manually. It renders libGDX logo before drawing the stage.
			stage.act(delta);

			final Batch batch = stage.getBatch();
			batch.setColor(stage.getRoot().getColor()); // We want the logo to share color alpha with the stage.
			batch.begin();
			batch.draw(logo, (int) (stage.getWidth() - logo.getWidth()) / 2,
					(int) (stage.getHeight() - logo.getHeight()) / 2);
			batch.end();

			stage.draw();
		}
	}
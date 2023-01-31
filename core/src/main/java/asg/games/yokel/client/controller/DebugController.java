package asg.games.yokel.client.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;

import asg.games.yokel.client.GlobalConstants;

/** Thanks to View annotation, this class will be automatically found and initiated.
 *
 * This is the first application's views, it will check if the debug Players will be shown, the UI tester, or the normal login Page
 * shown right after the application starts. It will hide after all assests are
 * loaded. */
@View(id = GlobalConstants.DEBUG_VIEW, value = GlobalConstants.DEBUG_VIEW_PATH)
public class DebugController implements ViewRenderer {

    @Override
    public void render(Stage stage, final float delta) {
        stage.act(delta);
        stage.draw();
    }
}



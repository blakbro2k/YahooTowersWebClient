package asg.games.yokel.client.controller;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.log.LoggerService;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.utils.LogUtil;

/**
 * Thanks to View annotation, this class will be automatically found and initiated.
 * <p>
 * This is the first application's views, it will check if the debug Players will be shown, the UI tester, or the normal login Page
 * shown right after the application starts. It will hide after all assests are
 * loaded.
 */
@View(id = GlobalConstants.DEBUG_VIEW, value = GlobalConstants.DEBUG_VIEW_PATH)
public class DebugController implements ViewRenderer, ViewInitializer {
    Log4LibGDXLogger logger;
    @Inject
    private LoggerService loggerService;
    @Inject
    private SessionService sessionService;

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        try {
            logger.enter("initialize");
            logger.exit("initialize");
        } catch (Exception e) {
            String errorMsg = "Error in initialize()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void destroy(ViewController viewController) {
        try {
            logger.enter("destroy");
            logger.exit("destroy");
        } catch (Exception e) {
            String errorMsg = "Error in destroy()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }

    @Override
    public void render(Stage stage, final float delta) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        try {
            logger.enter("render");
            stage.act(delta);
            stage.draw();
            logger.exit("render");
        } catch (Exception e) {
            String errorMsg = "Error in render()";
            logger.error(errorMsg, e);
            sessionService.handleException(logger, e);
        }
    }
}



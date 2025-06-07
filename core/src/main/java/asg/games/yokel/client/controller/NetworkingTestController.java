package asg.games.yokel.client.controller;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.kotcrab.vis.ui.widget.VisTextField;

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
@View(id = GlobalConstants.NET_TEST_VIEW, value = GlobalConstants.NET_TEST_VIEW_PATH)
public class NetworkingTestController implements ViewRenderer, ViewInitializer, ActionContainer {
    Log4LibGDXLogger logger;
    @Inject
    private LoggerService loggerService;
    @Inject
    private SessionService sessionService;

    @LmlActor("username")
    private VisTextField username;
    @LmlActor("rating")
    private VisTextField rating;

    @Override
    public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
        logger = LogUtil.getLogger(loggerService, this.getClass());
        logger.setDebug();
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

    @LmlAction("netConnect")
    private void netConnect() throws ReflectionException {
        try {
            logger.enter("netConnect");
            sessionService.connectToServer();
            logger.exit("netConnect");
        } catch (Exception e) {
            String errorMsg = "Error in netConnect()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("netDisconnect")
    private void netDisconnect() throws ReflectionException {
        try {
            logger.enter("netDisconnect");
            sessionService.disconnectToServer();
            logger.exit("netDisconnect");
        } catch (Exception e) {
            String errorMsg = "Error in netDisconnect()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @LmlAction("isConnectionAlive")
    private void isConnectionAlive() throws ReflectionException {
        try {
            logger.enter("isConnectionAlive");
            sessionService.isConnected();
            logger.exit("isConnectionAlive");
        } catch (Exception e) {
            String errorMsg = "Error in isConnectionAlive()";
            logger.error(errorMsg, e);
            throw new ReflectionException(errorMsg, e);
        }
    }

    @Override
    public void render(Stage stage, float delta) {
        try {
            stage.act(delta);
            stage.draw();
        } catch (Exception e) {
            String errorMsg = "Error in render()";
            logger.error(errorMsg, e);
            sessionService.showError(e);
        }
    }
}



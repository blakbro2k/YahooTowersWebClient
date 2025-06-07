package asg.games.yokel.client.controller;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewController;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.stereotype.View;
import com.github.czyzby.kiwi.log.LoggerService;
import com.github.czyzby.lml.annotation.LmlAction;

import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.utils.LogUtil;

/** Thanks to View annotation, this class will be automatically found and initiated.
	 *
	 * This is application's main view, displaying a menu with several options. */
	@View(id = "menu", value = "ui/templates/menu.lml", themes = "music/theme.ogg")
	public class MenuController implements ViewInitializer {
	Log4LibGDXLogger logger;
		/** Asset-annotated files will be found and automatically loaded by the AssetsService. */
		@Inject
		private SessionService sessionService;
	@Inject
	private LoggerService loggerService;

		@Override
		public void initialize(Stage stage, ObjectMap<String, Actor> actorMappedByIds) {
			logger = LogUtil.getLogger(loggerService, this.getClass());
			logger.error("stage={}", stage);
			logger.error("actorMappedByIds={}", actorMappedByIds);
			logger.error("sessionService={}", sessionService);
			logger.error("sessionService={}", sessionService.isDebug());
		}

	@Override
	public void destroy(ViewController viewController) {

	}

	@LmlAction("isDebug")
	public boolean isDebug() {
		return sessionService.isDebug();
		}
	}
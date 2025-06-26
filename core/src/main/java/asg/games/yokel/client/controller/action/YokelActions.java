package asg.games.yokel.client.controller.action;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;

public class YokelActions {

    public static CountLabelToAction countTo(int nextGameSeconds, boolean isCountUp) {
        CountLabelToAction action = Actions.action(CountLabelToAction.class);
        action.setCountDown(nextGameSeconds);
        action.setReverse(isCountUp);
        return action;
    }

    public static CloseDialogAction closeDialog(Class<?> dialogController, InterfaceService interfaceService) {
        CloseDialogAction action = Actions.action(CloseDialogAction.class);
        action.setController(dialogController, interfaceService);
        return action;
    }
}
package asg.games.yokel.client.controller.action;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;

import asg.games.yokel.client.controller.dialog.NextGameController;

public class YokelActions {

    public static CountLabelToAction countTo(int nextGameSeconds, boolean isCountUp) {
        CountLabelToAction action = Actions.action(CountLabelToAction.class);
        action.setCountDown(nextGameSeconds);
        action.setReverse(isCountUp);
        return action;
    }

    public static CloseDialogAction closeDialog(InterfaceService interfaceService) {
        CloseDialogAction action = Actions.action(CloseDialogAction.class);
        action.setController(NextGameController.class, interfaceService);
        return action;
    }
}
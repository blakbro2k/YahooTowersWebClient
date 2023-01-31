package asg.games.yokel.client.controller.dialog;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.action.CloseDialogAction;
import asg.games.yokel.client.controller.action.CountLabelToAction;
import asg.games.yokel.client.service.SoundFXService;

/** This is a settings dialog, which can be shown in any views by using "show:settings" LML action or - in Java code -
 * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
 * will be available in the LML template. */
@ViewDialog(id = GlobalConstants.NEXT_GAME_DIALOG, value = GlobalConstants.NEXT_GAME_PATH)
public class NextGameController implements ActionContainer, ViewDialogShower {
    public static final int NEXT_GAME_SECONDS = 15;
    @Inject private SoundFXService soundFXService;
    @Inject private InterfaceService interfaceService;
    @LmlActor("timerLabel") private Label timerLabel;

    @Override
    public void doBeforeShow(Window dialog) {
        soundFXService.playGameStartSound();

        if(timerLabel != null) {
            timerLabel.addAction(
                    Actions.sequence(countTo(NEXT_GAME_SECONDS, false),
                    closeDialog(interfaceService))
            );
        }
    }

    private static CountLabelToAction countTo(int nextGameSeconds, boolean isCountUp) {
        CountLabelToAction action = Actions.action(CountLabelToAction.class);
        action.setCountDown(nextGameSeconds);
        action.setReverse(isCountUp);
        return action;
    }

    private static CloseDialogAction closeDialog(InterfaceService interfaceService) {
        CloseDialogAction action = Actions.action(CloseDialogAction.class);
        action.setController(NextGameController.class, interfaceService);
        return action;
    }
}
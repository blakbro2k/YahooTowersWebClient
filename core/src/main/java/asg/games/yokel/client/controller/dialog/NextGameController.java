package asg.games.yokel.client.controller.dialog;

import com.badlogic.gdx.graphics.Color;
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
import asg.games.yokel.client.controller.action.YokelActions;
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
        System.out.println("Enter doBeforeShow()");
        soundFXService.playGameStartSound();

        if(timerLabel != null) {
            timerLabel.setColor(Color.ORANGE);
            timerLabel.addAction(
                    Actions.sequence(YokelActions.countTo(NEXT_GAME_SECONDS, false),
                            YokelActions.closeDialog(this.getClass(), interfaceService)
                    ));
        }
        System.out.println("Exit doBeforeShow()");
    }
}
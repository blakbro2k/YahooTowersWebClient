package asg.games.yokel.client.controller.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.I18NBundle;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewDialogShower;
import com.github.czyzby.autumn.mvc.stereotype.Asset;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.action.ActionContainer;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.controller.action.YokelActions;
import asg.games.yokel.client.service.SessionService;
import asg.games.yokel.client.service.SoundFXService;
import asg.games.yokel.client.utils.UIUtil;

@ViewDialog(id = GlobalConstants.GAME_OVER_DIALOG, value = GlobalConstants.GAME_OVER_DIALOG_PATH)
public class GameOverDialogController implements ActionContainer, ViewDialogShower {
    private static final float LABEL_ANIMATION_DELAY = 0f;
    private static final float LABEL_ANIMATION_DURATION = 1f;
    public static final float LABEL_ANIMATION_CHAR_DELAY = 0.05f;
    public static final String LABEL_KEY_YOU_LOSE = "youLose";
    public static final String LABEL_KEY_YOU_WIN = "youWin";

    @Inject
    private InterfaceService interfaceService;
    @Inject
    private SoundFXService soundFXService;
    @Inject
    private SessionService sessionService;

    @Asset("i18n/bundle")
    private I18NBundle i18nBundle;
    @LmlActor("gameOverLabel")
    private Label gameOverLabel;
    @LmlActor("winLoseLabel")
    private Label winLoseLabel;
    @LmlActor("congratsLabel")
    private Label congratsLabel;
    @LmlActor("player1Label")
    private Label player1Label;
    @LmlActor("andLabel")
    private Label andLabel;
    @LmlActor("player2Label")
    private Label player2Label;

    public void resetLabels() {
        System.out.println("Enter doBeforeShow()");
        gameOverLabel.setVisible(false);
        winLoseLabel.setVisible(false);
        congratsLabel.setVisible(false);
        player1Label.setVisible(false);
        andLabel.setVisible(false);
        player2Label.setVisible(false);
        System.out.println("Enter doBeforeShow()");
    }

    @Override
    public void doBeforeShow(Window dialog) {
        System.out.println("Enter doBeforeShow()");
        resetLabels();
        soundFXService.playGameOverSound();

        float delay = 0f;
        float totalDuration = 0f;

        // Animate: Game Over
        if (gameOverLabel != null) {
            totalDuration += LABEL_ANIMATION_CHAR_DELAY * gameOverLabel.getText().length() + LABEL_ANIMATION_DURATION;
            gameOverLabel.setColor(Color.PINK);
            gameOverLabel.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run(() -> UIUtil.animateLabelFlyInLetterByLetter(gameOverLabel, LABEL_ANIMATION_CHAR_DELAY, LABEL_ANIMATION_DURATION))
            ));
            delay += 1.3f; // Adjust based on animation time
        }

        //.setTransform(true);
        // Animate: You Win!
        if (winLoseLabel != null) {
            totalDuration += LABEL_ANIMATION_DURATION;
            if (!sessionService.isWinner()) {
                winLoseLabel.setText(i18nBundle.get(LABEL_KEY_YOU_LOSE));
            } else {
                winLoseLabel.setText(i18nBundle.get(LABEL_KEY_YOU_WIN));
            }
            winLoseLabel.setColor(Color.YELLOW);
            winLoseLabel.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run(() -> UIUtil.animateLabelFlyIn(winLoseLabel, LABEL_ANIMATION_DELAY, LABEL_ANIMATION_DURATION))
            ));
            delay += 1.0f;
        }

        // Animate: Congrats
        if (congratsLabel != null) {
            totalDuration += LABEL_ANIMATION_CHAR_DELAY * congratsLabel.getText().length() + LABEL_ANIMATION_DURATION;
            congratsLabel.setColor(Color.PINK);
            congratsLabel.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run(() -> UIUtil.animateLabelFlyInLetterByLetter(congratsLabel, LABEL_ANIMATION_CHAR_DELAY, LABEL_ANIMATION_DURATION))
            ));
            delay += 1.0f;
        }

        // Animate: Team Text Label
        if (player1Label != null) {
            totalDuration += LABEL_ANIMATION_CHAR_DELAY * player1Label.getText().length() + LABEL_ANIMATION_DURATION;
            //player1Label.setText(i18NBundle.get("player1Text"));
            player1Label.setColor(Color.PINK);
            player1Label.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run(() -> UIUtil.animateLabelFlyInLetterByLetter(player1Label, LABEL_ANIMATION_CHAR_DELAY, LABEL_ANIMATION_DURATION)),
                    Actions.delay(4f)
            ));
            delay += 1.0f;
        }

        if (sessionService.isPartnered()) {
            if (andLabel != null) {
                totalDuration += LABEL_ANIMATION_CHAR_DELAY * andLabel.getText().length() + LABEL_ANIMATION_DURATION;
                andLabel.setColor(Color.PINK);
                andLabel.addAction(Actions.sequence(
                        Actions.delay(delay),
                        Actions.run(() -> UIUtil.animateLabelFlyInLetterByLetter(andLabel, LABEL_ANIMATION_CHAR_DELAY, LABEL_ANIMATION_DURATION)),
                        Actions.delay(4f)
                ));
                delay += 1.0f;
            }
            if (player2Label != null) {
                totalDuration += LABEL_ANIMATION_CHAR_DELAY * player2Label.getText().length() + LABEL_ANIMATION_DURATION;
                //player2Label.setText("#p2_name#");
                player2Label.setColor(Color.PINK);
                player2Label.addAction(Actions.sequence(
                        Actions.delay(delay),
                        Actions.run(() -> UIUtil.animateLabelFlyInLetterByLetter(player2Label, LABEL_ANIMATION_CHAR_DELAY, LABEL_ANIMATION_DURATION)),
                        Actions.delay(4f)
                ));
                delay += 1.0f;
            }
        }

        if (gameOverLabel != null) {
            gameOverLabel.addAction(Actions.sequence(
                    Actions.delay(totalDuration),
                    YokelActions.closeDialog(this.getClass(), interfaceService))
            );
        }

        System.out.println("Exit doBeforeShow()");
    }
}
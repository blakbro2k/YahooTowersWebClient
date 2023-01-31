package asg.games.yokel.client.controller.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.util.LmlUtilities;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.service.ScaleService;
import asg.games.yokel.client.service.SoundFXService;

/**
 * This is a settings dialog, which can be shown in any views by using "show:settings" LML action or - in Java code -
 * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
 * will be available in the LML template.
 */
@ViewDialog(id = GlobalConstants.SOUND_BOARD_DIALOG, value = GlobalConstants.SOUND_BOARD_DIALOG_PATH)
public class SoundBoardController implements ActionContainer {
    @Inject private InterfaceService interfaceService;
    //@Inject private SessionService sessionService;
    @Inject private SoundFXService soundFXService;
    @Inject private ScaleService scaleService;

    private final String ID_YAHOO_MUSIC_START = "yahooMusicStart";
    private final String ID_YAHOO_MUSIC_STOP = "yahooMusicStop";
    private final String ID_YAHOO_BLAST = "yahooBlast";
    private final String ID_PLACED = "placed";
    private final String ID_WHOOSH = "whoosh";
    private final String ID_YAH = "yah";
    private final String ID_CYCLE = "cycle";
    private final String ID_BROKEN = "broken";
    private final String ID_BOARD_DEATH = "death";
    private final String ID_GAME_START = "startGame";
    private final String ID_GAME_STOP = "gameOver";
    private int rowCount = 0;

    public void reset(){
        rowCount = 0;
    }

    @LmlAction("checkRow")
    public String checkRow() {
        boolean isRowTrue = ++rowCount % 4 == 0;
        return isRowTrue + "";
    }

    @LmlAction("getAllSoundIds")
    public Array<String> getAllSoundIds() {
        reset();
        return GdxArrays.newArray(ID_YAHOO_MUSIC_START,ID_YAHOO_MUSIC_STOP,ID_YAHOO_BLAST,
                ID_PLACED,ID_WHOOSH,ID_YAH,ID_CYCLE,ID_BROKEN,ID_BOARD_DEATH,ID_GAME_START,ID_GAME_STOP);
    }

    @LmlAction("playSound")
    public void playSound(final Actor actor) {
        String id = LmlUtilities.getActorId(actor);
        int SOUND_BOARD_SEAT = 0;
        switch (id) {
            case ID_YAHOO_MUSIC_START:
                soundFXService.startYahooFanfare(SOUND_BOARD_SEAT);
                break;
            case ID_YAHOO_MUSIC_STOP:
                soundFXService.stopYahooFanfare(SOUND_BOARD_SEAT);
                break;
            case ID_YAHOO_BLAST:
                soundFXService.playYahooSound();
                break;
            case ID_PLACED:
                soundFXService.playPiecePlacedSound();
                break;
            case ID_WHOOSH:
                soundFXService.playBlockDownSound();
                break;
            case ID_YAH:
                soundFXService.playYahooBrokenCell();
                break;
            case ID_CYCLE:
                soundFXService.playCycleClickSound();
                break;
            case ID_BROKEN:
                soundFXService.playBrokenCell();
                break;
            case ID_BOARD_DEATH:
                soundFXService.playBoardDeathSound();
                break;
            case ID_GAME_START:
                soundFXService.playGameStartSound();
                break;
            case ID_GAME_STOP:
                soundFXService.playGameOverSound();
                break;
            default:
                break;
        }
    }
}
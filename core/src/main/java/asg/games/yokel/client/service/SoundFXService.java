package asg.games.yokel.client.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.stereotype.Asset;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.YahooTowersClient;
import asg.games.yokel.client.utils.UIUtil;

//import asg.games.yokel.utils.YokelUtilities;

@Component
public class SoundFXService {
    @Inject private MusicService musicService;
    private final ObjectMap<String, Double> lastPlayedTimeStamp;
    private final boolean[] yahooPlaying = new boolean[8];

    @Asset(GlobalConstants.CYCLE_CLICK_PATH) private Sound cycleClickSound;
    @Asset(GlobalConstants.BLOCK_SPEED_DOWN_PATH) private Sound blockDown;
    @Asset(GlobalConstants.GAME_START_PATH) private Sound gameStartSound;
    @Asset(GlobalConstants.BLOCK_BREAK_PATH) private Sound blockBreakSound;
    @Asset(GlobalConstants.YAHOO_YAH_PATH) private Sound yahooYahSound;
    @Asset(GlobalConstants.YAHOO_PATH) private Sound yahooSound;
    @Asset(GlobalConstants.GAME_OVER_PATH) private Sound gameOverSound;
    @Asset(GlobalConstants.BOARD_DEATH_PATH) private Sound boardDeathSound;
    @Asset(GlobalConstants.MENACING_PATH) private Sound menacingSound;
    @Asset(GlobalConstants.PIECE_PLACED_PATH) private Sound piecePlacedSound;

    public SoundFXService(){
        lastPlayedTimeStamp = GdxMaps.newObjectMap();
    }

    @Destroy
    public void dispose() {
        lastPlayedTimeStamp.clear();
    }

    public float getSoundDuration(Sound soundFile) {
        return UIUtil.getInstance().getSoundDuration(soundFile);
    }

    /**
     * Only plays sound once the sound effect is finished
     *
     * @param sound
     * @param soundName
     */
    private void playFinishedSound(Sound sound, String soundName){
        playDelayedSound(sound, soundName, getSoundDuration(sound));
    }

    /**
     * Only plays sound once the sound effect is finished
     *
     * @param sound
     * @param soundName
     */
    private void playSoundNow(Sound sound, String soundName){
        playDelayedSound(sound, soundName, 0);
    }

    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Plays a sound only after a forced time delay
     * @param sound
     * @param soundName
     * @param delay
     */
    private void playDelayedSound(Sound sound, String soundName, float delay){
        Gdx.app.log("SoundFXService", "Playing  " + soundName + ": " + sound + "-" + UIUtil.getInstance().getSoundDuration(sound));

        if(sound == null || isEmpty(soundName)) return;
        double lastPlayed = getLastPlayed(soundName);
        long now = TimeUtils.millis();
        double elapsed = ((now - lastPlayed) / 1000.0);

        if(elapsed > delay){
            lastPlayed = TimeUtils.millis();
            safePlaySound(sound);
        }
        lastPlayedTimeStamp.put(soundName, lastPlayed);
    }

    private double getLastPlayed(String soundName) {
        if(lastPlayedTimeStamp.containsKey(soundName)){
            return lastPlayedTimeStamp.get(soundName);
        } else {
            return 0.0;
        }
    }

    public void playBlockDownSound() {
        playFinishedSound(blockDown, "whoosh");
    }

    public void playYahooSound(){
        playFinishedSound(yahooSound, "yahoo!");
    }

    public void playMenacingSound(){
        playLoopedSound(menacingSound);
    }

    private void playLoopedSound(Sound sound) {
        if(sound != null){
            sound.loop();
            safePlaySound(sound);
        }
    }

    public void stopMenacingSound(){
        safeStopSound(menacingSound);
    }

    private void safeStopSound(Sound sound) {
        if(sound != null) {
            sound.stop();
        }
    }

    private void safePlaySound(Sound sound) {
        if(sound != null) {
            musicService.play(sound);
        }
    }

    public void playCycleClickSound() {
        safePlaySound(cycleClickSound);
    }

    public void playGameStartSound(){
        playFinishedSound(gameStartSound, "gameStart");
    }

    public void playBrokenCell() {
        playDelayedSound(blockBreakSound, "brokenCell", 0.2999f);
    }

    public void playBoardDeathSound() {
        safePlaySound(boardDeathSound);
    }

    public void playYahooBrokenCell() {
        playFinishedSound(yahooYahSound, "brokenYahooCell");
    }

    public void playGameOverSound() {
        playFinishedSound(gameOverSound, "gameOver");
    }

    public void playPiecePlacedSound() {
        safePlaySound(piecePlacedSound);
    }

    //This is the yahoo + menacing sound for the client player
    public void startYahooFanfare(int seat) {
        if(!yahooPlaying[seat]){
            yahooPlaying[seat] = true;
            playYahooSound();
            playMenacingSound();
        }
    }

    public void stopYahooFanfare(int seat) {
        yahooPlaying[seat] = false;
        stopMenacingSound();
    }
}
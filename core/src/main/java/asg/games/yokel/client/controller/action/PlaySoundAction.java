package asg.games.yokel.client.controller.action;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import asg.games.yokel.client.utils.UIUtil;

public class PlaySoundAction extends Action {
    //private Log4LibGDXLogger logger = Log4LibGDXLoggerService.forClass(PlaySoundAction.class);
    Sound sound;
    float duration;
    private long start;
    private float end;
    private boolean began, complete;

    public void setSound(Sound sound){
        this.sound = sound;
        this.duration = UIUtil.getInstance().getSoundDuration(sound);
    }

    /** Called the first time {@link #act(float)} is called. This is a good place to query the {@link #actor actor's} starting
     * state. */
        void begin() {
            if(sound != null){
                sound.play();
                start = TimeUtils.millis();
                end = duration * 1000;
            }
        }

        /** Called the last time {@link #act(float)} is called. */
    protected void end () {
        if(sound != null){
            sound.stop();
            //end = TimeUtils.millis();
        }
    }

    int getElapsedSeconds(){
        return (int) ((TimeUtils.millis() - start) / 1000);
    }

    public void restart () {
        start = -1;
        end = -1;
        began = false;
        complete = false;
    }

    public void reset () {
        super.reset();
        restart();
    }

    @Override
    public boolean act (float delta) {
        if (complete) return true;
        Pool pool = getPool();
        setPool(null); // Ensure this action can't be returned to the pool while executing.
        try {
            if (!began) {
                begin();
                began = true;
            }
            int elapsed = getElapsedSeconds();
            complete = elapsed >= end;
            if (complete) end();
            return complete;
        } finally {
            setPool(pool);
        }
    }
}
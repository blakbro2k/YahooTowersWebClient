package asg.games.yokel.client.controller.action;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

public class CountLabelToAction extends Action {
    private long start;
    private int end;
    private boolean reverse, began, complete;

    protected void update(int time) {
        Actor label = getActor();
        if(label instanceof Label){
            ((Label) label).setText(time);
        }
    }

    public void setCountDown(int i) {
        end = i;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    int getCountDown() {
        return end;
    }

    public boolean getReverse() {
        return reverse;
    }

    /** Called the first time {@link #act(float)} is called. This is a good place to query the {@link #actor actor's} starting
     * state. */
    void begin() {
        start = TimeUtils.millis();
    }

    /** Called the last time {@link #act(float)} is called. */
    protected void end () {
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
        reverse = false;
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
            if(reverse){
                update(elapsed);
            } else {
                update(end - elapsed);
            }
            if (complete) end();
            return complete;
        } finally {
            setPool(pool);
        }
    }
}

package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.TimeUtils;

import asg.games.yokel.objects.YokelClock;
import asg.games.yokel.utils.YokelUtilities;

public class GameClock extends Table implements GameObject {
    private final static String NO_DIGIT_NME = "no_digit";
    private final static String COLON_NME = "colon";
    private final static String DIGIT_NME = "_digit";

    private long start;
    private boolean isRunning;

    private Image digit_1;
    private Image digit_2;
    private Image digit_3;
    private Image digit_4;
    private Image colon;

    public GameClock(Skin skin){
        super(skin);

        resetTimer();
        add(getDigit_1());
        add(getDigit_2());
        add(getColon());
        add(getDigit_3());
        add(getDigit_4());
    }

    public void start(){
        isRunning = true;
        start = TimeUtils.millis();
    }

    public void stop(){
        resetTimer();
    }

    public boolean isRunning(){
        return this.isRunning;
    }

    public int getElapsedSeconds(){
        return (int) ((TimeUtils.millis() - start) / 1000);
    }

    private int getSeconds(){
        if(isRunning()){
            return Math.round(getElapsedSeconds()) % 60;
        }
        return -1;
    }

    private int getMinutes(){
        if(isRunning()){
            return Math.round(getElapsedSeconds()) / 60;
        }
        return -1;
    }

    private void resetTimer(){
        start = 0;
        this.isRunning = false;
    }

    @Override
    public void act(float delta){
        setDigit_1();
        setDigit_2();
        setDigit_3();
        setDigit_4();
    }

    private Drawable getDigitImage(String imageName){
        if(imageName != null){
            return getSkin().getDrawable(imageName);
        }
        return null;
    }

    private Image getDigit_1(){
        if(digit_1 == null){
            digit_1 = new Image();
        }
        return digit_1;
    }

    private Image getDigit_2(){
        if(digit_2 == null){
            digit_2 = new Image();
        }
        return digit_2;
    }

    private Image getDigit_3(){
        if(digit_3 == null){
            digit_3 = new Image();
        }
        return digit_3;
    }

    private Image getDigit_4(){
        if(digit_4 == null){
            digit_4 = new Image();
        }
        return digit_4;
    }

    private Image getColon(){
        if(colon == null){
            colon = new Image();
        }
        colon.setDrawable(getDigitImage(COLON_NME));
        return colon;
    }

    private Drawable getDigit(int i){
        if(!isRunning){
            return getDigitImage(NO_DIGIT_NME);
        } else {
            return getDigitImage(i + DIGIT_NME);
        }
    }

    private void setDigit_1(){
        digit_1.setDrawable(getDigit(getMinutes() / 10));
    }

    private void setDigit_2(){
        digit_2.setDrawable(getDigit(getMinutes() % 10));
    }

    private void setDigit_3(){
        digit_3.setDrawable(getDigit(getSeconds() / 10));
    }

    private void setDigit_4(){
        digit_4.setDrawable(getDigit(getSeconds() % 10));
    }

    @Override
    public void setData(String data) {
        YokelClock clock = YokelUtilities.getObjectFromJsonString(YokelClock.class, data);
        if(clock != null){
            start = clock.getStart();
            isRunning = clock.getIsRunning();
        }
    }
}
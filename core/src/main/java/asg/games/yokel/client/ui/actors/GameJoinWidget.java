package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import asg.games.yokel.utils.YokelUtilities;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/** A table that can be dragged and act as a modal window. The top padding is used as the window's title height.
 * <p>
 * The preferred size of a window is the preferred size of the title text and the children as laid out by the table. After adding
 * children to the window, it can be convenient to call {@link #pack()} to size the window to the size of the children.
 * @author Nathan Sweet */
public class GameJoinWidget extends Table {
    static private final String WAITING_TEXT = "Waiting for\nMore\nPlayers";
    static private final String JOIN_TEXT = "Join";
    static private final String READY_TEXT = "Ready";
    static private final String START_TEXT = "Start";
    private ClickListener switchJoinText, readyPlayerOne;

    static private final Vector2 tmpPosition = new Vector2();
    static private final Vector2 tmpSize = new Vector2();
    static private final int MOVE = 1 << 5;

    //private GameWindowStyle style;
    boolean isMovable = true;//, isModal, isResizable;
    int resizeBorder = 8;
    private boolean keepWithinStage = true;
    //isGameReady means there is more than one player in the room.
    private boolean isSeated, preview, isGameReady, isPlayerReady  = false;

    private final TextButton joinButton, startButton, spaceButton;
    private final Label waitingLabel, readyLabel;

    public GameJoinWidget(Skin skin) {
        super(skin);
        //setSkin(skin);
        //setStyle(style);
        //setMovable(false);

        //setWidth(150);
        //setHeight(50);

        pad(4f);

        setTouchable(Touchable.enabled);
        setClip(true);
        setUpClickListener();

        joinButton = new TextButton(JOIN_TEXT, getSkin());
        joinButton.addListener(switchJoinText);

        startButton = new TextButton(START_TEXT, getSkin());
        startButton.addListener(readyPlayerOne);

        spaceButton = new TextButton(".    .", getSkin());
        spaceButton.setVisible(false);
        spaceButton.setDisabled(true);


        waitingLabel = new Label(WAITING_TEXT, getSkin());
        waitingLabel.setAlignment(Align.center);

        readyLabel = new Label(READY_TEXT, getSkin());
        readyLabel.setAlignment(Align.center);
        setUpJoinButton();
    }

    private void setUpJoinButton(){
        clearChildren();
        if(isSeated){
            setButtonsVisibility(false);
            if(isPlayerReady){
                if(!isGameReady){
                    add(waitingLabel).row();
                     //add(spaceButton).fillX();
                } else {
                    add(readyLabel).row();
                    add(spaceButton).fillX();
                }
            } else {
                add(spaceButton).fillX().row();
                add(startButton);
            }
        } else {
            setButtonsVisibility(true);
            add(joinButton).row();
            add(spaceButton).fillX();
        }
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(YokelUtilities.setDebug(debug, joinButton, startButton, spaceButton, waitingLabel, readyLabel));
    }

    private void setButtonsVisibility(boolean isVisible) {
        joinButton.setVisible(isVisible);
        spaceButton.setVisible(isVisible);
    }

    private void setUpClickListener() {
        switchJoinText = new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleJoinButtonClick();
                return true;
            }
        };

        readyPlayerOne = new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleStartButtonClick();
                return true;
            }
        };
    }

    public void setSeated(boolean isSeated){
        this.isSeated = isSeated;
        setUpJoinButton();
    }

    public void setIsGameReady(boolean isGameReady){
        this.isGameReady = isGameReady;
        setUpJoinButton();
    }

    public void setIsPlayerReady(boolean isPlayerReady){
        this.isPlayerReady = isPlayerReady;
        setUpJoinButton();
    }

    private void handleJoinButtonClick(){
        setSeated(true);
        setUpJoinButton();
    }

    private void handleStartButtonClick(){
        setIsPlayerReady(!isPlayerReady);
        setUpJoinButton();
    }

    public void setButtonListener(InputListener listener){
        joinButton.clearListeners();
        joinButton.addListener(switchJoinText);
        joinButton.addListener(listener);
    }
}
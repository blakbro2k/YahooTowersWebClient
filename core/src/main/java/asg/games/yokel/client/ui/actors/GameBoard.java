package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Queue;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelPiece;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.utils.YokelUtilities;

/**
 * Created by eboateng on 3/19/2018.
 */

public class GameBoard extends Table implements GameObject {
    private final static int COL_SPAN = 2;
    private final static int TOP_PAD = 4;
    private final static int BOTTOM_PAD = 6;

    private GameNameLabel nameLabel;
    private GamePiece next;
    private GamePowersQueue powers;
    private GameBlockArea area;
    private GamePiece clearPiece;

    private float blockWidth;
    private float blockPrevWidth;
    private float blockHeight;
    private float blockPrevHeight;

    private boolean isLeftBar = true;

    public GameBoard(Skin skin) {
        super(skin);
        initialize(skin);
        GameBlock block = UIUtil.getBlock(YokelBlock.CLEAR_BLOCK);
        GameBlock previewBlock = UIUtil.getBlock(YokelBlock.CLEAR_BLOCK, area.isPreview());

        blockWidth = block.getWidth();
        blockPrevWidth = previewBlock.getWidth();
        blockHeight = block.getHeight();
        blockPrevHeight = previewBlock.getHeight();

        clearPiece = new GamePiece(getSkin());
    }

    private void initialize(Skin skin) {
        next = new GamePiece(skin);
        powers = new GamePowersQueue(skin);
        area = new GameBlockArea(skin);
        nameLabel = new GameNameLabel(skin);
        setUpBoard();
    }

    private void setUpBoard(){
        clearChildren();
        Table right = new Table(getSkin());
        right.add(area);

        if(area.isPreview()){
            add(nameLabel).left().row();
            add(right);
        } else {
            Table left = new Table(getSkin());
            left.add(next).top().padTop(TOP_PAD).padBottom(BOTTOM_PAD).row();
            left.add(clearPiece).row();
            left.add(powers).bottom();
            add(nameLabel).left().colspan(COL_SPAN);
            row();

            if(isLeftBar){
                add(left).top();
                add(right).expandX();
            } else {
                add(right).expandX();
                add(left).top();
            }
        }
    }

    @Override
    public void setDebug(boolean debug){
        super.setDebug(YokelUtilities.setDebug(debug, nameLabel, next, powers, area));
    }

    public void setPreview(boolean preview){
        area.setPreview(preview);
        setUpBoard();
    }

    public boolean isPreview() {
        return area.isPreview();
    }

    public boolean getPreview(){
        return area.isPreview();
    }

    public void update(YokelGameBoard board){
        if(board != null){
            area.updateData(board);
            powers.updateQueue(blockToGameBlocks(board.getPowers()));
            setUpNext(board);
        }
    }

    private void setUpNext(YokelGameBoard board){
        YokelPiece piece = board.fetchCurrentNextPiece();
        if(piece != null){
            next.setData(piece.getJsonString());
        }
    }

    @Override
    public void setData(String data) {
        if(!YokelUtilities.isEmpty(data)){
            YokelPlayer player = YokelUtilities.getObjectFromJsonString(YokelPlayer.class, data);
            if(player != null){
                sitPlayerDown(player);
            } else {
                standPlayerUp();
            }
        } else {
            standPlayerUp();
        }
    }

    public void sitPlayerDown(YokelPlayer player){
        if(player != null){
            area.sitPlayerDown(player);
            setPlayerLabel(player.getJsonString());
        }
        setUpBoard();
    }

    public void standPlayerUp(){
        area.standPlayerUp();
        nameLabel.removePlayer();
        setUpBoard();
    }

    private void setPlayerLabel(String data){
        if(data != null){
            nameLabel.setData(data);
        }
    }

    public void setLeftBarOrientation(boolean isLeft){
        isLeftBar = isLeft;
        setUpBoard();
    }

    @Override
    public float getPrefHeight() {
        if(area.isPreview()){
            return blockPrevHeight * YokelGameBoard.MAX_PLAYABLE_ROWS + nameLabel.getPrefHeight();
        } else {
            return blockHeight * YokelGameBoard.MAX_PLAYABLE_ROWS + nameLabel.getPrefHeight();
        }
    }

    @Override
    public float getPrefWidth() {
        if(area.isPreview()){
            return blockPrevWidth * YokelGameBoard.MAX_COLS + nameLabel.getPrefWidth();
        } else {
            return blockWidth * YokelGameBoard.MAX_COLS + blockWidth * 2;
        }
    }

    public void setBoardNumber(int boardNumber) {
        area.setBoardNumber(boardNumber);
    }

    public int getBoardNumber() {
        return area.getBoardNumber();
    }

    public YokelGameBoard getYokelGameBoard() {
        return area.getBoard();
    }

    private Queue<GameBlock> blockToGameBlocks(Queue<Integer> blocks){
        Queue<GameBlock> gameBlocks = new Queue<>();
        for(int block : YokelUtilities.safeIterable(blocks)){
            gameBlocks.addFirst(UIUtil.getBlock(block, area.isPreview()));
        }
        return gameBlocks;
    }

    public void setActive(boolean b) {
        area.setActive(b);
    }

    public void setGameReady(boolean gameReady) {
        area.setGameReady(gameReady);
    }

    public void setPlayerView(boolean b) {
        area.setPlayerView(b);
    }

    public void killPlayer(){
        area.killPlayer();
    }

    public void addButtonListener(InputListener listener) {
        area.addButtonListener(listener);
    }

    public void hideJoinButton() {
        area.hideJoinButton();
    }

    public void showJoinButton() {
        area.showJoinButton();
    }

    public void setIsPlayerReady(boolean isPlayerReady) {
        area.setIsPlayerReady(isPlayerReady);
    }

    public void setYahooDuration(boolean isYahoo) {
        nameLabel.setYahoo(isYahoo);
    }
}
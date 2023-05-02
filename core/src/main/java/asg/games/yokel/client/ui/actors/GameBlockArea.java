package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yokel.client.factories.YokelObjectFactory;
import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.objects.YokelBrokenBlock;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelPiece;
import asg.games.yokel.objects.YokelPlayer;
import asg.games.yokel.utils.YokelUtilities;

public class GameBlockArea extends Stack {
    private static final String CELL_ATTR = "uiCell";
    private static final String CELL_ATTR_SEPARATOR = "_";
    private static final String GRID_NAME = "grid";
    private static final String BOARD_NUMBER_NAME = "boardNumber";
    private static final String ALIVE_BACKGROUND = "area_alive_bg";
    private static final String DEAD_BACKGROUND = "area_dead_bg";
    private static final String PLAYER_BACKGROUND = "area_player_bg";
    private static final float ANIMATION_OFFSET = 0.03f;

    private boolean isActive;
    private boolean isPreview;
    private boolean isPlayerView;

    private int boardNumber;
    private YokelGameBoard board;

    private final Skin skin;

    private Table grid;
    private Table bgNumber;
    private Table bgColor;
    private Label tableNumber;

    private GameJoinWidget joinWindow;

    private final ObjectMap<String, GameBlock> uiBlocks = GdxMaps.newObjectMap();
    private static final Array<Image> brokenBlocks = GdxArrays.newArray();
    private PieceDrawable pieceSprite;

    public GameBlockArea(Skin skin, boolean isPreview) {
        this.skin = skin;
        this.isPreview = isPreview;
        init();
    }

    public GameBlockArea(Skin skin) {
        this(skin, true);
    }

    private void init(){
        initializeBoard();
        initializeSize();

        setBoardNumber(0);
        bgColor.setBackground(skin.getDrawable(ALIVE_BACKGROUND));
        add(bgColor);
        bgNumber.add(tableNumber);
        bgNumber.add("");
        add(bgNumber);
        joinWindow = new GameJoinWidget(skin);
        joinWindow.setClip(true);
        //joinWindow.setVisible(false);
        //joinWindow.setSize(20,20);
        initializeGrid();
        add(joinWindow);
    }

    private void initializeSize(){
        GameBlock clear = getClearBlock();
        float width = clear.getWidth() * YokelGameBoard.MAX_COLS;
        float height = clear.getHeight() * YokelGameBoard.MAX_PLAYABLE_ROWS;
        //this.setBounds(sLoc.x, sLoc.y, width, height);
        //grid.setBounds(sLoc.x, sLoc.y, width, height);
        //setCullingArea(new Rectangle(sLoc.x, sLoc.y, width, height));
    }

    private void initializeBoard(){
        this.boardNumber = 0;
        this.grid = new Table(skin);
        this.bgColor = new Table(skin);
        this.bgNumber = new Table(skin);
        this.pieceSprite = new PieceDrawable();
    }

    public void setDebug (boolean enabled) {
        super.setDebug(YokelUtilities.setDebug(enabled, grid, bgNumber, bgColor, joinWindow, pieceSprite));
        for (GameBlock gameBlock : YokelUtilities.getMapValues(uiBlocks)) {
            YokelUtilities.setDebug(enabled, gameBlock);
        }
    }

    private void initializeGrid(){
        this.grid.clearChildren();
        this.grid.setName(GRID_NAME);
        for(int r = YokelGameBoard.MAX_PLAYABLE_ROWS - 1; r >= 0; r--){
            for(int c = 0; c < YokelGameBoard.MAX_COLS; c++){
                GameBlock uiBlock = getClearBlock();

                uiBlocks.put(getCellAttrName(r, c), uiBlock);
                if(c + 1 == YokelGameBoard.MAX_COLS){
                    grid.add(uiBlock).row();
                } else {
                    grid.add(uiBlock);
                }
            }
        }
        setAreaBounds();
        grid.add(pieceSprite);
        add(grid);
    }

    private void setAreaBounds(){
        GameBlock block = getClearBlock();
        float width = block.getWidth();
        float height = block.getHeight();
        Rectangle bounds = new Rectangle(0, 0, width * YokelGameBoard.MAX_COLS, height * YokelGameBoard.MAX_PLAYABLE_ROWS);
        grid.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        //grid.setCullingArea(bounds);
    }

    private GameBlock getClearBlock(){
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK, isPreview);
    }

    private String getCellAttrName(int r, int c){
        return CELL_ATTR + CELL_ATTR_SEPARATOR + r + CELL_ATTR_SEPARATOR + c;
    }

    public int getBoardNumber(){
        return this.boardNumber;
    }

    void setBoardNumber(int number){
        if(tableNumber == null){
            tableNumber = new Label("", skin);
            tableNumber.setFontScale(4);
            tableNumber.setColor(0,0,0,1);
        }
        this.bgNumber.setName(BOARD_NUMBER_NAME);
        this.boardNumber = number;
        this.tableNumber.setText(number);
        if(this.board != null) {
            this.board.setName(this.board.getId() + " : " + number);
        }
    }

    private void setBlock(int block, int r, int c){
        GameBlock uiCell = uiBlocks.get(getCellAttrName(r, c));

        if(uiCell != null){
            uiCell.update(block, isPreview);
        }
    }

    private boolean isDownCellFree(int column, int row) {
        return row > 0 && row < YokelGameBoard.MAX_PLAYABLE_ROWS + 1 && getPieceValue(column, row - 1) == YokelBlock.CLEAR_BLOCK;
    }

    private int getPieceValue(int c, int r){
        GameBlock uiCell = uiBlocks.get(getCellAttrName(r, c));
        int ret = YokelBlock.CLEAR_BLOCK;
        if(uiCell != null) ret = UIUtil.getInstance().getFactory().getBlockNumber(uiCell.getImage().getName());
        return ret;
    }

    public boolean isPreview(){
        return isPreview;
    }

    public void setPreview(boolean isPreview){
        this.isPreview = isPreview;
        redraw();
    }

    private void redraw(){
        setAreaBounds();
        initializeGrid();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for (GameBlock uiblock : uiBlocks.values()) {
            if (uiblock != null) {
                uiblock.act(delta * YokelUtilities.otof(ANIMATION_OFFSET));
            }
        }
    }

    private boolean validatePiece(){
        return pieceSprite != null && !isPreview && isActive && isPlayerView;
    }

    public boolean isActive() {
        return isActive;
    }

    public void addButtonListener(InputListener listener) {
        joinWindow.setButtonListener(listener);
    }

    public void sitPlayerDown(YokelPlayer player) {
        if(player != null){
            setPlayerSeated(true);
            joinWindow.setIsPlayerReady(true);
        }
    }

    public void setIsPlayerReady(boolean isPlayerReady) {
        joinWindow.setIsPlayerReady(isPlayerReady);
    }

    public void standPlayerUp() {
        setPlayerSeated(false);
    }

    public void hideJoinButton() {
        joinWindow.setVisible(false);
    }

    public void showJoinButton() {
        joinWindow.setVisible(true);
    }

    private static class PieceDrawable extends Actor {
        private final GameBlock[] blocks = new GameBlock[3];
        private int row;
        private int col;
        private boolean isActive;
        private float fallOffset;
        private GameBlockArea parent;

        PieceDrawable(){
            blocks[0] = UIUtil.getBlock(YokelBlock.CLEAR_BLOCK);
            blocks[1] = UIUtil.getBlock(YokelBlock.CLEAR_BLOCK);
            blocks[2] = UIUtil.getBlock(YokelBlock.CLEAR_BLOCK);
        }

        void setActive(boolean b){
            isActive = b;
        }

        @Override
        public void act(float delta){
            super.act(delta);
            if(isActive) {
                for(int i = 0; i < 3; i++){
                    if(blocks[i] != null){
                        blocks[i].act(delta);
                    }
                }
            }
        }

        @Override
        public void draw(Batch batch, float alpha){
            super.draw(batch, alpha);
            if(isActive){
                computePosition();
                float x = getX();
                float y = getY();

                for(int i = 0; i < 3; i++){
                    if(this.blocks[i] != null){
                        this.blocks[i].setPosition(x, y + (i * blocks[i].getHeight() / 2));
                        this.blocks[i].draw(batch, alpha);
                    }
                }
            }
        }

        void setBlocks(YokelPiece piece){
            if(piece != null && isActive){
                updateIndex(0, piece.getBlock1());
                updateIndex(1, piece.getBlock2());
                updateIndex(2, piece.getBlock3());
                this.row = piece.row;
                this.col = piece.column;
            }
        }

        private void updateIndex(int index, int block){
            if(index > -1 && index < blocks.length && blocks[index] != null){
                blocks[index].update(block,false);
            }
        }

        private void setParent(GameBlockArea area){
            this.parent = area;
        }

        private void computePosition() {
            GameBlock block = blocks[0];

            if(block != null){
                //Set the x to the position of the grid
                Vector2 pos = localToParentCoordinates(new Vector2(0, 0));

                float SIDE_BAR_OFFSET = 12;
                pos.x = pos.x / 2 + SIDE_BAR_OFFSET;
                pos.y = pos.y / 2;

                pos.x -= block.getWidth();
                if(parent != null && parent.isDownCellFree(col, row)){
                    pos.y -= ((1 - fallOffset) * block.getHeight() / 2);
                }

                float offSetX = block.getWidth() / 2 * col;
                float offSetY = block.getHeight() / 2 * row;

                this.setPosition(pos.x + offSetX, pos.y + offSetY);
            }
        }
        private void setFallOffset(float fallOffset) {
            this.fallOffset = fallOffset;
        }
    }

    private void setPieceSprite(YokelGameBoard board, float fallOffset){
        if(board != null) {
            YokelPiece piece = board.fetchCurrentPiece();
            if (piece != null) {
                pieceSprite.setBlocks(piece);
                pieceSprite.setParent(this);
                pieceSprite.setFallOffset(fallOffset);
                pieceSprite.setVisible(!board.isPieceSet());
            }
        }
    }

    public Image createImageSprite(Image image, int row, int col) {
        Pool<Image> spritePool = Pools.get(Image.class);
        Image imageSprite = spritePool.obtain();
        if (image != null) {
            YokelUtilities.setHeightFromDrawable(imageSprite, image.getDrawable());
            YokelUtilities.setWidthFromDrawable(imageSprite, image.getDrawable());
            Vector2 pos = grid.localToActorCoordinates(imageSprite, new Vector2(0, 0));
            float offSetX = image.getWidth() * col;
            float offSetY = image.getHeight() * row;
            imageSprite.setPosition(pos.x + offSetX, pos.y + offSetY);
        }
        return imageSprite;
    }

    public void updateData(YokelGameBoard gameBoard) {
        if (gameBoard != null) {
            this.board = gameBoard;
            update();
            setPieceSprite(gameBoard, board.fetchCurrentPieceFallTimer());
            System.out.print(board);
            Queue<YokelBrokenBlock> brokenCells = board.fetchBrokenCells();

            Vector2 pos = localToScreenCoordinates(new Vector2(this.grid.getX(), this.grid.getY()));
            //System.out.println("pieceSprite=" + pieceSprite.getX() + "," + pieceSprite.getY());
            //System.out.println("pieceSprite localCords=" + pos);

            int count = 0;
            if (brokenCells != null && brokenCells.size > 0) {
                for (YokelBrokenBlock brokenBlock : YokelUtilities.safeIterable(brokenCells)) {
                    int block = YokelBlockEval.getCellFlag(brokenBlock.getBlock());
                    String brokenName = YokelObjectFactory.getBrokenBlockName(block);
                    int col = brokenBlock.getCol();
                    int row = brokenBlock.getRow();
                    float localDuration = 0.8f;
                    float delay = count * 0.1f;
                    count++;

                    System.out.println("broken pos: " + pos);
                    //System.out.println("broken cell: " + brokenBlockImageRight);
                    System.out.println("broken row: " + row);
                    System.out.println("broken col: " + col);


                    Image tempRight = UIUtil.getBrokenBlock(brokenName + "_right");
                    Image brokenBlockImageRight = null;
                    if (tempRight != null) {
                        brokenBlockImageRight = new Image(tempRight.getDrawable());
                        YokelUtilities.setHeightFromDrawable(brokenBlockImageRight, brokenBlockImageRight.getDrawable());
                        YokelUtilities.setWidthFromDrawable(brokenBlockImageRight, brokenBlockImageRight.getDrawable());
                        float offSetX = brokenBlockImageRight.getWidth() * col;
                        float offSetY = brokenBlockImageRight.getHeight() * row + (brokenBlockImageRight.getHeight());
                        System.out.println("broken offSetX: " + offSetX);
                        System.out.println("broken offSetY: " + offSetY);

                        brokenBlockImageRight.setPosition(pos.x + offSetX, pos.y + offSetY - (16 * 13));
                        brokenBlockImageRight.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(pos.x, 0, localDuration, Interpolation.slowFast)
                                , Actions.removeActor(brokenBlockImageRight)));
                        getStage().addActor(brokenBlockImageRight);
                    }

                    Image tempLeft = UIUtil.getBrokenBlock(brokenName + "_left");
                    Image brokenBlockImageLeft = null;
                    if (tempLeft != null) {
                        brokenBlockImageLeft = new Image(tempLeft.getDrawable());
                        YokelUtilities.setHeightFromDrawable(brokenBlockImageLeft, brokenBlockImageLeft.getDrawable());
                        YokelUtilities.setWidthFromDrawable(brokenBlockImageLeft, brokenBlockImageLeft.getDrawable());
                        float offSetX = brokenBlockImageLeft.getWidth() * col;
                        float offSetY = brokenBlockImageLeft.getHeight() * row + (brokenBlockImageLeft.getHeight());

                        brokenBlockImageLeft.setPosition(pos.x + offSetX, pos.y + offSetY - (16 * 13));
                        brokenBlockImageLeft.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(pos.x + 48, 0, localDuration, Interpolation.slowFast)
                                , Actions.removeActor(brokenBlockImageLeft)));
                        getStage().addActor(brokenBlockImageLeft);
                    }

                    Image tempBottom = UIUtil.getBrokenBlock(brokenName + "_bottom");
                    Image brokenBlockImageBottom = null;
                    if (tempBottom != null) {
                        brokenBlockImageBottom = new Image(tempBottom.getDrawable());
                        YokelUtilities.setHeightFromDrawable(brokenBlockImageBottom, brokenBlockImageBottom.getDrawable());
                        YokelUtilities.setWidthFromDrawable(brokenBlockImageBottom, brokenBlockImageBottom.getDrawable());
                        float offSetX = brokenBlockImageBottom.getWidth() * col;
                        float offSetY = brokenBlockImageBottom.getHeight() * row + (brokenBlockImageBottom.getHeight());

                        brokenBlockImageBottom.setPosition(pos.x + offSetX, pos.y + offSetY - (16 * 13));
                        brokenBlockImageBottom.addAction(Actions.sequence(Actions.delay(delay), Actions.moveTo(pos.x + 24, 0, localDuration, Interpolation.slowFast)
                                , Actions.removeActor(brokenBlockImageBottom)));
                        getStage().addActor(brokenBlockImageBottom);
                    }


                    if (isPreview()) {
                        getStage().addActor(brokenBlockImageLeft);
                    } else {
                        getStage().addActor(brokenBlockImageLeft);
                        getStage().addActor(brokenBlockImageRight);
                        getStage().addActor(brokenBlockImageBottom);
                    }
                }
            }
        }
    }

    private void update(){
        if(board != null){
            for(int r = 0; r < YokelGameBoard.MAX_PLAYABLE_ROWS; r++){
                for(int c = 0; c < YokelGameBoard.MAX_COLS; c++){
                    setBlock(getBlockValueFromGameBoard(board, r, c), r, c);
                }
            }
        }
    }

    private int getBlockValueFromGameBoard(YokelGameBoard board, int r, int c) {
        int block = YokelBlock.CLEAR_BLOCK;
        if (board != null) {
            block = board.getBlockValueAt(c, r);
        }
        return block;
    }

    public void setActive(boolean b) {
        this.isActive = b;
        if (isActive) {
            if (isPlayerView) {
                bgColor.setBackground(skin.getDrawable(PLAYER_BACKGROUND));
            } else {
                bgColor.setBackground(skin.getDrawable(ALIVE_BACKGROUND));
            }
        }
    }

    public void setGameReady(boolean gameReady) {
        joinWindow.setIsGameReady(gameReady);
    }

    void setPlayerSeated(boolean b) {
        joinWindow.setSeated(b);
    }

    public void setPlayerView(boolean b) {
        this.isPlayerView = b;
        this.pieceSprite.setActive(!isPreview && isPlayerView);
        if (isPlayerView) {
            bgColor.setBackground(skin.getDrawable(PLAYER_BACKGROUND));
        }
    }

    void killPlayer(){
        bgColor.setBackground(skin.getDrawable(DEAD_BACKGROUND));
        setActive(false);
    }

    YokelGameBoard getBoard(){
        return board;
    }
}
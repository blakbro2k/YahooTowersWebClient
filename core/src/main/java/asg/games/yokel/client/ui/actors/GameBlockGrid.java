package asg.games.yokel.client.ui.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;
import asg.games.yokel.objects.YokelBlockEval;
import asg.games.yokel.objects.YokelGameBoard;
import asg.games.yokel.objects.YokelGameBoardState;
import asg.games.yokel.objects.YokelPiece;
import asg.games.yokel.utils.YokelUtilities;

public class GameBlockGrid extends Stack {
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

    private final Skin skin;

    private Table grid;
    private Table bgNumber;
    private Table bgColor;
    private Label tableNumber;

    private final ObjectMap<String, GameBlock> uiBlocks = GdxMaps.newObjectMap();
    private GamePieceDrawable pieceSprite;

    public GameBlockGrid(Skin skin, boolean isPreview) {
        this.skin = skin;
        this.isPreview = isPreview;
        init();
    }

    public GameBlockGrid(Skin skin) {
        this(skin, true);
    }

    private void init(){
        initializeBoard();
        //initializeSize();

        setBoardNumber(0);
        bgColor.setBackground(skin.getDrawable(ALIVE_BACKGROUND));
        add(bgColor);
        bgNumber.add(tableNumber);
        bgNumber.add("");
        add(bgNumber);
        initializeGrid();
        invalidate();
    }

    private void initializeSize(){
        GameBlock clear = getClearBlock();
        float width = clear.getWidth() * YokelGameBoard.MAX_COLS;
        float height = clear.getHeight() * YokelGameBoard.MAX_PLAYABLE_ROWS;
    }

    private void initializeBoard(){
        this.boardNumber = 0;
        this.grid = new Table(skin);
        this.bgColor = new Table(skin);
        this.bgNumber = new Table(skin);
        this.pieceSprite = new GamePieceDrawable(skin);
    }

    public void setDebug (boolean enabled) {
        super.setDebug(YokelUtilities.setDebug(enabled, grid, bgNumber, bgColor, pieceSprite));
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
        grid.add(pieceSprite);
        add(grid);
        setAreaBounds();
    }

    private void setAreaBounds(){
        GameBlock block = getClearBlock();
        float width = block.getWidth();
        float height = block.getHeight();
        Rectangle bounds = new Rectangle(0, 0, width * YokelGameBoard.MAX_COLS, height * YokelGameBoard.MAX_PLAYABLE_ROWS);
        grid.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        //grid.setCullingArea(bounds);
    }

    private GameBlock getClearBlock(){
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK, isPreview);
    }

    private String getCellAttrName(int r, int c) {
        return CELL_ATTR + CELL_ATTR_SEPARATOR + r + CELL_ATTR_SEPARATOR + c;
    }

    public int getBoardNumber() {
        return this.boardNumber;
    }

    public void setBoardNumber(int number) {
        if (tableNumber == null) {
            tableNumber = new Label("", skin);
            tableNumber.setFontScale(4);
            tableNumber.setColor(0, 0, 0, 1);
        }
        this.bgNumber.setName(BOARD_NUMBER_NAME);
        this.boardNumber = number;
        this.tableNumber.setText(number);
    }

    private void setBlock(int block, int r, int c) {
        GameBlock uiCell = uiBlocks.get(getCellAttrName(r, c));

        if (uiCell != null) {
            uiCell.setPreview(isPreview);
            uiCell.setImage(YokelBlockEval.getCellFlag(block));
        }
    }

    public GameBlock getGameBlock(int r, int c) {
        return uiBlocks.get(getCellAttrName(r, c));
    }


    boolean isDownCellFree(int column, int row) {
        return row > 0 && row < YokelGameBoard.MAX_PLAYABLE_ROWS + 1 && getPieceValue(column, row - 1) == YokelBlock.CLEAR_BLOCK;
    }

    private int getPieceValue(int c, int r) {
        GameBlock uiCell = uiBlocks.get(getCellAttrName(r, c));
        int ret = YokelBlock.CLEAR_BLOCK;
        if (uiCell != null)
            ret = UIUtil.getInstance().getFactory().getBlockNumber(uiCell.getImage().getName());
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

        for (GameBlock uiblock : YokelUtilities.getMapValues(uiBlocks)) {
            if (uiblock != null) {
                uiblock.act(delta * YokelUtilities.otof(ANIMATION_OFFSET));
            }
        }
    }

    private boolean validatePiece() {
        return pieceSprite != null && !isPreview && isActive && isPlayerView;
    }

    public boolean isActive() {
        return isActive;
    }


    private void setPieceSprite(YokelPiece piece, float fallOffset) {
        if (piece != null) {
            pieceSprite.setBlocks(piece);
            pieceSprite.setParent(this);
            pieceSprite.setFallOffset(fallOffset);
            pieceSprite.setVisible(true);
        } else {
            pieceSprite.setVisible(false);
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

    public void renderBoard(YokelGameBoard gameBoard) {
        if (gameBoard != null) {
            for (int r = 0; r < YokelGameBoard.MAX_PLAYABLE_ROWS; r++) {
                for (int c = 0; c < YokelGameBoard.MAX_COLS; c++) {
                    setBlock(getBlockValueFromGameBoard(gameBoard, r, c), r, c);
                }
            }
        }
    }

    public void renderBoard(YokelGameBoardState state) {
        if (state != null) {
            //Render cells
            int[][] cells = state.getPlayerCells();
            for (int r = 0; r < YokelGameBoard.MAX_PLAYABLE_ROWS; r++) {
                for (int c = 0; c < YokelGameBoard.MAX_COLS; c++) {
                    setBlock(cells[r][c], r, c);
                }
            }

            //Render Piece
            setPieceSprite(state.getPiece(), state.getPieceFallTimer());
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
        this.pieceSprite.setActive(b);
        if (isActive) {
            if (isPlayerView) {
                bgColor.setBackground(skin.getDrawable(PLAYER_BACKGROUND));
            } else {
                bgColor.setBackground(skin.getDrawable(ALIVE_BACKGROUND));
            }
        }
    }

    public void setPlayerView(boolean b) {
        this.isPlayerView = b;
        this.pieceSprite.setActive(!isPreview && isPlayerView);
        if (isPlayerView) {
            bgColor.setBackground(skin.getDrawable(PLAYER_BACKGROUND));
        }
    }

    void killPlayer() {
        bgColor.setBackground(skin.getDrawable(DEAD_BACKGROUND));
        setActive(false);
    }


}
package asg.games.yokel.client.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.scene2d.ui.reflected.AnimatedImage;

import asg.games.yokel.client.factories.YokelObjectFactory;
import asg.games.yokel.client.objects.YokelBlock;
import asg.games.yokel.client.objects.YokelBlockEval;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.ui.actors.GameBlockGrid;
import asg.games.yokel.client.ui.actors.GameBrokenBlockSpriteContainer;
import lombok.Setter;

public class UIUtil {
    private static final UIUtil myInstance = new UIUtil();
    private static final float YAHOO_DURATION = 1.26f;
    private static final String PREVIEW_TAG = "_preview";
    private static final float BLOCK_BREAK_DURATION = 0.65f;
    private static final int YAHOO_X_CENTER_OFFSET = 300;
    private static final int YAHOO_Y_CENTER_OFFSET = 300;
    private static final int YAHOO_STARBURST_ADDED_WIDTH = 40;
    private static final Interpolation defaultInterpolation = Interpolation.pow3In;
    private SoundUtil soundUtil;

    @Setter
    private YokelObjectFactory factory;

    public static UIUtil getInstance() {
        return myInstance;
    }

    public static void animateStandardBlockBreak(Stage stage, OrderedSet<GameBrokenBlockSpriteContainer> queue) {
        animateStandardBlockBreak(stage, queue, defaultInterpolation);
    }

    public static void animateStandardBlockBreak(Stage stage, OrderedSet<GameBrokenBlockSpriteContainer> queue, Interpolation interpolation) {
        final float duration = BLOCK_BREAK_DURATION;
        final float delay = 0.05f;
        final float blockOffSet = 32f;
        final float endOfScreen = 0f;

        if (queue.size == 0) return;

        ObjectSet.ObjectSetIterator<GameBrokenBlockSpriteContainer> brokenIterator = queue.iterator();

        if (brokenIterator.hasNext()) {
            GameBrokenBlockSpriteContainer brokenGameSprite = brokenIterator.next();
            if (brokenGameSprite != null) {

                Image left = brokenGameSprite.getLeftSprite();
                Image bottom = brokenGameSprite.getBottomSprite();
                Image right = brokenGameSprite.getRightSprite();
                GameBlock parent = brokenGameSprite.getParentGameBlock();

                GameBlockGrid grid = brokenGameSprite.getGrid();

                if (parent != null) {
                    parent.setBlock(asg.games.yokel.objects.YokelBlockEval.addBrokenFlag(parent.getBlock()));
                    float parentX = parent.getX();
                    float parentY = parent.getY();

                    //Get the coordinates of the parent block
                    Vector2 blockV = left.localToParentCoordinates(new Vector2(parentX, parentY));

                    if (grid != null) {
                        GameBlock gameBlock = grid.getGameBlock(brokenGameSprite.getRow(), brokenGameSprite.getCol());
                        blockV = grid.localToScreenCoordinates(new Vector2(gameBlock.getX(), gameBlock.getY()));
                    }

                    //Get the screen coordinates
                    Vector2 blockV2 = left.localToScreenCoordinates(new Vector2(blockV.x, blockV.y));

                    //Shift (and flip) block y
                    blockV2.x -= parentX;
                    blockV2.y = 1 - (blockV2.y - left.getWidth()) + stage.getViewport().getScreenHeight();

                    left.setBounds(blockV2.x, blockV2.y, left.getWidth(), left.getHeight());
                    left.addAction(Actions.sequence(Actions.delay(delay),
                            Actions.moveTo(blockV2.x - blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(left)));

                    bottom.setBounds(blockV2.x, blockV2.y, bottom.getWidth(), bottom.getHeight());
                    bottom.addAction(Actions.sequence(Actions.delay(delay),
                            Actions.moveTo(blockV2.x, endOfScreen, duration, interpolation), Actions.removeActor(bottom)));

                    right.setBounds(blockV2.x, blockV2.y, right.getWidth(), right.getHeight());
                    right.addAction(Actions.sequence(Actions.delay(delay),
                            Actions.moveTo(blockV2.x + blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(right)));

                    int brokenSize = queue.size;
                    if (parent.isPreview()) {
                        if (brokenSize % 3 == 1) {
                            stage.addActor(left);
                        } else if (brokenSize % 3 == 2) {
                            stage.addActor(right);
                        } else {
                            stage.addActor(bottom);
                        }
                    } else {
                        stage.addActor(left);
                        stage.addActor(bottom);
                        stage.addActor(right);
                    }
                }
                brokenIterator.remove();
                Pools.free(brokenGameSprite);
            }
        }
    }

    public static void animateYahooBlockBreak(Stage stage, OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue, float duration) {
        if (stage == null || brokenBlocksQueue == null || brokenBlocksQueue.size == 0) return;

        final int points = Math.max(brokenBlocksQueue.size, 3);
        final double angle = Math.PI / points;

        Array<Vector2> vectors = getPolygonVertices(points, stage.getWidth() + YAHOO_STARBURST_ADDED_WIDTH, YAHOO_X_CENTER_OFFSET, YAHOO_Y_CENTER_OFFSET);
        Queue<Vector2> yahooStarEnds = new Queue<>();
        double jitter = MathUtils.random(-0.2f, 0.2f); // subtle variation
        for (Vector2 vector : vectors) {
            yahooStarEnds.addLast(
                    rotatePoint(vector.x, vector.y, YAHOO_X_CENTER_OFFSET, YAHOO_Y_CENTER_OFFSET, angle + jitter)
            );
        }

        for (Vector2 vector : vectors) {
            yahooStarEnds.addLast(rotatePoint(vector.x, vector.y, YAHOO_X_CENTER_OFFSET, YAHOO_Y_CENTER_OFFSET, angle));
        }

        ObjectSet.ObjectSetIterator<GameBrokenBlockSpriteContainer> iterator = brokenBlocksQueue.iterator();

        while (iterator.hasNext()) {
            GameBrokenBlockSpriteContainer sprite = iterator.next();
            if (sprite == null) continue;

            GameBlock parent = sprite.getParentGameBlock();
            GameBlockGrid grid = sprite.getGrid();
            Image left = sprite.getLeftSprite();
            Image bottom = sprite.getBottomSprite();
            Image right = sprite.getRightSprite();

            if (parent != null) {
                Image block = new Image(parent.getImage().getDrawable());
                parent.setBlock(YokelBlockEval.addBrokenFlag(parent.getBlock()));

                float parentX = parent.getX();
                float parentY = parent.getY();
                Vector2 blockV = left.localToParentCoordinates(new Vector2(parentX, parentY));

                if (grid != null) {
                    GameBlock gameBlock = grid.getGameBlock(sprite.getRow(), sprite.getCol());
                    blockV = grid.localToScreenCoordinates(new Vector2(gameBlock.getX(), gameBlock.getY()));
                }

                Vector2 blockV2 = left.localToScreenCoordinates(new Vector2(blockV.x, blockV.y));
                blockV2.x -= parentX;
                blockV2.y = 1 - (blockV2.y) + stage.getHeight();

                block.setVisible(true);
                block.setBounds(blockV2.x, blockV2.y, block.getWidth(), block.getHeight());

                Vector2 end = yahooStarEnds.size > 0 ? yahooStarEnds.removeFirst() : new Vector2(blockV2.x, blockV2.y);
                block.addAction(Actions.sequence(
                        Actions.moveTo(end.x, end.y, duration, Interpolation.smoother),
                        Actions.removeActor(block)));

                left.setVisible(false);
                bottom.setVisible(false);
                right.setVisible(false);

                stage.addActor(block);
            }

            Pools.free(sprite);
            iterator.remove();
        }
    }

    public YokelObjectFactory getFactory(){
        if (factory == null) throw new GdxRuntimeException("YokelObjectFactory was not set.  Please call setFactory(YokelObjectFactory factory) to set an active YokelObjectFactory object");
        return factory;
    }

    public void setSoundUtil(SoundUtil soundUtil) {
        if(soundUtil != null) {
            this.soundUtil = soundUtil;
        }
    }

    public float getSoundDuration(Sound sound) {
        if (soundUtil != null) {
            return soundUtil.getDuration(sound);
        }
        throw new GdxRuntimeException("Cannot get Sound Duration, was SoundUtil initialized?");
    }

    public Image getBlockImage(String blockName) {
        return (Image) getFactory().getUserInterfaceService().getActor(blockName);
    }

    public Image getBrokenBlockImage(String brokenBlockName) {
        return (Image) getFactory().getUserInterfaceService().getActor(brokenBlockName);
    }

    public Image getPreviewBlockImage(String blockName) {
        if (YokelUtilities.containsIgnoreCase(blockName, PREVIEW_TAG)) {
            blockName = blockName.substring(0, blockName.indexOf(PREVIEW_TAG));
        }
        return getBlockImage(blockName + PREVIEW_TAG);
    }

    public Image getBlockImage(int blockId) {
        return getBlockImage(getFactory().getBlockImageName(blockId));
    }

    public Image getPreviewBlockImage(int blockId) {
        return getBlockImage(getFactory().getBlockImageName(blockId) + PREVIEW_TAG);
    }

    public static Array<Drawable> getAniImageFrames(AnimatedImage image){
        Array<Drawable> drawables = new Array<>();
        if(image != null){
            for(Drawable frame : YokelUtilities.safeIterable(image.getFrames())){
                if(frame != null){
                    drawables.add(frame);
                }
            }
        }
        return drawables;
    }

    public GameBlock getGameBlock(int blockId, boolean preview) {
        return getFactory().getGameBlock(blockId, preview);
    }

    public static GameBlock getClearBlock(boolean isPreview) {
        return UIUtil.getInstance().getGameBlock(YokelBlock.CLEAR_BLOCK, isPreview);
    }

    public static GameBlock getBlock(int block) {
        return UIUtil.getInstance().getGameBlock(block, false);
    }

    public static GameBlock getBlock(int block, boolean isPreview) {
        return UIUtil.getInstance().getGameBlock(block, isPreview);
    }

    public static Image getBrokenBlock(String brokenBlockName) {
        return UIUtil.getInstance().getBrokenBlockImage(brokenBlockName);
    }

    public static void setActorNameFromActor(Actor actor, Actor actorToName) {
        if (actor != null && actorToName != null) {
            actor.setName(actorToName.getName());
        }
    }

    public static void freeBlock(GameBlock uiCell) {
        Pools.free(uiCell);
    }

    public static GameBlock updateGameBlock(GameBlock original, int block, boolean isPreview) {
        GameBlock incoming = getBlock(block, isPreview);
        if (original != null && !original.equals(incoming)) {
            Pools.free(original);
            return incoming;
        } else {
            Pools.free(incoming);
            return original;
        }
    }

    public static int getTrueBlock(int block) {
        if (YokelBlockEval.hasAddedByYahooFlag(block) || YokelBlockEval.hasBrokenFlag(block)) {
            return YokelBlockEval.getCellFlag(block);
        } else {
            return YokelBlockEval.getIDFlag(YokelBlockEval.getID(block), block);
        }
    }

    public static Image getNewImage(Image image) {
        Image nuImage = null;
        if (image != null) {
            nuImage = new Image(image.getDrawable());
        }
        return nuImage;
    }

    public static GameBrokenBlockSpriteContainer getBrokenBlockSprites(GameBlock parent, int block, GameBlockGrid grid, int row, int col) {
        String brokenBlockString = UIUtil.getInstance().factory.getBlockImageName(YokelBlockEval.addBrokenFlag(block));
        GameBrokenBlockSpriteContainer spriteContainer = Pools.obtain(GameBrokenBlockSpriteContainer.class);

        Image left = getNewImage(myInstance.getBlockImage(brokenBlockString + "_left"));
        Image bottom = getNewImage(myInstance.getBlockImage(brokenBlockString + "_bottom"));
        Image right = getNewImage(myInstance.getBlockImage(brokenBlockString + "_right"));

        if (parent != null) {
            parent.addActor(left);
            parent.addActor(bottom);
            parent.addActor(right);
        }

        spriteContainer.setParentGameBlock(parent);
        spriteContainer.setLeftSprite(left);
        spriteContainer.setBottomSprite(bottom);
        spriteContainer.setRightSprite(right);
        spriteContainer.setBlock(block);
        spriteContainer.setGrid(grid);
        spriteContainer.setRow(row);
        spriteContainer.setCol(col);
        spriteContainer.setName("" + YokelBlockEval.getNormalLabel(block));
        return spriteContainer;
    }

    public static void addBrokenBlockActorToStage(Stage stage, boolean isYahoo, OrderedSet<GameBrokenBlockSpriteContainer> brokenBlocksQueue) {
        if (stage != null && brokenBlocksQueue != null) {
            ObjectSet.ObjectSetIterator<GameBrokenBlockSpriteContainer> brokenIterator = brokenBlocksQueue.iterator();

            if (isYahoo) {
                UIUtil.animateYahooBlockBreak(stage, brokenBlocksQueue, YAHOO_DURATION);
            } else {
                System.out.println("stage=" + stage);
                System.out.println("brokenBlocksQueue=" + brokenBlocksQueue);
                UIUtil.animateStandardBlockBreak(stage, brokenBlocksQueue);

                if (brokenIterator.hasNext()) {
                    GameBrokenBlockSpriteContainer brokenGameSprite = brokenIterator.next();
                    if (brokenGameSprite != null) {
                        float duration = BLOCK_BREAK_DURATION;
                        float delay = 0.05f;
                        float endOfScreen = 0f;
                        float blockOffSet = 32;

                        Interpolation interpolation = Interpolation.sineIn;

                        Image left = brokenGameSprite.getLeftSprite();
                        Image bottom = brokenGameSprite.getBottomSprite();
                        Image right = brokenGameSprite.getRightSprite();
                        GameBlock parent = brokenGameSprite.getParentGameBlock();

                        GameBlockGrid grid = brokenGameSprite.getGrid();

                        if (parent != null) {
                            parent.setBlock(YokelBlockEval.addBrokenFlag(parent.getBlock()));
                            float parentX = parent.getX();
                            float parentY = parent.getY();

                            //Get the coordinates of the parent block
                            Vector2 blockV = left.localToParentCoordinates(new Vector2(parentX, parentY));

                            if (grid != null) {
                                GameBlock gameBlock = grid.getGameBlock(brokenGameSprite.getRow(), brokenGameSprite.getCol());
                                blockV = grid.localToScreenCoordinates(new Vector2(gameBlock.getX(), gameBlock.getY()));
                            }

                            //Get the screen coordinates
                            Vector2 blockV2 = left.localToScreenCoordinates(new Vector2(blockV.x, blockV.y));

                            //Shift (and flip) block y
                            blockV2.x -= parentX;
                            blockV2.y = 1 - (blockV2.y - left.getWidth()) + stage.getViewport().getScreenHeight();

                            left.setBounds(blockV2.x, blockV2.y, left.getWidth(), left.getHeight());
                            left.addAction(Actions.sequence(Actions.delay(delay),
                                    Actions.moveTo(blockV2.x - blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(left)));

                            bottom.setBounds(blockV2.x, blockV2.y, bottom.getWidth(), bottom.getHeight());
                            bottom.addAction(Actions.sequence(Actions.delay(delay),
                                    Actions.moveTo(blockV2.x, endOfScreen, duration, interpolation), Actions.removeActor(bottom)));

                            right.setBounds(blockV2.x, blockV2.y, right.getWidth(), right.getHeight());
                            right.addAction(Actions.sequence(Actions.delay(delay),
                                    Actions.moveTo(blockV2.x + blockOffSet, endOfScreen, duration, interpolation), Actions.removeActor(right)));

                            int brokenSize = brokenBlocksQueue.size;
                            if (parent.isPreview()) {
                                if (brokenSize % 3 == 1) {
                                    stage.addActor(left);
                                } else if (brokenSize % 3 == 2) {
                                    stage.addActor(right);
                                } else {
                                    stage.addActor(bottom);
                                }
                            } else {
                                stage.addActor(left);
                                stage.addActor(bottom);
                                stage.addActor(right);
                            }
                        }
                        brokenIterator.remove();
                        Pools.free(brokenGameSprite);
                    }
                }
            }
        }
    }

    public static Array<Vector2> getPolygonVertices(int n, float radius, int h, int k) {
        Array<Vector2> verts = GdxArrays.newArray();
        double angle_between_vertices = 2 * Math.PI / n;

        for (int i = n; i >= 0; i--) {
            double theta = i * angle_between_vertices;
            double x = h + radius * Math.cos(theta);
            double y = k + radius * Math.sin(theta);

            verts.add(new Vector2((float) x, (float) y));
        }
        return verts;
    }

    public static Vector2 rotatePoint(double x, double y, double Cx, double Cy, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        double newX = Cx + (x - Cx) * cosTheta - (y - Cy) * sinTheta;
        double newY = Cy + (x - Cx) * sinTheta + (y - Cy) * cosTheta;

        return new Vector2((float) newX, (float) newY);
    }

    private static final Pool<Label> labelPool = new Pool<Label>() {
        @Override
        protected Label newObject() {
            Skin skin = getInstance().getFactory().getUserInterfaceService().getSkin();
            Label.LabelStyle style = skin.has("default", Label.LabelStyle.class) ? skin.get(Label.LabelStyle.class) : null;

            if (style == null || style.font == null) {
                Gdx.app.error("UIUtil", "LabelStyle 'default' is missing or has null font. Falling back to hardcoded font.");
                style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
            }
            // Default fallback — this will be overridden per animation request
            return new Label("", style);
        }
    };

    public static void animateLabelFlyIn(Label original, float delay, float duration) {
        if (original == null || original.getStage() == null || original.getStyle() == null) {
            Gdx.app.error("GameLabelAnimator", "Cannot animate: missing label, stage, or style.");
            return;
        }

        original.setVisible(false);

        // Obtain a label from the pool
        Label clone = labelPool.obtain();
        clone.setStyle(original.getStyle());
        clone.setText(original.getText());
        clone.setAlignment(original.getLabelAlign());
        clone.setFontScale(original.getFontScaleX(), original.getFontScaleY());
        clone.setColor(original.getColor());

        Vector2 screenPos = original.localToStageCoordinates(new Vector2(0, 0));
        Stage stage = original.getStage();
        clone.setPosition(screenPos.x, -clone.getHeight());

        stage.addActor(clone);

        clone.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.moveTo(screenPos.x, screenPos.y, duration, Interpolation.fade),
                Actions.run(() -> {
                    original.setVisible(true);
                    clone.remove();
                    labelPool.free(clone); // return to pool
                })
        ));
    }

    public static void animateLabelFlyInLetterByLetter(Label original, float delayBetweenChars, float duration) {
        if (original == null || original.getStage() == null || original.getStyle() == null) {
            Gdx.app.error("GameLabelAnimator", "Cannot animate: missing label, stage, or style.");
            return;
        }

        original.setVisible(false);
        String text = original.getText().toString();
        if (text.isEmpty()) return;

        Stage stage = original.getStage();
        Vector2 screenPos = original.localToStageCoordinates(new Vector2(0, 0));
        float x = screenPos.x, y = screenPos.y;
        float fontScaleX = original.getFontScaleX();
        float fontScaleY = original.getFontScaleY();
        Color originalColor = original.getColor();

        BitmapFont font = original.getStyle().font;
        GlyphLayout layout = new GlyphLayout();

        float spacing = 0f;
        Array<Label> animatedChars = new Array<>();

        for (int i = 0; i < text.length(); i++) {
            String charStr = String.valueOf(text.charAt(i));

            layout.setText(font, charStr); // get width of each char
            float charWidth = layout.width * fontScaleX;

            Label charLabel = labelPool.obtain();
            charLabel.setText(""); // clear previous content
            charLabel.clearActions();
            charLabel.clearListeners();
            charLabel.setVisible(true);
            charLabel.setSize(0, 0); // reset size
            charLabel.invalidateHierarchy(); // force layout update

            charLabel.setStyle(original.getStyle());
            charLabel.setText(charStr);
            charLabel.setFontScale(fontScaleX, fontScaleY);
            charLabel.setColor(originalColor);
            charLabel.setAlignment(original.getLabelAlign());
            charLabel.pack();

            float finalX = x + spacing;
            charLabel.setPosition(finalX, -charLabel.getHeight());
            stage.addActor(charLabel);
            animatedChars.add(charLabel);

            charLabel.addAction(Actions.sequence(
                    Actions.delay(i * delayBetweenChars),
                    Actions.moveTo(finalX, y, duration, Interpolation.fade)
            ));

            spacing += charWidth + 1f;
        }

        // Cleanup
        float totalDelay = (text.length() - 1) * delayBetweenChars + duration;
        stage.addAction(Actions.sequence(
                Actions.delay(totalDelay),
                Actions.run(() -> {
                    for (Label label : animatedChars) {
                        label.remove();
                        labelPool.free(label);
                    }
                    original.setVisible(true);
                })
        ));
    }



}
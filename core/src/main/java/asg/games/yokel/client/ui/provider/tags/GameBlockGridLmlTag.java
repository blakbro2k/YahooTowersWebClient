package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlockGrid;
import asg.games.yokel.client.utils.YokelUtilities;

public class GameBlockGridLmlTag extends AbstractNonParentalActorLmlTag {
    public GameBlockGridLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new GameBlockGrid(getSkin(builder));
    }

    protected GameBlockGrid getGameBlockGrid() {
        return (GameBlockGrid) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        if (!YokelUtilities.isEmpty(plainTextLine)) {
            GameBlockGrid getGameBlockGrid = getGameBlockGrid();
            getGameBlockGrid.setBoardNumber(YokelUtilities.otoi(plainTextLine));
        }
    }
}
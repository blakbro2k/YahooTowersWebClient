package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameNameLabel;
import asg.games.yokel.client.utils.YokelUtilities;

public class GameNameLabelLmlTag extends AbstractNonParentalActorLmlTag {
    public GameNameLabelLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new GameNameLabel(getSkin(builder));
    }

    protected GameNameLabel getGameNameLabel() {
        return (GameNameLabel) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        if (!YokelUtilities.isEmpty(plainTextLine)) {
            GameNameLabel getGameNameLabel = getGameNameLabel();
            final String textToAppend = getParser().parseString(plainTextLine, getGameNameLabel);
            getGameNameLabel.setNameTag(textToAppend);
        }
    }
}

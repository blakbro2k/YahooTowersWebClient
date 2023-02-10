package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlockArea;

public class GameBlockAreaLmlTag extends AbstractNonParentalActorLmlTag {
    public GameBlockAreaLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new GameBlockArea(getSkin(builder));
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
    }
}
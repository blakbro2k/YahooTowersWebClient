package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.utils.UIUtil;
import asg.games.yokel.objects.YokelBlock;

public class GameBlockLmlTag extends AbstractNonParentalActorLmlTag {
    public GameBlockLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(LmlActorBuilder builder) {
        return UIUtil.getBlock(YokelBlock.CLEAR_BLOCK);
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
    }
}

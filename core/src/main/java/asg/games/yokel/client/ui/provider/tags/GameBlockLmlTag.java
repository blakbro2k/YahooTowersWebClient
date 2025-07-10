package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yipee.libgdx.objects.YipeeBlockGDX;
import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.utils.UIUtil;

public class GameBlockLmlTag extends AbstractNonParentalActorLmlTag {
    public GameBlockLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(LmlActorBuilder builder) {

        return UIUtil.getBlock(YipeeBlockGDX.CLEAR_BLOCK);
    }

    protected GameBlock getGameBlock() {
        return (GameBlock) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {

    }
}

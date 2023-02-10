package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlock;

public class GameBlockTypeLmlAttribute implements LmlAttribute<GameBlock> {
    public GameBlockTypeLmlAttribute() {
    }

    public Class<GameBlock> getHandledType() {
        return GameBlock.class;
    }

    public void process(final LmlParser parser, final LmlTag tag, final GameBlock actor, final String rawAttributeData) {
        actor.setImage(parser.parseString(rawAttributeData, actor));
    }
}
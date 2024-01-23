package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlock;

public class GameBlockPreviewLmlAttribute implements LmlAttribute<GameBlock> {
    public GameBlockPreviewLmlAttribute() {
    }

    public Class<GameBlock> getHandledType() {
        return GameBlock.class;
    }

    public void process(LmlParser parser, LmlTag tag, GameBlock actor, String rawAttributeData) {
        actor.setPreview(parser.parseBoolean(rawAttributeData));
    }
}
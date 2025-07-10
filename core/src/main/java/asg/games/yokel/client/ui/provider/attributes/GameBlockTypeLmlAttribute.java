package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlock;
import asg.games.yokel.client.objects.YokelBlock;
import asg.games.yokel.client.utils.YokelUtilities;

public class GameBlockTypeLmlAttribute implements LmlAttribute<GameBlock> {
    public GameBlockTypeLmlAttribute() {
    }

    public Class<GameBlock> getHandledType() {
        return GameBlock.class;
    }

    public void process(final LmlParser parser, final LmlTag tag, final GameBlock actor, final String rawAttributeData) {
        String parsedString = parser.parseString(rawAttributeData, actor);
        int block = YokelBlock.CLEAR_BLOCK;
        try {
            block = YokelUtilities.otoi(parsedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        actor.setBlock(block);
    }
}
package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yipee.libgdx.game.YipeeGameBoardGDX;
import asg.games.yokel.client.ui.actors.GameBlockGrid;

public class GameBlockAreaDataLmlAttribute implements LmlAttribute<GameBlockGrid> {
    public GameBlockAreaDataLmlAttribute() {
    }

    public Class<GameBlockGrid> getHandledType() {
        return GameBlockGrid.class;
    }

    public void process(final LmlParser parser, final LmlTag tag, GameBlockGrid actor, final String rawAttributeData) {
        final ActorConsumer<?, GameBlockGrid> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwError("Could not find action for: " + rawAttributeData + " with actor: " + actor);
        } else {
            actor.renderBoard((YipeeGameBoardGDX) action.consume(actor));
        }
    }
}
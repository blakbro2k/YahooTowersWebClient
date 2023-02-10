package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlockArea;
import asg.games.yokel.objects.YokelGameBoard;

public class GameBlockAreaDataLmlAttribute implements LmlAttribute<GameBlockArea> {
    public GameBlockAreaDataLmlAttribute() {}

    public Class<GameBlockArea> getHandledType() {
        return GameBlockArea.class;
    }

    public void process(final LmlParser parser, final LmlTag tag, GameBlockArea actor, final String rawAttributeData) {
        final ActorConsumer<?, GameBlockArea> action = parser.parseAction(rawAttributeData, actor);
        if (action == null) {
            parser.throwError("Could not find action for: " + rawAttributeData + " with actor: " + actor);
        } else {
            actor.updateData((YokelGameBoard) action.consume(actor));
        }
    }
}
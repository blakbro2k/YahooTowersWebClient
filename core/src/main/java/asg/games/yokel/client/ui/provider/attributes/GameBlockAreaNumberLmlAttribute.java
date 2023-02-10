package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBoard;

public class GameBlockAreaNumberLmlAttribute implements LmlAttribute<GameBoard> {
    public GameBlockAreaNumberLmlAttribute() {}

    public Class<GameBoard> getHandledType() {
        return GameBoard.class;
    }

    public void process(LmlParser parser, LmlTag tag, GameBoard actor, String rawAttributeData) {
        actor.setBoardNumber(parser.parseInt(rawAttributeData));
    }
}
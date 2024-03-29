package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GamePlayerBoard;

public class GamePieceOrientationLmlAttribute implements LmlAttribute<GamePlayerBoard> {
    public GamePieceOrientationLmlAttribute() {
    }

    public Class<GamePlayerBoard> getHandledType() {
        return GamePlayerBoard.class;
    }

    public void process(LmlParser parser, LmlTag tag, GamePlayerBoard actor, String rawAttributeData) {
        actor.setLeftBarOrientation(parser.parseBoolean(rawAttributeData));
    }
}
package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameNextPiece;

public class GamePieceDataLmlAttribute implements LmlAttribute<GameNextPiece> {
    public GamePieceDataLmlAttribute() {
    }

    public Class<GameNextPiece> getHandledType() {
        return GameNextPiece.class;
    }

    public void process(final LmlParser parser, final LmlTag tag, GameNextPiece actor, final String rawAttributeData) {
        actor.updateYokelData(rawAttributeData);
    }
}
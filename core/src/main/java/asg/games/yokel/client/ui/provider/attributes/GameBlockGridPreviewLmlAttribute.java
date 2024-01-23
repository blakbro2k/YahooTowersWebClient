package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameBlockGrid;

public class GameBlockGridPreviewLmlAttribute implements LmlAttribute<GameBlockGrid> {
    public GameBlockGridPreviewLmlAttribute() {
    }

    public Class<GameBlockGrid> getHandledType() {
        return GameBlockGrid.class;
    }

    public void process(LmlParser parser, LmlTag tag, GameBlockGrid actor, String rawAttributeData) {
        actor.setPreview(parser.parseBoolean(rawAttributeData));
    }
}
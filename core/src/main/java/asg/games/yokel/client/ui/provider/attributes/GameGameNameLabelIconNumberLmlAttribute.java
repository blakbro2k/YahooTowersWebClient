package asg.games.yokel.client.ui.provider.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameNameLabel;

public class GameGameNameLabelIconNumberLmlAttribute implements LmlAttribute<GameNameLabel> {
    public GameGameNameLabelIconNumberLmlAttribute() {
    }

    public Class<GameNameLabel> getHandledType() {
        return GameNameLabel.class;
    }

    public void process(LmlParser parser, LmlTag tag, GameNameLabel actor, String rawAttributeData) {
        actor.setIcon(parser.parseInt(rawAttributeData));
    }
}
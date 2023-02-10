package asg.games.yokel.client.ui.provider.tags;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

public class GamePieceLmlTagProvider implements LmlTagProvider {
    @Override
    public LmlTag create(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        return new GamePieceLmlTag(parser, parentTag, rawTagData);
    }
}
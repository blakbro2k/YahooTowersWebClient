package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.ImageButtonLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GameIcon;

public class GamePlayerIconLmlTag extends ImageButtonLmlTag {
    public GamePlayerIconLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final Skin skin = getSkin(builder);
        return new GameIcon(GameIcon.getGameDefaultIconStyle(skin), skin);
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final GameIcon icon = getIcon();
        if(icon != null) {
            icon.setIconNumber(Integer.parseInt(plainTextLine));
        }

    }

    /** @return casted actor reference. */
    protected GameIcon getIcon() {
        return (GameIcon) getActor();
    }
}

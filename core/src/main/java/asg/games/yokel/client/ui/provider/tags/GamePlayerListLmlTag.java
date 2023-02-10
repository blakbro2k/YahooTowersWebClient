package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.TableLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

import asg.games.yokel.client.ui.actors.GamePlayerList;


public class GamePlayerListLmlTag extends TableLmlTag {
    GamePlayerListLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return createGamePlayerListTag(builder);
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
    }

    private GamePlayerList createGamePlayerListTag(final LmlActorBuilder builder){
        return new GamePlayerList(getSkin(builder));
    }
}
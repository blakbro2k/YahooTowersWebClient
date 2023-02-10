package asg.games.yokel.client.ui.provider.tags;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.actor.LabelLmlTag;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

import asg.games.yokel.client.ui.actors.GameLabel;

public class GameLabelLmlTag extends LabelLmlTag {
    public GameLabelLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected TextLmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        final TextLmlActorBuilder textBuilder = (TextLmlActorBuilder) builder;
        return new GameLabel(textBuilder.getText(), getSkin(builder));
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        final GameLabel label = getLabel();
        final String textToAppend = getParser().parseString(plainTextLine, label);
        if (Strings.isEmpty(label.getText())) {
            // Label is currently empty, so we just set the text as initial value.
            label.setText(textToAppend);
        } else {
            if (LmlUtilities.isMultiline(label)) {
                // Label is multiline. We might want to append an extra new line char.
                label.getText().append('\n');
            }
            label.getText().append(textToAppend);
        }
        label.invalidate();
    }

    /** @return casted actor reference. */
    protected GameLabel getLabel() {
        return (GameLabel) getActor();
    }
 }
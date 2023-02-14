package asg.games.yokel.client.controller.dialog;

import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.stereotype.ViewDialog;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

import asg.games.yokel.client.GlobalConstants;
import asg.games.yokel.client.service.SessionService;

/** This is a settings dialog, which can be shown in any views by using "show:settings" LML action or - in Java code -
 * through InterfaceService.showDialog(Class) method. Thanks to the fact that it implements ActionContainer, its methods
 * will be available in the LML template. */
@ViewDialog(id = GlobalConstants.ERROR_DIALOG, value = GlobalConstants.ERROR_DIALOG_PATH)
public class ErrorController implements ActionContainer {
    @Inject private SessionService sessionService;

    /** @return array of available GUI scales. */
    @LmlAction("getCurrentErrorMessage")
    public String getCurrentErrorMessage() {
        return sessionService.getCurrentError();
    }
}
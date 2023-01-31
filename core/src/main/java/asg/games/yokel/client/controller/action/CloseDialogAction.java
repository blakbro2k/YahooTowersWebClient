package asg.games.yokel.client.controller.action;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;

public class CloseDialogAction extends Action {
    private InterfaceService interfaceService;
    Class<?> dialogController;

    @Override
    public boolean act(float delta) {
        if(interfaceService != null && dialogController != null) {
            interfaceService.destroyDialog(dialogController);
        }
        return true;
    }

    public void setController(Class<?> dialogController, InterfaceService interfaceService){
        this.dialogController = dialogController;
        this.interfaceService = interfaceService;
    }
}

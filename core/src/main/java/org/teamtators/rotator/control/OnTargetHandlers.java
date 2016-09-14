package org.teamtators.rotator.control;

public class OnTargetHandlers {
    public static OnTargetHandler disableController() {
        return AbstractController::disable;
    }
}

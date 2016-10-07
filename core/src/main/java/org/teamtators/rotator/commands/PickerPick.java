package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.PickerPosition;

/**
 * Picks up a ball
 */
public class PickerPick extends CommandBase implements Configurable<PickerPick.Config> {
    private Config config;
    private AbstractPicker picker;
    private AbstractTurret turret;
    private AbstractController controller;
    private ControllerFactory controllerFactory;

    public PickerPick(CoreRobot robot) {
        super("PickerPick");
        this.picker = robot.picker();
        this.turret = robot.turret();
        this.controllerFactory = robot.controllerFactory();
        requires(picker);
        requires(turret);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        controller = controllerFactory.create(config.distanceController);
        controller.setName(getName());
        controller.setInputProvider(turret::getBallDistance);
        controller.setOutputConsumer(output -> turret.setKingRollerPower(-output));
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (!turret.isHomed()) {
            logger.warn("Turret not at home, stopping pick");
            cancel();
            return;
        }
        //Extends the picker
        picker.setPosition(PickerPosition.PICK);
        controller.enable();
        controller.setSetpoint(config.targetBallDistance);
    }

    @Override
    protected boolean step() {
        double sign = Math.signum(turret.getBallDistance() - controller.getSetpoint());
        turret.setTargetAngle(0);
        //Starts the rollers
        if (sign == -1) {
            picker.setPower(config.pick * sign);
            turret.setPinchRollerPower(config.pinch * sign);
        }
        return controller.isOnTarget();

    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.resetPower();
        turret.resetPinchRollerPower();
        turret.resetKingRollerPower();
    }

    public static class Config {
        public double pick, pinch;
        public double targetBallDistance;
        public JsonNode distanceController;
    }
}

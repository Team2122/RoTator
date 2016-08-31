package org.teamtators.rotator.tester.components;

import edu.wpi.first.wpilibj.Solenoid;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class SolenoidTest extends ComponentTest {
    private Solenoid solenoid;

    public SolenoidTest(String name, Solenoid solenoid) {
        super(name);
        this.solenoid = solenoid;
    }

    @Override
    public void start() {
        logger.info("Press A to activate solenoid, B to deactivate");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        if (button == LogitechF310.Button.A) {
            solenoid.set(true);
            logger.info("Solenoid activated");
        } else if (button == LogitechF310.Button.B) {
            solenoid.set(false);
            logger.info("Solenoid deactivated");
        }
    }
}

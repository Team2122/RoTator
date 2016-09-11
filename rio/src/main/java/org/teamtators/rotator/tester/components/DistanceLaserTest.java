package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.components.DistanceLaser;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class DistanceLaserTest extends ComponentTest {
    private DistanceLaser distanceLaser;

    public DistanceLaserTest(String name, DistanceLaser distanceLaser) {
        super(name);
        this.distanceLaser = distanceLaser;
    }

    @Override
    public void start() {
        logger.info("Press 'A' to get the Distance and Voltage of the DistanceLaser");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        if (button == LogitechF310.Button.A) {
            logger.info("Distance Laser ballDistance: {}; voltage: {}", distanceLaser.getDistance(), distanceLaser.getVoltage());
        }
    }
}

package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WPILibVision extends AbstractVision implements Configurable<WPILibVision.Config>, ITestable {
    private VictorSP ledPower;
    private NetworkTable table;

    @Inject
    public WPILibVision() {
    }

    @Override
    public void configure(WPILibVision.Config config) {
        this.ledPower = config.ledPower.create();
        this.table = NetworkTable.getTable(config.tableName);
    }

    @Override
    public void setLedState(boolean on) {
        super.setLedState(on);
        ledPower.set(on ? 1.0 : 0.0);
    }

    @Override
    public VisionData getVisionData() {
        double frameNum = table.getNumber("frameNumber", Double.NaN);
        int frameNumber;
        if (Double.isNaN(frameNum))
            frameNumber = 0;
        else
            frameNumber = (int) frameNum;
        double distance = table.getNumber("distance", Double.NaN);
        double offsetAngle = table.getNumber("offsetAngle", Double.NaN);
        double newAngle = table.getNumber("newAngle", Double.NaN);
        return new VisionData(frameNumber, distance, offsetAngle, newAngle);
    }

    @Override
    public void setTurretAngle(double turretAngle) {
        table.putNumber("turretAngle", turretAngle);
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Vision",
                new VisionTest());
    }

    public static class Config {
        public VictorSPConfig ledPower;
        public String tableName;
    }

    private class VisionTest extends ComponentTest {
        public VisionTest() {
            super("visionTest");
        }

        @Override
        public void start() {
            logger.info("Press A to turn leds on, B to turn leds off, X to get current vision information");
        }

        @Override
        public void onButtonDown(LogitechF310.Button button) {
            switch (button) {
                case A:
                    logger.info("LEDS on");
                    setLedState(true);
                    break;
                case B:
                    logger.info("LEDS off");
                    setLedState(false);
                    break;
                case X:
                    VisionData visionData = getVisionData();
                    logger.info(visionData.toString());
                    break;
            }
        }
    }
}

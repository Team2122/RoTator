package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.VictorSPTest;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WPILibVision extends AbstractVision implements Configurable<WPILibVision.Config>, ITestable {

    public static class Config {
        public VictorSPConfig ledPower;
        public String tableName;
    }

    private VictorSP ledPower;
    private NetworkTable table;

    @Override
    public void configure(WPILibVision.Config config) {
        this.ledPower = config.ledPower.create();
        this.table = NetworkTable.getTable(config.tableName);
    }

    @Override
    public void setLEDPower(double power) {
        if (power < 0.0f) {
            logger.warn("Power setting invalid: {} Resetting power to 0.0", power);
            power = 0.0f;
        }

        ledPower.set(power);
    }

    @Override
    public double getDistance() {
        return table.getNumber("distance", Double.NaN);
    }

    @Override
    public double getAngle() {
        return table.getNumber("angle", Double.NaN);
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Vision",
                new VictorSPTest("ledPower", ledPower),
                new VisionTest());
    }

    private class VisionTest extends ComponentTest {
        public VisionTest() {
            super("visionTest");
        }

        @Override
        public void start() {
            logger.info("Press A to get current vision information");
        }

        @Override
        public void onButtonDown(LogitechF310.Button button) {
            switch (button) {
                case A:
                    double angle = getAngle();
                    double distance = getDistance();
                    logger.info("Angle = {} degrees, Distance = {} inches", angle, distance);
                    break;
            }
        }
    }
}

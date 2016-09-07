package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.VictorSPConfig;
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
    private double lastDistance;
    private double lastAngle;

    public WPILibVision() {

    }

    @Override
    public void configure(WPILibVision.Config config) {
        this.ledPower = config.ledPower.create();
        this.table = NetworkTable.getTable(config.tableName);
    }

    @Override
    public void setLEDPower(double power) {
        if (power < 0.0f) power *= -1.0f;    //prevents reverse polarity without raising an exception
        ledPower.set(power);
    }

    @Override
    public double getDistance() {
        double distance = table.getNumber("distance", lastDistance);
        lastDistance = distance;
        return distance;
    }

    @Override
    public double getAngle() {
        double angle = table.getNumber("angle", lastAngle);
        lastAngle = angle;
        return angle;
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        List<ComponentTest> l = new ArrayList<>();
        l.add(new VictorSPTest("ledPower", ledPower));
        return new ComponentTestGroup("Vision", l);
    }
}

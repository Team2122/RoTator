package org.teamtators.rotator.subsystems;


import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.SolenoidTest;
import org.teamtators.rotator.tester.components.VictorSPTest;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class WPILibPicker extends AbstractPicker implements Configurable<WPILibPicker.Config>, ITestable {

    private VictorSP pickMotor;
    private Solenoid shortCylinder;
    private Solenoid longCylinder;
    private PickerPosition pickerPosition;

    @Override
    public void configure(Config config) {
        this.pickMotor = config.pickMotor.create();
        this.shortCylinder = new Solenoid(config.shortCylinder);
        this.longCylinder = new Solenoid(config.longCylinder);
    }

    @Override
    public void resetPower() {
        super.resetPower();
    }

    @Override
    public void setPower(double power) {
        pickMotor.set(power);
    }

    @Override
    public void setPosition(PickerPosition position) {
        super.setPosition(position);
        checkNotNull(position, "Picker position can not be null");
        switch (position) {
            case HOME:
                shortCylinder.set(false);
                longCylinder.set(false);
                break;
            case CHEVAL:
                shortCylinder.set(false);
                longCylinder.set(true);
                break;
            case PICK:
                shortCylinder.set(true);
                longCylinder.set(true);
                break;
        }
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Picker",
                new VictorSPTest("pickMotor", pickMotor),
                new SolenoidTest("shortCylinder", shortCylinder),
                new SolenoidTest("longCylinder", longCylinder));
    }

    public static class Config {
        public VictorSPConfig pickMotor;
        public int shortCylinder;
        public int longCylinder;
    }
}

package org.teamtators.rotator.subsystems;


import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.components.AbstractPicker;
import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.components.PickerPosition;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.DigitalSensorConfig;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.DigitalSensorTest;
import org.teamtators.rotator.tester.components.SolenoidTest;
import org.teamtators.rotator.tester.components.VictorSPTest;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class WPILibPicker extends AbstractPicker implements Configurable<WPILibPicker.Config>, ITestable {

    private VictorSP pickMotor;
    private VictorSP pinchRollerMotor;
    private Solenoid shortCylinder;
    private Solenoid longCylinder;
    private PickerPosition pickerPosition;
    private DigitalSensor chevalSensor;

    @Inject
    public WPILibPicker() {
    }

    @Override
    public void configure(Config config) {
        this.pickMotor = config.pickMotor.create();
        this.pinchRollerMotor = config.pinchRollerMotor.create();
        this.shortCylinder = new Solenoid(config.shortCylinder);
        this.longCylinder = new Solenoid(config.longCylinder);
        this.chevalSensor = config.chevalSensor.create();
    }

    @Override
    public void setPickPower(double power) {
        pickMotor.set(power);
    }

    @Override
    public void setPinchPower(double power) {
        pinchRollerMotor.set(power);
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
    public boolean isAtCheval() {
        return chevalSensor.get();
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Picker",
                new VictorSPTest("pickMotor", pickMotor),
                new VictorSPTest("pinchRollerMotor", pinchRollerMotor),
                new SolenoidTest("shortCylinder", shortCylinder),
                new SolenoidTest("longCylinder", longCylinder),
                new DigitalSensorTest("chevalSensor", chevalSensor));
    }

    public static class Config {
        public VictorSPConfig pickMotor;
        public VictorSPConfig pinchRollerMotor;
        public int shortCylinder;
        public int longCylinder;
        public DigitalSensorConfig chevalSensor;
    }
}

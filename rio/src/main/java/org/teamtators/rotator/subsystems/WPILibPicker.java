package org.teamtators.rotator.subsystems;


import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.tester.ComponentTest;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.SolenoidTest;
import org.teamtators.rotator.tester.components.VictorSPTest;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class WPILibPicker extends AbstractPicker implements Configurable<WPILibPicker.Config>, ITestable {

    public static class Config {
        public VictorSPConfig pickMotor;
        public int shortCylinder;
        public int longCylinder;
    }

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
    public void setPower(float power) {
        pickMotor.set(power);
    }

    @Override
    public void resetPower() {
        super.resetPower();
    }

    @Override
    public double getPower() {
        return pickMotor.get();
    }

    @Override
    public void setPosition(PickerPosition position) {
        switch (position) {
            case CHEVAL:
                pickerPosition = PickerPosition.CHEVAL;
                break;
            case HOME:
                pickerPosition = PickerPosition.HOME;
                break;
            case PICK:
                pickerPosition = PickerPosition.PICK;
                break;
        }
    }

    @Override
    public PickerPosition getPosition() {
        return pickerPosition;
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        List<ComponentTest> l = new ArrayList<>();
        l.add(new VictorSPTest("pickMotor", pickMotor));
        l.add(new SolenoidTest("shortCylinder", shortCylinder));
        l.add(new SolenoidTest("longCylinder", longCylinder));
        return new ComponentTestGroup("Picker", l);
    }
}

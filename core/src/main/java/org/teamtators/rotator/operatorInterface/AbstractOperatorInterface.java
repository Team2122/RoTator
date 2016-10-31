package org.teamtators.rotator.operatorInterface;

import org.teamtators.rotator.components.Component;

public abstract class AbstractOperatorInterface extends Component {
    public AbstractOperatorInterface() {
        super("OperatorInterface");
    }

    public abstract LogitechF310 driverJoystick();

    public abstract LogitechF310 gunnerJoystick();
}

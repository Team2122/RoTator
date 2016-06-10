import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
    /**
     * Robot-wide initialization code should go here.
     * <p>
     * Users should override this method for default Robot-wide initialization
     * which will be called when the robot is first powered on. It will be called
     * exactly one time.
     * <p>
     * Warning: the Driver Station "Robot Code" light and FMS "Robot Ready"
     * indicators will be off until RobotInit() exits. Code in RobotInit() that
     * waits for enable will cause the robot to never indicate that the code is
     * ready, causing the robot to be bypassed in a match.
     */
    @Override
    public void robotInit() {
        super.robotInit();
        System.out.println("Hi, I'm a rotating potato");
    }
}
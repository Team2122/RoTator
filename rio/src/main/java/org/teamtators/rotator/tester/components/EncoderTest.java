package org.teamtators.rotator.tester.components;

import edu.wpi.first.wpilibj.Encoder;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class EncoderTest extends ComponentTest {

    private Encoder encoder;

    public EncoderTest(String name, Encoder encoder) {
        super(name);
        this.encoder = encoder;
    }

    @Override
    public void start() {
        logger.info(">>>>Press 'A' to display the current values");
        logger.info(">>>>Press 'B' to resetAngle the encoder values");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        if (button == LogitechF310.Button.B) {
            encoder.reset();
            logger.info(">>>>Encoder resetAngle");
        } else if (button == LogitechF310.Button.A) {
            logger.info(">>>>Distance: {} (ticks: {}), Rate: {}", encoder.getDistance(), encoder.get(), encoder.getRate());
        }
    }
}

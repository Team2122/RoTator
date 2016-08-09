package org.teamtators.rotator.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.subsystems.SimulationDrive;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alex on 8/7/16.
 */
public class SimulationFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(SimulationFrame.class);
    private Config config = new Config();
    private Image fieldImage;
    private SimulationDrive drive;
    private WASDJoystick driverJoystick;
    private AffineTransform fieldImageTransform;

    public SimulationFrame(SimulationDrive drive, WASDJoystick driverJoystick) throws HeadlessException {
        super("RoTator Simulation");
        this.drive = drive;
        this.driverJoystick = driverJoystick;

        this.addKeyListener(driverJoystick);
        this.setSize((int) (config.fieldLength * config.inchesPerPixel),
                (int) (config.fieldWidth * config.inchesPerPixel));
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        drive.setMaxX(config.fieldLength);
        drive.setMaxY(config.fieldWidth);
        drive.setX(config.fieldLength / 2);
        drive.setY(config.fieldWidth / 2);

        try {
            InputStream imageStream = SimulationFrame.class.getClassLoader().getResourceAsStream("2016_Field_Labeled.png");
            fieldImage = ImageIO.read(imageStream);
        } catch (IOException e) {
            logger.error("Error loading field image", e);
        }

        updateFieldTransform();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        updateFieldTransform();
        g2d.drawImage(fieldImage, fieldImageTransform, this);
        g2d.dispose();

        double width = drive.getLength() * config.inchesPerPixel;
        double height = drive.getWidth() * config.inchesPerPixel;
        double x = drive.getX() * config.inchesPerPixel;
        double y = drive.getY() * config.inchesPerPixel;
        double rot = drive.getRotation();

        g2d = (Graphics2D) g.create();
        Rectangle rect = new Rectangle((int) -width / 2, (int) -height / 2, (int) width, (int) height);
        g2d.translate(x, y);
        g2d.rotate(rot);
        g2d.setColor(new Color(183, 183, 183));
        g2d.fill(rect);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(rect);
        g2d.dispose();
    }

    private void updateFieldTransform() {
        double fieldImageWidth = fieldImage.getWidth(null);
        double fieldImageHeight = fieldImage.getHeight(null);
        fieldImageTransform = new AffineTransform();
        fieldImageTransform.translate(getWidth() / 2, getHeight() / 2);
        double s = getHeight() / fieldImageHeight;
        fieldImageTransform.scale(s, s);
        fieldImageTransform.translate(-fieldImageWidth / 2, -fieldImageHeight / 2);
    }

    public static class Config {
        public double inchesPerPixel = 2;
        public int fieldWidth = 27 * 12;
        public int fieldLength = 54 * 12;
    }
}

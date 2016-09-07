package org.teamtators.rotator.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.subsystems.SimulationDrive;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

public class SimulationDisplay extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(SimulationDisplay.class);
    public static final Color ROBOT_COLOR = new Color(183, 183, 183);
    private Config config = new Config();
    private Image fieldImage;

    private SimulationDrive drive;

    public SimulationDisplay() throws HeadlessException {
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SimulationDisplay.this.requestFocusInWindow();
            }
        });
        try {
            InputStream imageStream = SimulationDisplay.class.getClassLoader().getResourceAsStream("2016_Field_Labeled.png");
            fieldImage = ImageIO.read(imageStream);
        } catch (IOException e) {
            logger.error("Error loading field image", e);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (config.fieldLength * config.inchesPerPixel),
                (int) (config.fieldWidth * config.inchesPerPixel));
    }

    @Inject
    public void setDrive(SimulationDrive drive) {
        this.drive = drive;
        this.drive.setMaxX(config.fieldLength);
        this.drive.setMaxY(config.fieldWidth);
        this.drive.setX(config.fieldLength / 2);
        this.drive.setY(config.fieldWidth / 2);
    }

    @Inject
    public void setDriverJoystick(WASDJoystick driverJoystick) {
        this.addKeyListener(driverJoystick);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        double fieldImageWidth = fieldImage.getWidth(null);
        double fieldImageHeight = fieldImage.getHeight(null);
        AffineTransform fieldImageTransform = new AffineTransform();
        fieldImageTransform.translate(getWidth() / 2, getHeight() / 2);
        double sx = getWidth() / fieldImageWidth;
        double sy = getHeight() / fieldImageHeight;
        fieldImageTransform.scale(sx, sy);
        fieldImageTransform.translate(-fieldImageWidth / 2, -fieldImageHeight / 2);

        g2d.drawImage(fieldImage, fieldImageTransform, this);
        g2d.dispose();

        double width = drive.getLength() * config.inchesPerPixel;
        double height = drive.getWidth() * config.inchesPerPixel;
        double x = drive.getX() * config.inchesPerPixel;
        double y = drive.getY() * config.inchesPerPixel;
        double rot = drive.getRotation();

        g2d = (Graphics2D) g.create();

        AffineTransform robotTransform = new AffineTransform();
        Dimension preferredSize = getPreferredSize();
        sx = getWidth() / (double) preferredSize.width;
        sy = getHeight() / (double) preferredSize.height;
        robotTransform.scale(sx, sy);
        robotTransform.translate(x, y);
        robotTransform.rotate(rot);
        g2d.setTransform(robotTransform);

        Rectangle rect = new Rectangle((int) -width / 2, (int) -height / 2, (int) width, (int) height);
        g2d.setColor(ROBOT_COLOR);
        g2d.fill(rect);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(rect);

        g2d.dispose();
    }

    public static class Config {
        public double inchesPerPixel = 2;
        public int fieldWidth = 27 * 12;
        public int fieldLength = 54 * 12;
    }
}

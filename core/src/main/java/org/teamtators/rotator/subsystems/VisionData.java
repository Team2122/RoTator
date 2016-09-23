package org.teamtators.rotator.subsystems;

public class VisionData {
    private int frameNumber;
    private double distance;
    private double angle;

    public VisionData(int frameNumber, double distance, double angle) {
        this.frameNumber = frameNumber;
        this.distance = distance;
        this.angle = angle;
    }

    /**
     * @return A unique sequential frame number for the current vision data
     */
    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * @return The current distance in inches of the camera to the target
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return The current offset in angle from the target
     */
    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return "VisionData{" +
                "frameNumber=" + frameNumber +
                ", distance=" + distance +
                ", angle=" + angle +
                '}';
    }
}

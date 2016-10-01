package org.teamtators.rotator.subsystems;

public class VisionData {
    private int frameNumber;
    private double distance;
    private double offsetAngle;
    private double newAngle;

    public VisionData(int frameNumber, double distance, double angle, double newAngle) {
        this.frameNumber = frameNumber;
        this.distance = distance;
        this.offsetAngle = angle;
        this.newAngle = newAngle;
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
    public double getOffsetAngle() {
        return offsetAngle;
    }

    public double getNewAngle() {
        return newAngle;
    }

    @Override
    public String toString() {
        return "VisionData{" +
                "frameNumber=" + frameNumber +
                ", distance=" + distance +
                ", offsetAngle=" + offsetAngle +
                ", newAngle=" + newAngle +
                '}';
    }
}

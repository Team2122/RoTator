package org.teamtators.rotator;

import com.google.common.collect.EvictingQueue;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * A sensor class for using an ADXRS453 gyroscope
 */
public class ADXRS453 implements Closeable {
    protected static int fixParity(int data) {
        data &= ~kP;
        return data | (calcParity(data) ? 0 : kP);
    }

    /**
     * Find the parity of a binary string
     *
     * @param data Data to find parity of
     * @return Whether or not the number of ones is odd
     */
    protected static boolean calcParity(int data) {
        int ones = 0;
        for (int i = 0; i < 32; i++) {
            if ((data & (1 << i)) != 0) {
                ones++;
            }
        }
        return (ones % 2) == 1;
    }

    protected boolean checkParity(int data) {
        boolean p1 = calcParity(data);
        boolean p0 = calcParity(data >> 16);
        if (!p1 || !p0) {
            logger.error("Parity check failed on response: %#x", data);
            return false;
        }
        return true;
    }

    protected boolean checkFaults(int data) {
        boolean hasFaults = (data & kFaultBits) != 0;
        if (hasFaults) {
            logger.warn("Faults detected:");
        } else {
            return true;
        }
        if ((data & kChk) != 0) {
            logger.warn(" * Self test enabled");
            return false;
        }
        if ((data & kCst) != 0)
            logger.error(" * Continuous self test fault");
        if ((data & kPwr) != 0)
            logger.error(" * Power fault");
        if ((data & kPor) != 0)
            logger.error(" * Non-volatile programming fault");
        if ((data & kNvm) != 0)
            logger.error(" * Non-volatile checksum fault");
        if ((data & kQ) != 0)
            logger.error(" * Quadrature calculation fault");
        if ((data & kPll) != 0)
            logger.error(" * Phase locked loop fault");
        return false;
    }

    protected boolean checkResponse(int data) {
        if (!checkParity(data))
            return false;
        int status = (data >> 26) & 0b11;
        switch (status) {
            case kInvalidData:
                logger.warn("Invalid data received");
                checkFaults(data);
                return false;
            case kValidData:
            case kTestData:
                return checkFaults(data);
            case kReadWrite:
                if ((data & (kReadBit | kWriteBit)) != 0)
                    return true;
                logger.error("Read/Write error: ");
                if ((data & kSpi) != 0)
                    logger.error(" * SPI error");
                if ((data & kRe) != 0)
                    logger.error(" * Request error");
                if ((data & kDu) != 0)
                    logger.error(" * Data unavailable");
                checkFaults(data);
                return false;
        }
        return false;
    }

    protected boolean checkPartID() {
        int pid = GetPartID();
        if ((pid & 0xff00) == 0x5200) {
            double temperature = getTemperature();
            int serial = GetSerialNumber();
            logger.info(
                    "Part ID of gyro is correct (%#04x). Temperature: %f C. Serial: (%#08x)",
                    pid, temperature, serial);
            return true;
        } else {
            logger.error("Bad gyro found. Part id: %#04x", pid);
            return false;
        }
    }

    protected void write(int data) {
        lastDataSent = data;
        data = Integer.reverse(data); // fix endianness
        ByteBuffer send = ByteBuffer.allocateDirect(4);
        send.putInt(data);
        spi.write(send, send.capacity()); // send it
    }

    protected int read() {
        ByteBuffer recv = ByteBuffer.allocateDirect(4);
        spi.read(false, recv, recv.capacity()); // read into the buffer
        int ret = recv.getInt();
        ret = Integer.reverse(ret); // fix endianness
        return ret;
    }

    protected int transfer(int data) {
        lastDataSent = data;
        data = Integer.reverse(data);
        ByteBuffer send = ByteBuffer.allocateDirect(4);
        ByteBuffer recv = ByteBuffer.allocateDirect(4);
        send.putInt(data);
        spi.transaction(send, recv, send.capacity());
        int ret = recv.getInt();
        ret = Integer.reverse(ret); // fix endianness
        return ret;
    }

    protected void startup() {
        int send, recv;
        send = fixParity(kSensorData | kChk);
        write(send);
        try {
            Thread.sleep(50); // in the spec
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        send = fixParity(kSensorData);
        write(send);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recv = transfer(send);
        if ((recv & kFaultBits) != kFaultBits) { // assert that all faults are set
            logger.error("Startup self test failed: %#x", recv);
            return;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        write(send);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!checkPartID())
            return;
        timer.start();
        updater.startPeriodic(updatePeriod);
    }

    protected void update() {
        lock.writeLock();
        int send, recv;
        send = fixParity(kSensorData);
        if (lastDataSent != send) // optimization, because of data latching
            write(send);
        recv = transfer(send);
        if (!checkResponse(recv))
            return;
        int rawRate = (recv >> 10) & 0xFFFF; // bits 10-25
        rate = rawRate / kDegreesPerSecondPerLSB;
        double ellapsed = timer.get();
        timer.reset();
        if (ellapsed > .5) // don't intergrate if more than half a second
            ellapsed = 0;
        if (isCalibrating) {
            calibrationValues.add(rate);
        }
        rate -= calibrationRate; // apply calibration value
        angle += rate * ellapsed; // intergrate it into the angle
    }

    protected Logger logger;

    protected Timer timer; ///< the timer used for measuring the delay between runs
    protected SPI spi; ///< the spi class used for communication with the gyro

    protected ReadWriteLock lock; ///< lock for threaded data
    protected Thread startup; ///< the task that performs the startup routine for the gyro
    protected Notifier updater; ///< the notifier that runs the update routine

    protected int lastDataSent; ///< the last data that was sent to the gyro
    protected double rate; ///< the latest rate read from the gyro, zerod
    protected float angle; ///< the calculated angle
    protected boolean isCalibrating; ///< true if we are calibrating right now
    protected float calibrationRate; ///< the zero value for the rate
    protected EvictingQueue<Double> calibrationValues;

    protected PIDSourceType pidSource;

    private static final int kSensorData = 1 << 29;
    private static final int kRead = 1 << 31;
    private static final int kWrite = 1 << 30;

    private static final int kP = 1 << 0;
    private static final int kChk = 1 << 1;
    private static final int kCst = 1 << 2;
    private static final int kPwr = 1 << 3;
    private static final int kPor = 1 << 4;
    private static final int kNvm = 1 << 5;
    private static final int kQ = 1 << 6;
    private static final int kPll = 1 << 7;
    private static final int kFaultBits = kChk | kCst | kPwr | kPor | kNvm | kQ | kPll;
    private static final int kDu = 1 << 16;
    private static final int kRe = 1 << 17;
    private static final int kSpi = 1 << 18;
    private static final int kP0 = 1 << 28;
    private static final int kWriteBit = 1 << 29;
    private static final int kReadBit = 1 << 30;

    private static final int kInvalidData = 0x0;
    private static final int kValidData = 0x1;
    private static final int kTestData = 0x2;
    private static final int kReadWrite = 0x03;

    public static final int REG_RATE = 0x00;
    public static final int REG_TEM = 0x02;
    public static final int REG_LO_CST = 0x04;
    public static final int REG_HI_CST = 0x06;
    public static final int REG_QUAD = 0x08;
    public static final int REG_FAULT = 0x0A;
    public static final int REG_PID = 0x0C;
    public static final int REG_SN_H = 0x0E;
    public static final int REG_SN_L = 0x10;

    private static int kSPIClockRate = 3000000;

    private static double kDegreesPerSecondPerLSB = 80.0;

    public static int getUpdatesPerSecond() {
        return updatesPerSecond;
    }

    public static void setUpdatesPerSecond(int updatesPerSecond) {
        ADXRS453.updatesPerSecond = updatesPerSecond;
    }

    private static int updatesPerSecond = 120;

    public static double getUpdatePeriod() {
        return updatePeriod;
    }

    private static double updatePeriod = 1.0 / updatesPerSecond;

    public static int getCalibrationTicks() {
        return calibrationTicks;
    }

    public static void setCalibrationTicks(int calibrationTicks) {
        ADXRS453.calibrationTicks = calibrationTicks;
    }

    private static int calibrationTicks = 5 * updatesPerSecond;

    /**
     * Creates a new ADXRS453
     *
     * @param port The SPI port to attach to
     */
    ADXRS453(SPI.Port port) {
        this(new SPI(port));
    }

    /**
     * Creates a new ADXRS453
     *
     * @param spi The spi to use
     */
    ADXRS453(SPI spi) {
        logger = LogManager.getLogger(this.getClass());
        this.spi = spi;
        calibrationValues = EvictingQueue.create(calibrationTicks);
        updater = new Notifier(this::update);
        pidSource = PIDSourceType.kDisplacement;
        fullReset();
        spi.setClockRate(kSPIClockRate);
        spi.setMSBFirst();
        spi.setSampleDataOnRising();
        spi.setClockActiveHigh();
        spi.setChipSelectActiveLow();
    }

    /**
     * Starts threads for processing gyro data
     */
    public void start() {
        logger.info("Starting gyro threads");
        startup = new Thread(this::startup);
    }

    /**
     * Stops the data processing threads
     */
    public void stop() {
        logger.info("Stopping gyro threads");
        updater.stop();
        timer.stop();
    }

    /**
     * Resets everything
     */
    public void fullReset() {
        lock.writeLock();
        rate = 0;
        angle = 0;
        calibrationRate = 0;
        isCalibrating = false;
        calibrationValues.clear();
        timer.reset();
    }

    /**
     * Starts calibrating the gyro. Resets the calibration value and begins
     * sampling gyro values to get the average 0 value. Sample time determined
     * by calibrationTicks
     */
    public void startCalibration() {
        lock.writeLock();
        logger.info("Starting gyro calibration");
        calibrationRate = 0;
        calibrationValues.clear();
        isCalibrating = true;
    }

    /**
     * Finishes calibration. Stops calibrating and sets the calibration value.
     */
    public void calibrate() {
        lock.writeLock();
        float calibrationValuesSum = 0;
        for (Double calibrationValue : calibrationValues) {
            calibrationValuesSum += calibrationValue;
        }
        calibrationRate = calibrationValuesSum / calibrationValues.size();
        angle = 0;
        isCalibrating = false;
        logger.info("Finished calibrating gyro. Offset is %f", calibrationRate);
    }

    /**
     * Gets the current calibration rate
     *
     * @return The current calibration rate
     */
    public float getCalibrationRate() {
        return calibrationRate;
    }

    /**
     * Checks if the gyro is currently calibrating
     *
     * @return
     */
    public boolean isCalibrating() {
        return isCalibrating;
    }

    /**
     * Gets the rate from the gyro
     *
     * @return The rate in degrees per second, positive is clockwise
     */
    public double getRate() {
        return rate;
    }

    /**
     * Gets the angle of the gyro
     *
     * @return The angle of the gyro in degrees
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Sets the angle of the gyro
     *
     * @param angle The angle in degrees
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Resets the angle of the gyro to zero
     */
    public void reset() {
        lock.writeLock();
        angle = 0.0f;
    }

    /**
     * Reads a register on the gyro
     *
     * @param reg The register to read
     * @return The value in the register
     */
    public int readRegister(int reg) {
        int send, recv;
        send = kRead | (reg << 17);
        send = fixParity(send);
        write(send);
        recv = read();
        if (!checkResponse(recv)) {
            return 0x00;
        }
        return (recv >> 5) & 0xFFFF;
    }

    /**
     * Writes to a register on the gyro
     *
     * @param reg   The register to write
     * @param value The value to write to it
     */
    public void writeRegister(int reg, int value) {
        int send;
        send = kRead | (reg << 17) | (value << 1);
        send = fixParity(send);
        write(send);
        checkResponse(read());
    }

    /**
     * Gets the temperature from the internal register on the gyro
     *
     * @return The temperature in celcius
     */
    public double getTemperature() {
        int rawTemp = readRegister(REG_TEM);
        int tempLSB = rawTemp >> 6;
        if ((tempLSB >> 9) == 1) {
            tempLSB = -((~(tempLSB - 1)) & 0x03FF);
        }
        return (tempLSB / 5.0) + 45.0;
    }

    /**
     * Gets the part ID of the currently connected gyro
     *
     * @return The part ID
     */
    public int GetPartID() {
        return readRegister(REG_PID);
    }

    /**
     * Gets the serial number of the currently connected gyro
     *
     * @return The 32 bit serial number
     */
    public int GetSerialNumber() {
        int serial = 0;
        serial |= readRegister(REG_SN_H) << 16;
        serial |= readRegister(REG_SN_L);
        return serial;
    }

    public void SetPIDSourceType(PIDSourceType pidSource) {
        this.pidSource = pidSource;
    }

    public double PIDGet() {
        switch (pidSource) {
            case kDisplacement:
                return getAngle();
            case kRate:
                return getRate();
            default:
                return 0;
        }
    }

    public void close() {
        spi.free();
        updater.stop();
    }
}

package org.teamtators.rotator.components;

import com.google.common.collect.EvictingQueue;
import edu.wpi.first.wpilibj.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.IGyro;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A sensor class for using an ADXRS453 gyroscope.
 * Measures angle change on the yaw axis.
 */
public class ADXRS453 implements PIDSource, IGyro {
    public static final int REG_RATE = 0x00;
    public static final int REG_TEM = 0x02;
    public static final int REG_LO_CST = 0x04;
    public static final int REG_HI_CST = 0x06;
    public static final int REG_QUAD = 0x08;
    public static final int REG_FAULT = 0x0A;
    public static final int REG_PID = 0x0C;
    public static final int REG_SN_H = 0x0E;
    public static final int REG_SN_L = 0x10;
    public static final double UPDATE_PERIOD = 1.0 / 120.0;
    protected static final Logger logger = LoggerFactory.getLogger(ADXRS453.class);
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
    private static final int kReadWrite = 0x3;
    private static final int kSPIClockRate = 3000000;
    private static final double kDegreesPerSecondPerLSB = 80.0;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    private Timer timer = new Timer();
    private SPI spi;
    private Thread startup;
    private int lastDataSent;
    private double rate;
    private double angle;
    private boolean isCalibrating;
    private double calibrationOffset;
    private EvictingQueue<Double> calibrationValues;
    private Notifier updater = new Notifier(this::update);
    private double calibrationPeriod;

    private PIDSourceType pidSource = PIDSourceType.kDisplacement;

    /**
     * Creates a new ADXRS453
     *
     * @param port The SPI port to attach to
     */
    public ADXRS453(SPI.Port port) {
        this(new SPI(port));
    }

    /**
     * Creates a new ADXRS453
     *
     * @param spi The spi to use
     */
    public ADXRS453(SPI spi) {
        this.spi = spi;
        spi.setClockRate(kSPIClockRate);
        spi.setMSBFirst();
        spi.setSampleDataOnRising();
        spi.setClockActiveHigh();
        spi.setChipSelectActiveLow();
        setCalibrationPeriod(5.0);
        fullReset();
    }

    private static int fixParity(int data) {
        data &= ~kP;
        return data | (calcParity(data) ? 0 : kP);
    }

    /**
     * Find the parity (even/odd) of an int
     *
     * @param data Data to find parity of
     * @return Whether or not the number of ones is odd
     */
    private static boolean calcParity(int data) {
        int parity = 0;
        while (data != 0) {
            parity += (data & 1);
            data >>>= 1;
        }
        return (parity % 2) == 1;
    }

    @Override
    public double getCalibrationPeriod() {
        readLock.lock();
        try {
            return calibrationPeriod;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void setCalibrationPeriod(double calibrationPeriod) {
        writeLock.lock();
        try {
            this.calibrationPeriod = calibrationPeriod;
            calibrationValues = EvictingQueue.create((int) (calibrationPeriod / UPDATE_PERIOD));
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Starts threads for processing gyro data
     */
    public void start() {
        startup = new Thread(this::startup);
        startup.start();
    }

    /**
     * Stops the data processing threads
     */
    public void stop() {
        logger.info("Stopping gyro threads");
        if (startup != null) {
            startup.interrupt();
            startup = null;
        }
        updater.stop();
        timer.stop();
    }

    @Override
    public void fullReset() {
        writeLock.lock();
        try {
            rate = 0;
            angle = 0;
            calibrationOffset = 0;
            isCalibrating = false;
            calibrationValues.clear();
            timer.reset();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void startCalibration() {
        logger.info("Starting gyro calibration");
        writeLock.lock();
        try {
            calibrationOffset = 0;
            calibrationValues.clear();
            isCalibrating = true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void finishCalibration() {
        writeLock.lock();
        try {
            calibrationOffset = calibrationValues.stream()
                    .reduce(0.0, (a, b) -> a + b) / calibrationValues.size();
            if (Double.isNaN(calibrationOffset) || Double.isInfinite(calibrationOffset)) {
                calibrationOffset = 0.0;
            }
            angle = 0;
            isCalibrating = false;
            logger.info("Finished calibrating gyro. Offset is {}", calibrationOffset);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public double getCalibrationOffset() {
        readLock.lock();
        try {
            return calibrationOffset;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isCalibrating() {
        return isCalibrating;
    }

    @Override
    public double getRate() {
        readLock.lock();
        try {
            return rate;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public double getAngle() {
        readLock.lock();
        try {
            return angle;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Sets the angle of the gyro
     *
     * @param angle The angle in degrees
     */
    public void setAngle(double angle) {
        writeLock.lock();
        try {
            this.angle = angle;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void resetAngle() {
        setAngle(0.0);
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
        return (recv >>> 5) & 0xFFFF;
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
        int tempLSB = rawTemp >>> 6;
        if ((tempLSB >>> 9) == 1) {
            tempLSB = -((~(tempLSB - 1)) & 0x03FF);
        }
        return (tempLSB / 5.0) + 45.0;
    }

    /**
     * Gets the part ID of the currently connected gyro
     *
     * @return The part ID
     */
    public int getPartID() {
        return readRegister(REG_PID);
    }

    /**
     * Gets the serial number of the currently connected gyro
     *
     * @return The 32 bit serial number
     */
    public int getSerialNumber() {
        int serial = 0;
        serial |= readRegister(REG_SN_H) << 16;
        serial |= readRegister(REG_SN_L);
        return serial;
    }

    public PIDSourceType getPIDSourceType() {
        return pidSource;
    }

    public void setPIDSourceType(PIDSourceType pidSource) {
        this.pidSource = pidSource;
    }

    public double pidGet() {
        switch (pidSource) {
            case kDisplacement:
                return getAngle();
            case kRate:
                return getRate();
            default:
                return 0;
        }
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        spi.free();
        updater.stop();
    }

    private boolean checkParity(int data) {
        boolean p1 = calcParity(data);
        boolean p0 = calcParity(data >>> 16);
        if (!p1 || !p0) {
            logger.error(String.format("Parity check failed on response: %#x", data));
            return false;
        }
        return true;
    }

    private boolean checkFaults(int data) {
        boolean hasFaults = (data & kFaultBits) != 0;
        StringBuilder logMessage;
        if (hasFaults) {
            logMessage = new StringBuilder("Faults detected:");
        } else {
            return true;
        }
        if ((data & kChk) != 0) {
            logMessage.append("\n * Self test enabled");
            logger.warn(logMessage.toString());
            return false;
        }
        if ((data & kCst) != 0)
            logMessage.append("\n * Continuous self test fault");
        if ((data & kPwr) != 0)
            logMessage.append("\n * Power fault");
        if ((data & kPor) != 0)
            logMessage.append("\n * Non-volatile programming fault");
        if ((data & kNvm) != 0)
            logMessage.append("\n * Non-volatile checksum fault");
        if ((data & kQ) != 0)
            logMessage.append("\n * Quadrature calculation fault");
        if ((data & kPll) != 0)
            logMessage.append("\n * Phase locked loop fault");
        logger.error(logMessage.toString());

        return false;
    }

    private boolean checkResponse(int data) {
        if (!checkParity(data))
            return false;
        int status = (data >>> 26) & 0b11;
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
                StringBuilder logMessage = new StringBuilder("Read/Write error: ");
                if ((data & kSpi) != 0)
                    logMessage.append("\n * SPI error");
                if ((data & kRe) != 0)
                    logMessage.append("\n * Request error");
                if ((data & kDu) != 0)
                    logMessage.append("\n * Data unavailable");
                logger.error(logMessage.toString());
                checkFaults(data);
                return false;
        }
        return false;
    }

    private boolean checkPartID() {
        int pid = getPartID();
        if ((pid & 0xff00) == 0x5200) {
            double temperature = getTemperature();
            int serial = getSerialNumber();
            logger.info(String.format(
                    "Part ID of gyro is correct (%#04x). Temperature: %f C. Serial: (%#08x)",
                    pid, temperature, serial));
            return true;
        } else {
            logger.error(String.format("Bad gyro found. Part id: %#04x", pid));
            return false;
        }
    }

    private void write(int data) {
        lastDataSent = data;
//        data = Integer.reverse(data); // fix endianness
        ByteBuffer send = ByteBuffer.allocateDirect(4);
        send.putInt(data);
        spi.write(send, send.capacity()); // send it
    }

    private int read() {
        ByteBuffer recv = ByteBuffer.allocateDirect(4);
        spi.read(false, recv, recv.capacity()); // read into the buffer
        int ret = recv.getInt();
//        ret = Integer.reverse(ret); // fix endianness
        return ret;
    }

    private int transfer(int data) {
        lastDataSent = data;
//        data = Integer.reverse(data);
        ByteBuffer send = ByteBuffer.allocateDirect(4);
        ByteBuffer recv = ByteBuffer.allocateDirect(4);
        send.putInt(data);
        spi.transaction(send, recv, send.capacity());
        int ret = recv.getInt();
//        ret = Integer.reverse(ret); // fix endianness
        return ret;
    }

    private void startup() {
        logger.info("Starting up gyro");
        int send, recv;
        send = fixParity(kSensorData | kChk);
        write(send);
        try {
            Thread.sleep(50); // in the spec
        } catch (InterruptedException e) {
            return;
        }
        send = fixParity(kSensorData);
        write(send);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            return;
        }
        recv = transfer(send);
        if ((recv & kFaultBits) != kFaultBits) { // assert that all faults are set
            logger.error(String.format("Startup self test failed: %#x", recv));
            return;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            return;
        }
        write(send);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            return;
        }
        if (!checkPartID())
            return;
        logger.debug("Starting gyro updater");
        timer.start();
        updater.startPeriodic(UPDATE_PERIOD);
    }

    private void update() {
        writeLock.lock();
        try {
            int send, recv;
            send = fixParity(kSensorData);
            if (lastDataSent != send) // optimization, because of data latching
                write(send);
            recv = transfer(send);
            if (!checkResponse(recv))
                return;
            int rawRate = (recv >>> 10) & 0xFFFF; // bits 10-25
            rate = rawRate / kDegreesPerSecondPerLSB;
            double elapsed = timer.get();
//            logger.debug(String.format("recv: %#x, rawRate: %d, rate: %f", recv, rawRate, rate));
            timer.reset();
            if (elapsed > .5) // don't intergrate if more than half a second
                elapsed = 0;
            if (isCalibrating) {
                calibrationValues.add(rate);
            } else {
                rate -= calibrationOffset; // apply calibration offset
                angle += rate * elapsed; // intergrate it into the angle
            }
        } finally {
            writeLock.unlock();
        }
    }
}

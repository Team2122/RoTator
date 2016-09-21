package org.teamtators.rotator.datalogging;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.control.AbstractSteppable;
import org.teamtators.rotator.control.ITimeProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collects quantitative data from various sources on the robot and logs it to a file
 */
@Singleton
public class DataCollector extends AbstractSteppable {
    private static final Logger logger = LoggerFactory.getLogger(DataCollector.class);
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    @Inject
    public ITimeProvider timeProvider;
    private Set<ProviderUsage> providers = ConcurrentHashMap.newKeySet();
    private String outputDir;

    @Inject
    public DataCollector() {
    }

    public String getOutputDir() {
        return outputDir;
    }

    @Inject
    public void setOutputDir(@Named("dataLogDir") String outputDir) {
        this.outputDir = outputDir;
        try {
            new File(outputDir).mkdirs();
        } catch (SecurityException e) {
            logger.error("Security policy prevented creation of data logging dir", e);
        }
    }

    /**
     * Register a new data provider to be used periodically
     *
     * @param provider New provider to add
     */
    public void startProvider(LogDataProvider provider) {
        Preconditions.checkNotNull(provider);
        if (!isEnabled()) enable();
        FileWriter writer;
        CSVPrinter printer;
        String timestamp = DATE_FORMAT.format(new Date());
        String fileName = String.format("%s/%s %s.csv", outputDir, timestamp, provider.getName());
        try {
            writer = new FileWriter(fileName);
            printer = new CSVPrinter(writer, CSVFormat.EXCEL);
        } catch (IOException e) {
            logger.error("Failed to create outputs for new data provider " + provider.getName(), e);
            return;
        }
        logger.debug("Starting data logging for {} to {}", provider.getName(), fileName);
        Iterable<Object> keys = Iterables.concat(Collections.singletonList("timestamp"), provider.getKeys());
        addRow(printer, keys);
        providers.add(new ProviderUsage(provider, writer, printer));
    }

    /**
     * Retire a data provider
     *
     * @param provider Provider to remove
     */
    public void stopProvider(LogDataProvider provider) {
        Preconditions.checkNotNull(provider);
        Optional<ProviderUsage> providerUsage = providers.stream()
                .filter(current -> current.provider.getName().equals(provider.getName()))
                .findFirst();
        if (providerUsage.isPresent()) {
            ProviderUsage usage = providerUsage.get();
            providers.remove(usage);
            logger.debug("Stopping datalogging and flushing file for provider {}", provider.getName());
            try {
                usage.csvPrinter.flush();
                usage.csvPrinter.close();
            } catch (IOException e) {
                logger.error("Error flushing csv file", e);
            }
        }
    }

    private void addRow(CSVPrinter printer, Iterable<Object> row) {
        try {
            printer.printRecord(row);
        } catch (IOException e) {
            logger.error("Exception while printing data log record", e);
        }
    }

    @Override
    public void step(double delta) {
        for (ProviderUsage providerUsage : providers) {
            Iterable<Object> values = Iterables.concat(Collections.singletonList(timeProvider.getTimestamp()),
                    providerUsage.provider.getValues());
            addRow(providerUsage.csvPrinter, values);
        }
    }

    private static class ProviderUsage {
        LogDataProvider provider;
        FileWriter writer;
        CSVPrinter csvPrinter;

        ProviderUsage(LogDataProvider provider, FileWriter writer, CSVPrinter csvPrinter) {
            this.provider = provider;
            this.writer = writer;
            this.csvPrinter = csvPrinter;
        }
    }
}

package org.teamtators.rotator.datalogging;

import java.util.List;

/**
 * Provides data for DataCollector
 */
public interface LogDataProvider {
    String getName();

    List<Object> getKeys();

    List<Object> getValues();
}

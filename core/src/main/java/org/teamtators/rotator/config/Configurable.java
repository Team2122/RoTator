package org.teamtators.rotator.config;

/**
 * An interface for objects that can be configured
 * T is the type of the config that this object accepts. The YAML/JSON config corresponding to this object
 * will be deserialized into an object of this type.
 */
public interface Configurable<T> {
    /**
     * Configures this object with the specified config
     *
     * @param config The configuration object
     */
    void configure(T config);
}

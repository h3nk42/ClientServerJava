package com.company2;

import java.time.LocalDateTime;

/**
 * <h1>DataSnippet</h1>
 * The DataSnippet class holds a location name, temperature value, humidity value and a timestamp
 *
 * @author Carlos Koenig, Jonas Richter
 * @version 1.0
 * @since 2020-01-15
 */

public class DataSnippet {

    private String location;
    private float temperature;
    private float humidity;
    private LocalDateTime timestamp;

    public DataSnippet(String location, float temperature, float humidity, LocalDateTime timestamp)
    {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    /**
     * This method is used to get the location ot the DataSnippet.
     * @return String that contains the location name.
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method is used to get the temperature ot the DataSnippet.
     * @return float that contains the temperature.
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * This method is used to get the humidity ot the DataSnippet.
     * @return float that contains the humidity.
     */
    public float getHumidity() {
        return humidity;
    }

    /**
     * This method is used to get the timestamp ot the DataSnippet.
     * @return LocalDateTime that contains the timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

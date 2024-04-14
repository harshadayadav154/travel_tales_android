package com.example.travel_tales.models;

import java.io.Serializable;

/**
 * Class representing a Location.
 *
 * @author Nabin Ghatani 2024-04-13
 */
public class Location implements Serializable {
    private String name;
    private double latitude;
    private double longitude;

    /**
     * Default constructor.
     */
    public Location() {
    }

    /**
     * Parameterized constructor to initialize a Location object.
     *
     * @param name      The name of the location.
     * @param latitude  The latitude coordinate of the location.
     * @param longitude The longitude coordinate of the location.
     */
    public Location(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getter method for the name of the location.
     *
     * @return The name of the location.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method to set the name of the location.
     *
     * @param name The name of the location to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for the latitude coordinate of the location.
     *
     * @return The latitude coordinate of the location.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter method to set the latitude coordinate of the location.
     *
     * @param latitude The latitude coordinate to set.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter method for the longitude coordinate of the location.
     *
     * @return The longitude coordinate of the location.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter method to set the longitude coordinate of the location.
     *
     * @param longitude The longitude coordinate to set.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}


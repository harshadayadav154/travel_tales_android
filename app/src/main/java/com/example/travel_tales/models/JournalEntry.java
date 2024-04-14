package com.example.travel_tales.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Class representing a journal entry with a title, description, date, and associated location ID.
 *
 * @author Nabin Ghatani 2024-04-13
 */
public class JournalEntry extends Audit implements Serializable {
    private int id;
    private int userId;
    private String title;
    private String description;
    private Date date;
    private Location location;
    private List<String> imagePaths;

    /**
     * Default constructor.
     */
    public JournalEntry() {
    }

    /**
     * Parameterized constructor to initialize a JournalEntry object.
     *
     * @param userId      The ID of the user who created the journal entry.
     * @param title       The title of the journal entry.
     * @param description The description of the journal entry.
     * @param date        The date of the journal entry.
     * @param location    Rhe location associated with the journal entry.
     * @param imagePaths  The list of file paths of images associated with the journal entry.
     */
    public JournalEntry(int userId, String title, String description, Date date, Location location, List<String> imagePaths) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imagePaths = imagePaths;
    }

    // Getters and Setters

    /**
     * Getter for the ID of the journal entry.
     *
     * @return The ID of the journal entry.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the ID of the journal entry.
     *
     * @param id The ID to set for the journal entry.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the title of the journal entry.
     *
     * @return The title of the journal entry.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of the journal entry.
     *
     * @param title The title to set for the journal entry.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the description of the journal entry.
     *
     * @return The description of the journal entry.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description of the journal entry.
     *
     * @param description The description to set for the journal entry.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the date of the journal entry.
     *
     * @return The date of the journal entry.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter for the date of the journal entry.
     *
     * @param date The date to set for the journal entry.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Getter method for the ID of the user who created the journal entry.
     *
     * @return The ID of the user.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Setter method to set the ID of the user who created the journal entry.
     *
     * @param userId The ID of the user to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }


    /**
     * Getter method for the location associated with the journal entry.
     *
     * @return The location object.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Setter method to set the location associated with the journal entry.
     *
     * @param location The location to set.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Getter method for the list of file paths of images associated with the journal entry.
     *
     * @return The list of image file paths.
     */
    public List<String> getImagePaths() {
        return imagePaths;
    }

    /**
     * Setter method to set the list of file paths of images associated with the journal entry.
     *
     * @param imagePaths The list of image file paths to set.
     */
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}

